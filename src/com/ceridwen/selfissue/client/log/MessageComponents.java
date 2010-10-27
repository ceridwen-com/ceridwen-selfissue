/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * <http://www.gnu.org/licenses/>.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey - initial API and implementation
 ******************************************************************************/
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
