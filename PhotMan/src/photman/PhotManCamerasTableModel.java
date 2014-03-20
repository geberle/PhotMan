package photman;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

/**
 * <p>
 * This class redefines the table model used to allow the user to type in the cameras time offsets.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <pre>
 * Change history:
 *   2014-02-19 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManCamerasTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -422178333668045142L;
	
	private String[][] m_data;

	private final String[] m_columns = {"Cameras","Time offset [hh:mm:ss]"};
	private final SimpleDateFormat m_sdf = new SimpleDateFormat("HH:mm:ss");
	private final String m_timeInit = "00:00:00";

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return m_columns.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (m_data == null) return 0;
		return m_data.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		if (m_data == null) return null;
		return m_data[row][col];
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col) {
		return m_columns[col];
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int c) {
		return String.class;
	}
	
	/**
	 * Checks if the given cell is editable or not. Only cells in column 1 are editable.
     * @param row the row defining the cell
     * @param col the column defining the cell
     * @return true if the cell is editable, false otherwise
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		return (col == 1);
	}	
	
	/**
	 * Sets the value in the given table cell. Only column 1 can be modified and the content
	 * of the cell is verified before it is displayed.
     * @param value the data to be displayed in the cell
     * @param row the row defining the cell
     * @param col the column defining the cell
     * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
    public void setValueAt(Object value, int row, int col) {
		if (value == null) return;
		String str = (String) value;
		if (col == 1) {
			try {
				String str1 = str.substring(0,1);
				boolean hasSign = false;
				if (("+".equals(str1)) || ("-".equals(str1))) {
					str = str.substring(1);
					hasSign = true;
				}
				m_sdf.parse(str);
				if (hasSign) str = str1 + str;
			}
			catch (Exception e) {
				str = m_timeInit;
			}
		}
        m_data[row][col] = str;
        fireTableCellUpdated(row,col);
    }

    /**
     * Initializes the data to be displayed on the screen in the table.
     * @param data the data to be displayed
     */
    protected void setData(HashMap<String,String> data) {
		m_data = new String[data.size()][m_columns.length];
		int row = 0;
		for (String cam : data.keySet()) {
			m_data[row][0] = cam;
			m_data[row][1] = data.get(cam);
			row++;
		}
	}
    
    /**
     * Returns the data displayed in the table, as the user modified them.
     * @return the table data
     */
    protected HashMap<String,String> getData() {
    	HashMap<String,String> cams = new HashMap<>();
    	for (int i1 = 0; i1 < m_data.length; i1++) cams.put(m_data[i1][0],m_data[i1][1]);
    	return cams;
    }
}
