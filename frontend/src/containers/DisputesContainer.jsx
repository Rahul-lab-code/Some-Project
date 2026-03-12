import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function DisputesContainer() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiService.getDisputes()
      .then(res => setData(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">Disputes</h1>
      {loading ? <p>Loading...</p> : (
        <div className="bg-white shadow rounded-lg p-6 border border-gray-100">
          {data.length === 0 ? <p className="text-gray-500">No active disputes.</p> : (
            <ul className="divide-y divide-gray-200">
              {data.map((d, i) => (
                <li key={i} className="py-4">
                  <p className="font-semibold">{d.title}</p>
                  <p className="text-gray-600 text-sm mt-1">{d.description}</p>
                  <span className="inline-block mt-2 px-2 py-1 text-xs bg-red-100 text-red-800 rounded">{d.status}</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}
