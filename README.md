<div align="center">

# MarketHive

### Your Gateway to a Multi-Vendor Shopping Experience

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=for-the-badge&logo=lombok&logoColor=white)](https://projectlombok.org/)

Connecting customers with verified vendors through a secure, scalable, and fully role-based e-commerce REST API.
<img width="2170" height="725" alt="image" src="https://github.com/user-attachments/assets/e58e359c-6583-421f-93c0-c9ece5ee0919" />
</div>

---

##  Table of Contents

- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Design Patterns & SOLID Principles](#-design-patterns--solid-principles)
- [Built With](#-built-with)
- [Project Structure](#-project-structure)
- [Database Design](#-database-design)
- [User Roles & Permissions](#-user-roles--permissions)
- [API Documentation](#-api-documentation)
- [Logging System](#-logging-system)
- [Getting Started](#-getting-started)
- [Environment Configuration](#-environment-configuration)
- [Postman Collection](#-postman-collection)
- [Seed Data Credentials](#-seed-data-credentials)
- [Development Team](#-development-team)

---

##  Project Overview

**MarketHive** is a production-ready backend REST API for a **Multi-Vendor E-Commerce System**. It allows vendors to register their own storefronts, list and manage products, and fulfill customer orders — all under the supervision of a powerful admin layer.

The platform enforces a strict **vendor approval workflow** — no vendor can start selling until an admin explicitly approves their account. Customers can browse, search, add to cart, place orders, pay, and leave reviews — all secured with **JWT authentication** and **role-based access control**.

The project is built following **Clean Architecture**, **SOLID Principles**, and modern Spring Boot best practices including layered separation of concerns, stateless security, DTO-based communication, and structured file logging.

<!-- ADD PROJECT SCREENSHOT / BANNER HERE -->

---

##  Key Features

### For Customers (User Role)
- **Browse & Search** — Explore all active products, filter by category or market, search by keyword
- **Smart Cart** — Add items, update quantities, remove items — cart persists in the database
- **Order Management** — Place orders from cart, view order history, cancel pending orders
- **Secure Payments** — Pay per order with credit card or PayPal, view payment status
- **Reviews & Ratings** — Leave one review per product, update or delete own reviews
- **Profile Management** — View and manage personal account profile

###  For Vendors (Market Role)
- **Storefront Management** — Create and manage your own market after admin approval
- **Product Management** — Add, update, delete products — toggle visibility at any time
- **Stock Control** — Stock is automatically deducted on orders and restored on cancellation
- **Ownership Enforcement** — Vendors can only manage their own markets and products

### For Administrators (Admin Role)
- **Vendor Approval** — Approve or reject vendor account registrations
- **User Management** — View all users, activate or deactivate accounts
- **Market Oversight** — View and control all markets on the platform
- **Order Control** — Update order statuses across the entire platform
- **Payment Management** — View all payments, issue refunds
- **Category Management** — Create and manage the hierarchical product category tree

---

## System Architecture

MarketHive follows a **Layered Architecture** combined with **RESTful API** design principles for clean separation of concerns and long-term scalability.

- **Controller Layer** — Handles HTTP requests, validates input, delegates to services, returns standardized `ApiResponse<T>` wrappers
- **Service Layer** — All business logic lives here. Each service handles exactly one domain, following Single Responsibility
- **Repository Layer** — Spring Data JPA repositories handle all DB interaction using method naming conventions and JPQL — no raw SQL
- **Entity Layer** — JPA entities map to MySQL tables. `User` implements `UserDetails` for Spring Security integration
- **Security Layer** — Stateless JWT auth. Every request passes through `JwtAuthFilter` before reaching any controller

```
Client Request
      │
      ▼
 JwtAuthFilter ──── validates token ────► SecurityContext
      │
      ▼
 Controller ──── delegates ────► Service ──── queries ────► Repository ──── MySQL
      │                              │
      │                        Business Logic
      │                     (validation, ownership, rules)
      ▼
 ApiResponse<T> ──── returned as JSON ────► Client
```

---

## Design Patterns & SOLID Principles

### Design Patterns

- **Repository Pattern** — All DB access goes through Spring Data JPA repositories. Services never touch the database directly
- **DTO Pattern** — Entities are never exposed directly. Request DTOs validate input; Response DTOs control output — the password hash never leaves the server
- **Builder Pattern** — Lombok `@Builder` constructs complex objects like `User`, `Order`, and `AuthResponse` cleanly and safely
- **Filter Chain Pattern** — Spring Security filter chain intercepts every request before it reaches a controller
- **Singleton** — All `@Service` and `@Repository` beans are singletons managed by the Spring IoC container

### SOLID Principles

| Principle | Implementation | Example |
|-----------|---------------|---------|
| **S – Single Responsibility** | Each service handles exactly one domain | `CartServiceImpl` manages only cart logic |
| **O – Open/Closed** | New features added via new classes, not modifying existing ones | Adding a new payment method doesn't change `OrderService` |
| **L – Liskov Substitution** | All service implementations are interchangeable via their interface | Any class using `UserService` can swap implementations without breaking |
| **I – Interface Segregation** | Each interface is focused and small | `CartService` contains only cart methods — not mixed with order logic |
| **D – Dependency Inversion** | Controllers depend on interfaces, not concrete classes | `OrderController` injects `OrderService` interface, not `OrderServiceImpl` |

```java
// Example: Dependency Inversion + Single Responsibility
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService; // depends on abstraction, not implementation

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
            ApiResponse.success("Order placed", orderService.placeOrder(request, currentUser.getId()))
        );
    }
}
```

This ensures **maintainability, testability, and scalability** across the entire project.

---

## 🛠️ Built With

### Core Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 17 | Primary programming language |
| Spring Boot | 3.2.4 | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | Database ORM & repositories |
| Hibernate | 6.x | JPA implementation |
| MySQL | 8.0+ | Relational database engine |

### Libraries & Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| `spring-boot-starter-web` | 3.2.4 | REST API, HTTP handling, embedded Tomcat |
| `spring-boot-starter-data-jpa` | 3.2.4 | ORM, repositories, database abstraction |
| `spring-boot-starter-security` | 3.2.4 | Authentication, authorization, filter chain |
| `spring-boot-starter-validation` | 3.2.4 | Request body validation (`@NotBlank`, `@Email`, `@Min`) |
| `mysql-connector-j` | latest | MySQL JDBC driver |
| `jjwt-api` | 0.11.5 | JWT token creation and parsing API |
| `jjwt-impl` | 0.11.5 | JWT implementation (runtime) |
| `jjwt-jackson` | 0.11.5 | JWT JSON serialization (runtime) |
| `lombok` | latest | Boilerplate reduction (`@Builder`, `@Slf4j`, `@RequiredArgsConstructor`) |
| `spring-boot-starter-test` | 3.2.4 | Unit and integration testing |
| `spring-security-test` | 3.2.4 | Security layer testing utilities |

### Development Tools

| Tool | Purpose |
|------|---------|
| IntelliJ IDEA | Primary IDE |
| Postman | API testing and documentation |
| MySQL Workbench | Database management |
| Maven | Build tool and dependency management |
| Git | Version control |
| SLF4J + Logback | Structured logging with daily file rotation |

---

##  Project Structure

```
src/
└── main/
    ├── java/com/MarketHive/
    │   │
    │   ├── MarketHiveApplication.java          ← Entry point (@SpringBootApplication)
    │   │
    │   ├── config/
    │   │   └── SecurityConfig.java             ← Spring Security + JWT filter chain setup
    │   │
    │   ├── security/
    │   │   ├── JwtUtils.java                   ← Token generation, validation, claim extraction
    │   │   ├── JwtAuthFilter.java              ← Intercepts every request, sets SecurityContext
    │   │   └── AuthEntryPoint.java             ← Returns 401 JSON for unauthorized requests
    │   │
    │   ├── filter/
    │   │   └── RequestLoggingFilter.java       ← Logs every HTTP request and response
    │   │
    │   ├── enums/
    │   │   ├── Role.java                       ← admin | market | user
    │   │   ├── OrderStatus.java                ← pending | confirmed | shipped | delivered | cancelled
    │   │   └── PaymentStatus.java              ← pending | paid | failed | refunded
    │   │
    │   ├── entity/
    │   │   ├── User.java                       ← Implements UserDetails (Spring Security)
    │   │   ├── Market.java                     ← Vendor storefront, owned by market-role user
    │   │   ├── Category.java                   ← Self-referencing (parent/child hierarchy)
    │   │   ├── Product.java                    ← Belongs to Market + Category, has stock
    │   │   ├── Order.java                      ← Customer order with status and shipping address
    │   │   ├── OrderItem.java                  ← Line items, stores unit price at purchase time
    │   │   ├── Payment.java                    ← One-to-one with Order, tracks transaction
    │   │   ├── Review.java                     ← One per user per product (UNIQUE constraint)
    │   │   └── CartItem.java                   ← Persisted cart, cleared when order is placed
    │   │
    │   ├── repository/
    │   │   ├── UserRepository.java             ← findByEmail, existsByEmail, findAllByRole
    │   │   ├── MarketRepository.java           ← findByOwnerId, findByIsApproved
    │   │   ├── CategoryRepository.java         ← findByParentIsNull, findByParentId
    │   │   ├── ProductRepository.java          ← searchByKeyword, findByMarketId
    │   │   ├── OrderRepository.java            ← findByUserId, findByStatus
    │   │   ├── OrderItemRepository.java        ← findByOrderId, findByProductId
    │   │   ├── PaymentRepository.java          ← findByOrderId, findByStatus
    │   │   ├── ReviewRepository.java           ← findAverageRating, existsByProductAndUser
    │   │   └── CartItemRepository.java         ← findByUserId, deleteByUserId
    │   │
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── RegisterRequest.java        ← name, email, password, phone
    │   │   │   ├── LoginRequest.java           ← email, password
    │   │   │   ├── MarketRequest.java          ← name, description, logoUrl
    │   │   │   ├── CategoryRequest.java        ← name, parentId
    │   │   │   ├── ProductRequest.java         ← name, price, stock, categoryId, imageUrl
    │   │   │   ├── CartRequest.java            ← productId, quantity
    │   │   │   ├── OrderRequest.java           ← shippingAddress
    │   │   │   ├── PaymentRequest.java         ← orderId, method, transactionId
    │   │   │   └── ReviewRequest.java          ← rating (1–5), comment
    │   │   └── response/
    │   │       ├── ApiResponse.java            ← Generic wrapper { success, message, data }
    │   │       ├── AuthResponse.java           ← token, id, name, email, role
    │   │       ├── UserResponse.java           ← id, name, email, role, phone, isActive
    │   │       ├── MarketResponse.java         ← id, name, ownerName, isApproved
    │   │       ├── CategoryResponse.java       ← id, name, parentId, parentName
    │   │       ├── ProductResponse.java        ← id, name, price, stock, averageRating
    │   │       ├── CartResponse.java           ← productId, quantity, unitPrice, subtotal
    │   │       ├── OrderResponse.java          ← id, status, items, payment, totalAmount
    │   │       ├── OrderItemResponse.java      ← productName, quantity, unitPrice, subtotal
    │   │       ├── PaymentResponse.java        ← amount, status, method, paidAt
    │   │       └── ReviewResponse.java         ← rating, comment, userName, createdAt
    │   │
    │   ├── exception/
    │   │   ├── ResourceNotFoundException.java  ← Thrown when entity not found → 404
    │   │   ├── BadRequestException.java        ← Thrown for invalid operations → 400
    │   │   ├── ForbiddenException.java         ← Thrown for ownership violations → 403
    │   │   └── GlobalExceptionHandler.java     ← @RestControllerAdvice catches all exceptions
    │   │
    │   ├── service/
    │   │   ├── AuthService.java
    │   │   ├── UserService.java
    │   │   ├── MarketService.java
    │   │   ├── CategoryService.java
    │   │   ├── ProductService.java
    │   │   ├── CartService.java
    │   │   ├── OrderService.java
    │   │   ├── PaymentService.java
    │   │   ├── ReviewService.java
    │   │   └── impl/
    │   │       ├── AuthServiceImpl.java
    │   │       ├── UserServiceImpl.java        ← Also implements UserDetailsService for JWT
    │   │       ├── MarketServiceImpl.java
    │   │       ├── CategoryServiceImpl.java
    │   │       ├── ProductServiceImpl.java
    │   │       ├── CartServiceImpl.java
    │   │       ├── OrderServiceImpl.java
    │   │       ├── PaymentServiceImpl.java
    │   │       └── ReviewServiceImpl.java
    │   │
    │   └── controller/
    │       ├── AuthController.java             ← /api/auth/**
    │       ├── UserController.java             ← /api/users/** and /api/admin/users/**
    │       ├── MarketController.java           ← /api/markets/** and /api/market/**
    │       ├── CategoryController.java         ← /api/categories/**
    │       ├── ProductController.java          ← /api/products/** and /api/market/products/**
    │       ├── CartController.java             ← /api/cart/**
    │       ├── OrderController.java            ← /api/orders/** and /api/admin/orders/**
    │       ├── PaymentController.java          ← /api/payments/** and /api/admin/payments/**
    │       └── ReviewController.java           ← /api/reviews/**
    │
    └── resources/
        ├── application.properties              ← DB, JWT, JPA configuration
        └── logback-spring.xml                  ← Log files, rotation, routing rules
```

---

## Database Design

### Tables Overview

| Table | Description | Key Relations |
|-------|-------------|---------------|
| `users` | All accounts (admin, market, user) in one table with a role column | Owns markets, places orders, writes reviews |
| `markets` | Vendor storefronts, each owned by one user | Belongs to user, has many products |
| `categories` | Self-referencing hierarchy (parent → child) | Has many products |
| `products` | Items for sale with stock tracking | Belongs to market and category |
| `orders` | Customer purchase records with status | Belongs to user, has order items |
| `order_items` | Line items — stores price at time of purchase | Belongs to order and product |
| `payments` | One-to-one with each order | Tracks method, transaction ID, and status |
| `reviews` | Product ratings — one per user per product | Enforced with UNIQUE(product_id, user_id) |
| `cart_items` | Persisted shopping cart per user | Cleared automatically when order is placed |

### Entity Relationships

```
users ──────────────────┬──< markets
                        ├──< orders
                        ├──< reviews
                        └──< cart_items

markets ────────────────└──< products

categories ─────────────┬──< products
                        └──< categories (self / parent)

orders ─────────────────┬──< order_items
                        └─── payments (1 : 1)

products ───────────────┬──< order_items
                        ├──< reviews
                        └──< cart_items
```

---

##  User Roles & Permissions

| Feature | Public | User | Market | Admin |
|---------|:------:|:----:|:------:|:-----:|
| Browse products, markets, categories | ✅ | ✅ | ✅ | ✅ |
| Register / Login | ✅ | ✅ | ✅ | ✅ |
| Manage cart | ❌ | ✅ | ❌ | ❌ |
| Place & cancel orders | ❌ | ✅ | ❌ | ❌ |
| Process payments | ❌ | ✅ | ❌ | ❌ |
| Write & manage reviews | ❌ | ✅ | ❌ | ❌ |
| Create & manage own markets | ❌ | ❌ | ✅ | ❌ |
| Create & manage own products | ❌ | ❌ | ✅ | ❌ |
| Manage all users | ❌ | ❌ | ❌ | ✅ |
| Approve / reject vendor accounts | ❌ | ❌ | ❌ | ✅ |
| Update order statuses | ❌ | ❌ | ❌ | ✅ |
| Issue refunds | ❌ | ❌ | ❌ | ✅ |
| Manage categories | ❌ | ❌ | ❌ | ✅ |

### Vendor Approval Flow

```
Vendor registers   →  Account created (isActive = false, no token returned)
       ↓
Admin logs in      →  Views all pending market accounts
       ↓
Admin approves     →  Account activated (isActive = true)
       ↓
Vendor logs in     →  Receives JWT token → can now create markets and sell products
```

---

##  API Documentation

### Base URL
```
http://localhost:8080
```

### Standard Response Format

```json
{
  "success": true,
  "message": "Operation description",
  "data": { }
}
```

---

### Auth

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|:-------------:|
| POST | `/api/auth/register/user` | Register a customer account | ❌ |
| POST | `/api/auth/register/market` | Register a vendor (pending approval) | ❌ |
| POST | `/api/auth/login` | Login and receive JWT token | ❌ |

---

### Users

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/users/me` | Get my own profile | Any |
| GET | `/api/admin/users` | List all users | Admin |
| GET | `/api/admin/users/{id}` | Get user by ID | Admin |
| PATCH | `/api/admin/users/{id}/toggle-active` | Toggle user active status | Admin |
| PATCH | `/api/admin/users/{id}/approve-market` | Approve a vendor account | Admin |
| PATCH | `/api/admin/users/{id}/reject-market` | Reject a vendor account | Admin |

---

###  Markets

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/markets` | All approved markets | Public |
| GET | `/api/markets/{id}` | Market by ID | Public |
| POST | `/api/market/markets` | Create my market | Market |
| GET | `/api/market/markets` | My markets | Market |
| PUT | `/api/market/markets/{id}` | Update my market | Market |
| DELETE | `/api/market/markets/{id}` | Delete my market | Market |
| GET | `/api/admin/markets` | All markets | Admin |
| PATCH | `/api/admin/markets/{id}/approve` | Approve market | Admin |
| PATCH | `/api/admin/markets/{id}/reject` | Reject market | Admin |

---

### Categories

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/categories` | All categories | Public |
| GET | `/api/categories/roots` | Root categories only | Public |
| GET | `/api/categories/{id}/children` | Subcategories | Public |
| POST | `/api/categories` | Create category | Admin |
| PUT | `/api/categories/{id}` | Update category | Admin |
| DELETE | `/api/categories/{id}` | Delete category | Admin |

---

###  Products

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/products` | All active products | Public |
| GET | `/api/products/{id}` | Product by ID | Public |
| GET | `/api/products/search?keyword=` | Search products | Public |
| GET | `/api/markets/{id}/products` | Products by market | Public |
| GET | `/api/categories/{id}/products` | Products by category | Public |
| POST | `/api/market/markets/{id}/products` | Add product to market | Market |
| PUT | `/api/market/products/{id}` | Update own product | Market |
| DELETE | `/api/market/products/{id}` | Delete own product | Market |
| PATCH | `/api/market/products/{id}/toggle-active` | Toggle product visibility | Market |

---

### 🛒 Cart

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/cart` | Get my cart | User |
| POST | `/api/cart` | Add item to cart | User |
| PATCH | `/api/cart/{productId}?quantity=` | Update item quantity | User |
| DELETE | `/api/cart/{productId}` | Remove item from cart | User |
| DELETE | `/api/cart` | Clear entire cart | User |

---

### Orders

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/orders` | Place order from cart | User |
| GET | `/api/orders` | My orders | User |
| GET | `/api/orders/{id}` | Order by ID | User |
| PATCH | `/api/orders/{id}/cancel` | Cancel my order | User |
| GET | `/api/admin/orders` | All orders | Admin |
| PATCH | `/api/admin/orders/{id}/status?status=` | Update order status | Admin |

**Order Status Values:**
```
pending → confirmed → shipped → delivered
                    ↘ cancelled
```

---

###  Payments

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/payments` | Process payment for an order | User |
| GET | `/api/payments/order/{orderId}` | Get payment by order | User |
| GET | `/api/admin/payments` | All payments | Admin |
| PATCH | `/api/admin/payments/order/{orderId}/refund` | Refund a payment | Admin |

---

###  Reviews

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/reviews/product/{productId}` | Reviews for a product | Public |
| GET | `/api/reviews/me` | My reviews | User |
| POST | `/api/reviews/product/{productId}` | Add a review | User |
| PUT | `/api/reviews/{reviewId}` | Update my review | User |
| DELETE | `/api/reviews/{reviewId}` | Delete my review | User |

---

## Logging System

MarketHive uses **SLF4J + Logback** for structured, file-based logging with daily rotation. No extra dependency needed — Logback ships built-in with Spring Boot.

### Log Files

| File | Contents | Retention |
|------|----------|-----------|
| `logs/app.log` | Everything from the application | 30 days |
| `logs/error.log` | Errors only (5xx responses) | 30 days |
| `logs/requests.log` | Every HTTP request and response with duration | 14 days |
| `logs/security.log` | Login, register, approval events | 60 days |

### Sample Log Output

```
# requests.log
2026-04-27 12:48:36 INFO  RequestLoggingFilter - >>> REQUEST  | POST /api/auth/login | IP: 127.0.0.1 | User: anonymous
2026-04-27 12:48:36 INFO  RequestLoggingFilter - <<< RESPONSE | POST /api/auth/login | STATUS: 200 | 143ms

# security.log
2026-04-27 12:48:36 INFO  AuthServiceImpl - LOGIN | email: john@gmail.com
2026-04-27 12:48:36 INFO  AuthServiceImpl - LOGIN | Success | id: c300... | role: user
```

### Log Folder Structure
```
logs/
├── app.log
├── error.log
├── requests.log
├── security.log
└── archived/
    ├── app-2026-04-27.log
    ├── requests-2026-04-27.log
    └── security-2026-04-27.log
```

---

## Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 17 or higher |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| IntelliJ IDEA | Any (recommended) |
| Postman | Any |

### Step 1 — Create the Database

```sql
CREATE DATABASE ecommerce;
```

Then run `ecommerce_schema_mysql.sql` to create all tables and insert seed data.

### Step 2 — Configure the Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 3 — Run the Application

```bash
mvn spring-boot:run
```

Wait for:
```
Started MarketHiveApplication in X seconds
```

### Step 4 — Test with Postman

Follow this exact flow after importing the Postman collection:

```
1.  POST /api/auth/login                         → login as admin        → adminToken saved
2.  POST /api/auth/register/market               → register a vendor
3.  PATCH /api/admin/users/{id}/approve-market   → admin approves vendor
4.  POST /api/auth/login                         → vendor logs in        → marketToken saved
5.  POST /api/market/markets                     → create market         → marketId saved
6.  POST /api/market/markets/{id}/products       → add product           → productId saved
7.  POST /api/auth/register/user                 → register customer     → userToken saved
8.  POST /api/cart                               → add to cart
9.  POST /api/orders                             → place order           → orderId saved
10. POST /api/payments                           → pay for order
11. POST /api/reviews/product/{id}              → leave a review
```

---

##  Environment Configuration

```properties
# ── Server ─────────────────────────────────────────────────────
server.port=8080

# ── Database ────────────────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ── JPA / Hibernate ─────────────────────────────────────────────
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# ── JWT ─────────────────────────────────────────────────────────
app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
app.jwt.expiration-ms=86400000
```

| Property | Description | Default |
|----------|-------------|---------|
| `app.jwt.secret` | Base64-encoded HS256 signing key | See above |
| `app.jwt.expiration-ms` | Token lifetime in milliseconds | `86400000` (24 hours) |

>  **Production Warning:** Always change the JWT secret before deploying. Never commit secrets to version control.

---

##  Postman Collection
Here the Project PostMan Collection For the Whole end points 
https://www.postman.com/eng-yara-s-team/workspace/springboot/collection/47929313-45c45929-e0fe-4e29-a925-b81d3b7c9709?action=share&source=copy-link&creator=47929313


##  Development Team

| Role | Developer |
|------|-----------|
| **Backend Developer** | Yara H. Daraghmeh |

---

<div align="center">

*MarketHive — Multi-Vendor E-Commerce API · Spring Boot 3 · JWT · MySQL · 2026*

</div>
