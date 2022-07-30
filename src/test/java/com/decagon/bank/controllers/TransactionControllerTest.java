package com.decagon.bank.controllers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.decagon.bank.dtos.request.OriginatorKyc;
import com.decagon.bank.dtos.request.TopupDto;
import com.decagon.bank.dtos.request.WithrawalDto;
import com.decagon.bank.dtos.response.Account;
import com.decagon.bank.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {TransactionController.class})
@ExtendWith(SpringExtension.class)
class TransactionControllerTest {
    @Autowired
    private TransactionController transactionController;

    @MockBean
    private TransactionService transactionService;

    @Test
    @DisplayName("testFundAccount_return_200_for_valid_request_body")
    void testFundAccount() throws Exception {
        when(this.transactionService
                     .deposit((TopupDto) any(), (javax.servlet.http.HttpServletRequest) any()))
                .thenReturn(mock(Account.class));

        OriginatorKyc originatorKyc = new OriginatorKyc();
        originatorKyc.setEmail("jane.doe@example.org");
        originatorKyc.setBankCode("Bank Code");
        originatorKyc.setName("Name");
        originatorKyc.setPhoneNum("4105551212");
        originatorKyc.setIban("Iban");

        TopupDto topupDto = new TopupDto();
        topupDto.setAmount(BigDecimal.valueOf(42L));
        topupDto.setOriginatorKyc(originatorKyc);
        topupDto.setTranxNaration("Tranx Naration");
        topupDto.setTranxRef("Tranx Ref");
        topupDto.setCurrency("GBP");
        topupDto.setIban("Iban");
        topupDto.setTranxType("Tranx Type");
        topupDto.setChannelCode("Channel Code");
        String content = (new ObjectMapper()).writeValueAsString(topupDto);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/transactions/deposits")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(content);
        MockMvcBuilders.standaloneSetup(this.transactionController)
                       .build()
                       .perform(requestBuilder)
                       .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("getTransactions_return_200_for_valid_request_body")
    void testGetTransactions() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transactions");
        ResultActions actualPerformResult =
                MockMvcBuilders.standaloneSetup(this.transactionController)
                               .build()
                               .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    @DisplayName("testWithdrawAmount_return_200_for_valid_request_body")
    void testWithdrawAmount() throws Exception {
        when(this.transactionService.withdrawal((WithrawalDto) any(),
                                                (javax.servlet.http.HttpServletRequest) any()))
                .thenReturn(mock(Account.class));

        WithrawalDto withrawalDto = new WithrawalDto();
        withrawalDto.setVerificationCode("Verification Code");
        withrawalDto.setAmount(BigDecimal.valueOf(42L));
        withrawalDto.setTranxNaration("Tranx Naration");
        withrawalDto.setTranxRef("Tranx Ref");
        withrawalDto.setCurrency("GBP");
        withrawalDto.setIban("Iban");
        withrawalDto.setTranxType("Tranx Type");
        withrawalDto.setChannelCode("Channel Code");
        String content = (new ObjectMapper()).writeValueAsString(withrawalDto);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/transactions/withdraw")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(content);
        MockMvcBuilders.standaloneSetup(this.transactionController)
                       .build()
                       .perform(requestBuilder)
                       .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

