package com.decagon.bank.dtos.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LiquidateDto {

    private String iban;
    private String tranxRef;
    private String channelCode;
    private String tranxType;
    private String tranxCrncy;
    private String tranxNaration;
    private Boolean isLiquidate;
    private Boolean isLiquidityApproval;
    private String verificationCode;

}
