import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './components/AuthProvider';
import HomeContainer from './containers/HomeContainer';
import LoginContainer from './containers/LoginContainer';
import RegisterContainer from './containers/RegisterContainer';
import PropertiesContainer from './containers/PropertiesContainer';
import BookingsContainer from './containers/BookingsContainer';
import CalendarContainer from './containers/CalendarContainer';
import ReviewsContainer from './containers/ReviewsContainer';
import PaymentsContainer from './containers/PaymentsContainer';
import DisputesContainer from './containers/DisputesContainer';
import MaintenanceContainer from './containers/MaintenanceContainer';
import UsersContainer from './containers/UsersContainer';
import AnalyticsContainer from './containers/AnalyticsContainer';

const ProtectedRoute = ({ children }) => {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function AppRoutes() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginContainer />} />
        <Route path="/register" element={<RegisterContainer />} />
        <Route path="/" element={<ProtectedRoute><HomeContainer /></ProtectedRoute>} />
        <Route path="/properties" element={<ProtectedRoute><PropertiesContainer /></ProtectedRoute>} />
        <Route path="/bookings" element={<ProtectedRoute><BookingsContainer /></ProtectedRoute>} />
        <Route path="/calendar" element={<ProtectedRoute><CalendarContainer /></ProtectedRoute>} />
        <Route path="/reviews" element={<ProtectedRoute><ReviewsContainer /></ProtectedRoute>} />
        <Route path="/payments" element={<ProtectedRoute><PaymentsContainer /></ProtectedRoute>} />
        <Route path="/disputes" element={<ProtectedRoute><DisputesContainer /></ProtectedRoute>} />
        <Route path="/maintenance" element={<ProtectedRoute><MaintenanceContainer /></ProtectedRoute>} />
        <Route path="/users" element={<ProtectedRoute><UsersContainer /></ProtectedRoute>} />
        <Route path="/analytics" element={<ProtectedRoute><AnalyticsContainer /></ProtectedRoute>} />
      </Routes>
    </AuthProvider>
  );
}

export default AppRoutes;
