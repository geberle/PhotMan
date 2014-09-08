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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * <p>
 * This class is used to define the drag and drop mechanism in a single JList.
 * </p>
 * <pre>
 * Change history:
 *   2014-02-17 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManListItemTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 6040333703697686051L;

	private DataFlavor m_localObjectFlavor;
	private Object[] m_transferedObjects = null;
	private int[] m_indices = null;
	private int m_addIndex  = -1; //Location where items were added
	private int m_addCount  = 0;  //Number of items added.
	
	/**
	 * Class constructor.
	 */
	public PhotManListItemTransferHandler() {
		m_localObjectFlavor = new ActivationDataFlavor(
				Object[].class,DataFlavor.javaJVMLocalObjectMimeType,"Array of items");
	}
	
	/**
	 * Creates a transferable object to be used as the source for the data transfer (here the list
	 * of the selected images).
	 * @param c the source for the transferable objects (the images list)
	 * @return the created transferable object
	 * 
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@SuppressWarnings("unchecked")
	@Override 
	protected Transferable createTransferable(JComponent c) {
		JList<PhotManImage> list = (JList<PhotManImage>) c;
		m_indices = list.getSelectedIndices();
		m_transferedObjects = list.getSelectedValuesList().toArray();
		return new DataHandler(m_transferedObjects,m_localObjectFlavor.getMimeType());
	}
	
	/**
	 * This method is called repeatedly during a drag and drop operation to allow the developer
	 * to configure properties of, and to return the acceptability of transfers; with a return value of 
	 * true indicating that the transfer represented by the given TransferSupport (which contains all of 
	 * the details of the transfer) is acceptable at the current time, and a value of false rejecting the transfer.
	 * @param info the TransferSupport object
	 * @return true if the transfer is acceptable, false otherwise

	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override 
	public boolean canImport(TransferSupport info) {
		if (!info.isDrop() || !info.isDataFlavorSupported(m_localObjectFlavor)) return false;
		return true;
	}
	
	/**
	 * Returns the type of transfer actions supported by the source. Actually only a move is allowed.
	 * @param c the source object
	 * @return true if the transfer is allowed, false otherwise

	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override 
	public int getSourceActions(JComponent c) {
		return MOVE;
	}
	
	/**
	 * Causes a transfer to occur from a drag and drop operation.
	 * @param info the object containing all the transfer information
	 * @return true if the operation succeeded, false otherwise
	 * 
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 */
	@SuppressWarnings("unchecked")
	@Override 
	public boolean importData(TransferSupport info) {
		if (!canImport(info)) return false;
		
		JList<PhotManImage> target = (JList<PhotManImage>) info.getComponent();
		JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
		DefaultListModel<PhotManImage> listModel = (DefaultListModel<PhotManImage>) target.getModel();
		
		int index = dl.getIndex();
		int max = listModel.getSize();
		if(index<0 || index>max) index = max;
		m_addIndex = index;
		try {
			Object[] values = (Object[]) info.getTransferable().getTransferData(m_localObjectFlavor);
			m_addCount = values.length;
			for (int i1 = 0; i1 < values.length; i1++) {
				int idx = index++;
				listModel.add(idx,(PhotManImage) values[i1]);
				target.addSelectionInterval(idx,idx);
			}
			return true;
		} 
		catch(UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		} 
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return false;
	}
	
	/**
	 * This method is invoked after data has been exported, in order to allow the cleanup of the transfer.
	 * @param c the source object
	 * @param data the transfered data
	 * @param action the type of the action that was performed
	 * 
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
	 */
	@Override 
	protected void exportDone(JComponent c, Transferable data, int action) {
		cleanup(c,action == MOVE);
	}
	
	/**
	 * Does the cleanup after the transfer. In this case removes the transfered objects from their previous
	 * place in the list.
	 * @param c the source object
	 * @param remove should we remove the transfered objects form their previous place ?
	 */
	@SuppressWarnings("unchecked")
	private void cleanup(JComponent c, boolean remove) {
		if (remove && m_indices != null) {
			JList<PhotManImage> source = (JList<PhotManImage>) c;
			DefaultListModel<PhotManImage> model = (DefaultListModel<PhotManImage>) source.getModel();
			if (m_addCount > 0) {
				for (int i1 = 0; i1 < m_indices.length; i1++) {
					if (m_indices[i1] >= m_addIndex) {
						m_indices[i1] += m_addCount;
					}
				}
			}
			for (int i1 = m_indices.length-1; i1 >= 0; i1--) model.remove(m_indices[i1]);
		}
		m_indices  = null;
		m_addCount = 0;
		m_addIndex = -1;
	}
}
