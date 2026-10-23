CREATE TABLE organizations (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               name VARCHAR(150) NOT NULL,
                               slug VARCHAR(100) NOT NULL,
                               status VARCHAR(20) NOT NULL,
                               created_at DATETIME(6) NOT NULL,
                               updated_at DATETIME(6) NOT NULL,
                               deleted_at DATETIME(6),
                               CONSTRAINT pk_organizations PRIMARY KEY (id),
                               CONSTRAINT uk_organizations_name UNIQUE (name),
                               CONSTRAINT uk_organizations_slug UNIQUE (slug)
);

CREATE TABLE roles (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL,
                       CONSTRAINT pk_roles PRIMARY KEY (id),
                       CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE management_users (
                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                  organization_id BIGINT,
                                  role_id BIGINT NOT NULL,
                                  username VARCHAR(100) NOT NULL,
                                  password_hash VARCHAR(255) NOT NULL,
                                  enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at DATETIME(6) NOT NULL,
                                  updated_at DATETIME(6) NOT NULL,
                                  CONSTRAINT pk_management_users PRIMARY KEY (id),
                                  CONSTRAINT uk_management_users_username UNIQUE (username),
                                  CONSTRAINT uk_management_users_organization UNIQUE (organization_id),
                                  CONSTRAINT fk_management_users_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
                                  CONSTRAINT fk_management_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE api_routes (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            organization_id BIGINT NOT NULL,
                            route_id VARCHAR(100) NOT NULL,
                            name VARCHAR(200) NOT NULL,
                            path_pattern VARCHAR(500) NOT NULL,
                            target_base_url VARCHAR(1000) NOT NULL,
                            target_path_prefix VARCHAR(500) NOT NULL,
                            requests_per_minute INTEGER,
                            response_timeout_ms INTEGER NOT NULL,
                            idempotency_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                            active BOOLEAN NOT NULL DEFAULT FALSE,
                            configuration_version BIGINT NOT NULL,
                            created_at DATETIME(6) NOT NULL,
                            updated_at DATETIME(6) NOT NULL,
                            CONSTRAINT pk_api_routes PRIMARY KEY (id),
                            CONSTRAINT uk_api_routes_tenant_route UNIQUE (organization_id, route_id),
                            CONSTRAINT fk_api_routes_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE TABLE route_allowed_methods (
                                       route_pk BIGINT NOT NULL,
                                       http_method VARCHAR(10) NOT NULL,
                                       CONSTRAINT pk_route_allowed_methods PRIMARY KEY (route_pk, http_method),
                                       CONSTRAINT fk_route_allowed_methods_route FOREIGN KEY (route_pk) REFERENCES api_routes (id) ON DELETE CASCADE
);

CREATE TABLE route_header_rules (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    route_pk BIGINT NOT NULL,
                                    direction VARCHAR(20) NOT NULL,
                                    action VARCHAR(20) NOT NULL,
                                    header_name VARCHAR(200) NOT NULL,
                                    header_value VARCHAR(2000),
                                    created_at DATETIME(6) NOT NULL,
                                    updated_at DATETIME(6) NOT NULL,
                                    CONSTRAINT pk_route_header_rules PRIMARY KEY (id),
                                    CONSTRAINT fk_route_header_rules_route FOREIGN KEY (route_pk) REFERENCES api_routes (id) ON DELETE CASCADE
);

CREATE TABLE route_configuration_versions (
                                              id BIGINT NOT NULL AUTO_INCREMENT,
                                              organization_id BIGINT NOT NULL,
                                              route_id VARCHAR(100) NOT NULL,
                                              version_number BIGINT NOT NULL,
                                              change_type VARCHAR(30) NOT NULL,
                                              configuration_snapshot LONGTEXT NOT NULL,
                                              created_at DATETIME(6) NOT NULL,
                                              CONSTRAINT pk_route_configuration_versions PRIMARY KEY (id),
                                              CONSTRAINT uk_route_versions_tenant_version UNIQUE (organization_id, route_id, version_number),
                                              CONSTRAINT fk_route_versions_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE TABLE control_plane_audit_records (
                                             id BIGINT NOT NULL AUTO_INCREMENT,
                                             organization_id BIGINT,
                                             actor VARCHAR(100) NOT NULL,
                                             action VARCHAR(100) NOT NULL,
                                             resource_type VARCHAR(100) NOT NULL,
                                             resource_id VARCHAR(100) NOT NULL,
                                             correlation_id VARCHAR(100),
                                             duration_ms BIGINT NOT NULL,
                                             result VARCHAR(20) NOT NULL,
                                             details VARCHAR(2000),
                                             created_at DATETIME(6) NOT NULL,
                                             CONSTRAINT pk_control_plane_audit_records PRIMARY KEY (id),
                                             CONSTRAINT fk_audit_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);