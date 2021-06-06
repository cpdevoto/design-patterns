# 1. Download the JavaFX SDK as shown here: https://gluonhq.com/products/javafx/
# 2. Run ./gradlew clean build shadowJar

java --module-path /Users/cdevoto/Tools/javafx-sdk-11.0.2/lib --add-modules=javafx.controls -jar build/libs/name-generator-all.jar