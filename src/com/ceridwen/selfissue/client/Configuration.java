package com.ceridwen.selfissue.client;

import java.math.BigInteger;
import com.ceridwen.util.encryption.TEAAlgorithm;
import java.net.*;
import java.io.*;
import org.apache.xpath.CachedXPathAPI;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.text.*;
import javax.swing.*;
import org.apache.commons.logging.*;

public class Configuration {
  private static Log log = LogFactory.getLog(CirculationHandler.class);

  private static final String CONFIGURATION_FILE =
      "com/ceridwen/selfissue/client/config/config.xml";
  private static DocumentBuilderFactory factory;
  private static CachedXPathAPI xPathAPI;

  static {
    factory = DocumentBuilderFactory.newInstance();
    xPathAPI = new CachedXPathAPI();

  }

  private static void fatal(Throwable e, String msg) {
    System.err.print("FATAL: " + msg);
    System.out.println("**FATAL SHUTDOWN - INVALID CONFIGURATION***");
    ErrorDialog dlg = new ErrorDialog(e, msg);
    dlg.show();
    Runtime.getRuntime().halt(200);
  }

  private static synchronized DocumentBuilder getDocumentBuilder() {

    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      fatal(e, "DOMFactory, static initialization: Parser with specified options cannot be built!");
    }

    return builder;
  }

  private static NodeList selectNodeList(Node node, String xPath) throws
      Exception {

    NodeList nodeList = null;
    try {
      nodeList = xPathAPI.selectNodeList(node, xPath);
    } catch (javax.xml.transform.TransformerException e) {
      fatal(e, "Could not select valid nodes");
      System.exit(200);
    }
    return nodeList;
  }

  private static Node selectSingleNode(Node node, String xPath) throws
      Exception {

    Node selection = null;
    try {
      selection = xPathAPI.selectSingleNode(node, xPath);
    } catch (javax.xml.transform.TransformerException e) {
      fatal(e, "Could not select valid node");
    }
    return selection;
  }

  private static String getValue(Node node) {
    if (node == null) {
      return "";
    }

    if (node.getNodeType() == Node.ATTRIBUTE_NODE)
      return node.getNodeValue();

    if (node.hasChildNodes() &&
        node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
      if (node.getFirstChild().getNodeValue() != null)
        return node.getFirstChild().getNodeValue();
      else
        return ""; //example: text node created with null content.
    }

    if (node.getNodeValue() != null)
      return node.getNodeValue();
    else
      return ""; //example: empty node.
  }

  static Document document = null;
  private static Document parse() throws Exception {
    try {
      if (document == null) {
        DocumentBuilder builder = getDocumentBuilder();
        document = builder.parse(getInputStream());
      }
    } catch (IOException e) {
      String message =
          "DOMFactory, public static Document parse(File file): Cannot parse file " +
          file + "; " + e.toString();
      throw new Exception(message, e);
    } catch (SAXException e) {
      String message =
          "DOMFactory:public static Document parse(File file) - Cannot parse file " +
          file + "; " + e.toString();
      throw new Exception(message, e);
    } catch (IllegalArgumentException e) {
      String message =
          "DOMFactory:public static Document parse(File file) - Cannot parse file " +
          file + "; " + e.toString();
      throw new Exception(message, e);
    }

    return document;
  }

  static File file = null;

  private static InputStream getInputStream() {
    try {
      return LoadResource(CONFIGURATION_FILE).openConnection().getInputStream();
    } catch (Exception ex) {
      return null;
    }
  }

  public static String getProperty(String key) {
    try {
      Node value = selectSingleNode(parse().getFirstChild(),
                                    "//SelfIssue/" + key);
      String propValue = getValue(value);
      if (propValue.length() == 0) {
        log.debug("**CONFIGURATION**  Empty key:" + key);
      }

      return propValue;
    } catch (Exception ex) {
      fatal(ex, "Could not retrieve property value");
      return null;
    }
  }

  public static NodeList getPropertyList(String key) {
    try {
      NodeList value = selectNodeList(parse().getFirstChild(),
                                    "//SelfIssue/" + key);
      if (value.getLength() == 0) {
        System.err.println("**CONFIGURATION**  Empty key:" + key);
      }

      return value;
    } catch (Exception ex) {
      fatal(ex, "Could not retrieve property values");
      return null;
    }

  }

  public static String getSubProperty(Node node, String key) {
    try {
      Node value = selectSingleNode(node,key);
      String propValue = getValue(value);
      if (propValue.length() == 0) {
        log.debug("**CONFIGURATION**  Empty key:" + key);
      }

      return propValue;
    } catch (Exception ex) {
      fatal(ex, "Could not retrieve property sub-value");
      return null;
    }
  }

  public static int getIntSubProperty(Node node, String key) {
    try {
      return (Configuration.getSubProperty(node, key) == null) ? 0 :
          Integer.parseInt(Configuration.getSubProperty(node, key));
    } catch (Exception ex) {
      return 0;
    }
  }

  public static boolean getBoolProperty(String key) {
    return (Configuration.getProperty(key) == null) ? false :
        (Configuration.getProperty(key).equalsIgnoreCase("true") ||
         Configuration.getProperty(key).equalsIgnoreCase("yes") ||
         Configuration.getProperty(key).equalsIgnoreCase("1"));
  }

  public static int getIntProperty(String key) {
    try {
      return (Configuration.getProperty(key) == null) ? 0 :
          Integer.parseInt(Configuration.getProperty(key));
    } catch (Exception ex) {
      return 0;
    }
  }


  public static String getMessage(String messageKey, String[] components) {
    String pattern = getProperty("UI/Messages/" + messageKey);
    if (pattern == null)
      return null;
    String decodedPattern = pattern.replaceAll("\\^", "\r\n");
    MessageFormat format = new MessageFormat(decodedPattern);
    return format.format(components);
  }
  public static URL LoadResource(String path) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url;
    try {
      if (loader == null) {
        url = new Object().getClass().getResource(path);
      } else {
        url = loader.getResource(path);
      }
    } catch (Exception e) {
      return null;
    }
    return url;
  }


  public static ImageIcon LoadImage(String image) {
    Class cl = new Object().getClass();
    String path = Configuration.getProperty(image);
    URL url = LoadResource(path);

    if (url == null)
      return new ImageIcon();
    else
      return new ImageIcon(url);
  }

  private static byte key[] = new BigInteger(TEAAlgorithm.getHex("CeridwenSelfIsue".getBytes()), 16).toByteArray();

  public static String Encrypt(String s) {
    try {
      TEAAlgorithm alg = new TEAAlgorithm(key);
      String pt = alg.padPlaintext(s);
      byte ps[] = pt.getBytes();
      return alg.binToHex(alg.encode(ps, ps.length));
    }
    catch (Exception ex) {
      log.error("Decryption overflow", ex);
      return null;
    }
  }

  public static String Decrypt(String s) {
    try {
      TEAAlgorithm alg = new TEAAlgorithm(key);
      byte ps[] = alg.hexToBin(s);
      String ret = new String(alg.decode(ps, ps.length)).trim();
      return ret;
    }
    catch (Exception ex) {
      log.error("Decryption overflow", ex);
      return null;
    }
  }
}
