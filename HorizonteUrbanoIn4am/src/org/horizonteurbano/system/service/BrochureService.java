package org.horizonteurbano.system.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.horizonteurbano.system.models.Property;
import java.io.File;

public class BrochureService {

    public boolean exportBrochure(Property property, File file) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 22);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Ficha Detallada - Horizonte Urbano");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
                contentStream.newLineAtOffset(50, 650);
                contentStream.setLeading(25f); 
                
                contentStream.showText("Código Interno: " + property.getInternalCode());
                contentStream.newLine();
                contentStream.showText("Dirección: " + property.getAddress());
                contentStream.newLine();
                contentStream.showText("Tipo de Inmueble: " + (property.getType() != null ? property.getType().getNameType() : "No asignado"));
                contentStream.newLine();
                contentStream.showText("Área: " + property.getArea() + " m2");
                contentStream.newLine();
                contentStream.showText(String.format("Precio de Oferta: $ %,.2f", property.getPrice()));
                contentStream.newLine();
                contentStream.showText("Estado Comercial: " + (property.getState() != null ? property.getState().getNameState() : "No asignado"));
                
                contentStream.endText();
            }
            document.save(file);
            return true;
        } catch (Exception e) {
            System.err.println("Error generando PDF con PDFBox: " + e.getMessage());
            return false;
        }
    }
}