package com.example.td5springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateDishRequest {
    private String name;
    private DishTypeEnum dishType;
    private Double price;
}
