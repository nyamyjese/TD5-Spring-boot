package com.example.td5springboot.validator;

import com.example.td5springboot.entity.CreateDishRequest;
import com.example.td5springboot.entity.Dish;
import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DishValidator {
    public void validate(Dish dish) {
        if (dish.getName() == null || dish.getName().isBlank()) {
            throw new BadRequestException("NewDish.name cannot be null");
        }
        if (dish.getDishType() == null) {
            throw new BadRequestException("NewDish.dishType cannot be null");
        }
        if (dish.getPrice() != null && dish.getPrice() < 0) {
            throw new BadRequestException("NewDish.price cannot be negative");
        }
    }

    public void validateCreateRequest(CreateDishRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("NewDish.name cannot be null");
        }
        if (request.getDishType() == null) {
            throw new BadRequestException("NewDish.dishType cannot be null");
        }
        if (request.getPrice() != null && request.getPrice() < 0) {
            throw new BadRequestException("NewDish.price cannot be negative");
        }
    }

    public void validateIngredientsList(List<Ingredient> ingredients) {
        if (ingredients == null) {
            throw new BadRequestException("Request body is mandatory");
        }
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getId() == null) {
                throw new BadRequestException("NewDishIngredient.id cannot be null");
            }
        }
    }
}