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
package com.ceridwen.selfissue.client.nulldevices;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.devices.IDReaderDeviceListener;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class IDReaderDevice implements com.ceridwen.selfissue.client.devices.IDReaderDevice {
  public IDReaderDevice() {
  }


  @Override
  public void init(Node node) {
    return;
  }

  @Override
  public void deinit() {
    return;
  }
  
  @Override
  public void stop() {
  }

  @Override
  public void start(IDReaderDeviceListener listener) {
  }
}
