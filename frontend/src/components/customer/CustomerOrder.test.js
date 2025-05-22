import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import CustomerOrder from './CustomerOrder';
import MenuService from '../../services/MenuService';
import OrderService from '../../services/OrderService';


jest.mock('react-router-dom', () => ({
  useParams: () => ({ tableId: '5' }),
  useNavigate: () => jest.fn()
}));


jest.mock('../../services/MenuService', () => ({
  getMenuForTable: jest.fn()
}));

jest.mock('../../services/OrderService', () => ({
  placeOrder: jest.fn()
}));

describe('CustomerOrder Component', () => {

  const mockMenuItems = [
    {
      id: 1,
      name: 'Burger',
      description: 'Delicious burger with cheese',
      price: 12.50,
      category: 'MAIN_COURSE'
    },
    {
      id: 2,
      name: 'Fries',
      description: 'Crispy french fries',
      price: 4.50,
      category: 'SIDE_DISH'
    },
    {
      id: 3,
      name: 'Soda',
      description: 'Refreshing soda',
      price: 2.50,
      category: 'BEVERAGE'
    }
  ];

  const mockOrderResponse = {
    data: {
      id: 123,
      tableId: 5,
      status: 'CREATED',
      items: [
        { menuItemId: 1, quantity: 1 },
        { menuItemId: 2, quantity: 2 }
      ]
    }
  };

  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();

    // Mock successful API calls
    MenuService.getMenuForTable.mockResolvedValue({ data: mockMenuItems });
    OrderService.placeOrder.mockResolvedValue(mockOrderResponse);
  });

  test('renders loading state initially', () => {
    render(<CustomerOrder />);
    expect(screen.getByText('Loading menu...')).toBeInTheDocument();
  });

  test('renders error state when API call fails', async () => {
    MenuService.getMenuForTable.mockRejectedValue(new Error('API error'));

    render(<CustomerOrder />);

    await waitFor(() => {
      expect(screen.getByText('Failed to load menu. Please try again later.')).toBeInTheDocument();
    });
  });

  test('renders menu items grouped by category', async () => {
    render(<CustomerOrder />);

    await waitFor(() => {

      expect(screen.getByText('Place Order for Table 5')).toBeInTheDocument();


      expect(screen.getByText('Main Course')).toBeInTheDocument();
      expect(screen.getByText('Side Dish')).toBeInTheDocument();
      expect(screen.getByText('Beverage')).toBeInTheDocument();


      expect(screen.getByText('Burger')).toBeInTheDocument();
      expect(screen.getByText('Delicious burger with cheese')).toBeInTheDocument();
      expect(screen.getByText('$12.50')).toBeInTheDocument();

      expect(screen.getByText('Fries')).toBeInTheDocument();
      expect(screen.getByText('Crispy french fries')).toBeInTheDocument();
      expect(screen.getByText('$4.50')).toBeInTheDocument();

      expect(screen.getByText('Soda')).toBeInTheDocument();
      expect(screen.getByText('Refreshing soda')).toBeInTheDocument();
      expect(screen.getByText('$2.50')).toBeInTheDocument();



      const addButtons = screen.getAllByText('Add to Order');
      expect(addButtons).toHaveLength(3);
    });
  });

  test('allows adding items to the order', async () => {
    render(<CustomerOrder />);

    await waitFor(() => {
      expect(screen.getByText('Burger')).toBeInTheDocument();
    });


    expect(screen.getByText('No items selected yet.')).toBeInTheDocument();


    const addButtons = screen.getAllByText('Add to Order');
    fireEvent.click(addButtons[0]); // Burger


    expect(screen.queryByText('No items selected yet.')).not.toBeInTheDocument();
    expect(screen.getByText('Burger')).toBeInTheDocument();
    expect(screen.getByText('$12.50 each')).toBeInTheDocument();


    expect(screen.getByText('Total:')).toBeInTheDocument();
    expect(screen.getByText('$12.50')).toBeInTheDocument();
  });

  test('allows changing item quantity', async () => {
    render(<CustomerOrder />);

    await waitFor(() => {
      expect(screen.getByText('Burger')).toBeInTheDocument();
    });


    const addButtons = screen.getAllByText('Add to Order');
    fireEvent.click(addButtons[0]); // Burger


    const quantityInput = screen.getByRole('spinbutton');
    expect(quantityInput.value).toBe('1');


    fireEvent.change(quantityInput, { target: { value: '3' } });


    expect(quantityInput.value).toBe('3');


    expect(screen.getByText('$37.50')).toBeInTheDocument();
  });

  test('allows removing items from the order', async () => {
    render(<CustomerOrder />);

    await waitFor(() => {
      expect(screen.getByText('Burger')).toBeInTheDocument();
    });


    const addButtons = screen.getAllByText('Add to Order');
    fireEvent.click(addButtons[0]); // Burger


    expect(screen.queryByText('No items selected yet.')).not.toBeInTheDocument();


    const removeButton = screen.getByText('-');
    fireEvent.click(removeButton);


    expect(screen.getByText('No items selected yet.')).toBeInTheDocument();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  test('places an order successfully', async () => {
    const navigateMock = jest.fn();
    jest.spyOn(require('react-router-dom'), 'useNavigate').mockImplementation(() => navigateMock);

    render(<CustomerOrder />);

    await waitFor(() => {
      expect(screen.getByText('Burger')).toBeInTheDocument();
    });


    const addButtons = screen.getAllByText('Add to Order');
    fireEvent.click(addButtons[0]); // Burger
    fireEvent.click(addButtons[1]); // Fries
    fireEvent.click(addButtons[1]); // Fries again


    const placeOrderButton = screen.getByText('Place Order');
    fireEvent.click(placeOrderButton);


    expect(screen.getByText('Placing Order...')).toBeInTheDocument();

    await waitFor(() => {

      expect(screen.getByText('Order placed successfully!')).toBeInTheDocument();


      expect(OrderService.placeOrder).toHaveBeenCalledWith({
        tableId: 5,
        items: [
          { menuItemId: 1, quantity: 1 },
          { menuItemId: 2, quantity: 2 }
        ]
      });


      jest.advanceTimersByTime(2000);
      expect(navigateMock).toHaveBeenCalledWith('/order/status/123');
    });
  });

  test('handles order placement failure', async () => {
    OrderService.placeOrder.mockRejectedValue(new Error('API error'));

    render(<CustomerOrder />);

    await waitFor(() => {
      expect(screen.getByText('Burger')).toBeInTheDocument();
    });


    const addButtons = screen.getAllByText('Add to Order');
    fireEvent.click(addButtons[0]); // Burger


    const placeOrderButton = screen.getByText('Place Order');
    fireEvent.click(placeOrderButton);

    await waitFor(() => {

      expect(screen.getByText('Failed to place order. Please try again.')).toBeInTheDocument();
    });
  });
});
