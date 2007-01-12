package com.ceridwen.selfissue.client.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

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
	private static final long serialVersionUID = 1L;
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
    setLayout(borderLayout1);
    jLabel1.setFont(new java.awt.Font("Tahoma", Font.BOLD, 64));
    jLabel1.setForeground(Color.red);
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel1.setText("Terminal Out Of Order");
    add(jLabel1, java.awt.BorderLayout.CENTER);
  }
}
