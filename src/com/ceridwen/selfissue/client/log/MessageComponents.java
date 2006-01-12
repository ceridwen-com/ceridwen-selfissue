package com.ceridwen.selfissue.client.log;

class MessageComponents
{
  public String patronId = null;
  public String itemId = null;
  public String addInfo = null;
  public String type = null;

  public MessageComponents(String patronId, String itemId, String addInfo,
                           String type)
  {
    this.patronId = (patronId == null) ? "Unknown" : patronId;
    this.itemId = (itemId == null) ? "Unknown" : itemId;
    this.addInfo = addInfo;
    this.type = (type == null) ? "Unknown" : type;
  }
}
