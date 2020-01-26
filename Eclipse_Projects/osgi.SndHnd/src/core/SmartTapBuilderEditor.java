




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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;


/**
 * Property editor for converting taps from the tap pad into a motif, and then inserting the motif into the song as musical notes 
 * within the context of some user-selected parameters.
 * 
 * Includes fields for adding statistical variation to model human error in timing and pitch.
 * 
 * @author tgreen
 *
 */
public class SmartTapBuilderEditor extends PropertyEditAdapter implements
	ActionListener {
	
	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * The interface to be updated upon a change.
	 */
	protected OutputChoiceInterface tvp = null;
	
	/**
	 * The maximum number of taps that can appear in a motif.
	 */
	protected static final int MAX_TAPS = 7;
	
	
	/**
	 * Text field for editing the length of the motif to insert for each round.
	 */
	protected JTextField motifLengthField = new JTextField( "4" );
	
	
	/**
	 * Text fields for editing the melodic interval number with respect to the current intonation for the pitches at which to insert the notes.  Melodic interval numbers start at zero.
	 */
	protected JTextField[] melodicIntervalFields = new JTextField[ MAX_TAPS ];
	
	/**
	 * Combo boxes for editing the step on the current intonation scale for the pitches at which to insert the notes.
	 */
	protected JComboBox[] noteComboBoxes = new JComboBox[ MAX_TAPS ];
	
	/**
	 * Check box for editing whether to have statistical variation in pitch.
	 */
	protected JCheckBox spreadPitchOn = new JCheckBox();
	
	/**
	 * Text field for editing the size of the random variation of the pitch (in hertz).
	 */
	protected JTextField spreadPitchSize = new JTextField( "10.0" );
	
	/**
	 * Text field for editing the random number seed with respect to pitch.
	 */
	protected JTextField spreadPitchRandseed = new JTextField( "1554" );
	
	/**
	 * Check box for editing whether to have statistical variation in time.
	 */
	protected JCheckBox spreadTimeOn = new JCheckBox();
	
	/**
	 * Text field for editing the size of the random variation of the time (in seconds).
	 */
	protected JTextField spreadTimeSize = new JTextField( "0.0085" );
	
	/**
	 * Text field for editing the random number seed with respect to time.
	 */
	protected JTextField spreadTimeRandseed = new JTextField( "1553" );
	
	
	/**
	 * Text field for editing the number of rounds of musical notes to insert.
	 */
	protected JTextField numRoundsField = new JTextField( "20" );
	
	
	/**
	 * Button to get the current set of time intervals from the tap pad.
	 */
	protected JButton sampleButton = new JButton( "Sample" );
	
	
	/**
	 * Button to generate the notes and insert them into the song.
	 */
	protected JButton generateButton = new JButton( "Generate" );
	
	
	/**
	 * The time intervals sampled from the tap pad.
	 */
	protected ArrayList<TimeInterval> timeIntervals = new ArrayList<TimeInterval>();
	
	
	
	/**
	 * Constructor.
	 * @param _tvp The interface to be updated upon a change.
	 */
	public SmartTapBuilderEditor( OutputChoiceInterface _tvp ) {
		super();
		tvp = _tvp;
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, generateButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
				
		pan2.add( "any" , sampleButton );
		
		
		
		
		pan2.add("any", new JLabel( "Motif Length:" ));
		pan2.add("any", motifLengthField );
		
		
		
		
		int cnt;
		for( cnt = 0 ; cnt < MAX_TAPS ; cnt++ )
		{
			melodicIntervalFields[ cnt ] = new JTextField( "3" );
			noteComboBoxes[ cnt ] = new JComboBox();
			
		}
		
		
		
		final String[] keys = NoteTable.getScaleNamesDefaultScale_Key();
		for( cnt = 0 ; cnt < keys.length ; cnt++ )
		{
			int cnt2;
			for( cnt2 = 0 ; cnt2 < MAX_TAPS ; cnt2++ )
			{
				noteComboBoxes[ cnt2 ].addItem( keys[ cnt ] );
				
			}
		}
		
		
		
		for( cnt = 0 ; cnt < MAX_TAPS ; cnt++ )
		{
			pan2.add("any", melodicIntervalFields[ cnt ] );
			pan2.add("any", noteComboBoxes[ cnt ] );
		}
		
		
		
		
		pan2.add("any", new JLabel( "Spread Pitch On:" ));
		pan2.add("any", spreadPitchOn);
		
		pan2.add("any", new JLabel("Spread Pitch Size (Cents) : "));
		pan2.add("any", spreadPitchSize);
		
		pan2.add("any", new JLabel( "Spread Pitch Randseed:" ));
		pan2.add("any", spreadPitchRandseed);
		
		pan2.add("any", new JLabel( "Spread Time On:" ));
		pan2.add("any", spreadTimeOn);
		
		pan2.add("any", new JLabel("Spread Time Size (Seconds) : "));
		pan2.add("any", spreadTimeSize);
		
		pan2.add("any", new JLabel( "Spread Time Randseed:" ));
		pan2.add("any", spreadTimeRandseed);
		
		
		pan2.add("any", new JLabel( "Num Rounds:" ));
		pan2.add("any", numRoundsField );
		
		
		pan2.add( "any" , generateButton );

		

		sampleButton.addActionListener(this);
		generateButton.addActionListener(this);
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
	 * Generates musical notes and inserts them into the song.
	 * @throws Throwable
	 */
	protected void handleGenerate() throws Throwable
	{
		final int core = 0;
		TrackFrame tr = new TrackFrame();
		InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
		IntelligentAgent agent = track.getAgent();
		ArrayList<NoteDesc> tvect = new ArrayList<NoteDesc>();
		
		
		int motifLength = Integer.parseInt( motifLengthField.getText() );
				
		
		double[] tapLength = new double[ motifLength ];
		double[] tapDelta = new double[ motifLength ];
		int[] lenCnt = new int[ motifLength ];
		int[] deltaCnt = new int[ motifLength ];
		int cnt = 0;
		double prevBeat = -209.0;
		double stBeat = -209.0;


		for( TimeInterval ti : timeIntervals ) {
			int sample_start_time_seconds = SongData.getStartTimeSeconds( core );
			double startBeat = SongData.getBeatNumber( ti.getStartTimeSeconds() + sample_start_time_seconds , core );
			double endBeat = SongData.getBeatNumber( ti.getEndTimeSeconds() + sample_start_time_seconds , core );
			
			int mnum = cnt % motifLength;
			
			
			tapLength[ mnum ] += endBeat - startBeat;
			
			if( cnt > 0 )
			{
				tapDelta[ mnum ] += startBeat - prevBeat;
				deltaCnt[ mnum ]++;
			}
			else
			{
				stBeat = startBeat;
			}
			
			
			prevBeat = startBeat;
			lenCnt[ mnum ]++;
			cnt++;
		}

		
		for( cnt = 0 ; cnt < motifLength ; cnt++ )
		{
			tapDelta[ cnt ] = tapDelta[ cnt ] / deltaCnt[ cnt ];
			tapLength[ cnt ] = tapLength[ cnt ] / lenCnt[ cnt ];
		}
		
		
		double beat = stBeat;
		final int numRounds = Integer.parseInt( numRoundsField.getText() );
		
		
		final int spreadPitchRandseed = Integer.parseInt( this.spreadPitchRandseed.getText() );
		final int spreadTimeRandseed = Integer.parseInt( this.spreadTimeRandseed.getText() );
		final boolean spreadPitchOn = this.spreadPitchOn.isSelected();
		final boolean spreadTimeOn = this.spreadTimeOn.isSelected();
		final double spreadTimeSize = Double.parseDouble( this.spreadTimeSize.getText() );
		final double spreadPitchSize = Double.parseDouble( this.spreadPitchSize.getText() );
		
		
		
		Random randFreq =  null;
		if( spreadPitchOn )
		{
			randFreq = new Random( spreadPitchRandseed );
		}
		
		Random randTime = null;
		if( spreadTimeOn )
		{
			randTime = new Random( spreadTimeRandseed );
		}
		
		
		for( cnt = 0 ; cnt < numRounds ; cnt++ )
		{
			int cnt2 = 0;
			for( cnt2 = 0 ; cnt2 < motifLength ; cnt2++ )
			{
				double startBeat = beat + tapDelta[ cnt2 ];
				double endBeat = startBeat + tapLength[ cnt2 ];
				beat = startBeat;
				
				
				if( spreadTimeOn )
				{
					double startTime = SongData.getElapsedTimeForBeatBeat( startBeat, core);
					double endTime = SongData.getElapsedTimeForBeatBeat( endBeat, core);
					
					startTime += spreadTimeSize * ( randTime.nextGaussian() );
					endTime += spreadTimeSize * ( randTime.nextGaussian() );
					
					startBeat = SongData.getBeatNumber( startTime , core);
					endBeat = SongData.getBeatNumber( endTime , core);
					
					if( endBeat < startBeat )
					{
						endBeat = startBeat;
					}
				}
				
				double freq = 0.0;
				{
					final JComboBox noteCombo = noteComboBoxes[ cnt2 ];
					final JTextField melodicInterval = melodicIntervalFields[ cnt2 ];
					final int melodicIntervalNumber = Integer.parseInt( melodicInterval.getText() );
					final int numSteps = noteCombo.getSelectedIndex();
					
					freq = NoteTable.getNoteFrequencyDefaultScale_Key( melodicIntervalNumber , numSteps );
				}
				
				if( spreadPitchOn )
				{
					double fa = ( Math.log( freq ) / Math.log( 2.0 ) ) * ( 12.0 * 100.0 );
					fa += spreadPitchSize * ( randFreq.nextGaussian() );
					double fb = Math.pow( 2.0 , fa / ( 12.0 * 100.0 ) );
					freq = fb;
				}
				
				NoteDesc nd = SongData.buildNoteDesc( agent , core ,
						startBeat , endBeat , freq );
				// nd.setUserDefinedEnd( userDefinedEnd ); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				tvect.add(nd);
				tr.getNotes().add(nd);
			}
		}
		
		
		track.getTrackFrames().add(tr);
		track.updateTrackFrames(core);
		SongListeners.updateViewPanes();	
	}
	
	
	
	
	/**
	 * Handles retrieving the time intervals of the taps from the tap pad.
	 */
	protected void handleSample()
	{
		ArrayList<TimeInterval> iTimeIntervals = tvp.getTapPad().getTimeIntervalArrayList();
		timeIntervals = new ArrayList<TimeInterval>();
		for( TimeInterval t : iTimeIntervals )
		{
			timeIntervals.add( t );
		}
	}

	
	/**
	 * Handles the pressing of the "Generate" button by generating musicla notes and inserting them into the song.
	 * Handles the pressing of the "Sample" button by retrieving the time intervals of the taps from the tap pad.
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == generateButton) {
			try {
				
				
				handleGenerate();

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
		
		
		
		if (src == sampleButton) {
			try {
				
				
				handleSample();

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

