package com.decagon.bank.entities.models;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.security.auth.login.AccountException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "bank_account", indexes = {
        @Index(name = "account_iban_index", columnList = "account_iban")
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@Access(AccessType.FIELD)
public class AccountModel {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter(AccessLevel.NONE)
    private BigDecimal balance;

    private String bankCode;

    @Setter(AccessLevel.NONE)
    @Column(name = "account_iban", unique = true)
    private String iban;

    @Column(unique = true)
    private String bvn;

    private String accountType;

    private Boolean isLiquidated;

    private Boolean isLiquidityApproval;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToOne
    @JoinColumn
    private UserModel userModel;

    @Setter(AccessLevel.NONE)
    private String currency;

    private String status;


    public AccountModel() {

    }

    public AccountModel(String iban, String currency, String status, String accountType) {

        this.iban = iban;
        this.currency = currency;
        this.status = status;
        this.accountType = accountType;
        this.balance = new BigDecimal("0.00");
    }

    public AccountModel deposit(BigDecimal amount) {

        if (this.balance == null) {
            this.balance = new BigDecimal("0.00");
        }
       this.balance = this.balance.add(amount);

        return this;
    }

    public AccountModel withdraw(BigDecimal amount)
            throws AccountException {
        if (balance.doubleValue() >= 100.00 && balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
        } else {
            throw new AccountException("insufficient account balance!");
        }
        return this;
    }

    public AccountModel liquidate(BigDecimal amount)
            throws AccountException {

        if (balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
        } else {
            throw new AccountException("insufficient account balance!");
        }
        return this;
    }

    //TODO: print Account statement
    public String printStatement() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AccountModel accountModel = (AccountModel) o;
        return Objects.equals(iban, accountModel.iban);
    }

    @Override
    public int hashCode() {

        return Objects.hash(balance, iban, createdAt);
    }

}
