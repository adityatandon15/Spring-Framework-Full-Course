package in.strikes.transactionDemo.repository;

import in.strikes.transactionDemo.entity.PaymentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAuditRepository extends JpaRepository<PaymentAudit, Long> {
}
