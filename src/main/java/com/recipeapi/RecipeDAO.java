package com.recipeapi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class RecipeDAO {
    private static final String INSERT_RECIPE_SQL = "INSERT INTO SAVED_RECIPES (savedName, imageUrl, savedLink) VALUES (?, ?, ?)";

    public List<Recipe> insertRecipe(Recipe recipe) {
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_RECIPE_SQL)) {
            statement.setString(1, recipe.getSavedName());
            statement.setString(2, recipe.getImageUrl());
            statement.setString(3, recipe.getSavedLink());

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0) {
                return getAllSavedRecipes();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Recipe> getAllSavedRecipes() {
        String SELECT_ALL_RECIPES_SQL = "SELECT id, savedName, imageUrl, savedLink FROM SAVED_RECIPES";

        List<Recipe> savedRecipes = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_RECIPES_SQL)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String savedName = resultSet.getString("savedName");
                String imageUrl = resultSet.getString("imageUrl");
                String savedLink = resultSet.getString("savedLink");

                Recipe recipe = new Recipe(savedName, imageUrl, savedLink);
                recipe.setId(id);
                savedRecipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return savedRecipes;
    }

    public boolean deleteRecipe(int id) {
        String DELETE_RECIPE_SQL = "DELETE FROM SAVED_RECIPES WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_RECIPE_SQL)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
