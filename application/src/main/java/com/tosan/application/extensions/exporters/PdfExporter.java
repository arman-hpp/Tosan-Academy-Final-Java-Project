package com.tosan.application.extensions.exporters;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.List;

public class PdfExporter implements IExporter {
    @Async
    public <T> void export(HttpServletResponse response, Class<T> clazz, List<T> list) throws IOException {
        var fields = clazz.getDeclaredFields();

        var outputStream = response.getOutputStream();
        var document = new Document(PageSize.A4.rotate(), 3f, 3f, 10f, 0f);

        try{
            PdfWriter.getInstance(document, outputStream);
        } catch (DocumentException ex) {
            throw new IOException(ex);
        }

        document.open();

        var table = new PdfPTable(fields.length);

        for (var field : fields) {
            field.setAccessible(true);
            var fieldName = field.getName();

            var header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(fieldName));
            table.addCell(header);
        }

        for (var item : list) {
            for (var field : fields) {
                field.setAccessible(true);

                Object fieldValue = null;
                try {
                    fieldValue = field.get(item);
                } catch (IllegalAccessException ex) {
                    // ignore
                }

                table.addCell(String.valueOf(fieldValue));
            }
        }


        try {
            document.add(table);
        } catch (DocumentException ex) {
            throw new IOException(ex);
        }

        document.close();

        outputStream.flush();
        outputStream.close();
    }
}
