package com.recipeapi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class RecipeDAO {
    private static final String INSERT_RECIPE_SQL = "INSERT INTO SAVED_RECIPES (savedName, imageUrl, savedLink) VALUES (?, ?, ?)";

    public boolean insertRecipe(Recipe recipe) {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_RECIPE_SQL)) {
            statement.setString(1, recipe.getSavedName());
            statement.setString(2, recipe.getImageUrl());
            statement.setString(3, recipe.getSavedLink());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
