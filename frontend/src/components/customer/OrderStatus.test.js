import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import OrderStatus from './OrderStatus';
import OrderService from '../../services/OrderService';


jest.mock('react-router-dom', () => ({
  useParams: () => ({ orderId: '123' })
}));


jest.mock('../../services/OrderService', () => ({
  getCustomerOrder: jest.fn(),
  subscribeToNotifications: jest.fn()
}));


jest.mock('../common/OrderToast', () => {
  return function MockOrderToast({ order, show, onClose }) {
    return show ? <div data-testid="order-toast">Order Toast for Order #{order?.id}</div> : null;
  };
});

describe('OrderStatus Component', () => {

  const mockOrder = {
    id: 123,
    tableId: 5,
    status: 'CREATED',
    createdAt: '2023-05-22T10:30:00',
    totalAmount: 25.50,
    items: [
      { menuItemId: 1, itemName: 'Burger', price: 12.50, quantity: 1 },
      { menuItemId: 2, itemName: 'Fries', price: 4.50, quantity: 2 }
    ]
  };


  const mockEventSource = {
    close: jest.fn()
  };
  
  beforeEach(() => {
    jest.clearAllMocks();


    OrderService.getCustomerOrder.mockResolvedValue({ data: mockOrder });


    OrderService.subscribeToNotifications.mockImplementation((onMessage) => {
      return mockEventSource;
    });
  });
  
  test('renders loading state initially', () => {
    render(<OrderStatus />);
    expect(screen.getByText('Loading order details...')).toBeInTheDocument();
  });
  
  test('renders error state when API call fails', async () => {
    OrderService.getCustomerOrder.mockRejectedValue(new Error('API error'));
    
    render(<OrderStatus />);
    
    await waitFor(() => {
      expect(screen.getByText('Failed to load order details. Please try again later.')).toBeInTheDocument();
    });
  });
  
  test('renders order not found when order is null', async () => {
    OrderService.getCustomerOrder.mockResolvedValue({ data: null });
    
    render(<OrderStatus />);
    
    await waitFor(() => {
      expect(screen.getByText('Order not found.')).toBeInTheDocument();
    });
  });
  
  test('renders order details correctly', async () => {
    render(<OrderStatus />);
    
    await waitFor(() => {

      expect(screen.getByText('Order #123')).toBeInTheDocument();


      expect(screen.getByText('Created')).toBeInTheDocument();


      expect(screen.getByText(/Table: 5/)).toBeInTheDocument();
      expect(screen.getByText(/Total: \$25.50/)).toBeInTheDocument();



      expect(screen.getByText('Burger')).toBeInTheDocument();
      expect(screen.getByText('$12.50 each')).toBeInTheDocument();
      expect(screen.getByText('Fries')).toBeInTheDocument();
      expect(screen.getByText('$4.50 each')).toBeInTheDocument();
      expect(screen.getAllByText(/Qty:/)).toHaveLength(2);


      expect(screen.getByText('Your order has been received and is waiting to be processed.')).toBeInTheDocument();
    });
  });
  
  test('subscribes to notifications and cleans up on unmount', async () => {
    const { unmount } = render(<OrderStatus />);
    
    await waitFor(() => {
      expect(OrderService.subscribeToNotifications).toHaveBeenCalled();
    });
    
    unmount();
    expect(mockEventSource.close).toHaveBeenCalled();
  });
  
  test('displays different status badges based on order status', async () => {

    OrderService.getCustomerOrder.mockResolvedValue({ 
      data: { ...mockOrder, status: 'IN_PROGRESS' } 
    });
    
    const { rerender } = render(<OrderStatus />);
    
    await waitFor(() => {
      expect(screen.getByText('In Progress')).toBeInTheDocument();
      expect(screen.getByText('Your order is being prepared in the kitchen.')).toBeInTheDocument();
    });
    

    OrderService.getCustomerOrder.mockResolvedValue({ 
      data: { ...mockOrder, status: 'READY' } 
    });
    
    rerender(<OrderStatus />);
    
    await waitFor(() => {
      expect(screen.getByText('Ready')).toBeInTheDocument();
      expect(screen.getByText('Your order is ready and will be served shortly.')).toBeInTheDocument();
    });
    

    OrderService.getCustomerOrder.mockResolvedValue({ 
      data: { ...mockOrder, status: 'DELIVERED' } 
    });
    
    rerender(<OrderStatus />);
    
    await waitFor(() => {
      expect(screen.getByText('Delivered')).toBeInTheDocument();
      expect(screen.getByText('Your order has been delivered. Enjoy your meal!')).toBeInTheDocument();
    });
    

    OrderService.getCustomerOrder.mockResolvedValue({ 
      data: { ...mockOrder, status: 'CANCELLED' } 
    });
    
    rerender(<OrderStatus />);
    
    await waitFor(() => {
      expect(screen.getByText('Cancelled')).toBeInTheDocument();
      expect(screen.getByText('Your order has been cancelled.')).toBeInTheDocument();
    });
  });
});