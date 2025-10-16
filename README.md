# Платформа Booking Backend

Распределённая микросервисная система для управления бронированиями отелей, построенная на Spring Boot 3.5, Spring Cloud 2024.x и Java 17. Проект демонстрирует согласование бронирований сагой, идемпотентные процессы, JWT‑безопасность и маршрутизацию через API Gateway.

## Обзор архитектуры

```bash
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
- **JWT‑безопасность и проверка 401/403**: модуль `shared` предоставляет сервис токенов, фильтры и авто-конфигурацию; роли `USER` / `ADMIN` контролируются на уровне методов. Интеграционные тесты (`BookingSecurityIntegrationTest`, `GatewaySecurityIntegrationTest`) гарантируют корректные ответы 401/403.
- **API Gateway**: маршрутизирует `/api/bookings/**` и `/api/hotels/**`, проксируя JWT к backend‑сервисам.
- **OpenAPI**: у каждого сервиса доступен Swagger UI через `springdoc-openapi`.
- **Интеграционные тесты**: `BookingSagaIntegrationTest` тестирует успешный сценарий и компенсацию; `RoomServiceConcurrencyTest` имитирует конкурентные бронирования; `BookingSecurityIntegrationTest` и `GatewaySecurityIntegrationTest` покрывают безопасность.
- **Трассировка запросов**: `CorrelationIdFilter` формирует `X-Correlation-Id`, логирование бизнес-событий (`booking-service`, `hotel-service`) фиксирует `bookingId/userId/roomId`, что облегчает диагностику.

## Требования

- Java 17+
- Gradle Wrapper (в репозитории)
- (Опционально) Docker для контейнеризации

## Быстрый старт (Docker)

1. Убедитесь, что установлены Docker и Docker Compose.

2. Соберите и запустите весь стек:

   ```bash
   docker compose up -d --build
   ```

3. Проверьте состояние контейнеров (опционально):

   ```bash
   docker compose ps
   ```

4. Полезные URL после запуска:
   - Eureka Dashboard: <http://localhost:8761>
   - Gateway (внешний вход): <http://localhost:8080>
   - Booking Service Swagger: <http://localhost:8081/swagger-ui.html>
   - Hotel Service Swagger: <http://localhost:8082/swagger-ui.html>

5. Для просмотра логов:

   ```bash
   docker compose logs -f
   ```

6. Остановить и удалить контейнеры:

   ```bash
   docker compose down
   ```

## Конфигурация

- **application.yml** каждого сервиса содержит ключевые настройки:
  - `app.security.jwt.secret` — Base64‑секрет (рекомендуется переопределять по средам).
  - `spring.cloud.gateway.routes` — маршруты API Gateway.
  - `eureka.client.service-url.defaultZone` — адрес Eureka.
- **Тестовый профиль** (`booking-service/src/test/resources/application-test.yml`) отключает Eureka, включает in-memory H2 и выключает проверку совместимости Spring Cloud.

## Тестирование

### Unit / Integration

```
./gradlew clean test
```

- `BookingSagaIntegrationTest` — успешная сага и компенсация с моками HotelClient.
- `BookingSecurityIntegrationTest`, `GatewaySecurityIntegrationTest` — проверки 401/403.
- `RoomServiceConcurrencyTest` — конкурентный доступ к `RoomService`.

### E2E (Docker)

```
docker compose up -d --build
```

1. Зарегистрируйте пользователя:

   ```bash
   curl -X POST http://localhost:8080/api/users/register \
     -H "Content-Type: application/json" \
     -d '{"username":"demo","password":"DemoPass123"}'
   ```

2. Получите токен:

   ```bash
   curl -X POST http://localhost:8080/api/users/auth \
     -H "Content-Type: application/json" \
     -d '{"username":"demo","password":"DemoPass123"}'
   ```

3. Создайте бронирование:

   ```bash
   curl -X POST http://localhost:8080/api/bookings \
     -H "Authorization: Bearer <TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"hotelId":1,"roomId":1,"startDate":"2025-10-25","endDate":"2025-10-28","autoSelect":false}'
   ```

4. Посмотрите логи и корреляцию:

   ```bash
   docker compose logs gateway
   ```

5. Остановите окружение:

   ```bash
   docker compose down
   ```

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

## Архитектурные решения (ADR)

- **Сага с компенсацией**: Booking Service после создания PENDING записи вызывает Hotel Service через Feign, подтверждает бронирование и при ошибке вызывает компенсацию (`releaseRoom`). Решение выбрано вместо двухфазного коммита для повышения отказоустойчивости и независимости сервисов.
- **Идемпотентность запросов**: идентификатор `requestId` вычисляется детерминированно из параметров бронирования и хранится в базе. Повторный вызов возвращает существующий результат, что снижает риски дублей при повторных запросах клиента.
- **Распределённая трассировка**: `shared` модуль предоставляет `CorrelationIdFilter`, который генерирует `X-Correlation-Id`. Все сервисы включают корреляционный идентификатор в логи и межсервисные вызовы, что упрощает диагностику.
- **Security как библиотека**: общие компоненты JWT (фильтры, сервис токенов, обработчики ошибок) размещены в модуле `shared` и подключаются через авто-конфигурацию. Это исключает дублирование и унифицирует обработку безопасности.

## Заметки по разработке

- Для Lombok и MapStruct включено автогенерирование; в IDE (IntelliJ) убедитесь, что включена обработка аннотаций.
- Совместимость Spring Boot/Cloud гарантирована за счёт фиксированных версий (`spring-cloud-starter-netflix-eureka-client:4.2.0`, `springdoc-openapi` и т.д.).

## Лицензия

Учебный проект. Вы можете адаптировать и расширять его по своему усмотрению.
