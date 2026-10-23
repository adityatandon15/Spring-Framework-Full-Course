package in.coderarmy.gatekeeper.management.service;

import in.coderarmy.gatekeeper.management.dto.requestDto.HeaderRuleRequest;
import in.coderarmy.gatekeeper.management.dto.requestDto.RouteRequest;
import in.coderarmy.gatekeeper.management.dto.requestDto.RouteStatusRequest;
import in.coderarmy.gatekeeper.management.dto.responseDto.AuditRecordResponse;
import in.coderarmy.gatekeeper.management.dto.responseDto.RouteResponse;
import in.coderarmy.gatekeeper.management.dto.responseDto.RouteVersionResponse;
import in.coderarmy.gatekeeper.management.entity.ApiRoute;
import in.coderarmy.gatekeeper.management.entity.ControlPlaneAuditRecord;
import in.coderarmy.gatekeeper.management.entity.Organization;
import in.coderarmy.gatekeeper.management.entity.OrganizationStatus;
import in.coderarmy.gatekeeper.management.entity.RouteConfigurationVersion;
import in.coderarmy.gatekeeper.management.entity.RouteHeaderRule;
import in.coderarmy.gatekeeper.management.exception.ApiException;
import in.coderarmy.gatekeeper.management.repository.ApiRouteRepository;
import in.coderarmy.gatekeeper.management.repository.ControlPlaneAuditRecordRepository;
import in.coderarmy.gatekeeper.management.repository.RouteConfigurationVersionRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class RouteService {

    private final ApiRouteRepository apiRouteRepository;
    private final RouteConfigurationVersionRepository versionRepository;
    private final ControlPlaneAuditRecordRepository auditRecordRepository;
    private final TenantContext tenantContext;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public RouteService(ApiRouteRepository apiRouteRepository,
                        RouteConfigurationVersionRepository versionRepository,
                        ControlPlaneAuditRecordRepository auditRecordRepository, TenantContext tenantContext,
                        AuditService auditService, ObjectMapper objectMapper) {
        this.apiRouteRepository = apiRouteRepository;
        this.versionRepository = versionRepository;
        this.auditRecordRepository = auditRecordRepository;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RouteResponse create(RouteRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();
        validate(request, organization);

        if(apiRouteRepository.existsByOrganizationIdAndRouteId(organization.getId(), request.getRouteId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Route ID already exists for this organization");
        }

        ApiRoute route = new ApiRoute();
        route.setOrganization(organization);
        route.setConfigurationVersion(1L);
        route.setCreatedAt(LocalDateTime.now());
        apply(route, request);
        apiRouteRepository.save(route);

        saveVersion(route, "CREATED");

        auditService.record(organization, "CREATE_ROUTE",
                "API_ROUTE", route.getRouteId(), startedAt,
                "Route configuration version 1 created");
        return RouteResponse.from(route);
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> findAllForTenant() {
        Organization organization = tenantContext.getOrganization();
        return toResponses(apiRouteRepository.findAllByOrganizationId(organization.getId()));
    }

    @Transactional(readOnly = true)
    public RouteResponse findById(Long id) {
        Organization organization = tenantContext.getOrganization();
        return RouteResponse.from(getOwnedRoute(id, organization.getId()));
    }

    @Transactional
    public RouteResponse update(Long id, RouteRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();
        ApiRoute route = getOwnedRoute(id, organization.getId());

        if(!route.getRouteId().equals(request.getRouteId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "routeId cannot be changed");
        }

        validate(request, organization);

        route.setConfigurationVersion(route.getConfigurationVersion() + 1);

        apply(route, request);

        saveVersion(route, "UPDATED");

        auditService.record(organization, "UPDATE_ROUTE", "API_ROUTE",
                route.getRouteId(), startedAt, "Route configuration version " +
                        route.getConfigurationVersion() + " created");

        return RouteResponse.from(route);
    }

    @Transactional
    public RouteResponse changeStatus(Long id, RouteStatusRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();
        ApiRoute route = getOwnedRoute(id, organization.getId());
        route.setActive(request.getActive());
        route.setConfigurationVersion(route.getConfigurationVersion() + 1);
        route.setUpdatedAt(LocalDateTime.now());

        String changeType = request.getActive() ? "ACTIVATED" : "DEACTIVATED";
        String action = request.getActive() ? "ACTIVATE_ROUTE" : "DEACTIVATE_ROUTE";

        saveVersion(route, changeType);

        auditService.record(organization, action, "API_ROUTE", route.getRouteId(), startedAt,
                "Route configuration version " + route.getConfigurationVersion() + " created");

        return RouteResponse.from(route);
    }

    @Transactional
    public void delete(Long id) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();
        ApiRoute route = getOwnedRoute(id, organization.getId());
        route.setConfigurationVersion(route.getConfigurationVersion() + 1);
        route.setUpdatedAt(LocalDateTime.now());

        saveVersion(route, "DELETED");

        auditService.record(organization, "DELETE_ROUTE", "API_ROUTE", route.getRouteId(), startedAt,
                "Final configuration saved as version " + route.getConfigurationVersion());

        apiRouteRepository.delete(route);
    }

    @Transactional(readOnly = true)
    public List<RouteVersionResponse> getVersions(String routeId) {
        Organization organization = tenantContext.getOrganization();

        List<RouteVersionResponse> response = new ArrayList<RouteVersionResponse>();
        List<RouteConfigurationVersion> versions = versionRepository
                .findAllByOrganizationIdAndRouteIdOrderByVersionNumberDesc(organization.getId(), routeId);

        for(RouteConfigurationVersion version : versions) {
            response.add(RouteVersionResponse.from(version));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> findAllActiveForGateway() {
        return toResponses(apiRouteRepository.findAllByActiveTrueAndOrganizationStatus(OrganizationStatus.ACTIVE));
    }

    private ApiRoute getOwnedRoute(Long id, Long organizationId) {
        return apiRouteRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Route not found"));
    }

    private void apply(ApiRoute route, RouteRequest request) {
        route.setRouteId(request.getRouteId());
        route.setName(request.getName());
        route.setPathPattern(request.getPathPattern());
        route.setTargetBaseUrl(request.getTargetBaseUrl());
        route.setTargetPathPrefix(request.getTargetPathPrefix());
        route.setRequestsPerMinute(request.getRequestsPerMinute());
        route.setResponseTimeoutMs(request.getResponseTimeoutMs());
        route.setIdempotencyEnabled(request.isIdempotencyEnabled());
        route.setActive(request.isActive());
        route.setUpdatedAt(LocalDateTime.now());
        route.setAllowedMethods(normalizeMethods(request.getAllowedMethods()));

        route.getHeaderRules().clear();
        for(HeaderRuleRequest ruleRequest : request.getHeaderRules()) {
            RouteHeaderRule rule = new RouteHeaderRule();
            rule.setRoute(route);
            rule.setDirection(ruleRequest.getDirection().toUpperCase(Locale.ENGLISH));
            rule.setAction(ruleRequest.getAction().toUpperCase(Locale.ENGLISH));
            rule.setHeaderName(ruleRequest.getHeaderName());
            rule.setHeaderValue(ruleRequest.getHeaderValue());
            rule.setCreatedAt(LocalDateTime.now());
            rule.setUpdatedAt(LocalDateTime.now());
            route.getHeaderRules().add(rule);
        }
    }

    private void validate(RouteRequest request, Organization organization) {
        String tenantPrefix = "/" + organization.getSlug() + "/";

        if(!request.getPathPattern().startsWith(tenantPrefix)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "pathPattern must start with the tenant prefix " + tenantPrefix);
        }

        if(!request.getTargetPathPrefix().startsWith("/")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "targetPathPrefix must start with /");
        }

        try {
            URI uri = URI.create(request.getTargetBaseUrl());
            if(!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException();
            }
        }
        catch(IllegalArgumentException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "targetBaseUrl must be a valid HTTP or HTTPS URL");
        }

        normalizeMethods(request.getAllowedMethods());

        for(HeaderRuleRequest rule : request.getHeaderRules()) {
            String direction = rule.getDirection().toUpperCase(Locale.ENGLISH);
            String action = rule.getAction().toUpperCase(Locale.ENGLISH);
            if(!"REQUEST".equals(direction) && !"RESPONSE".equals(direction)) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Header direction must be REQUEST or RESPONSE");
            }
            if(!"ADD".equals(action) && !"REMOVE".equals(action)) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Header action must be ADD or REMOVE");
            }
            if("ADD".equals(action) &&
                    (rule.getHeaderValue() == null || rule.getHeaderValue().trim().isEmpty())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "ADD header rules require headerValue");
            }
        }
    }

    private Set<String> normalizeMethods(Set<String> methods) {
        Set<String> normalized = new HashSet<String>();
        for(String method : methods) {
            String value = method.toUpperCase(Locale.ENGLISH);
            try {
                HttpMethod.valueOf(value);
            }
            catch(IllegalArgumentException exception) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported HTTP method: " + method);
            }
            normalized.add(value);
        }
        return normalized;
    }

    private void saveVersion(ApiRoute route, String changeType) {
        RouteConfigurationVersion version = new RouteConfigurationVersion();
        version.setOrganization(route.getOrganization());
        version.setRouteId(route.getRouteId());
        version.setVersionNumber(route.getConfigurationVersion());
        version.setChangeType(changeType);
        version.setConfigurationSnapshot(objectMapper.writeValueAsString(RouteResponse.from(route)));
        version.setCreatedAt(LocalDateTime.now());
        versionRepository.save(version);
    }

    private List<RouteResponse> toResponses(List<ApiRoute> routes) {
        List<RouteResponse> response = new ArrayList<RouteResponse>();
        for(ApiRoute route : routes) {
            response.add(RouteResponse.from(route));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<AuditRecordResponse> getAuditRecords() {
        Organization organization = tenantContext.getOrganization();

        List<AuditRecordResponse> response = new ArrayList<AuditRecordResponse>();

        List<ControlPlaneAuditRecord> records = auditRecordRepository
                .findAllByOrganizationIdOrderByCreatedAtDesc(organization.getId());

        for(ControlPlaneAuditRecord record : records) {
            response.add(AuditRecordResponse.from(record));
        }
        return response;
    }
}
