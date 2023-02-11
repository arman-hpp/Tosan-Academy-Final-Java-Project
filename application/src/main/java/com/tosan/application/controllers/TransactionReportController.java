package com.tosan.application.controllers;

import com.tosan.application.extensions.errors.ControllerErrorParser;
import com.tosan.application.extensions.exporters.ExportTypes;
import com.tosan.application.extensions.exporters.IExporterFactory;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.dtos.TransactionReportInputDto;
import com.tosan.core_banking.services.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class TransactionReportController {
    private final TransactionService _transactionService;
    private final IExporterFactory _exporterFactory;

    public TransactionReportController(TransactionService transactionService, IExporterFactory exporterFactory) {
        _transactionService = transactionService;
        _exporterFactory = exporterFactory;
    }

    @GetMapping({"/","/index"})
    public String loadForm(Model model) {
        var fromDateTime = LocalDateTime.now().minusYears(1).toLocalDate().atTime(LocalTime.MIN);
        var toDateTime = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        model.addAttribute("transactionReportInputDto",
                new TransactionReportInputDto(fromDateTime, toDateTime, ExportTypes.CSV.toString()));

        return "views/admin/transaction_report";
    }

    @GetMapping("/exportTransactions")
    public void exportToExcel(HttpServletResponse response,
                              @ModelAttribute TransactionReportInputDto transactionReportInputDto) {
        response.setContentType("application/octet-stream");

        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        var currentDateTime = LocalDateTime.now().format(dateFormatter);
        var exportType = ExportTypes.valueOf(transactionReportInputDto.getExportType());

        String headerKey = "Content-Disposition";
        String headerValue =
                "attachment; filename=transactions_" +
                currentDateTime +
                        exportType.getFileExtension();
        response.setHeader(headerKey, headerValue);

        var future = _transactionService
                .loadTransactions(transactionReportInputDto.getFromDate(), transactionReportInputDto.getToDate());

        try {
            var transactionDtoList = future.get();

            var exporter = _exporterFactory.CreateExporter(exportType);
            exporter.export(response, TransactionDto.class, transactionDtoList);
        }
        catch (IOException | InterruptedException | ExecutionException ex) {
             try {
                 response.sendRedirect("/transaction_report/index?error=" + ControllerErrorParser.getError(ex));
             } catch (IOException innerEx) {
                // ignore
             }
        }
    }
}
