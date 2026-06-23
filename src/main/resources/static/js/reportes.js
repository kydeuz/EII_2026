async function exportarPdf() {
  try {
    const response = await apiFetch("/api/reportes/pdf");
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "reporte.pdf";
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  } catch (err) {
    alert("No se pudo descargar el PDF: " + err.message);
  }
}