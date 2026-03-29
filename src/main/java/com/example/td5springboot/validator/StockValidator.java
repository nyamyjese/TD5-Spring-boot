package com.example.td5springboot.validator;


import com.example.td5springboot.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class StockValidator {

    public void validateStockParams(String at, String unit) {
        if (at == null || at.isBlank()) {
            throw new BadRequestException("Either mandatory query parameter `at` or `unit` is not provided.");
        }
        if (unit == null || unit.isBlank()) {
            throw new BadRequestException("Either mandatory query parameter `at` or `unit` is not provided.");
        }
    }
}
