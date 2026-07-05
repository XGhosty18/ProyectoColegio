package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final AlumnoRepository alumnoRepo;
    private final CursoRepository cursoRepo;
    private final NotaRepository notaRepo;
    private final AsistenciaAlumnoRepository asistenciaRepo;
    private final EvaluacionRepository evaluacionRepo;
    private final BimestreRepository bimestreRepo;

    public byte[] generarReporteNotas(Long cursoId, Long bimestreId) throws IOException {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Notas");

        var headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        var font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        List<org.sge.backend.model.entity.Curso> cursos;
        if (cursoId != null) {
            cursos = List.of(cursoRepo.findById(cursoId).orElseThrow());
        } else {
            cursos = cursoRepo.findAll();
        }

        int rowNum = 0;
        var header = sheet.createRow(rowNum++);
        String[] cols = {"Alumno", "Curso", "Evaluación", "Tipo", "Bimestre", "Nota"};
        for (int i = 0; i < cols.length; i++) {
            var cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        for (var curso : cursos) {
            List<org.sge.backend.model.entity.Evaluacion> evaluaciones;
            if (bimestreId != null) {
                evaluaciones = evaluacionRepo.findByCursoId(curso.getId()).stream()
                    .filter(e -> e.getBimestre().getId().equals(bimestreId))
                    .toList();
            } else {
                evaluaciones = evaluacionRepo.findByCursoId(curso.getId());
            }

            for (var eval : evaluaciones) {
                var notas = notaRepo.findByEvaluacionId(eval.getId());
                for (var nota : notas) {
                    var row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(nota.getAlumno().getNombres() + " " + nota.getAlumno().getApellidos());
                    row.createCell(1).setCellValue(curso.getMateria().getNombre());
                    row.createCell(2).setCellValue(eval.getNombre());
                    row.createCell(3).setCellValue(eval.getTipoEvaluacion().getNombre());
                    row.createCell(4).setCellValue(eval.getBimestre().getNombre());
                    row.createCell(5).setCellValue(nota.getValor().doubleValue());
                }
            }
        }

        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        var out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
