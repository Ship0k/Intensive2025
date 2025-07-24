    Микросервис предоставляет REST API для управления пользователями (CRUD) и публикует уведомления при создании и удалении через брокер сообщений. 
Для развёртывания модульной микросервисной системы используется Docker Compose — запускается одной командой: docker compose up --build.

    Включены централизованная конфигурация, сервис-дискавери, интеграция с брокером сообщений, база данных и локальные инструменты.
  Используемые технологии:
Сервисы	              user-service, notification-service
Инфраструктура	      config-server, eureka-server, gateway-service
Сообщения	      Kafka
База данных	      PostgreSQL
Dev-инструменты	      Mailhog, Kafka-UI
Оркестрация	      Docker Compose, healthcheck

  Точки входа:
API Gateway	http://localhost:8079 - Входная точка: маршруты /api/users/**, /api/mail/send

Eureka UI	http://localhost:8761
Kafka UI	http://localhost:8081
MailHog UI	http://localhost:8025

User Service напрямую	http://localhost:8080/api/users
Notification Service напрямую	http://localhost:8082/api/mail/send

