import axios from 'axios'

export default () => {
  return axios.create({
    baseURL: `/gag-web/rest`,
    withCredentials: true,
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
    }
  });
}
