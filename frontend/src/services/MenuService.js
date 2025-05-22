import axios from 'axios';
import AuthService from './AuthService';

class MenuService {
  getAllMenuItems() {
    return axios.get('/manager/menu', {
      headers: AuthService.getAuthHeader()
    });
  }

  getMenuForTable(tableId) {
    return axios.get(`/customer/menu/${tableId}`);
  }

  addMenuItem(menuItem) {
    return axios.post('/manager/menu', menuItem, {
      headers: AuthService.getAuthHeader()
    });
  }

  updateMenuItem(id, menuItem) {
    return axios.put(`/manager/menu/${id}`, menuItem, {
      headers: AuthService.getAuthHeader()
    });
  }

  deleteMenuItem(id) {
    return axios.delete(`/manager/menu/${id}`, {
      headers: AuthService.getAuthHeader()
    });
  }
}

export default new MenuService();
