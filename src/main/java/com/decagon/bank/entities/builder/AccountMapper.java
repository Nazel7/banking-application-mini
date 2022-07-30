package com.decagon.bank.entities.builder;


import com.decagon.bank.dtos.response.Account;
import com.decagon.bank.dtos.response.Transaction;
import com.decagon.bank.entities.models.AccountModel;
import com.decagon.bank.entities.models.TransactionModel;
import com.decagon.bank.enums.TransType;
import com.decagon.bank.enums.TranxStatus;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

    public static Account mapToDomain(AccountModel model, BigDecimal amount,
                                      String tranxType, String tranxStatus) {

        if (tranxType.equalsIgnoreCase(TransType.WITHDRAWAL.name()) ||
                tranxType.equalsIgnoreCase(TransType.LIQUIDATE.name())) {

            return Account
                    .builder()
                    .withdrawnAmount(amount.doubleValue())
                    .balance(model.getBalance().doubleValue())
                    .status(tranxStatus == null ? TranxStatus.PENDING.name() : tranxStatus)
                    .build();
        }
        if (tranxType.equalsIgnoreCase(TransType.DEPOSIT.name())) {

            return Account
                    .builder()
                    .depositedAmount(amount.doubleValue())
                    .balance(model.getBalance().doubleValue())
                    .status(tranxStatus == null ? TranxStatus.PENDING.name() : tranxStatus)
                    .build();
        }

        return null;

    }

    public static Account mapToTranxHistoryPageToDomain(Page<TransactionModel> transactionModelPage) {

        final List<Transaction> transactionList =
                transactionModelPage.getContent().stream().map(transactionModel -> Transaction
                        .builder()
                        .status(transactionModel.getStatus())
                        .amount(transactionModel.getAmount())
                        .benefAccountNo(transactionModel.getBenefAccountNo())
                        .debitAccountNo(transactionModel.getDebitAccountNo())
                        .paymentReference(transactionModel.getPaymentReference())
                        .currency(transactionModel.getCurrency())
                        .tranNarration(transactionModel.getTranNarration())
                        .tranType(transactionModel.getTranType())
                        .userId(transactionModel.getUserId())
                        .tranxRef(transactionModel.getTranxRef())
                        .channelCode(transactionModel.getChannelCode())
                        .isLiquidate(transactionModel.getIsLiquidate())
                        .liquidityApproval(transactionModel.getLiquidityApproval())
                        .createdAt(transactionModel.getCreatedAt())
                        .build()).collect(
                        Collectors.toList());

        return Account
                .builder()
                .status(TranxStatus.SUCCESSFUL.name())
                .totalElement(transactionModelPage.getTotalElements())
                .historySize(transactionModelPage.getContent().size())
                .createdAt(new Date())
                .mTransactions(transactionList)
                .build();

    }


}
