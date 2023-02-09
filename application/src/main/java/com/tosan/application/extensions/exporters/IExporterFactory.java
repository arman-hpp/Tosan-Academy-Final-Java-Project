package com.tosan.application.extensions.exporters;

import com.tosan.model.ExportTypes;

public interface IExporterFactory {
    IExporter CreateExporter(ExportTypes exportType);
}
