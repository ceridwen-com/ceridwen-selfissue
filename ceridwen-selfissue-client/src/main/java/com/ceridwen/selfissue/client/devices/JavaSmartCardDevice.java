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

import javax.smartcardio.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;

class CardThread extends Thread {
//    private static Log log = LogFactory.getLog(CardThread.class);
  
  private IDReaderDeviceListener listener;
  private CardTerminal terminal;
  private boolean Running; 

  public CardThread(IDReaderDeviceListener listener, CardTerminal terminal) {
    this.listener = listener;
    this.terminal = terminal;    
  }

  public void stopThread()
  {
    this.Running = false;
  }
  
  public void run() {
    this.Running = (terminal != null); // abort if no terminal
    while (this.Running) {
      try {
        if (terminal.isCardPresent()) {
          Card card = terminal.connect("*");
          StringBuffer id = new StringBuffer();
          for (Byte b: card.getATR().getBytes()) {
            id.append(Integer.toHexString(b));
          }
          listener.autoInputData(id.toString() , null);
          while (terminal.isCardPresent() && this.Running) {
            Thread.sleep(100);
          }
          card.disconnect(false);
          Thread.sleep(100);
        }
      } catch (Exception ex) {
//          log.error("Error during thread", ex);
      }
      
    }    
  }
}

public class JavaSmartCardDevice implements IDReaderDevice {
    private static Log log = LogFactory.getLog(JavaSmartCardDevice.class);
    
  CardTerminal terminal;
  CardThread thread;
  
  @Override
  public void init(Node node) {
    TerminalFactory factory = TerminalFactory.getDefault();
    CardTerminals terminals = factory.terminals();
    String terminalName = Configuration.getSubProperty(node, "SmartCardReader");
    terminal = terminals.getTerminal(terminalName);
    if (terminal == null) {
        log.error("Smart card reader not found");
    }
  }

  @Override
  public void start(IDReaderDeviceListener listener) {
    thread = new CardThread(listener, terminal);
    thread.start();
  }

  @Override
  public void stop() {
    thread.stopThread();
  }

  @Override
  public void deinit() {
  }  
}
