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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.ceridwen.selfissue.client.SelfIssueFrame;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.dialogs.PasswordDialog;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OutOfOrderPanel extends SelfIssuePanel
{
  /**
	 * 
	 */
	
/**
	 * 
	 */
	private static final long serialVersionUID = 4410821939255496775L;
/**
	 * 
	 */
	
private BorderLayout borderLayout1 = new BorderLayout();
  private JLabel jLabel1 = new JLabel();
  public OutOfOrderPanel()
  {
    try {
      jbInit();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
	Color BackgroundColour = Configuration.getBackgroundColour("BackgroundColour"); //new Color(0xC9, 0xD3, 0xDD);  
//	Color DefaultTextColour = Configuration.getForegroundColour("DefaultTextColour"); //Color.black;
	Color WarningTextColour = Configuration.getForegroundColour("WarningTextColour"); //Color.gray;
	  
    setLayout(borderLayout1);
    this.setOpaque(true);
    this.setBackground(BackgroundColour);
    jLabel1.setFont(new java.awt.Font("Tahoma", Font.BOLD, 64));
    jLabel1.setForeground(WarningTextColour);
    jLabel1.setOpaque(false);
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel1.setText("Terminal Out Of Order");
    add(jLabel1, java.awt.BorderLayout.CENTER);
    
    this.addKeyListener(new OutOfOrderPanel_keyAdapter(this));
  }
  
  StringBuffer command = new StringBuffer("");
  
  void OutOfOrderPanel_keyTyped(KeyEvent e){
      if (Configuration.getBoolProperty("CommandInterface/AllowResetOutOfOrder")) {
		  char ch = e.getKeyChar();
		  if (ch == '¦') {
			  if (command.toString().equals("*Reset Out Of Order")) {
			        SelfIssueFrame.setOnTop(false);
			        PasswordDialog ResetConfirmation = new PasswordDialog("Please enter system password");
			        ResetConfirmation.clearPassword();
			        ResetConfirmation.setVisible(true);
			        if (ResetConfirmation.getPassword().equals(Configuration.Decrypt(
			            Configuration.getProperty(
			                "CommandInterface/SystemPassword")))) {
				        SelfIssueFrame.setOnTop(true);
				        SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, PatronPanel.class);
				        this.firePanelChange(ev);			  
			        }
			        SelfIssueFrame.setOnTop(true);
			  }
			  command.delete(0, command.length());
		  } else if (ch == '*') {
			  command.delete(0, command.length());
			  command.append(ch);
		  } else {
			  command.append(ch);
		  }
      }
  }
}


class OutOfOrderPanel_keyAdapter extends java.awt.event.KeyAdapter {
	  private OutOfOrderPanel adaptee;

	  OutOfOrderPanel_keyAdapter(OutOfOrderPanel adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void keyTyped(KeyEvent e) {
	    adaptee.OutOfOrderPanel_keyTyped(e);
	  }
	}
