package in.strikes.transactionDemo.service;

import in.strikes.transactionDemo.entity.Order;
import in.strikes.transactionDemo.repository.OrderRepository;
import in.strikes.transactionDemo.repository.PaymentAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private PaymentAuditService paymentAuditService;

    public OrderService(OrderRepository orderRepository,
                        PaymentAuditService paymentAuditService) {
        this.orderRepository = orderRepository;
        this.paymentAuditService = paymentAuditService;
    }

    @Transactional
    public void placeOrder(Order order) {
        orderRepository.save(order);

        paymentAuditService.audit(order);
    }
}
