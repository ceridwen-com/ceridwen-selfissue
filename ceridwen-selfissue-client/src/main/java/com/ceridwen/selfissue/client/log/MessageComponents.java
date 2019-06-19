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
package com.ceridwen.selfissue.client.log;

import java.text.DateFormat;
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

public MessageComponents(String patronId, String itemId, String addInfo,
                           String subjectType, String type, String originalTransactionTime, String datestamp)
  {
    this.patronId = (patronId == null) ? "Unknown" : patronId;
    this.itemId = (itemId == null) ? "Unknown" : itemId;
    this.addInfo = addInfo;
    this.type = (type == null) ? "Unknown" : type;
    this.subjectType = (subjectType == null) ? "Unknown" : subjectType;
    this.originalTransactionTime = (originalTransactionTime == null)? "Unknown" : originalTransactionTime;
    this.datestamp = (datestamp == null) ?  DateFormat.getDateInstance().format(new Date()) : datestamp;
  }
}
