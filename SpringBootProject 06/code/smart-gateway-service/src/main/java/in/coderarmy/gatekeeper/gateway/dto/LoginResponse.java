package in.coderarmy.gatekeeper.gateway.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;
    private long expiresInSeconds;
    private String role;
    private Long organizationId;
}
