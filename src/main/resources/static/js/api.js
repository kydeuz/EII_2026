const API_BASE = "http://localhost:8080";

function obtenerToken() {
  return localStorage.getItem("jwt");
}

function guardarToken(token) {
  localStorage.setItem("jwt", token);
}

async function apiFetch(path, options = {}) {
  const token = obtenerToken();
  const headers = { ...(options.headers || {}), Authorization: token ? `Bearer ${token}` : "" };
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (!response.ok) {
    let mensaje = `Error ${response.status}`;
    try {
      const data = await response.clone().json();
      if (data.error) mensaje = data.error;
    } catch (e) {}
    throw new Error(mensaje);
  }
  return response;
}