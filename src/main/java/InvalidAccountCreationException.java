public class InvalidAccountCreationException extends RuntimeException {
  public InvalidAccountCreationException(String message) {
    super(message);
  }

  public InvalidAccountCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
