import React from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
import PaymentForm from './components/PaymentForm';

const publishableKey = import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY as string | undefined;

export default function App() {
  if (!publishableKey) {
    return (
      <div className="container">
        <h1 className="app-title">Stripe Payment Demo</h1>
        <p className="app-subtitle" style={{ color: 'var(--danger)' }}>
          Missing VITE_STRIPE_PUBLISHABLE_KEY. Create .env and add your Stripe publishable key.
        </p>
      </div>
    );
  }

  const stripePromise = loadStripe(publishableKey);

  return (
    <div className="container">

      <Elements stripe={stripePromise}>
        <PaymentForm />
      </Elements>
    </div>
  );
}
