package com.decagon.bank.dtos.request;

import lombok.Data;

@Data
public class OriginatorKyc {

    private String email;
    private String phoneNum;
    private String iban;
    private String bankCode;
    private String name;
}
