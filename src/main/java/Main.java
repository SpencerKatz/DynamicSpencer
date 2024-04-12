import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

  private static final int startSceneSize = 400;
  private static final int loginSpacing = 10;
  private static final double loginTextAreaMultiplier = 2.0 / 3.0;
  public static final String stylesheet = "default.css";
  public static final String DEFAULT_RESOURCE_PACKAGE = "stylesheets.";
  public static final String DEFAULT_RESOURCE_FOLDER =
      "/" + DEFAULT_RESOURCE_PACKAGE.replace(".", "/");
  private final XMLParser xmlParser = new XMLParser();

  @Override
  public void start(Stage primaryStage) {
    VBox pane = setupUI(primaryStage);
    Scene scene = new Scene(pane, startSceneSize, startSceneSize);
    scene.getStylesheets()
        .add(getClass().getResource(DEFAULT_RESOURCE_FOLDER + stylesheet).toExternalForm());
    primaryStage.setTitle("Login Page");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private VBox setupUI(Stage stage) {
    VBox pane = new VBox(loginSpacing);
    pane.setAlignment(Pos.CENTER);

    TextField username = new TextField();
    username.setMaxWidth(loginTextAreaMultiplier * startSceneSize);
    username.setPromptText("Enter your username");

    PasswordField password = new PasswordField();
    password.setPromptText("Enter your password");
    password.setMaxWidth(loginTextAreaMultiplier * startSceneSize);

    Button loginButton = new Button("Login");
    Button signUpButton = new Button("SignUp");

    loginButton.setOnAction(e -> handleLogin(username.getText(), password.getText(), stage));
    signUpButton.setOnAction(e -> handleSignUp(username.getText(), password.getText()));

    pane.getChildren().addAll(
        new Label("Username:"), username,
        new Label("Password:"), password,
        loginButton, signUpButton
    );

    return pane;
  }

  private void handleLogin(String username, String password, Stage stage) {
    if (xmlParser.validPassword(username, password)) {
      UI ui = new UI(new User(username), stage);
    } else {
      showError("Invalid Username or Password");
    }
  }

  private void handleSignUp(String username, String password) {
    try {
      if (!xmlParser.existingUsername(username)) {
        xmlParser.newUser(username, password);
        showConfirmation("User created successfully. Please login.");
      } else {
        showError("Username Already Exists");
      }
    } catch (Exception e) {
      showError("An error occurred: " + e.getMessage());
    }
  }

  private void showError(String message) {
    showAlert(Alert.AlertType.ERROR, "Error", message);
  }

  private void showConfirmation(String message) {
    showAlert(Alert.AlertType.INFORMATION, "Success", message);
  }

  private void showAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.initModality(Modality.APPLICATION_MODAL);
    alert.setTitle(title);
    Label messageLabel = new Label(message);
    messageLabel.setWrapText(true);
    alert.getDialogPane().setContent(messageLabel);
    alert.showAndWait();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

