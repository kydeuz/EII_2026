let pagina = 0;
const size = 10;

async function cargarHistorial() {
    const usuarioId = document.getElementById('filtroUsuario').value || null;
    const labId = document.getElementById('filtroLab').value || null;
    const fecha = document.getElementById('filtroFecha').value || null;

    let url = `/api/historial?page=${pagina}&size=${size}`;
    if (usuarioId) url += `&usuarioId=${usuarioId}`;
    if (labId) url += `&laboratorioId=${labId}`;
    if (fecha) url += `&fecha=${fecha}`;

    try {
        const response = await apiFetch(url);
        const paginaData = await response.json();
        const reservas = paginaData.content;
        const tbody = document.querySelector('#tablaHistorial tbody');
        tbody.innerHTML = '';
        reservas.forEach(r => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${r.id}</td>
                <td>${r.usuario?.nombre || 'N/A'}</td>
                <td>${r.laboratorio?.nombre || 'N/A'}</td>
                <td>${r.fecha}</td>
                <td>${r.horaInicio}</td>
                <td>${r.horaFin}</td>
                <td>${r.estado}</td>
                <td>
                    ${r.estado === 'CONFIRMADA' ?
                      `<button onclick="cancelarReserva(${r.id})">Cancelar</button>` : ''}
                </td>
            `;
            tbody.appendChild(tr);
        });
        document.getElementById('paginaActual').textContent = paginaData.number;
        // Deshabilitar botones si es primera/última página
        document.getElementById('btnAnterior').disabled = paginaData.first;
        document.getElementById('btnSiguiente').disabled = paginaData.last;
    } catch (error) {
        alert('Error al cargar historial: ' + error.message);
    }
}

document.getElementById('btnFiltrar').addEventListener('click', () => {
    pagina = 0;
    cargarHistorial();
});

document.getElementById('btnAnterior').addEventListener('click', () => {
    if (pagina > 0) {
        pagina--;
        cargarHistorial();
    }
});

document.getElementById('btnSiguiente').addEventListener('click', () => {
    pagina++;
    cargarHistorial();
});

// Reutilizamos cancelarReserva de reservas.js
// Asegúrate de que esa función esté en un script común o importa ambas
// Si no, duplica la lógica.
function cancelarReserva(id) {
    // Código similar al de reservas.js
}

// Cargar al entrar
cargarHistorial();