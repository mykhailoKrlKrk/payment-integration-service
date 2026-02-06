# Stripe Payment Integration

This project demonstrates a complete card payment flow using **Stripe** with a
**Spring Boot (Java)** backend and a lightweight frontend application.

---

## Tech Stack

### Backend
- Java 21
- Spring Boot (MVC)
- Spring Data JPA
- Liquibase
- PostgreSQL
- Stripe Java SDK

### Frontend
- Vite
- React
- Stripe.js (`@stripe/react-stripe-js`)
- TypeScript

### Infrastructure
- Docker
- Docker Compose
- Stripe CLI (for local webhooks)

---

## Payment Flow Overview

1. User opens the payment form in the browser and enters card details.
2. Client sends a **Create Payment** request to the backend.
3. Backend creates a **Stripe PaymentIntent** and persists the payment.
4. Client confirms the card payment using `clientSecret`.
5. Stripe processes the payment.
6. Stripe sends a webhook notification to the backend.
7. Backend validates the webhook signature and triggers a status check.
8. Payment status is updated in the database.
9. Client displays the final payment status to the user.

A scheduler is used as a fallback mechanism to synchronize payment status if
webhook delivery does not occur.

---

## Prerequisites

- Docker & Docker Compose
- Stripe account (Test mode)
- Stripe CLI

---

## Configuration

All configuration is provided via environment variables.

Create a `.env` file in the project root:
```env
# App
ACTIVE_PROFILE=dev
SERVER_PORT=8080
PAYMENTS_SYNC_RATE=30000
PAYMENTS_SYNC_BATCH_SIZE=50

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=stripe_payments
DB_USERNAME=stripe_user
DB_PASSWORD=stripe_pass

# Stripe (Test mode)
STRIPE_SECRET_KEY=<your-secret-key>
STRIPE_WEBHOOK_SECRET=<your-webhook-secret>

# Frontend
FRONTEND_PORT=3000
VITE_API_BASE_URL=http://localhost:8080/payment-integration/api
VITE_STRIPE_PUBLISHABLE_KEY=<your-publishable-key>
```

> **Getting your Stripe keys:**
> 1. Go to [Stripe Dashboard](https://dashboard.stripe.com/test/apikeys)
> 2. Copy your **Secret key** (starts with `sk_test_`)
> 3. Copy your **Publishable key** (starts with `pk_test_`)
> 4. Replace the placeholders in your `.env` file

---

## Running the Application (Important)

Before starting the application with Docker Compose, **Stripe webhook forwarding must be configured**.

### 1. Start Stripe webhook listener

In a separate terminal, run:
```bash
stripe listen \
  --forward-to http://localhost:8080/payment-integration/api/v1/payments/webhook/stripe
```

The Stripe CLI will output a webhook signing secret that starts with `whsec_`

### 2. Configure Stripe keys

Add all three Stripe keys to your `.env` file:
```env
STRIPE_SECRET_KEY=<paste-your-secret-key-here>
STRIPE_WEBHOOK_SECRET=<paste-webhook-secret-from-stripe-cli>
VITE_STRIPE_PUBLISHABLE_KEY=<paste-your-publishable-key-here>
```

After updating the `.env` file, start the application:
```bash
docker compose up --build
```

---

## Application Endpoints

Once the application is running:

**Frontend UI:**  
http://localhost:3000

**Backend API:**  
http://localhost:8080/payment-integration/api

**Swagger UI:**  
http://localhost:8080/payment-integration/api/swagger-ui.html
