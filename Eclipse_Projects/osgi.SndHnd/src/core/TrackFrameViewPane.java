




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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * View pane for manipulating the set of frames in an instrument track.
 * 
 * @author tgreen
 *
 */
public class TrackFrameViewPane extends JPanel implements ActionListener {
	
	/**
	 * The current set of tracks.
	 */
	private ArrayList<InstrumentTrack> tracks;
	
	/**
	 * The current set of frame numbers.
	 */
	private ArrayList<Integer> frameNumbers;
	
	/**
	 * The list of track frames.
	 */
	final JList<String> lst = new JList<String>();
	
	
	/**
	 * Button for deleting the selected track frame.
	 */
	JMenuItem delButton = new JMenuItem("Delete Selected Frame");
	
	
	/**
	 * Constructor.
	 */
	public TrackFrameViewPane()
	{
		refreshTrackFrames();
		JScrollPane scp = new JScrollPane( lst );
		setLayout( new BorderLayout( 0 , 0 ) );
		
		JMenuBar mbar = new JMenuBar();

		JMenu menu = new JMenu("Edit");
		menu.add(delButton);
		
		mbar.add(menu);
		
		add(BorderLayout.NORTH, mbar);
		add(BorderLayout.CENTER, scp );
		
		delButton.addActionListener(this);
		
		setMinimumSize( new Dimension( 400 , 200 ) );
		setPreferredSize( new Dimension( 400 , 200 ) );
	}

	
	/**
	 * Refreshes the list of track frames.
	 */
	public void refreshTrackFrames()
	{
		int frameNum = 0;
		int trackNum = 0;
		tracks = new ArrayList<InstrumentTrack>();
		frameNumbers = new ArrayList<Integer>();
		ArrayList<String> trackDescs = new ArrayList<String>();
		InstrumentTrack curTr = SongData.getCurrentTrack();
		
		for( final InstrumentTrack track : SongData.instrumentTracks ) {
			if (track != curTr) {
				for( final TrackFrame tf : track.getTrackFrames() ) {
					tracks.add( track );
					frameNumbers.add( frameNum );
					
					String str = "" + frameNum + "-- Track: " + trackNum + " ";
					str = str + track.getAgent().getHname() + " ";
					str = str + "[DarkCyan] ";
					
					trackDescs.add( str );
					frameNum++;
				}
			}
			else
			{
				int anum = 0;
				for( final TrackFrame tf : track.getTrackFrames() ) {
					tracks.add( track );
					frameNumbers.add( frameNum );
					
					String str = "" + frameNum + " -- Track: " + trackNum + " * ";
					str = str + track.getAgent().getHname() + " ";
					
					switch( anum )
					{
						case 0: {
							str = str + "[Green] ";
						}
						break;

						case 1: {
							str = str + "[Magenta] ";
						}
						break;

						case 2: {
							str = str + "[DarkYellow] ";
						}
						break;

						case 3: {
							str = str + "[Orange] ";
						}
						break;
						
						default:
							{ str = str + "[Green] "; }
						break;	
					}
					
					trackDescs.add( str );
					frameNum++;
					anum++;
				}
			}
			trackNum++;
		}
		
		lst.setListData(new Vector(trackDescs));
		lst.repaint();
	}

	
	/**
	 * Handles a button press by executing the selected action.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			
			if (e.getSource() == delButton) {
				int idx = lst.getSelectedIndex();
				if( idx >= 0 )
				{
					final InstrumentTrack track = tracks.get( idx );
					final int index = frameNumbers.get( idx );
					final ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
					trackFrames.remove( index );
					SongListeners.updateViewPanes();
				}
				else
				{
					Toolkit.getDefaultToolkit().beep();
				}
			}
			
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}

	
}

