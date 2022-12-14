package com.decagon.bank.utils;


import com.decagon.bank.dtos.request.LiquidateDto;
import com.decagon.bank.dtos.request.SignUpDto;
import com.decagon.bank.dtos.request.TopupDto;
import com.decagon.bank.dtos.request.TransferDto;
import com.decagon.bank.dtos.request.WithrawalDto;
import com.decagon.bank.entities.models.AccountModel;
import com.decagon.bank.entities.models.UserModel;
import com.decagon.bank.enums.AccountStatus;
import com.decagon.bank.enums.AccountType;
import com.decagon.bank.enums.Currency;

import java.security.SecureRandom;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseUtil {

    public static AccountModel generateAccountNumber(UserModel userModel, String accounType) {
        log.info("::: In generateAccountNumber.....");
        String[] num = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int n = random.nextInt(num.length);
            sb.append(num[n]);
        }

        String iban = sb.toString();
        log.info("::: Iban: [{}]", iban);
        AccountType accountType = AccountType.getAccountType(accounType);
        log.info("::: AccountType: [{}]", accountType);

        AccountModel accountModel = new AccountModel(iban, Currency.NGN.name(),
                AccountStatus.ACTIVE.name(), accounType);
        accountModel.setAccountType(accountType.name());
        accountModel.setBvn(userModel.getBvn());

        log.info("::: Account with Iban: [{}] created for user with email: [{}]",
                accountModel.getIban(),
                userModel.getEmail());

        return accountModel;

    }

    public static boolean isRequestSatisfied(SignUpDto signUpDto) {
        log.info("::: In SignUp payload validation.....");
        try {

            Objects.requireNonNull(signUpDto.getFirstName());
            Objects.requireNonNull(signUpDto.getLastName());
            Objects.requireNonNull(signUpDto.getPhone());
            Objects.requireNonNull(signUpDto.getAccountType());
            Objects.requireNonNull(signUpDto.getVerifiedPhone());
            Objects.requireNonNull(signUpDto.getVerifiedBvn());

            boolean isPhoneNumValid = TransactionObjFormatter.isMatchNigerianPhoneNum(signUpDto.getPhone());
            if (!isPhoneNumValid) {
                log.error("::: Email| PhoneNum not valid");
                return false;
            }
            if (!signUpDto.getVerifiedPhone()) {
                log.error("::: Phone not verified");
                return false;
            }

            log.info("::: Satisfied requestBody: [{}] :::", signUpDto);
            return true;

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            log.debug("::: Unsatisfied requestBody [{}]:::", signUpDto);
            return false;
        }

    }

    public static boolean isRequestSatisfied(TransferDto transferDto) {
        log.info("::: In Transfer payload validation.....");
        try {

            Objects.requireNonNull(transferDto.getAmount());
            Objects.requireNonNull(transferDto.getBenefAccountNo());
            Objects.requireNonNull(transferDto.getDebitAccountNo());
            Objects.requireNonNull(transferDto.getPaymentReference());
            Objects.requireNonNull(transferDto.getTranxRef());
            Objects.requireNonNull(transferDto.getTranCrncy());
            Objects.requireNonNull(transferDto.getTranType());
            Objects.requireNonNull(transferDto.getChannelCode());
            Objects.requireNonNull(transferDto.getUserId());

            log.info("::: Satisfied requestBody: [{}] :::", transferDto);
            return true;

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            log.debug("::: Unsatisfied requestBody [{}]:::", transferDto);
            return false;
        }

    }

    public static boolean isRequestSatisfied(TopupDto topupDto) {
        log.info("::: In TopUp payload validation.....");
        try {

            Objects.requireNonNull(topupDto.getAmount());
            Objects.requireNonNull(topupDto.getIban());
            Objects.requireNonNull(topupDto.getChannelCode());
            Objects.requireNonNull(topupDto.getTranxRef());

            log.info("::: Satisfied requestBody: [{}] :::", topupDto);
            return true;

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            log.debug("::: Unsatisfied requestBody [{}]:::", topupDto);
            return false;
        }

    }

    public static boolean isRequestSatisfied(WithrawalDto withrawalDto) {
        log.info("::: In Withdrawal payload validation.....");
        try {

            Objects.requireNonNull(withrawalDto.getAmount());
            Objects.requireNonNull(withrawalDto.getIban());
            Objects.requireNonNull(withrawalDto.getChannelCode());
            Objects.requireNonNull(withrawalDto.getTranxRef());
            Objects.requireNonNull(withrawalDto.getTranxType());
            Objects.requireNonNull(withrawalDto.getCurrency());
            Currency currency = Currency.getInvestmentPlan(withrawalDto.getCurrency());
            log.info("Currency: " + currency);
            log.info("::: Satisfied requestBody: [{}] :::", withrawalDto);
            return true;

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            log.debug("::: Unsatisfied requestBody [{}]:::", withrawalDto);
            return false;
        }

    }

    public static boolean isRequestSatisfied(LiquidateDto liquidateDto) {
        log.info("::: In Liquidity payload validation.....");
        try {

            Objects.requireNonNull(liquidateDto.getIban());
            Objects.requireNonNull(liquidateDto.getChannelCode());
            Objects.requireNonNull(liquidateDto.getTranxRef());
            Objects.requireNonNull(liquidateDto.getTranxType());
            Objects.requireNonNull(liquidateDto.getChannelCode());
            Objects.requireNonNull(liquidateDto.getTranxCrncy());
            Currency currency = Currency.getInvestmentPlan(liquidateDto.getTranxCrncy());
            log.info("Currency: " + currency);
            if (!liquidateDto.getIsLiquidate() || !liquidateDto.getIsLiquidityApproval()) {
                log.error("::: Liquidity key error.");
                return false;
            }

            log.info("::: Satisfied requestBody: [{}] :::", liquidateDto);
            return true;

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            log.debug("::: Unsatisfied requestBody [{}]:::", liquidateDto);
            return false;
        }

    }

    public static Boolean verifyAccount(AccountModel debitAccount, AccountModel creditAccount) {

        try {
            if (debitAccount.getStatus().equalsIgnoreCase(AccountStatus.ACTIVE.name()) &&
                    creditAccount.getStatus().equalsIgnoreCase(AccountStatus.ACTIVE.name())) {

                log.info("::: Account with iban: [{}] is active to receive fund",
                        creditAccount.getIban());
                return true;
            }

            log.error("::: Account with iban: [{}] not verified :::", creditAccount.getIban());
            return false;

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            log.error("::: Account with iban: [{}] not verified :::", creditAccount.getIban());

            return false;
        }

    }

    public static Boolean verifyAccount(AccountModel accountToVerify) {

        try {
            if (accountToVerify.getStatus().equalsIgnoreCase(AccountStatus.ACTIVE.name())) {

                log.info("::: Account is active with iban: [{}] ",
                        accountToVerify.getIban());
                return true;
            }

            log.error("::: Account not verified with iban: [{}]", accountToVerify.getIban());
            return false;

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            log.error("::: Account not verified with iban: [{}]", accountToVerify.getIban());

            return false;
        }

    }

    // A dummyBVN is generated to simulate real life transaction as no transaction can occur without BVN,
    // This is Strong proposition that cannot be ignore even this exercise do  not covers it.
    public static String generateDummyBVN() {
        log.info("::: In generateAccountNumber.....");
        String[] num = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        sb.append("223");
        for (int i = 0; i < 8; i++) {
            int n = random.nextInt(num.length);

            sb.append(num[n]);
        }

        String dummyBvn = sb.toString();
        log.info("::: DummyBVN: [{}]", dummyBvn);

        return dummyBvn;
    }

}
