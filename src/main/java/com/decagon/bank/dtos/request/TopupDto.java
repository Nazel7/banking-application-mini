package com.decagon.bank.dtos.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TopupDto {
    private BigDecimal amount;
    private String iban;
    private String tranxRef;
    private String channelCode;
    private String tranxType;
    public OriginatorKyc originatorKyc;
    private String currency;
    private String tranxNaration;

}
