package com.tosan.application.extensions.exporters;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvExporter {
    public static <T> void export(HttpServletResponse response, Class<T> clazz, List<T> list) throws IOException {
        var fields = clazz.getDeclaredFields();

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT)) {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                var fieldName = fields[i].getName();

                csvPrinter.print(fieldName);
            }
            csvPrinter.println();


            for (var item : list) {
                for (var field : fields) {
                    field.setAccessible(true);

                    Object fieldValue = null;
                    try {
                        fieldValue = field.get(item);
                    } catch (IllegalAccessException ex) {
                        // ignore
                    }

                    csvPrinter.print(fieldValue);
                }

                csvPrinter.println();
            }
        }
    }
}
