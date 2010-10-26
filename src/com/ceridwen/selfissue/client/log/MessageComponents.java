package com.ceridwen.selfissue.client.log;

import java.util.Date;

class MessageComponents
{
  public String patronId = null;
  public String itemId = null;
  public String addInfo = null;
  public String type = null;
  public String datestamp = null;
  public String originalTransactionTime = null;
  public String subjectType = null;

  @SuppressWarnings("deprecation")
public MessageComponents(String patronId, String itemId, String addInfo,
                           String subjectType, String type, String originalTransactionTime, String datestamp)
  {
    this.patronId = (patronId == null) ? "Unknown" : patronId;
    this.itemId = (itemId == null) ? "Unknown" : itemId;
    this.addInfo = addInfo;
    this.type = (type == null) ? "Unknown" : type;
    this.subjectType = (subjectType == null) ? "Unknown" : subjectType;
    this.originalTransactionTime = (originalTransactionTime == null)? "Unknown" : originalTransactionTime;
    this.datestamp = (datestamp == null) ?  new Date().toLocaleString() : datestamp;
  }
}
