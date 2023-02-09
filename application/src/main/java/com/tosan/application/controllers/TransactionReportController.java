package com.tosan.application.controllers;

import com.tosan.application.extensions.exporters.IExporterFactory;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.dtos.TransactionReportInputDto;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.model.ExportTypes;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/transaction_report")
@Layout(title = "Transactions Report", value = "layouts/default")
public class TransactionReportController {
    private final TransactionService _transactionService;
    private final IExporterFactory _exporterFactory;

    public TransactionReportController(TransactionService transactionService, IExporterFactory exporterFactory) {
        _transactionService = transactionService;
        _exporterFactory = exporterFactory;
    }

    @GetMapping("/index")
    public String loadForm(Model model) {
        var fromDateTime = LocalDateTime.now().minusYears(1).toLocalDate().atTime(LocalTime.MIN);
        var toDateTime = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        model.addAttribute("transactionReportInputDto", new TransactionReportInputDto(fromDateTime, toDateTime, ExportTypes.CSV));
        return "transaction_report";
    }

    @GetMapping("/exportTransactions")
    public void exportToExcel(HttpServletResponse response,
                              @ModelAttribute TransactionReportInputDto transactionReportInputDto) {
        response.setContentType("application/octet-stream");

        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        var currentDateTime = LocalDateTime.now().format(dateFormatter);

        String headerKey = "Content-Disposition";
        String headerValue =
                "attachment; filename=transactions_" +
                currentDateTime +
                transactionReportInputDto.getExportType().getFileExtension();
        response.setHeader(headerKey, headerValue);

        var future = _transactionService
                .loadTransactions(transactionReportInputDto.getFromDate(), transactionReportInputDto.getToDate());

        try {
            var transactionDtoList = future.get();

            var exporter = _exporterFactory.CreateExporter(transactionReportInputDto.getExportType());
            exporter.export(response, TransactionDto.class, transactionDtoList);
        }
        catch (IOException | InterruptedException | ExecutionException e) {
             // ignore
        }
    }
}