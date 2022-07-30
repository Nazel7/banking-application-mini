package com.decagon.bank.entities.builder;

import com.decagon.bank.dtos.request.LiquidateDto;
import com.decagon.bank.dtos.request.TopupDto;
import com.decagon.bank.dtos.request.TransferDto;
import com.decagon.bank.dtos.request.WithrawalDto;
import com.decagon.bank.dtos.response.Transaction;
import com.decagon.bank.entities.models.TransactionModel;
import com.decagon.bank.enums.TransType;

public class TransactionMapper {

    public static TransactionModel mapToModel(TransferDto transferDto, String token){

        return TransactionModel
                .builder()
                .amount(transferDto.getAmount())
                .tranxRef(transferDto.getTranxRef())
                .paymentReference(transferDto.getPaymentReference())
                .benefAccountNo(transferDto.getBenefAccountNo())
                .debitAccountNo(transferDto.getDebitAccountNo())
                .currency(transferDto.getTranCrncy())
                .tranType(transferDto.getTranType())
                .userToken(token)
                .tranNarration(transferDto.getTranNarration())
                .userId(transferDto.getUserId())
                .channelCode(transferDto.getChannelCode())
                .build();
    }

    public static TransactionModel mapToModel(WithrawalDto withrawalDto, String token){

        return TransactionModel
                .builder()
                .amount(withrawalDto.getAmount())
                .tranxRef(withrawalDto.getTranxRef())
                .benefAccountNo(withrawalDto.getIban())
                .debitAccountNo(withrawalDto.getIban())
                .currency(withrawalDto.getCurrency())
                .tranType(withrawalDto.getTranxType().equals(TransType.WITHDRAWAL.name()) ?
                        withrawalDto.getTranxType(): TransType.WITHDRAWAL.name())
                .userToken(token)
                .tranNarration(withrawalDto.getTranxNaration())
                .channelCode(withrawalDto.getChannelCode())
                .build();
    }
    public static TransactionModel mapToModel(LiquidateDto liquidateDto, String token){

        return TransactionModel
                .builder()
                .tranxRef(liquidateDto.getTranxRef())
                .benefAccountNo(liquidateDto.getIban())
                .debitAccountNo(liquidateDto.getIban())
                .currency(liquidateDto.getTranxCrncy())
                .tranType(!liquidateDto.getTranxType().equals(TransType.LIQUIDATE.name()) ?
                        TransType.LIQUIDATE.name(): liquidateDto.getTranxType())
                .userToken(token)
                .tranNarration(liquidateDto.getTranxNaration())
                .liquidityApproval(liquidateDto.getIsLiquidityApproval())
                .isLiquidate(liquidateDto.getIsLiquidate())
                .channelCode(liquidateDto.getChannelCode())
                .build();
    }

    public static TransactionModel mapToModel(TopupDto topupDto, String token){

        return TransactionModel
                .builder()
                .tranxRef(topupDto.getTranxRef())
                .benefAccountNo(topupDto.getIban())
                .currency(topupDto.getCurrency())
                .tranType(topupDto.getTranxType())
                .userToken(token)
                .tranNarration(topupDto.getTranxNaration())
                .channelCode(topupDto.getChannelCode())
                .amount(topupDto.getAmount())
                .build();
    }

    public static Transaction mapToDomain(TransactionModel transactionModel){

        return Transaction
                .builder()
                .status(transactionModel.getStatus())
                .amount(transactionModel.getAmount())
                .paymentReference(transactionModel.getPaymentReference())
                .benefAccountNo(transactionModel.getBenefAccountNo())
                .debitAccountNo(transactionModel.getDebitAccountNo())
                .tranxRef(transactionModel.getTranxRef())
                .currency(transactionModel.getCurrency())
                .tranType(transactionModel.getTranType())
                .tranNarration(transactionModel.getTranNarration())
                .userId(transactionModel.getUserId())
                .build();
    }

}
