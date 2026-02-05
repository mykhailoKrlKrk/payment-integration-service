import React, { useState, useEffect } from 'react';
import { CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { createPayment, getPayment } from '../api/paymentClient';
import Result from './Result';

type Status = 'idle' | 'creating' | 'confirming' | 'waiting' | 'final' | 'error';

const POLL_INTERVAL_MS = 1500;
const POLL_TIMEOUT_MS = 30000;

export default function PaymentForm() {
  const stripe = useStripe();
  const elements = useElements();

  const [amount, setAmount] = useState<number>(1000);
  const [currency, setCurrency] = useState<string>('usd');
  const [email, setEmail] = useState<string>('');
  const [idempotencyKey, setIdempotencyKey] = useState<string>('');
  const [scenario, setScenario] = useState<'SUCCESS' | 'DECLINE'>('SUCCESS');

  const [status, setStatus] = useState<Status>('idle');
  const [message, setMessage] = useState<string | null>(null);
  const [result, setResult] = useState<any>(null);

  useEffect(() => {
    setMessage(null);
  }, [amount, currency, email, idempotencyKey, scenario]);

  const isProcessing = status === 'creating' || status === 'confirming' || status === 'waiting';

  async function handlePay(e: React.FormEvent) {
    e.preventDefault();
    setMessage(null);

    if (!stripe || !elements) {
      setMessage('Stripe is not ready');
      return;
    }

    setStatus('creating');
    try {
      const payload = {
        amount,
        currency,
        idempotencyKey: idempotencyKey || undefined,
        provider: 'STRIPE' as const,
        providerDetails: {
          returnUrl: 'http://localhost:3000/result',
          saveCard: false,
          customerEmail: email || undefined,
        },
      };
      const createResp = await createPayment(payload);
      const clientSecret = createResp.clientSecret;
      const paymentId = createResp.paymentId;
      const providerPaymentId = createResp.providerPaymentId;

      if (!clientSecret) {
        setStatus('error');
        setMessage('Server did not return clientSecret');
        return;
      }

      setStatus('confirming');
      const card = elements.getElement(CardElement);
      if (!card) {
        setStatus('error');
        setMessage('Card element not found');
        return;
      }

      const confirmResult = await stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card,
          billing_details: {
            email: email || undefined,
          },
        },
      });

      if (confirmResult.error) {
        setStatus('error');
        setMessage(`Payment confirmation failed: ${confirmResult.error.message}`);
        setResult({
          paymentId: paymentId ?? providerPaymentId,
          providerPaymentId,
          clientSecret,
          confirmResult,
        });
        return;
      }

      setStatus('waiting');
      setResult({
        paymentId: paymentId ?? providerPaymentId,
        providerPaymentId,
        clientSecret,
      });

      const start = Date.now();
      await new Promise<void>((resolve) => {
        const interval = setInterval(async () => {
          const elapsed = Date.now() - start;
          if (elapsed > POLL_TIMEOUT_MS) {
            clearInterval(interval);
            setStatus('final');
            setMessage('Timeout waiting for final payment status');
            resolve();
            return;
          }

          if (!paymentId) {
            clearInterval(interval);
            setStatus('final');
            setMessage('Created payment but internal id missing. Check server logs/webhooks.');
            resolve();
            return;
          }

          try {
            const current = await getPayment(paymentId);
            if (current?.rawStatus) {
              if (current.rawStatus === 'SUCCEEDED' || current.rawStatus === 'succeeded') {
                clearInterval(interval);
                setStatus('final');
                setResult(current);
                resolve();
                return;
              }
              if (current.rawStatus === 'DECLINED' || current.rawStatus === 'declined') {
                clearInterval(interval);
                setStatus('final');
                setResult(current);
                resolve();
                return;
              }
              setResult(current);
            } else if (current?.status) {
              if (current.status === 'SUCCEEDED' || current.status === 'DECLINED') {
                clearInterval(interval);
                setStatus('final');
                setResult(current);
                resolve();
                return;
              }
              setResult(current);
            }
          } catch (err: any) {
            if (err?.response?.status === 404) {
              clearInterval(interval);
              setStatus('final');
              setMessage('Payment not found on server');
              resolve();
            }
          }
        }, POLL_INTERVAL_MS);
      });
    } catch (err: any) {
      setStatus('error');
      setMessage(err?.message || 'Unexpected error');
    }
  }

  const normalizedStatus = (r: any) => {
    const raw = r?.rawStatus || r?.status || '';
    return String(raw).toLowerCase();
  };

  return (
    <div className="card">
      <div className="app-header" style={{ textAlign: 'left', marginBottom: 12 }}>
        <h3 className="app-title">Create a test payment</h3>
      </div>

      {status === 'final' && result && (normalizedStatus(result) === 'succeeded' || normalizedStatus(result) === 'success') && (
        <div className="banner success">
          <span className="icon" aria-hidden>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M20 6L9 17l-5-5" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
          </span>
          Payment succeeded
        </div>
      )}

      {((status === 'final' && result && (normalizedStatus(result) === 'declined' || normalizedStatus(result) === 'failed')) || status === 'error') && (
        <div className="banner error">
          <span className="icon" aria-hidden>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M18 6L6 18M6 6l12 12" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
          </span>
          {status === 'error' ? (message || 'Payment failed') : (result?.rawStatus || result?.status || 'Payment was declined')}
        </div>
      )}

      {isProcessing && (
        <div className="banner processing" role="status" aria-live="polite">
          <div className="spinner" />
          Processing payment...
        </div>
      )}

      <form onSubmit={handlePay} noValidate>
        <div className="form-row">
          <label>Amount (minor units)</label>
          <input
            className="input"
            type="number"
            value={amount}
            onChange={(e) => setAmount(Number(e.target.value))}
            min={1}
            required
          />
        </div>

        <div className="form-row">
          <label>Currency</label>
          <select className="input" value={currency} onChange={(e) => setCurrency(e.target.value)}>
            <option value="usd">USD</option>
            <option value="eur">EUR</option>
          </select>
        </div>

        <div className="form-row">
          <label>Email (optional)</label>
          <input className="input" type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>

        <div className="form-row">
          <label>Idempotency key (optional)</label>
          <input className="input" value={idempotencyKey} onChange={(e) => setIdempotencyKey(e.target.value)} />
        </div>

        <div className="form-row">
          <label>Scenario (used for hinting test card)</label>
          <select className="input" value={scenario} onChange={(e) => setScenario(e.target.value as any)}>
            <option value="SUCCESS">SUCCESS (4242 4242 4242 4242)</option>
            <option value="DECLINE">DECLINE (4000 0000 0000 9995 / 4000 0000 0000 0002)</option>
          </select>
        </div>

        <div className="form-row">
          <label>Card</label>
          <div className="card-element-wrapper">
            <CardElement options={{ hidePostalCode: true }} />
          </div>
        </div>

        <div className="button-row">
          <button type="submit" disabled={isProcessing}>
            {isProcessing ? <span className="spinner" /> : <svg className="icon" viewBox="0 0 24 24" width="16" height="16" fill="none"><path d="M12 3v18M3 12h18" stroke="rgba(255,255,255,0.95)" strokeWidth="2" strokeLinecap="round"/></svg>}
            {isProcessing ? 'Processing…' : 'Pay'}
          </button>
        </div>
      </form>

      <div className="hints">
        <strong>Test cards</strong>
        <ul>
          <li>Success: 4242 4242 4242 4242</li>
          <li>Decline (insufficient_funds): 4000 0000 0000 9995</li>
          <li>Decline (card_declined): 4000 0000 0000 0002</li>
        </ul>
      </div>

      <div className="status">
        <p>State: {status}</p>
        {message && <p className="error">{message}</p>}
      </div>

      <Result data={result} />
    </div>
  );
}
