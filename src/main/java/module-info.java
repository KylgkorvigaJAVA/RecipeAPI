module com.example.recipeapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.desktop;


    opens com.recipeapi to javafx.fxml;
    exports com.recipeapi;
}