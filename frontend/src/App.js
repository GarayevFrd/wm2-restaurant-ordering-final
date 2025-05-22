import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Container } from 'react-bootstrap';


import Navigation from './components/Navigation';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import CustomerHome from './components/customer/CustomerHome';
import CustomerMenu from './components/customer/CustomerMenu';
import CustomerOrder from './components/customer/CustomerOrder';
import OrderStatus from './components/customer/OrderStatus';
import KitchenDashboard from './components/kitchen/KitchenDashboard';
import WaiterDashboard from './components/waiter/WaiterDashboard';
import ManagerDashboard from './components/manager/ManagerDashboard';
import MenuManagement from './components/manager/MenuManagement';
import TableManagement from './components/manager/TableManagement';


import AuthService from './services/AuthService';

function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }
    setLoading(false);
  }, []);


  const ProtectedRoute = ({ children, roles }) => {
    if (loading) {
      return <div>Loading...</div>;
    }

    if (!currentUser) {
      return <Navigate to="/login" />;
    }

    if (roles && !roles.includes(currentUser.role)) {
      return <Navigate to="/" />;
    }

    return children;
  };

  return (
    <Router>
      <Navigation currentUser={currentUser} setCurrentUser={setCurrentUser} />
      <Container className="mt-3">
        <Routes>

          <Route path="/login" element={<Login setCurrentUser={setCurrentUser} />} />
          <Route path="/register" element={<Register />} />


          <Route path="/menu/:tableId" element={<CustomerMenu />} />
          <Route path="/order/:tableId" element={<CustomerOrder />} />
          <Route path="/order/status/:orderId" element={<OrderStatus />} />


          <Route 
            path="/kitchen" 
            element={
              <ProtectedRoute roles={['KITCHEN']}>
                <KitchenDashboard />
              </ProtectedRoute>
            } 
          />


          <Route 
            path="/waiter" 
            element={
              <ProtectedRoute roles={['WAITER']}>
                <WaiterDashboard />
              </ProtectedRoute>
            } 
          />


          <Route 
            path="/manager" 
            element={
              <ProtectedRoute roles={['MANAGER']}>
                <ManagerDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/manager/menu" 
            element={
              <ProtectedRoute roles={['MANAGER']}>
                <MenuManagement />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/manager/tables" 
            element={
              <ProtectedRoute roles={['MANAGER']}>
                <TableManagement />
              </ProtectedRoute>
            } 
          />


          <Route path="/" element={
            currentUser && ['KITCHEN', 'WAITER', 'MANAGER'].includes(currentUser.role) ? (
              <Navigate to={`/${currentUser.role.toLowerCase()}`} />
            ) : (
              <CustomerHome />
            )
          } />
        </Routes>
      </Container>
    </Router>
  );
}

export default App;
