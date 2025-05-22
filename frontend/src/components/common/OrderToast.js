import React, { useState, useEffect } from 'react';
import { Toast, ToastContainer } from 'react-bootstrap';

/**
 * Toast notification component for order status changes
 * @param {Object} props - Component props
 * @param {Object} props.order - The order object with status information
 * @param {boolean} props.show - Whether to show the toast
 * @param {Function} props.onClose - Function to call when toast is closed
 */
const OrderToast = ({ order, show, onClose }) => {
  const [statusMessage, setStatusMessage] = useState('');
  const [bgColor, setBgColor] = useState('success');
  
  useEffect(() => {
    if (order && order.status) {
      switch (order.status) {
        case 'CREATED':
          setStatusMessage('Your order has been received!');
          setBgColor('info');
          break;
        case 'IN_PREPARATION':
          setStatusMessage('Your order is being prepared in the kitchen.');
          setBgColor('primary');
          break;
        case 'READY':
          setStatusMessage('Your order is ready and will be served shortly!');
          setBgColor('success');
          break;
        case 'DELIVERED':
          setStatusMessage('Your order has been delivered. Enjoy your meal!');
          setBgColor('secondary');
          break;
        case 'CANCELLED':
          setStatusMessage('Your order has been cancelled.');
          setBgColor('danger');
          break;
        default:
          setStatusMessage(`Order status updated to: ${order.status}`);
          setBgColor('light');
      }
    }
  }, [order]);
  
  if (!order) return null;
  
  return (
    <ToastContainer position="top-end" className="p-3">
      <Toast 
        show={show} 
        onClose={onClose} 
        delay={5000} 
        autohide 
        bg={bgColor}
        className="text-white"
      >
        <Toast.Header closeButton>
          <strong className="me-auto">Order #{order.id}</strong>
          <small>just now</small>
        </Toast.Header>
        <Toast.Body>
          {statusMessage}
        </Toast.Body>
      </Toast>
    </ToastContainer>
  );
};

export default OrderToast;