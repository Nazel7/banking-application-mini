package com.decagon.bank.entities.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "email_index", columnList = "email"),
        @Index(name = "bvn_index", columnList = "bvn"),
        @Index(name = "phone_index", columnList = "phone"),
        @Index(name = "tier_level_index", columnList = "tierLevel"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    @Email(message = "Email must be valid",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @Column(unique = true)
    private String email;
    private String address;
    @Column(unique = true, nullable = false)
    private String phone;
    private String pin;
    @Column(unique = true)
    private String bvn;
    private String tierLevel;
    private String homeAddress;

    @OneToOne(mappedBy = "userModel")
    private AccountModel account;

    private Boolean verifiedEmail;
    private Boolean verifiedPhone;
    private Boolean verifiedBvn;
    private Boolean verifiedHomeAddress;

    @Column(name = ("verification_code"), length = 64)
    private String verificationCode;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @Tolerate
    public UserModel(){

    }
}
