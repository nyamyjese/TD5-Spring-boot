package com.example.td5springboot.controller;

import com.example.td5springboot.entity.Ingredient;
import com.example.td5springboot.entity.StockValue;
import com.example.td5springboot.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Integer id) {
        return ResponseEntity.ok(ingredientService.getIngredientById(id));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<StockValue> getIngredientStock(
            @PathVariable Integer id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {
        return ResponseEntity.ok(ingredientService.getIngredientStock(id, at, unit));
    }
}
