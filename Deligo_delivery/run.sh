#!/bin/bash

# Define paths
JAVA_FX_LIB_PATH="/Users/user/Downloads/javafx-sdk-21.0.6/lib"
JAR_NAME="target/Deligo_delivery-1.0-SNAPSHOT.jar"

echo "🚀 Starting build process..."

# Step 1: Clean and package the application
mvn clean package

# Step 2: Check if the JAR file was successfully created
if [ ! -f "$JAR_NAME" ]; then
    echo "❌ Build failed: JAR file not found!"
    exit 1
fi

echo "✅ Build successful!"

# Step 3: Run the application with JavaFX module path
echo "🚀 Running the application..."
java --module-path "$JAVA_FX_LIB_PATH" --add-modules javafx.controls,javafx.fxml -jar "$JAR_NAME"
