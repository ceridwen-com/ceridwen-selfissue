package com.ceridwen.selfissue.client;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Title:        Real Time Self Issue
 * Description:
 * Copyright:    Copyright (c) 2003
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class PasswordDialog extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton jButton1 = new JButton();
  JPasswordField jPasswordField1 = new JPasswordField();
  JLabel jLabel1 = new JLabel();

  public PasswordDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public PasswordDialog() {
    this(null, "Shutdown Request", true);
  }

  public void clearPassword() {
    jPasswordField1.setText("");
  }

  public String getPassword() {
    return String.valueOf(jPasswordField1.getPassword());
  }

  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPasswordField1.setFont(new java.awt.Font("DialogInput", 0, 18));
    jPasswordField1.setText("jPasswordField1");
    jLabel1.setText("Please enter shutdown password:");
    this.setModal(true);
    this.setResizable(false);
    getContentPane().add(panel1);
    panel1.add(jButton1,  BorderLayout.SOUTH);
    panel1.add(jPasswordField1,  BorderLayout.CENTER);
    panel1.add(jLabel1, BorderLayout.NORTH);

    this.pack();

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = this.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.hide();
  }
}
