package com.ceridwen.selfissue.client.log;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

import com.ceridwen.circulation.SIP.messages.Message;
import java.io.*;

public class OnlineLogEvent implements Serializable {
  public static final int STATUS_CHECKOUTSUCCESS = 0x0001;
  public static final int STATUS_CHECKOUTFAILURE = 0x0002;
  public static final int STATUS_CHECKOUTPENDING = 0x0004;
  public static final int STATUS_MANUALCHECKOUT = 0x0008;
  public static final int STATUS_NOTIFICATION = 0x0010;
  public static final int STATUS_UNLOCKFAILURE = 0x0020;
  public static final int STATUS_CANCELCHECKOUTFAILURE = 0x0040;

  private String source;
  private String library;
  private String TimeStamp;
  private Message request;
  private Message response;
  private boolean actionRequired;
  private int level;
  private String addInfo;
  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }
  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }
  public String getLibrary() {
    return library;
  }
  public Message getRequest() {
    return request;
  }
  public Message getResponse() {
    return response;
  }
  public String getSource() {
    return source;
  }
  public String getTimeStamp() {
    return TimeStamp;
  }
  public void setTimeStamp(String TimeStamp) {
    this.TimeStamp = TimeStamp;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public void setResponse(Message response) {
    this.response = response;
  }
  public void setRequest(Message request) {
    this.request = request;
  }
  public void setLibrary(String library) {
    this.library = library;
  }
  public int getLevel() {
    return level;
  }
  public void setLevel(int level) {
    this.level = level;
  }
  public boolean isActionRequired() {
    return actionRequired;
  }
  public void setActionRequired(boolean actionRequired) {
    this.actionRequired = actionRequired;
  }
  public String getAddInfo() {
    return addInfo;
  }
  public void setAddInfo(String addInfo) {
    this.addInfo = addInfo;
  }
}
