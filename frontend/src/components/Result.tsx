import React from 'react';

export default function Result({ data }: { data: any }) {
  if (!data) return null;

  const raw = (data.rawStatus || data.status || '').toString().toLowerCase();

  const statusBadge = () => {
    if (raw === 'succeeded' || raw === 'success') {
      return (
        <span className="banner success" style={{ display: 'inline-flex', alignItems: 'center', padding: '6px 10px', fontWeight: 700 }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style={{ marginRight: 8 }}>
            <path d="M20 6L9 17l-5-5" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          Succeeded
        </span>
      );
    }
    if (raw === 'declined' || raw === 'failed') {
      return (
        <span className="banner error" style={{ display: 'inline-flex', alignItems: 'center', padding: '6px 10px', fontWeight: 700 }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style={{ marginRight: 8 }}>
            <path d="M18 6L6 18M6 6l12 12" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          {raw === 'declined' ? 'Declined' : 'Failed'}
        </span>
      );
    }
    return (
      <span className="banner processing" style={{ display: 'inline-flex', alignItems: 'center', padding: '6px 10px', fontWeight: 700 }}>
        <span className="spinner" style={{ marginRight: 8 }} />
        Processing
      </span>
    );
  };

  return (
    <div className="result card" style={{ marginTop: 14 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
        <h4 style={{ margin: 0, fontSize: 16 }}>Payment Result</h4>
        {statusBadge()}
      </div>

      <table>
        <tbody>
          <tr>
            <td>Payment ID</td>
            <td style={{ wordBreak: 'break-all' }}>{data.paymentId ?? data.providerPaymentId}</td>
          </tr>
          <tr>
            <td>Provider payment id</td>
            <td style={{ wordBreak: 'break-all' }}>{data.providerPaymentId ?? '-'}</td>
          </tr>
          <tr>
            <td>Amount</td>
            <td>{data.amount ?? '-'}</td>
          </tr>
          <tr>
            <td>Currency</td>
            <td>{data.currency ?? '-'}</td>
          </tr>
          <tr>
            <td>Status / rawStatus</td>
            <td>{data.rawStatus ?? data.status ?? '-'}</td>
          </tr>
          <tr>
            <td>Client secret</td>
            <td style={{ wordBreak: 'break-all' }}>{data.clientSecret ?? '-'}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}
