import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './components/AuthProvider';
import Layout from './components/Layout';
import RoleRoute from './components/RoleRoute';
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

        <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
          <Route path="/" element={<HomeContainer />} />
          <Route path="/properties" element={<RoleRoute allowedRoles={['HOST', 'ADMIN', 'GUEST']}><PropertiesContainer /></RoleRoute>} />
          <Route path="/bookings" element={<RoleRoute allowedRoles={['HOST', 'ADMIN', 'GUEST']}><BookingsContainer /></RoleRoute>} />
          <Route path="/calendar" element={<RoleRoute allowedRoles={['HOST', 'ADMIN']}><CalendarContainer /></RoleRoute>} />
          <Route path="/reviews" element={<RoleRoute allowedRoles={['HOST', 'ADMIN', 'GUEST']}><ReviewsContainer /></RoleRoute>} />
          <Route path="/payments" element={<RoleRoute allowedRoles={['HOST', 'ADMIN', 'GUEST']}><PaymentsContainer /></RoleRoute>} />
          <Route path="/disputes" element={<RoleRoute allowedRoles={['HOST', 'ADMIN', 'GUEST']}><DisputesContainer /></RoleRoute>} />
          <Route path="/maintenance" element={<RoleRoute allowedRoles={['HOST', 'ADMIN', 'GUEST']}><MaintenanceContainer /></RoleRoute>} />
          <Route path="/users" element={<RoleRoute allowedRoles={['ADMIN']}><UsersContainer /></RoleRoute>} />
          <Route path="/analytics" element={<RoleRoute allowedRoles={['ADMIN']}><AnalyticsContainer /></RoleRoute>} />
        </Route>
      </Routes>
    </AuthProvider>
  );
}

export default AppRoutes;
