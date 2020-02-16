




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


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import verdantium.Adapters;
import verdantium.EtherEventPropertySource;
import verdantium.core.DefaultPropertyEditor;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;


public class SndHndPropertyEditor extends DefaultPropertyEditor {
	
	private JTextField numMeasuresField;
	
	private JTextField measureListLengthField;
	
	private JRadioButton bytesPerSample3;
	
	private JRadioButton bytesPerSample4;
	
	private JCheckBox stereoOn;
	
	private JTextField quanNumBits;
	
	private JCheckBox calcGlobalOn;
	
	
	
	protected JCheckBox roughDraftOn;
	
	protected JTextField roughDraftWaveCoeff;
	
	protected JRadioButton roughBezAppxRoughDraftMode;
	
	protected JTextField roughNumWaves;
	
	protected JTextField roughSamplesPerWave;
	
	protected JCheckBox roughSkipVibratoForRoughDraft;
	
	protected JCheckBox roughSkipDistortionForRoughDraft;
	
	
	
	protected JComboBox<String> samplingRate;
	
	protected JComboBox<String> pseudonymicDivisor;
	
	protected JCheckBox reSampleOnBuild;
	
	protected JTextField displayFlatness;
	
	
	
	protected JList<String> alst;
	protected Vector<Class<? extends IntelligentAgent>> avct;
	
	
	
	
	private OutputChoiceInterface outputChoice;
	
	
	protected Runnable agentRunn = null;
	

	public SndHndPropertyEditor(EtherEventPropertySource arg0, Properties arg1, OutputChoiceInterface _outputChoice) {
		super(arg0, arg1);
		outputChoice = _outputChoice;
	}
	
	/**
	* Adds the tabs to the property editor needed to display Poseidon-specific properties.
	*/
	protected void addTabs(EtherEventPropertySource in, Properties inp) {
		target = (TestPlayer2) in;
		
		final int core = 0;
		numMeasuresField = new JTextField();
		measureListLengthField = new JTextField();
		
		bytesPerSample3 = new JRadioButton( "3 Bytes Per Sample" );
		bytesPerSample4 = new JRadioButton( "4 Bytes Per Sample" );
		stereoOn = new JCheckBox( "Stereo On" );
		roughDraftOn = new JCheckBox( "Rough Draft On" );
		roughSkipVibratoForRoughDraft = new JCheckBox( "Skip Vibrato For Rough Draft" );
		roughSkipDistortionForRoughDraft = new JCheckBox( "Skip Harmonic Distortion For Rough Draft" );
		roughSamplesPerWave = new JTextField( "xxx" );
		roughNumWaves = new JTextField( "xxx" );
		roughBezAppxRoughDraftMode = new JRadioButton( "Bezier Approximation Rough Draft Mode" );
		roughDraftWaveCoeff = new JTextField( "xxx" );
		calcGlobalOn = new JCheckBox( "Calc Global On" );
		displayFlatness = new JTextField( "" + ( SongData.display_flatness.getX() ) );
		
		JPanel p3 = new JPanel();
		TabPane.add("Measures", p3);
		p3.setLayout(new VerticalLayout(1));
		JLabel laba = new JLabel();
		String labas = "Last Note Beat -- " + 
			( SongData.getMeasureString( SongData.getLastActualNoteBeat() , core ) );
		laba.setText( labas );
		p3.add("any", laba );
		JLabel labb = new JLabel();
		String labbs = "Last Interp Beat -- " + 
			( SongData.getMeasureString( SongData.getLastInterpBeat(core) , core ) );
		labb.setText( labbs );
		p3.add("any", labb );
		p3.add("any", new JLabel("Num Measures: "));
		p3.add("any", numMeasuresField);
		p3.add("any", new JLabel("Measure List Length: "));
		p3.add("any", measureListLengthField);
		numMeasuresField.setText( "" + SongData.NUM_MEASURES );
		measureListLengthField.setText( "" + SongData.measuresStore.getBeatsPerMeasure().length );
		JButton MeasApplyButton = new JButton("Apply");
		p3.add("any", MeasApplyButton );
		
		JPanel p4 = new JPanel();
		TabPane.add("Sampling", p4);
		p4.setLayout(new VerticalLayout(1));
		p4.add("any", bytesPerSample3);
		p4.add("any", bytesPerSample4);
		ButtonGroup bg = new ButtonGroup();
		bg.add( bytesPerSample3 );
		bg.add( bytesPerSample4 );
		bytesPerSample3.setSelected( TestPlayer2.getBytesPerSample() == 3 );
		bytesPerSample4.setSelected( TestPlayer2.getBytesPerSample() == 4 );
		stereoOn.setSelected( SongData.STEREO_ON );
		p4.add("any", stereoOn );
		calcGlobalOn.setSelected( SongData.CALC_GLOBAL_MAX_VAL );
		p4.add("any", calcGlobalOn);
		quanNumBits = new JTextField();
		long quan = TestPlayer2.getQuantization();
		int quanBits = 0;
		while( quan > 0 )
		{
			quan = quan / 2;
			quanBits++;
		}
		quanNumBits.setText( "" + ( 32 - ( quanBits - 1 ) ) );
		p4.add( "any" , new JLabel( "Quantization (Number Of Bits) : " ) );
		p4.add( "any" , quanNumBits );
		JButton SmplApplyButton = new JButton("Apply");
		p4.add("any", SmplApplyButton );
		
		
		
		JPanel p6 = new JPanel();
		TabPane.add("Sampling Rate",p6);
		p6.setLayout(new VerticalLayout(1));
		
		samplingRate = new JComboBox<String>();
		samplingRate.setEditable(true);
		p6.add("any",new JLabel("Sampling Rate : "));
		p6.add("any",samplingRate);
		DefaultComboBoxModel<String> srmodel = new DefaultComboBoxModel<String>();
		samplingRate.setModel( srmodel );
		samplingRate.addItem("12000");
		samplingRate.addItem("24000");
		samplingRate.addItem("44100");
		samplingRate.addItem("48000");
		samplingRate.addItem("64000");
		samplingRate.addItem("65535");
		samplingRate.addItem("96000");
		samplingRate.addItem("192000");
		samplingRate.addItem("384000");
		srmodel.setSelectedItem("" + TestPlayer2.getSamplingRate() );
		
		pseudonymicDivisor = new JComboBox<String>();
		pseudonymicDivisor.setEditable(true);
		p6.add("any",new JLabel("Pseudonymic Divisor : "));
		p6.add("any",pseudonymicDivisor);
		DefaultComboBoxModel<String> psmodel = new DefaultComboBoxModel<String>();
		pseudonymicDivisor.setModel( psmodel );
		pseudonymicDivisor.addItem("1");
		pseudonymicDivisor.addItem("2");
		pseudonymicDivisor.addItem("4");
		pseudonymicDivisor.addItem("8");
		pseudonymicDivisor.addItem("16");
		psmodel.setSelectedItem("" + TestPlayer2.getPseudonymicDivisor() );
		
		reSampleOnBuild = new JCheckBox( "Re-Sample On Build" );
		reSampleOnBuild.setSelected( SongData.reSampleOnBuild );
		p6.add("any",reSampleOnBuild);
		final JPanel p6a = new JPanel();
		p6a.setLayout( new FlowLayout()  );
		JButton DisplayHardwareControlButton = new JButton( "Hardware..." );
		JButton PlaybackStdButton = new JButton( "Playback Std" );
		JButton RateApplyButton = new JButton( "Apply" );
		p6a.add( DisplayHardwareControlButton );
		p6a.add( PlaybackStdButton );
		p6a.add( RateApplyButton );
		p6.add( "any" , p6a );
		
		
		JPanel pan5 = new JPanel();
		TabPane.add("Rough Draft", pan5);
		pan5.setLayout(new VerticalLayout(1));
		bg = new ButtonGroup();
		bg.add( roughBezAppxRoughDraftMode );
		roughDraftOn.setSelected( SongData.ROUGH_DRAFT_MODE );
		roughBezAppxRoughDraftMode.setSelected(true);
		roughDraftWaveCoeff.setText( "" + SongData.roughDraftWaveCoeff );
		roughNumWaves.setText( "" + SongData.getRoughDraftBezNumWaves() );
		roughSamplesPerWave.setText( "" + SongData.getRoughDraftBezSamplesPerWave() );
		roughSkipVibratoForRoughDraft.setSelected( SongData.skipVibratoForRoughDraft );
		roughSkipDistortionForRoughDraft.setSelected( SongData.skipDistortionForRoughDraft );
		pan5.add( "any" , roughDraftOn );
		pan5.add( "any" , new JLabel( "Rough Draft Wave Coeff (1.0=FullUpNoApprox) : " ) );
		pan5.add( "any" , roughDraftWaveCoeff );
		pan5.add( "any" , roughBezAppxRoughDraftMode );
		pan5.add("any", new JLabel("Bezier Rough Draft Number Of Waves : ") );
		pan5.add("any", roughNumWaves );
		pan5.add("any", new JLabel("Bezier Rough Draft Number Of Samples Per Wave : ") );
		pan5.add("any", roughSamplesPerWave );
		pan5.add("any", roughSkipVibratoForRoughDraft);
		pan5.add("any", roughSkipDistortionForRoughDraft);
		JButton RoughApplyButton = new JButton("Apply");
		pan5.add("any", RoughApplyButton );
		
		
		JPanel pan7 = new JPanel();
		TabPane.add("Display", pan7);
		pan7.setLayout(new VerticalLayout(1));
		pan7.add( "any" , new JLabel( "Display Flatness : " ) );
		pan7.add( "any" , displayFlatness );
		JButton DisplayApplyButton = new JButton("Apply");
		pan7.add("any", DisplayApplyButton );
		
		
		JPanel pan6 = new JPanel();
		TabPane.add("Agent", pan6);
		pan6.setLayout(new VerticalLayout(1));
		alst = new JList<String>();
		JScrollPane ascp = new JScrollPane( alst );
		pan6.add("any", ascp );
		JButton AgentApplyButton = new JButton("Apply");
		pan6.add("any", AgentApplyButton );
		buildAgentListModel( alst );
		
		agentRunn = new Runnable()
		{
			public void run()
			{
				SwingUtilities.invokeLater( new Runnable()
				{
					public void run()
					{
						buildAgentListModel(alst);
					}
				} );
			}
		};
		
		AgentManager.addListener( agentRunn );
		
		
		super.addTabs(in, inp);
		
		ActionListener ButtonL = Adapters.createGActionListener(this, "handleSetMeasureApplyButton");
		MeasApplyButton.addActionListener(ButtonL);
		
		ButtonL = Adapters.createGActionListener(this, "handleSetSmplApplyButton");
		SmplApplyButton.addActionListener(ButtonL);
		
		ButtonL = Adapters.createGActionListener(this, "handleSetRoughApplyButton");
		RoughApplyButton.addActionListener(ButtonL);
		
		ButtonL = Adapters.createGActionListener(this, "handleSetRateApplyButton");
		RateApplyButton.addActionListener(ButtonL);
		
		ButtonL = Adapters.createGActionListener(this, "handleSetAgentApplyButton");
		AgentApplyButton.addActionListener(ButtonL);
		
		ButtonL = Adapters.createGActionListener(this, "handleSetDisplayButton");
		DisplayApplyButton.addActionListener(ButtonL);
		
		ButtonL = Adapters.createGActionListener(this, "handleDisplayHardwareControl");
		DisplayHardwareControlButton.addActionListener(ButtonL);
		
		ButtonL = Adapters.createGActionListener(this, "handlePlaybackStd");
		PlaybackStdButton.addActionListener(ButtonL);
		
	}
	
	
	/**
	* Handles the destruction of the component.
	*/
	public void handleDestroy() {
		AgentManager.removeListener( agentRunn );
		super.handleDestroy();
	}
	
	
	protected void handleAgentSet( Class<? extends IntelligentAgent> clss ) throws Throwable
	{
		    final int core = 0;
			InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
			IntelligentAgent agent = clss.newInstance();
			track.setAgent( agent );
			track.getAgent().setHname( clss.getName() );
			track.updateTrackFrames( core );
			SongListeners.updateViewPanes();
			outputChoice.refreshList();
	}
	
	
	public void handleSetAgentApplyButton( ActionEvent e )
	{
		try
		{
			int option = JOptionPane.showConfirmDialog(getGUI(), "Change agent (deletes memory)?");
			if( option != JOptionPane.YES_OPTION )
			{
				return;
			}
			int idx = alst.getSelectedIndex();
			if( idx >= 0 )
			{
				Class<? extends IntelligentAgent> clss = avct.get( idx );
				handleAgentSet( clss );
			}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	protected void buildAgentListModel( JList<String> alst )
	{
		DefaultListModel<String> lm = new DefaultListModel<String>();
		avct = new Vector<Class<? extends IntelligentAgent>>();
		
		Map<String,Class<? extends IntelligentAgent>> hm = AgentManager.getMapClone();
		
		for( Entry<String,Class<? extends IntelligentAgent>> e : hm.entrySet() )
		{
			String st = e.getKey();
			Class<? extends IntelligentAgent> clss = e.getValue();
			lm.addElement( st );
			avct.addElement( clss );
		}
		
		alst.setModel( lm );
	}
	
	
	public void handleSetMeasureApplyButton( ActionEvent al )
	{
		try
		{
			final int core = 0;
			SongData.NUM_MEASURES = Integer.parseInt( numMeasuresField.getText() );
			int measureListLength = Integer.parseInt( measureListLengthField.getText() );
			if( measureListLength < 1 )
			{
				throw( new RuntimeException( "Bad" ) );
			}
			int[] mi = SongData.measuresStore.getBeatsPerMeasure();
			int[] mm = new int[ measureListLength ];
			int count;
			for( count = 0 ; count < mm.length ; count++ )
			{
				int ind = Math.min( count , mi.length - 1 );
				mm[ count ] = mi[ ind ];
			}
			SongData.measuresStore.setBeatsPerMeasure( mm );
			int maxBeatNumber = SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES );
			SongData.updateMaxBeat( maxBeatNumber , core );
			SongListeners.updateViewPanes();
		}
		catch (NumberFormatException ex) {
			handleThrow(new IllegalInputException("Something input was not a number.", ex));
		}
		catch (Throwable ex) {
			handleThrow(ex);
		}
	}
	
	public void handleSetSmplApplyButton( ActionEvent al )
	{
		try
		{
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			int bytesPer = 3;
			if( bytesPerSample4.isSelected() )
			{
				bytesPer = 4;
			}
			TestPlayer2.setBytesPerSample( bytesPer );
			TestPlayer2.setStereoOn( stereoOn.isSelected() );
			SongData.CALC_GLOBAL_MAX_VAL = calcGlobalOn.isSelected();
			
			int dint = 32 - Integer.parseInt( quanNumBits.getText() );
			long val = ( (long) 1 ) << ( (long) dint );
			System.out.println( "Quan : " + val );
			TestPlayer2.setQuantization( val );
		}
		catch (NumberFormatException ex) {
			handleThrow(new IllegalInputException("Something input was not a number.", ex));
		}
		catch (Throwable ex) {
			handleThrow(ex);
		}
	}
	
	
	public void handleSetRateApplyButton( ActionEvent al )
	{
		try
		{
			String srateitem = "" + samplingRate.getSelectedItem();
			String pseudoitem = "" + pseudonymicDivisor.getSelectedItem();
			int srate = Integer.parseInt( srateitem );
			int pseudo = Integer.parseInt( pseudoitem );
			
			if( pseudo < 1 )
					throw( new RuntimeException( "Bad Selection" ) );
			if( ( srate % pseudo ) != 0 )
				throw( new RuntimeException( "Bad Selection" ) );
			if( ( srate / pseudo ) > TestPlayer2.SMPL_MAX_SAFE )
				throw( new RuntimeException( "Bad Selection" ) );
			if( ( pseudo != 1 ) && ( srate <= TestPlayer2.SMPL_MAX_SAFE ) )
				throw( new RuntimeException( "Bad Selection" ) );
			
			TestPlayer2.setSamplingRate( srate );
			TestPlayer2.setPseudonymicDivisor( pseudo );
			SongData.reSampleOnBuild = reSampleOnBuild.isSelected();
		}
		catch (NumberFormatException ex) {
			handleThrow(new IllegalInputException("Something input was not a number.", ex));
		}
		catch (Throwable ex) {
			handleThrow(ex);
		}
	}
	
	
	public void handleSetRoughApplyButton( ActionEvent al )
	{
		try {
			final int core = 0;
			
			SongData.ROUGH_DRAFT_MODE = roughDraftOn.isSelected();
			
			int mode = SongData.ROUGH_DRAFT_MODE_BEZ_APPROX;
		
			
			int nw = Integer.parseInt( roughNumWaves.getText() );
			double spw = Double.parseDouble( roughSamplesPerWave.getText() );
			SongData.roughDraftMode = mode;
			SongData.skipVibratoForRoughDraft = roughSkipVibratoForRoughDraft.isSelected();
			SongData.skipDistortionForRoughDraft = roughSkipDistortionForRoughDraft.isSelected();
			SongData.setApproxParams( nw , spw , core );
			
			SongData.roughDraftWaveCoeff = Double.parseDouble( roughDraftWaveCoeff.getText() );

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
	
	
	public void handleSetDisplayButton( ActionEvent al )
	{
		try {
			double spw = Double.parseDouble( displayFlatness.getText() );
			SongData.display_flatness.setCoords( spw );
		} catch (NumberFormatException ex) {
			handleThrow(new IllegalInputException(
					"Something input was not a number.", ex));
		} catch (Throwable ex) {
			handleThrow(ex);
		}
	}
	
	
	public void handleDisplayHardwareControl( ActionEvent al )
	{
		try {
			SoundControlPanel.showControlPanel();
		}  catch (Throwable ex) {
			handleThrow(ex);
		}
	}
	
	
	
	public void handlePlaybackStd( ActionEvent al )
	{
		try {
			calcGlobalOn.setSelected( true );
			roughDraftOn.setSelected( false );
			samplingRate.setSelectedItem( "96000" );
			this.handleSetRoughApplyButton( null );
			this.handleSetMeasureApplyButton( null );
			this.handleSetSmplApplyButton( null );
		}  catch (Throwable ex) {
			handleThrow(ex);
		}
	}
	

	
}




