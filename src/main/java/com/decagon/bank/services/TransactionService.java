package com.decagon.bank.services;

import com.decagon.bank.configs.TranxMessageConfig;
import com.decagon.bank.contants.ChannelConsts;
import com.decagon.bank.dtos.request.LiquidateDto;
import com.decagon.bank.dtos.request.TopupDto;
import com.decagon.bank.dtos.request.TransferDto;
import com.decagon.bank.dtos.request.WithrawalDto;
import com.decagon.bank.dtos.response.Account;
import com.decagon.bank.dtos.response.Transaction;
import com.decagon.bank.dtos.response.TransferNotValidException;
import com.decagon.bank.dtos.response.UserNotFoundException;
import com.decagon.bank.entities.builder.AccountMapper;
import com.decagon.bank.entities.builder.TransactionMapper;
import com.decagon.bank.entities.models.AccountModel;
import com.decagon.bank.entities.models.TransactionModel;
import com.decagon.bank.entities.models.UserModel;
import com.decagon.bank.enums.Currency;
import com.decagon.bank.enums.TransType;
import com.decagon.bank.enums.TranxStatus;
import com.decagon.bank.event.notifcation.DataInfo;
import com.decagon.bank.event.notifcation.NotificationLog;
import com.decagon.bank.event.notifcation.NotificationLogEvent;
import com.decagon.bank.event.notifcation.Receipient;
import com.decagon.bank.repositories.AccountRepo;
import com.decagon.bank.repositories.TransactionRepo;
import com.decagon.bank.repositories.UserRepo;
import com.decagon.bank.utils.BaseUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionService {

    private final TransactionRepo mTransactionRepo;
    private final AccountRepo mAccountRepo;
    private final UserRepo mUserRepo;
    private final ApplicationEventPublisher mEventPublisher;
    private final TranxMessageConfig mMessageConfig;

    @Transactional
    public Transaction transferFund(TransferDto transferDto, HttpServletRequest request)
            throws TransferNotValidException {
        log.info("::: In tranferFund.....");

        try {

            boolean isRequestValid = BaseUtil.isRequestSatisfied(transferDto);

            if (!isRequestValid) {
                log.error("::: Invalid Transfer request, please try again later.");
                throw new TransferNotValidException("Invalid Transfer request, please try again later.");
            }

            Optional<UserModel> userModel = mUserRepo.findById(transferDto.getUserId());
            final AccountModel debitAccount =
                    mAccountRepo.findAccountModelByIban(transferDto.getDebitAccountNo());

            if (userModel.isEmpty()) {

                log.error("::: User not found with body");
                throw new UserNotFoundException("User not found ");
            }

            if (!userModel.get().equals(debitAccount.getUserModel())) {

                log.error("::: user account not valid, Account: [{}] :::", debitAccount);
                throw new AccountNotFoundException("User account not valid");
            }

            final AccountModel creditAccount =
                    mAccountRepo.findAccountModelByIban(transferDto.getBenefAccountNo());

            final UserModel sender = debitAccount.getUserModel();
            System.out.println("Sender " + sender);
            final UserModel receiver = creditAccount.getUserModel();
            System.out.println("Receiver " + receiver);

            // Very if account is active not close or debit freeze
            boolean isAccountStatusVerified =
                    BaseUtil.verifyAccount(debitAccount, creditAccount);
            log.info("::: Account Status verified: [{}] :::", isAccountStatusVerified);

            final TransactionModel transactionModel = TransactionMapper
                    .mapToModel(transferDto, "user-transfer-token");

            // Due to limitation of this exercise, a user should be verified before fund transfer using
            // VerificationToken or tranxToken

//            if (!sender.getVerificationCode().equals(transferDto.getVerificationCode())) {
//                log.error("Transfer verificationCode failed.....");
//                throw new IllegalAccessException("Access denied for Invalid verificationCode");
//            }
            final TransactionModel existLogModelWithRef =
                    mTransactionRepo.findTransactionModelByTranxRef(transactionModel.getTranxRef());
            if (existLogModelWithRef != null) {
                log.error("::: Duplicate error. LogModel already exist.");
                throw new IllegalArgumentException("Duplicate error. LogModel already exist.");
            }

            final AccountModel debitedAccount =
                    debitAccount.withdraw(transferDto.getAmount());

            if (debitedAccount == null) {
                log.error("::: Transfer failed insufficient balance.");
                throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
            }

            final AccountModel creditedAccount = creditAccount.deposit(transferDto.getAmount());
            if (creditedAccount == null) {
                log.error("::: Transfer-account deposit failed.");
                throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
            }


            mAccountRepo.save(debitedAccount);

            creditedAccount.setIsLiquidated(false);
            creditAccount.setIsLiquidityApproval(false);
            mAccountRepo.save(creditedAccount);

            log.info("::: Account has been debited with iban: [{}]", debitedAccount.getIban());
            log.info("::: Account has been credited with iban: [{}] ", creditedAccount.getIban());
            log.info(mMessageConfig.getTransfer_successful());
            transactionModel.setStatus(TranxStatus.SUCCESSFUL.name());
            TransactionModel savedTransaction = mTransactionRepo.save(transactionModel);
            log.info("::: FundTransfer LogModel audited successfully");

            DataInfo data = new DataInfo();
            Receipient receipient = new Receipient();
            List<Receipient> receipients = new ArrayList<>();
            NotificationLog notificationLog = new NotificationLog();

            receipient.setEmail(creditAccount.getUserModel().getEmail());
            receipient.setTelephone(creditAccount.getUserModel().getPhone());
            receipients.add(receipient);
            String notificationMessage =
                    String.format("Your account %s has been credit with sum of [%s%s] only ",
                            creditAccount.getIban(),
                            creditAccount.getCurrency(),
                            transferDto.getAmount());
            data.setMessage(notificationMessage);
            data.setRecipients(receipients);
            notificationLog.setData(data);
            notificationLog.setTranxRef(transferDto.getTranxRef());
            notificationLog.setChannelCode(transferDto.getChannelCode());
            notificationLog.setTranxDate(savedTransaction.getCreatedAt());

            notificationLog.setEventType(TransType.TRANSFER.name());
            String name = userModel.get().getFirstName().concat(" ").concat(userModel.get().getLastName());
            notificationLog.setInitiator(name);

            final NotificationLogEvent
                    notificationLogEvent = new NotificationLogEvent(this, notificationLog);
            mEventPublisher.publishEvent(notificationLogEvent);
            log.info("::: notification sent to recipient: [{}] DB locator :::",
                    notificationLogEvent.getNotificationLog());

            return TransactionMapper.mapToDomain(savedTransaction);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
        }

    }


    @Transactional
    public Account deposit(TopupDto topupDto, HttpServletRequest request) throws TransferNotValidException {
        log.info("::: In deposit.....");

        try {

            // this is met to sent from request for the purpose of this test I am setting a default value
            topupDto.setTranxRef(UUID.randomUUID().toString().substring(0, 12));
            topupDto.setChannelCode(ChannelConsts.VENDOR_CHANNEL);
            topupDto.setCurrency(Currency.NGN.name());
            topupDto.setTranxType(TransType.DEPOSIT.name());
            topupDto.setTranxNaration("Deposit Operation");
            topupDto.setChannelCode(ChannelConsts.VENDOR_CHANNEL);

            boolean isRequestValid = BaseUtil.isRequestSatisfied(topupDto);
            final TransactionModel transactionModel = TransactionMapper.mapToModel(topupDto,
                                                                                   "userToken-deposit-token");

            if (!isRequestValid) {
                log.error("::: TopUp request error with payload: [{}]", topupDto);
                throw new IllegalArgumentException("TopUp request error with payload");
            }

            final TransactionModel existLogModelWithRef =
                    mTransactionRepo.findTransactionModelByTranxRef(transactionModel.getTranxRef());
            if (existLogModelWithRef != null) {
                log.error("::: Duplicate error. LogModel already exist.");
                throw new IllegalArgumentException("Duplicate error. LogModel already exist.");
            }

            final AccountModel creditAccount =
                    mAccountRepo.findAccountModelByIban(topupDto.getIban());

            log.info("::: About to validate Account owner.....");
            /*
             In real application, it is proper to check a valid access to the account,
            if the current login user is the owner of the account
            **/

            AccountModel topedAccount = creditAccount.deposit(topupDto.getAmount());
            topedAccount.setIsLiquidityApproval(false);
            topedAccount.setIsLiquidated(false);
            mAccountRepo.save(topedAccount);

            transactionModel.setStatus(TranxStatus.SUCCESSFUL.name());
            TransactionModel savedLogModel = mTransactionRepo.save(transactionModel);
            log.info("::: Deposit LogModel audited successfully with paylaod: [{}]", savedLogModel);

            NotificationLog notificationModel = new NotificationLog();
            DataInfo data = new DataInfo();
            String notificationMessage =
                    String.format("Your account %s has been credit with sum of [%s%s] only ",
                            creditAccount.getIban(),
                            creditAccount.getCurrency(),
                            topupDto.getAmount());
            data.setMessage(notificationMessage);
            notificationModel.setData(data);
            notificationModel.setInitiator(topedAccount.getUserModel().getFirstName().concat(" ")
                    .concat(topedAccount.getUserModel().getLastName()));
            notificationModel.setEventType(TransType.DEPOSIT.name());
            notificationModel.setChannelCode(ChannelConsts.VENDOR_CHANNEL);
            notificationModel.setTranxDate(new Date());
            notificationModel.setTranxRef(topupDto.getTranxRef());

            final NotificationLogEvent eventLog = new NotificationLogEvent(this, notificationModel);
            mEventPublisher.publishEvent(eventLog);
            log.info("::: notification sent successfully, data: [{}]",
                    eventLog.getNotificationLog());

            return AccountMapper.mapToDomain(topedAccount, topupDto.getAmount(), TransType.DEPOSIT.name(),
                                             TranxStatus.SUCCESSFUL.name());

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
        }


    }


    @Transactional
    public Account withdrawal(WithrawalDto withrawalDto, HttpServletRequest request) throws TransferNotValidException {
        log.info("::: In doFundWithdrawal.....");

        try {

            // In proper Withdrawal VerificationCode or withrawalCode should have been check here,
            // this is limited because of the scope of this test.
            // And Below should have been sent from request not hardcoded.
            withrawalDto.setChannelCode(ChannelConsts.VENDOR_CHANNEL);
            withrawalDto.setTranxRef(UUID.randomUUID().toString().substring(0, 12));
            withrawalDto.setTranxType(TransType.WITHDRAWAL.name());
            withrawalDto.setCurrency(Currency.NGN.name());

            boolean isRequestValid = BaseUtil.isRequestSatisfied(withrawalDto);
            if (!isRequestValid) {
                log.error("::: FundWithdrawal request error with payload: [{}]", withrawalDto);
                throw new IllegalArgumentException("TopUp request error with payload");
            }

            final TransactionModel existLogModelWithRef =
                    mTransactionRepo.findTransactionModelByTranxRef(withrawalDto.getTranxRef());
            if (existLogModelWithRef != null) {
                log.error("::: Duplicate error. LogModel already exist.");
                throw new IllegalArgumentException("Duplicate error. LogModel already exist.");
            }

            final AccountModel accountModel = mAccountRepo.findAccountModelByIban(withrawalDto.getIban());

            // Very if account is active not close or debit freeze
            // I real world scenerio accountVerificationCode and tier-level specification should have been check here.
            boolean isAccountStatusVerified =
                    BaseUtil.verifyAccount(accountModel);
            log.info("::: Account Status verified: [{}] :::", isAccountStatusVerified);

            if (!isAccountStatusVerified) {
                log.error("::: Account not Active");
                throw new TransferNotValidException("Account not Active");
            }

            final TransactionModel transactionModel = TransactionMapper.mapToModel(withrawalDto,
                                                                                   "user-withdrawal-token");
            final AccountModel debitedAccount = accountModel.withdraw(withrawalDto.getAmount());

            if (debitedAccount == null) {
                log.error("::: Transfer failed insufficient balance.");
                throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
            }

            AccountModel debitedAccountSaved = mAccountRepo.save(debitedAccount);
            log.info("::: Account debit updated successfully with payload; [{}]", debitedAccountSaved);

            transactionModel.setStatus(TranxStatus.SUCCESSFUL.name());
            TransactionModel savedLogModel = mTransactionRepo.save(transactionModel);
            log.info("::: FundWithdrawal LogModel audited successfully with paylaod: [{}]", savedLogModel);

            String notificationMessage =
                    String.format("Your account %s has been debited with sum of [%s%s] only ",
                            debitedAccount.getIban(),
                            debitedAccount.getCurrency(),
                            withrawalDto.getAmount());

            NotificationLog notificationLog = new NotificationLog();
            DataInfo data = new DataInfo();

            data.setMessage(notificationMessage);
            notificationLog.setData(data);
            notificationLog.setInitiator(debitedAccountSaved.getUserModel().getFirstName().concat(" ")
                    .concat(debitedAccountSaved.getUserModel().getLastName()));
            notificationLog.setEventType(TransType.WITHDRAWAL.name());
            notificationLog.setChannelCode(ChannelConsts.VENDOR_CHANNEL);
            notificationLog.setTranxDate(new Date());
            notificationLog.setTranxRef(withrawalDto.getTranxRef());

            final NotificationLogEvent
                    notificationLogEvent = new NotificationLogEvent(this, notificationLog);
            mEventPublisher.publishEvent(notificationLogEvent);
            log.info("::: notification sent successfully, data: [{}]",
                    notificationLogEvent.getNotificationLog());

            return AccountMapper.mapToDomain(debitedAccount, withrawalDto.getAmount(), TransType.WITHDRAWAL.name(),
                                             TranxStatus.SUCCESSFUL.name());

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
        }

    }

    // A type of withdrawal to withraw all available fund

    @Transactional
    public Account liquidateAccount(LiquidateDto liquidateDto, HttpServletRequest request) throws TransferNotValidException {

        try {

            NotificationLog notificationLog = new NotificationLog();
            DataInfo data = new DataInfo();
            boolean isRequestValid = BaseUtil.isRequestSatisfied(liquidateDto);
            if (!isRequestValid) {
                log.error("::: AccountLiquidation request error with payload: [{}]", liquidateDto);
                throw new IllegalArgumentException("TopUp request error with payload");
            }

            final TransactionModel existLogModelWithRef =
                    mTransactionRepo.findTransactionModelByTranxRef(liquidateDto.getTranxRef());
            if (existLogModelWithRef != null) {
                log.error("::: Duplicate error. LogModel already exist.");
                throw new IllegalArgumentException("Duplicate error. LogModel already exist.");
            }

            final AccountModel accountModel =
                    mAccountRepo.findAccountModelByIban(liquidateDto.getIban());

            // Very if account is active not close or debit freeze
            boolean isAccountStatusVerified = BaseUtil.verifyAccount(accountModel);
            log.info("::: Account Status verified: [{}] :::", isAccountStatusVerified);

            if (!isAccountStatusVerified) {
                log.info("::: Transaction failed");
                throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
            }

            final TransactionModel transactionModel =
                    TransactionMapper.mapToModel(liquidateDto, "user-liquidation-token");
            final AccountModel debitedAccount = accountModel.liquidate(accountModel.getBalance());

            if (debitedAccount == null) {
                log.error("::: AccountLiquidation withdrawal failed, insufficient balance.");
                throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
            }

            debitedAccount.setIsLiquidated(true);
            debitedAccount.setIsLiquidityApproval(liquidateDto.getIsLiquidityApproval());
            AccountModel debitedAccountSaved = mAccountRepo.save(debitedAccount);
            log.info("::: Account debit updated successfully with payload`; [{}]",
                     debitedAccountSaved);

            // Check is Liquidation approved and liquidated successful
            if (!liquidateDto.getIsLiquidate() || !liquidateDto.getIsLiquidityApproval()) {
                log.error("::: Liquidity Request Error, Liquidation must be approved.");
                throw new IllegalArgumentException(
                        "Liquidity Request Error, Liquidation must be approved." +
                                " Date: " + new Date());
            }

            transactionModel.setStatus(TranxStatus.SUCCESSFUL.name());
            TransactionModel savedLogModel = mTransactionRepo.save(transactionModel);
            log.info("::: FundWithdrawal LogModel audited successfully with paylaod: [{}]",
                     savedLogModel);

            String notificationMessage =
                    String.format(
                            "Your account %s has been liquidated with account total sum of [%s%s] only ",
                            debitedAccount.getIban(),
                            debitedAccount.getCurrency(),
                            accountModel.getBalance());
            data.setMessage(notificationMessage);
            notificationLog.setData(data);
            notificationLog.setInitiator(debitedAccountSaved.getUserModel().getFirstName().concat(" ")
                                                     .concat(debitedAccountSaved.getUserModel()
                                                                                .getLastName()));
            notificationLog.setEventType(TransType.LIQUIDATE.name());
            notificationLog.setChannelCode(ChannelConsts.VENDOR_CHANNEL);
            notificationLog.setTranxDate(new Date());
            notificationLog.setTranxRef(liquidateDto.getTranxRef());

            final NotificationLogEvent
                    notificationLogEvent = new NotificationLogEvent(this, notificationLog);
            mEventPublisher.publishEvent(notificationLogEvent);
            log.info("::: notification sent successfully, data: [{}]",
                     notificationLogEvent.getNotificationLog());

            return AccountMapper.mapToDomain(debitedAccount, accountModel.getBalance(),
                                             TransType.LIQUIDATE.name(),
                                             TranxStatus.SUCCESSFUL.name());


        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
        }


    }

    private String getLoginToken(HttpServletRequest request) {
        // This could be highly useful if authentication is put in place,
        // but for the purpose of this test I just put how it might be useful
        String token = request.getHeader("Authorization");
        if (token.contains("Bearer")) {
            token = token.split(" ")[1];
        }
        return token;
    }

    public Account getTransactionHistory(final String accountNo, Integer pageNo, Integer size, HttpServletRequest request)
            throws TransferNotValidException {

        log.info("::: In transactionHistory");
        try {

            log.info("::: About to process transactionHistory operations......");
            Pageable pageable = PageRequest.of(pageNo -1, size);
            Page<TransactionModel> transactionModelPage;
            if (accountNo == null) {
                log.info("::: AccountNum is null processing default transactionHistory.....");
                transactionModelPage =
                        mTransactionRepo.fetchAllTransactionOrderByCreatedAtDesc(pageable);
                log.info("::: Default TransactionHistory fetched successfully, payloadSize: [{}]",
                         transactionModelPage.getContent().size());
            } else {
                log.info("::: Processing transactionHistory with AccountNum.....");
                transactionModelPage =
                        mTransactionRepo.findAllByBenefAccountNoOrderByCreatedAtDesc(accountNo, pageable);
                log.info("::: TransactionHistory fetched successfully, payloadSize: [{}]",
                         transactionModelPage.getContent().size());

            }

            return AccountMapper.mapToTranxHistoryPageToDomain(transactionModelPage);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TransferNotValidException(mMessageConfig.getTranfer_fail());
        }

    }

}
