package in.strikes.transactionDemo.repository;

import in.strikes.transactionDemo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
