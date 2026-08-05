package in.strikes.transactionDemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PaymentAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private Long orderId;

    private Boolean success;

    public PaymentAudit(BigDecimal amount, Long orderId, Boolean success) {
        this.amount = amount;
        this.orderId = orderId;
        this.success = success;
    }
}
