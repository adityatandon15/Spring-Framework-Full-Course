package in.strikes.transactionDemo.controller;

import in.strikes.transactionDemo.entity.Account;
import in.strikes.transactionDemo.entity.TransferRecord;
import in.strikes.transactionDemo.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<String> transferAmount(
            @RequestBody TransferRecord record) throws Throwable {

        transferService.transfer(record.getFromAccountId(),
                record.getToAccountId(),
                record.getAmount());

        return ResponseEntity.ok("DONE");
    }
}
