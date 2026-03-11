import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import AppRoutes from './App';

describe('App', () => {
  it('renders login route initially if unauthenticated', () => {
    // Initial route is '/', but ProtectedRoute redirects to '/login'
    render(
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    );
    expect(screen.getAllByText('Login')[0]).toBeInTheDocument();
  });
});
