package com.tosan.application.extensions.exporters;

public interface IExporterFactory {
    IExporter CreateExporter(ExportTypes exportType);
}
