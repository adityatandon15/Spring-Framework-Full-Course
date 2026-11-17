package in.coderarmy.gatekeeper.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GatewayPolicyException extends RuntimeException {

    private final HttpStatus status;
    private final String outcome;

    public GatewayPolicyException(HttpStatus status, String message, String outcome) {
        super(message);
        this.status = status;
        this.outcome = outcome;
    }
}
