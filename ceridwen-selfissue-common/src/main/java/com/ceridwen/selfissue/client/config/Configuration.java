/* 
 * Copyright (C) 2019 Ceridwen Limited
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ceridwen.selfissue.client.config;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.text.MessageFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ceridwen.selfissue.client.dialogs.ErrorDialog;
import com.ceridwen.util.encryption.TEAAlgorithm;
import com.gaborcselle.persistent.PersistentQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import com.ceridwen.util.collections.Queue;
import java.io.Serializable;

public class Configuration {
  private static Log log = LogFactory.getLog(Configuration.class);

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
    dlg.setVisible(true);
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

  private static synchronized NodeList selectNodeList(Node node, String xPath) throws
      Exception {

    NodeList nodeList = null;
    try {
      nodeList = xPathAPI.selectNodeList(node, xPath);
    } catch (javax.xml.transform.TransformerException e) {
      fatal(e, "Could not select valid nodes: " + xPath);
      System.exit(200);
    }
    return nodeList;
  }

  private static synchronized Node selectSingleNode(Node node, String xPath) throws
      Exception {

    Node selection = null;
    try {
      selection = xPathAPI.selectSingleNode(node, xPath);
    } catch (javax.xml.transform.TransformerException e) {
      fatal(e, "Could not select valid node: " + xPath);
    }
    return selection;
  }

  private static String getValue(Node node) {
    if (node == null) {
      return "";
    }

    if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
      return node.getNodeValue();
    }
    if (node.hasChildNodes() &&
        node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
      if (node.getFirstChild().getNodeValue() != null) {
        return node.getFirstChild().getNodeValue();
      } else {
        return ""; //example: text node created with null content.
      }
    }

    if (node.getNodeValue() != null) {
      return node.getNodeValue();
    } else {
      return ""; //example: empty node.
    }
  }

  private static Document document = null;
  private static Document parse() throws Exception {
    try {
      if (document == null) {
        DocumentBuilder builder = getDocumentBuilder();
        document = builder.parse(getInputStream());
      }
    } catch (IOException | SAXException | IllegalArgumentException e) {
      String message =
          "DOMFactory, public static Document parse(File file): Cannot parse file: " + e.toString();
      throw new Exception(message, e);
    }

    return document;
  }

  private static InputStream getInputStream() {
    try {
      return LoadResource(CONFIGURATION_FILE).openConnection().getInputStream();
    } catch (IOException ex) {
      return null;
    }
  }

  public static String getProperty(String key) {
    try {
      Node value = selectSingleNode(parse().getFirstChild(),
                                    "//SelfIssue/" + key);
      String propValue = getValue(value);
      if (StringUtils.isEmpty(propValue)) {
        log.debug("**CONFIGURATION**  Empty key:" + key);
      }

      return propValue;
    } catch (Exception ex) {
      fatal(ex, "Could not retrieve property value: " + key);
      return null;
    }
  }

  public static Node getPropertyNode(String key) {
    try {
      Node value = selectSingleNode(parse().getFirstChild(),
                                    "//SelfIssue/" + key);
      if (value == null) {
        throw new NullPointerException("Node was null retrieving property value");        
      }
      return value;
    } catch (Exception ex) {
      fatal(ex, "Could not retrieve property value: " + key);
      return null;
    }
  }

  public static NodeList getPropertyList(String key) {
    try {
      NodeList value = selectNodeList(parse().getFirstChild(),
                                    "//SelfIssue/" + key);
      if (value.getLength() == 0) {
        log.debug("**CONFIGURATION**  Empty key:" + key);
      }

      return value;
    } catch (Exception ex) {
      fatal(ex, "Could not retrieve property values: " + key);
      return null;
    }

  }

  public static Node getSubPropertyNode(Node node, String key) {
	try {
		  return selectSingleNode(node,key);
	  } catch (Exception ex) {
	      fatal(ex, "Could not retrieve property sub-value: " + key);
	      return null;
	    }
	}
  
  public static String getSubProperty(Node node, String key) {
    try {
      Node value = selectSingleNode(node,key);
      String propValue = getValue(value);
      if (StringUtils.isEmpty(propValue)) {
        log.debug("**CONFIGURATION**  Empty key:" + key);
      }

      return propValue;
    } catch (Exception ex) {
      fatal(ex, "Could not retrieve property sub-value: " + key);
      return null;
    }
  }

//  public static int getIntSubProperty(Node node, String key) {
//    return Configuration.getIntSubProperty(node, key, 0);
//}

  
  public static int getIntSubProperty(Node node, String key, int def) {
    try {
      return (Configuration.getSubProperty(node, key) == null) ? 0 :
          Integer.parseInt(Configuration.getSubProperty(node, key));
    } catch (NumberFormatException ex) {
      return def;
    }
  }
  
  public static boolean getBoolSubProperty(Node node, String key) {
	    return (Configuration.getSubProperty(node, key) == null) ? false :
	        (Configuration.getSubProperty(node, key).equalsIgnoreCase("true") ||
	         Configuration.getSubProperty(node, key).equalsIgnoreCase("yes") ||
	         Configuration.getSubProperty(node, key).equalsIgnoreCase("1"));
	  }

  public static boolean getBoolProperty(String key) {
    return (Configuration.getProperty(key) == null) ? false :
        (Configuration.getProperty(key).equalsIgnoreCase("true") ||
         Configuration.getProperty(key).equalsIgnoreCase("yes") ||
         Configuration.getProperty(key).equalsIgnoreCase("1"));
  }

//  public static int getIntProperty(String key) {
//      return Configuration.getIntProperty(key, 0);
//  }  
  
  public static int getIntProperty(String key, int def) {
    try {
      return (Configuration.getProperty(key) == null) ? 0 :
          Integer.parseInt(Configuration.getProperty(key));
    } catch (NumberFormatException ex) {
      return def;
    }
  }
  
  public static <T extends Serializable> Queue<T> getPersistentQueue(String file) throws IOException {
    try {   
        return (Queue<T>)Class.forName(Configuration.getProperty("Systems/Spooler/PersistentQueueImplementation")).getConstructor(new Class[]{String.class}).newInstance(new Object[]{file});
    } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        return new PersistentQueue<>(file);
    }      
  }
  
  private static double getScalingFactor() {
    double monitor = Configuration.getIntProperty("UI/Advanced/ReferenceMonitor", 25);
    double dpi = Configuration.getIntProperty("UI/Advanced/ReferenceDPI", 96);
      
    return ((double)2.0*(double)Toolkit.getDefaultToolkit().getScreenSize().height)/(monitor*dpi);
  }
  
  public static int getScaledPointSize(String key) {
      return (int)Math.round(Configuration.getIntProperty(key, 0)*Configuration.getScalingFactor());
  }

  public static int pt2Pixel(int pt) {
    return (int)Math.round(pt*Toolkit.getDefaultToolkit().getScreenResolution()/72);     
  }
  
  public static Font getFont(String key) {
    return new java.awt.Font(Configuration.getProperty("UI/Styling/" + key + "_Font"), 1, Configuration.getScaledPointSize("UI/Styling/" + key + "_Size"));
  }
  
  private static Color getColour(String colour, Color defaultColour) {
	  try {
		  String raw = Configuration.getProperty("UI/Palette/" + colour);
		  String parsed[] = raw.split(",");		  
		  int r = 0;
		  int g = 0;
		  int b = 0;
		  if (parsed.length > 0) {
			  r = Integer.parseInt(parsed[0]);
		  }
		  if (parsed.length > 1) {
			  g = Integer.parseInt(parsed[1]);
		  }
		  if (parsed.length > 2) {
			  b = Integer.parseInt(parsed[2]);
		  }
		  return new Color(r, g, b);
	  } catch (NumberFormatException ex) {
		  return defaultColour;
	  }
  }
  
  public static Color getForegroundColour(String colour) {
	  return Configuration.getColour(colour, new Color(0xff, 0xff, 0xff));
  }

  public static Color getBackgroundColour(String colour) {
	  return Configuration.getColour(colour, new Color(0x00, 0x00, 0x00));
  }

  public static String colorEncode(Color colour) {
      return String.format("#%06x", colour.getRGB() & 0x00FFFFFF);
  }
  
  public static String getMessage(String messageKey, String[] components) {
    String pattern = getProperty("UI/Messages/" + messageKey);
    if (pattern == null) {
      return null;
    }
    MessageFormat format = new MessageFormat(pattern);
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
    String path = Configuration.getProperty(image);
    URL url = LoadResource(path);

    if (url == null) {
      return new ImageIcon();
    } else {
      ImageIcon src = new ImageIcon(url);
      double scaling = Configuration.getScalingFactor();
      return new ImageIcon(src.getImage().getScaledInstance( 
        (int)Math.round(src.getIconWidth()*scaling), 
        (int)Math.round(src.getIconHeight()*scaling), 
        Image.SCALE_SMOOTH));
    }
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
