package in.strikes.transactionDemo.repository;

import in.strikes.transactionDemo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {


}
