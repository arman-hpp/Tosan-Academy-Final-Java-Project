package com.tosan.application.extensions.exporters;

public class ExporterFactory implements IExporterFactory{
    public IExporter CreateExporter(ExportTypes exportType) {
        if (exportType == ExportTypes.Excel) {
            return new ExcelExporter();
        } else if (exportType == ExportTypes.CSV) {
            return new CsvExporter();
        } else if (exportType == ExportTypes.PDF) {
            return new PdfExporter();
        } else {
            return new CsvExporter();
        }
    }
}
