import Api from './api.js'

const api = Api();

export default {
  isDev() {
    return process.env.NODE_ENV && process.env.NODE_ENV === 'development'
  },
  
  getUsers() {
    return api.get('/users')
      .then(response => response.data)
      .catch(err => {
        console.log(err)
      });
  },
  
  getUser(id) {
    return api.get(`/users/${Number(id)}`)
      .then(response => response.data)
      .catch(err => console.log(err));
  },
}
