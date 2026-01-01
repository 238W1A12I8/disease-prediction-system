package com.example.diseaseprediction.service;

import com.example.diseaseprediction.model.Prediction;
import com.example.diseaseprediction.model.User;
import com.example.diseaseprediction.repository.PredictionRepository;
import com.example.diseaseprediction.repository.UserRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(102, 126, 234);
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(118, 75, 162);

    public ReportService(PredictionRepository predictionRepository, UserRepository userRepository) {
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
    }

    public byte[] generatePredictionReport(Long predictionId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new RuntimeException("Prediction not found"));
        
        // Verify the prediction belongs to the user
        if (!prediction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        return generateSinglePredictionPdf(prediction, user);
    }

    public byte[] generateUserHistoryReport() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Prediction> predictions = predictionRepository.findByUserEmailOrderByCreatedAtDesc(email);
        
        return generateHistoryPdf(predictions, user);
    }

    private byte[] generateSinglePredictionPdf(Prediction prediction, User user) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // Header
            addHeader(document, "Disease Prediction Report");
            
            // User Info
            document.add(new Paragraph("Patient Information")
                    .setFontSize(14)
                    .setFontColor(PRIMARY_COLOR)
                    .setBold()
                    .setMarginTop(20));
            
            Table userTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .useAllAvailableWidth();
            addTableRow(userTable, "Name:", user.getName());
            addTableRow(userTable, "Email:", user.getEmail());
            addTableRow(userTable, "Date:", prediction.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")));
            document.add(userTable);
            
            // Prediction Result
            document.add(new Paragraph("Prediction Result")
                    .setFontSize(14)
                    .setFontColor(PRIMARY_COLOR)
                    .setBold()
                    .setMarginTop(20));
            
            Table resultTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .useAllAvailableWidth();
            addTableRow(resultTable, "Predicted Disease:", prediction.getDisease().getDiseaseName());
            addTableRow(resultTable, "Confidence:", String.format("%.1f%%", prediction.getConfidence() * 100));
            document.add(resultTable);
            
            // Disease Information
            document.add(new Paragraph("Disease Information")
                    .setFontSize(14)
                    .setFontColor(PRIMARY_COLOR)
                    .setBold()
                    .setMarginTop(20));
            
            if (prediction.getDisease().getDescription() != null) {
                document.add(new Paragraph("Description:")
                        .setFontSize(11)
                        .setBold());
                document.add(new Paragraph(prediction.getDisease().getDescription())
                        .setFontSize(10));
            }
            
            if (prediction.getDisease().getPrecautions() != null) {
                document.add(new Paragraph("Precautions:")
                        .setFontSize(11)
                        .setBold()
                        .setMarginTop(10));
                document.add(new Paragraph(prediction.getDisease().getPrecautions())
                        .setFontSize(10));
            }
            
            // Disclaimer
            addDisclaimer(document);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
        
        return baos.toByteArray();
    }

    private byte[] generateHistoryPdf(List<Prediction> predictions, User user) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // Header
            addHeader(document, "Prediction History Report");
            
            // User Info
            document.add(new Paragraph("Patient: " + user.getName() + " (" + user.getEmail() + ")")
                    .setFontSize(11)
                    .setMarginTop(10));
            document.add(new Paragraph("Total Predictions: " + predictions.size())
                    .setFontSize(11));
            
            // Predictions Table
            if (!predictions.isEmpty()) {
                document.add(new Paragraph("Prediction History")
                        .setFontSize(14)
                        .setFontColor(PRIMARY_COLOR)
                        .setBold()
                        .setMarginTop(20));
                
                Table table = new Table(UnitValue.createPercentArray(new float[]{25, 40, 15, 20}))
                        .useAllAvailableWidth();
                
                // Header row
                table.addHeaderCell(createHeaderCell("Date"));
                table.addHeaderCell(createHeaderCell("Disease"));
                table.addHeaderCell(createHeaderCell("Confidence"));
                table.addHeaderCell(createHeaderCell("Precautions"));
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                
                for (Prediction p : predictions) {
                    table.addCell(new Cell().add(new Paragraph(p.getCreatedAt().format(formatter)).setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(p.getDisease().getDiseaseName()).setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(String.format("%.0f%%", p.getConfidence() * 100)).setFontSize(9)));
                    String precautions = p.getDisease().getPrecautions();
                    if (precautions != null && precautions.length() > 30) {
                        precautions = precautions.substring(0, 30) + "...";
                    }
                    table.addCell(new Cell().add(new Paragraph(precautions != null ? precautions : "-").setFontSize(9)));
                }
                
                document.add(table);
            } else {
                document.add(new Paragraph("No predictions found.")
                        .setFontSize(11)
                        .setItalic()
                        .setMarginTop(20));
            }
            
            // Disclaimer
            addDisclaimer(document);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
        
        return baos.toByteArray();
    }

    private void addHeader(Document document, String title) {
        document.add(new Paragraph("Disease Prediction System")
                .setFontSize(20)
                .setFontColor(PRIMARY_COLOR)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        
        document.add(new Paragraph(title)
                .setFontSize(16)
                .setFontColor(SECONDARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
    }

    private void addTableRow(Table table, String label, String value) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFontSize(10).setBold())
                .setBorder(null)
                .setPadding(5));
        table.addCell(new Cell()
                .add(new Paragraph(value).setFontSize(10))
                .setBorder(null)
                .setPadding(5));
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(10).setBold())
                .setBackgroundColor(PRIMARY_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(5);
    }

    private void addDisclaimer(Document document) {
        document.add(new Paragraph("Medical Disclaimer")
                .setFontSize(12)
                .setBold()
                .setFontColor(new DeviceRgb(231, 76, 60))
                .setMarginTop(30));
        
        document.add(new Paragraph(
                "This report is generated by an AI-based disease prediction system for informational purposes only. " +
                "It is NOT a substitute for professional medical advice, diagnosis, or treatment. " +
                "Always seek the advice of your physician or other qualified health provider with any questions " +
                "you may have regarding a medical condition. Never disregard professional medical advice or delay " +
                "in seeking it because of information contained in this report.")
                .setFontSize(8)
                .setItalic()
                .setFontColor(ColorConstants.GRAY));
        
        document.add(new Paragraph("Generated on: " + java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm:ss")))
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20));
    }
}
