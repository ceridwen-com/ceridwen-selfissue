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
package com.ceridwen.selfissue.client.panels;

import java.util.EventObject;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */
import com.ceridwen.circulation.SIP.messages.Message;

public class SelfIssuePanelEvent extends EventObject {
  /**
	 * 
	 */
	
/**
	 * 
	 */
	private static final long serialVersionUID = -6743577454537349756L;
/**
	 * 
	 */
	
public Message request;
  public Message response;
  public Class<?> nextPanel;

  public SelfIssuePanelEvent(Object source, Class<?> nextPanel) {
    super(source);
    this.nextPanel = nextPanel;
  }
}
