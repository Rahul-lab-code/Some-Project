import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function PaymentsContainer() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiService.getPayments()
      .then(res => setData(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">Payments</h1>
      {loading ? <p>Loading...</p> : (
        <div className="bg-white shadow rounded-lg overflow-hidden border border-gray-100">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Method</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {data.length === 0 ? (
                <tr><td colSpan="4" className="px-6 py-4 text-center text-gray-500">No payments found.</td></tr>
              ) : data.map((p, i) => (
                <tr key={i}>
                  <td className="px-6 py-4 whitespace-nowrap">{p.id}</td>
                  <td className="px-6 py-4 whitespace-nowrap">${p.amount}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{p.status}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{p.paymentMethod}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
