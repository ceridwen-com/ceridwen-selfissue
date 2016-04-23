/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey (www.ceridwen.com).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at 
 * <http://www.gnu.org/licenses/>
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 ******************************************************************************/
package com.ceridwen.selfissue.client.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * Title:        Real Time Self Issue
 * Description:
 * Copyright:    Copyright (c) 2003
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class PasswordDialog extends JDialog {
  /**
	 * 
	 */
	
/**
	 * 
	 */
	private static final long serialVersionUID = -2725392047240858969L;
/**
	 * 
	 */
	
private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JButton jButton1 = new JButton();
  private JPasswordField jPasswordField1 = new JPasswordField();
  private JLabel jLabel1 = new JLabel();

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

  public PasswordDialog(String title) {
    this(null, title, true);
  }

  public void clearPassword() {
    jPasswordField1.setText("");
  }

  public String getPassword() {
    return String.valueOf(jPasswordField1.getPassword());
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    this.setMinimumSize(new Dimension(224,0));
    jPasswordField1.setFont(new java.awt.Font("DialogInput", 0, 18));
    jPasswordField1.setText("jPasswordField1");
    jPasswordField1.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        jPasswordField1_keyTyped(e);
      }
    });
    jLabel1.setText(this.getTitle() + ":");
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
    this.setVisible(false);
  }

  public void jPasswordField1_keyTyped(KeyEvent e) {
    if (e.getKeyChar() == '\n' || e.getKeyChar() == '^' || e.getKeyChar() == 0x1B) {
      e.consume();
      this.jButton1_actionPerformed(new ActionEvent(this, 0, ""));
    }
  }
}
