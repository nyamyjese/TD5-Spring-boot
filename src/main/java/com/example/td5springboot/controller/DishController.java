package com.example.td5springboot.controller;

import com.example.td5springboot.entity.Dish;
import com.example.td5springboot.entity.DishIngredient;
import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes(){
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<Dish> updateIngredient(
            @PathVariable Integer id,
            @RequestBody(required = false) List<Ingredient> ingredients){
        return ResponseEntity.ok(dishService.updateDishIngredients(id, ingredients));
    }
}
