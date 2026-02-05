import axios from 'axios';

const API_BASE = (import.meta.env.VITE_API_BASE_URL as string) || 'http://localhost:8080/payment-integration/api';

export interface CreatePaymentRequest {
  amount: number;
  currency: string;
  idempotencyKey?: string;
  provider: 'STRIPE';
  providerDetails?: {
    returnUrl?: string;
    saveCard?: boolean;
    customerEmail?: string;
  };
}

export interface PaymentResponse {
  paymentId?: string;
  amount?: number;
  currency?: string;
  providerPaymentId?: string;
  clientSecret?: string;
  rawStatus?: string;
}

export const createPayment = (payload: CreatePaymentRequest) =>
  axios.post<PaymentResponse>(`${API_BASE}/v1/payments`, payload).then(r => r.data);

export const getPayment = (id: string) =>
  axios.get<PaymentResponse>(`${API_BASE}/v1/payments/${encodeURIComponent(id)}`).then(r => r.data);
