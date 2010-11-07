package com.ceridwen.selfissue.client.devices;

import javax.smartcardio.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;

class CardThread extends Thread {
    private static Log log = LogFactory.getLog(CardThread.class);
  
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
          log.error("Error during thread", ex);
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
