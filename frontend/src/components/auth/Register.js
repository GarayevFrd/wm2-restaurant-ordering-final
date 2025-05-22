import React, { useState } from 'react';
import { Form, Button, Alert, Card } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import AuthService from '../../services/AuthService';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('WAITER'); // Default role
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [successful, setSuccessful] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setMessage('');
    setLoading(true);
    setSuccessful(false);

    try {
      const response = await AuthService.register(username, password, role);
      setMessage(response.data.message);
      setSuccessful(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch (error) {
      const resMessage =
        (error.response &&
          error.response.data &&
          error.response.data.error) ||
        error.message ||
        error.toString();
      setMessage(resMessage);
      setSuccessful(false);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="col-md-6 offset-md-3">
      <Card className="auth-form mt-5">
        <Card.Body>
          <h2 className="text-center mb-4">Register</h2>
          {message && (
            <Alert variant={successful ? "success" : "danger"}>
              {message}
            </Alert>
          )}
          <Form onSubmit={handleRegister}>
            <Form.Group className="mb-3" controlId="formUsername">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formRole">
              <Form.Label>Role</Form.Label>
              <Form.Select 
                value={role} 
                onChange={(e) => setRole(e.target.value)}
                required
              >
                <option value="WAITER">Waiter</option>
                <option value="KITCHEN">Kitchen Staff</option>
                <option value="MANAGER">Manager</option>
              </Form.Select>
            </Form.Group>

            <Button variant="primary" type="submit" disabled={loading} className="w-100">
              {loading ? 'Loading...' : 'Register'}
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
};

export default Register;