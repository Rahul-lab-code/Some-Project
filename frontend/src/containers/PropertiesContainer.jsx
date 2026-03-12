import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function PropertiesContainer() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiService.getProperties()
      .then(res => setData(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">Properties</h1>
        <button className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded">Add Property</button>
      </div>
      {loading ? <p>Loading...</p> : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {data.length === 0 ? <p className="text-gray-500">No properties found.</p> : data.map((prop, i) => (
            <div key={i} className="bg-white p-6 rounded-lg shadow border border-gray-100">
              <h3 className="text-lg font-bold">{prop.title || 'Property Name'}</h3>
              <p className="text-gray-600 mt-2">{prop.description || 'Description'}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
