import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function MaintenanceContainer() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiService.getMaintenance()
      .then(res => setData(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">Maintenance</h1>
        <button className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded">Report Issue</button>
      </div>
      {loading ? <p>Loading...</p> : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {data.length === 0 ? <p className="text-gray-500">No maintenance requests found.</p> : data.map((req, i) => (
            <div key={i} className="bg-white p-6 rounded-lg shadow border border-gray-100 border-l-4 border-l-yellow-500">
              <h3 className="font-bold">{req.issueType}</h3>
              <p className="text-gray-600 text-sm mt-2">{req.description}</p>
              <p className="mt-4 text-xs font-semibold uppercase text-gray-500">Status: {req.status}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
