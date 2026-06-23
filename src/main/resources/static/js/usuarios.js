// Elementos del DOM
const formulario = document.getElementById('usuarioForm');
const inputNombre = document.getElementById('nombre');
const inputEmail = document.getElementById('email');
const inputRol = document.getElementById('rol');
const inputPassword = document.getElementById('password');
const editId = document.getElementById('editId');
const btnActualizar = document.getElementById('btnActualizar');
const btnCancelar = document.getElementById('btnCancelarEdicion');
const mensajeDiv = document.getElementById('mensaje');
const tbody = document.querySelector('#tablaUsuarios tbody');

// Cargar lista de usuarios
async function cargarUsuarios() {
    try {
        const response = await apiFetch('/api/usuarios');
        const usuarios = await response.json();
        tbody.innerHTML = '';
        if (usuarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">No hay usuarios.</td></tr>';
            return;
        }
        usuarios.forEach(usuario => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${usuario.id}</td>
                <td>${usuario.nombre}</td>
                <td>${usuario.email}</td>
                <td>${usuario.rol}</td>
                <td class="acciones">
                    <button onclick="cargarEdicion(${usuario.id}, '${usuario.nombre}', '${usuario.email}', '${usuario.rol}')">Editar</button>
                    <button onclick="eliminarUsuario(${usuario.id})">Eliminar</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        mostrarMensaje('Error al cargar usuarios: ' + error.message, true);
    }
}

// Cargar datos en el formulario para editar
function cargarEdicion(id, nombre, email, rol) {
    editId.value = id;
    inputNombre.value = nombre;
    inputEmail.value = email;
    inputRol.value = rol;
    inputPassword.value = ''; // limpiar campo contraseña
    btnActualizar.style.display = 'inline-block';
    btnCancelar.style.display = 'inline-block';
}

// Cancelar edición
function cancelarEdicion() {
    editId.value = '';
    inputNombre.value = '';
    inputEmail.value = '';
    inputRol.value = 'ALUMNO';
    inputPassword.value = '';
    btnActualizar.style.display = 'none';
    btnCancelar.style.display = 'none';
}

// Actualizar usuario
async function actualizarUsuario(id, nombre, email, rol, password) {
    const body = { nombre, email, rol };
    if (password && password.trim() !== '') {
        body.password = password;
    }
    const response = await apiFetch(`/api/usuarios/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!response.ok) {
        const err = await response.json().catch(() => ({ error: 'Error al actualizar' }));
        throw new Error(err.error || 'Error ' + response.status);
    }
    return response.json();
}

// Eliminar usuario
async function eliminarUsuario(id) {
    if (!confirm(`¿Eliminar al usuario con ID ${id}?`)) return;
    try {
        const response = await apiFetch(`/api/usuarios/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            throw new Error('Error al eliminar: ' + response.status);
        }
        mostrarMensaje('Usuario eliminado correctamente');
        cargarUsuarios();
    } catch (error) {
        mostrarMensaje(error.message, true);
    }
}

// Mostrar mensajes
function mostrarMensaje(texto, esError = false) {
    mensajeDiv.textContent = texto;
    mensajeDiv.className = esError ? 'error' : '';
    setTimeout(() => { mensajeDiv.textContent = ''; }, 3000);
}

// Envío del formulario (solo actualizar)
formulario.addEventListener('submit', async function(e) {
    e.preventDefault();
    const id = editId.value;
    const nombre = inputNombre.value.trim();
    const email = inputEmail.value.trim();
    const rol = inputRol.value;
    const password = inputPassword.value;

    if (!nombre || !email || !rol) {
        mostrarMensaje('Nombre, email y rol son obligatorios.', true);
        return;
    }

    try {
        await actualizarUsuario(id, nombre, email, rol, password);
        mostrarMensaje('Usuario actualizado correctamente');
        cancelarEdicion();
        cargarUsuarios();
    } catch (error) {
        mostrarMensaje(error.message, true);
    }
});

btnCancelar.addEventListener('click', cancelarEdicion);

// Carga inicial
cargarUsuarios();