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
package com.ceridwen.selfissue.client.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import com.jaxfront.core.dom.DOMBuilder;
import com.jaxfront.core.dom.Document;
import com.jaxfront.core.help.HelpEvent;
import com.jaxfront.core.help.HelpListener;
import com.jaxfront.core.images.SystemImages;
import com.jaxfront.core.schema.ValidationException;
import com.jaxfront.core.ui.TypeVisualizerFactory;
import com.jaxfront.core.util.JAXFrontProperties;
import com.jaxfront.core.util.LicenseErrorException;
import com.jaxfront.core.util.URLHelper;
import com.jaxfront.core.util.io.BrowserControl;
import com.jaxfront.core.util.io.cache.XUICache;
//import com.jaxfront.pdf.PDFGenerator;
import com.jaxfront.swing.ui.editor.EditorPanel;
import com.jaxfront.swing.ui.editor.ShowXMLDialog;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/*****************************************************************************************************************************************************
 * JAXFront Integration Example and use of the EditorPanel API.
 * 
 ****************************************************************************************************************************************************/
/*-- 

 Copyright (C) 2001-2003 by xcentric technology & consulting GmbH. All rights reserved.

 This software is the confidential and proprietary information of xcentric technology & 
 consulting GmbH ("Confidential Information"). You shall not disclose such Confidential 
 Information and shall use it only in accordance with the terms of the license agreement
 you entered into with xcentric.

 www.jaxfront.com

 */
public class Editor extends JFrame implements WindowListener, HelpListener {
	private final static String APPLICATION_TITLE = "Self Issue Client Configuration Editor";
	private final static int WINDOW_HEIGHT = 768;
	private final static int WINDOW_WIDTH = 1024;
	// Misc. Components
	private Document _currentDom;
	private JPanel _centerPanel;
    private JPanel _helpPanel;
    private JSplitPane _splitPane;
	private EditorPanel _editor;
	private String _currentLanguage = "en"; // english as default
	private AbstractAction _defaultAction;
	private AbstractAction _reloadAction;
	private AbstractAction _saveAction;
	private AbstractAction _previewAction;
	private AbstractAction _printAction;
	private AbstractAction _exitAction;

	public Editor() {
		super();
		init();
	}

	private void init() {
		try {
			// init frame
			setTitle(APPLICATION_TITLE);
			setIconImage(JAXFrontProperties.getImage(SystemImages.ICON_XCENTRIC));
			getContentPane().setLayout(new BorderLayout());
			// initialize actions and gui components
			initActions();
			initMenuBar();
			initToolBar();
			_centerPanel = new JPanel(new BorderLayout());
			_centerPanel.setBorder(null);
            _helpPanel = new JPanel();			
            _splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            _splitPane.setDividerSize(4);
            _splitPane.setDividerLocation(500);
            _splitPane.setBorder(null);
            _splitPane.setTopComponent(_centerPanel);
            _splitPane.setBottomComponent(_helpPanel);
            getContentPane().add(_splitPane, BorderLayout.CENTER);
			addWindowListener(this);
			setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
			load(false);
			setVisible(true);
		} catch (LicenseErrorException licEx) {
			licEx.showLicenseDialog(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
		} catch (Throwable t) {
		}
		new Editor();
	}

	private void initActions() {
		_defaultAction = new AbstractAction("Default", JAXFrontProperties.getImageIcon(SystemImages.ICON_NEW_FILE)) {
			public void actionPerformed(ActionEvent e) {
				load(true);
			}
		};
		_reloadAction = new AbstractAction("Reload", JAXFrontProperties.getImageIcon(SystemImages.ICON_EDIT)) {
			public void actionPerformed(ActionEvent e) {
				load(false);
			}
		};
		_saveAction = new AbstractAction("Save", JAXFrontProperties.getImageIcon(SystemImages.ICON_SAVE_FILE)) {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		};
		_printAction = new AbstractAction("Print PDF", JAXFrontProperties.getImageIcon(SystemImages.ICON_PDF)) {
			public void actionPerformed(ActionEvent e) {
				print();
			}
		};
		_previewAction = new AbstractAction("View XML", JAXFrontProperties.getImageIcon(SystemImages.ICON_EDIT_XML)) {
			public void actionPerformed(ActionEvent e) {
				serialize();
			}
		};
		_exitAction = new AbstractAction("Exit", JAXFrontProperties.getImageIcon(SystemImages.ICON_CLOSE)) {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		};
		_saveAction.setEnabled(false);
		_printAction.setEnabled(false);
	}

	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);		
		JMenu applicationMenu = new JMenu("Configuration");
		applicationMenu.add(_reloadAction);
		applicationMenu.add(_saveAction);
		applicationMenu.addSeparator();
		applicationMenu.add(_defaultAction);
		applicationMenu.addSeparator();
		applicationMenu.add(_previewAction);
		applicationMenu.add(_printAction);
		applicationMenu.addSeparator();
		applicationMenu.add(_exitAction);
		menuBar.add(applicationMenu);
		setJMenuBar(menuBar);
	}

	private void initToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);		
		toolBar.add(_reloadAction);
		toolBar.add(_saveAction);
		toolBar.addSeparator();
		toolBar.add(_defaultAction);
		toolBar.addSeparator();
		toolBar.add(_previewAction);
		toolBar.add(_printAction);
		toolBar.addSeparator();
		toolBar.add(_exitAction);
//		JLabel logoLabel = new JLabel(JAXFrontProperties.getImageIcon("images/jaxfront.gif"));
//		JPanel logoPanel = new JPanel(new BorderLayout());
//		logoPanel.add(logoLabel, BorderLayout.EAST);
//		toolBar.add(logoPanel);
		getContentPane().add(toolBar, BorderLayout.NORTH);
	}

	private void print() {
		if (_currentDom != null) {
//			ByteArrayOutputStream bos = PDFGenerator.getInstance().print(_currentDom);
			ByteArrayOutputStream bos = null;
			if (bos != null) {
				try {
					String tempPDFName = "c:\\temp\\test.pdf";
					FileOutputStream fos = new FileOutputStream(tempPDFName);
					bos.writeTo(fos);
					fos.close();
					BrowserControl.displayURL(tempPDFName);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	private void visualizeDOM() {
		// set language
		_currentDom.getGlobalDefinition().setLanguage(_currentLanguage);
		com.jaxfront.core.type.Type lastSelectedType = null;
		if (_editor != null && _editor.getSelectedTreeNode() != null) {
			lastSelectedType = _editor.getSelectedTreeNode().getType();
		}
		TypeVisualizerFactory.getInstance().releaseCache(_currentDom);
		EditorPanel editorPanel = new EditorPanel(_currentDom.getRootType(), this);
		if (lastSelectedType != null) {
			editorPanel.selectNode(lastSelectedType);
		}
		editorPanel.setBorder(null);
        editorPanel.addHelpListener(this);
		JPanel validationErrorPanel = new JPanel(new BorderLayout());
		validationErrorPanel.setBorder(null);
		editorPanel.setTargetMessageTable(validationErrorPanel);
		_centerPanel.removeAll();
		_editor = editorPanel;
		_centerPanel.add(_editor, BorderLayout.CENTER);
		// activate actions
		_saveAction.setEnabled(true);
		_printAction.setEnabled(false);
	}

	private void save() {
		URL xmlUrl = URLHelper.getUserURL("com/ceridwen/selfissue/client/config/config.xml");
			try {
				_currentDom.saveAs(new File(xmlUrl.toURI()));
			} catch (ValidationException e) {				
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void serialize() {
		ShowXMLDialog dialog = new ShowXMLDialog(_currentDom);
		dialog.prettyPrint();
		Dimension dialogDim = dialog.getSize();
		Dimension thisDim = getSize();
		int x = (thisDim.width - dialogDim.width) / 2;
		int y = (thisDim.height - dialogDim.height) / 2;
		if (getLocation().x > 0 || getLocation().y > 0) {
			x = x + getLocation().x;
			y = y + getLocation().y;
		}
		dialog.setLocation(((x > 0) ? x : 0), ((y > 0) ? y : 0));
		dialog.setVisible(true);		
	}

	private void load(boolean defaultXml) {
//		URL url = URLHelper.getUserURL("examples/purchaseOrder/po.xsd");
//		URL xmlUrl = URLHelper.getUserURL("examples/purchaseOrder/po.xml");
//		URL xuiUrl = URLHelper.getUserURL("examples/purchaseOrder/po.xui");
		URL url = URLHelper.getUserURL("com/ceridwen/selfissue/client/config/config.xsd");
		URL xuiUrl = URLHelper.getUserURL("com/ceridwen/selfissue/client/config/config.xui");
		URL xmlUrl = null;
		if (defaultXml) {
			xmlUrl = URLHelper.getUserURL("com/ceridwen/selfissue/client/config/default.xml");
		} else { 
			xmlUrl = URLHelper.getUserURL("com/ceridwen/selfissue/client/config/config.xml");
		}
			
		String root = null;
		try {
	    	XUICache.getInstance().releaseCache();
			_currentDom = DOMBuilder.getInstance().build("default-context", url, xmlUrl, xuiUrl, root);
			_currentDom.getGlobalDefinition().setLanguage(_currentLanguage);
			if (_editor != null) _editor.selectNode((com.jaxfront.core.type.Type) null);
			visualizeDOM();
		} catch (Exception ex) {
		}
	}

    public void showHelp( HelpEvent event ) {
        _helpPanel.removeAll();
        String helpText = event.getHelpID();
        JTextArea helpLabel = new JTextArea(helpText);
        helpLabel.setEditable(false);
        helpLabel.setOpaque(false);
//        JLabel helpLabel = new JLabel(helpText);
        _helpPanel.add(helpLabel, BorderLayout.CENTER);
        _helpPanel.updateUI();
    }

	private void exit() {
		System.exit(0);
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
		exit();
	}

	public void windowClosing(WindowEvent e) {
		exit();
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}