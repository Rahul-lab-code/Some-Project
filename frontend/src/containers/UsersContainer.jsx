import { useEffect, useState } from 'react';
import apiService from '../api/apiService';

export default function UsersContainer() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiService.getUsers()
      .then(res => setData(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">Users Admin</h1>
      {loading ? <p>Loading...</p> : (
        <div className="bg-white shadow rounded-lg overflow-hidden border border-gray-100">
           <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Role</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {data.length === 0 ? (
                <tr><td colSpan="4" className="px-6 py-4 text-center text-gray-500">No users found.</td></tr>
              ) : data.map((u, i) => (
                <tr key={i}>
                  <td className="px-6 py-4 whitespace-nowrap">{u.id}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{u.fullName || `${u.firstName} ${u.lastName}`}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{u.email}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                      {u.role}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
