# 🔐 ClashAccess — Security & Endpoint Metadata Starter

ClashAccess is a plug-and-play Spring Boot security starter built by ClashBuddy Studio, designed to unify access control and expose structured metadata across all ClashBuddy services — including applications, games, designers, agents, pay systems, and gateways.

## 🧩 ClashAccess vs Spring Security (When and Why)

### 🔄 Is there an equivalent to ClashAccess?

There is currently no direct open-source equivalent to ClashAccess that provides the same unified role and permission-based annotation system **combined with metadata exposure** and **gateway coordination** out of the box.

However, some alternatives include:

- **Spring Security ACL**: Fine-grained access control framework. Requires custom implementation and does not expose metadata.
- **OPA (Open Policy Agent)**: A powerful policy engine for services. Great for centralized control, but requires separate policy servers and learning Rego DSL.
- **Keycloak with Spring Boot adapters**: Manages roles and permissions centrally. Still lacks built-in endpoint scanning or metadata exposure like ClashAccess.

### ⚖️ Feature Comparison
| Feature                                 | Spring Security ACL / OPA / Keycloak         | ClashAccess                                      |
|----------------------------------------|-----------------------------------------------|--------------------------------------------------|
| Role/Permission Annotations            | Varies / Needs custom DSL                    | `@RequireAccess` for roles and permissions       |
| Dynamic Endpoint Metadata              | ❌ None                                       | ✅ Built-in and customizable                     |
| Auto Gateway Integration               | ❌ Manual                                     | ✅ Loads metadata at gateway startup             |
| Public Endpoint Bypass Detection       | ❌ Requires custom rules                      | ✅ Automatic via metadata flags                  |
| Easy Developer Hook for Metadata Use   | ❌ Not designed for Java-first integration    | ✅ Extend `ClashAccessMetadataAware`             |

### 🧪 Example: What ClashAccess Can Do

### 🔐 Secured Endpoint Example
```java
@RequireAccess(roles = "admin", permissions = "user.manage")
@GetMapping("/admin/users")
public List<User> getUsers(AuthorizedUser user) {
    return userService.findAll();
}
```

### 🟢 Public Endpoint Example (No Access Control)
```java
@GetMapping("/auth/login")
public TokenResponse login(@RequestBody LoginRequest request) {
    return authService.authenticate(request);
}
```

**Application-side:**
```java
@RequireAccess(roles = "admin", permissions = "user.manage")
@GetMapping("/admin/users")
public List<User> getUsers(AuthorizedUser user) { ... }
```

**Gateway-side:** Automatically receives this metadata and can skip auth filtering for public paths or enforce stricter access based on flags — no code duplication needed.

ClashAccess saves time, ensures consistency, and enables centralized visibility without tight coupling or external DSLs.

Spring Security is a powerful framework for managing authentication and authorization in Spring applications. However, in complex **microservice architectures**, it often introduces tightly coupled configurations and redundant endpoint rules across services.

ClashAccess is not a replacement for Spring Security — it's a **complementary layer built specifically for microservices**:

| Feature                     | Spring Security                             | ClashAccess                                             |
| --------------------------- | ------------------------------------------- | ------------------------------------------------------- |
| Target Architecture         | Monoliths & Microservices                   | **Microservices only**                                  |
| Endpoint Protection         | Filters, method security, roles/authorities | `@RequireAccess` for roles/permissions, metadata-driven |
| Gateway Coordination        | Manual                                      | **Automatic via metadata endpoints**                    |
| Metadata Exposure           | None                                        | **Built-in metadata endpoint**                          |
| Headers for Context         | Not exposed                                 | Injected headers (`x-ca-uid`, roles, permissions)       |
| Real-time Access Reflection | Needs custom integration                    | Role/permission cache and Redis-based refresh support   |

> ⚠️ **Why ClashAccess?**
>
> - Avoid rewriting access logic in every service
> - Prevent security misconfigurations using startup checks
> - Centralize access control logic while keeping services decoupled

Use ClashAccess when you want **secure, flexible, and maintainable access control** between distributed services — especially when working across different teams, environments, or game modules.

📌 You don't need to **replace** Spring Security — you enhance it. Let Spring handle authentication, and let ClashAccess coordinate role/permission enforcement and metadata exposure across your system.

## 🚀 What It Does

> ⚠️ **Note:** This version of ClashAccess is designed specifically for microservice architectures. It is not suitable for monolithic applications out of the box. In monolithic setups, you must implement a custom Spring Boot filter to inject the required security headers (`x-ca-uid`, `x-ca-urs`, `x-ca-ups`) into each request manually.

ClashAccess simplifies and enforces access security across your distributed system with:

- ✅ Annotation-based role & permission security (`@RequireAccess`)
- ✅ Dynamic metadata exposure for API Gateways
- ✅ Unified access validation across services
- ✅ No controller code needed to expose public/protected endpoints
- ✅ Ready-to-use for both **application services** and **API gateways**

---

## 🌟 Use Case

### 🔹 For Application Services

- Secure endpoints using roles & permissions
- Automatically expose metadata about public/protected routes for API Gateways

### 🔹 For API Gateway

- Load public route metadata from other services
- Skip authorization checks for public endpoints
- Centralize access control without tight coupling

---

## 🔧 Quick Setup

### 1. Add Dependency

```groovy
implementation 'studio.clashbuddy:spring-boot-starter-clashaccess:1.0.0'
```

```xml
<dependency>
  <groupId>studio.clashbuddy</groupId>
  <artifactId>spring-boot-starter-clashaccess</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

### 2. Configuration Reference

#### 📂 For Application Microservices

This configuration enables scanning and secure metadata exposure for your application microservices:

> 🟢 If `clashbuddy.clashaccess.application.enabled = true`, the application is considered **ready to expose** metadata via the specified endpoint and protect it using the defined access key.

```yaml
clashbuddy:
  clashaccess:
    service-type: application
    application:
      scan: true            # Scans all available controller endpoints; default is true
      enabled: true         # Enables automatic metadata exposure; default is true
      access:
        key: your-secure-key       # ⚠️ WARNING: Default is "access". Change to protect metadata.
        endpoint: /get-public-endpoints  # ⚠️ WARNING: Default is "/clashbuddy-clash-access/endpoint-metadata"
```

#### 📂 For API Gateway

```yaml
clashbuddy:
  clashaccess:
    service-type: gateway
    gateway:
      access-type: public     # Options: "public" (default), "private", "both"
      accesses:
        - endpoint: http://identity-service/get-public-endpoints
          key: your-identity-key
        - endpoint: http://payment-service/get-public-endpoints
          key: your-pay-key
```

> 🔎 `access-type` tells the gateway what kind of metadata to load: public, private, or both.

---

## ⚠️ Metadata Security Warning

Avoid using the default metadata endpoint and key:

- Default endpoint: `/clashbuddy-clash-access/endpoint-metadata`
- Default key: `access`

If you do not change these defaults, **any service with knowledge of the endpoint and default key can retrieve sensitive metadata**, including:

- Full controller class names
- Method names and signatures
- Endpoint paths and HTTP methods
- Role and permission requirements

This could expose your internal system structure and security rules to unauthorized third parties.

### 🔐 Example of a Secured Endpoint in Metadata
```json
{
  "httpMethod": "GET",
  "endpoints": ["/admin/users"],
  "basePath": "/api",
  "controller": "AdminController",
  "fullControllerName": "com.example.api.AdminController",
  "method": "getUsers",
  "roles": ["admin"],
  "permissions": ["user.manage"],
  "public": false,
  "mainEndpoint": "/api/admin/users"
}
```

### 🟢 Example of a Public (Not Secured) Endpoint
```json
{
  "httpMethod": "POST",
  "endpoints": ["/auth/login"],
  "basePath": "/api",
  "controller": "AuthController",
  "fullControllerName": "com.example.auth.AuthController",
  "method": "login",
  "roles": [],
  "permissions": [],
  "public": true,
  "mainEndpoint": "/api/auth/login"
}
```

**Recommendations:**

- Use a unique `key`
- Customize the `endpoint` path

- Use a unique `key`
- Customize the `endpoint` path

---

## ❗️ Developer Integration

> ✅ Developers can extend `ClashAccessMetadataAware` to access metadata:

```java
@Component
public class PermissionSyncMetadataHandler extends ClashAccessMetadataAware {
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    protected void onMetadataReady(Set<ClashScannedEndpointMetadata> metadata) {
        List<PermissionEntity> permissions = metadata.stream()
            .flatMap(m -> m.getPermissions().stream())
            .distinct()
            .map(PermissionEntity::new)
            .toList();

        permissionRepository.saveAll(permissions);
    }
}
```

---

## 🧠 How It Works Internally

### ✅ Application Services

- Scan your controllers
- Detect `@RequireAccess` annotations
- Expose secure endpoint metadata

### ✅ API Gateway

- Load metadata from registered services
- Register public endpoints
- Skip auth filter if path is public

---

## 🗓️ Example Metadata Output

When hitting `/get-public-endpoints?key=...`:

> 🟢 If no `@RequireAccess` is present, the endpoint is **public** by default.

```json
[
  {
    "httpMethod": "POST",
    "endpoints": ["/auth/login"],
    "basePath": "/api",
    "contextPath": "/",
    "controller": "TestController",
    "fullControllerName": "com.example.auth.AuthenticationController",
    "method": "login",
    "roles": [],
    "permissions": [],
    "public": true,
    "mainEndpoint": "/api/auth/login"
  }
]
```

---

## ⚙️ Advanced Features

- 🧩 `@RequireAccess` supports `excludedRoles`, `excludedPermissions`
- 🕯️ Validates misconfigurations at startup
- 📆 Metadata served from library endpoint
- 🔄 Flexible key and path setup

---

## 📊 Logging Example

```
📅 ClashAccess Initialization Summary:
   🔧 Mode:              APPLICATION
   🔍 Endpoint Scan:     true
   ✅ Module Enabled:     true
   📦 Ready to process metadata endpoints.
```

---

## 🔒 Best Practices

- Use a secure API key in production
- Never rely on client-supplied headers
- Ensure endpoint paths are normalized
- Maintain unique base paths per service

---

## 👨‍💼 Contributing

PRs and feedback welcome. Fork it to fit your needs.

---

## 🏋️ Built by ClashBuddy Studio

ClashAccess was developed to serve the unique access control and metadata needs of real-time, modular, and secure game infrastructure.


Visit: [clashbuddy.studio](https://clashbuddy.studio)

---

📢 Contact us: [dev@clashbuddy.studio](mailto\:dev@clashbuddy.studio)

---

## 🎯 Conclusion

ClashAccess empowers your microservice ecosystem with seamless, scalable security — without the usual overhead. Whether you're building games, payment processors, or content platforms, this starter gives your services the clarity and control they need to enforce access policies and expose routes with confidence.

Adopt ClashAccess to:

- Secure endpoints with clean annotations
- Automate metadata discovery
- Coordinate access between services and your API Gateway effortlessly

Your architecture stays clean. Your security stays strong. Your developers stay productive.

Welcome to the future of service-level access control — built for ClashBuddy, ready for you.

