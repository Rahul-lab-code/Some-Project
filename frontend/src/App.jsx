import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './components/AuthProvider';
import Layout from './components/Layout';
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
          <Route path="/properties" element={<PropertiesContainer />} />
          <Route path="/bookings" element={<BookingsContainer />} />
          <Route path="/calendar" element={<CalendarContainer />} />
          <Route path="/reviews" element={<ReviewsContainer />} />
          <Route path="/payments" element={<PaymentsContainer />} />
          <Route path="/disputes" element={<DisputesContainer />} />
          <Route path="/maintenance" element={<MaintenanceContainer />} />
          <Route path="/users" element={<UsersContainer />} />
          <Route path="/analytics" element={<AnalyticsContainer />} />
        </Route>
      </Routes>
    </AuthProvider>
  );
}

export default AppRoutes;
