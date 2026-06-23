async function cancelarReserva(id) {
  if (!confirm(`¿Cancelar la reserva #${id}?`)) return;
  try {
    const response = await apiFetch(`/api/reservas/${id}/cancelar`, { method: "PATCH" });
    const data = await response.json();
    alert(data.mensaje + " — Estado: " + data.estado);
    location.reload();
  } catch (err) {
    alert("No se pudo cancelar: " + err.message);
  }
}