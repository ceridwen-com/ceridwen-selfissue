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

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JComponent;
import javax.swing.JComboBox;

import com.jaxfront.core.type.SimpleType;
import com.jaxfront.swing.ui.beans.AbstractSimpleVisualizer;

import org.apache.commons.lang3.ArrayUtils;

public class FontChooser extends AbstractSimpleVisualizer {
		/**
	 * 
	 */
	private static final long serialVersionUID = 5617887268768044397L;
		@SuppressWarnings("rawtypes") // Targeting Java 1.6
		private JComboBox _fontChooser;
			
		public String getText() {
			String value = null;
			if (_fontChooser != null) {
				value =_fontChooser.getSelectedItem().toString(); 
			}
			return value;
		}
		
		public void populateView() {
			if (getModel() != null && _fontChooser != null) {
				String value = ((SimpleType)getModel()).getValue();
				if (value != null) {
					_fontChooser.setSelectedItem(value);
				}
			}
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" }) // Targeting Java 1.6				
		protected JComponent createEditorComponent() {
			_fontChooser = new JComboBox(getFontNames());
			_fontChooser.setEditable(false);
			if (_fontChooser.getItemCount() > 0) {			
				_fontChooser.setSelectedIndex(0);
			}
			_fontChooser.addFocusListener(this);
			_fontChooser.setFont(new Font(null,1,20));			
			_firstFocusableComponent = _fontChooser;			
			return _fontChooser;
		}
		
		protected String[] getFontNames()
		{
			String logicalFonts[] = {"Serif", "SansSerif", "Monospaced", "Dialog", "DialogInput"};
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			return ArrayUtils.addAll(logicalFonts, env.getAvailableFontFamilyNames()); 
		}		
}
