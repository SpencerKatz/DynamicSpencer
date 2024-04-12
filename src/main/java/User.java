import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.crypto.WalletUtils;
import org.web3j.crypto.exception.CipherException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

/**
 * Handles user functionality such as wallet management, transactions, and signing messages using Ethereum blockchain.
 */
public class User {

  private final XMLParser parser;
  private final String username;
  private Credentials currentWallet;
  private final Web3j web3;
  private static final String walletDirectory = System.getProperty("user.dir") + "/src/main/resources/walletDirectory";
  private static final String httpService = "https://sepolia.infura.io/v3/bcd588d5219e459fa5faa21bf429c957";

  /**
   * Constructs a User object and initializes the user interface.
   * @param username The username of the user.
   */
  public User(String username) {
    this.username = username;
    parser = new XMLParser();
    currentWallet = null;
    web3 = Web3j.build(new HttpService(httpService));
  }

  /**
   * Creates a new Ethereum wallet file with the specified password.
   * @param walletPassword The password to encrypt the wallet.
   * @throws InvalidAlgorithmParameterException If the algorithm parameters are not valid.
   * @throws CipherException If the encryption cannot be performed.
   * @throws NoSuchAlgorithmException If the cryptographic algorithm is not available.
   * @throws IOException If there is an I/O error writing to the wallet file.
   * @throws NoSuchProviderException If the security provider is not available.
   */
  public void createNewAccount(String walletPassword)
      throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
    String walletName = WalletUtils.generateNewWalletFile(walletPassword, new File(walletDirectory));
    Credentials newAccountCredentials = WalletUtils.loadCredentials(walletPassword, walletDirectory + "/" + walletName);
    parser.addNewWallet(username, walletName);
    newAccountCredentials.getEcKeyPair().getPrivateKey();
  }

  /**
   * Loads an Ethereum wallet by name and password.
   * @param walletName The file name of the wallet.
   * @param walletPassword The password of the wallet.
   * @throws CipherException If the encryption cannot be performed.
   * @throws IOException If there is an I/O error loading the wallet file.
   */
  public void loadWallet(String walletName, String walletPassword)
      throws CipherException, IOException {
    currentWallet = WalletUtils.loadCredentials(walletPassword, walletDirectory + "/" + walletName);
  }

  /**
   * Retrieves the current balance of the loaded wallet in Ether.
   * @return The balance in Ether.
   * @throws IOException If there is an I/O error when fetching the balance.
   */
  public double getBalance() throws IOException {
    EthGetBalance balanceWei = web3.ethGetBalance(currentWallet.getAddress(), DefaultBlockParameterName.LATEST).send();
    return Convert.fromWei(balanceWei.getBalance().toString(), Convert.Unit.ETHER).doubleValue();
  }

  /**
   * Signs a message with the currently loaded wallet's private key.
   * @param msg The message to be signed.
   * @return The signature in hexadecimal format.
   */
  public String signMessage(String msg) {
    byte[] messageHash = Hash.sha3(msg.getBytes());
    Sign.SignatureData signature = Sign.signPrefixedMessage(messageHash, currentWallet.getEcKeyPair());
    byte[] sigBytes = new byte[65];
    System.arraycopy(signature.getR(), 0, sigBytes, 0, 32);
    System.arraycopy(signature.getS(), 0, sigBytes, 32, 32);
    byte v = signature.getV().length > 0 ? signature.getV()[0] : 0;
    sigBytes[64] = v;
    return Numeric.toHexString(sigBytes);
  }

  /**
   * Retrieves a list of wallet names associated with the user's account.
   * @return A list of wallet names as Strings.
   */
  public List<String> getWalletNames() {
    return parser.getUserWallets(username);
  }

  /**
   * Sends a transaction to the specified address with the specified amount in Ether.
   * This method converts the Ether amount into Wei, the smallest unit of Ether,
   * and submits the transaction to the Ethereum network.
   *
   * @param to The recipient's address as a string. This address should be a valid Ethereum address.
   * @param amount The amount to send, denoted in Ether, not Wei. This should be a positive value.
   * @return The hash of the transaction once submitted to the Ethereum network.
   * @throws TransactionException If there is an issue with the transaction itself, such as failure during execution.
   * @throws IOException If there is a communication issue while interacting with the Ethereum network.
   * @throws InterruptedException If the transaction is interrupted during execution.
   * @throws Exception General exception capturing any other unexpected issues that occur during transaction processing.
   */
  public String sendTransaction(String to, double amount)
      throws TransactionException, IOException, InterruptedException, Exception {
    BigDecimal amountInWei = Convert.toWei(BigDecimal.valueOf(amount), Convert.Unit.ETHER);
    TransactionReceipt transactionReceipt = Transfer.sendFunds(web3, currentWallet, to, amountInWei,
        Unit.ETHER).send();
    return transactionReceipt.getTransactionHash();
  }


  /**
   * Gets the username of this user.
   * @return The username.
   */
  public String getUsername() {
    return username;
  }
}
