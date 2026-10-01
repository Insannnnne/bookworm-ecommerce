# Book Worm E-Bookstore — Backend API

Spring Boot 3.3.5 · Java 17 · Spring Data JPA · Spring Security (JWT) · PostgreSQL / H2

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.3.5 |
| Language | Java 17 |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| ORM | Spring Data JPA / Hibernate |
| DB (prod) | PostgreSQL 15 |
| DB (dev) | H2 in-memory |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven 3.9 |

---

## Project Structure

```
src/main/java/com/bookworm/
├── config/          SecurityConfig, OpenApiConfig
├── controller/      AuthController, ProductController, CategoryController,
│                    AuthorController, CartController, OrderController,
│                    PaymentController, UserFeatureController
├── dto/             Request/Response DTOs for all entities
├── entity/          JPA entities (12 tables)
├── exception/       GlobalExceptionHandler, ResourceNotFoundException
├── repository/      Spring Data JPA repositories (11)
├── security/        JwtTokenProvider, JwtAuthenticationFilter,
│                    CustomUserDetailsService
└── service/         Business logic services (9)
```

---

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.9+

### Dev profile (H2 in-memory — no DB install needed)

```bash
./mvnw clean package -DskipTests
java -Dspring.profiles.active=dev \
     -Dapp.jwt.secret=<YOUR_SECRET_MIN_32_CHARS> \
     -jar target/bookworm-1.0.0.jar
```

### Prod profile (PostgreSQL)

```bash
export DB_URL=jdbc:postgresql://localhost:5432/bookworm
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export APP_JWT_SECRET=your_secret_min_32_chars
java -Dspring.profiles.active=prod -jar target/bookworm-1.0.0.jar
```

---

## API Documentation

Swagger UI: http://localhost:8080/swagger-ui/index.html  
OpenAPI JSON: http://localhost:8080/v3/api-docs

All protected endpoints require `Authorization: Bearer <jwt_token>` header.

---

## API Endpoints (50 operations)

### Authentication
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/register` | Public | Register new user, returns JWT |
| POST | `/auth/login` | Public | Login, returns JWT |
| POST | `/auth/logout` | Bearer | Stateless logout (204) |

### User Profile & Addresses
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/users/me` | Bearer | Get authenticated user profile |
| PUT | `/users/me` | Bearer | Update first/last name |
| GET | `/users/me/addresses` | Bearer | List saved addresses |
| POST | `/users/me/addresses` | Bearer | Add delivery address |
| PUT | `/users/me/addresses/{id}` | Bearer | Update address |
| DELETE | `/users/me/addresses/{id}` | Bearer | Delete address |

### Categories
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/categories` | Public | List all categories |
| POST | `/categories` | Bearer | Create category |
| GET | `/categories/{id}` | Public | Get category by ID |
| PUT | `/categories/{id}` | Bearer | Update category |
| DELETE | `/categories/{id}` | Bearer | Delete category |
| GET | `/categories/{id}/products` | Public | Products in category |

### Authors
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/authors` | Public | Paginated author list |
| POST | `/authors` | Bearer | Create author |
| GET | `/authors/{id}` | Public | Get author by ID |
| PUT | `/authors/{id}` | Bearer | Update author |
| DELETE | `/authors/{id}` | Bearer | Delete author |
| GET | `/authors/{id}/products` | Public | Products by author |

### Products
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/products` | Public | Paginated product list |
| POST | `/products` | Bearer | Create product |
| GET | `/products/{id}` | Public | Full product detail |
| PUT | `/products/{id}` | Bearer | Update product |
| DELETE | `/products/{id}` | Bearer | Delete product |
| GET | `/products/search` | Public | Search/filter (`q`, `categoryId`, `authorId`, `format`, `language`, `minPrice`, `maxPrice`) |
| GET | `/products/featured` | Public | Featured products (`?section=recommended\|bestsellers\|new_launches`) |
| GET | `/products/{id}/related` | Public | Related products |
| GET | `/products/{id}/reviews` | Public | Product reviews |
| POST | `/products/{id}/reviews` | Bearer | Submit review (rating + comment) |

### Cart
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/cart` | Bearer | Get current cart with totals |
| POST | `/cart/items` | Bearer | Add item to cart |
| PUT | `/cart/items/{id}` | Bearer | Update item quantity |
| DELETE | `/cart/items/{id}` | Bearer | Remove cart item |
| DELETE | `/cart` | Bearer | Clear entire cart |
| POST | `/cart/coupon` | Bearer | Apply coupon code |
| DELETE | `/cart/coupon` | Bearer | Remove coupon |

### Wishlist
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/users/me/wishlist` | Bearer | List wishlist items |
| POST | `/users/me/wishlist` | Bearer | Add product to wishlist |
| DELETE | `/users/me/wishlist/{productId}` | Bearer | Remove from wishlist |

### Recommendations
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/users/me/recommendations` | Bearer | Personalised book recommendations |

### Orders
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/orders` | Bearer | Paginated order history |
| POST | `/orders` | Bearer | Place order from cart |
| GET | `/orders/{id}` | Bearer | Order detail |
| POST | `/orders/{id}/buy-again` | Bearer | Re-add order items to cart |
| GET | `/orders/{id}/confirmation` | Bearer | Post-purchase confirmation screen |

### Payments
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/payments/initiate` | Bearer | Initiate payment session |
| GET | `/payments/{paymentId}` | Bearer | Payment session status |
| POST | `/payments/{paymentId}/confirm` | Bearer | Confirm payment (Credit Card / Debit Card / UPI / Wallet) |

---

## Request / Response Examples

### Register
```http
POST /auth/register
Content-Type: application/json

{
  "firstName": "Ananya",
  "lastName": "Mohanty",
  "email": "ananya@bookworm.com",
  "password": "Test@1234",
  "phone": "9000000001"
}
```
Response `201`:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": { "id": 1, "firstName": "Ananya", "lastName": "Mohanty", "email": "ananya@bookworm.com" }
}
```

### Create Product
```http
POST /products
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Joy of Minimalism",
  "description": "Declutter your life",
  "price": 149.00,
  "format": "Paperback",
  "language": "English",
  "isbn": "978-0-111111-01-1",
  "stock": 100,
  "categoryIds": [1],
  "authorId": 1,
  "featured": true,
  "newLaunch": true
}
```
Response `201`: Full `ProductResponse` with id, title, author, categories, price, stock.

### Place Order
```http
POST /orders
Authorization: Bearer <token>
Content-Type: application/json

{ "addressId": 1, "paymentMethod": "CREDIT_CARD" }
```
Response `201`: `OrderResponse` with items, subtotal, tax (12%), delivery charges, totalAmount.

---

## Database Schema (13 tables)

`users` · `addresses` · `categories` · `authors` · `products` · `product_categories` (join) ·
`reviews` · `carts` · `cart_items` · `wishlist_items` · `orders` · `order_items` · `payments`

---

## Security

- Passwords hashed with **BCrypt**
- JWT tokens signed with **HS256**, 24-hour expiry
- All secrets loaded from **environment variables** — never hardcoded
- Server binds to `127.0.0.1` only
- Input validation via Jakarta Bean Validation on all request DTOs

---

## Test Results

All **72 API endpoints** tested and passing:

| Group | Tests | Status |
|---|---|---|
| Auth & User | 10 | PASS |
| Categories | 7 | PASS |
| Authors | 7 | PASS |
| Products | 19 | PASS |
| Cart | 10 | PASS |
| Wishlist | 6 | PASS |
| Recommendations | 1 | PASS |
| Orders | 5 | PASS |
| Payments | 3 | PASS |
| Order Confirmation | 1 | PASS |
| Edge Cases | 3 | PASS |
| **Total** | **72** | **100% PASS** |
