




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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.*;
import java.util.*;

import meta.WrapRuntimeException;
import verdantium.EtherEvent;
import verdantium.EtherEventHandler;
import verdantium.ProgramDirector;
import verdantium.StandardEtherEvent;
import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;

/**
 * A property editor for setting the starting and ending beat numbers of a song.
 * <P>
 * 
 * @author Thorn Green
 */
public class StartBeatEditor extends PropertyEditAdapter implements
		ActionListener {

	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * The beat number of the starting beat of the song.  Beat numbers start at zero.
	 */
	protected JTextField startBeat = new JTextField( "xxx" );
	
	/**
	 * The beat number of the ending beat of the song.  Beat numbers start at zero.
	 */
	protected JTextField endBeat = new JTextField( "xxx" );
	
	/**
	 * The Apply button.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	/**
	 * Button for setting the start beat number of the song to the starting beat of the first measure.
	 */
	protected JButton startMeasuresButton = new JButton( "Start Measures" );
	
	/**
	 * Button for setting the end beat number of the song to the ending beat of the last measure.
	 */
	protected JButton endMeasuresButton = new JButton( "End Measures" );
	
	/**
	 * Button for setting the start beat number of the song to the starting beat of the first note.
	 */
	protected JButton startNotesButton = new JButton( "Start Notes" );
	
	/**
	 * Button for setting the end beat number of the song to the ending beat of the last note.
	 */
	protected JButton endNotesButton = new JButton( "End Notes" );
	
	/**
	 * The interface to be updated upon a change.
	 */
	OutputChoiceInterface outputChoice;
	
	

	/**
	 * Constructor.
	 * @param _outputChoice The interface to be updated upon a change.
	 */
	public StartBeatEditor( OutputChoiceInterface _outputChoice ) {
		outputChoice = _outputChoice;
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		startBeat.setText( "" + SongData.START_BEAT );
		endBeat.setText( "" + SongData.END_BEAT );

		pan2.add("any", new JLabel("Start Beat : ") );
		pan2.add("any", startBeat );
		pan2.add("any", startMeasuresButton);
		pan2.add("any", startNotesButton);
		pan2.add("any", new JLabel("End Beat : ") );
		pan2.add("any", endBeat );
		pan2.add("any", endMeasuresButton);
		pan2.add("any", endNotesButton);
		pan2.add("any", applyButton);

		startMeasuresButton.addActionListener(this);
		startNotesButton.addActionListener(this);
		endMeasuresButton.addActionListener(this);
		endNotesButton.addActionListener(this);
		applyButton.addActionListener(this);
	}

	/**
	 * Gets the GUI of the property editor.
	 */
	public JComponent getGUI() {
		return (MyPan);
	}

	/**
	 * Handles the destruction of the component by removing appropriate change
	 * listeners.
	 */
	public void handleDestroy() {
	}
	
	
	

	
	/**
	 * Handles a button-press event from the Apply button applying the currently selected start/end beats.
	 * 
	 * Handles a button press from a button other than the Apply button by changing the current start/end beat selections. 
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if( src == startMeasuresButton )
		{
			startBeat.setText( "0" );
			startBeat.repaint();
		}
		
		if( src == endMeasuresButton )
		{
			endBeat.setText( "" + ( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) ) );
			endBeat.repaint();
		}
		
		if( src == startNotesButton )
		{
			double beatNum = 1E+36;
			for( final InstrumentTrack i : SongData.instrumentTracks )
			{
				for( final TrackFrame t : i.getTrackFrames() )
				{
					for( final NoteDesc n : t.getNotes() )
					{
						beatNum = Math.min( beatNum ,  n.getActualStartBeatNumber() );
					}
				}
			}
			int sb = (int) beatNum;
			if( sb < 0 ) sb = 0;
			startBeat.setText( "" + sb );
			startBeat.repaint();
		}
		
		if( src == endNotesButton )
		{
			double beatNum = 0.0;
			for( final InstrumentTrack i : SongData.instrumentTracks )
			{
				for( final TrackFrame t : i.getTrackFrames() )
				{
					for( final NoteDesc n : t.getNotes() )
					{
						beatNum = Math.max( beatNum ,  n.getActualEndBeatNumber() );
					}
				}
			}
			endBeat.setText( "" + ( (int)( beatNum ) + 1 ) );
			endBeat.repaint();
		}

		if (src == applyButton) {
			try {
				
				
				
				double stBeat = Double.parseDouble( startBeat.getText() );
				double edBeat = Double.parseDouble( endBeat.getText() );
				SongData.START_BEAT = stBeat;
				SongData.END_BEAT = edBeat;
				SongListeners.updateViewPanes();

				// EtherEvent send =
				// new PropertyEditEtherEvent(
				// this,
				// PropertyEditEtherEvent.setPageSize,
				// null,
				// MyPage);
				// send.setParameter(new Dimension((int) wid, (int) hei));
				// ProgramDirector.fireEtherEvent(send, null);
			} catch (NumberFormatException ex) {
				handleThrow(new IllegalInputException(
						"Something input was not a number.", ex));
			} catch (Throwable ex) {
				handleThrow(ex);
			}
		}

	}

	/**
	 * Handles the throwing of an error or exception.
	 */
	public void handleThrow(Throwable in) {
		VerdantiumUtils.handleThrow(in, this, null);
	}

	
}

