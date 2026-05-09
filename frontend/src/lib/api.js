import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:48124/api';

export const api = axios.create({
  baseURL: BASE_URL,
});

export const chatStream = (endpoint, query, sessionId) => {
  return new EventSource(`${BASE_URL}${endpoint}?query=${encodeURIComponent(query)}&sessionId=${sessionId}`);
};

export const getFiles = () => api.get('/knowledge/files');
export const uploadFile = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return api.post('/knowledge/upload', formData);
};
export const deleteFile = (name) => api.delete(`/knowledge/files/${name}`);
