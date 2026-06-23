document.getElementById('registroForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const nombre = document.getElementById('nombre').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const rol = document.getElementById('rol').value;

    if (!nombre || !email || !password || !rol) {
        document.getElementById('mensajeError').textContent = 'Todos los campos son obligatorios.';
        return;
    }

    const body = { nombre, email, password, rol };

    try {
        // Este endpoint no requiere token (está permitido en SecurityConfig)
        const response = await fetch(`${API_BASE}/api/auth/registro`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({ error: 'Error desconocido' }));
            throw new Error(err.error || `Error ${response.status}`);
        }

        document.getElementById('mensajeExito').textContent = 'Registro exitoso. Redirigiendo al login...';
        document.getElementById('mensajeError').textContent = '';
        setTimeout(() => window.location.href = 'login.html', 1500);
    } catch (error) {
        document.getElementById('mensajeError').textContent = error.message;
        document.getElementById('mensajeExito').textContent = '';
    }
});