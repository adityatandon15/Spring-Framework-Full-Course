package in.coderarmy.gatekeeper.management.dto.requestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RouteRequest {

    @NotBlank
    @Size(max = 100)
    private String routeId;

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String pathPattern;

    @NotBlank
    @Size(max = 1000)
    private String targetBaseUrl;

    @NotBlank
    @Size(max = 500)
    private String targetPathPrefix;

    @Min(1)
    private Integer requestsPerMinute;

    @NotNull
    @Min(1)
    private Integer responseTimeoutMs;

    private boolean idempotencyEnabled;
    private boolean active;

    @NotEmpty
    private Set<@NotBlank String> allowedMethods
            = new HashSet<String>();

    @NotNull
    private List<@Valid HeaderRuleRequest> headerRules
            = new ArrayList<HeaderRuleRequest>();
}
