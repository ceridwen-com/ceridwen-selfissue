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
import java.nio.charset.Charset;
import java.util.SortedMap;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.ceridwen.circulation.SIP.messages.Message;
import com.jaxfront.core.type.SimpleType;
import com.jaxfront.swing.ui.beans.AbstractSimpleVisualizer;

public class CharsetChooser extends AbstractSimpleVisualizer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9134550751102339368L;

		@SuppressWarnings("rawtypes") // Targeting Java 1.6
		private JComboBox _charsetChooser;
		@SuppressWarnings("rawtypes") // Targeting Java 1.6
		private DefaultComboBoxModel _model;
		
		public CharsetChooser() {
		}
					
		public String getText() {
			String value = null;
			if (_charsetChooser != null) {
				value =_charsetChooser.getSelectedItem().toString(); 
			}
			return value;
		}
		
		public void populateView() {
			if (getModel() != null && _charsetChooser != null) {
				String value = ((SimpleType)getModel()).getValue();
				if (value != null) {
					if (_model.getIndexOf(value) >= 0) {
						_charsetChooser.setSelectedItem(value);
					} else {
						_charsetChooser.setSelectedItem(Message.getCharsetEncoding());
					}
				}
			}
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" }) // Targeting Java 1.6		
		protected JComponent createEditorComponent() {
			_model = new DefaultComboBoxModel();
			_charsetChooser = new JComboBox(_model);
			getCharsetPaths(_charsetChooser);
			_charsetChooser.setEditable(false);
			if (_charsetChooser.getItemCount() > 0) {
				_charsetChooser.setSelectedIndex(0);
			}
			_charsetChooser.addFocusListener(this);
			_charsetChooser.setFont(new Font(null,1,20));			
			_firstFocusableComponent = _charsetChooser;			
			return _charsetChooser;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" }) // Targeting Java 1.6		
		protected void getCharsetPaths(JComboBox chooser)		
		{
	        try {
	        	SortedMap<String, Charset> charsets = Charset.availableCharsets();
	        	TreeSet<String> names = new TreeSet<String>();
	        	for (Charset charset: charsets.values()) {
	        		names.add(charset.name());
	        		for (String alias: charset.aliases()) {
	        			names.add(alias);
	        		}
	        	}	
                        chooser.addItem("");
	        	for (String charset: names) {
	        		chooser.addItem(charset);	        		
	        	}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
}
