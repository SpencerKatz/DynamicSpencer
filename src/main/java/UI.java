import java.io.IOException;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import org.web3j.protocol.exceptions.TransactionException;

public class UI {

  private final User user;
  private final Stage stage;
  private final Label infoLabel = new Label("");
  private ComboBox<String> walletComboBox;
  private final TextField messageInput = new TextField();
  private final Label signedMessageLabel = new Label();
  private final Label balanceLabel = new Label("Balance: ");
  private final TextField addressInput = new TextField();
  private final TextField amountInput = new TextField();
  private final Label transactionStatusLabel = new Label();
  private static final String STYLESHEET = "default.css";
  private static final String DEFAULT_RESOURCE_PACKAGE = "stylesheets/";
  private static final String DEFAULT_RESOURCE_FOLDER = "/" + DEFAULT_RESOURCE_PACKAGE;

  public UI(User user, Stage stage) {
    this.user = user;
    this.stage = stage;
    initializeFields();
    setScene();
  }

  private void initializeFields() {
    addressInput.setPromptText("Enter address");
    amountInput.setPromptText("Enter amount");
  }

  public void setScene() {
    stage.setTitle(user.getUsername());
    Pane pane = new VBox(10);

    pane.getChildren().addAll(
        createWalletSelectionPane(),
        createButtonPane(),
        infoLabel,
        new Label("Message:"), messageInput, signedMessageLabel,
        balanceLabel,
        new Label("Address:"), addressInput,
        new Label("Amount:"), amountInput,
        transactionStatusLabel
    );

    Scene scene = new Scene(pane, 800, 600);
    scene.getStylesheets().add(getClass().getResource(DEFAULT_RESOURCE_FOLDER + STYLESHEET).toExternalForm());
    stage.setScene(scene);
    stage.show();
  }

  private Pane createWalletSelectionPane() {
    walletComboBox = new ComboBox<>();
    loadWalletNames();
    Button selectButton = new Button("Open Wallet");
    selectButton.setOnAction(e -> setWalletPasswordScene(walletComboBox.getValue()));

    VBox walletSelectionPane = new VBox(10);
    walletSelectionPane.getChildren().addAll(new Label("Select Wallet:"), walletComboBox, selectButton);
    return walletSelectionPane;
  }

  private Pane createButtonPane() {
    List<Button> buttons = makeButtons();
    HBox buttonPane = new HBox(10);
    buttonPane.getChildren().addAll(buttons);
    return buttonPane;
  }

  private List<Button> makeButtons() {
    List<Button> buttons = new ArrayList<>();
    buttons.add(createButton("Get Balance", e -> updateBalance()));
    buttons.add(createButton("Sign Message", e -> signMessage()));
    buttons.add(createButton("Create new Wallet", e -> setMakeNewWalletScene()));
    buttons.add(createButton("Send Transaction", e -> sendTransaction()));
    return buttons;
  }

  private Button createButton(String label, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
    Button button = new Button(label);
    button.setOnAction(action);
    return button;
  }

  private void updateBalance() {
    try {
      double balance = user.getBalance();
      balanceLabel.setText("Balance: " + balance);
    } catch (NullPointerException npe) {
      displayError("Please load a wallet first.");
    } catch (Exception e) {
      displayError(e.getMessage());
    }
  }

  private void signMessage() {
    try {
      String signedMessage = user.signMessage(messageInput.getText());
      signedMessageLabel.setText("Signed Message: " + signedMessage);
    } catch (NullPointerException npe) {
      displayError("Please load a wallet first.");
    } catch (Exception e) {
      displayError(e.getMessage());
    }
  }

  private void sendTransaction() {
    try {
      String transactionStatus =
          user.sendTransaction(addressInput.getText(), Double.parseDouble(amountInput.getText()));
      transactionStatusLabel.setText("Transaction Status: " + transactionStatus);
    } catch (NullPointerException npe) {
      displayError("Please load a wallet first.");
    } catch (IOException e) {
      displayError("Unable to contact Ethereum Network");
    } catch (TransactionException e) {
      displayError("Transaction Error");
    } catch (InterruptedException e) {
      displayError("Transaction Interrupted");
    } catch (Exception e) {
      displayError("Unknown Error: Possible errors include an invalid address to send to or insufficient funds.");
    }
  }

  private void displayError(String message) {
    infoLabel.setText("Error: " + message);
  }

  private void loadWalletNames() {
    walletComboBox.getItems().setAll(user.getWalletNames());
  }

  private void setWalletPasswordScene(String walletName) {
    if (walletName == null || walletName.isEmpty()) {
      displayError("Please select a wallet first.");
      return;
    }

    Pane pane = new VBox(10);
    Scene walletScene = new Scene(pane, 400, 400);
    Label passwordLabel = new Label("Password for " + walletName + ":");

    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Enter your password");
    passwordField.setMaxWidth(200);

    Button confirmButton = new Button("Confirm");
    confirmButton.setOnAction(event -> {
      try {
        user.loadWallet(walletName, passwordField.getText());
        infoLabel.setText("Wallet loaded successfully.");
      } catch (Exception e) {
        displayError("Failed to load wallet: " + e.getMessage());
      }
      setScene();
    });

    Button backButton = createBackButton();
    pane.getChildren().addAll(passwordLabel, passwordField, confirmButton, backButton);
    stage.setScene(walletScene);
  }

  private void setMakeNewWalletScene() {
    Pane pane = new VBox(10);
    Scene walletScene = new Scene(pane, 400, 400);
    Label passwordLabel = new Label("Password:");

    PasswordField password = new PasswordField();
    password.setPromptText("Enter Password for your new wallet");
    password.setMaxWidth(200);

    Button confirmButton = new Button("Confirm");
    confirmButton.setOnAction(event -> {
      try {
        user.createNewAccount(password.getText());
        infoLabel.setText("New wallet created successfully.");
      } catch (Exception e) {
        displayError("Failed to create wallet: " + e.getMessage());
      }
    });

    Button backButton = createBackButton();
    pane.getChildren().addAll(passwordLabel, password, confirmButton, backButton);
    stage.setScene(walletScene);
  }

  private Button createBackButton() {
    Button backButton = new Button("Back");
    backButton.setOnAction(event -> setScene());
    return backButton;
  }
}


