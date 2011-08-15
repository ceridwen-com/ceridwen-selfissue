package com.ceridwen.selfissue.client.config;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;

import com.jaxfront.core.type.SimpleType;
import com.jaxfront.swing.ui.beans.AbstractSimpleVisualizer;

import org.apache.commons.lang3.ArrayUtils;

public class FontChooser extends AbstractSimpleVisualizer {
		private JComboBox<String> _fontChooser;
			
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
		
		protected JComponent createEditorComponent() {
			_fontChooser = new JComboBox<String>(getFontNames());
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
