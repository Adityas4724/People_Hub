import axios from 'axios'
const api = axios.create({ baseURL: '/api' })
api.interceptors.request.use(config => {
  const token = localStorage.getItem('peoplehub_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
// Expired or invalid token: clear the session so the user lands on the sign-in screen
api.interceptors.response.use(r => r, err => {
  if (err.response?.status === 401 && localStorage.getItem('peoplehub_token')) {
    ['token', 'user', 'role'].forEach(k => localStorage.removeItem('peoplehub_' + k))
    window.location.reload()
  }
  return Promise.reject(err)
})
export default api
