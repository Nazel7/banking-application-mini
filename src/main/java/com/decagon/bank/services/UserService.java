package com.decagon.bank.services;

import com.decagon.bank.dtos.request.SignUpDto;
import com.decagon.bank.dtos.response.User;
import com.decagon.bank.dtos.response.UserNotFoundException;
import com.decagon.bank.entities.builder.UserMapper;
import com.decagon.bank.entities.models.AccountModel;
import com.decagon.bank.entities.models.UserModel;
import com.decagon.bank.enums.AccountType;
import com.decagon.bank.repositories.AccountRepo;
import com.decagon.bank.repositories.UserRepo;
import com.decagon.bank.utils.BaseUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepo mUserRepo;
    private final AccountRepo mAccountRepo;

    @Value("${spring.application.bank-code}")
    private String bankCode;

    @Transactional
    public User registerUser(final SignUpDto signUpDto) throws UserNotFoundException {
        log.info("::: In registerUser.....");

        // Proper way is to be sent from the request as this is the limitation to this exercise.
        signUpDto.setAccountType(AccountType.SAVINGS.name());
        signUpDto.setVerifiedPhone(true);
        signUpDto.setVerifiedBvn(true);
        signUpDto.setBvn(BaseUtil.generateDummyBVN());

        if (!BaseUtil.isRequestSatisfied(signUpDto)) {
            throw new UserNotFoundException("Unsatisfied request body");
        }
        log.info("::: About to map to model......");
        UserModel userMapped = UserMapper.mapToModel(signUpDto);

        final AccountModel accountModel = BaseUtil.generateAccountNumber(userMapped, signUpDto.getAccountType());

        accountModel.setUserModel(userMapped);
        accountModel.setBankCode(bankCode);
        UserModel userModelSaved = mUserRepo.save(userMapped);
        log.info("::: New user with id: [{}] saved to DB :::", userModelSaved.getId());

        AccountModel accountModelSaved = mAccountRepo.save(accountModel);
        log.info("::: New account creation for user with id: [{}] saved to DB :::",
                 accountModelSaved.getId());

        log.info("ACCCCCNO: " + accountModel.getIban());
        return UserMapper.mapToDomain(userModelSaved, accountModel.getIban());

    }

}
