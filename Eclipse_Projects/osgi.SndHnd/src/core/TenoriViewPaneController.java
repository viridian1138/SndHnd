




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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;



/**
 * Property editor for controlling a tenori view.
 * 
 * @author tgreen
 *
 */
public class TenoriViewPaneController extends JPanel implements ActionListener {
	
	/**
	 * Menu item for starting tenori playback.
	 */
	protected JMenuItem play = new JMenuItem( "Play" );
	
	/**
	 * Menu item for stopping tenori playback.
	 */
	protected JMenuItem stop = new JMenuItem( "Stop" );
	
	/**
	 * Menu item for rotating the entire tenori by one column to the left.
	 */
	protected JMenuItem rotateLeft = new JMenuItem( "Rotate Left" );
	
	/**
	 * Menu item for rotating the entire tenori by one column to the right.
	 */
	protected JMenuItem rotateRight = new JMenuItem( "Rotate Right" );
	
	/**
	 * Menu item for editing the key of the tenori.
	 */
	protected JMenuItem editKey = new JMenuItem( "Edit Key..." );
	
	/**
	 * Menu item for adding statistical variation to model human error in timing and pitch.
	 */
	protected JMenuItem editSpread = new JMenuItem( "Edit Spread..." );
	
	/**
	 * Menu item for editing the vertical and horizontal grid size of the tenori.
	 */
	protected JMenuItem editGrid = new JMenuItem( "Edit Grid..." );
	
	/**
	 * Menu item for cloning the tenori.
	 */
	protected JMenuItem cloneTenori = new JMenuItem( "Clone Tenori" );
	
	/**
	 * Menu item for inserting tenori notes into the current song.
	 */
	protected JMenuItem insertNotes = new JMenuItem( "Insert Notes..." );
	
	/**
	 * Menu item for printing the list of pitches in the current intonation at the various tenori levels.
	 */
	protected JMenuItem printPitchList = new JMenuItem( "Print Pitch List" );
	
	/**
	 * Menu item for splitting the tenori.
	 */
	protected JMenuItem autoSplit = new JMenuItem( "Auto Split..." );
	
	/**
	 * Menu item for saving a tenori to a file.
	 */
	protected JMenuItem saveTenori = new JMenuItem( "Save Tenori..." );
	
	/**
	 * Menu item for loading a tenori from a file.
	 */
	protected JMenuItem loadTenori = new JMenuItem( "Load Tenori..." );
	
	
	
	
	/**
	 * The tenori view pane to edit.
	 */
	protected TenoriViewPane pane;

	/**
	 * Constructor.
	 * @param _pane The tenori view pane to edit.
	 */
	public TenoriViewPaneController( TenoriViewPane _pane ) {
		super();
		pane = _pane;
		
		JMenuBar mbar = new JMenuBar();
		
		setLayout(new BorderLayout(0,0));
		add( BorderLayout.NORTH , mbar );
		
		JMenu menu = new JMenu( "Edit" );
		mbar.add( menu );
		
		

		menu.add(play);
		menu.add(stop);
		menu.add(rotateLeft);
		menu.add(rotateRight);
		menu.add(editKey);
		menu.add(editSpread);
		menu.add(editGrid);
		menu.add(cloneTenori);
		menu.add(insertNotes);
		menu.add(printPitchList);
		menu.add(autoSplit);
		menu.add(saveTenori);
		menu.add(loadTenori);
		


		
		play.addActionListener(this);
		stop.addActionListener(this);
		rotateLeft.addActionListener(this);
		rotateRight.addActionListener(this);
		editKey.addActionListener(this);
		editSpread.addActionListener(this);
		editGrid.addActionListener(this);
		cloneTenori.addActionListener(this);
		insertNotes.addActionListener(this);
		printPitchList.addActionListener(this);
		autoSplit.addActionListener(this);
		saveTenori.addActionListener(this);
		loadTenori.addActionListener(this);
	}
	
	
	/**
	 * Handles a button press by executing the appropriate command.
	 */
	public void actionPerformed(ActionEvent e) {
		
		if( e.getSource() == play )
		{
			pane.play();
		}
		
		if( e.getSource() == stop )
		{
			pane.stop();
		}
		
		if( e.getSource() == rotateLeft )
		{
			pane.rotateLeft();
		}
		
		if( e.getSource() == rotateRight )
		{
			pane.rotateRight();
		}
		
		if( e.getSource() == editKey )
		{
			pane.editKey();
		}
		
		if( e.getSource() == editSpread )
		{
			pane.editSpread();
		}
		
		if( e.getSource() == editGrid )
		{
			pane.editGrid();
		}
		
		if( e.getSource() == cloneTenori )
		{
			pane.cloneTenori();
		}
		
		if( e.getSource() == insertNotes )
		{
			pane.insertNotes();
		}
		
		if( e.getSource() == printPitchList )
		{
			pane.printPitchList();
		}
		
		if( e.getSource() == autoSplit )
		{
			pane.autoSplit();
		}
		
		if( e.getSource() == saveTenori )
		{
			pane.saveTenori();
		}
		
		if( e.getSource() == loadTenori )
		{
			pane.loadTenori();
		}
		
		
	}

}

