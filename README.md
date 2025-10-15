# Платформа Booking Backend

Распределённая микросервисная система для управления бронированиями отелей, построенная на Spring Boot 3.5, Spring Cloud 2024.x и Java 17. Проект демонстрирует согласование бронирований сагой, идемпотентные процессы, JWT‑безопасность и маршрутизацию через API Gateway.

## Обзор архитектуры

```
+------------------+        +--------------------+        +-------------------+
|  API Gateway     |<-----> |  Booking Service   |<-----> |  Hotel Service     |
|  (Spring Cloud   |  JWT   |  (Бронирования,    |  Feign |  (Номера,          |
|   Gateway)       |        |   пользователи,    |        |   доступность)     |
+------------------+        +--------------------+        +-------------------+
          |                           |                           |
          |                           |                           |
          |                           v                           v
          |                    +--------------+          +----------------+
          |                    |  Shared Lib  |          |  Eureka Server |
          |                    |  (безопасн., |          |  (Service      |
          |                    |   DTO,       |          |   Discovery)   |
          |                    |   логгинг)   |          +----------------+
          |                    +--------------+
          |
          v
Swagger UI / OpenAPI для каждого сервиса
```

### Модули

| Модуль            | Описание |
| ----------------- | -------- |
| `gateway`         | Spring Cloud Gateway с проксированием JWT и конфигурацией маршрутов. |
| `booking-service` | Управление пользователями и бронированиями, двухшаговая сага, JWT‑аутентификация, Feign‑клиенты к hotel-service. |
| `hotel-service`   | CRUD по отелям и комнатам, рекомендации номеров, блокировки доступности и компенсационные операции. |
| `shared`          | Общие компоненты: JWT‑безопасность, обработка ошибок, корреляция логов, DTO, MapStruct‑мэпперы. |
| `eureka-server`   | Реестр сервисов (Service Discovery) для взаимодействия между компонентами. |

## Ключевые возможности

- **Сага бронирования**: `BookingService` создаёт PENDING записи, вызывает подтверждение доступности номера, переводит статус в CONFIRMED либо CANCELLED с компенсацией.
- **Идемпотентность**: `requestId` формируется из атрибутов бронирования, что предотвращает дубли; репозиторий проверяет пересечения броней.
- **Балансировка нагрузки номеров**: `hotel-service` ведёт счётчик `timesBooked` и отдаёт рекомендации, упорядоченные по минимальной загрузке.
- **JWT‑безопасность**: модуль `shared` предоставляет сервис токенов, фильтры и авто-конфигурацию; роли `USER` / `ADMIN` контролируются на уровне методов.
- **API Gateway**: маршрутизирует `/api/bookings/**` и `/api/hotels/**`, проксируя JWT к backend‑сервисам.
- **OpenAPI**: у каждого сервиса доступен Swagger UI через `springdoc-openapi`.
- **Интеграционные тесты**: `BookingSagaIntegrationTest` проверяет успешную обработку и компенсацию при сбоях внешнего сервиса.

## Требования

- Java 17+
- Gradle Wrapper (в репозитории)
- (Опционально) Docker для контейнеризации

## Быстрый старт

1. **Запустите Eureka Server**
   ```bash
   ./gradlew :eureka-server:bootRun
   ```

2. **Запустите Hotel Service**
   ```bash
   ./gradlew :hotel-service:bootRun
   ```

3. **Запустите Booking Service**
   ```bash
   ./gradlew :booking-service:bootRun
   ```

4. **Запустите API Gateway**
   ```bash
   ./gradlew :gateway:bootRun
   ```

5. Откройте Swagger UI:
   - Booking Service: http://localhost:8081/swagger-ui.html
   - Hotel Service: http://localhost:8082/swagger-ui.html
   - Gateway: публичные маршруты на http://localhost:8080

## Конфигурация

- **application.yml** каждого сервиса содержит ключевые настройки:
  - `app.security.jwt.secret` — Base64‑секрет (рекомендуется переопределять по средам).
  - `spring.cloud.gateway.routes` — маршруты API Gateway.
  - `eureka.client.service-url.defaultZone` — адрес Eureka.
- **Тестовый профиль** (`booking-service/src/test/resources/application-test.yml`) отключает Eureka, включает in-memory H2 и выключает проверку совместимости Spring Cloud.

## Тестирование

```bash
./gradlew clean test
```

Основной интеграционный тест:
- `BookingSagaIntegrationTest` (booking-service) — проверка успешного сценария и компенсации с мокированным `HotelClient`.

## Основные API

### Booking Service
- `POST /api/users/register` — регистрация пользователя (возвращает JWT).
- `POST /api/users/auth` — аутентификация и выдача JWT.
- `POST /api/bookings` — создание бронирования (с выбором конкретного номера или авто-подбором).
- `GET /api/bookings` — список бронирований текущего пользователя.
- `DELETE /api/bookings/{id}` — отмена бронирования.

### Hotel Service
- `POST /api/hotels` (ADMIN) — добавление отеля.
- `POST /api/rooms` (ADMIN) — добавление номера.
- `GET /api/rooms/recommend` — рекомендованные номера, отсортированные по `timesBooked`.
- `POST /api/rooms/{id}/confirm-availability` — подтверждение доступности (внутренний маршрут для саги).
- `POST /api/rooms/{id}/release` — компенсационное снятие блокировки (внутренний маршрут).

### Gateway
- Проксирует `/api/bookings/**` и `/api/hotels/**`, передавая JWT в backend‑сервисы.

## Заметки по разработке

- Для Lombok и MapStruct включено автогенерирование; в IDE (IntelliJ) убедитесь, что включена обработка аннотаций.
- В тестах используется `@MockBean` (помечен как устаревший в будущих версиях Spring Boot); при обновлении стоит перейти на рекомендуемый API моков.
- Совместимость Spring Boot/Cloud гарантирована за счёт фиксированных версий (`spring-cloud-starter-netflix-eureka-client:4.2.0`, `springdoc-openapi` и т.д.).

## Лицензия

Учебный проект. Вы можете адаптировать и расширять его по своему усмотрению.
