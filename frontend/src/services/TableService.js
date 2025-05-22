import axios from 'axios';
import AuthService from './AuthService';

class TableService {
  getAllTables() {
    return axios.get('/tables', {
      headers: AuthService.getAuthHeader()
    });
  }

  getTableById(id) {
    return axios.get(`/tables/${id}`, {
      headers: AuthService.getAuthHeader()
    });
  }

  createTable() {
    return axios.post('/tables', {}, {
      headers: AuthService.getAuthHeader()
    });
  }

  deleteTable(id) {
    return axios.delete(`/tables/${id}`, {
      headers: AuthService.getAuthHeader()
    });
  }

  getTableQRCode(id) {
    return axios.get(`/tables/${id}/qrcode`, {
      headers: AuthService.getAuthHeader(),
      responseType: 'blob'
    });
  }
}

export default new TableService();