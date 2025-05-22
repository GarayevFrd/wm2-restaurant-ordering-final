import React, { useState, useEffect } from 'react';
import { Card, Button, Row, Col, Badge, Alert } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import MenuService from '../../services/MenuService';

const CustomerMenu = () => {
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
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

  const handlePlaceOrder = () => {
    navigate(`/order/${tableId}`);
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

  if (error) {
    return <Alert variant="danger" className="mt-3">{error}</Alert>;
  }

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Menu for Table {tableId}</h1>
      
      {menuItems.length === 0 ? (
        <Alert variant="info">No menu items available at the moment.</Alert>
      ) : (
        <>
          {renderMenuItemsByCategory()}
          
          <div className="text-center mt-4 mb-5">
            <Button 
              variant="primary" 
              size="lg" 
              onClick={handlePlaceOrder}
            >
              Place an Order
            </Button>
          </div>
        </>
      )}
    </div>
  );
};

export default CustomerMenu;