package com.example.td5springboot.service;

import com.example.td5springboot.entity.Dish;
import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.exception.NotFoundException;
import com.example.td5springboot.repository.DishRepository;
import com.example.td5springboot.repository.IngredientRepository;
import com.example.td5springboot.validator.DishValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class DishService {
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;
    private final DishValidator dishValidator;

    public DishService(DishRepository dishRepository, IngredientRepository ingredientRepository, DishValidator dishValidator) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
        this.dishValidator = dishValidator;
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public void getDishById(Integer id) {
        dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish.id=" + id + " is not found"));
    }

    public Dish updateDishIngredients(Integer dishId, List<Ingredient> ingredientsFromRequest) {
        dishValidator.validateIngredientsList(ingredientsFromRequest);

        getDishById(dishId);

        List<Ingredient> validIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredientsFromRequest) {
            if (ingredient.getId() != null) {
                ingredientRepository.findById(ingredient.getId()).ifPresent(validIngredients::add);
            }
        }

        return dishRepository.updateIngredients(dishId, validIngredients);
    }
}
