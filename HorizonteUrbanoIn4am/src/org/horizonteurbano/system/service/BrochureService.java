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
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                // Encabezado
                contentStream.beginText();
                contentStream.setFont(fontBold, 22);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Horizonte Urbano");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(fontRegular, 12);
                contentStream.newLineAtOffset(50, 730);
                contentStream.showText("Ficha Detallada de Propiedad");
                contentStream.endText();

                contentStream.setLineWidth(1f);
                contentStream.moveTo(50, 715);
                contentStream.lineTo(545, 715);
                contentStream.stroke();

                // Foto de portada (si existe y el archivo es accesible)
                float textStartY = 680;
                if (property.getCoverUrl() != null && !property.getCoverUrl().isBlank()) {
                    try {
                        File imageFile = new File(property.getCoverUrl());
                        if (imageFile.exists()) {
                            org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject image
                                    = org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromFile(imageFile.getPath(), document);

                            float maxWidth = 495f;
                            float maxHeight = 220f;
                            float scale = Math.min(maxWidth / image.getWidth(), maxHeight / image.getHeight());
                            float imgWidth = image.getWidth() * scale;
                            float imgHeight = image.getHeight() * scale;

                            contentStream.drawImage(image, 50, 690 - imgHeight, imgWidth, imgHeight);
                            textStartY = 690 - imgHeight - 30;
                        }
                    } catch (Exception imgEx) {
                        System.err.println("No se pudo cargar la imagen de portada: " + imgEx.getMessage());
                    }
                }

                // Cuerpo
                contentStream.beginText();
                contentStream.setFont(fontRegular, 14);
                contentStream.newLineAtOffset(50, textStartY);
                contentStream.setLeading(28f);

                contentStream.showText("Codigo Interno: " + property.getInternalCode());
                contentStream.newLine();
                contentStream.showText("Direccion: " + property.getAddress());
                contentStream.newLine();
                contentStream.showText("Tipo de Inmueble: " + (property.getType() != null ? property.getType().getNameType() : "No asignado"));
                contentStream.newLine();
                contentStream.showText("Area: " + property.getArea() + " metros cuadrados");
                contentStream.newLine();
                contentStream.showText(String.format("Precio de Oferta: Q %,.2f", property.getPrice()));
                contentStream.newLine();
                contentStream.showText("Estado Comercial: " + (property.getState() != null ? property.getState().getNameState() : "No asignado"));
                contentStream.newLine();

                if (property.getDateRegister() != null) {
                    contentStream.showText("Fecha de Registro: " + property.getDateRegister().toLocalDate());
                    contentStream.newLine();
                }

                contentStream.endText();

                // Pie de página
                contentStream.beginText();
                contentStream.setFont(fontRegular, 9);
                contentStream.newLineAtOffset(50, 40);
                contentStream.showText("Ficha generada el " + java.time.LocalDate.now() + " - Horizonte Urbano");
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
