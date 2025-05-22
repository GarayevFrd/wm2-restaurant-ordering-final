import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Alert, Spinner, Modal } from 'react-bootstrap';
import TableService from '../../services/TableService';
import OrderService from '../../services/OrderService';
import QRCodeService from '../../services/QRCodeService';
import QRCode from 'react-qr-code';

const TableManagement = () => {
  const [tables, setTables] = useState([]);
  const [orders, setOrders] = useState([]);
  const [tableQRCodes, setTableQRCodes] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showQRModal, setShowQRModal] = useState(false);
  const [currentQRCode, setCurrentQRCode] = useState(null);
  const [currentTableId, setCurrentTableId] = useState(null);
  const [creating, setCreating] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {

      const [tablesResponse, ordersResponse] = await Promise.all([
        TableService.getAllTables(),
        OrderService.getAllOrders()
      ]);

      const tablesData = tablesResponse.data;
      setTables(tablesData);
      setOrders(ordersResponse.data);


      const qrCodes = {};
      for (const table of tablesData) {
        qrCodes[table.id] = QRCodeService.generateQRCodeValue(table.id);
      }

      setTableQRCodes(qrCodes);
      setLoading(false);
    } catch (error) {
      setError('Failed to load data. Please try again later.');
      setLoading(false);
    }
  };

  const handleCreateTable = async () => {
    setCreating(true);
    try {
      await TableService.createTable();

      fetchData();
    } catch (error) {
      setError(`Failed to create table. ${error.message}`);
    } finally {
      setCreating(false);
    }
  };

  const handleDeleteTable = async (tableId) => {

    const activeOrders = orders.filter(
      order => order.table && 
      order.table.id === tableId && 
      ['CREATED', 'IN_PREPARATION', 'READY'].includes(order.status)
    );

    if (activeOrders.length > 0) {
      alert('Cannot delete table with active orders.');
      return;
    }

    if (window.confirm('Are you sure you want to delete this table?')) {
      setDeleting(true);
      try {
        await TableService.deleteTable(tableId);


        fetchData();
      } catch (error) {
        setError(`Failed to delete table. ${error.message}`);
      } finally {
        setDeleting(false);
      }
    }
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

  const getTableStatus = (tableId) => {
    const activeOrders = orders.filter(
      order => order.table && 
      order.table.id === tableId && 
      ['CREATED', 'IN_PREPARATION', 'READY'].includes(order.status)
    );

    if (activeOrders.length > 0) {
      return { status: 'occupied', variant: 'warning', text: 'Occupied' };
    } else {
      return { status: 'available', variant: 'success', text: 'Available' };
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

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Table Management</h1>
        <Button 
          variant="success" 
          onClick={handleCreateTable}
          disabled={creating}
        >
          {creating ? (
            <>
              <Spinner as="span" animation="border" size="sm" className="me-2" />
              Creating...
            </>
          ) : (
            'Add New Table'
          )}
        </Button>
      </div>

      {error && <Alert variant="danger" className="mb-4">{error}</Alert>}

      {tables.length === 0 ? (
        <Alert variant="info">No tables available. Add your first table!</Alert>
      ) : (
        <Row>
          {tables.map(table => {
            const tableStatus = getTableStatus(table.id);

            return (
              <Col key={table.id} md={4} className="mb-4">
                <Card className={`border-${tableStatus.variant}`}>
                  <Card.Header className={`bg-${tableStatus.variant} text-white`}>
                    <div className="d-flex justify-content-between align-items-center">
                      <h5 className="mb-0">Table #{table.id}</h5>
                      <span>{tableStatus.text}</span>
                    </div>
                  </Card.Header>
                  <Card.Body>

                    {tableQRCodes[table.id] && (
                      <div className="text-center mb-3" style={{ cursor: 'pointer' }} onClick={() => handleShowQRCode(table.id)}>
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

                    <div className="d-flex justify-content-end mb-3">
                      <Button 
                        variant="outline-danger" 
                        onClick={() => handleDeleteTable(table.id)}
                        disabled={deleting || tableStatus.status === 'occupied'}
                      >
                        Delete
                      </Button>
                    </div>

                    <div className="small text-muted">
                      {tableStatus.status === 'occupied' ? (
                        <div>
                          Active Orders: {orders.filter(
                            order => order.table && 
                            order.table.id === table.id && 
                            ['CREATED', 'IN_PREPARATION', 'READY'].includes(order.status)
                          ).length}
                        </div>
                      ) : (
                        <div>No active orders</div>
                      )}
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            );
          })}
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
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default TableManagement;
