// api.js — Fetch wrapper with error handling

const API_BASE = '';  // same origin

async function apiFetch(path, options = {}) {
  try {
    const res = await fetch(API_BASE + path, options);
    let data;
    try {
      data = await res.json();
    } catch (parseErr) {
      data = { success: false, message: 'Invalid response from server' };
    }
    return { ok: res.ok, status: res.status, data };
  } catch (err) {
    return { ok: false, status: 0, data: { success: false, message: 'Network error: ' + err.message } };
  }
}

async function apiGet(path) {
  return apiFetch(path, { method: 'GET' });
}

async function apiPost(path, params) {
  const body = new URLSearchParams(params).toString();
  return apiFetch(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body
  });
}

async function apiPut(path, params) {
  const body = new URLSearchParams(params).toString();
  return apiFetch(path, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body
  });
}

async function apiDelete(path) {
  return apiFetch(path, { method: 'DELETE' });
}
