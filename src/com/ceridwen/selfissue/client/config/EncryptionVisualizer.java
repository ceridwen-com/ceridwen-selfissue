package com.ceridwen.selfissue.client.config;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JPasswordField;

import com.jaxfront.core.type.SimpleType;
import com.jaxfront.swing.ui.beans.AbstractSimpleVisualizer;


public class EncryptionVisualizer extends AbstractSimpleVisualizer {
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