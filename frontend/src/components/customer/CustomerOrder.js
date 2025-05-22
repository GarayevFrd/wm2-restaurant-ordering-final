import React, { useState, useEffect } from 'react';
import { Card, Button, Form, Row, Col, ListGroup, Badge, Alert } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import MenuService from '../../services/MenuService';
import OrderService from '../../services/OrderService';

const CustomerOrder = () => {
  const [menuItems, setMenuItems] = useState([]);
  const [selectedItems, setSelectedItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { tableId } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMenu = async () => {
      try {
        const response = await MenuService.getMenuForTable(tableId);
        setMenuItems(response.data);
        setLoading(false);
      } catch (error) {
        setError('Failed to load menu. Please try again later.');
        setLoading(false);
      }
    };

    fetchMenu();
  }, [tableId]);

  const handleAddItem = (menuItem) => {
    const existingItem = selectedItems.find(item => item.menuItemId === menuItem.id);

    if (existingItem) {
      setSelectedItems(selectedItems.map(item => 
        item.menuItemId === menuItem.id 
          ? { ...item, quantity: item.quantity + 1 } 
          : item
      ));
    } else {
      setSelectedItems([...selectedItems, {
        menuItemId: menuItem.id,
        name: menuItem.name,
        price: menuItem.price,
        quantity: 1
      }]);
    }
  };

  const handleRemoveItem = (menuItemId) => {
    const existingItem = selectedItems.find(item => item.menuItemId === menuItemId);

    if (existingItem.quantity > 1) {
      setSelectedItems(selectedItems.map(item => 
        item.menuItemId === menuItemId 
          ? { ...item, quantity: item.quantity - 1 } 
          : item
      ));
    } else {
      setSelectedItems(selectedItems.filter(item => item.menuItemId !== menuItemId));
    }
  };

  const handleQuantityChange = (menuItemId, quantity) => {
    if (quantity <= 0) {
      setSelectedItems(selectedItems.filter(item => item.menuItemId !== menuItemId));
    } else {
      setSelectedItems(selectedItems.map(item => 
        item.menuItemId === menuItemId 
          ? { ...item, quantity: parseInt(quantity) } 
          : item
      ));
    }
  };

  const calculateTotal = () => {
    return selectedItems.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const handlePlaceOrder = async () => {
    if (selectedItems.length === 0) {
      setError('Please select at least one item to place an order.');
      return;
    }

    setSubmitting(true);
    setError('');
    setSuccess('');

    try {

      const orderItems = selectedItems.map(item => ({
        menuItemId: item.menuItemId,
        quantity: item.quantity
      }));

      const order = {
        tableId: parseInt(tableId),
        items: orderItems
      };

      const response = await OrderService.placeOrder(order);
      setSuccess('Order placed successfully!');
      setSelectedItems([]);


      setTimeout(() => {
        navigate(`/order/status/${response.data.id}`);
      }, 2000);
    } catch (error) {
      setError('Failed to place order. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const renderMenuItemsByCategory = () => {
    const categories = [...new Set(menuItems.map(item => item.category))];

    return categories.map(category => (
      <div key={category} className="mb-4">
        <h3 className="mb-3">{formatCategory(category)}</h3>
        <Row>
          {menuItems
            .filter(item => item.category === category)
            .map(item => (
              <Col key={item.id} md={4} className="mb-3">
                <Card className="menu-item-card h-100">
                  <Card.Body>
                    <Card.Title>{item.name}</Card.Title>
                    <Card.Text>{item.description}</Card.Text>
                    <Badge bg="secondary" className="mb-2">${item.price.toFixed(2)}</Badge>
                    <div className="d-grid gap-2 mt-3">
                      <Button 
                        variant="outline-primary" 
                        onClick={() => handleAddItem(item)}
                      >
                        Add to Order
                      </Button>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
        </Row>
      </div>
    ));
  };

  const formatCategory = (category) => {
    return category.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
  };

  if (loading) {
    return <div className="text-center mt-5">Loading menu...</div>;
  }

  if (error && !success) {
    return <Alert variant="danger" className="mt-3">{error}</Alert>;
  }

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Place Order for Table {tableId}</h1>

      {success && (
        <Alert variant="success" className="mb-4">{success}</Alert>
      )}

      <Row>
        <Col md={8}>
          {menuItems.length === 0 ? (
            <Alert variant="info">No menu items available at the moment.</Alert>
          ) : (
            renderMenuItemsByCategory()
          )}
        </Col>

        <Col md={4}>
          <Card className="order-summary">
            <Card.Header as="h5">Your Order</Card.Header>
            <Card.Body>
              {selectedItems.length === 0 ? (
                <p className="text-muted">No items selected yet.</p>
              ) : (
                <ListGroup variant="flush">
                  {selectedItems.map(item => (
                    <ListGroup.Item key={item.menuItemId} className="d-flex justify-content-between align-items-center">
                      <div>
                        <div>{item.name}</div>
                        <div className="text-muted">${item.price.toFixed(2)} each</div>
                      </div>
                      <div className="d-flex align-items-center">
                        <Button 
                          variant="outline-secondary" 
                          size="sm"
                          onClick={() => handleRemoveItem(item.menuItemId)}
                        >
                          -
                        </Button>
                        <Form.Control
                          type="number"
                          min="1"
                          value={item.quantity}
                          onChange={(e) => handleQuantityChange(item.menuItemId, e.target.value)}
                          className="mx-2"
                          style={{ width: '60px' }}
                        />
                        <Button 
                          variant="outline-secondary" 
                          size="sm"
                          onClick={() => handleAddItem({ id: item.menuItemId, name: item.name, price: item.price })}
                        >
                          +
                        </Button>
                      </div>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              )}

              <div className="d-flex justify-content-between mt-3">
                <h5>Total:</h5>
                <h5>${calculateTotal().toFixed(2)}</h5>
              </div>

              <div className="d-grid gap-2 mt-4">
                <Button 
                  variant="success" 
                  size="lg" 
                  onClick={handlePlaceOrder}
                  disabled={selectedItems.length === 0 || submitting}
                >
                  {submitting ? 'Placing Order...' : 'Place Order'}
                </Button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default CustomerOrder;
