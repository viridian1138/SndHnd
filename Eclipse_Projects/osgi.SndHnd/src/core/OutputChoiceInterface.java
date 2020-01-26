




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

import gredit.GWaveForm;
import grview.DraggableTransformNodeTest;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import palettes.PaletteClassesCoeff;
import palettes.PaletteClassesDump;
import palettes.PaletteClassesIntonation;
import palettes.PaletteClassesWave;
import verdantium.ProgramDirector;
import verdantium.VerdantiumPropertiesEditor;
import verdantium.undo.UTag;
import verdantium.undo.UndoManager;
import aazon.builderNode.AazonTransChld;
import aazon.builderNode.BuilderNode;
import aczon.AczonUnivAllocator;

public class OutputChoiceInterface implements ActionListener {

	JPanel pan = new JPanel();

	JList lst = new JList();

	JMenuItem playButton = new JMenuItem("Play");

	JMenuItem wavButton = new JMenuItem("Export to WAV");
	
	JMenuItem cpanButton = new JMenuItem("Show Control Panel");

	JMenuItem tapButton = new JMenuItem("Show Tap Pad");

	JMenuItem clearTapButton = new JMenuItem("Clear Tap Pad");

	JMenuItem buildButton = new JMenuItem("Build Instrument Frame from Tap Pad");
	
	JMenuItem smartBuildButton = new JMenuItem("Smart-Build Instrument Frame from Tap Pad");
	
	JMenuItem replicateAcrossButton = new JMenuItem("Replicate Across Taps x50");

	JMenuItem buildVoiceButton = new JMenuItem(
			"Build Instrument Frame from Tap + Voice Mark");

	JMenuItem buildVoiceBendButton = new JMenuItem(
			"Build Instrument Frame from Tap + Voice Bend");

	JMenuItem buildDigiButton = new JMenuItem("Build Track From Digitizer");
	
	JMenuItem determineKey = new JMenuItem( "Determine Key" );
	
	JMenuItem confirmKey = new JMenuItem( "Confirm Key" );
	
	JMenuItem insertInitWAV = new JMenuItem( "Insert Init WAV..." );

	JMenuItem digitizeButton = new JMenuItem("Digitize Sound");

	JMenuItem viewButton = new JMenuItem("View Notes...");
	
	JMenuItem viewWavesButton = new JMenuItem("View Waves...");
	
	JMenuItem viewVolumeButton = new JMenuItem("View Volumes...");
	
	JMenuItem viewTempoButton = new JMenuItem( "View Tempo..." );
	
	JMenuItem viewMeasuresButton = new JMenuItem( "View Measures..." );
	
	JMenuItem viewTrackFramesButton = new JMenuItem( "View Track Frames..." );
	
	JMenuItem viewVibratoRateButton = new JMenuItem( "View Vibrato Rate..." );
	
	JMenuItem buildLoopButton = new JMenuItem( "Build Pseudo-Loop" );
	
//	JMenuItem broadcastSlimButton = new JMenuItem("Broadcast To SlimServer");
	
	JMenuItem estimateTimeCompleteButton = new JMenuItem("Estimate Time To Complete");
	
	JMenuItem estimateCoreCapabilityButton = new JMenuItem("Estimate Core Capabiliy");
	
	JMenuItem printTimeForTap = new JMenuItem("Print Time For Tap");

	JMenuItem quitButton = new JMenuItem("Quit");

	JMenuItem metronomeOn = new JMenuItem("Metronome On");

	JMenuItem metronomeOff = new JMenuItem("Metronome Off");
	
	JMenuItem editSampling = new JMenuItem("Edit Sampling...");
	
	JMenuItem editIntonation = new JMenuItem("Edit Intonation...");
	
	JMenuItem editTenoriTone = new JMenuItem("Edit Tenori Tone...");

	JMenuItem setStartBeat = new JMenuItem("Set Start / End Beats...");
	
	JMenuItem setStartBeatPlayhead = new JMenuItem("Set Start Beat To Playhead");
	
	JMenuItem setEndBeatPlayhead = new JMenuItem("Set End Beat To Playhead");

	JMenuItem addTrack = new JMenuItem("Add Track");

	JMenuItem incrementTrack = new JMenuItem("Increment Track");

	JMenuItem trackOn = new JMenuItem("Track On");

	JMenuItem trackOff = new JMenuItem("Track Off");

	JMenuItem deleteTrack = new JMenuItem("Delete Track");
	
	JMenuItem deleteAllFramesInTrack = new JMenuItem("Delete All Frames In Track");

	JMenuItem setVoiceMarkTrack = new JMenuItem("Set Voice Mark Track");
	
	JMenuItem showTenori = new JMenuItem("Show Tenori...");
	
	JMenuItem showTrebleClef = new JMenuItem("Show Treble Clef...");
	
	JMenuItem showBassClef = new JMenuItem("Show Bass Clef...");
	
	JMenuItem showVisualMetronome = new JMenuItem("Show Visual Metronome...");
	
	JMenuItem showElapsedTimeView = new JMenuItem("Show Elapsed Time View...");

	

	
	
	JMenuItem prepMastering = new JMenuItem(
			"Prepare For Mastering");
	
	JMenuItem editAgentName = new JMenuItem( "Edit Agent Name..." );
	
	JMenuItem editAgent = new JMenuItem( "Edit Agent Properties..." );
	
	JMenuItem editAgentVibrato = new JMenuItem( "Edit Agent Vibrato Properties..." );
	
	JMenuItem pitchBendAnalyze = new JMenuItem( "Pitch-Bend Analyze" );
	
	JMenuItem envelopeAnalyze = new JMenuItem( "Envelope Analyze" );
	
	JMenuItem waveDiagramAnalyze = new JMenuItem( "Wave Diagram Analyze" );
	
	JMenuItem captureAgentToPseudoLoop = new JMenuItem("Capture Agent To Pseudo Loop");
	
	JMenuItem capturePseudoLoopToAgent  = new JMenuItem("Capture Pseudo Loop To Agent");
	
	JMenuItem captureTrackToPseudoLoop  = new JMenuItem("Capture Track To Pseudo Loop");
	
	JMenuItem printPseudoLoopList = new JMenuItem("Print Pseudo Loop List");
	
	JMenuItem revertAgentDefaultLoop = new JMenuItem( "Revert Agent To Default Loop" );
	
	JMenuItem dumpWaveform = new JMenuItem( "Dump Waveform" );
	
	JMenuItem editPack1 = new JMenuItem( "Edit Pack1" );
	
	JMenuItem editPack2 = new JMenuItem( "Edit Pack2" );
	
	JMenuItem editPack3 = new JMenuItem( "Edit Pack3" );

	JMenuItem buildOctaveM2 = new JMenuItem("Octave -2");

	JMenuItem buildOctaveM1 = new JMenuItem("Octave -1");

	JMenuItem buildOctaveP0 = new JMenuItem("Octave +0");

	JMenuItem buildOctaveP1 = new JMenuItem("Octave +1");

	JMenuItem buildOctaveP2 = new JMenuItem("Octave +2");
	
	JMenuItem printAutocorFrequencies = new JMenuItem("Print Autocor Frequencies");
	
	JMenuItem pasteToLoTenor = new JMenuItem("Paste To Low Tenor");
	
	JMenuItem pasteToHiTenor = new JMenuItem("Paste To High Tenor");
	
	JMenuItem pasteToLoWideband = new JMenuItem("Paste To Low Wideband");
	
	JMenuItem pasteToHiWideband = new JMenuItem("Paste To High Wideband");
	
	JMenuItem trackMainButton = new JMenuItem("Build Main Round");
	
	JMenuItem trackHarmonyButton = new JMenuItem("Build Harmony Round");
	
	JMenuItem printResults = new JMenuItem( "Print Results" );

	File soundFile = null;

	protected static TapPad tapPad = null;
	
	protected static VisualMetronome visualMetronome = null;
	
	protected static ElapsedTimeView elapsedTimeView = null;

	SoundDigitizer digitizer = null;

	Frame movieControlFrame = null;

	SoundPlayer _soundPlayer = null;
	
	UndoManager undoMgr;

	double octaveMultiplier = 1.0;

	public JComponent getGUI() {
		return (pan);
	}

	public OutputChoiceInterface(File _soundFile, UndoManager _undoMgr) {
		super();
		soundFile = _soundFile;
		try
		{
			_soundPlayer = new SoundPlayer( soundFile );
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
		undoMgr = _undoMgr;
		pan.setLayout(new BorderLayout(0, 0));

		JMenuBar mbar = new JMenuBar();

		JMenu menu = new JMenu("Audio");
		menu.add(playButton);
		menu.add(wavButton);
		menu.add(cpanButton);
		menu.add(tapButton);
		menu.add(clearTapButton);
		menu.add(buildButton);
		menu.add(smartBuildButton);
		menu.add(replicateAcrossButton);
		menu.add(buildVoiceButton);
		menu.add(buildVoiceBendButton);
		menu.add(buildDigiButton);
		menu.add(digitizeButton);
		menu.add(viewButton);
		menu.add(viewVolumeButton);
		menu.add(viewTempoButton);
		menu.add(viewMeasuresButton);
		menu.add(viewTrackFramesButton);
		menu.add(viewVibratoRateButton);
		menu.add(buildLoopButton);
//		menu.add(broadcastSlimButton);
		menu.add(estimateTimeCompleteButton);
		menu.add(estimateCoreCapabilityButton);
		menu.add(printTimeForTap);
		menu.add(quitButton);
		
		mbar.add(menu);

		menu = new JMenu("Init");
		menu.add(determineKey);
		menu.add(confirmKey);
		menu.add(insertInitWAV);

		mbar.add(menu);

		menu = new JMenu("Settings");
		menu.add(showVisualMetronome);
		menu.add(showElapsedTimeView);
		menu.add(editSampling);
		menu.add(editIntonation);
		menu.add(editTenoriTone);
		menu.add(setStartBeat);
		menu.add(setStartBeatPlayhead);
		menu.add(setEndBeatPlayhead);

		mbar.add(menu);

		menu = new JMenu("Track");
		menu.add(addTrack);
		menu.add(incrementTrack);
		menu.add(trackOn);
		menu.add(trackOff);
		menu.add(deleteTrack);
		menu.add(deleteAllFramesInTrack);
		menu.add(setVoiceMarkTrack);
		menu.add(showTenori);
		menu.add(showTrebleClef);
		menu.add(showBassClef);

		mbar.add(menu);

		menu = new JMenu("BuildV");
		menu.add(buildOctaveM2);
		menu.add(buildOctaveM1);
		menu.add(buildOctaveP0);
		menu.add(buildOctaveP1);
		menu.add(buildOctaveP2);
		menu.add(printAutocorFrequencies);
		menu.add(pasteToLoTenor);
		menu.add(pasteToHiTenor);
		menu.add(pasteToLoWideband);
		menu.add(pasteToHiWideband);

		mbar.add(menu);

		menu = new JMenu("Agent");
		menu.add(prepMastering);
		menu.add(editAgentName);
		menu.add(editAgent);
		menu.add(editAgentVibrato);
		menu.add(pitchBendAnalyze);
		menu.add(envelopeAnalyze);
		menu.add(captureAgentToPseudoLoop);
		menu.add(capturePseudoLoopToAgent);
		menu.add(captureTrackToPseudoLoop);
		menu.add(printPseudoLoopList);
		menu.add(revertAgentDefaultLoop);
		menu.add(dumpWaveform);
		menu.add(editPack1);
		menu.add(editPack2);
		menu.add(editPack3);

		mbar.add(menu);
		
		menu = new JMenu("Compose");
		menu.add(trackMainButton);
		menu.add(trackHarmonyButton);
		menu.add(printResults);

		mbar.add(menu);
		
		menu = new JMenu("WaveAnalyze");
		menu.add(viewWavesButton);
		menu.add(waveDiagramAnalyze);

		mbar.add(menu);
		

		pan.add(BorderLayout.NORTH, mbar);
		pan.add(BorderLayout.CENTER, lst);

		playButton.addActionListener(this);
		wavButton.addActionListener(this);
		cpanButton.addActionListener(this);
		tapButton.addActionListener(this);
		clearTapButton.addActionListener(this);
		buildButton.addActionListener(this);
		smartBuildButton.addActionListener(this);
		replicateAcrossButton.addActionListener(this);
		buildVoiceButton.addActionListener(this);
		buildVoiceBendButton.addActionListener(this);
		buildDigiButton.addActionListener(this);
		digitizeButton.addActionListener(this);
		viewButton.addActionListener(this);
		viewWavesButton.addActionListener(this);
		viewVolumeButton.addActionListener(this);
		viewTempoButton.addActionListener(this);
		viewMeasuresButton.addActionListener(this);
		viewTrackFramesButton.addActionListener(this);
		viewVibratoRateButton.addActionListener(this);
		buildLoopButton.addActionListener(this);
//		broadcastSlimButton.addActionListener(this);
		estimateTimeCompleteButton.addActionListener(this);
		estimateCoreCapabilityButton.addActionListener(this);
		printTimeForTap.addActionListener(this);
		quitButton.addActionListener(this);
		
		determineKey.addActionListener(this);
		confirmKey.addActionListener(this);
		insertInitWAV.addActionListener(this);

		showVisualMetronome.addActionListener(this);
		showElapsedTimeView.addActionListener(this);
		editSampling.addActionListener(this);
		editIntonation.addActionListener(this);
		editTenoriTone.addActionListener(this);
		setStartBeat.addActionListener(this);
		setStartBeatPlayhead.addActionListener(this);
		setEndBeatPlayhead.addActionListener(this);

		addTrack.addActionListener(this);
		incrementTrack.addActionListener(this);
		trackOn.addActionListener(this);
		trackOff.addActionListener(this);
		deleteTrack.addActionListener(this);
		deleteAllFramesInTrack.addActionListener(this);
		setVoiceMarkTrack.addActionListener(this);
		showTenori.addActionListener(this);
		showTrebleClef.addActionListener(this);
		showBassClef.addActionListener(this);

		editAgentName.addActionListener(this);
		editAgent.addActionListener(this);
		editAgentVibrato.addActionListener(this);
		prepMastering.addActionListener(this);
		pitchBendAnalyze.addActionListener(this);
		envelopeAnalyze.addActionListener(this);
		waveDiagramAnalyze.addActionListener(this);
		captureAgentToPseudoLoop.addActionListener(this);
		capturePseudoLoopToAgent.addActionListener(this);
		captureTrackToPseudoLoop.addActionListener(this);
		printPseudoLoopList.addActionListener(this);
		revertAgentDefaultLoop.addActionListener(this);
		dumpWaveform.addActionListener(this);
		editPack1.addActionListener(this);
		editPack2.addActionListener(this);
		editPack3.addActionListener(this);

		buildOctaveM2.addActionListener(this);
		buildOctaveM1.addActionListener(this);
		buildOctaveP0.addActionListener(this);
		buildOctaveP1.addActionListener(this);
		buildOctaveP2.addActionListener(this);
		printAutocorFrequencies.addActionListener(this);
		pasteToLoTenor.addActionListener(this);
		pasteToHiTenor.addActionListener(this);
		pasteToLoWideband.addActionListener(this);
		pasteToHiWideband.addActionListener(this);
		
		trackMainButton.addActionListener(this);
		trackHarmonyButton.addActionListener(this);
		printResults.addActionListener(this);

		refreshList();
		
		lst.addListSelectionListener(
				new ListSelectionListener()
				{
			public void valueChanged(ListSelectionEvent e) {
				if( !( e.getValueIsAdjusting() ) )
				{
					if( lst.getSelectedIndex() >= 0 )
					{
						final int idx = lst.getSelectedIndex();
						SongData.currentTrack = idx;
						if (SongData.currentTrack >= SongData.instrumentTracks.size()) {
							SongData.currentTrack = 0;
						}
						SongListeners.updateViewPanes();
						refreshList();
					}
				}
			}
				}  );
		
		
		
	}

	public static OutputChoiceInterface show(File sound, UndoManager _undoMgr) {
		OutputChoiceInterface choice = new OutputChoiceInterface(sound, _undoMgr);
		JFrame fr = new JFrame();
		fr.getContentPane().add(choice.getGUI());
		fr.pack();
		fr.show();
		return (choice);
	}
	
	public static TapPad getTapPad()
	{
		return( tapPad );
	}

	protected void buildTrackFrameFromTapPad(int mode, double octaveMultiplier) throws Throwable {
		final int core = 0;
		if (mode >= 0) {
			int noteCount = 0;
			TrackFrame tr = new TrackFrame();
			InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
			IntelligentAgent agent = track.getAgent();
			ArrayList<NoteDesc> tvect = new ArrayList<NoteDesc>();

			NotePitchDigitizer.setVoiceMarkMode();
			ArrayList<TimeInterval> timeIntervals = tapPad.getTimeIntervalArrayList();
			if (timeIntervals.size() > 0) {
				for( final TimeInterval ti : timeIntervals ) {
					NoteDesc nd = SongData.buildNoteDesc(mode, ti, noteCount,
							agent,core);
					tvect.add(nd);
					tr.getNotes().add(nd);
					noteCount++;
				}

				track.getTrackFrames().add(tr);
				track.updateTrackFrames(core);

				if (mode == 2) {
					for ( final NoteDesc nd : tvect ) {
						SongData.configureNoteFreqMode2(agent, nd, core);
						nd.getFreqAndBend().setBaseFreq(
								(nd.getFreqAndBend().getBaseFreq())
										* octaveMultiplier);
					}

					track.updateTrackFrames(core);
				}

				if ((mode != 0) && (mode != 2)) {
					for ( final NoteDesc nd : tvect ) {
						nd.getFreqAndBend().setBaseFreq(
								(nd.getFreqAndBend().getBaseFreq())
										* octaveMultiplier);
					}

					track.updateTrackFrames(core);
				}

				timeIntervals.clear();
			}

		}
	}

	protected void digitize() {
		try {
			if (digitizer == null) {
				digitizer = new SoundDigitizer(_soundPlayer);
				digitizer.pack();
				digitizer.show();
			}
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}

/*	protected void viewTrack() {
		if (noteViewPane == null) {
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			noteViewPane = new NoteViewPane( undoMgr );
			NoteViewPaneController controller = new NoteViewPaneController(
					noteViewPane);
			noteViewPane.setStartFrequency(150.0);
			noteViewPane.setEndFrequency(750.0);
			noteViewPane.setStartBeatNumber(-10);
			noteViewPane.setEndBeatNumber( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10);
			noteViewPane.refreshDisplayList();
			undoMgr.commitUndoableOp(utag, "Create NoteViewPane" );
			undoMgr.clearUndoMemory();
			JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, noteViewPane);
			fr.getContentPane().add(BorderLayout.SOUTH, controller);
			fr.pack();
			fr.show();
		}
	} */
	
	protected void viewTrack2() {
		// if (noteViewPane == null) {
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			final NoteViewPane2 noteViewPane = new NoteViewPane2( undoMgr );
			NoteViewPaneController2 controller = new NoteViewPaneController2(
					noteViewPane);
			noteViewPane.setStartFrequency(150.0);
			noteViewPane.setEndFrequency(750.0);
			noteViewPane.setStartBeatNumber(-10);
			noteViewPane.setEndBeatNumber( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10);
			noteViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create NoteViewPane" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, noteViewPane);
			fr.getContentPane().add(BorderLayout.SOUTH, controller);
			fr.pack();
			fr.show();
			SongListeners.noteViewPanes.add( noteViewPane );
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.noteViewPanes.remove( noteViewPane );
					}
				}
				
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.noteViewPanes.remove( noteViewPane );
					}
				}
			} );
			
			
	//	}
	}
	
	protected void viewTrackWave2() {
//		if (waveViewPane == null) {
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			final WaveViewPane waveViewPane = new WaveViewPane( undoMgr );
			WaveViewPaneController controller = new WaveViewPaneController(
					waveViewPane);
			waveViewPane.setStartLevel( -1.1 );
			waveViewPane.setEndLevel( 1.1 );
			waveViewPane.setStartBeatNumber(-10);
			waveViewPane.setEndBeatNumber( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10);
			waveViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create waveViewPane" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, waveViewPane);
			fr.getContentPane().add(BorderLayout.SOUTH, controller);
			fr.pack();
			fr.show();
			SongListeners.waveViewPanes.add( waveViewPane );
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.waveViewPanes.remove( waveViewPane );
					}
				}
				
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.waveViewPanes.remove( waveViewPane );
					}
				}
			} );
//		}
	}
	
	protected void viewTenori() {
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			final TenoriViewPane tenoriViewPane = new TenoriViewPane( undoMgr , SongListeners.tenoriViewPanes );
			TenoriViewPaneController controller = new TenoriViewPaneController(
					tenoriViewPane);
			tenoriViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create tenoriViewPane" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, tenoriViewPane);
			fr.getContentPane().add(BorderLayout.SOUTH, controller);
			fr.pack();
			fr.show();
			SongListeners.tenoriViewPanes.add( tenoriViewPane );
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.tenoriViewPanes.remove( tenoriViewPane );
					}
				}
				
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.tenoriViewPanes.remove( tenoriViewPane );
					}
				}
			} );
	}
	
	protected void viewTrackVolumes() {
//		if (volumeViewPane == null) {
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
			final VolumeViewPane2 volumeViewPane = new VolumeViewPane2( undoMgr );
			VolumeViewPaneController2 controller = new VolumeViewPaneController2(
					volumeViewPane);
			volumeViewPane.setStartLevel(-0.1);
			volumeViewPane.setEndLevel(1.1);
			volumeViewPane.setStartBeatNumber(0.0);
			volumeViewPane.setEndBeatNumber( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ));
			volumeViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create VolumeViewPane" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, volumeViewPane);
			fr.getContentPane().add(BorderLayout.SOUTH, controller);
			fr.pack();
			fr.show();
			SongListeners.volumeViewPanes.add( volumeViewPane );
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.volumeViewPanes.remove( volumeViewPane );
					}
				}
				
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.volumeViewPanes.remove( volumeViewPane );
					}
				}
			} );
			
//		}
	}
	
	protected void viewTempo() {
//		if (tempoViewPane == null) {
			final int core = 0;
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			final TempoViewPane2 tempoViewPane = new TempoViewPane2( undoMgr );
			TempoViewPaneController2 controller = new TempoViewPaneController2(
					tempoViewPane);
			tempoViewPane.setStartLevel(-20.0);
			tempoViewPane.setEndLevel(220.0);
			tempoViewPane.setStartSecondsNumber( SongData.getElapsedTimeForBeatBeat( -10 , core ) );
			tempoViewPane.setEndSecondsNumber( SongData.getElapsedTimeForBeatBeat( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10 , core ) );
			tempoViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create TempoViewPane" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, tempoViewPane);
			fr.getContentPane().add(BorderLayout.SOUTH, controller);
			fr.pack();
			fr.show();
			SongListeners.tempoViewPanes.add( tempoViewPane );
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.tempoViewPanes.remove( tempoViewPane );
					}
				}
				
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.tempoViewPanes.remove( tempoViewPane );
					}
				}
			} );
//		}
	}
	
	protected void viewMeasures() {
//		if (measuresViewPane == null) {
			final MeasureViewPane measuresViewPane = new MeasureViewPane();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, measuresViewPane);
			fr.pack();
			fr.show();
			SongListeners.measuresViewPanes.add( measuresViewPane );
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.measuresViewPanes.remove( measuresViewPane );
					}
				}
				
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.measuresViewPanes.remove( measuresViewPane );
					}
				}
			} );
//		}
	}
	
	protected void viewTrackFrames() {
//		if (measuresViewPane == null) {
			final TrackFrameViewPane trackFrameViewPane = new TrackFrameViewPane();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, trackFrameViewPane);
			fr.pack();
			fr.show();
			SongListeners.trackFrameViewPanes.add( trackFrameViewPane );
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.trackFrameViewPanes.remove( trackFrameViewPane );
					}
				}
				
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.trackFrameViewPanes.remove( trackFrameViewPane );
					}
				}
			} );
//		}
	}
	
	protected void buildPseudoLoop()
	{
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		PseudoLoopBuilder pseudoLoopBuilder = new PseudoLoopBuilder( undoMgr );
		//PseudoLoopBuilderController controller = new PseudoLoopBuilderController(
		//		pseudoLoopBuilder); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		undoMgr.commitUndoableOp(utag, "Create PseudoLoopBuilder" );
		undoMgr.clearUndoMemory();
		JFrame fr = new JFrame();
		fr.getContentPane().setLayout(new BorderLayout(0, 0));
		fr.getContentPane().add(BorderLayout.CENTER, pseudoLoopBuilder);
		// fr.getContentPane().add(BorderLayout.SOUTH, controller);
		fr.pack();
		fr.show();
	}
	
	protected void viewVibrato() {
//		if (vibratoViewPane == null) {
		    final int core = 0;
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			final VibratoViewPane2 vibratoViewPane = new VibratoViewPane2( undoMgr );
			VibratoViewPaneController2 controller = new VibratoViewPaneController2(
					vibratoViewPane);
			vibratoViewPane.setStartLevel(-1.0);
			vibratoViewPane.setEndLevel(20.0);
			vibratoViewPane.setStartSecondsNumber( SongData.getElapsedTimeForBeatBeat( -10 , core ) );
			vibratoViewPane.setEndSecondsNumber( SongData.getElapsedTimeForBeatBeat( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10 , core ) );
			vibratoViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create VibratoViewPane" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, vibratoViewPane);
			fr.getContentPane().add(BorderLayout.SOUTH, controller);
			fr.pack();
			fr.show();
			SongListeners.vibratoViewPanes.add( vibratoViewPane );
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.vibratoViewPanes.remove( vibratoViewPane );
					}
				}
				
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						SongListeners.vibratoViewPanes.remove( vibratoViewPane );
					}
				}
			} );
//		}
	}
	

	public void regenerateMovie(int mode, double octaveMultiplier)
			throws Throwable {
		if (tapPad != null) {
			buildTrackFrameFromTapPad(mode, octaveMultiplier);

			SongListeners.updateViewPanes();
		}
		
		if( !( SongData.reSampleOnBuild ) )
		{
			return;
		}

		if (movieControlFrame != null) {
			movieControlFrame.hide();
			movieControlFrame.removeAll();
			// movieControlFrame.dispose();
			movieControlFrame = null;
			_soundPlayer = null;
		}

		try {
			soundFile = TestPlayer2.generateSound();
			_soundPlayer = new SoundPlayer( soundFile );
			if (tapPad != null) {
				tapPad.setSoundPlayer(_soundPlayer);
			}
			if (digitizer != null) {
				digitizer.setSoundPlayer(_soundPlayer);
			}
			if (visualMetronome != null) {
				visualMetronome.setSoundPlayer(_soundPlayer);
			}
			if (elapsedTimeView != null) {
				elapsedTimeView.setSoundPlayer(_soundPlayer);
			}
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}
	
	public void estimateTimeToComplete() throws Throwable
	{
		TestPlayer2.estimateTimeToComplete();
	}
	
	public void estimateCoreCapability() throws Throwable
	{
		TestPlayer2.estimateCoreCapability();
	}
	
	public void printTimeForTap()
	{
		final int core = 0;
		ArrayList<TimeInterval> timeIntervals = tapPad.getTimeIntervalArrayList();
		System.out.println( "Tap List : " );
		for( final TimeInterval ti : timeIntervals )
		{
			double startSeconds = ti.getStartTimeSeconds();
			System.out.println( "Seconds -- " + startSeconds );
			double beatNumber = SongData.getBeatNumber( startSeconds , core );
			String beatString = SongData.getMeasureString( beatNumber , core );
			System.out.println( "Beat -- " + beatString );
		}
	}
	
	public static Double getBeatNumberForFirstTap()
	{
		final int core = 0;
		if( tapPad == null )
		{
			return( null );
		}
		ArrayList<TimeInterval> timeIntervals = tapPad.getTimeIntervalArrayList();
		if (timeIntervals.size() > 0) {
			Iterator<TimeInterval> it = timeIntervals.iterator();
			TimeInterval ti = it.next();
			double startSeconds = ti.getStartTimeSeconds();
			double beatNumber = SongData.getBeatNumber( startSeconds , core );
			return( new Double( beatNumber ) );
		}
		return( null );
	}
	
	public static Double getBeatNumberForSecondTap()
	{
		final int core = 0;
		if( tapPad == null )
		{
			return( null );
		}
		ArrayList<TimeInterval> timeIntervals = tapPad.getTimeIntervalArrayList();
		if (timeIntervals.size() > 1) {
			Iterator<TimeInterval> it = timeIntervals.iterator();
			TimeInterval ti = it.next();
			ti = it.next();
			double startSeconds = ti.getStartTimeSeconds();
			double beatNumber = SongData.getBeatNumber( startSeconds , core );
			return( new Double( beatNumber ) );
		}
		return( null );
	}

	

	public void refreshList() {
		ArrayList<String> vect = new ArrayList<String>();
		int count;
		for (count = 0; count < SongData.instrumentTracks.size(); count++) {
			InstrumentTrack ins = SongData.instrumentTracks.get(count);
			String str = "" + count + " ";
			if (count == SongData.currentTrack) {
				str = str + "* ";
			}
			str = str + ins.getAgent().getHname() + " ";

			if (ins.isTrackOn()) {
				str = str + "[On]";
			} else {
				str = str + "[Off]";
			}
			
			if( count == SongData.voiceMarkTrack )
			{
				str = str + "[VoiceMark]";
			}

			vect.add(str);
		}

		lst.setListData(new Vector<String>(vect));
		lst.repaint();
	}



	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == playButton) {
				if (movieControlFrame == null) {
					if( _soundPlayer == null ) _soundPlayer = new SoundPlayer( soundFile );
			        // Put it in a window and play it
			        JFrame f = new JFrame("SoundPlayer");
			        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			        f.getContentPane( ).add( _soundPlayer , "Center");
			        f.pack( );
			        f.setVisible(true);
					movieControlFrame = f;
				}
			}

			if (e.getSource() == wavButton) {
				
				Frame fr = null;
				
				FileDialog fd = new FileDialog( fr , "Save", FileDialog.SAVE);
				
				fd.show();
				
				if( fd.getFile() == null )
				{
					return;
				}
				
				File fi = new File( fd.getFile() );
				

				FileInputStream is = new FileInputStream( soundFile );
				FileOutputStream os = new FileOutputStream( fi );
				
				byte[] buffer = new byte[ 8192 ];
				
				int length;
				while( ( length = is.read( buffer ) ) > 0 )
				{
					os.write(buffer, 0, length);
				}
				
				is.close();
				os.close();
				
			}
			
			
			if (e.getSource() == cpanButton) {
				SoundControlPanel.showControlPanel();
			}


			if (e.getSource() == tapButton) {
				if (tapPad == null) {
					tapPad = new TapPad( _soundPlayer );
					Frame fr = new Frame();
					fr.setLayout(new BorderLayout(0, 0));
					fr.add(BorderLayout.CENTER, tapPad);
					fr.pack();
					fr.show();
				}
			}
			
			if (e.getSource() == showVisualMetronome) {
				if (visualMetronome == null) {
					visualMetronome = new VisualMetronome();
					JPanel pn = visualMetronome.constructPanel();
					final JFrame fr = new JFrame();
					fr.getContentPane().setLayout( new BorderLayout( 0 , 0 ) );
					fr.getContentPane().add( BorderLayout.CENTER , pn );
					fr.pack();
					fr.show();
					visualMetronome.handleShow( _soundPlayer );
				}
			}
			
			if (e.getSource() == showElapsedTimeView) {
				if (elapsedTimeView == null) {
					elapsedTimeView = new ElapsedTimeView();
					JPanel pn = elapsedTimeView.constructPanel();
					final JFrame fr = new JFrame();
					fr.getContentPane().setLayout( new BorderLayout( 0 , 0 ) );
					fr.getContentPane().add( BorderLayout.CENTER , pn );
					fr.pack();
					fr.show();
					elapsedTimeView.handleShow( _soundPlayer );
				}
			}

			if (e.getSource() == clearTapButton) {
				ArrayList<TimeInterval> timeIntervals = tapPad.getTimeIntervalArrayList();
				timeIntervals.clear();
			}

			if (e.getSource() == buildButton) {
				regenerateMovie(0, octaveMultiplier);
			}

			if (e.getSource() == buildVoiceButton) {
				regenerateMovie(1, octaveMultiplier);
			}

			if (e.getSource() == buildVoiceBendButton) {
				regenerateMovie(2, octaveMultiplier);
			}
			
			if( e.getSource() == smartBuildButton ) {
				SmartTapBuilderEditor editor = new SmartTapBuilderEditor(this);
				ProgramDirector.showPropertyEditor(editor, null,
						"Edit Start / End Beats");
			}
			
			if( e.getSource() == replicateAcrossButton ) {
				final ArrayList<TimeInterval> timeIntervals = tapPad.getTimeIntervalArrayList();
				final int len = timeIntervals.size();
				final TimeInterval last = timeIntervals.get( len - 1 );
				final TimeInterval first = timeIntervals.get( 0 );
				final double timeDelta = last.startTimeSeconds - first.startTimeSeconds;
				int count;
				for( count = 1 ; count < 50 ; count++ )
				{
					if( ( count % 8 ) != 0 )
					{
						final double startDelta = count * timeDelta;
						int icnt;
						for( icnt = 0 ; icnt < ( len - 1 ) ; icnt++ )
						{
							if(  ( count != 1 ) || ( icnt != 0 ) )
							{
								TimeInterval t0 = timeIntervals.get( icnt );
								timeIntervals.add( new TimeInterval( t0.startTimeSeconds + startDelta , t0.endTimeSeconds + startDelta ) );
							}
						}
					}
				}
			}

			if (e.getSource() == buildDigiButton) {
				/* final int core = 0;
				InstrumentTrack track = new InstrumentTrack();
				SongData.instrumentTracks.add(track);
				track.getTrackFrames().add(SongData.buildNotes());
				SampledAgent agent = DigitizerData.buildSampledAgent();
				track.setAgent(agent);
				double startTime = DigitizerData.startTimeSeconds;
				double endTime = DigitizerData.endTimeSeconds;
				TimeInterval ti = new TimeInterval(startTime, endTime);
				NoteDesc nd = SongData.buildNoteDesc(0, ti, 0, agent, core);
				TrackFrame tr = new TrackFrame();
				tr.getNotes().add(nd);
				track.getTrackFrames().add(tr);
				track.updateTrackFrames(core);
				regenerateMovie(-1, octaveMultiplier);
				refreshList(); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
			}
			
			//if (e.getSource() == buildRoundButton ) {
			//	Composer composer = new HarmonyComposerBNib();
			//	// Composer composer = new SimpleRoundComposerN();
			//	composer.composeRound(true);
			//	regenerateMovie(-1, octaveMultiplier);
			//	refreshList();
			//}
			
			if( e.getSource() == trackMainButton )
			{
				TreeMap<String,Class<? extends IComposerController>> map = ComposerManager.getMapClone();
				Iterator<Entry<String,Class<? extends IComposerController>>> it = map.entrySet().iterator();
				if( !( it.hasNext() ) )
				{
					throw( new RuntimeException( "Fail." ) );
				}
				Entry<String,Class<? extends IComposerController>> en = it.next();
				String key = en.getKey();
				Class<? extends IComposerController> clss = en.getValue();
				IComposerController cnt = clss.newInstance();
				cnt.buildRoundMain(this);
			}
			
			if( e.getSource() == trackHarmonyButton )
			{
				TreeMap<String,Class<? extends IComposerController>> map = ComposerManager.getMapClone();
				Iterator<Entry<String,Class<? extends IComposerController>>> it = map.entrySet().iterator();
				if( !( it.hasNext() ) )
				{
					throw( new RuntimeException( "Fail." ) );
				}
				Entry<String,Class<? extends IComposerController>> en = it.next();
				String key = en.getKey();
				Class<? extends IComposerController> clss = en.getValue();
				IComposerController cnt = clss.newInstance();
				cnt.buildRoundHarmony(this);
			}
			
			if( e.getSource() == printResults )
			{
				TreeMap<String,Class<? extends IComposerController>> map = ComposerManager.getMapClone();
				Iterator<Entry<String,Class<? extends IComposerController>>> it = map.entrySet().iterator();
				if( !( it.hasNext() ) )
				{
					throw( new RuntimeException( "Fail." ) );
				}
				Entry<String,Class<? extends IComposerController>> en = it.next();
				String key = en.getKey();
				Class<? extends IComposerController> clss = en.getValue();
				IComposerController cnt = clss.newInstance();
				cnt.printResults();
			}
			
			if (e.getSource() == determineKey) {
				final int core = 0;
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				TrackFrame frame = track.getTrackFrames().get(1);
				NoteDesc note = frame.getNotes().get(0);
				KeyAndScaleData.determineKey(note,core);
			}
			
			if (e.getSource() == confirmKey ) {
				KeyAndScaleData.confirmKey();
				SongListeners.updateViewPanes();
			}
			
			if (e.getSource() == insertInitWAV ) {
				InstrumentTrack track = new InstrumentTrack();
				SongData.instrumentTracks.add(track);
				track.getTrackFrames().add(SongData.buildNotes());
				Class<? extends IntelligentAgent> clss = AgentManager.getMapClone().get( "Sampled Agent" );
				if( clss == null )
				{
					throw( new RuntimeException( "Fail..." ) );
				}
				IntelligentAgent agent = clss.newInstance();
				track.setAgent( agent );
				track.getAgent().setHname( clss.getName() );
				VibratoParameters vibr = new VibratoParameters();
				vibr.setVibratoMode( VibratoParameters.VIBRATO_MODE_NONE );
				agent.setVibratoParams( vibr );
				int sz = SongData.instrumentTracks.size();
				final int prevCurrent = SongData.currentTrack;
				SongData.currentTrack = sz - 1;
				SongListeners.updateViewPanes();
				refreshList();
				if (tapPad == null) {
					tapPad = new TapPad( _soundPlayer );
					Frame fr = new Frame();
					fr.setLayout(new BorderLayout(0, 0));
					fr.add(BorderLayout.CENTER, tapPad);
					fr.pack();
					fr.show();
				}
				ArrayList<TimeInterval> timeIntervals = tapPad.getTimeIntervalArrayList();
				timeIntervals.clear();
				timeIntervals.add( new TimeInterval( 0.0 , 5.0 * 60.0 ) );
				regenerateMovie(0, octaveMultiplier); 
				SongData.currentTrack = prevCurrent;
				SongListeners.updateViewPanes();
				refreshList();
			}

			if (e.getSource() == digitizeButton) {
				digitize();
			}

			if (e.getSource() == viewButton) {
				viewTrack2();
			}
			
			if (e.getSource() == viewWavesButton) {
				viewTrackWave2();
			}
			
			if (e.getSource() == viewVolumeButton) {
				viewTrackVolumes();
			}
			
			if (e.getSource() == viewTempoButton) {
				viewTempo();
			}
			
			if (e.getSource() == viewMeasuresButton) {
				viewMeasures();
			}
			
			if (e.getSource() == viewTrackFramesButton) {
				viewTrackFrames();
			}
			
			if (e.getSource() == viewVibratoRateButton) {
				viewVibrato();
			}
			
			if (e.getSource() == buildLoopButton ) {
				buildPseudoLoop();
			}
			
//			if (e.getSource() == broadcastSlimButton) {
//				SlimServerBroadcaster broadcaster = new SlimServerBroadcaster();
//				broadcaster.broadcastToSlimServer( soundFile );
//			}
			
			if( e.getSource() == estimateTimeCompleteButton )
			{
				estimateTimeToComplete();
			}
			
			if( e.getSource() == estimateCoreCapabilityButton )
			{
				estimateCoreCapability();
			}
			
			if( e.getSource() == printTimeForTap )
			{
				printTimeForTap();
			}

			if (e.getSource() == quitButton) {
				System.exit(0);
			}
			
			if (e.getSource() == editSampling) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				HashSet<BuilderNode> s = TestPlayer2.editPackCoeff.getElem();
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesCoeff());
			}
			
			if (e.getSource() == editIntonation) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				NoteTable.handleRatiosUpdate();
				int count;
				for (count = 0; count < SongData.instrumentTracks.size(); count++) {
					InstrumentTrack ins = SongData.instrumentTracks.get(count);
					if( ins.getAgent() != null )
					{
						ins.getAgent().initializeInitializers();
					}
				}
				HashSet<BuilderNode> s = TestPlayer2.editPackIntonation.getElem();
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesIntonation());
				SongListeners.updateViewPanes();
			}
			
			if( e.getSource() == editTenoriTone )
			{
				EventQueue.invokeLater(new Runnable() {

		            public void run() {
		                new JavaSoundInstrumentTone().display();
		            }
		        });
			}

			if (e.getSource() == setStartBeat) {
				StartBeatEditor editor = new StartBeatEditor(this);
				ProgramDirector.showPropertyEditor(editor, null,
						"Edit Start / End Beats");
			}
			
			if (e.getSource() == setStartBeatPlayhead) {
				final int core = 0;
				final double TIME_SCALE = 1000000;
				final long startTime = _soundPlayer.getMicrosecondPosition();
				final double startTimeSeconds = ((double) startTime) / TIME_SCALE;
				final double startBeat = SongData.getBeatNumber( startTimeSeconds , core );
				SongData.START_BEAT = startBeat - 1.0;
			}
			
			if (e.getSource() == setEndBeatPlayhead) {
				final int core = 0;
				final double TIME_SCALE = 1000000;
				final long endTime = _soundPlayer.getMicrosecondPosition();
				final double endTimeSeconds = ((double) endTime) / TIME_SCALE;
				final double endBeat = SongData.getBeatNumber( endTimeSeconds , core );
				SongData.END_BEAT = endBeat + 1.0;
			}

			if (e.getSource() == prepMastering) {
				SongData.ROUGH_DRAFT_MODE = false;
				SongData.CALC_GLOBAL_MAX_VAL = true;

				SongData.START_BEAT = 0;
				SongData.END_BEAT = SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES );
			}

			if (e.getSource() == addTrack) {
				InstrumentTrack track = new InstrumentTrack();
				SongData.instrumentTracks.add(track);
				track.getTrackFrames().add(SongData.buildNotes());
				Class<? extends IntelligentAgent> clss = AgentManager.getMapClone().get( "High Guitar Agent" );
				if( clss == null )
				{
					throw( new RuntimeException( "Fail..." ) );
				}
				track.setAgent( clss.newInstance() );
				track.getAgent().setHname( clss.getName() );
				int sz = SongData.instrumentTracks.size();
				SongListeners.updateViewPanes();
				refreshList();
			}

			if (e.getSource() == incrementTrack) {
				SongData.currentTrack++;
				if (SongData.currentTrack >= SongData.instrumentTracks.size()) {
					SongData.currentTrack = 0;
				}
				SongListeners.updateViewPanes();
				refreshList();
			}

			if (e.getSource() == trackOn) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				track.setTrackOn(true);
				refreshList();
			}

			if (e.getSource() == trackOff) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				track.setTrackOn(false);
				refreshList();
			}

			if (e.getSource() == deleteTrack) {
				int option = JOptionPane.showConfirmDialog(getGUI(), "Delete track?");
				if( option != JOptionPane.YES_OPTION )
				{
					return;
				}
				SongData.instrumentTracks.remove(SongData.currentTrack);
				SongData.currentTrack = 0;
				SongListeners.updateViewPanes();
				refreshList();
			}
			
			if (e.getSource() == deleteAllFramesInTrack) {
				int option = JOptionPane.showConfirmDialog(getGUI(), "Delete all frames in track?");
				if( option != JOptionPane.YES_OPTION )
				{
					return;
				}
				SongData.getCurrentTrack().getTrackFrames().clear();
				SongListeners.updateViewPanes();
			}

			if (e.getSource() == setVoiceMarkTrack) {
				SongData.voiceMarkTrack = SongData.currentTrack;
				System.out.println("Voice Mark Track Is "
						+ (SongData.voiceMarkTrack ));
			}
			
			if (e.getSource() == showTenori) {
				viewTenori();
			}
			
			if (e.getSource() == showTrebleClef) {
				VerdantiumPropertiesEditor editor = 
					new TrebleClefPlayer( );
				ProgramDirector.showPropertyEditor(editor, null,
					"Treble Clef");
			}
			
			if (e.getSource() == showBassClef) {
				VerdantiumPropertiesEditor editor = 
					new BassClefPlayer( );
				ProgramDirector.showPropertyEditor(editor, null,
					"Bass Clef");
			}
			
			if (e.getSource() == editAgentName) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				VerdantiumPropertiesEditor editor = 
						new AgentNameEditor( agent , this );
				ProgramDirector.showPropertyEditor(editor, null,
						"Agent Name Properties");
			}
			
			if (e.getSource() == editAgent) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				if (agent instanceof EditableAgent) {
					VerdantiumPropertiesEditor editor = 
							( (EditableAgent) agent ).getEditor( track );
					if( editor == null )
					{
						return;
					}
					ProgramDirector.showPropertyEditor(editor, null,
							"Agent Properties");
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			}
			
			if (e.getSource() == editAgentVibrato) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				if (agent instanceof EditableAgent) {
					VerdantiumPropertiesEditor editor = 
							( (EditableAgent) agent ).getVibratoEditor( track );
					ProgramDirector.showPropertyEditor(editor, null,
							"Agent Vibrato Properties");
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			}
			
			if (e.getSource() == pitchBendAnalyze) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				TrackFrame frame = track.getTrackFrames().get(1);
				NoteDesc note = frame.getNotes().get(0);
				PitchBendAnalyzer analyzer = new PitchBendAnalyzer(note);
				analyzer.calculate();
			}
			
			if (e.getSource() == envelopeAnalyze) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				TrackFrame frame = track.getTrackFrames().get(1);
				NoteDesc note = frame.getNotes().get(0);
				EnvelopeAnalyzer analyzer = new EnvelopeAnalyzer(note);
				analyzer.calculate();
			}
			
			if (e.getSource() == waveDiagramAnalyze) {
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				TrackFrame frame = track.getTrackFrames().get(1);
				NoteDesc note = frame.getNotes().get(0);
				if (note != null) {
					NoteDesc desc = note;
					
					System.out.println( "Working..." );

					WaveRenderingPane pane = new WaveRenderingPane( desc );
					
					JFrame fr = new JFrame();
					
					fr.getContentPane().setLayout( new BorderLayout( 0 , 0 ) );
					
					fr.getContentPane().add( BorderLayout.CENTER , pane );
					
					fr.pack();
					
					fr.show();
					
					System.out.println( "Phase Done." );

				} else {
					Toolkit.getDefaultToolkit().beep();
				}
				
			}
			
			if( e.getSource() == captureAgentToPseudoLoop)
			{
				JTextField fld = new JTextField( "Loop Name" );
				String name = JOptionPane.showInputDialog(fld, "Loop Name : ");
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				SongData.captureAgentToPseudoLoop( agent , name );
			}
			
			if( e.getSource() == capturePseudoLoopToAgent)
			{
				JTextField fld = new JTextField( "Loop Name" );
				String name = JOptionPane.showInputDialog(fld, "Loop Name : ");
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				SongData.capturePseudoLoopToAgent( name , agent );
			}
			
			if( e.getSource() == captureTrackToPseudoLoop)
			{
				final int core = 0;
				JTextField fld = new JTextField( "Loop Name" );
				String name = JOptionPane.showInputDialog(fld, "Loop Name : ");
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				TrackFrame frame = track.getTrackFrames().get(1);
				SongData.captureTrackFrameToPseudoLoop( frame , name , core );
			}
			
			if (e.getSource() == printPseudoLoopList) {
				System.out.println( "** Listing Pseudo Loops" );
				for( final Entry<String,ArrayList<NoteInitializer>> ee : SongData.pseudoLoopMap.entrySet() )
				{
					String key = ee.getKey();
					ArrayList<NoteInitializer> arr = ee.getValue();
					System.out.println( key + " -- " + ( arr.size() ) );
				}
				System.out.println( "** End Pseudo Loops" );
			}
			
			if( e.getSource() == revertAgentDefaultLoop )
			{
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				if( agent != null )
				{
					agent.initializeInitializers();
				}
			}
			
			if( e.getSource() == dumpWaveform )
			{
				final int core = 0;
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
				int len = trackFrames.size();
				int count;
				for( count = 0 ; count < len ; count++ )
				{
					TrackFrame tfr = track.getTrackFrames().get( count );
					ArrayList<NoteDesc> notes = tfr.getNotes();
					if( notes.size() > 0 )
					{
						NoteDesc desc = notes.get(0);
						
						GWaveForm gwv = desc.getWaveform( core ).genWave( new HashMap() );
						
						HashSet<BuilderNode> s = new HashSet<BuilderNode>();
						
						Object univ1 = AczonUnivAllocator.allocateUniv();
						Object univ2 = AczonUnivAllocator.allocateUniv();
						
						AazonTransChld.initialCoords( univ1 , s , new Random(757) , gwv );
						
						DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesDump());
						
						return;
					}
				}
			}
			
			if( e.getSource() == editPack1 )
			{
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				if( agent != null )
				{
						HashSet<BuilderNode> s = agent.getEditPack1().getElem();
						Object univ1 = AczonUnivAllocator.allocateUniv();
						Object univ2 = AczonUnivAllocator.allocateUniv();
						DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesWave());
				}
			}
			
			if( e.getSource() == editPack2 )
			{
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				if( agent != null )
				{
						HashSet<BuilderNode> s = agent.getEditPack2().getElem();
						Object univ1 = AczonUnivAllocator.allocateUniv();
						Object univ2 = AczonUnivAllocator.allocateUniv();
						DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesWave());
				}
			}
			
			if( e.getSource() == editPack3 )
			{
				InstrumentTrack track = SongData.instrumentTracks.get(SongData.currentTrack);
				IntelligentAgent agent = track.getAgent();
				if( agent != null )
				{
						HashSet<BuilderNode> s = agent.getEditPack3().getElem();
						Object univ1 = AczonUnivAllocator.allocateUniv();
						Object univ2 = AczonUnivAllocator.allocateUniv();
						DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesWave());
				}
			}

			if (e.getSource() == buildOctaveM2) {
				octaveMultiplier = 0.25;
			}

			if (e.getSource() == buildOctaveM1) {
				octaveMultiplier = 0.5;
			}

			if (e.getSource() == buildOctaveP0) {
				octaveMultiplier = 1.0;
			}

			if (e.getSource() == buildOctaveP1) {
				octaveMultiplier = 2.0;
			}

			if (e.getSource() == buildOctaveP2) {
				octaveMultiplier = 4.0;
			}
			
			if(e.getSource() == printAutocorFrequencies)
			{
				NotePitchDigitizer.printAutocorFrequencies();
			}
			
			if(e.getSource() == pasteToLoTenor)
			{
				NotePitchDigitizer.setTenorLoPaste();
			}
			
			if(e.getSource() == pasteToHiTenor)
			{
				NotePitchDigitizer.setTenorHiPaste();
			}
			
			if(e.getSource() == pasteToLoWideband)
			{
				NotePitchDigitizer.setWidebandLoPaste();
			}
			
			if(e.getSource() == pasteToHiWideband)
			{
				NotePitchDigitizer.setWidebandHiPaste();
			}

		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}

}
