package com.example.td5springboot.repository;

import com.example.td5springboot.entity.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final JdbcTemplate jdbcTemplate;

    public IngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ingredient> findAll() {
        String sql = "SELECT id, name, price, category FROM ingredient ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapIngredientWithoutStock(rs));
    }

    public Optional<Ingredient> findById(Integer id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";
        List<Ingredient> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Ingredient ingredient = mapIngredientWithoutStock(rs);
            ingredient.setStockMovementList(findStockMovementsByIngredientId(ingredient.getId()));
            return ingredient;
        }, id);
        return results.stream().findFirst();
    }

    public List<StockMovement> findStockMovementsByIngredientId(Integer ingredientId) {
        String sql = """
                SELECT id, quantity, unit, type, creation_datetime
                FROM stock_movement
                WHERE id_ingredient = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StockMovement movement = new StockMovement();
            movement.setId(rs.getInt("id"));
            movement.setType(MovementTypeEnum.valueOf(rs.getString("type")));
            movement.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

            StockValue stockValue = new StockValue();
            stockValue.setQuantity(rs.getDouble("quantity"));
            stockValue.setUnit(Unit.valueOf(rs.getString("unit")));
            movement.setValue(stockValue);

            return movement;
        }, ingredientId);
    }

    public Optional<StockMovement> findStockAtDateWithUnit(Integer ingredientId, String at, String unit) {
        String sql = """
                SELECT id, quantity, unit, type, creation_datetime
                FROM stock_movement
                WHERE id_ingredient = ?
                  AND creation_datetime <= ?::timestamp
                  AND unit = ?::unit
                ORDER BY creation_datetime DESC
                LIMIT 1
                """;
        List<StockMovement> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            StockMovement movement = new StockMovement();
            movement.setId(rs.getInt("id"));
            movement.setType(MovementTypeEnum.valueOf(rs.getString("type")));
            movement.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

            StockValue stockValue = new StockValue();
            stockValue.setQuantity(rs.getDouble("quantity"));
            stockValue.setUnit(Unit.valueOf(rs.getString("unit")));
            movement.setValue(stockValue);

            return movement;
        }, ingredientId, at, unit);
        return results.stream().findFirst();
    }


    public Double calculateStockValue(Integer ingredientId, String at, String unit) {
        String sql = """
                SELECT
                    COALESCE(SUM(CASE WHEN type = 'IN'  THEN quantity ELSE 0 END), 0)
                  - COALESCE(SUM(CASE WHEN type = 'OUT' THEN quantity ELSE 0 END), 0) AS stock_value
                FROM stock_movement
                WHERE id_ingredient = ?
                  AND creation_datetime <= ?::timestamp
                  AND unit = ?::unit
                """;
        Double result = jdbcTemplate.queryForObject(sql, Double.class, ingredientId, at, unit);
        return result != null ? result : 0.0;
    }

    private Ingredient mapIngredientWithoutStock(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("id"));
        ingredient.setName(rs.getString("name"));
        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
        double price = rs.getDouble("price");
        ingredient.setPrice(rs.wasNull() ? null : price);
        return ingredient;
    }
}
