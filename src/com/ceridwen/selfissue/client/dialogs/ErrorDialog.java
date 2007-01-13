package com.ceridwen.selfissue.client.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ErrorDialog
    extends JDialog {
  /**
	 * 
	 */
	
/**
	 * 
	 */
	private static final long serialVersionUID = -6474958525441134339L;
/**
	 * 
	 */
	
private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JButton closeButton = new JButton();
  private JTextArea errorMessage = new JTextArea();
  private String errorMsg = "";

  public ErrorDialog(Frame owner, String title, boolean modal) {
    this(owner, title, modal, "");
  }

  public ErrorDialog(Frame owner, String title, boolean modal, String msg) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.errorMsg = msg;
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public ErrorDialog(String msg) {
    this(new Frame(), "Fatal Error", true, msg);
  }

  public ErrorDialog(Throwable e, String msg) {
    this("Fatal error reading configuration file\n\n" + msg + "\n\n\n" + e.getMessage());
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    closeButton.setText("Exit Application");
    closeButton.addActionListener(new ErrorDialog_jButton1_actionAdapter(this));
    errorMessage.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    errorMessage.setForeground(Color.red);
    errorMessage.setMinimumSize(new Dimension(400, 200));
    errorMessage.setEditable(false);
    errorMessage.setText(errorMsg);
    errorMessage.setWrapStyleWord(true);
    getContentPane().add(panel1);
    panel1.add(closeButton, java.awt.BorderLayout.SOUTH);
    panel1.add(errorMessage, java.awt.BorderLayout.CENTER);
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

  public void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
  }
}

class ErrorDialog_jButton1_actionAdapter
    implements ActionListener {
  private ErrorDialog adaptee;
  ErrorDialog_jButton1_actionAdapter(ErrorDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}
