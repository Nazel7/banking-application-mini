package com.decagon.bank.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.decagon.bank.configs.TranxMessageConfig;
import com.decagon.bank.dtos.request.OriginatorKyc;
import com.decagon.bank.dtos.request.TopupDto;
import com.decagon.bank.dtos.request.WithrawalDto;
import com.decagon.bank.dtos.response.TransferNotValidException;
import com.decagon.bank.entities.models.TransactionModel;
import com.decagon.bank.repositories.AccountRepo;
import com.decagon.bank.repositories.TransactionRepo;
import com.decagon.bank.repositories.UserRepo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {TransactionService.class, TranxMessageConfig.class})
@ExtendWith(SpringExtension.class)
class TransactionServiceTest {
    @MockBean
    private AccountRepo accountRepo;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private TransactionRepo transactionRepo;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TranxMessageConfig tranxMessageConfig;

    @MockBean
    private UserRepo userRepo;

    @Test
    void testDeposit() throws TransferNotValidException {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setIsLiquidate(true);
        transactionModel.setBenefAccountNo("3");
        transactionModel.setDebitAccountNo("3");
        transactionModel.setAmount(BigDecimal.valueOf(42L));
        transactionModel.setLiquidityApproval(true);
        transactionModel.setPaymentReference("Payment Reference");
        transactionModel.setTranxRef("Tranx Ref");
        transactionModel.setTranNarration("Tran Narration");
        transactionModel.setCurrency("GBP");
        transactionModel.setUserId(123L);
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        transactionModel
                .setCreatedAt(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
        transactionModel.setStatus("Status");
        transactionModel.setId(123L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        transactionModel
                .setUpdatedAt(Date.from(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant()));
        transactionModel.setUserToken("ABC123");
        transactionModel.setChannelCode("Channel Code");
        transactionModel.setTranType("Tran Type");
        when(this.transactionRepo.findTransactionModelByTranxRef((String) any()))
                .thenReturn(transactionModel);

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
        assertThrows(TransferNotValidException.class,
                     () -> this.transactionService.deposit(topupDto, new MockHttpServletRequest()));
        verify(this.transactionRepo).findTransactionModelByTranxRef((String) any());
    }

    @Test
    void testDeposit2() throws TransferNotValidException {
        when(this.transactionRepo.findTransactionModelByTranxRef((String) any()))
                .thenThrow(new IllegalArgumentException("foo"));

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
        assertThrows(TransferNotValidException.class,
                     () -> this.transactionService.deposit(topupDto, new MockHttpServletRequest()));
        verify(this.transactionRepo).findTransactionModelByTranxRef((String) any());
    }

    @Test
    void testDeposit3() throws TransferNotValidException {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setIsLiquidate(true);
        transactionModel.setBenefAccountNo("3");
        transactionModel.setDebitAccountNo("3");
        transactionModel.setAmount(BigDecimal.valueOf(42L));
        transactionModel.setLiquidityApproval(true);
        transactionModel.setPaymentReference("Payment Reference");
        transactionModel.setTranxRef("Tranx Ref");
        transactionModel.setTranNarration("Tran Narration");
        transactionModel.setCurrency("GBP");
        transactionModel.setUserId(123L);
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        transactionModel
                .setCreatedAt(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
        transactionModel.setStatus("Status");
        transactionModel.setId(123L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        transactionModel
                .setUpdatedAt(Date.from(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant()));
        transactionModel.setUserToken("ABC123");
        transactionModel.setChannelCode("Channel Code");
        transactionModel.setTranType("Tran Type");
        when(this.transactionRepo.findTransactionModelByTranxRef((String) any()))
                .thenReturn(transactionModel);

        OriginatorKyc originatorKyc = new OriginatorKyc();
        originatorKyc.setEmail("jane.doe@example.org");
        originatorKyc.setBankCode("Bank Code");
        originatorKyc.setName("Name");
        originatorKyc.setPhoneNum("4105551212");
        originatorKyc.setIban("Iban");

        TopupDto topupDto = new TopupDto();
        topupDto.setAmount(null);
        topupDto.setOriginatorKyc(originatorKyc);
        topupDto.setTranxNaration("Tranx Naration");
        topupDto.setTranxRef("Tranx Ref");
        topupDto.setCurrency("GBP");
        topupDto.setIban("Iban");
        topupDto.setTranxType("Tranx Type");
        topupDto.setChannelCode("Channel Code");
        assertThrows(TransferNotValidException.class,
                     () -> this.transactionService.deposit(topupDto, new MockHttpServletRequest()));
    }

    @Test
    void testWithdrawal() throws TransferNotValidException {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setIsLiquidate(true);
        transactionModel.setBenefAccountNo("3");
        transactionModel.setDebitAccountNo("3");
        transactionModel.setAmount(BigDecimal.valueOf(42L));
        transactionModel.setLiquidityApproval(true);
        transactionModel.setPaymentReference("Payment Reference");
        transactionModel.setTranxRef("Tranx Ref");
        transactionModel.setTranNarration("Tran Narration");
        transactionModel.setCurrency("GBP");
        transactionModel.setUserId(123L);
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        transactionModel
                .setCreatedAt(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
        transactionModel.setStatus("Status");
        transactionModel.setId(123L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        transactionModel
                .setUpdatedAt(Date.from(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant()));
        transactionModel.setUserToken("ABC123");
        transactionModel.setChannelCode("Channel Code");
        transactionModel.setTranType("Tran Type");
        when(this.transactionRepo.findTransactionModelByTranxRef((String) any()))
                .thenReturn(transactionModel);

        WithrawalDto withrawalDto = new WithrawalDto();
        withrawalDto.setVerificationCode("Verification Code");
        withrawalDto.setAmount(BigDecimal.valueOf(42L));
        withrawalDto.setTranxNaration("Tranx Naration");
        withrawalDto.setTranxRef("Tranx Ref");
        withrawalDto.setCurrency("GBP");
        withrawalDto.setIban("Iban");
        withrawalDto.setTranxType("Tranx Type");
        withrawalDto.setChannelCode("Channel Code");
        assertThrows(TransferNotValidException.class,
                     () -> this.transactionService
                             .withdrawal(withrawalDto, new MockHttpServletRequest()));
        verify(this.transactionRepo).findTransactionModelByTranxRef((String) any());
    }

    @Test
    void testWithdrawal2() throws TransferNotValidException {
        when(this.transactionRepo.findTransactionModelByTranxRef((String) any()))
                .thenThrow(new IllegalArgumentException("foo"));

        WithrawalDto withrawalDto = new WithrawalDto();
        withrawalDto.setVerificationCode("Verification Code");
        withrawalDto.setAmount(BigDecimal.valueOf(42L));
        withrawalDto.setTranxNaration("Tranx Naration");
        withrawalDto.setTranxRef("Tranx Ref");
        withrawalDto.setCurrency("GBP");
        withrawalDto.setIban("Iban");
        withrawalDto.setTranxType("Tranx Type");
        withrawalDto.setChannelCode("Channel Code");
        assertThrows(TransferNotValidException.class,
                     () -> this.transactionService
                             .withdrawal(withrawalDto, new MockHttpServletRequest()));
        verify(this.transactionRepo).findTransactionModelByTranxRef((String) any());
    }

    @Test
    void testWithdrawal3() throws TransferNotValidException {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setIsLiquidate(true);
        transactionModel.setBenefAccountNo("3");
        transactionModel.setDebitAccountNo("3");
        transactionModel.setAmount(BigDecimal.valueOf(42L));
        transactionModel.setLiquidityApproval(true);
        transactionModel.setPaymentReference("Payment Reference");
        transactionModel.setTranxRef("Tranx Ref");
        transactionModel.setTranNarration("Tran Narration");
        transactionModel.setCurrency("GBP");
        transactionModel.setUserId(123L);
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        transactionModel
                .setCreatedAt(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
        transactionModel.setStatus("Status");
        transactionModel.setId(123L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        transactionModel
                .setUpdatedAt(Date.from(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant()));
        transactionModel.setUserToken("ABC123");
        transactionModel.setChannelCode("Channel Code");
        transactionModel.setTranType("Tran Type");
        when(this.transactionRepo.findTransactionModelByTranxRef((String) any()))
                .thenReturn(transactionModel);

        WithrawalDto withrawalDto = new WithrawalDto();
        withrawalDto.setVerificationCode("Verification Code");
        withrawalDto.setAmount(null);
        withrawalDto.setTranxNaration("Tranx Naration");
        withrawalDto.setTranxRef("Tranx Ref");
        withrawalDto.setCurrency("GBP");
        withrawalDto.setIban("Iban");
        withrawalDto.setTranxType("Tranx Type");
        withrawalDto.setChannelCode("Channel Code");
        assertThrows(TransferNotValidException.class,
                     () -> this.transactionService
                             .withdrawal(withrawalDto, new MockHttpServletRequest()));
    }
}

