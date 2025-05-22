import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Badge, Alert, Spinner, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import OrderService from '../../services/OrderService';
import TableService from '../../services/TableService';

const ManagerDashboard = () => {
  const [orders, setOrders] = useState([]);
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [stats, setStats] = useState({
    totalOrders: 0,
    activeOrders: 0,
    completedOrders: 0,
    cancelledOrders: 0,
    totalRevenue: 0
  });

  useEffect(() => {
    fetchData();

    const interval = setInterval(fetchData, 60000);
    return () => clearInterval(interval);
  }, []);

  const fetchData = async () => {
    try {

      const [ordersResponse, tablesResponse] = await Promise.all([
        OrderService.getAllOrders(),
        TableService.getAllTables()
      ]);

      const allOrders = ordersResponse.data;
      setOrders(allOrders);
      setTables(tablesResponse.data);


      calculateStats(allOrders);

      setLoading(false);
    } catch (error) {
      setError('Failed to load data. Please try again later.');
      setLoading(false);
    }
  };

  const calculateStats = (allOrders) => {
    const activeOrders = allOrders.filter(order => 
      ['CREATED', 'IN_PREPARATION', 'READY'].includes(order.status)
    ).length;

    const completedOrders = allOrders.filter(order => 
      order.status === 'DELIVERED'
    ).length;

    const cancelledOrders = allOrders.filter(order => 
      order.status === 'CANCELLED'
    ).length;

    const totalRevenue = allOrders
      .filter(order => order.status === 'DELIVERED')
      .reduce((sum, order) => sum + (order.totalAmount || 0), 0);

    setStats({
      totalOrders: allOrders.length,
      activeOrders,
      completedOrders,
      cancelledOrders,
      totalRevenue
    });
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


  const recentOrders = [...orders]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 5);

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Manager Dashboard</h1>

      <Row className="mb-4">
        <Col md={3}>
          <Card className="text-center h-100">
            <Card.Body>
              <Card.Title>Total Orders</Card.Title>
              <h2>{stats.totalOrders}</h2>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center h-100 bg-warning text-white">
            <Card.Body>
              <Card.Title>Active Orders</Card.Title>
              <h2>{stats.activeOrders}</h2>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center h-100 bg-success text-white">
            <Card.Body>
              <Card.Title>Completed Orders</Card.Title>
              <h2>{stats.completedOrders}</h2>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center h-100 bg-primary text-white">
            <Card.Body>
              <Card.Title>Total Revenue</Card.Title>
              <h2>${stats.totalRevenue.toFixed(2)}</h2>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="mb-4">
        <Col md={6}>
          <Card className="h-100">
            <Card.Header>
              <div className="d-flex justify-content-between align-items-center">
                <h5 className="mb-0">Recent Orders</h5>
                <Button 
                  variant="outline-primary" 
                  size="sm" 
                  onClick={fetchData}
                >
                  Refresh
                </Button>
              </div>
            </Card.Header>
            <Card.Body>
              {recentOrders.length === 0 ? (
                <Alert variant="info">No orders yet.</Alert>
              ) : (
                <div>
                  {recentOrders.map(order => (
                    <div key={order.id} className="mb-3 p-2 border-bottom">
                      <div className="d-flex justify-content-between">
                        <div>
                          <strong>Order #{order.id}</strong> - Table {order.tableId || 'N/A'}
                        </div>
                        <Badge bg={getStatusBadgeVariant(order.status)}>
                          {order.status.replace('_', ' ')}
                        </Badge>
                      </div>
                      <div className="small text-muted">
                        {new Date(order.createdAt).toLocaleString()}
                      </div>
                      <div>
                        Total: ${order.totalAmount ? order.totalAmount.toFixed(2) : '0.00'}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
        <Col md={6}>
          <Card className="h-100">
            <Card.Header>
              <h5 className="mb-0">Tables Status</h5>
            </Card.Header>
            <Card.Body>
              {tables.length === 0 ? (
                <Alert variant="info">No tables available.</Alert>
              ) : (
                <Row>
                  {tables.map(table => (
                    <Col key={table.id} md={4} className="mb-3">
                      <Card>
                        <Card.Body className="p-2 text-center">
                          <div>Table #{table.id}</div>
                          {orders.filter(order => order.tableId === table.id && 
                            ['CREATED', 'IN_PREPARATION', 'READY'].includes(order.status)).length > 0 ? (
                            <Badge bg="warning">Occupied</Badge>
                          ) : (
                            <Badge bg="success">Available</Badge>
                          )}
                        </Card.Body>
                      </Card>
                    </Col>
                  ))}
                </Row>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="mb-4">
        <Col md={6}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title>Menu Management</Card.Title>
              <Card.Text>
                Add, edit, or remove menu items from your restaurant's menu.
              </Card.Text>
              <Link to="/manager/menu">
                <Button variant="primary">Manage Menu</Button>
              </Link>
            </Card.Body>
          </Card>
        </Col>
        <Col md={6}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title>Table Management</Card.Title>
              <Card.Text>
                Manage tables, generate QR codes, and view table status.
              </Card.Text>
              <Link to="/manager/tables">
                <Button variant="primary">Manage Tables</Button>
              </Link>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ManagerDashboard;
