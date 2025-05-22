import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import Navigation from './Navigation';
import AuthService from '../services/AuthService';

jest.mock('react-router-dom', () => ({
  Link: ({ children, to }) => <a href={to}>{children}</a>,
  useNavigate: () => jest.fn()
}));

jest.mock('../services/AuthService', () => ({
  logout: jest.fn()
}));

describe('Navigation Component', () => {
  const mockSetCurrentUser = jest.fn();
  
  beforeEach(() => {
    jest.clearAllMocks();
  });
  
  test('renders correctly when user is not logged in', () => {
    render(<Navigation currentUser={null} setCurrentUser={mockSetCurrentUser} />);

    expect(screen.getByText('Restaurant Ordering System')).toBeInTheDocument();

    expect(screen.getByText('Home')).toBeInTheDocument();
    expect(screen.getByText('Login')).toBeInTheDocument();
    expect(screen.getByText('Register')).toBeInTheDocument();

    expect(screen.queryByText('Logout')).not.toBeInTheDocument();
  });
  
  test('renders correctly for MANAGER role', () => {
    const managerUser = { username: 'manager1', role: 'MANAGER' };
    render(<Navigation currentUser={managerUser} setCurrentUser={mockSetCurrentUser} />);

    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Menu Management')).toBeInTheDocument();
    expect(screen.getByText('Table Management')).toBeInTheDocument();

    expect(screen.getByText('Signed in as: manager1 (MANAGER)')).toBeInTheDocument();

    expect(screen.getByText('Logout')).toBeInTheDocument();
  });
  
  test('renders correctly for KITCHEN role', () => {
    const kitchenUser = { username: 'chef1', role: 'KITCHEN' };
    render(<Navigation currentUser={kitchenUser} setCurrentUser={mockSetCurrentUser} />);

    expect(screen.getByText('Kitchen Dashboard')).toBeInTheDocument();

    expect(screen.getByText('Signed in as: chef1 (KITCHEN)')).toBeInTheDocument();

    expect(screen.getByText('Logout')).toBeInTheDocument();
  });
  
  test('renders correctly for WAITER role', () => {
    const waiterUser = { username: 'waiter1', role: 'WAITER' };
    render(<Navigation currentUser={waiterUser} setCurrentUser={mockSetCurrentUser} />);

    expect(screen.getByText('Waiter Dashboard')).toBeInTheDocument();

    expect(screen.getByText('Signed in as: waiter1 (WAITER)')).toBeInTheDocument();

    expect(screen.getByText('Logout')).toBeInTheDocument();
  });
  
  test('logout button calls AuthService.logout and updates state', () => {
    const user = { username: 'user1', role: 'MANAGER' };
    render(<Navigation currentUser={user} setCurrentUser={mockSetCurrentUser} />);

    fireEvent.click(screen.getByText('Logout'));

    expect(AuthService.logout).toHaveBeenCalledTimes(1);

    expect(mockSetCurrentUser).toHaveBeenCalledWith(null);
  });
});