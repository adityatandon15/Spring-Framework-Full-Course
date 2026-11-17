package in.coderarmy.gatekeeper.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GatewayErrorResponse {

    private int status;
    private String message;
    private String correlationId;
    private LocalDateTime timestamp;
}
