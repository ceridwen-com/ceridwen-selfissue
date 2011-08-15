package com.ceridwen.selfissue.client.config;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.jaxfront.core.type.SimpleType;
import com.jaxfront.swing.ui.beans.AbstractSimpleVisualizer;

public class FileChooser extends AbstractSimpleVisualizer {
		private JComboBox<String> _fileChooser;
		String _filter[];
		
		public FileChooser() {
			_filter = null;
		}
		
		public FileChooser(String filter[]) {
			_filter = filter;
		}
			
		public String getText() {
			String value = null;
			if (_fileChooser != null) {
				value =_fileChooser.getSelectedItem().toString(); 
			}
			return value;
		}
		
		public void populateView() {
			if (getModel() != null && _fileChooser != null) {
				String value = ((SimpleType)getModel()).getValue();
				if (value != null) {
					_fileChooser.setSelectedItem(value);
				}
			}
		}
		
		protected JComponent createEditorComponent() {
			_fileChooser = new JComboBox<String>();
			getFilePaths(_fileChooser);
			_fileChooser.setEditable(false);
			if (_fileChooser.getItemCount() > 0) {
				_fileChooser.setSelectedIndex(0);
			}
			_fileChooser.addFocusListener(this);
			_fileChooser.setFont(new Font(null,1,20));			
			_firstFocusableComponent = _fileChooser;			
			return _fileChooser;
		}

		
		public void addFiles(File root, JComboBox<String> chooser, File input, String[] ext) {
			if (input.isDirectory()) {
				for (File file: input.listFiles()) {
					addFiles(root, chooser, file, ext);
				}
				
			} else {
				String fullPath = input.getAbsolutePath();
				if (ext == null) {
					String rootPath = root.getAbsolutePath();
					String relativePath = new File(rootPath).toURI().relativize(new File(fullPath).toURI()).getPath();
					chooser.addItem(relativePath);
					return;					
				}
				for (String x: ext) {
					if (fullPath.endsWith("." + x)) {
						String rootPath = root.getAbsolutePath();
						String relativePath = new File(rootPath).toURI().relativize(new File(fullPath).toURI()).getPath();
						chooser.addItem(relativePath);
						return;
					}
				}
			}
		}
				
		protected void getFilePaths(JComboBox<String> chooser)		
		{
			String CONFIG = "com/ceridwen/selfissue/client/config/config.xml";
	        try {
	        	URL resource = Configuration.LoadResource(CONFIG);
	        	String base = resource.getPath().substring(0, resource.getPath().lastIndexOf(CONFIG));
	        	addFiles(new File(base), chooser, new File(base), _filter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
}
