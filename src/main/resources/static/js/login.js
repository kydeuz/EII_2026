// ============================================================
// 1. Mostrar / ocultar formulario de olvido de contraseña
// ============================================================

document.getElementById('linkOlvido').addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('formOlvido').style.display = 'block';
});

document.getElementById('btnVolver').addEventListener('click', function() {
    document.getElementById('formOlvido').style.display = 'none';
    document.getElementById('loginForm').style.display = 'block';
});


// ============================================================
// 2. Lógica de restablecer contraseña (MVP)
// ============================================================
document.getElementById('btnReset').addEventListener('click', async function() {
    const email = document.getElementById('emailReset').value.trim();
    const nuevaPassword = document.getElementById('nuevaPassword').value;
    const mensaje = document.getElementById('mensajeReset');

    if (!email || !nuevaPassword) {
        mensaje.textContent = 'Completa ambos campos.';
        mensaje.style.color = 'red';
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/api/auth/reset-password`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, nuevaPassword })
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({ error: 'Error desconocido' }));
            throw new Error(err.error || `Error ${response.status}`);
        }

        mensaje.textContent = 'Contraseña actualizada. Redirigiendo al login...';
        mensaje.style.color = 'green';
        setTimeout(() => {
            document.getElementById('formOlvido').style.display = 'none';
            document.getElementById('loginForm').style.display = 'block';
            mensaje.textContent = '';
        }, 2000);
    } catch (error) {
        mensaje.textContent = error.message;
        mensaje.style.color = 'red';
    }
});

// ============================================================
// 3. Login logica basica 
// ============================================================
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.error || 'Error en login');
        }
        const data = await response.json();
        localStorage.setItem('jwt', data.token);
        localStorage.setItem('usuarioId', data.usuarioId);
        localStorage.setItem('usuarioNombre', data.usuario);
        localStorage.setItem('usuarioRol', data.rol);
        window.location.href = '/index.html';
    } catch (error) {
        document.getElementById('mensajeError').textContent = error.message;
    }
});

