import axios from 'axios';

const API_URL = '/auth/';

class AuthService {
  async login(username, password) {
    const response = await axios.post(API_URL + 'login', {
      username,
      password
    });
    if (response.data.token) {
      localStorage.setItem('user', JSON.stringify({
        username,
        token: response.data.token,
        role: this.parseJwt(response.data.token).role
      }));
    }
    return response.data;
  }

  logout() {
    localStorage.removeItem('user');
  }

  async register(username, password, role) {
    return axios.post(API_URL + 'register', {
      username,
      password,
      role
    });
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }


  parseJwt(token) {
    try {
      const decoded = JSON.parse(atob(token.split('.')[1]));
      // Remove 'ROLE_' prefix from role if it exists
      if (decoded.role && decoded.role.startsWith('ROLE_')) {
        decoded.role = decoded.role.substring(5);
      }
      return decoded;
    } catch (e) {
      return null;
    }
  }


  getAuthHeader() {
    const user = this.getCurrentUser();
    if (user && user.token) {
      return { Authorization: 'Bearer ' + user.token };
    } else {
      return {};
    }
  }


  isAuthenticated() {
    const user = this.getCurrentUser();
    return user !== null && user.token !== undefined;
  }
}

export default new AuthService();
