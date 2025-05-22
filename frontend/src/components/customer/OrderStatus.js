import React, { useState, useEffect, useRef } from 'react';
import { Card, Badge, Alert, ListGroup } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import OrderService from '../../services/OrderService';
import OrderToast from '../common/OrderToast';

const OrderStatus = () => {
  const [order, setOrder] = useState(null);
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showToast, setShowToast] = useState(false);
  const [toastOrder, setToastOrder] = useState(null);
  const { orderId } = useParams();
  const eventSourceRef = useRef(null);

  useEffect(() => {
    const fetchOrderDetails = async () => {
      try {
        const orderResponse = await OrderService.getCustomerOrder(orderId);
        setOrder(orderResponse.data);
        setStatus(orderResponse.data.status);
        setLoading(false);
      } catch (error) {
        setError('Failed to load order details. Please try again later.');
        setLoading(false);
      }
    };

    fetchOrderDetails();


    eventSourceRef.current = OrderService.subscribeToNotifications(
      (updatedOrder) => {

        if (updatedOrder.id === parseInt(orderId)) {
          console.log('Received update for our order:', updatedOrder);

          if (updatedOrder.status !== status) {
            setStatus(updatedOrder.status);


            setToastOrder(updatedOrder);
            setShowToast(true);

            if (order) {
              setOrder({
                ...order,
                status: updatedOrder.status
              });
            }
          }
        }
      },
      (error) => {
        console.error('SSE error:', error);
      }
    );


    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
    };
  }, [orderId, status, order]);

  const getStatusBadge = (status) => {
    switch (status) {
      case 'CREATED':
        return <Badge bg="info" className="status-badge">Created</Badge>;
      case 'IN_PROGRESS':
        return <Badge bg="primary" className="status-badge">In Progress</Badge>;
      case 'READY':
        return <Badge bg="success" className="status-badge">Ready</Badge>;
      case 'DELIVERED':
        return <Badge bg="secondary" className="status-badge">Delivered</Badge>;
      case 'CANCELLED':
        return <Badge bg="danger" className="status-badge">Cancelled</Badge>;
      default:
        return <Badge bg="light" text="dark" className="status-badge">{status}</Badge>;
    }
  };

  if (loading) {
    return <div className="text-center mt-5">Loading order details...</div>;
  }

  if (error) {
    return <Alert variant="danger" className="mt-3">{error}</Alert>;
  }

  if (!order) {
    return <Alert variant="warning" className="mt-3">Order not found.</Alert>;
  }

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Order Status</h1>


      <OrderToast 
        order={toastOrder} 
        show={showToast} 
        onClose={() => setShowToast(false)} 
      />

      <Card className="mb-4">
        <Card.Header as="h5" className="d-flex justify-content-between align-items-center">
          <span>Order #{orderId}</span>
          {getStatusBadge(status)}
        </Card.Header>
        <Card.Body>
          <Card.Text>
            <strong>Table:</strong> {order.tableId || 'N/A'}<br />
            <strong>Ordered at:</strong> {new Date(order.createdAt).toLocaleString()}<br />
            <strong>Total:</strong> ${order.totalAmount?.toFixed(2) || '0.00'}
          </Card.Text>

          <h5 className="mt-4">Order Items</h5>
          <ListGroup variant="flush">
            {order.items?.map(item => (
              <ListGroup.Item key={item.menuItemId} className="d-flex justify-content-between align-items-center">
                <div>
                  <div>{item.itemName}</div>
                  <div className="text-muted">${item.price.toFixed(2)} each</div>
                </div>
                <Badge bg="secondary">Qty: {item.quantity}</Badge>
              </ListGroup.Item>
            ))}
          </ListGroup>
        </Card.Body>
        <Card.Footer className="text-muted">
          {status === 'CREATED' && 'Your order has been received and is waiting to be processed.'}
          {status === 'IN_PROGRESS' && 'Your order is being prepared in the kitchen.'}
          {status === 'READY' && 'Your order is ready and will be served shortly.'}
          {status === 'DELIVERED' && 'Your order has been delivered. Enjoy your meal!'}
          {status === 'CANCELLED' && 'Your order has been cancelled.'}
        </Card.Footer>
      </Card>
    </div>
  );
};

export default OrderStatus;
