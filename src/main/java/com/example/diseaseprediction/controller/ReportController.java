package com.example.diseaseprediction.controller;

import com.example.diseaseprediction.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "PDF report generation endpoints")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/prediction/{id}")
    @Operation(summary = "Generate PDF report for a specific prediction")
    public ResponseEntity<byte[]> getPredictionReport(@PathVariable Long id) {
        byte[] pdfBytes = reportService.generatePredictionReport(id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prediction-report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/history")
    @Operation(summary = "Generate PDF report of user's prediction history")
    public ResponseEntity<byte[]> getHistoryReport() {
        byte[] pdfBytes = reportService.generateUserHistoryReport();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prediction-history.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
