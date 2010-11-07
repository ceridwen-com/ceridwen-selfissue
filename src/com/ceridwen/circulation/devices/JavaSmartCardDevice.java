package com.ceridwen.circulation.devices;

import javax.smartcardio.*;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;

class CardThread extends Thread {
  
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
    this.Running = true;
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
      }
      
    }    
  }
}

public class JavaSmartCardDevice implements IDReaderDevice {
  CardTerminal terminal;
  CardThread thread;
  
  @Override
  public void init(Node node) {
    TerminalFactory factory = TerminalFactory.getDefault();
    CardTerminals terminals = factory.terminals();
    terminal = terminals.getTerminal(Configuration.getSubProperty(node, "SmartCardReader"));
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
  public void reset() {
    thread.stopThread();
    thread.start();
  }

  @Override
  public void pause() {
    thread.stopThread();
  }

  @Override
  public void resume() {
    thread.start();
  }

  @Override
  public void deinit() {
  }  
}
