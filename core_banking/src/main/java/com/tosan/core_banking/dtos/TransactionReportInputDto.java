package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class TransactionReportInputDto extends BaseDto {
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fromDate;
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime toDate;
}
