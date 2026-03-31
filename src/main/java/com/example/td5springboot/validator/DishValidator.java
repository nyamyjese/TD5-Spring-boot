package com.example.td5springboot.validator;

import com.example.td5springboot.entity.CreateDishRequest;
import com.example.td5springboot.entity.Dish;
import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DishValidator {
    public void validate (Dish dish) {
        if(dish.getName() == null || dish.getName().isBlank()){
            throw new BadRequestException("NewDish.name cannot be null");
        }

        if(dish.getDishType() == null){
            throw new BadRequestException("NewDish.DishType cannot be null");
        }

        if(dish.getPrice() == null){
            throw new BadRequestException("NewDish.DishPrice cannot be null");
        }

        if(dish.getPrice() <= 0){
            throw new BadRequestException("NewDish.DishPrice cannot be negative");
        }
    }

    public void validateIngredientsList(List<Ingredient> ingredients) {
        if(ingredients == null){
            throw new BadRequestException("Request body is mandatory");
        }

        for(Ingredient ingredient : ingredients){
            if(ingredient.getId() == null){
                throw new BadRequestException("NewDishIngredient.id cannot be null");
            }
        }
    }

    public void validateCreateDishRequest(CreateDishRequest createDishRequest) {
        if(createDishRequest.getName() == null || createDishRequest.getName().isBlank()){
            throw new BadRequestException("NewDishRequest.name cannot be null");
        }

        if(createDishRequest.getDishType() == null){
            throw new BadRequestException("NewDishRequest.dihType cannot be null");
        }

        if(createDishRequest.getPrice() == null){
            throw new BadRequestException("NewDishRequest.price cannot be null");
        }

        if(createDishRequest.getPrice() <= 0){
            throw new BadRequestException("NewDishRequest.price cannot be negative");
        }
    }
}
