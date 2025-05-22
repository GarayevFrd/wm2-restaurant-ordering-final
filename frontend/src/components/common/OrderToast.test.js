import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import OrderToast from './OrderToast';

describe('OrderToast Component', () => {
  const mockOrder = {
    id: 123,
    status: 'CREATED'
  };
  
  const mockOnClose = jest.fn();
  
  beforeEach(() => {
    mockOnClose.mockClear();
  });
  
  test('renders nothing when order is null', () => {
    const { container } = render(<OrderToast order={null} show={true} onClose={mockOnClose} />);
    expect(container.firstChild).toBeNull();
  });
  
  test('renders with correct order ID', () => {
    render(<OrderToast order={mockOrder} show={true} onClose={mockOnClose} />);
    expect(screen.getByText('Order #123')).toBeInTheDocument();
  });
  
  test('does not render when show is false', () => {
    render(<OrderToast order={mockOrder} show={false} onClose={mockOnClose} />);
    expect(screen.queryByText('Order #123')).not.toBeVisible();
  });
  
  test('displays correct message for CREATED status', () => {
    render(<OrderToast order={mockOrder} show={true} onClose={mockOnClose} />);
    expect(screen.getByText('Your order has been received!')).toBeInTheDocument();
  });
  
  test('displays correct message for IN_PREPARATION status', () => {
    const order = { ...mockOrder, status: 'IN_PREPARATION' };
    render(<OrderToast order={order} show={true} onClose={mockOnClose} />);
    expect(screen.getByText('Your order is being prepared in the kitchen.')).toBeInTheDocument();
  });
  
  test('displays correct message for READY status', () => {
    const order = { ...mockOrder, status: 'READY' };
    render(<OrderToast order={order} show={true} onClose={mockOnClose} />);
    expect(screen.getByText('Your order is ready and will be served shortly!')).toBeInTheDocument();
  });
  
  test('displays correct message for DELIVERED status', () => {
    const order = { ...mockOrder, status: 'DELIVERED' };
    render(<OrderToast order={order} show={true} onClose={mockOnClose} />);
    expect(screen.getByText('Your order has been delivered. Enjoy your meal!')).toBeInTheDocument();
  });
  
  test('displays correct message for CANCELLED status', () => {
    const order = { ...mockOrder, status: 'CANCELLED' };
    render(<OrderToast order={order} show={true} onClose={mockOnClose} />);
    expect(screen.getByText('Your order has been cancelled.')).toBeInTheDocument();
  });
  
  test('displays default message for unknown status', () => {
    const order = { ...mockOrder, status: 'UNKNOWN_STATUS' };
    render(<OrderToast order={order} show={true} onClose={mockOnClose} />);
    expect(screen.getByText('Order status updated to: UNKNOWN_STATUS')).toBeInTheDocument();
  });
  
  test('calls onClose when close button is clicked', () => {
    render(<OrderToast order={mockOrder} show={true} onClose={mockOnClose} />);
    const closeButton = screen.getByLabelText('Close');
    fireEvent.click(closeButton);
    expect(mockOnClose).toHaveBeenCalledTimes(1);
  });
});