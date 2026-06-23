async function cargarLaboratorios() {
    const select = document.getElementById('laboratorio');
    try {
        const response = await apiFetch('/api/laboratorios');
        const labs = await response.json();
        console.log('Laboratorios crudos:', labs); // ← Muestra todos los campos
        select.innerHTML = '';
        labs.forEach(lab => {
            // Validación de seguridad
            if (!lab.id) {
                console.warn('Laboratorio sin ID:', lab);
                return;
            }
            const option = document.createElement('option');
            option.value = lab.id;
            // Si ubicacion es undefined o null, muestra "Sin ubicación"
            const ubicacion = lab.ubicacion || 'Sin ubicación';
            option.textContent = `${lab.nombre} - ${ubicacion}`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error(error);
        alert('No se pudieron cargar los laboratorios. Inicia sesión.');
    }
}

let disponibilidadOk = false;

document.addEventListener('DOMContentLoaded', function() {
    cargarLaboratorios();

    const btnVerificar = document.getElementById('btnVerificar');
    const btnReservar = document.getElementById('btnReservar');
    const formulario = document.getElementById('reservaForm');

    if (btnVerificar) {
        btnVerificar.addEventListener('click', async () => {
            const labId = document.getElementById('laboratorio').value;
            const fecha = document.getElementById('fecha').value;
            const inicio = document.getElementById('horaInicio').value;
            const fin = document.getElementById('horaFin').value;

            console.log('Verificando con:', { labId, fecha, inicio, fin });

            if (!labId || !fecha || !inicio || !fin) {
                alert('Completa todos los campos');
                return;
            }
            if (inicio >= fin) {
                alert('La hora de inicio debe ser anterior a la de fin');
                return;
            }

            try {
                const response = await apiFetch(
                    `/api/reservas/disponibilidad?laboratorioId=${labId}&fecha=${fecha}&inicio=${inicio}&fin=${fin}`
                );
                const data = await response.json();
                const msgSpan = document.getElementById('disponibilidadMsg');
                if (data.disponible) {
                    msgSpan.style.color = 'green';
                    msgSpan.textContent = 'Disponible';
                    disponibilidadOk = true;
                    btnReservar.disabled = false;
                } else {
                    msgSpan.style.color = 'red';
                    msgSpan.textContent = 'No disponible (cruces: ' + data.cruces + ')';
                    disponibilidadOk = false;
                    btnReservar.disabled = true;
                }
            } catch (error) {
                console.error('Error en verificación:', error);
                alert('Error al verificar: ' + error.message);
            }
        });
    }

    if (formulario) {
        formulario.addEventListener('submit', async function(e) {
            e.preventDefault();
            if (!disponibilidadOk) {
                alert('Primero verifica la disponibilidad');
                return;
            }
            const usuarioId = localStorage.getItem('usuarioId');
            if (!usuarioId) {
                alert('No estás autenticado');
                window.location.href = '/login.html';
                return;
            }
            const body = {
                usuarioId: usuarioId,
                laboratorioId: document.getElementById('laboratorio').value,
                fecha: document.getElementById('fecha').value,
                horaInicio: document.getElementById('horaInicio').value,
                horaFin: document.getElementById('horaFin').value
            };
            try {
                const response = await apiFetch('/api/reservas', {
                    method: 'POST',
                    body: JSON.stringify(body),
                    headers: { 'Content-Type': 'application/json' }
                });
                const data = await response.json();
                alert(data.mensaje);
                window.location.href = '/historial.html';
            } catch (error) {
                alert(error.message);
            }
        });
    }
});