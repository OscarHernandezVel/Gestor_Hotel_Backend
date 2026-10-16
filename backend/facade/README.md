# Hotel Facade API

Spring Boot 4 / Java 17 backend. Design patterns:

| Pattern | Where |
|---|---|
| **Facade** | `facade/HotelFacade` is the only class the controller talks to. It coordinates `ReserveOperation`, `CheckInOperation`, `CheckOutOperation`, `AddServiceOperation` and the subsystems in `service/` (rooms, fees, extras, access, billing). |
| **Builder** | `builder/ReservationBuilder` assembles and validates an immutable `ReservationDraft`. |
| **Login** | `auth/` (register, BCrypt passwords, bearer tokens, `AuthInterceptor` protecting `/api/hotel/**`). |

## Run

```bash
# in-memory H2 database, nothing else needed
./mvnw spring-boot:run          # or: mvn spring-boot:run

# MySQL instead
export DATABASE_URL="jdbc:mysql://HOST:3306/hotel"
export DB_USER=root DB_PASSWORD=secret
./mvnw spring-boot:run
```

Tests: `./mvnw test`. A full API walk-through: `./api-test.sh` (server must be running).

## Endpoints

Public: `GET /api/health`, `POST /api/auth/register`, `POST /api/auth/login`
Protected (header `Authorization: Bearer <token>`): `POST /api/auth/logout`

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/hotel/rooms/availability?from=&to=&type=` | free rooms |
| GET | `/api/hotel/quote?type=&checkIn=&checkOut=` | price estimate |
| POST | `/api/hotel/reservations` | reserve (Builder inside) |
| GET | `/api/hotel/reservations` | my reservations |
| GET | `/api/hotel/reservations/{id}` | one reservation |
| POST | `/api/hotel/reservations/{id}/services` | add SPA / BREAKFAST / TRANSFER |
| POST | `/api/hotel/reservations/{id}/check-in` | check-in, returns digital key |
| POST | `/api/hotel/reservations/{id}/check-out` | check-out, returns invoice |
| GET | `/api/hotel/reservations/{id}/invoice` | invoice breakdown |
| DELETE | `/api/hotel/reservations/{id}` | cancel (only RESERVED) |

Dates use `yyyy-MM-dd`. Room types: `SIMPLE`, `DOUBLE`, `SUITE`.
