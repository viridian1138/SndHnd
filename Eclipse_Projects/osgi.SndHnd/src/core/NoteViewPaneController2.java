




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

public class NoteViewPaneController2 extends JPanel implements ActionListener {
	
	
	protected JMenuItem editBasePitch = new JMenuItem( "Edit Base Pitch" );
	
	protected JMenuItem editInterpPitch = new JMenuItem( "Edit Interp Pitch" );
	
	protected JMenuItem snapBasePitch = new JMenuItem( "Snap Base Pitch" );
	
	protected JMenuItem snapInterpPitch = new JMenuItem( "Snap Interp Pitch" );
	
	protected JMenuItem editLength = new JMenuItem( "Edit Duration" );
	
	protected JMenuItem seq = new JMenuItem( "Sequester To Window" );
	
	protected JMenuItem insPitchBefore = new JMenuItem( "Insert Pitch Before" );
	
	protected JMenuItem insPitchAfter = new JMenuItem( "Insert Pitch After" );
	
	protected JMenuItem delPitch = new JMenuItem( "Delete Pitch" );
	
	protected JMenuItem selPitch = new JMenuItem( "Select Pitch" );
	
	protected JMenuItem editInterpPosn = new JMenuItem( "Edit Interp Posn" );
	
	protected JMenuItem generateHarmonies = new JMenuItem( "Generate Harmonies" );
	
	protected JMenuItem clearHarmonies = new JMenuItem( "Clear Harmonies" );
	
	protected JMenuItem generateHarmonySurf = new JMenuItem( "Generate Harmony Surf" );
	
	protected JMenuItem snapBasePitchHrm = new JMenuItem( "Snap Base Pitch To Harmony" );
	
	protected JMenuItem snapInterpPitchHrm = new JMenuItem( "Snap Interp Pitch To Harmony" );
	
	protected JMenuItem plotFourierHrm = new JMenuItem( "Plot Fourier On Harmony Curve" );
	
	protected JMenuItem plotVoiceFourierHrm = new JMenuItem( "Plot Voice Fourier On Harmony Curve" );
	
	protected JMenuItem produceHarmony1 = new JMenuItem( "Produce Harmony1 (Magenta)" );
	
	protected JMenuItem produceHarmony2 = new JMenuItem( "Produce Harmony2 (Yellow)" );
	
	protected JMenuItem produceHarmony3 = new JMenuItem( "Produce Harmony3 (Orange)" );
	
	protected JMenuItem produceHarmony0 = new JMenuItem( "Produce Harmony0 (Green)" );
	
	protected JMenuItem fillHarmony = new JMenuItem( "Fill Harmonies" );
	
	protected JMenuItem editHarmony = new JMenuItem( "Edit Harmony..." );
	
	protected JMenuItem snapBasePitchVoice = new JMenuItem( "Snap Base Pitch To Voice Mark" );
	
	protected JMenuItem snapInterpPitchVoice = new JMenuItem( "Snap Interp Pitch To Voice Mark" );
	
	protected JMenuItem baseOctavePlus = new JMenuItem( "Base Pitch Octave +" );
	
	protected JMenuItem baseOctaveMinus = new JMenuItem( "Base Pitch Octave -" );
	
	protected JMenuItem snapBasePitchAgentOctave = new JMenuItem( "Snap Base Pitch To Agent Octave" );
	
	protected JMenuItem addPortamento = new JMenuItem( "Add Portamento" );
	
	protected JMenuItem copyPitchToPasteBuffer = new JMenuItem( "Copy Pitch To Paste Buffer" );
	
	protected JMenuItem snapPitchFromPasteBuffer = new JMenuItem( "Snap Pitch From Paste Buffer" );
	
	protected JMenuItem copyNoteToPasteBuffer = new JMenuItem( "Copy Note To Paste Buffer" );
	
	
	
	protected JMenuItem zoom = new JMenuItem( "Zoom" );
	
	protected JMenuItem zoomX = new JMenuItem( "ZoomX" );
	
	protected JMenuItem zoomY = new JMenuItem( "ZoomY" );
	
	protected JMenuItem unZoom = new JMenuItem( "Unzoom" );
	
	protected JMenuItem unZoom2X = new JMenuItem( "Unzoom2X" );
	
	protected JMenuItem unZoomLimits = new JMenuItem( "Unzoom To Playback Limits" );
	
	
	
	protected JMenuItem deleteAllNotesInTrack = new JMenuItem( "Delete All Notes In Track" );
	
	protected JMenuItem deleteSelectedNote = new JMenuItem( "Delete Selected Note" );
	
	protected JMenuItem deleteAllAfter = new JMenuItem( "Delete All Notes After Tap" );
	
	protected JMenuItem deleteTrackNotes = new JMenuItem( "Delete Notes Between Taps" );
	
	
	
	protected JMenuItem produceHarmonyMohe = new JMenuItem( "Produce Harmony Mohe (Yellow)" );
	
	protected JMenuItem dumpNoteProperties = new JMenuItem( "Dump Note Properties..." );
	
	
	
	protected NoteViewPane2 pane;

	public NoteViewPaneController2( NoteViewPane2 _pane ) {
		super();
		pane = _pane;
		
		JMenuBar mbar = new JMenuBar();
		
		setLayout(new BorderLayout(0,0));
		add( BorderLayout.NORTH , mbar );
		
		JMenu menu = new JMenu( "Edit" );
		mbar.add( menu );
		
		menu.add(editBasePitch);
		menu.add(editInterpPitch);
		menu.add(snapBasePitch);
		menu.add(snapInterpPitch);
		menu.add(editLength);
		menu.add(seq);
		menu.add(insPitchBefore);
		menu.add(insPitchAfter);
		menu.add(delPitch);
		menu.add(selPitch);
		menu.add(editInterpPosn);
		menu.add(snapBasePitchVoice);
		menu.add(snapInterpPitchVoice);
		menu.add(baseOctavePlus);
		menu.add(baseOctaveMinus);
		menu.add(snapBasePitchAgentOctave);
		menu.add(addPortamento);
		menu.add(copyPitchToPasteBuffer);
		menu.add(snapPitchFromPasteBuffer);
		menu.add(copyNoteToPasteBuffer);
		
		
		
		menu = new JMenu( "Harmony" );
		mbar.add( menu );
		
		menu.add(generateHarmonies);
		menu.add(clearHarmonies);
		menu.add(snapBasePitchHrm);
		menu.add(snapInterpPitchHrm);
		menu.addSeparator();
		menu.add(produceHarmony1);
		menu.add(produceHarmony2);
		menu.add(produceHarmony3);
		menu.add(produceHarmony0);
		menu.addSeparator();
		menu.add( fillHarmony );
		menu.add( editHarmony );
		menu.add(generateHarmonySurf);
		menu.add(produceHarmonyMohe);
		
		
		
		
		menu = new JMenu( "Other" );
		mbar.add( menu );
		
		menu.add(zoom);
		menu.add(zoomX);
		menu.add(zoomY);
		menu.add(unZoom);
		menu.add(unZoom2X);
		menu.add(unZoomLimits);
		menu.add(plotFourierHrm);
		menu.add(plotVoiceFourierHrm);
		menu.add(dumpNoteProperties);
		
		
		
		menu = new JMenu( "Delete" );
		mbar.add( menu );
		
		menu.add(deleteAllNotesInTrack);
		menu.add(deleteSelectedNote);
		menu.add(deleteAllAfter);
		menu.add(deleteTrackNotes);
		
		

		editBasePitch.addActionListener(this);
		editInterpPitch.addActionListener(this);
		snapBasePitch.addActionListener(this);
		snapInterpPitch.addActionListener(this);
		editLength.addActionListener(this);
		seq.addActionListener(this);
		insPitchBefore.addActionListener(this);
		insPitchAfter.addActionListener(this);
		delPitch.addActionListener(this);
		selPitch.addActionListener(this);
		editInterpPosn.addActionListener(this);
		snapBasePitchVoice.addActionListener(this);
		snapInterpPitchVoice.addActionListener(this);
		baseOctavePlus.addActionListener(this);
		baseOctaveMinus.addActionListener(this);
		snapBasePitchAgentOctave.addActionListener(this);
		addPortamento.addActionListener(this);
		copyPitchToPasteBuffer.addActionListener(this);
		snapPitchFromPasteBuffer.addActionListener(this);
		copyNoteToPasteBuffer.addActionListener(this);
		
		generateHarmonies.addActionListener(this);
		clearHarmonies.addActionListener(this);
		snapBasePitchHrm.addActionListener(this);
		snapInterpPitchHrm.addActionListener(this);
		produceHarmony1.addActionListener(this);
		produceHarmony2.addActionListener(this);
		produceHarmony3.addActionListener(this);
		produceHarmony0.addActionListener(this);
		fillHarmony.addActionListener(this);
		editHarmony.addActionListener(this);
		generateHarmonySurf.addActionListener(this);
		produceHarmonyMohe.addActionListener(this);
		
		zoom.addActionListener(this);
		zoomX.addActionListener(this);
		zoomY.addActionListener(this);
		unZoom.addActionListener(this);
		unZoom2X.addActionListener(this);
		unZoomLimits.addActionListener(this);
		plotFourierHrm.addActionListener(this);
		plotVoiceFourierHrm.addActionListener(this);
		dumpNoteProperties.addActionListener(this);
		
		deleteAllNotesInTrack.addActionListener(this);
		deleteSelectedNote.addActionListener(this);
		deleteAllAfter.addActionListener(this);
		deleteTrackNotes.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		
	try
	{
		
		if( e.getSource() == editBasePitch )
		{
			pane.editBasePitch();
		}
		
		if( e.getSource() == editInterpPitch )
		{
			pane.editInterpPitch();
		}
		
		if( e.getSource() == snapBasePitch )
		{
			pane.snapBasePitch();
		}
		
		if( e.getSource() == snapInterpPitch )
		{
			pane.snapInterpPitch();
		}
		
		if( e.getSource() == editLength )
		{
			pane.editDuration();
		}
		
		if( e.getSource() == zoom )
		{
			pane.zoom();
		}
		
		if( e.getSource() == zoomX )
		{
			pane.zoomX();
		}
		
		if( e.getSource() == zoomY )
		{
			pane.zoomY();
		}
		
		if( e.getSource() == unZoom )
		{
			pane.unZoom();
		}
		
		if( e.getSource() == unZoom2X )
		{
			pane.unZoom2X();
		}
		
		if( e.getSource() == unZoomLimits )
		{
			pane.unZoomLimits();
		}
		
		if( e.getSource() == seq )
		{
			pane.sequesterToWindow();
		}
		
		if( e.getSource() == insPitchBefore )
		{
			pane.insertPitchBefore();
		}
		
		if( e.getSource() == insPitchAfter )
		{
			pane.insertPitchAfter();
		}
		
		if( e.getSource() == delPitch )
		{
			pane.delPitch();
		}
		
		if( e.getSource() == selPitch )
		{
			pane.selectPitch();
		}
		
		if( e.getSource() == editInterpPosn )
		{
			pane.editInterpPosn();
		}
		
		if( e.getSource() == generateHarmonies )
		{
			pane.generateHarmonies();
		}
		
		if( e.getSource() == generateHarmonySurf )
		{
			pane.generateHarmonySurf();
		}
		
		if( e.getSource() == clearHarmonies )
		{
			pane.clearHarmonies();
		}
		
		if( e.getSource() == snapBasePitchHrm )
		{
			pane.snapBasePitchHrm();
		}
		
		if( e.getSource() == snapInterpPitchHrm )
		{
			pane.snapInterpPitchHrm();
		}
		
		if( e.getSource() == produceHarmony1 )
		{
			pane.produceHarmony( 1 );
		}
		
		if( e.getSource() == produceHarmony2 )
		{
			pane.produceHarmony( 2 );
		}
		
		if( e.getSource() == produceHarmony3 )
		{
			pane.produceHarmony( 3 );
		}
		
		if( e.getSource() == produceHarmony0 )
		{
			pane.produceHarmony( 0 );
		}
		
		if( e.getSource() == fillHarmony )
		{
			pane.fillHarmony();
		}
		
		if( e.getSource() == editHarmony )
		{
			pane.editHarmony();
		}
		
		if( e.getSource() == snapBasePitchVoice )
		{
			pane.snapBasePitchVoice();
		}
		
		if( e.getSource() == snapInterpPitchVoice )
		{
			pane.snapInterpPitchVoice();
		}
		
		if( e.getSource() == baseOctavePlus )
		{
			pane.baseOctavePlus();
		}
		
		if( e.getSource() == baseOctaveMinus )
		{
			pane.baseOctaveMinus();
		}
		
		if( e.getSource() == snapBasePitchAgentOctave )
		{
			pane.snapBasePitchAgentOctave();
		}
		
		if( e.getSource() == addPortamento )
		{
			pane.insertPortamento();
		}
		
		if( e.getSource() == copyPitchToPasteBuffer )
		{
			pane.copyPitchToPasteBuffer();
		}
		
		if( e.getSource() == snapPitchFromPasteBuffer )
		{
			pane.snapPitchFromPasteBuffer();
		}
		
		if( e.getSource() == copyNoteToPasteBuffer )
		{
			pane.copyNoteToPasteBuffer();
		}
		
		if( e.getSource() == deleteAllNotesInTrack )
		{
			pane.deleteAllNotesInTrack();
		}
		
		if( e.getSource() == deleteSelectedNote )
		{
			pane.deleteSelectedNote();
		}
		
		if( e.getSource() == deleteAllAfter )
		{
			pane.deleteAllAfterTap();
		}
		
		if( e.getSource() == deleteTrackNotes )
		{
			pane.deleteTrackNotes();
		}
		
		if( e.getSource() == produceHarmonyMohe )
		{
			pane.produceHarmonyMohe( );
		}
		
		if( e.getSource() == plotFourierHrm )
		{
			pane.plotFourierHrm();
		}
		
		if( e.getSource() == plotVoiceFourierHrm )
		{
			pane.plotVoiceFourierHrm();
		}
		
		if( e.getSource() == dumpNoteProperties )
		{
			pane.dumpNoteProperties( );
		}
		
	}
	catch( Throwable ex )
	{
		ex.printStackTrace( System.out );
	}
	
	}

}

