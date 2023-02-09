package com.tosan.application.extensions.exporters;

public enum ExportTypes {
    Excel(".xlsx"),
    CSV(".csv"),
    PDF(".pdf");

    private final String fileExtension;

    ExportTypes(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
