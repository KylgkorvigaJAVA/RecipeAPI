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
import java.util.Random;


public class Controller {

    Image img;
    String name, link;

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
    void initialize() {
        ObservableList<String> mealTypes = FXCollections.observableArrayList( "Breakfast", "Dinner", "Lunch", "Snack", "Desserts");

        mealTypeComboBox.setItems(mealTypes);
        searchButton.setOnAction(this::getRecipe);
        linkToRecipe.setOnAction(this::openRecipeLink);
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
        RecipeDAO recipeDAO = new RecipeDAO();
        Recipe recipe = new Recipe();
        recipe.setSavedName(name);
        recipe.setImageUrl(img.getUrl());
        recipe.setSavedLink(link);

        boolean saved = recipeDAO.insertRecipe(recipe);

        if (saved) {
            System.out.println("Recipe saved successfully");
        } else {
            System.out.println("Recipe could not be saved");
        }
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
        String formattedUserInput = userInput.trim().replaceAll("\\s+", "%20");

        String apiUrl = "https://api.edamam.com/api/recipes/v2?type=public&beta=false&q="
                + formattedUserInput +
                "&app_id=a7cab5fb&app_key=06e2c24ca99233810f55eb57ef9c273e";
        if (type != null && !type.isEmpty()) {
            if(type.equals("Desserts")) {
                apiUrl += "&dishType=" + type;
            } else {
                apiUrl += "&mealType=" + type;
            }
        }
        return apiUrl;
    }

    private void displayRecipeInfo() {

        imgV.setImage(img);

        recipeName.setText(name);

        linkToRecipe.setText(link);
    }
}