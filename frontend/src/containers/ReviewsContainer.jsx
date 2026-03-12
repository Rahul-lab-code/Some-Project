import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function ReviewsContainer() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [propertyId, setPropertyId] = useState(1);

  useEffect(() => {
    setLoading(true);
    apiService.getReviews(propertyId)
      .then(res => setData(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [propertyId]);

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">Reviews</h1>
      {loading ? <p>Loading...</p> : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {data.length === 0 ? <p className="text-gray-500">No reviews found.</p> : data.map((r, i) => (
            <div key={i} className="bg-white p-6 rounded-lg shadow border border-gray-100">
              <div className="flex items-center mb-4">
                <span className="text-yellow-400 font-bold text-lg">{r.rating} / 5</span>
              </div>
              <p className="text-gray-600">{r.comment}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
