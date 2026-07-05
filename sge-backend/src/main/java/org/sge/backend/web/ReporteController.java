package org.sge.backend.web;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.response.*;
import org.sge.backend.service.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @GetMapping("/notas")
    public ResponseEntity<byte[]> reporteNotas(
            @RequestParam(required = false) Long cursoId,
            @RequestParam(required = false) Long bimestreId) throws Exception {
        var bytes = service.generarReporteNotas(cursoId, bimestreId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_notas.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(bytes);
    }
}
