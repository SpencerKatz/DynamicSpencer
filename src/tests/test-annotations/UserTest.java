import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.web3j.crypto.exception.CipherException;

public class UserTest {

  private User user;
  private final String validPassword = "fun";
  private final String walletName = "UTC--2024-04-11T23-40-09.892441000Z--ea2d2e276033772f09311e0ce64dde5f2f329c17.json";

  @BeforeEach
  void setUp() {
    // This setup assumes that you've initialized User with necessary parameters.
    user = new User("batman"); // Using null for Stage, assuming UI is not involved in the tests
  }

  @Test
  void createUserWithValidCredentials() {
    // This assumes there is a method to check if the credentials are set
    assertNotNull(user.getUsername());
    assertEquals("batman", user.getUsername());
  }

  @Test
  void loadWalletWithValidCredentialsShouldNotThrow() {
    assertDoesNotThrow(() -> user.loadWallet(walletName, validPassword));
  }

  @Test
  void loadWalletWithInvalidCredentialsShouldThrow() {
    assertThrows(Exception.class, () -> user.loadWallet(walletName, "randomWrongPassword"));
  }

  @Test
  void getBalanceWithoutWalletLoadedShouldThrow() {
    assertThrows(NullPointerException.class, () -> user.getBalance());
  }

  @Test
  void signMessageWithNoWalletLoadedShouldThrow() {
    assertThrows(NullPointerException.class, () -> user.signMessage("Message"));
  }

  @Test
  void createNewAccountShouldNotThrowWithValidPassword() {
    assertDoesNotThrow(() -> user.createNewAccount(validPassword));
  }

  @Test
  void sendTransactionWithoutWalletShouldThrow() {
    assertThrows(NullPointerException.class, () -> user.sendTransaction("862eff1f5772b4bc7645a11a08d58a1df6d0549a", 0.1));
  }

  @Test
  void addingNewWalletShouldIncreaseWalletCount()
      throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
    // Assuming you can add a wallet
    user.createNewAccount(validPassword);
    assertFalse(user.getWalletNames().isEmpty());
  }

}




