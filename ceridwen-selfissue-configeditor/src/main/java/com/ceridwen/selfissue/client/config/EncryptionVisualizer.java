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

import javax.swing.JComponent;
import javax.swing.JPasswordField;

import com.jaxfront.core.type.SimpleType;
import com.jaxfront.swing.ui.beans.AbstractSimpleVisualizer;


public class EncryptionVisualizer extends AbstractSimpleVisualizer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -956743761694232016L;
	private JPasswordField _textArea;
		
	public String getText() {
		String value = null;
		if (_textArea != null) {
			value = new String(_textArea.getPassword());
		}
		if (value != null) {
			value = Configuration.Encrypt(value);
		}
		return value;
	}
	
	public void populateView() {
		if (getModel() != null && _textArea != null) {
			String value = ((SimpleType)getModel()).getValue();
			if (value != null) {
				_textArea.setText(Configuration.Decrypt(value));
			}
		}
	}
	
	protected JComponent createEditorComponent() {
		_textArea = new JPasswordField();
		_textArea.addFocusListener(this);
		_textArea.setFont(new Font(null,1,20));
		_firstFocusableComponent = _textArea;
		return _textArea;
	}
}