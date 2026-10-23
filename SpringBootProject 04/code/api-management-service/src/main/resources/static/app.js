(function() {
    "use strict";

    var SESSION_KEY = "gatekeeper-control-plane-session";
    var state = {
        token: null,
        username: null,
        role: null,
        organizationId: null,
        organizations: [],
        routes: [],
        organization: null,
        audits: [],
        currentSection: "overview"
    };

    var elements = {};

    document.addEventListener("DOMContentLoaded", function() {
        cacheElements();
        bindEvents();
        restoreSession();
    });

    function cacheElements() {
        var ids = [
            "loginView", "dashboardView", "loginForm", "loginUsername", "loginPassword", "loginButton",
            "loginError", "togglePassword", "mainNavigation", "sidebarUsername", "sidebarRole", "userInitials",
            "logoutButton", "mobileMenuButton", "breadcrumbRole", "pageTitle", "roleBadge", "overviewSection",
            "organizationsSection", "routesSection", "organizationProfileSection", "auditSection",
            "createOrganizationButton", "refreshOrganizationsButton", "organizationSearch", "organizationsTableBody",
            "organizationsEmpty", "createRouteButton", "refreshRoutesButton", "routeSearch", "routesTableBody",
            "routesEmpty", "tenantOrganizationCard", "refreshAuditButton", "auditTableBody", "auditEmpty",
            "organizationDialog", "organizationForm", "organizationDialogTitle", "organizationId", "organizationName",
            "organizationSlug", "tenantAdminDialog", "tenantAdminForm", "tenantAdminDialogTitle",
            "tenantAdminOrganization", "tenantAdminOrganizationId", "tenantAdminExists", "tenantAdminUsername",
            "tenantAdminPassword", "tenantAdminEnabled", "deleteTenantAdminButton", "routeDialog", "routeForm",
            "routeDialogTitle", "routePk", "routeId", "routeName", "routePathPattern", "routeTargetBaseUrl",
            "routeTargetPathPrefix", "routeRequestsPerMinute", "routeResponseTimeout", "routeAllowedMethods",
            "routeIdempotency", "routeActive", "addHeaderRuleButton", "headerRulesContainer", "versionsDialog",
            "versionsDialogTitle", "versionsTimeline", "toastContainer"
        ];

        ids.forEach(function(id) {
            elements[id] = document.getElementById(id);
        });
    }

    function bindEvents() {
        elements.loginForm.addEventListener("submit", login);
        elements.togglePassword.addEventListener("click", togglePassword);
        elements.logoutButton.addEventListener("click", logout);
        elements.mobileMenuButton.addEventListener("click", toggleMobileMenu);
        elements.createOrganizationButton.addEventListener("click", function() { openOrganizationDialog(); });
        elements.refreshOrganizationsButton.addEventListener("click", loadOrganizations);
        elements.organizationSearch.addEventListener("input", renderOrganizations);
        elements.organizationsTableBody.addEventListener("click", handleOrganizationAction);
        elements.organizationsTableBody.addEventListener("change", handleOrganizationStatus);
        elements.organizationForm.addEventListener("submit", saveOrganization);
        elements.tenantAdminForm.addEventListener("submit", saveTenantAdmin);
        elements.deleteTenantAdminButton.addEventListener("click", deleteTenantAdmin);
        elements.createRouteButton.addEventListener("click", function() { openRouteDialog(); });
        elements.refreshRoutesButton.addEventListener("click", loadRoutes);
        elements.routeSearch.addEventListener("input", renderRoutes);
        elements.routesTableBody.addEventListener("click", handleRouteAction);
        elements.routeForm.addEventListener("submit", saveRoute);
        elements.addHeaderRuleButton.addEventListener("click", function() { addHeaderRule(); });
        elements.headerRulesContainer.addEventListener("click", removeHeaderRule);
        elements.refreshAuditButton.addEventListener("click", loadAudits);

        document.querySelectorAll(".close-dialog").forEach(function(button) {
            button.addEventListener("click", function() {
                button.closest("dialog").close();
            });
        });

        document.querySelectorAll("dialog").forEach(function(dialog) {
            dialog.addEventListener("click", function(event) {
                if(event.target === dialog) {
                    dialog.close();
                }
            });
        });

        document.querySelectorAll(".demo-login").forEach(function(button) {
            button.addEventListener("click", function() {
                elements.loginUsername.value = button.dataset.username;
                elements.loginPassword.value = button.dataset.password;
                elements.loginPassword.focus();
            });
        });
    }

    function restoreSession() {
        try {
            var saved = JSON.parse(sessionStorage.getItem(SESSION_KEY));
            if(saved && saved.token && saved.role) {
                state.token = saved.token;
                state.username = saved.username;
                state.role = saved.role;
                state.organizationId = saved.organizationId;
                showDashboard();
                return;
            }
        }
        catch(error) {
            sessionStorage.removeItem(SESSION_KEY);
        }
        showLogin();
    }

    async function login(event) {
        event.preventDefault();
        setButtonLoading(elements.loginButton, true, "Signing in...");
        elements.loginError.classList.add("hidden");

        try {
            var response = await api("/api/auth/login", {
                method: "POST",
                body: {
                    username: elements.loginUsername.value.trim(),
                    password: elements.loginPassword.value
                },
                publicRequest: true
            });

            if(response.role !== "PLATFORM_ADMIN" && response.role !== "TENANT_ADMIN") {
                throw new Error("This panel supports platform and tenant administrators only.");
            }

            state.token = response.token;
            state.username = elements.loginUsername.value.trim();
            state.role = response.role;
            state.organizationId = response.organizationId;
            sessionStorage.setItem(SESSION_KEY, JSON.stringify({
                token: state.token,
                username: state.username,
                role: state.role,
                organizationId: state.organizationId
            }));
            elements.loginForm.reset();
            showDashboard();
        }
        catch(error) {
            elements.loginError.textContent = error.message;
            elements.loginError.classList.remove("hidden");
        }
        finally {
            setButtonLoading(elements.loginButton, false, "Sign in");
        }
    }

    function logout(showMessage) {
        sessionStorage.removeItem(SESSION_KEY);
        state.token = null;
        state.username = null;
        state.role = null;
        state.organizationId = null;
        state.organizations = [];
        state.routes = [];
        state.organization = null;
        state.audits = [];
        showLogin();
        if(showMessage !== false) {
            toast("Signed out", "Your browser session has been cleared.");
        }
    }

    function showLogin() {
        elements.dashboardView.classList.add("hidden");
        elements.loginView.classList.remove("hidden");
        setTimeout(function() { elements.loginUsername.focus(); }, 50);
    }

    function showDashboard() {
        elements.loginView.classList.add("hidden");
        elements.dashboardView.classList.remove("hidden");
        elements.sidebarUsername.textContent = state.username;
        elements.sidebarRole.textContent = humanRole(state.role);
        elements.userInitials.textContent = initials(state.username);
        elements.roleBadge.textContent = state.role;
        elements.breadcrumbRole.textContent = state.role === "PLATFORM_ADMIN" ? "PLATFORM" : "TENANT";
        buildNavigation();
        navigate("overview");
    }

    function buildNavigation() {
        var items = state.role === "PLATFORM_ADMIN" ? [
            { id: "overview", label: "Overview" },
            { id: "organizations", label: "Organizations" }
        ] : [
            { id: "overview", label: "Overview" },
            { id: "routes", label: "Routes" },
            { id: "organizationProfile", label: "My organization" },
            { id: "audit", label: "Audit records" }
        ];

        elements.mainNavigation.replaceChildren();
        items.forEach(function(item) {
            var button = document.createElement("button");
            button.type = "button";
            button.dataset.section = item.id;
            button.textContent = item.label;
            button.addEventListener("click", function() { navigate(item.id); });
            elements.mainNavigation.appendChild(button);
        });
    }

    async function navigate(section) {
        state.currentSection = section;
        var sectionMap = {
            overview: elements.overviewSection,
            organizations: elements.organizationsSection,
            routes: elements.routesSection,
            organizationProfile: elements.organizationProfileSection,
            audit: elements.auditSection
        };
        var titleMap = {
            overview: "Overview",
            organizations: "Organizations",
            routes: "Routes",
            organizationProfile: "My organization",
            audit: "Audit records"
        };

        Object.keys(sectionMap).forEach(function(key) {
            sectionMap[key].classList.toggle("hidden", key !== section);
        });
        elements.mainNavigation.querySelectorAll("button").forEach(function(button) {
            button.classList.toggle("active", button.dataset.section === section);
        });
        elements.pageTitle.textContent = titleMap[section];
        document.querySelector(".sidebar").classList.remove("open");

        try {
            if(section === "overview") {
                await loadOverview();
            }
            else if(section === "organizations") {
                await loadOrganizations();
            }
            else if(section === "routes") {
                await Promise.all([loadRoutes(), loadTenantOrganization(false)]);
            }
            else if(section === "organizationProfile") {
                await loadTenantOrganization();
            }
            else if(section === "audit") {
                await loadAudits();
            }
        }
        catch(error) {
            handlePageError(error);
        }
    }

    async function loadOverview() {
        elements.overviewSection.innerHTML = loadingBlock("Loading control-plane summary...");
        if(state.role === "PLATFORM_ADMIN") {
            await loadOrganizations(false);
            renderPlatformOverview();
        }
        else {
            await Promise.all([loadRoutes(false), loadTenantOrganization(false), loadAudits(false)]);
            renderTenantOverview();
        }
    }

    function renderPlatformOverview() {
        var active = countBy(state.organizations, "status", "ACTIVE");
        var pending = countBy(state.organizations, "status", "PENDING");
        var suspended = countBy(state.organizations, "status", "SUSPENDED");
        elements.overviewSection.innerHTML =
            '<div class="section-heading"><div><p class="eyebrow">PLATFORM OPERATIONS</p><h1>Control plane overview</h1>' +
            '<p class="muted">Tenant provisioning and platform-level access at a glance.</p></div></div>' +
            '<div class="stats-grid">' +
            statCard("Organizations", state.organizations.length, "TOTAL TENANTS") +
            statCard("Active", active, "READY TO CONFIGURE") +
            statCard("Awaiting action", pending + suspended, pending + " PENDING · " + suspended + " SUSPENDED") +
            '</div>' +
            '<div class="spotlight-card"><div><p class="eyebrow" style="color:var(--lime)">NEXT PLATFORM ACTION</p>' +
            '<h2>Provision a tenant without crossing its boundary.</h2>' +
            '<p>Create an organization, assign exactly one tenant administrator, then activate it. The tenant receives its own JWT-scoped workspace.</p></div>' +
            '<button class="button" type="button" id="overviewPlatformAction">Manage organizations →</button></div>';
        document.getElementById("overviewPlatformAction").addEventListener("click", function() { navigate("organizations"); });
    }

    function renderTenantOverview() {
        var active = state.routes.filter(function(route) { return route.active; }).length;
        var policies = state.routes.filter(function(route) {
            return route.requestsPerMinute || route.idempotencyEnabled || route.headerRules.length;
        }).length;
        var name = state.organization ? state.organization.name : "Your tenant";
        elements.overviewSection.innerHTML =
            '<div class="section-heading"><div><p class="eyebrow">TENANT CONTROL PLANE</p><h1>' + escapeHtml(name) + '</h1>' +
            '<p class="muted">Your organization\'s gateway configuration, isolated by the JWT tenant claim.</p></div></div>' +
            '<div class="stats-grid">' +
            statCard("Routes", state.routes.length, "TOTAL CONFIGURED") +
            statCard("Active", active, "AVAILABLE TO GATEWAY") +
            statCard("Policy-enabled", policies, state.audits.length + " AUDIT RECORDS") +
            '</div>' +
            '<div class="spotlight-card"><div><p class="eyebrow" style="color:var(--lime)">GATEWAY CONFIGURATION</p>' +
            '<h2>Shape traffic before it reaches your services.</h2>' +
            '<p>Define paths, destinations, methods, rate limits, idempotency and header transformations. Every mutation receives a durable version.</p></div>' +
            '<button class="button" type="button" id="overviewTenantAction">Manage routes →</button></div>';
        document.getElementById("overviewTenantAction").addEventListener("click", function() { navigate("routes"); });
    }

    async function loadOrganizations(render) {
        state.organizations = await api("/api/platform/organizations");
        if(render !== false) {
            renderOrganizations();
        }
    }

    function renderOrganizations() {
        var query = elements.organizationSearch.value.trim().toLowerCase();
        var organizations = state.organizations.filter(function(organization) {
            return !query || organization.name.toLowerCase().indexOf(query) >= 0 ||
                organization.slug.toLowerCase().indexOf(query) >= 0;
        });

        elements.organizationsTableBody.innerHTML = organizations.map(function(organization) {
            return '<tr>' +
                '<td><div class="cell-primary"><strong>' + escapeHtml(organization.name) + '</strong><small>ID ' + organization.id + '</small></div></td>' +
                '<td><span class="path-code">/' + escapeHtml(organization.slug) + '</span></td>' +
                '<td><select class="status-select" data-action="status" data-id="' + organization.id + '" aria-label="Status for ' + escapeAttribute(organization.name) + '">' +
                statusOptions(organization.status) + '</select></td>' +
                '<td class="cell-secondary">' + formatDate(organization.createdAt) + '</td>' +
                '<td class="align-right"><div class="row-actions">' +
                '<button class="row-action" data-action="admin" data-id="' + organization.id + '">Admin</button>' +
                '<button class="row-action" data-action="edit" data-id="' + organization.id + '">Edit</button>' +
                '<button class="row-action danger" data-action="delete" data-id="' + organization.id + '">Delete</button>' +
                '</div></td></tr>';
        }).join("");

        elements.organizationsEmpty.classList.toggle("hidden", organizations.length !== 0);
        elements.organizationsTableBody.closest(".table-scroll").classList.toggle("hidden", organizations.length === 0);
    }

    function handleOrganizationAction(event) {
        var button = event.target.closest("button[data-action]");
        if(!button) {
            return;
        }
        var organization = findById(state.organizations, button.dataset.id);
        if(!organization) {
            return;
        }
        if(button.dataset.action === "edit") {
            openOrganizationDialog(organization);
        }
        else if(button.dataset.action === "admin") {
            openTenantAdminDialog(organization);
        }
        else if(button.dataset.action === "delete") {
            deleteOrganization(organization);
        }
    }

    async function handleOrganizationStatus(event) {
        var select = event.target.closest('select[data-action="status"]');
        if(!select) {
            return;
        }
        var organization = findById(state.organizations, select.dataset.id);
        var oldStatus = organization.status;
        try {
            await api("/api/platform/organizations/" + organization.id + "/status", {
                method: "PATCH",
                body: { status: select.value }
            });
            organization.status = select.value;
            toast("Status updated", organization.name + " is now " + select.value.toLowerCase() + ".");
        }
        catch(error) {
            select.value = oldStatus;
            toast("Could not update status", error.message, true);
        }
    }

    function openOrganizationDialog(organization) {
        elements.organizationForm.reset();
        elements.organizationId.value = organization ? organization.id : "";
        elements.organizationName.value = organization ? organization.name : "";
        elements.organizationSlug.value = organization ? organization.slug : "";
        elements.organizationDialogTitle.textContent = organization ? "Edit organization" : "New organization";
        elements.organizationDialog.showModal();
        setTimeout(function() { elements.organizationName.focus(); }, 30);
    }

    async function saveOrganization(event) {
        event.preventDefault();
        var id = elements.organizationId.value;
        var submit = event.submitter;
        setButtonLoading(submit, true, "Saving...");
        try {
            await api("/api/platform/organizations" + (id ? "/" + id : ""), {
                method: id ? "PUT" : "POST",
                body: {
                    name: elements.organizationName.value.trim(),
                    slug: elements.organizationSlug.value.trim()
                }
            });
            elements.organizationDialog.close();
            await loadOrganizations();
            toast(id ? "Organization updated" : "Organization created",
                elements.organizationName.value.trim() + " has been saved.");
        }
        catch(error) {
            toast("Could not save organization", error.message, true);
        }
        finally {
            setButtonLoading(submit, false, "Save organization");
        }
    }

    async function deleteOrganization(organization) {
        if(!window.confirm("Soft-delete " + organization.name + "? Its tenant administrator will no longer be able to log in.")) {
            return;
        }
        try {
            await api("/api/platform/organizations/" + organization.id, { method: "DELETE" });
            await loadOrganizations();
            toast("Organization deleted", organization.name + " was soft-deleted.");
        }
        catch(error) {
            toast("Could not delete organization", error.message, true);
        }
    }

    async function openTenantAdminDialog(organization) {
        elements.tenantAdminForm.reset();
        elements.tenantAdminOrganizationId.value = organization.id;
        elements.tenantAdminOrganization.textContent = organization.name + " · /" + organization.slug;
        elements.tenantAdminDialogTitle.textContent = "Tenant administrator";
        elements.tenantAdminEnabled.checked = true;
        elements.tenantAdminExists.value = "false";
        elements.deleteTenantAdminButton.classList.add("hidden");
        elements.tenantAdminDialog.showModal();

        try {
            var admin = await api("/api/platform/organizations/" + organization.id + "/tenant-admin", {
                allow404: true
            });
            if(admin) {
                elements.tenantAdminExists.value = "true";
                elements.tenantAdminUsername.value = admin.username;
                elements.tenantAdminEnabled.checked = admin.enabled;
                elements.deleteTenantAdminButton.classList.remove("hidden");
                elements.tenantAdminPassword.placeholder = "Enter a new password";
            }
            else {
                elements.tenantAdminPassword.placeholder = "Minimum 8 characters";
            }
            elements.tenantAdminUsername.focus();
        }
        catch(error) {
            elements.tenantAdminDialog.close();
            toast("Could not load administrator", error.message, true);
        }
    }

    async function saveTenantAdmin(event) {
        event.preventDefault();
        var organizationId = elements.tenantAdminOrganizationId.value;
        var exists = elements.tenantAdminExists.value === "true";
        var submit = event.submitter;
        setButtonLoading(submit, true, "Saving...");
        try {
            await api("/api/platform/organizations/" + organizationId + "/tenant-admin", {
                method: exists ? "PUT" : "POST",
                body: {
                    username: elements.tenantAdminUsername.value.trim(),
                    password: elements.tenantAdminPassword.value,
                    enabled: elements.tenantAdminEnabled.checked
                }
            });
            elements.tenantAdminDialog.close();
            toast(exists ? "Administrator updated" : "Administrator created",
                elements.tenantAdminUsername.value.trim() + " can use the tenant panel.");
        }
        catch(error) {
            toast("Could not save administrator", error.message, true);
        }
        finally {
            setButtonLoading(submit, false, "Save administrator");
        }
    }

    async function deleteTenantAdmin() {
        var organizationId = elements.tenantAdminOrganizationId.value;
        if(!window.confirm("Delete this tenant administrator? This cannot be undone.")) {
            return;
        }
        try {
            await api("/api/platform/organizations/" + organizationId + "/tenant-admin", { method: "DELETE" });
            elements.tenantAdminDialog.close();
            toast("Administrator deleted", "The management account has been removed.");
        }
        catch(error) {
            toast("Could not delete administrator", error.message, true);
        }
    }

    async function loadRoutes(render) {
        state.routes = await api("/api/tenant/routes");
        if(render !== false) {
            renderRoutes();
        }
    }

    function renderRoutes() {
        var query = elements.routeSearch.value.trim().toLowerCase();
        var routes = state.routes.filter(function(route) {
            return !query || route.routeId.toLowerCase().indexOf(query) >= 0 ||
                route.name.toLowerCase().indexOf(query) >= 0 || route.pathPattern.toLowerCase().indexOf(query) >= 0;
        });

        elements.routesTableBody.innerHTML = routes.map(function(route) {
            var tags = [];
            if(route.requestsPerMinute) { tags.push(route.requestsPerMinute + "/min"); }
            if(route.idempotencyEnabled) { tags.push("Idempotent"); }
            if(route.headerRules.length) { tags.push(route.headerRules.length + " headers"); }
            if(!tags.length) { tags.push("Base routing"); }
            return '<tr>' +
                '<td><div class="cell-primary"><strong>' + escapeHtml(route.name) + '</strong><small>' + escapeHtml(route.routeId) + ' · ' + escapeHtml(route.allowedMethods.join(", ")) + '</small></div></td>' +
                '<td><span class="path-code" title="' + escapeAttribute(route.pathPattern) + '">' + escapeHtml(route.pathPattern) + '</span></td>' +
                '<td><div class="policy-tags">' + tags.map(function(tag) { return '<span class="policy-tag">' + escapeHtml(tag) + '</span>'; }).join("") + '</div></td>' +
                '<td><span class="status ' + (route.active ? "status-active" : "status-inactive") + '">' + (route.active ? "ACTIVE" : "INACTIVE") + '</span></td>' +
                '<td class="cell-secondary">v' + route.configurationVersion + '</td>' +
                '<td class="align-right"><div class="row-actions">' +
                '<button class="row-action" data-action="versions" data-id="' + route.id + '">Versions</button>' +
                '<button class="row-action" data-action="toggle" data-id="' + route.id + '">' + (route.active ? "Deactivate" : "Activate") + '</button>' +
                '<button class="row-action" data-action="edit" data-id="' + route.id + '">Edit</button>' +
                '<button class="row-action danger" data-action="delete" data-id="' + route.id + '">Delete</button>' +
                '</div></td></tr>';
        }).join("");

        elements.routesEmpty.classList.toggle("hidden", routes.length !== 0);
        elements.routesTableBody.closest(".table-scroll").classList.toggle("hidden", routes.length === 0);
    }

    function handleRouteAction(event) {
        var button = event.target.closest("button[data-action]");
        if(!button) {
            return;
        }
        var route = findById(state.routes, button.dataset.id);
        if(!route) {
            return;
        }
        if(button.dataset.action === "edit") {
            openRouteDialog(route);
        }
        else if(button.dataset.action === "toggle") {
            toggleRoute(route);
        }
        else if(button.dataset.action === "versions") {
            showVersions(route);
        }
        else if(button.dataset.action === "delete") {
            deleteRoute(route);
        }
    }

    function openRouteDialog(route) {
        elements.routeForm.reset();
        elements.headerRulesContainer.replaceChildren();
        elements.routePk.value = route ? route.id : "";
        elements.routeDialogTitle.textContent = route ? "Edit route" : "New route";
        elements.routeId.value = route ? route.routeId : "";
        elements.routeId.disabled = Boolean(route);
        elements.routeName.value = route ? route.name : "";
        elements.routePathPattern.value = route ? route.pathPattern : defaultRoutePath();
        elements.routeTargetBaseUrl.value = route ? route.targetBaseUrl : "";
        elements.routeTargetPathPrefix.value = route ? route.targetPathPrefix : "/";
        elements.routeRequestsPerMinute.value = route && route.requestsPerMinute ? route.requestsPerMinute : "";
        elements.routeResponseTimeout.value = route ? route.responseTimeoutMs : 3000;
        elements.routeAllowedMethods.value = route ? route.allowedMethods.join(", ") : "GET";
        elements.routeIdempotency.checked = route ? route.idempotencyEnabled : false;
        elements.routeActive.checked = route ? route.active : false;
        if(route) {
            route.headerRules.forEach(addHeaderRule);
        }
        elements.routeDialog.showModal();
        setTimeout(function() { (route ? elements.routeName : elements.routeId).focus(); }, 30);
    }

    function addHeaderRule(rule) {
        var row = document.createElement("div");
        row.className = "rule-row";
        row.innerHTML =
            '<select class="rule-direction" aria-label="Header direction"><option value="REQUEST">Request</option><option value="RESPONSE">Response</option></select>' +
            '<select class="rule-action" aria-label="Header action"><option value="ADD">Add</option><option value="REMOVE">Remove</option></select>' +
            '<input class="rule-name" placeholder="Header name" maxlength="200" aria-label="Header name" required>' +
            '<input class="rule-value" placeholder="Header value" maxlength="2000" aria-label="Header value">' +
            '<button class="icon-button remove-rule" type="button" aria-label="Remove header rule">×</button>';
        row.querySelector(".rule-direction").value = rule && rule.direction ? rule.direction : "REQUEST";
        row.querySelector(".rule-action").value = rule && rule.action ? rule.action : "ADD";
        row.querySelector(".rule-name").value = rule && rule.headerName ? rule.headerName : "";
        row.querySelector(".rule-value").value = rule && rule.headerValue ? rule.headerValue : "";
        elements.headerRulesContainer.appendChild(row);
    }

    function removeHeaderRule(event) {
        var button = event.target.closest(".remove-rule");
        if(button) {
            button.closest(".rule-row").remove();
        }
    }

    async function saveRoute(event) {
        event.preventDefault();
        var id = elements.routePk.value;
        var methods = elements.routeAllowedMethods.value.split(",").map(function(method) {
            return method.trim().toUpperCase();
        }).filter(Boolean);
        var headerRules = Array.prototype.map.call(elements.headerRulesContainer.querySelectorAll(".rule-row"), function(row) {
            var action = row.querySelector(".rule-action").value;
            return {
                direction: row.querySelector(".rule-direction").value,
                action: action,
                headerName: row.querySelector(".rule-name").value.trim(),
                headerValue: action === "REMOVE" ? null : row.querySelector(".rule-value").value.trim()
            };
        });
        var body = {
            routeId: elements.routeId.value.trim(),
            name: elements.routeName.value.trim(),
            pathPattern: elements.routePathPattern.value.trim(),
            targetBaseUrl: elements.routeTargetBaseUrl.value.trim(),
            targetPathPrefix: elements.routeTargetPathPrefix.value.trim(),
            requestsPerMinute: elements.routeRequestsPerMinute.value ? Number(elements.routeRequestsPerMinute.value) : null,
            responseTimeoutMs: Number(elements.routeResponseTimeout.value),
            idempotencyEnabled: elements.routeIdempotency.checked,
            active: elements.routeActive.checked,
            allowedMethods: methods,
            headerRules: headerRules
        };
        var submit = event.submitter;
        setButtonLoading(submit, true, "Saving...");
        try {
            await api("/api/tenant/routes" + (id ? "/" + id : ""), {
                method: id ? "PUT" : "POST",
                body: body
            });
            elements.routeDialog.close();
            await loadRoutes();
            toast(id ? "Route updated" : "Route created", body.routeId + " was saved as a new configuration version.");
        }
        catch(error) {
            toast("Could not save route", error.message, true);
        }
        finally {
            setButtonLoading(submit, false, "Save route");
        }
    }

    async function toggleRoute(route) {
        try {
            await api("/api/tenant/routes/" + route.id + "/status", {
                method: "PATCH",
                body: { active: !route.active }
            });
            await loadRoutes();
            toast(route.active ? "Route deactivated" : "Route activated",
                route.routeId + " received a new configuration version.");
        }
        catch(error) {
            toast("Could not change route status", error.message, true);
        }
    }

    async function deleteRoute(route) {
        if(!window.confirm("Delete route " + route.routeId + "? Its configuration history will remain available.")) {
            return;
        }
        try {
            await api("/api/tenant/routes/" + route.id, { method: "DELETE" });
            await loadRoutes();
            toast("Route deleted", route.routeId + " was removed; its version history was retained.");
        }
        catch(error) {
            toast("Could not delete route", error.message, true);
        }
    }

    async function showVersions(route) {
        elements.versionsDialogTitle.textContent = route.routeId + " versions";
        elements.versionsTimeline.innerHTML = loadingBlock("Loading route history...");
        elements.versionsDialog.showModal();
        try {
            var versions = await api("/api/tenant/route-versions/" + encodeURIComponent(route.routeId));
            if(!versions.length) {
                elements.versionsTimeline.innerHTML = '<div class="empty-state"><strong>No versions found</strong><span>This route has no history yet.</span></div>';
                return;
            }
            elements.versionsTimeline.innerHTML = versions.map(function(version) {
                return '<article class="timeline-item"><div><h3>Version ' + version.versionNumber + ' · ' + escapeHtml(version.changeType) + '</h3>' +
                    '<p>' + formatDateTime(version.createdAt) + '</p><details><summary>View JSON snapshot</summary><pre>' +
                    escapeHtml(prettyJson(version.configurationSnapshot)) + '</pre></details></div>' +
                    '<span class="status status-active">v' + version.versionNumber + '</span></article>';
            }).join("");
        }
        catch(error) {
            elements.versionsDialog.close();
            toast("Could not load versions", error.message, true);
        }
    }

    async function loadTenantOrganization(render) {
        state.organization = await api("/api/tenant/organization");
        if(render !== false) {
            renderTenantOrganization();
        }
    }

    function renderTenantOrganization() {
        var organization = state.organization;
        elements.tenantOrganizationCard.innerHTML =
            '<article class="profile-card"><p class="eyebrow">TENANT IDENTITY</p><h2>' + escapeHtml(organization.name) + '</h2>' +
            '<p class="muted">/' + escapeHtml(organization.slug) + '</p><div class="profile-details">' +
            '<div><span>ORGANIZATION ID</span><strong>' + organization.id + '</strong></div>' +
            '<div><span>STATUS</span><strong>' + escapeHtml(organization.status) + '</strong></div>' +
            '<div><span>CREATED</span><strong>' + formatDate(organization.createdAt) + '</strong></div>' +
            '<div><span>LAST UPDATED</span><strong>' + formatDate(organization.updatedAt) + '</strong></div>' +
            '</div></article>' +
            '<form id="tenantOrganizationForm" class="profile-card profile-edit"><div><h3>Update display name</h3>' +
            '<p>The URL slug and lifecycle status remain platform-controlled.</p></div>' +
            '<label class="field"><span>Organization name</span><input id="tenantOrganizationName" maxlength="150" value="' + escapeAttribute(organization.name) + '" required></label>' +
            '<button class="button" type="submit">Save name</button></form>';
        document.getElementById("tenantOrganizationForm").addEventListener("submit", saveTenantOrganization);
    }

    async function saveTenantOrganization(event) {
        event.preventDefault();
        var submit = event.submitter;
        var name = document.getElementById("tenantOrganizationName").value.trim();
        setButtonLoading(submit, true, "Saving...");
        try {
            await api("/api/tenant/organization", { method: "PUT", body: { name: name } });
            await loadTenantOrganization();
            toast("Organization updated", "The display name is now " + name + ".");
        }
        catch(error) {
            toast("Could not update organization", error.message, true);
        }
        finally {
            setButtonLoading(submit, false, "Save name");
        }
    }

    async function loadAudits(render) {
        state.audits = await api("/api/tenant/audit-records");
        if(render !== false) {
            renderAudits();
        }
    }

    function renderAudits() {
        elements.auditTableBody.innerHTML = state.audits.map(function(audit) {
            return '<tr><td><div class="cell-primary"><strong>' + escapeHtml(audit.action) + '</strong><small>' + audit.durationMs + ' ms</small></div></td>' +
                '<td><div class="cell-primary"><strong>' + escapeHtml(audit.resourceType) + '</strong><small>' + escapeHtml(audit.resourceId) + '</small></div></td>' +
                '<td>' + escapeHtml(audit.actor) + '</td><td><span class="status status-active">' + escapeHtml(audit.result) + '</span></td>' +
                '<td class="cell-secondary">' + escapeHtml(audit.correlationId || "—") + '</td>' +
                '<td class="cell-secondary">' + formatDateTime(audit.createdAt) + '</td></tr>';
        }).join("");
        elements.auditEmpty.classList.toggle("hidden", state.audits.length !== 0);
        elements.auditTableBody.closest(".table-scroll").classList.toggle("hidden", state.audits.length === 0);
    }

    async function api(path, options) {
        options = options || {};
        var method = options.method || "GET";
        var headers = { "Accept": "application/json" };
        if(options.body !== undefined) {
            headers["Content-Type"] = "application/json";
        }
        if(!options.publicRequest && state.token) {
            headers.Authorization = "Bearer " + state.token;
        }
        if(method !== "GET") {
            headers["X-Correlation-ID"] = correlationId();
        }

        var response = await fetch(path, {
            method: method,
            headers: headers,
            body: options.body === undefined ? undefined : JSON.stringify(options.body)
        });

        if(response.status === 401 && !options.publicRequest) {
            logout(false);
            throw new Error("Your session expired. Please sign in again.");
        }
        if(response.status === 404 && options.allow404) {
            return null;
        }

        var responseBody = null;
        var contentType = response.headers.get("content-type") || "";
        if(contentType.indexOf("application/json") >= 0) {
            responseBody = await response.json();
        }
        if(!response.ok) {
            var message = responseBody && responseBody.message ? responseBody.message : "Request failed with status " + response.status;
            if(responseBody && responseBody.errors) {
                var details = Object.keys(responseBody.errors).map(function(key) {
                    return key + ": " + responseBody.errors[key];
                }).join(" · ");
                if(details) {
                    message += " — " + details;
                }
            }
            throw new Error(message);
        }
        return responseBody;
    }

    function statusOptions(current) {
        return ["PENDING", "ACTIVE", "SUSPENDED"].map(function(status) {
            return '<option value="' + status + '"' + (status === current ? " selected" : "") + '>' + status + '</option>';
        }).join("");
    }

    function statCard(label, value, detail) {
        return '<article class="stat-card"><span>' + escapeHtml(label) + '</span><strong>' + value + '</strong><small>' + escapeHtml(detail) + '</small></article>';
    }

    function loadingBlock(message) {
        return '<div class="empty-state"><strong>Loading</strong><span>' + escapeHtml(message) + '</span></div>';
    }

    function setButtonLoading(button, loading, text) {
        if(!button) {
            return;
        }
        if(!button.dataset.originalHtml) {
            button.dataset.originalHtml = button.innerHTML;
        }
        button.disabled = loading;
        button.textContent = loading ? text : "";
        if(!loading) {
            button.innerHTML = button.dataset.originalHtml;
        }
    }

    function togglePassword() {
        var showing = elements.loginPassword.type === "text";
        elements.loginPassword.type = showing ? "password" : "text";
        elements.togglePassword.textContent = showing ? "Show" : "Hide";
    }

    function toggleMobileMenu() {
        document.querySelector(".sidebar").classList.toggle("open");
    }

    function defaultRoutePath() {
        if(state.organization && state.organization.slug) {
            return "/" + state.organization.slug + "/";
        }
        return "/";
    }

    function findById(list, id) {
        return list.find(function(item) { return String(item.id) === String(id); });
    }

    function countBy(list, property, value) {
        return list.filter(function(item) { return item[property] === value; }).length;
    }

    function initials(value) {
        return (value || "GK").split(/[-_.\s]+/).map(function(part) { return part.charAt(0); }).join("").slice(0, 2).toUpperCase();
    }

    function humanRole(role) {
        return role === "PLATFORM_ADMIN" ? "Platform admin" : "Tenant admin";
    }

    function formatDate(value) {
        if(!value) { return "—"; }
        return new Intl.DateTimeFormat(undefined, { year: "numeric", month: "short", day: "numeric" }).format(new Date(value));
    }

    function formatDateTime(value) {
        if(!value) { return "—"; }
        return new Intl.DateTimeFormat(undefined, {
            year: "numeric", month: "short", day: "numeric", hour: "2-digit", minute: "2-digit"
        }).format(new Date(value));
    }

    function prettyJson(value) {
        try {
            return JSON.stringify(JSON.parse(value), null, 2);
        }
        catch(error) {
            return value;
        }
    }

    function correlationId() {
        if(window.crypto && window.crypto.randomUUID) {
            return window.crypto.randomUUID();
        }
        return "ui-" + Date.now() + "-" + Math.random().toString(16).slice(2);
    }

    function toast(title, message, error) {
        var element = document.createElement("div");
        element.className = "toast" + (error ? " error" : "");
        var content = document.createElement("div");
        var strong = document.createElement("strong");
        var span = document.createElement("span");
        strong.textContent = title;
        span.textContent = message;
        content.appendChild(strong);
        content.appendChild(span);
        element.appendChild(content);
        elements.toastContainer.appendChild(element);
        setTimeout(function() { element.remove(); }, 4200);
    }

    function handlePageError(error) {
        toast("Could not load this page", error.message, true);
    }

    function escapeHtml(value) {
        return String(value === null || value === undefined ? "" : value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function escapeAttribute(value) {
        return escapeHtml(value).replace(/`/g, "&#096;");
    }
})();
