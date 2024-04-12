import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParser {

  private static final String USER_FILE_PATH =
      System.getProperty("user.dir") + "/src/main/resources/users.xml";

  private final Map<String, String> userPasswords = new HashMap<>();
  private final Map<String, List<String>> userWallets = new HashMap<>();

  public XMLParser() {
    parseUsers();
  }

  private Document getDocument(File file) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(file);
  }

  private void saveDocument(Document doc, File file) throws Exception {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.transform(new DOMSource(doc), new StreamResult(file));
  }

  private void parseUsers() {
    try {
      File xmlFile = new File(USER_FILE_PATH);
      Document doc = getDocument(xmlFile);
      NodeList userList = doc.getElementsByTagName("user");

      for (int i = 0; i < userList.getLength(); i++) {
        Node userNode = userList.item(i);
        if (userNode.getNodeType() == Node.ELEMENT_NODE) {
          Element userElement = (Element) userNode;
          processUserElement(userElement);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void processUserElement(Element userElement) {
    String username = getElementText(userElement, "username");
    String password = getElementText(userElement, "password");
    userPasswords.put(username, password);

    NodeList walletNodes = userElement.getElementsByTagName("wallet");
    List<String> wallets = new ArrayList<>();
    for (int i = 0; i < walletNodes.getLength(); i++) {
      Element walletElement = (Element) walletNodes.item(i);
      wallets.add(getElementText(walletElement, "name"));
    }
    userWallets.put(username, wallets);
  }

  private String getElementText(Element parent, String tagName) {
    return parent.getElementsByTagName(tagName).item(0).getTextContent();
  }

  public List<String> getUserWallets(String username) {
    return userWallets.getOrDefault(username, new ArrayList<>());
  }

  public void addNewWallet(String username, String walletName) {
    try {
      File xmlFile = new File(USER_FILE_PATH);
      Document doc = getDocument(xmlFile);
      NodeList userList = doc.getElementsByTagName("user");
      for (int i = 0; i < userList.getLength(); i++) {
        Element userElement = (Element) userList.item(i);
        if (username.equals(getElementText(userElement, "username"))) {
          addWalletToUser(doc, userElement, walletName);
          break;
        }
      }
      saveDocument(doc, xmlFile);
      updateWalletCache(username, walletName);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void addWalletToUser(Document doc, Element userElement, String walletName) {
    Element walletsElement = (Element) userElement.getElementsByTagName("wallets").item(0);
    Element walletElement = doc.createElement("wallet");
    appendElementWithText(doc, walletElement, "name", walletName);
    walletsElement.appendChild(walletElement);
  }

  private void appendElementWithText(Document doc, Element parent, String tagName, String text) {
    Element element = doc.createElement(tagName);
    element.appendChild(doc.createTextNode(text));
    parent.appendChild(element);
  }

  private void updateWalletCache(String username, String walletName) {
    List<String> wallets = userWallets.getOrDefault(username, new ArrayList<>());
    wallets.add(walletName);
    userWallets.put(username, wallets);
  }

  public void newUser(String username, String password) {
    try {
      File xmlFile = new File(USER_FILE_PATH);
      Document doc = getDocument(xmlFile);
      Node users = doc.getFirstChild();
      Element newUser = doc.createElement("user");
      appendElementWithText(doc, newUser, "username", username);
      appendElementWithText(doc, newUser, "password", password);

      Element wallets = doc.createElement("wallets");
      newUser.appendChild(wallets);

      users.appendChild(newUser);

      saveDocument(doc, xmlFile);
      userPasswords.put(username, password);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean validPassword(String username, String password) {
    return existingUsername(username) && userPasswords.get(username).equals(password);
  }

  public boolean existingUsername(String username) {
    return userPasswords.containsKey(username);
  }

}



