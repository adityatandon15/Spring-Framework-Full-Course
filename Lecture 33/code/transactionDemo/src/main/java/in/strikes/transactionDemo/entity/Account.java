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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal balance;

    public void debitAccount(BigDecimal amount) {
        if(amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive");
        }

        if(balance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficiemt Balance");
        }

        balance = balance.subtract(amount);
    }

    public void creditAccount(BigDecimal amount) {
        if(amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive");
        }

        balance = balance.add(amount);
    }
}
