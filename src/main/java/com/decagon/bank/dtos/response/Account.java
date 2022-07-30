package com.decagon.bank.dtos.response;

import com.decagon.bank.entities.models.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    private String status;
    private UUID userId;
    private Double depositedAmount;
    private Double withdrawnAmount;
    private Double balance;
    private String iban;
    private Date createdAt;
    private Date updatedAt;
    private String currency;
    private List<Transaction> mTransactions = new ArrayList<>();
    private Long totalElement;
    private Integer historySize;

}
