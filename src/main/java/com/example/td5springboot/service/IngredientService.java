package com.example.td5springboot.service;

import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.entity.StockValue;
import com.example.td5springboot.entity.Unit;
import com.example.td5springboot.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class IngredientService {
    private final IngredientRepository ingredientRepository;
    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Integer id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient.id=" + id + " is not found"));
    }

    public StockValue getIngredientStock(Integer id , String at , String unit){
        if(at == null || at.isBlank()){
            throw new RuntimeException("at is null or at is blank");
        }

        if(unit == null || unit.isBlank()){
            throw new RuntimeException("unit is null or unit is blank");
        }

        getIngredientById(id);

        Unit unitEnum;

        try{
            unitEnum = Unit.valueOf(unit.toUpperCase());
        }
        catch(IllegalArgumentException e){
            throw new RuntimeException("unit is null or unit is blank");
        }

        Double stockValue = ingredientRepository.calculateStockValue(id,at,unit.toUpperCase());

        StockValue result = new StockValue();
        result.setQuantity(stockValue);
        result.setUnit(unitEnum);
        return result;
    }
}
