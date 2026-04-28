export const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8082/api';

export function getToken() {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('token');
  }
  return null;
}

export function setToken(token: string) {
  if (typeof window !== 'undefined') {
    localStorage.setItem('token', token);
  }
}

export function removeToken() {
  if (typeof window !== 'undefined') {
    localStorage.removeItem('token');
  }
}

// Wrapper for fetch that automatically adds the Authorization header
export async function apiFetch(endpoint: string, options: RequestInit = {}) {
  const token = getToken();
  
  const headers = new Headers(options.headers || {});
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }
  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 403 || response.status === 401) {
    // Token expired or invalid
    removeToken();
    if (typeof window !== 'undefined' && !window.location.pathname.startsWith('/auth')) {
      alert("Your session has expired. Please log in again.");
      window.location.href = '/auth/login';
    }
  }

  // Handle empty responses (like 200 OK with no body)
  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return null;
  }

  // Some text responses don't parse as JSON
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const data = await response.json();
    if (!response.ok) {
      if (data.message) throw new Error(data.message);
      if (data.error) throw new Error(data.error);
      
      // Handle Spring Boot field validation maps (e.g. { "username": "Username must contain letters" })
      if (typeof data === 'object' && Object.keys(data).length > 0) {
        const messages = Object.values(data).filter(v => typeof v === 'string').join(', ');
        if (messages) throw new Error(messages);
      }
      throw new Error(`HTTP ${response.status}`);
    }
    return data;
  } else {
    const text = await response.text();
    if (!response.ok) {
      throw new Error(text || `HTTP ${response.status}`);
    }
    return text;
  }
}
