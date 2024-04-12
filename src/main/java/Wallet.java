public class Wallet {
  private final String name;
  private final String password;

  public Wallet(String name, String password) {
    this.name = name;
    this.password = password;
  }

  public String getName() {
    return name;
  }

}
