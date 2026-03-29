package com.example.td5springboot.validator;

import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class IngredientValidator {
    public void validate (Ingredient ingredient){
        if(ingredient.getName() == null || ingredient.getName().isBlank()){
            throw new BadRequestException("NewIngredient.name cannot be null");
        }

        if(ingredient.getCategory() == null){
            throw new BadRequestException("NewIngredient.category cannot be null");
        }

        if(ingredient.getPrice() == null){
            throw new BadRequestException("NewIngredient.price cannot be null");
        }

        if(ingredient.getPrice() < 0){
            throw new BadRequestException("NewIngredient.price cannot be negative");
        }
    }
}
