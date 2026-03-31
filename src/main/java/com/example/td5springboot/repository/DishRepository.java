package com.example.td5springboot.repository;

import com.example.td5springboot.entity.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    private final JdbcTemplate jdbcTemplate;

    public DishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Dish> findAll() {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish ORDER BY id";
        List<Dish> dishes = jdbcTemplate.query(sql, (rs, rowNum) -> mapDish(rs));
        dishes.forEach(dish -> dish.setDishIngredients(findIngredientsByDishId(dish.getId())));
        return dishes;
    }

    public Optional<Dish> findById(Integer id) {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";
        List<Dish> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Dish dish = mapDish(rs);
            dish.setDishIngredients(findIngredientsByDishId(dish.getId()));
            return dish;
        }, id);
        return results.stream().findFirst();
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM dish WHERE LOWER(name) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    @Transactional
    public Dish save(CreateDishRequest request) {
        String sql = """
                INSERT INTO dish (name, dish_type, selling_price)
                VALUES (?, ?::dish_type, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getName());
            ps.setString(2, request.getDishType().name());
            if (request.getPrice() != null) {
                ps.setDouble(3, request.getPrice());
            } else {
                ps.setNull(3, java.sql.Types.NUMERIC);
            }
            return ps;
        }, keyHolder);

        Integer generatedId = keyHolder.getKey().intValue();
        return findById(generatedId)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved dish"));
    }

    public List<DishIngredient> findIngredientsByDishId(Integer dishId) {
        String sql = """
                SELECT i.id, i.name, i.price, i.category,
                       di.required_quantity, di.unit
                FROM ingredient i
                JOIN dish_ingredient di ON di.id_ingredient = i.id
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
                INSERT INTO dish_ingredient (id_dish, id_ingredient, required_quantity, unit)
                VALUES (?, ?, 0, 'KG')
                """;
        for (Ingredient ingredient : ingredients) {
            jdbcTemplate.update(attachSql, dishId, ingredient.getId());
        }
        return findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish.id=" + dishId + " is not found"));
    }

    private Dish mapDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
        double price = rs.getDouble("selling_price");
        dish.setPrice(rs.wasNull() ? null : price);
        return dish;
    }
}