import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Badge, Alert, Spinner, Tabs, Tab } from 'react-bootstrap';
import OrderService from '../../services/OrderService';
import TableService from '../../services/TableService';

const WaiterDashboard = () => {
  const [orders, setOrders] = useState([]);
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updateStatus, setUpdateStatus] = useState({ orderId: null, loading: false });
  const [activeTab, setActiveTab] = useState('ready');

  useEffect(() => {
    fetchData();

    const interval = setInterval(fetchData, 30000);
    return () => clearInterval(interval);
  }, []);

  const fetchData = async () => {
    try {

      const [ordersResponse, tablesResponse] = await Promise.all([
        OrderService.getAllOrders(),
        TableService.getAllTables()
      ]);

      setOrders(ordersResponse.data);
      setTables(tablesResponse.data);
      setLoading(false);
    } catch (error) {
      setError('Failed to load data. Please try again later.');
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (orderId, newStatus) => {
    setUpdateStatus({ orderId, loading: true });
    try {
      await OrderService.updateOrderStatus(orderId, newStatus);

      fetchData();
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

  const getFilteredOrders = () => {
    switch (activeTab) {
      case 'ready':
        return orders.filter(order => order.status === 'READY');
      case 'active':
        return orders.filter(order => 
          ['CREATED', 'IN_PREPARATION', 'READY'].includes(order.status)
        );
      case 'all':
        return orders;
      default:
        return [];
    }
  };

  const renderActionButtons = (order) => {
    switch (order.status) {
      case 'READY':
        return (
          <Button 
            variant="primary" 
            size="sm" 
            onClick={() => handleUpdateStatus(order.id, 'DELIVERED')}
            disabled={updateStatus.orderId === order.id && updateStatus.loading}
          >
            {updateStatus.orderId === order.id && updateStatus.loading ? (
              <Spinner as="span" animation="border" size="sm" />
            ) : (
              'Mark as Delivered'
            )}
          </Button>
        );
      case 'CREATED':
      case 'IN_PREPARATION':
        return (
          <Badge bg={getStatusBadgeVariant(order.status)}>
            {order.status.replace('_', ' ')}
          </Badge>
        );
      case 'DELIVERED':
        return (
          <Badge bg="primary">Delivered</Badge>
        );
      case 'CANCELLED':
        return (
          <Badge bg="danger">Cancelled</Badge>
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

  const filteredOrders = getFilteredOrders();

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Waiter Dashboard</h1>

      <Tabs
        activeKey={activeTab}
        onSelect={(k) => setActiveTab(k)}
        className="mb-3"
      >
        <Tab eventKey="ready" title="Ready for Delivery">
          {filteredOrders.length === 0 ? (
            <Alert variant="info">No orders ready for delivery at the moment.</Alert>
          ) : (
            <OrdersTable 
              orders={filteredOrders} 
              renderActionButtons={renderActionButtons} 
              getStatusBadgeVariant={getStatusBadgeVariant}
              fetchData={fetchData}
            />
          )}
        </Tab>
        <Tab eventKey="active" title="Active Orders">
          {filteredOrders.length === 0 ? (
            <Alert variant="info">No active orders at the moment.</Alert>
          ) : (
            <OrdersTable 
              orders={filteredOrders} 
              renderActionButtons={renderActionButtons} 
              getStatusBadgeVariant={getStatusBadgeVariant}
              fetchData={fetchData}
            />
          )}
        </Tab>
        <Tab eventKey="all" title="All Orders">
          {filteredOrders.length === 0 ? (
            <Alert variant="info">No orders found.</Alert>
          ) : (
            <OrdersTable 
              orders={filteredOrders} 
              renderActionButtons={renderActionButtons} 
              getStatusBadgeVariant={getStatusBadgeVariant}
              fetchData={fetchData}
            />
          )}
        </Tab>
      </Tabs>

      <h2 className="mt-5 mb-4">Tables</h2>
      {tables.length === 0 ? (
        <Alert variant="info">No tables available.</Alert>
      ) : (
        <div className="row">
          {tables.map(table => (
            <div key={table.id} className="col-md-4 mb-4">
              <Card>
                <Card.Body>
                  <Card.Title>Table #{table.id}</Card.Title>
                  <Card.Text>
                    {orders.filter(order => order.tableId === table.id && 
                      ['CREATED', 'IN_PREPARATION', 'READY'].includes(order.status)).length > 0 ? (
                      <Badge bg="warning">Has Active Orders</Badge>
                    ) : (
                      <Badge bg="success">Available</Badge>
                    )}
                  </Card.Text>
                  <Button 
                    variant="outline-primary" 
                    size="sm"
                    onClick={() => {
                      const tableOrders = orders.filter(order => 
                        order.tableId === table.id
                      );

                      alert(`Table #${table.id} has ${tableOrders.length} orders.`);
                    }}
                  >
                    View Orders
                  </Button>
                </Card.Body>
              </Card>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const OrdersTable = ({ orders, renderActionButtons, getStatusBadgeVariant, fetchData }) => (
  <Card>
    <Card.Header>
      <div className="d-flex justify-content-between align-items-center">
        <h5 className="mb-0">Orders</h5>
        <Button variant="outline-primary" size="sm" onClick={fetchData}>
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
);

export default WaiterDashboard;
