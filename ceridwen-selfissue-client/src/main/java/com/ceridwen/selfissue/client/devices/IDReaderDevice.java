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
package com.ceridwen.selfissue.client.devices;

import org.w3c.dom.Node;

public interface IDReaderDevice {
/**
 * Initialise the security/RFID device  
 * @param node configuration parameters
 */
  public abstract void init(Node node);
/**
 * Start the security/RFID device listening for new items  
 * @param listener listener class to fire detected ids to
 */
  public abstract void start(IDReaderDeviceListener listener);
/**
 * Stop the security/RFID device listening for new items  
 */
  public abstract void stop();
/**
 * De-initialise the security/RFID device  
 */
  public abstract void deinit();
}
