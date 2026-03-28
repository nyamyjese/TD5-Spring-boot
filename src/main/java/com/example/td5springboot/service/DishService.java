package com.example.td5springboot.service;

import com.example.td5springboot.entity.Dish;
import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.repository.DishRepository;
import com.example.td5springboot.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class DishService {
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(DishRepository dishRepository, IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Dish getDishById(Integer id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish.id=" + id + " is not found"));
    }

    public Dish updateDishIngredients(Integer dishId , List<Ingredient> ingredientsFromRequest){
        if(ingredientsFromRequest == null){
            throw new RuntimeException("Request body is null");
        }

        getDishById(dishId);

        List<Ingredient> validateIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredientsFromRequest) {
            if(ingredient.getId() != null){
                ingredientRepository.findById(ingredient.getId()).ifPresent(validateIngredients::add);
            }
        }

        return dishRepository.updateIngredients(dishId, validateIngredients);
    }
}
