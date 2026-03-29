package com.example.td5springboot.service;

import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.entity.StockValue;
import com.example.td5springboot.entity.Unit;
import com.example.td5springboot.exception.BadRequestException;
import com.example.td5springboot.exception.NotFoundException;
import com.example.td5springboot.repository.IngredientRepository;
import com.example.td5springboot.validator.StockValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final StockValidator stockValidator;

    public IngredientService(IngredientRepository ingredientRepository, StockValidator stockValidator) {
        this.ingredientRepository = ingredientRepository;
        this.stockValidator = stockValidator;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Integer id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient.id=" + id + " is not found"));
    }

    public StockValue getIngredientStock(Integer id, String at, String unit) {
        stockValidator.validateStockParams(at, unit);

        getIngredientById(id);

        Unit unitEnum;
        try {
            unitEnum = Unit.valueOf(unit.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid unit: " + unit);
        }

        Double stockValue = ingredientRepository.calculateStockValue(id, at, unit.toUpperCase());

        StockValue result = new StockValue();
        result.setQuantity(stockValue);
        result.setUnit(unitEnum);
        return result;
    }
}
