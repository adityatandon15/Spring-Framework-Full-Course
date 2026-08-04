package in.strikes.transactionDemo.service;

import in.strikes.transactionDemo.entity.Account;
import in.strikes.transactionDemo.entity.TransferRecord;
import in.strikes.transactionDemo.repository.AccountRepository;
import in.strikes.transactionDemo.repository.TransferRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private AccountRepository accountRepository;
    private TransferRepository transferRepository;

    public TransferService(TransferRepository transferRepository,
                           AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public void transfer(Long fromAccountId,
                         Long toAccountId,
                         BigDecimal amount) throws Throwable {

        Account fromAccount =
                accountRepository.findById(fromAccountId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        Account toAccount =
                accountRepository.findById(toAccountId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        fromAccount.debitAccount(amount);
        accountRepository.saveAndFlush(fromAccount);

        toAccount.creditAccount(amount);
        accountRepository.saveAndFlush(toAccount);

        transferRepository.save(new TransferRecord(
                fromAccountId,
                toAccountId,
                amount,
                LocalDate.now()
        ));
        transferRepository.flush();

        throw new RuntimeException("Some Error Occured");
    }
}
