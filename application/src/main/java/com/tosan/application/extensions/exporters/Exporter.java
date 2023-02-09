package com.tosan.application.extensions.exporters;

import com.tosan.model.ExportTypes;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class Exporter {
    public static <T> void export(ExportTypes exportTypes, HttpServletResponse response, Class<T> clazz, List<T> list) throws IOException {
        if(exportTypes == ExportTypes.Excel) {
            ExcelExporter.export(response, clazz, list);
        } else if (exportTypes == ExportTypes.CSV) {
            CsvExporter.export(response, clazz, list);
        }
    }
}
