package com.decagon.bank.entities.builder;

import com.decagon.bank.dtos.request.SignUpDto;
import com.decagon.bank.dtos.request.UserInfoDto;
import com.decagon.bank.dtos.response.LogginResponse;
import com.decagon.bank.dtos.response.User;
import com.decagon.bank.entities.models.UserModel;
import com.decagon.bank.enums.TranxStatus;

public class UserMapper {

    public static UserModel mapToModel(SignUpDto signUpDto){

        return UserModel
                .builder()
                .firstName(signUpDto.getFirstName())
                .lastName(signUpDto.getLastName())
                .phone(signUpDto.getPhone())
                .bvn(signUpDto.getBvn())
                .verifiedPhone(signUpDto.getVerifiedPhone())
                .verifiedBvn(signUpDto.getVerifiedBvn())
                .build();
    }

    public static User mapToDomain(UserModel userModel, String iban){

        return User
                .builder()
                .status(TranxStatus.SUCCESSFUL.name())
                .accountNo(iban)
                .build();
    }

}
