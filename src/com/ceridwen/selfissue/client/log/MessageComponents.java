package com.ceridwen.selfissue.client.log;

import java.util.Date;

class MessageComponents
{
  public String patronId = null;
  public String itemId = null;
  public String addInfo = null;
  public String type = null;
  public String datestamp = null;

  public MessageComponents(String patronId, String itemId, String addInfo,
                           String type, String datestamp)
  {
    this.patronId = (patronId == null) ? "Unknown" : patronId;
    this.itemId = (itemId == null) ? "Unknown" : itemId;
    this.addInfo = addInfo;
    this.type = (type == null) ? "Unknown" : type;
    this.datestamp = (datestamp == null) ?  new Date().toLocaleString() : datestamp;
  }
}
