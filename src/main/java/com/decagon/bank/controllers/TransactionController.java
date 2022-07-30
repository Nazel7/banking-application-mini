package com.decagon.bank.controllers;

import com.decagon.bank.dtos.request.LiquidateDto;
import com.decagon.bank.dtos.request.TopupDto;
import com.decagon.bank.dtos.request.TransferDto;
import com.decagon.bank.dtos.request.WithrawalDto;
import com.decagon.bank.dtos.response.Account;
import com.decagon.bank.dtos.response.Transaction;
import com.decagon.bank.dtos.response.TransferNotValidException;
import com.decagon.bank.services.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("transactions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionController {

    private final TransactionService mTransactionService;

    @Async
    @CrossOrigin
    @PostMapping("/transfer")
    @ApiOperation(value = "::: Transfer Fund :::", notes = "APi for fund transfer")
    public CompletableFuture<ResponseEntity<Transaction>> transferFund(@RequestBody TransferDto transferDto, HttpServletRequest request)
            throws TransferNotValidException {

        final Transaction transaction = mTransactionService.transferFund(transferDto, request);

        return CompletableFuture.completedFuture(new ResponseEntity<>(transaction, HttpStatus.OK));
    }

    @Async
    @CrossOrigin
    @ApiOperation(value = "::: Deposit :::", notes = "Api for quick account topUp")
    @PutMapping("/deposits")
    public CompletableFuture<ResponseEntity<Account>> fundAccount(@RequestBody TopupDto topupDto, HttpServletRequest request)
            throws TransferNotValidException {

        final Account account = mTransactionService.deposit(topupDto, request);

        return CompletableFuture.completedFuture(new ResponseEntity<>(account, HttpStatus.OK));
    }

    @Async
    @CrossOrigin
    @ApiOperation(value = "::: Withdrawal :::", notes = "Api for quick account withrawal")
    @PutMapping("/withdraw")
    public CompletableFuture<ResponseEntity<Account>> withdrawAmount(@RequestBody WithrawalDto withrawalDto, HttpServletRequest request)
            throws TransferNotValidException {

        final Account account = mTransactionService.withdrawal(withrawalDto, request);

        return CompletableFuture.completedFuture(new ResponseEntity<>(account, HttpStatus.OK));
    }

    @Async
    @CrossOrigin
    @ApiOperation(value = "::: Liquidate Account :::", notes = "Api for quick account liquidity")
    @PutMapping("/liquidate")
    public CompletableFuture<ResponseEntity<Account>> liquidateAccount(@RequestBody LiquidateDto liquidateDto, HttpServletRequest request)
            throws TransferNotValidException {

        final Account account = mTransactionService.liquidateAccount(liquidateDto, request);

        return CompletableFuture.completedFuture(new ResponseEntity<>(account, HttpStatus.OK));
    }


    @Async
    @CrossOrigin
    @ApiOperation(value = "::: Get Transactions :::", notes = "Api for quick account liquidity")
    @PutMapping
    public CompletableFuture<ResponseEntity<Account>> getTransactions(@RequestParam(value = "accountId", required = false) String accountId,
                                                                      @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
                                                                      @RequestParam(value = "size", defaultValue = "30", required = false) Integer size,
                                                                      HttpServletRequest request)
            throws TransferNotValidException {

        final Account account = mTransactionService.getTransactionHistory(accountId, pageNo, size, request);

        return CompletableFuture.completedFuture(new ResponseEntity<>(account, HttpStatus.OK));
    }

    @Async
    @CrossOrigin
    @ApiOperation(value = "::: Get Transactions :::", notes = "Api for quick account liquidity")
    @GetMapping
    public CompletableFuture<?> getTransactions()
            throws TransferNotValidException {

        return CompletableFuture.completedFuture(new ResponseEntity<>("::: Welcome to Decagon-Gafar-Bank", HttpStatus.OK));
    }



}
