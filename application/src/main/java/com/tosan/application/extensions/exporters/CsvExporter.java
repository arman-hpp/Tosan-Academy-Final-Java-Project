package com.tosan.application.extensions.exporters;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.List;

public class CsvExporter implements IExporter {
    @Async
    public <T> void export(HttpServletResponse response, Class<T> clazz, List<T> list) throws IOException {
        var fields = clazz.getDeclaredFields();
        try (var csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT)) {
            for (var value : fields) {
                value.setAccessible(true);
                var fieldName = value.getName();

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
