package com.ceridwen.selfissue.client;

import java.util.Hashtable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface SelfIssueFrameMBean
{
  String getCurrentScreen();
  int getSpoolSize();
  String getSecurityDevice();
  String getLoggingDevice();
  Hashtable getModes();

  void setOutOfOrder(boolean b);
  boolean getOutOfOrder();

  String checkConnectivity();
  String checkConfiguration(String key);
  void terminateSelfIssue();
  void resetSystem();
  void resetSecurity();
}
