// Elementos del DOM
const formulario = document.getElementById('labForm');
const inputNombre = document.getElementById('nombre');
const inputUbicacion = document.getElementById('ubicacion');
const inputCapacidad = document.getElementById('capacidad');
const editId = document.getElementById('editId');
const btnCrear = document.getElementById('btnCrear');
const btnActualizar = document.getElementById('btnActualizar');
const btnCancelar = document.getElementById('btnCancelarEdicion');
const mensajeDiv = document.getElementById('mensaje');
const tbody = document.querySelector('#tablaLaboratorios tbody');

// Cargar lista de laboratorios y mostrarlos en la tabla
async function cargarLaboratorios() {
    try {
        const response = await apiFetch('/api/laboratorios');
        const labs = await response.json();
        tbody.innerHTML = '';
        if (labs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">No hay laboratorios aún.</td></tr>';
            return;
        }
        labs.forEach(lab => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${lab.id}</td>
                <td>${lab.nombre}</td>
                <td>${lab.ubicacion}</td>
                <td>${lab.capacidad}</td>
                <td class="acciones">
                    <button onclick="cargarEdicion(${lab.id}, '${lab.nombre}', '${lab.ubicacion}', ${lab.capacidad})">Editar</button>
                    <button onclick="eliminarLaboratorio(${lab.id})">Eliminar</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        mostrarMensaje('Error al cargar laboratorios: ' + error.message, true);
    }
}

// Preparar el formulario para editar
function cargarEdicion(id, nombre, ubicacion, capacidad) {
    console.log('Cargando edición para ID:', id);
    editId.value = id;
    inputNombre.value = nombre;
    inputUbicacion.value = ubicacion;
    inputCapacidad.value = capacidad;

    // Cambiar botones: el botón "Actualizar" ahora es de tipo submit
    btnCrear.style.display = 'none';
    btnActualizar.style.display = 'inline-block';
    btnActualizar.type = 'submit';        // ← clave: ahora envía el formulario
    btnCancelar.style.display = 'inline-block';
}

// Cancelar edición y volver a modo creación
function cancelarEdicion() {
    editId.value = '';
    inputNombre.value = '';
    inputUbicacion.value = '';
    inputCapacidad.value = '';

    btnCrear.style.display = 'inline-block';
    btnActualizar.style.display = 'none';
    btnActualizar.type = 'button';        // ← devolver a tipo button
    btnCancelar.style.display = 'none';
}

// Crear un nuevo laboratorio
async function crearLaboratorio(nombre, ubicacion, capacidad) {
    const body = { nombre, ubicacion, capacidad: parseInt(capacidad) };
    const response = await apiFetch('/api/laboratorios', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!response.ok) {
        const err = await response.json().catch(() => ({ error: 'Error al crear' }));
        throw new Error(err.error || 'Error ' + response.status);
    }
    return response.json();
}

// Actualizar laboratorio existente
async function actualizarLaboratorio(id, nombre, ubicacion, capacidad) {
    const body = { nombre, ubicacion, capacidad: parseInt(capacidad) };
    console.log('Enviando PUT a /api/laboratorios/' + id, body);
    const response = await apiFetch(`/api/laboratorios/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    console.log('Respuesta PUT:', response.status);
    if (!response.ok) {
        const errText = await response.text();
        console.error('Error PUT:', errText);
        throw new Error('Error al actualizar: ' + response.status);
    }
    const data = await response.json();
    console.log('Laboratorio actualizado:', data);
    return data;
}

// Eliminar laboratorio
async function eliminarLaboratorio(id) {
    if (!confirm(`¿Eliminar el laboratorio con ID ${id}?`)) return;
    try {
        const response = await apiFetch(`/api/laboratorios/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Error al eliminar: ' + response.status);
        }
        mostrarMensaje('Laboratorio eliminado correctamente');
        cargarLaboratorios();
    } catch (error) {
        mostrarMensaje(error.message, true);
    }
}

// Mostrar mensajes (éxito o error)
function mostrarMensaje(texto, esError = false) {
    mensajeDiv.textContent = texto;
    mensajeDiv.className = esError ? 'error' : '';
    setTimeout(() => { mensajeDiv.textContent = ''; }, 3000);
}

// Manejo del envío del formulario (creación o actualización según estado)
formulario.addEventListener('submit', async function(e) {
    e.preventDefault();
    console.log('Formulario submit. editId.value:', editId.value);
    const nombre = inputNombre.value.trim();
    const ubicacion = inputUbicacion.value.trim();
    const capacidad = inputCapacidad.value;

    if (!nombre || !ubicacion || !capacidad || capacidad < 1) {
        mostrarMensaje('Completa todos los campos correctamente.', true);
        return;
    }

    try {
        if (editId.value) {
            // Modo edición
            await actualizarLaboratorio(editId.value, nombre, ubicacion, capacidad);
            mostrarMensaje('Laboratorio actualizado correctamente');
            cancelarEdicion();
        } else {
            // Modo creación
            await crearLaboratorio(nombre, ubicacion, capacidad);
            mostrarMensaje('Laboratorio creado correctamente');
        }
        formulario.reset();
        cargarLaboratorios();
    } catch (error) {
        mostrarMensaje(error.message, true);
    }
});

// Evento para cancelar edición
btnCancelar.addEventListener('click', cancelarEdicion);

// Carga inicial
cargarLaboratorios();