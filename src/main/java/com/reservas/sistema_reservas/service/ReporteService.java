package com.reservas.sistema_reservas.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.reservas.sistema_reservas.entity.Reserva;
import com.reservas.sistema_reservas.repository.ReservaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ReservaRepository reservaRepository;

    public ByteArrayInputStream generarPdf() {
        List<Reserva> lista = reservaRepository.findAll();
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Título del PDF
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("REPORTE GENERAL DE RESERVAS", fontTitulo);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Tabla de 5 columnas
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            // Cabeceras de la tabla
            table.addCell("ID");
            table.addCell("Usuario");
            table.addCell("Laboratorio");
            table.addCell("Fecha");
            table.addCell("Estado");

            // Llenado de datos con validación de nulos (Null-Safe)
            for (Reserva r : lista) {
                table.addCell(r.getId().toString());
                table.addCell(r.getUsuario() != null ? r.getUsuario().getNombre() : "N/A");
                table.addCell(r.getLaboratorio() != null ? r.getLaboratorio().getNombre() : "N/A");
                table.addCell(r.getFecha() != null ? r.getFecha().toString() : "N/A");
                table.addCell(r.getEstado() != null ? r.getEstado().name() : "N/A");
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generarExcel() {
        List<Reserva> lista = reservaRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reservas");
            Row header = sheet.createRow(0);
            String[] columnas = {"ID", "Usuario", "Laboratorio", "Fecha", "Estado"};
            for(int i=0; i<columnas.length; i++) header.createCell(i).setCellValue(columnas[i]);

            int rowIdx = 1;
            for (Reserva r : lista) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getId());
                row.createCell(1).setCellValue(r.getUsuario() != null ? r.getUsuario().getNombre() : "N/A");
                row.createCell(2).setCellValue(r.getLaboratorio() != null ? r.getLaboratorio().getNombre() : "N/A");
                row.createCell(3).setCellValue(r.getFecha() != null ? r.getFecha().toString() : "N/A");
                row.createCell(4).setCellValue(r.getEstado() != null ? r.getEstado().name() : "N/A");
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel");
        }
    }
}