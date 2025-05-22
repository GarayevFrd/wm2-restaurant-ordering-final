import React from 'react';
import { Navbar, Nav, Container, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';

const Navigation = ({ currentUser, setCurrentUser }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    AuthService.logout();
    setCurrentUser(null);
    navigate('/login');
  };

  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container>
        <Navbar.Brand as={Link} to="/">Restaurant Ordering System</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            {currentUser ? (
              <>
                {currentUser.role === 'MANAGER' && (
                  <>
                    <Nav.Link as={Link} to="/manager">Dashboard</Nav.Link>
                    <Nav.Link as={Link} to="/manager/menu">Menu Management</Nav.Link>
                    <Nav.Link as={Link} to="/manager/tables">Table Management</Nav.Link>
                  </>
                )}
                {currentUser.role === 'KITCHEN' && (
                  <Nav.Link as={Link} to="/kitchen">Kitchen Dashboard</Nav.Link>
                )}
                {currentUser.role === 'WAITER' && (
                  <Nav.Link as={Link} to="/waiter">Waiter Dashboard</Nav.Link>
                )}
              </>
            ) : (
              <>
                <Nav.Link as={Link} to="/">Home</Nav.Link>
                <Nav.Link as={Link} to="/login">Login</Nav.Link>
                <Nav.Link as={Link} to="/register">Register</Nav.Link>
              </>
            )}
          </Nav>
          {currentUser && (
            <Nav>
              <Navbar.Text className="me-3">
                Signed in as: {currentUser.username} ({currentUser.role})
              </Navbar.Text>
              {currentUser.role === 'MANAGER' && (
                <Button variant="primary" className="me-2" as={Link} to="/manager">Dashboard</Button>
              )}
              {currentUser.role === 'KITCHEN' && (
                <Button variant="primary" className="me-2" as={Link} to="/kitchen">Dashboard</Button>
              )}
              {currentUser.role === 'WAITER' && (
                <Button variant="primary" className="me-2" as={Link} to="/waiter">Dashboard</Button>
              )}
              <Button variant="outline-light" onClick={handleLogout}>Logout</Button>
            </Nav>
          )}
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Navigation;
