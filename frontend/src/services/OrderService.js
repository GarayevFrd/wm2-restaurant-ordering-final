import axios from 'axios';
import AuthService from './AuthService';

class OrderService {

  placeOrder(order) {

    const orderDTO = {
      tableId: order.tableId,
      items: order.items.map(item => ({
        menuItemId: item.menuItemId,
        quantity: item.quantity
      }))
    };

    return axios.post('/customer/order', orderDTO)
      .catch(error => {
        console.error('Error placing order:', error);
        throw error;
      });
  }


  updateOrderItem(orderId, updatedOrder) {

    const orderDTO = {
      tableId: updatedOrder.tableId,
      items: updatedOrder.items.map(item => ({
        menuItemId: item.menuItemId,
        quantity: item.quantity
      }))
    };

    return axios.put(`/customer/order/${orderId}/item`, orderDTO)
      .catch(error => {
        console.error('Error updating order item:', error);
        throw error;
      });
  }


  removeOrderItem(orderId, itemId) {
    return axios.delete(`/customer/order/${orderId}/item/${itemId}`)
      .catch(error => {
        console.error('Error removing order item:', error);
        throw error;
      });
  }


  getOrderStatus(orderId) {
    return axios.get(`/customer/order/status/${orderId}`)
      .catch(error => {
        console.error('Error getting order status:', error);
        throw error;
      });
  }


  getCustomerOrder(orderId) {
    return axios.get(`/customer/order/${orderId}`)
      .catch(error => {
        console.error('Error getting customer order:', error);
        throw error;
      });
  }


  getAllOrders() {
    return axios.get('/api/orders', {
      headers: AuthService.getAuthHeader()
    }).catch(error => {
      console.error('Error getting all orders:', error);
      throw error;
    });
  }


  getOrderById(orderId) {
    return axios.get(`/api/orders/${orderId}`, {
      headers: AuthService.getAuthHeader()
    }).catch(error => {
      console.error('Error getting order by ID:', error);
      throw error;
    });
  }


  updateOrderStatus(orderId, status) {
    return axios.put(`/api/orders/${orderId}/status?status=${status}`, {}, {
      headers: AuthService.getAuthHeader()
    }).catch(error => {
      console.error('Error updating order status:', error);
      throw error;
    });
  }


  cancelOrder(orderId) {
    return axios.put(`/api/orders/${orderId}/cancel`, {}, {
      headers: AuthService.getAuthHeader()
    }).catch(error => {
      console.error('Error canceling order:', error);
      throw error;
    });
  }


  getOrdersByTable(tableId) {
    return axios.get(`/api/orders/table/${tableId}`, {
      headers: AuthService.getAuthHeader()
    }).catch(error => {
      console.error('Error getting orders by table:', error);
      throw error;
    });
  }


  subscribeToNotifications(onStatusChange, onError) {
    let eventSource;


    if (AuthService.isAuthenticated()) {

      eventSource = new EventSource('/api/notifications/subscribe');
    } else {

      eventSource = new EventSource('/api/notifications/customer/subscribe');
    }


    eventSource.addEventListener('ORDER_STATUS_CHANGED', (event) => {
      try {
        const orderData = JSON.parse(event.data);
        console.log('Order status changed:', orderData);
        if (onStatusChange) {
          onStatusChange(orderData);
        }
      } catch (error) {
        console.error('Error parsing notification data:', error);
      }
    });


    eventSource.addEventListener('CONNECT', (event) => {
      console.log('Notification connection established:', event.data);
    });


    eventSource.onerror = (error) => {
      console.error('SSE error:', error);
      if (onError) {
        onError(error);
      }

      eventSource.close();
      setTimeout(() => {
        this.subscribeToNotifications(onStatusChange, onError);
      }, 5000);
    };

    return eventSource;
  }
}

export default new OrderService();
