import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function AnalyticsContainer() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiService.getAnalytics()
      .then(res => setData(res.data || null))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">System Analytics</h1>
      {loading ? <p>Loading...</p> : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {!data ? <p className="text-gray-500 col-span-4">No analytics data available.</p> : (
             <>
                <div className="bg-white p-6 rounded-lg shadow border border-gray-100">
                  <h3 className="text-sm font-medium text-gray-500">Total Users</h3>
                  <p className="mt-2 text-3xl font-bold text-gray-900">{data.totalUsers || 0}</p>
                </div>
                <div className="bg-white p-6 rounded-lg shadow border border-gray-100">
                  <h3 className="text-sm font-medium text-gray-500">Total Properties</h3>
                  <p className="mt-2 text-3xl font-bold text-gray-900">{data.totalProperties || 0}</p>
                </div>
                <div className="bg-white p-6 rounded-lg shadow border border-gray-100">
                  <h3 className="text-sm font-medium text-gray-500">Active Bookings</h3>
                  <p className="mt-2 text-3xl font-bold text-gray-900">{data.activeBookings || 0}</p>
                </div>
                <div className="bg-white p-6 rounded-lg shadow border border-gray-100">
                  <h3 className="text-sm font-medium text-gray-500">Revenue</h3>
                  <p className="mt-2 text-3xl font-bold text-green-600">${data.revenue || '0.00'}</p>
                </div>
             </>
          )}
        </div>
      )}
    </div>
  );
}
