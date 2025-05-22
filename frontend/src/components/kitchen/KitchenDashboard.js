import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Badge, Alert, Spinner } from 'react-bootstrap';
import OrderService from '../../services/OrderService';

const KitchenDashboard = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updateStatus, setUpdateStatus] = useState({ orderId: null, loading: false });

  useEffect(() => {
    fetchOrders();

    const interval = setInterval(fetchOrders, 30000);
    return () => clearInterval(interval);
  }, []);

  const fetchOrders = async () => {
    try {
      const response = await OrderService.getAllOrders();

      const filteredOrders = response.data.filter(order => 
        ['CREATED', 'IN_PREPARATION', 'READY'].includes(order.status)
      );
      setOrders(filteredOrders);
      setLoading(false);
    } catch (error) {
      setError('Failed to load orders. Please try again later.');
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (orderId, newStatus) => {
    setUpdateStatus({ orderId, loading: true });
    try {
      await OrderService.updateOrderStatus(orderId, newStatus);

      fetchOrders();
    } catch (error) {
      setError(`Failed to update order status. ${error.message}`);
    } finally {
      setUpdateStatus({ orderId: null, loading: false });
    }
  };

  const getStatusBadgeVariant = (status) => {
    switch (status) {
      case 'CREATED': return 'info';
      case 'IN_PREPARATION': return 'warning';
      case 'READY': return 'success';
      case 'DELIVERED': return 'primary';
      case 'CANCELLED': return 'danger';
      default: return 'secondary';
    }
  };

  const renderActionButtons = (order) => {
    switch (order.status) {
      case 'CREATED':
        return (
          <Button 
            variant="warning" 
            size="sm" 
            onClick={() => handleUpdateStatus(order.id, 'IN_PREPARATION')}
            disabled={updateStatus.orderId === order.id && updateStatus.loading}
          >
            {updateStatus.orderId === order.id && updateStatus.loading ? (
              <Spinner as="span" animation="border" size="sm" />
            ) : (
              'Start Preparation'
            )}
          </Button>
        );
      case 'IN_PREPARATION':
        return (
          <Button 
            variant="success" 
            size="sm" 
            onClick={() => handleUpdateStatus(order.id, 'READY')}
            disabled={updateStatus.orderId === order.id && updateStatus.loading}
          >
            {updateStatus.orderId === order.id && updateStatus.loading ? (
              <Spinner as="span" animation="border" size="sm" />
            ) : (
              'Mark as Ready'
            )}
          </Button>
        );
      case 'READY':
        return (
          <Badge bg="success">Ready for Delivery</Badge>
        );
      default:
        return null;
    }
  };

  if (loading) {
    return (
      <div className="text-center mt-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </div>
    );
  }

  if (error) {
    return <Alert variant="danger" className="mt-3">{error}</Alert>;
  }

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Kitchen Dashboard</h1>

      {orders.length === 0 ? (
        <Alert variant="info">No orders to prepare at the moment.</Alert>
      ) : (
        <Card>
          <Card.Header>
            <div className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0">Current Orders</h5>
              <Button variant="outline-primary" size="sm" onClick={fetchOrders}>
                Refresh
              </Button>
            </div>
          </Card.Header>
          <Card.Body>
            <Table responsive striped hover>
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Table</th>
                  <th>Items</th>
                  <th>Status</th>
                  <th>Created At</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(order => (
                  <tr key={order.id}>
                    <td>#{order.id}</td>
                    <td>{order.tableId || 'N/A'}</td>
                    <td>
                      {order.items && order.items.map(item => (
                        <div key={item.menuItemId}>
                          {item.quantity}x {item.itemName}
                        </div>
                      ))}
                    </td>
                    <td>
                      <Badge bg={getStatusBadgeVariant(order.status)}>
                        {order.status.replace('_', ' ')}
                      </Badge>
                    </td>
                    <td>{new Date(order.createdAt).toLocaleString()}</td>
                    <td>{renderActionButtons(order)}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </Card.Body>
        </Card>
      )}
    </div>
  );
};

export default KitchenDashboard;
