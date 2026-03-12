import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function CalendarContainer() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [propertyId, setPropertyId] = useState(1); // Default mock property ID

  useEffect(() => {
    setLoading(true);
    apiService.getCalendar(propertyId)
      .then(res => setData(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [propertyId]);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">Calendar Availability</h1>
      </div>
      {loading ? <p>Loading...</p> : (
        <div className="bg-white shadow rounded-lg overflow-hidden border border-gray-100 p-6">
          {data.length === 0 ? <p className="text-gray-500">No availability data found for Property {propertyId}.</p> : (
             <ul className="divide-y divide-gray-200">
               {data.map((c, i) => (
                 <li key={i} className="py-4 flex">
                   <div className="ml-3">
                     <p className="text-sm font-medium text-gray-900">{c.date} - {c.status}</p>
                   </div>
                 </li>
               ))}
             </ul>
          )}
        </div>
      )}
    </div>
  );
}
