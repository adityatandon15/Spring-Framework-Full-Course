package in.strikes.transactionDemo.repository;

import in.strikes.transactionDemo.entity.TransferRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<TransferRecord, Long> {
}
