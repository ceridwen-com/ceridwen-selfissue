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
package com.ceridwen.selfissue.client.config;

import com.ceridwen.util.io.NullOutputStream;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import com.jaxfront.core.dom.DOMBuilder;
import com.jaxfront.core.dom.DirtyChangeEvent;
import com.jaxfront.core.dom.DirtyChangeListener;
import com.jaxfront.core.dom.Document;
import com.jaxfront.core.dom.DocumentCreationException;
import com.jaxfront.core.help.HelpEvent;
import com.jaxfront.core.help.HelpListener;
import com.jaxfront.core.images.SystemImages;
import com.jaxfront.core.schema.SchemaCreationException;
import com.jaxfront.core.schema.ValidationException;
import com.jaxfront.core.ui.TypeVisualizerFactory;
import com.jaxfront.core.util.JAXFrontProperties;
import com.jaxfront.core.util.LicenseErrorException;
import com.jaxfront.core.util.URLHelper;
import com.jaxfront.core.util.io.cache.XUICache;
import com.jaxfront.swing.ui.editor.EditorPanel;
import com.jaxfront.swing.ui.editor.ShowXMLDialog;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


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
@SuppressWarnings("serial")
public class Editor extends JFrame implements WindowListener, HelpListener, DirtyChangeListener {
	private final static String APPLICATION_TITLE = "Self Issue Client Configuration Editor";
	private final static int WINDOW_HEIGHT = 768;
	private final static int WINDOW_WIDTH = 1024;
	// Misc. Components
	private Document _currentDom;
	private boolean isDirty = false;
	private JPanel _centerPanel;
    private JPanel _helpPanel;
    private JSplitPane _splitPane;
	private EditorPanel _editor;
	private final String _currentLanguage = "en"; // english as default
	private AbstractAction _defaultAction;
	private AbstractAction _reloadAction;
	private AbstractAction _saveAction;
	private AbstractAction _previewAction;
	private AbstractAction _printAction;
	private AbstractAction _exitAction;
        
    private static final Log log = LogFactory.getLog(Editor.class);        

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
			// initialize actions and gui components			getContentPane().
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
            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			addWindowListener(this);
			setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
			setVisible(true);
			_reloadAction.actionPerformed(new ActionEvent(this, 0, ""));
		} catch (LicenseErrorException licEx) {
			licEx.showLicenseDialog(this);
		} catch (Exception e) {
                    Editor.log.fatal("Checkin Panel Failure: " + e.getMessage() + " - " +
                        Arrays.toString(e.getStackTrace()));
		}
	}

	public static void main(String[] args) {
		System.setOut(new PrintStream(new NullOutputStream()));
		new Editor();
	}

	private void initActions() {
		_defaultAction = CursorController.createAction("Default", JAXFrontProperties.getImageIcon(SystemImages.ICON_NEW_FILE), this,
			new AbstractAction() {
                                @Override
				public void actionPerformed(ActionEvent e) {
					load(true);
				}
			}
		);
		_reloadAction = CursorController.createAction("Reload", JAXFrontProperties.getImageIcon(SystemImages.ICON_EDIT), this,
			new AbstractAction() {	
                                @Override
				public void actionPerformed(ActionEvent e) {
					load(false);
				}
			}
		);
		_saveAction = CursorController.createAction("Save", JAXFrontProperties.getImageIcon(SystemImages.ICON_SAVE_FILE), this,
			new AbstractAction() {	
                                @Override
				public void actionPerformed(ActionEvent e) {
					save();
				}
			}
		);
		_printAction = CursorController.createAction("Print PDF", JAXFrontProperties.getImageIcon(SystemImages.ICON_PDF), this,
			new AbstractAction() {	
                                @Override
				public void actionPerformed(ActionEvent e) {
					print();
				}
			}
		);
		_previewAction = CursorController.createAction("View XML", JAXFrontProperties.getImageIcon(SystemImages.ICON_EDIT_XML), this,
			new AbstractAction() {	
                                @Override
				public void actionPerformed(ActionEvent e) {
					serialize();
				}
			}
		);
		_exitAction = new AbstractAction("Exit", JAXFrontProperties.getImageIcon(SystemImages.ICON_CLOSE)) {
                        @Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		};
		_saveAction.setEnabled(false);
		_printAction.setEnabled(false);
	}

	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
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
		toolBar.add(_reloadAction);
		toolBar.add(_saveAction);
		toolBar.addSeparator();
		toolBar.add(_defaultAction);
		toolBar.addSeparator();
		toolBar.add(_previewAction);
//		toolBar.add(_printAction);
		toolBar.addSeparator();
		toolBar.add(_exitAction);
//		JLabel logoLabel = new JLabel(JAXFrontProperties.getImageIcon("images/jaxfront.gif"));
//		JPanel logoPanel = new JPanel(new BorderLayout());
//		logoPanel.add(logoLabel, BorderLayout.EAST);
//		toolBar.add(logoPanel);
		getContentPane().add(toolBar, BorderLayout.NORTH);
	}

	private void print() {
//		if (_currentDom != null) {
//			ByteArrayOutputStream bos = PDFGenerator.getInstance().print(_currentDom);
//			if (bos != null) {
//				try {
//					String tempPDFName = "c:\\temp\\test.pdf";
//					FileOutputStream fos = new FileOutputStream(tempPDFName);
//					bos.writeTo(fos);
//					fos.close();
//					BrowserControl.displayURL(tempPDFName);
//				} catch (Throwable t) {
//					t.printStackTrace();
//				}
//			}
//		}
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
		_centerPanel.revalidate();
		// activate actions
		_saveAction.setEnabled(true);
		_printAction.setEnabled(false);
		isDirty = false;
		_currentDom.addDirtyChangeListener(this);
	}

	private void save() {
		URL xmlUrl = URLHelper.getUserURL("com/ceridwen/selfissue/client/config/config.xml");
			try {
				_currentDom.saveAs(new File(xmlUrl.toURI()));
				isDirty = false;
			} catch (ValidationException | IOException | URISyntaxException e) {				
				// TODO Auto-generated catch block
                             Editor.log.fatal("Checkin Panel Failure: " + e.getMessage() + " - " +
                                Arrays.toString(e.getStackTrace()));
			}
            // TODO Auto-generated catch block
            // TODO Auto-generated catch block
            
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
		if (defaultXml) {
			if (JOptionPane.showConfirmDialog(this, "This will lose all configuration. Do you wish to proceed?", "Changes will be lost", JOptionPane.YES_NO_OPTION) == 1) {
				return;
			}			
		} else if (isDirty) {
			if (JOptionPane.showConfirmDialog(this, "Recent changes will be lost. Do you wish to proceed?", "Changes will be lost", JOptionPane.YES_NO_OPTION) == 1) {
				return;
			}
		}

//		URL url = URLHelper.getUserURL("examples/purchaseOrder/po.xsd");
//		URL xmlUrl = URLHelper.getUserURL("examples/purchaseOrder/po.xml");
//		URL xuiUrl = URLHelper.getUserURL("examples/purchaseOrder/po.xui");
		URL url = URLHelper.getUserURL("com/ceridwen/selfissue/client/config/config.xsd");
		URL xuiUrl = URLHelper.getUserURL("com/ceridwen/selfissue/client/config/config.xui");
		URL xmlUrl;
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
		} catch (DocumentCreationException | SchemaCreationException e) {
                    Editor.log.fatal("Checkin Panel Failure: " + e.getMessage() + " - " +
                        Arrays.toString(e.getStackTrace()));
                }
	}

        @Override
    public void showHelp( HelpEvent event ) {
        _helpPanel.removeAll();
        String helpText = event.getHelpID();
        JTextArea helpLabel = new JTextArea(helpText);
        helpLabel.setEditable(false);
        helpLabel.setOpaque(false);
        helpLabel.setLineWrap(true);
        helpLabel.setWrapStyleWord(true);
//        JLabel helpLabel = new JLabel(helpText);
        helpLabel.setBounds(_helpPanel.getBounds());
        _helpPanel.add(helpLabel, BorderLayout.CENTER);
        _helpPanel.updateUI();
    }

	private void exit() {
		if (isDirty) {
			if (JOptionPane.showConfirmDialog(this, "Changes have not been saved. Do you wish to exit?", "Changes not saved", JOptionPane.YES_NO_OPTION) == 0) {
				System.exit(0);
			}
		} else {
			System.exit(0);
		}
	}
	
        @Override
	public void windowActivated(WindowEvent e) {
	}

        @Override
	public void windowClosed(WindowEvent e) {
		exit();
	}

        @Override
	public void windowClosing(WindowEvent e) {
		exit();
	}

        @Override
	public void windowDeactivated(WindowEvent e) {
	}

        @Override
	public void windowDeiconified(WindowEvent e) {
	}

        @Override
	public void windowIconified(WindowEvent e) {
	}

        @Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void dirtyChange(DirtyChangeEvent e) {
		isDirty = e.hasChanged();		
	}
}