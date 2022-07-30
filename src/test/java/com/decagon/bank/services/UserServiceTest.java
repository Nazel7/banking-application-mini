package com.decagon.bank.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.decagon.bank.dtos.request.SignUpDto;
import com.decagon.bank.dtos.response.UserNotFoundException;
import com.decagon.bank.repositories.AccountRepo;
import com.decagon.bank.repositories.UserRepo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {UserService.class, String.class})
@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @MockBean
    private AccountRepo accountRepo;

    @MockBean
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("test_register_user_will_be_success_on_valid_input_data")
    void testRegisterUser() throws UserNotFoundException {
        SignUpDto signUpDto = mock(SignUpDto.class);
        when(signUpDto.getBvn()).thenReturn("Bvn");
        when(signUpDto.getVerifiedBvn()).thenReturn(true);
        when(signUpDto.getVerifiedPhone()).thenReturn(false);
        when(signUpDto.getAccountType()).thenReturn("3");
        when(signUpDto.getPhone()).thenReturn("4105551212");
        when(signUpDto.getLastName()).thenReturn("Doe");
        when(signUpDto.getFirstName()).thenReturn("Jane");
        doNothing().when(signUpDto).setBvn((String) any());
        doNothing().when(signUpDto).setVerifiedBvn((Boolean) any());
        doNothing().when(signUpDto).setVerifiedPhone((Boolean) any());
        doNothing().when(signUpDto).setAccountType((String) any());
        assertThrows(UserNotFoundException.class, () -> this.userService.registerUser(signUpDto));
        verify(signUpDto).getAccountType();
        verify(signUpDto).getFirstName();
        verify(signUpDto).getLastName();
        verify(signUpDto, atLeast(1)).getPhone();
        verify(signUpDto).getVerifiedBvn();
        verify(signUpDto, atLeast(1)).getVerifiedPhone();
        verify(signUpDto).setAccountType((String) any());
        verify(signUpDto).setBvn((String) any());
        verify(signUpDto).setVerifiedBvn((Boolean) any());
        verify(signUpDto).setVerifiedPhone((Boolean) any());
    }

}

