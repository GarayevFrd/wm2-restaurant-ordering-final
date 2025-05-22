import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Alert, Spinner, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import QRCodeService from '../../services/QRCodeService';
import QRCode from 'react-qr-code';

const CustomerHome = () => {
  const [tables, setTables] = useState([]);
  const [tableQRCodes, setTableQRCodes] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showQRModal, setShowQRModal] = useState(false);
  const [currentQRCode, setCurrentQRCode] = useState(null);
  const [currentTableId, setCurrentTableId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchTables();
  }, []);

  const fetchTables = async () => {
    try {
      const response = await axios.get('/tables');
      const tablesData = response.data;
      setTables(tablesData);
      const qrCodes = {};
      for (const table of tablesData) {
        qrCodes[table.id] = QRCodeService.generateQRCodeValue(table.id);
      }

      setTableQRCodes(qrCodes);
      setLoading(false);
    } catch (error) {
      setError('Failed to load tables. Please try again later.');
      setLoading(false);
    }
  };

  const handleTableSelect = (tableId) => {
    navigate(`/menu/${tableId}`);
  };

  const handleShowQRCode = (tableId) => {
    if (tableQRCodes[tableId]) {
      setCurrentQRCode(tableQRCodes[tableId]);
      setCurrentTableId(tableId);
      setShowQRModal(true);
    } else {

      const qrCodeValue = QRCodeService.generateQRCodeValue(tableId);
      setCurrentQRCode(qrCodeValue);
      setCurrentTableId(tableId);
      setShowQRModal(true);
    }
  };

  const handleCloseQRModal = () => {
    setShowQRModal(false);
    setCurrentQRCode(null);
    setCurrentTableId(null);
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
      <h1 className="mb-4 text-center">Welcome to Our Restaurant</h1>
      <p className="text-center mb-5">Please select a table to view the menu and place your order</p>

      {tables.length === 0 ? (
        <Alert variant="info">No tables available at the moment. Please check back later.</Alert>
      ) : (
        <Row>
          {tables.map(table => (
            <Col key={table.id} md={4} className="mb-4">
              <Card className="h-100 shadow-sm">
                <Card.Body className="d-flex flex-column">
                  <Card.Title className="text-center">Table #{table.id}</Card.Title>


                  {tableQRCodes[table.id] && (
                    <div className="text-center my-3" style={{ cursor: 'pointer' }} onClick={() => handleShowQRCode(table.id)}>
                      <div style={{ maxWidth: '150px', margin: '0 auto' }}>
                        <QRCode 
                          value={tableQRCodes[table.id]} 
                          size={150}
                          style={{ height: "auto", maxWidth: "100%", width: "100%" }}
                        />
                      </div>
                      <p className="small text-muted mt-1">Click to enlarge</p>
                    </div>
                  )}

                  <div className="mt-auto pt-3">
                    <div className="d-grid">
                      <Button 
                        variant="primary" 
                        onClick={() => handleTableSelect(table.id)}
                      >
                        Select Table
                      </Button>
                    </div>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}


      <Modal show={showQRModal} onHide={handleCloseQRModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>QR Code for Table #{currentTableId}</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-center">
          {currentQRCode ? (
            <div>
              <div style={{ maxWidth: '250px', margin: '0 auto' }}>
                <QRCode 
                  value={currentQRCode} 
                  size={250}
                  style={{ height: "auto", maxWidth: "100%", width: "100%" }}
                />
              </div>
              <p className="mt-3">
                Scan this QR code to access the menu and place orders for Table #{currentTableId}.
              </p>
              <p className="text-muted small">
                URL: {currentQRCode}
              </p>
            </div>
          ) : (
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading QR Code...</span>
            </Spinner>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseQRModal}>
            Close
          </Button>
          <Button variant="primary" onClick={() => {
            handleCloseQRModal();
            handleTableSelect(currentTableId);
          }}>
            Select This Table
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default CustomerHome;
