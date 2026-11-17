package in.coderarmy.gatekeeper.gateway.service;

import in.coderarmy.gatekeeper.gateway.dto.GatewayRoute;
import in.coderarmy.gatekeeper.gateway.exception.GatewayPolicyException;
import in.coderarmy.gatekeeper.gateway.model.RateLimitResult;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RateLimitService {

    private static final long WINDOW_SECONDS = 60;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<String> rateLimitScript;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.rateLimitScript = new DefaultRedisScript<String>();

        this.rateLimitScript.setResultType(String.class);

        this.rateLimitScript.setScriptText(
                "local current = redis.call('INCR', KEYS[1]); " +
                        "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end; " +
                        "local ttl = redis.call('TTL', KEYS[1]); " +
                        "return tostring(current) .. ':' .. tostring(ttl);");
    }

    public RateLimitResult check(GatewayRoute route, String clientIp) {
        if(route.getRequestsPerMinute() == null) {
            return RateLimitResult.skipped();
        }

        String key = "gateway:rate:" + route.getOrganizationId() + ":" + route.getRouteId() + ":" +
                clientIp.replace(':', '_');

        try {
            String result = redisTemplate.execute(rateLimitScript,
                    Collections.singletonList(key), String.valueOf(WINDOW_SECONDS));

            if(result == null || !result.contains(":")) {
                throw unavailable();
            }

            String[] values = result.split(":");

            long current = Long.parseLong(values[0]);

            long ttl = Math.max(Long.parseLong(values[1]), 1);

            long limit = route.getRequestsPerMinute();

            return new RateLimitResult(current <= limit, limit,
                    Math.max(limit - current, 0), ttl);
        }
        catch(GatewayPolicyException exception) {
            throw exception;
        }
        catch(Exception exception) {
            throw unavailable();
        }
    }

    private GatewayPolicyException unavailable() {
        return new GatewayPolicyException(HttpStatus.SERVICE_UNAVAILABLE,
                "Rate limiting is temporarily unavailable", "POLICY_UNAVAILABLE");
    }
}
