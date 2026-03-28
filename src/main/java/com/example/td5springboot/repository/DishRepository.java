package com.example.td5springboot.repository;

import com.example.td5springboot.entity.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {
    private final JdbcTemplate jdbcTemplate;

    public DishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Dish> findAll() {
        String sql = """
                SELECT dish.id AS dish_id, dish.name AS dish_name, dish_type,
                       dish.selling_price AS dish_price
                FROM dish
                ORDER BY dish.id
                """;
        List<Dish> dishes = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Dish dish = new Dish();
            dish.setId(rs.getInt("dish_id"));
            dish.setName(rs.getString("dish_name"));
            dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
            double price = rs.getDouble("dish_price");
            dish.setPrice(rs.wasNull() ? null : price);
            return dish;
        });

        dishes.forEach(dish -> dish.setDishIngredients(findIngredientsByDishId(dish.getId())));
        return dishes;
    }

    public Optional<Dish> findById(Integer id) {
        String sql = """
                SELECT dish.id AS dish_id, dish.name AS dish_name, dish_type,
                       dish.selling_price AS dish_price
                FROM dish
                WHERE dish.id = ?
                """;
        List<Dish> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Dish dish = new Dish();
            dish.setId(rs.getInt("dish_id"));
            dish.setName(rs.getString("dish_name"));
            dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
            double price = rs.getDouble("dish_price");
            dish.setPrice(rs.wasNull() ? null : price);
            dish.setDishIngredients(findIngredientsByDishId(rs.getInt("dish_id")));
            return dish;
        }, id);
        return results.stream().findFirst();
    }

    public List<DishIngredient> findIngredientsByDishId(Integer dishId) {
        String sql = """
                SELECT ingredient.id, ingredient.name, ingredient.price, ingredient.category,
                       di.required_quantity, di.unit
                FROM ingredient
                JOIN dish_ingredient di ON di.id_ingredient = ingredient.id
                WHERE di.id_dish = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getInt("id"));
            ingredient.setName(rs.getString("name"));
            double price = rs.getDouble("price");
            ingredient.setPrice(rs.wasNull() ? null : price);
            ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

            DishIngredient dishIngredient = new DishIngredient();
            dishIngredient.setIngredient(ingredient);
            double qty = rs.getDouble("required_quantity");
            dishIngredient.setQuantity(rs.wasNull() ? null : qty);
            dishIngredient.setUnit(Unit.valueOf(rs.getString("unit")));

            return dishIngredient;
        }, dishId);
    }

    @Transactional
    public Dish updateIngredients(Integer dishId, List<Ingredient> ingredients) {
        jdbcTemplate.update("DELETE FROM dish_ingredient WHERE id_dish = ?", dishId);

        String attachSql = """
                INSERT INTO dish_ingredient (id, id_ingredient, id_dish, required_quantity, unit)
                VALUES (nextval(pg_get_serial_sequence('dish_ingredient','id')), ?, ?, 0, 'PCS')
                """;

        for (Ingredient ingredient : ingredients) {
            jdbcTemplate.update(attachSql, ingredient.getId(), dishId);
        }

        return findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish.id=" + dishId + " is not found"));
    }
}
