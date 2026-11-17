package in.coderarmy.gatekeeper.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateLimitResult {

    private boolean allowed;
    private long limit;
    private long remaining;
    private long retryAfterSeconds;

    public static RateLimitResult skipped() {
        return new RateLimitResult(true, 0, 0, 0);
    }
}
