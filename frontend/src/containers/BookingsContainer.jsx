import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function BookingsContainer() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiService.getBookings()
      .then(res => setData(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">Bookings</h1>
      {loading ? <p>Loading...</p> : (
        <div className="bg-white shadow rounded-lg overflow-hidden border border-gray-100">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Property</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Dates</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {data.length === 0 ? (
                <tr><td colSpan="4" className="px-6 py-4 text-center text-gray-500">No bookings found.</td></tr>
              ) : data.map((b, i) => (
                <tr key={i}>
                  <td className="px-6 py-4 whitespace-nowrap">{b.id}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{b.propertyId}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{b.status}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{b.startDate} - {b.endDate}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
