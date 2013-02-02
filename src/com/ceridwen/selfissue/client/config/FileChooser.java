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

import java.awt.Font;
import java.io.File;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.jaxfront.core.type.SimpleType;
import com.jaxfront.swing.ui.beans.AbstractSimpleVisualizer;

public class FileChooser extends AbstractSimpleVisualizer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2219930215862492984L;
		@SuppressWarnings("rawtypes") // Targeting Java 1.6
		private JComboBox _fileChooser;
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

		@SuppressWarnings({ "rawtypes", "unchecked" }) // Targeting Java 1.6		
		protected JComponent createEditorComponent() {
			_fileChooser = new JComboBox();
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
	        	chooser.addItem("");
	        	URL resource = Configuration.LoadResource(CONFIG);
	        	String base = resource.getPath().substring(0, resource.getPath().lastIndexOf(CONFIG));
	        	addFiles(new File(base), chooser, new File(base), _filter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
}
