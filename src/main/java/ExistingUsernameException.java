public class ExistingUsernameException extends RuntimeException {

  public ExistingUsernameException(String message) {
    super(message);
  }

  public ExistingUsernameException(String message, Throwable cause) {
    super(message, cause);
  }
}
