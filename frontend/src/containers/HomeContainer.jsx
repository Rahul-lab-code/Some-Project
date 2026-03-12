import { useAuth } from '../components/AuthProvider';

export default function HomeContainer() {
  const { user } = useAuth();
  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
      <div className="bg-white p-6 rounded-lg shadow border border-gray-100">
        <h2 className="text-xl font-semibold mb-2">Welcome back, {user?.fullName || 'User'}!</h2>
        <p className="text-gray-600">You are logged in as a {user?.role}.</p>
      </div>
    </div>
  );
}
