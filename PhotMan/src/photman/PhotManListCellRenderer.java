/**   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package photman;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * <p>
 * This class is used to render the image object on the screen. The image is represented on screen 
 * by its thumbnail and its final name.
 * </p>
 * <pre>
 * Change history:
 *   2014-02-17 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManListCellRenderer implements ListCellRenderer<PhotManImage> {
	private final JPanel m_pane = new JPanel(new BorderLayout());
	private final JLabel m_icon = new JLabel((Icon) null,JLabel.CENTER);
	private final JLabel m_name = new JLabel("",JLabel.CENTER);
	
	/**
	 * Creates a component that can be used as "rubber stamps" to paint the cells in a JList. In this class the
	 * component is a pane containing two labels.
	 * @param list the JList object
	 * @param value the object to be represented in the cell
	 * @param index the index of the object in the list
	 * @param isSelected is this object selected ?
	 * @param cellHasFocus has this cell the actual focus ?
	 * @return the pane component
	 * 
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<? extends PhotManImage> list, PhotManImage value, int index,
			boolean isSelected, boolean cellHasFocus) {
		m_icon.setIcon(value.getThumbnail());
		m_name.setText(value.getFinalName());
		m_name.setForeground(isSelected? list.getSelectionForeground() : list.getForeground());
		
		m_pane.add(m_icon,BorderLayout.CENTER);
		m_pane.add(m_name,BorderLayout.SOUTH);
        m_pane.setBackground(isSelected? list.getSelectionBackground() : list.getBackground());
		return m_pane;
	}
}
