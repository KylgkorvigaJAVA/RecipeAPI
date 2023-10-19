package com.recipeapi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Controller {


    @FXML
    private Label recipeNumber;
    @FXML
    private ImageView savedImgV;
    @FXML
    private Hyperlink savedLinkToRecipe;
    @FXML
    private Label savedRecipeName;
    @FXML
    private Label recipeName;
    @FXML
    private ImageView imgV;
    @FXML
    private Hyperlink linkToRecipe;
    @FXML
    private Button searchButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField txtField;
    @FXML
    private ComboBox<String> mealTypeComboBox;
    @FXML
    private Button backButton;

    @FXML
    private Button deleteRecipeButton;

    @FXML
    private Button forwardButton;


    private RecipeDAO recipeDAO = new RecipeDAO();
    List<Recipe> savedRecipes = new ArrayList<>();
    int currentRecipeIndex = 0;
    Image img;
    String name, link;

    @FXML
    void deleteButtonClicked(ActionEvent event) {
        int currentRecipeId = savedRecipes.get(currentRecipeIndex).getId();

        boolean deleted = recipeDAO.deleteRecipe(currentRecipeId);

        if(deleted) {
            savedRecipes.remove(currentRecipeIndex);
            if(!savedRecipes.isEmpty()) {
                if(currentRecipeIndex >= savedRecipes.size()) {
                    currentRecipeIndex = savedRecipes.size() - 1;
                }
                displaySavedRecipe(currentRecipeIndex);
                System.out.println("Deleted Recipe ID: " + currentRecipeId);
            } else {
                System.out.println("No saved recipes!");
            }
        } else {
            System.out.println("Recipe delete failed!");
        }
        refreshSavedRecipes();
    }
    @FXML
    void forwardButtonClicked(ActionEvent event) {
        if(currentRecipeIndex < savedRecipes.size() - 1) {
            currentRecipeIndex++;
            displaySavedRecipe(currentRecipeIndex);
        }
    }
    @FXML
    void backButtonClicked(ActionEvent event) {
        if (currentRecipeIndex > 0) {
            currentRecipeIndex--;
        } else {
            currentRecipeIndex = savedRecipes.size() - 1;
        }
        displaySavedRecipe(currentRecipeIndex);
    }

    public void refreshSavedRecipes() {
        List<Recipe> updateSavedRecipes = recipeDAO.getAllSavedRecipes();
        savedRecipes.clear();
        savedRecipes.addAll(updateSavedRecipes);
        currentRecipeIndex = 0;
        displaySavedRecipe(currentRecipeIndex);
    }
    void displaySavedRecipe(int index) {
        Recipe recipe = savedRecipes.get(index);

        savedRecipeName.setText(recipe.getSavedName());
        savedLinkToRecipe.setText(recipe.getSavedLink());
        Image savedImage = new Image(recipe.getImageUrl());
        savedImgV.setImage(savedImage);

        recipeNumber.setText("Saved Recipe " + (index +1) + " of " + savedRecipes.size());

        System.out.println("currentRecipeIndex: " + currentRecipeIndex);
        System.out.println("Current Recipe ID: " + recipe.getId());
    }

    void initializeSavedRecipes() {
        savedRecipes = recipeDAO.getAllSavedRecipes();

        if(savedRecipes.isEmpty()) {
            backButton.setVisible(false);
            forwardButton.setVisible(false);
            deleteRecipeButton.setVisible(false);
            recipeNumber.setVisible(false);
            savedImgV.setVisible(false);
            savedLinkToRecipe.setVisible(false);
            savedRecipeName.setVisible(false);
        } else {
            backButton.setVisible(true);
            forwardButton.setVisible(true);
            deleteRecipeButton.setVisible(true);
            recipeNumber.setVisible(true);
            savedImgV.setVisible(true);
            savedLinkToRecipe.setVisible(true);
            savedRecipeName.setVisible(true);
            displaySavedRecipe(currentRecipeIndex);
        }
    }

    @FXML
    void initialize() {
        ObservableList<String> mealTypes = FXCollections.observableArrayList( "All", "Breakfast", "Lunch", "Dinner", "Snack", "Desserts");

        mealTypeComboBox.setItems(mealTypes);
        mealTypeComboBox.getSelectionModel().selectFirst();
        searchButton.setOnAction(this::getRecipe);
        linkToRecipe.setOnAction(this::openRecipeLink);
        initializeSavedRecipes();
    }

    private void openRecipeLink(ActionEvent event) {
        String url = linkToRecipe.getText();
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void insertRecipe(ActionEvent event) {
        Recipe recipe = new Recipe(name, img.getUrl(), link);
        recipe.setSavedName(name);
        recipe.setImageUrl(img.getUrl());
        recipe.setSavedLink(link);

        savedRecipes = recipeDAO.insertRecipe(recipe);

        if (savedRecipes != null) {
            System.out.println("Recipe saved successfully");
            System.out.println("Name: " + name);
            System.out.println("Img: " + img.getUrl());
            System.out.println("Link: " + link);
            refreshSavedRecipes();
        } else {
            System.out.println("Recipe could not be saved");
            System.out.println("Name: " + name);
            System.out.println("Img: " + img.getUrl());
            System.out.println("Link: " + link);
        }
        initializeSavedRecipes();
    }

    @FXML
    void getRecipe(ActionEvent event) {
        HttpURLConnection conn = null;
        int responsecode = 0;
        BufferedReader br;
        StringBuilder strBuilder = new StringBuilder();
        saveButton.setVisible(true);

        try {
            String selectedMealType = mealTypeComboBox.getValue();
            String userInput = txtField.getText();
            String apiUrl = buildApiUrl(selectedMealType, userInput);

            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);

            responsecode = conn.getResponseCode();
            System.out.println("HttpResponseCode: " + responsecode);
            System.out.println(apiUrl);
            if (responsecode >= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

            String line;
            while ((line = br.readLine()) != null) {
                strBuilder.append(line);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            Random random = new Random();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(strBuilder.toString());

            JSONObject recipeObject = (JSONObject) ((JSONObject) ((JSONArray) jsonObject.get("hits")).get(random.nextInt(19))).get("recipe");

            link = (String) recipeObject.get("url");
            name = (String) recipeObject.get("label");
            img = new Image((String) recipeObject.get("image"));


            displayRecipeInfo();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String buildApiUrl(String type, String userInput) {
        String formattedUserInput = userInput.trim().replaceAll("[,.\\s]+", "%20");

        String apiUrl = "https://api.edamam.com/api/recipes/v2?type=public&beta=false&q="
                + formattedUserInput +
                "&app_id=a7cab5fb&app_key=06e2c24ca99233810f55eb57ef9c273e";
        if (!type.equals("All")) {
            if(type.equals("Desserts")) {
                apiUrl += "&dishType=" + type;
            } else {
                apiUrl += "&mealType=" + type;
            }
        }
        return apiUrl + "&random=true";
    }

    private void displayRecipeInfo() {

        imgV.setImage(img);

        recipeName.setText(name);

        linkToRecipe.setText(link);
    }
}