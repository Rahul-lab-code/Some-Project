import { Link, Outlet } from 'react-router-dom';
import { useAuth } from './AuthProvider';

export default function Layout() {
  const { user, logout } = useAuth();

  return (
    <div className="flex h-screen bg-gray-100">
      <aside className="w-64 bg-gray-900 text-white flex flex-col">
        <div className="p-4 text-xl font-bold border-b border-gray-800">
          Rental Platform
        </div>
        <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
          <Link to="/" className="block p-2 rounded hover:bg-gray-800">Dashboard</Link>
          <Link to="/properties" className="block p-2 rounded hover:bg-gray-800">Properties</Link>
          <Link to="/bookings" className="block p-2 rounded hover:bg-gray-800">Bookings</Link>
          <Link to="/calendar" className="block p-2 rounded hover:bg-gray-800">Calendar</Link>
          <Link to="/reviews" className="block p-2 rounded hover:bg-gray-800">Reviews</Link>
          <Link to="/payments" className="block p-2 rounded hover:bg-gray-800">Payments</Link>
          <Link to="/disputes" className="block p-2 rounded hover:bg-gray-800">Disputes</Link>
          <Link to="/maintenance" className="block p-2 rounded hover:bg-gray-800">Maintenance</Link>
          {user?.role === 'ADMIN' && (
            <>
              <Link to="/users" className="block p-2 rounded hover:bg-gray-800 text-purple-400">Users (Admin)</Link>
              <Link to="/analytics" className="block p-2 rounded hover:bg-gray-800 text-purple-400">Analytics (Admin)</Link>
            </>
          )}
        </nav>
        <div className="p-4 border-t border-gray-800">
          <div className="mb-2 text-sm text-gray-400">{user?.email}</div>
          <button
            onClick={logout}
            className="w-full bg-red-600 hover:bg-red-700 text-white p-2 rounded transition-colors"
          >
            Logout
          </button>
        </div>
      </aside>

      <main className="flex-1 overflow-y-auto p-8">
        <Outlet />
      </main>
    </div>
  );
}
