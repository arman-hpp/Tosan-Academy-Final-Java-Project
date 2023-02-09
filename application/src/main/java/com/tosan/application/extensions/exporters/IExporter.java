package com.tosan.application.extensions.exporters;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.List;

public interface IExporter {
    @Async
    <T> void export(HttpServletResponse response, Class<T> clazz, List<T> list) throws IOException;
}
