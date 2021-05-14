package org.devoware.names.application;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class NameGeneratorMain extends Application {
  @Override
  public void start(Stage primaryStage) {
    try {
      primaryStage.setTitle("Xanathar Name Generator App");

      BorderPane pane = new BorderPane();
      Scene scene = new Scene(pane, 400, 400);

      VBox generatorBox = new VBox();
      generatorBox.setSpacing(10);
      generatorBox.setPadding(new Insets(20, 20, 20, 20));

      Label generatorLabel = new Label("Name Generator:");

      ObservableList<NameGenerator> items =
          FXCollections.observableArrayList(Arrays.stream(NameGenerator.values())
              .collect(Collectors.toList()));
      ComboBox<NameGenerator> generatorField = new ComboBox<>(items);

      Button generate = new Button("Generate");
      generate.setDefaultButton(true);
      generate.setDisable(true);

      generatorField.setOnAction(e -> {
        generate.setDisable(false);
      });

      generate.setOnAction(e -> {
        NameGenerator ng = generatorField.getValue();
        System.out.println(ng.getTitle());
        System.out.println();
        for (int i = 0; i < 10; i++) {
          System.out.println(ng.generateName());
        }
        System.out.println();
      });

      generatorBox.getChildren().addAll(generatorLabel, generatorField, generate);

      pane.setTop(generatorBox);


      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
