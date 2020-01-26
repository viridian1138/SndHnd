




//$$strtCprt
/**
* SndHnd
* 
* Copyright (C) 1992-2020 Thornton Green
* 
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with this program; if not, 
* see <http://www.gnu.org/licenses>.
* Additional permission under GNU GPL version 3 section 7
*
*/
//$$endCprt







package core;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * View that displays the number of beats for each measure (the time signature).
 * For instance, it's possible to change time signature from a 4/4 measure to a 6/8 measure
 * by having a "4" followed by a "6" in the view.
 * Measure numbers start at zero.
 * The denominator of the time signature (which western musical symbol assigns to one beat) is not stored in the software.
 * 
 * @author tgreen
 *
 */
public class MeasureViewPane extends JPanel {
	
	/**
	 * The number of beats per each measure.  It is assumed that
	 * the last number defines the remaining time signatures out to infinity.
	 */
	private int[] measures;
	
	/**
	 * Table model showing the set of measures.
	 */
	private AbstractTableModel tab = null;
	
	/**
	 * Constructor.
	 */
	public MeasureViewPane()
	{
		int[] meas = SongData.measuresStore.getBeatsPerMeasure();
		measures = meas;
		
		AbstractTableModel tb = new AbstractTableModel() {
			
			@Override
		    public String getColumnName(int col) {
		        return( "" + col );
		    }
		    
		    /**
		     * Gets the number of rows in the table.
		     * @return The number of rows in the table.
		     */
		    public int getRowCount() { return( 1 ); }
		    
		    /**
		     * Gets the number of columns in the table.
		     * @return The number of columns in the table.
		     */
		    public int getColumnCount() { return( measures.length ); }
		    
		    /**
		     * Gets the value for a particular row-column cell.
		     * @param row The input row.
		     * @param column The input column.
		     * @return The value at the row-column.
		     */
		    public Object getValueAt(int row, int col) {
		        return( new Integer( measures[ col ] ) );
		    }
		    
		    @Override
		    public boolean isCellEditable(int row, int col)
		        { return true; }
		    
		    @Override
		    public Class getColumnClass(int c) {
		        return( Integer.class );
		    }
		    
		    @Override
		    public void setValueAt(Object value, int row, int col) {
		    	try
		    	{
		    	int[] mm = new int[ measures.length ];
		    	int count;
		    	for( count = 0 ; count < measures.length ; count++ )
		    	{
		    		mm[ count ] = measures[ count ];
		    	}
		    	mm[ col ] = ( (Integer) value ).intValue();
		        if( mm[ col ] < 1 )
		        {
		        	throw( new RuntimeException( "Bad" ) );
		        }
		        if( mm[ col ] > 20 )
		        {
		        	throw( new RuntimeException( "Bad" ) );
		        }
		        
		        SongData.measuresStore.setBeatsPerMeasure( mm );
		        
		    	SongListeners.updateViewPanes();
		    	System.out.println( "Set Value " + mm[ col ] );
		    	}
		    	catch( Throwable ex )
		    	{
		    		ex.printStackTrace( System.out );
		    	}
		    }
		};
		tab = tb;
		
		JTable table = new JTable( tab );
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scp = new JScrollPane( table );
		setLayout( new BorderLayout( 0 , 0 ) );
		add( BorderLayout.CENTER , scp );
		setMinimumSize( new Dimension( 400 , 200 ) );
		setPreferredSize( new Dimension( 400 , 200 ) );
	}

	
	/**
	 * Refreshes the set of measures being displayed.
	 */
	public void refreshMeasures()
	{
		int[] meas = SongData.measuresStore.getBeatsPerMeasure();
		measures = meas;
		tab.fireTableStructureChanged();
		MeasureViewPane.this.repaint();
	}
	
}

