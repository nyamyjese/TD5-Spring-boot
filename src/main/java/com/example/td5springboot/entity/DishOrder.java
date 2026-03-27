package com.example.td5springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DishOrder {
    private Integer id;
    private Dish dish;
    private Integer quantity;
}
