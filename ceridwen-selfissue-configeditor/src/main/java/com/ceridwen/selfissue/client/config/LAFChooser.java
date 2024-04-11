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


import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JComboBox;

import com.jaxfront.core.type.SimpleType;
import com.jaxfront.swing.ui.beans.AbstractSimpleVisualizer;
import java.util.ArrayList;
import javax.swing.UIManager;

public class LAFChooser extends AbstractSimpleVisualizer {
		/**
	 * 
	 */
	private static final long serialVersionUID = 5617887268768044397L;
		@SuppressWarnings("rawtypes") // Targeting Java 1.6
		private JComboBox _lafChooser;
			
        @Override
		public String getText() {
			String value = null;
			if (_lafChooser != null) {
				value =_lafChooser.getSelectedItem().toString(); 
			}
			return value;
		}
		
        @Override
		public void populateView() {
			if (getModel() != null && _lafChooser != null) {
				String value = ((SimpleType)getModel()).getValue();
				if (value != null) {
					_lafChooser.setSelectedItem(value);
				}
			}
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" }) // Targeting Java 1.6				
        @Override
		protected JComponent createEditorComponent() {
			_lafChooser = new JComboBox(getLAFNames());
			_lafChooser.setEditable(false);
			if (_lafChooser.getItemCount() > 0) {			
				_lafChooser.setSelectedIndex(0);
			}
			_lafChooser.addFocusListener(this);
			_lafChooser.setFont(new Font(null,1,20));			
			_firstFocusableComponent = _lafChooser;			
			return _lafChooser;
		}
		
		protected String[] getLAFNames()
		{
                        installThirdParyLAF();   
                        
			ArrayList<String> lafs = new ArrayList<>();
                        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                            lafs.add(info.getClassName());
                        }
			return lafs.toArray(new String[]{}); 
		}		

    private void installThirdParyLAF() {
        UIManager.installLookAndFeel(FlatLightLaf.NAME, FlatLightLaf.class.getName());
        UIManager.installLookAndFeel(FlatLightLaf.NAME, FlatDarkLaf.class.getName());
        UIManager.installLookAndFeel(FlatLightLaf.NAME, FlatDarculaLaf.class.getName());
        UIManager.installLookAndFeel(FlatLightLaf.NAME, FlatIntelliJLaf.class.getName());
    }
}