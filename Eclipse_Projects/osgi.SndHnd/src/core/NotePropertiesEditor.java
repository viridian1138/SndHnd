




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


import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;
import grview.DraggableTransformNodeTest;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import palettes.PaletteClassesDump;
import verdantium.ProgramDirector;
import verdantium.VerdantiumUtils;
import verdantium.undo.UndoManager;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;
import aazon.builderNode.AazonTransChld;
import aazon.builderNode.BuilderNode;
import aczon.AczonUnivAllocator;
import bezier.BezierCubicClampedCoefficient;
import bezier.GPiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;



/**
* A property editor that allows the size of the document page to be changed for
* a client property editor or component.
* <P>
* @author Thorn Green
*/
public class NotePropertiesEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	protected GNonClampedCoefficient noteEnv = null;
	
	protected GPiecewiseCubicMonotoneBezierFlat pitchEnv = null;
	
	protected NoteDesc noteDesc = null;
	
	protected InstrumentTrack insTrack = null;
	
	protected UndoManager undoMgr =  null;
	
	protected JButton applyButton = new JButton("Apply");
	
	protected JCheckBox userDefinedEnvelope = new JCheckBox( "User-Defined Envelope" );
	
	protected JCheckBox userDefinedPitchBend = new JCheckBox( "User-Defined Pitch-Bend" );
	
	protected JCheckBox userDefinedVibrato = new JCheckBox( "User-Defined Vibrato" );
	
	protected JCheckBox userDefinedEnd = new JCheckBox( "User-Defined End" );
	
	protected JButton dumpWaveformButton = new JButton( "Dump Waveform..." );
	
	protected JButton dumpWaveEnvelopeButton = new JButton( "Dump Wave Envelope..." );
	
	protected JButton dumpNoteEnvelopeButton = new JButton( "Dump Note Envelope..." );
	
	protected JButton dumpActualNoteEnvelopeButton = new JButton( "Dump Actual Note Envelope..." );
	
	protected JButton dumpPitchBendButton = new JButton( "Dump Pitch Bend..." );
	
	protected JButton dumpFreqBendButton = new JButton( "Dump Freq Bend..." );
	
	protected JButton editPortamento = new JButton( "Edit Portamento..." );
	
	protected JButton editVibrato = new JButton( "Edit Vibrato..." );
	
	
	
	/**
	* Constructs the property editor for a given PageSizeHandler.
	*/
	public NotePropertiesEditor(NoteDesc _noteDesc, InstrumentTrack _insTrack , UndoManager _undoMgr ) {
		noteDesc = _noteDesc;
		insTrack = _insTrack;
		undoMgr = _undoMgr;
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", userDefinedEnvelope);
		
		pan2.add("any", userDefinedPitchBend);
		
		pan2.add("any", userDefinedVibrato);
		
		pan2.add("any", userDefinedEnd);
		
		pan2.add("any", dumpWaveformButton);
		
		pan2.add("any", dumpWaveEnvelopeButton);
		
		pan2.add("any", dumpNoteEnvelopeButton);
		
		pan2.add("any", dumpActualNoteEnvelopeButton);
		
		pan2.add("any", dumpPitchBendButton);
		
		pan2.add("any", dumpFreqBendButton);
		
		pan2.add("any", editPortamento);
		
		pan2.add("any", editVibrato);
		
		pan2.add("any", new JTextField( "Duration : " + ( 
				noteDesc.getEndBeatNumber() - noteDesc.getStartBeatNumber() ) ) );
		
		pan2.add("any", new JTextField( "Actual Duration : " + ( 
				noteDesc.getActualEndBeatNumber() - noteDesc.getActualStartBeatNumber() ) ) );
		
		pan2.add("any", applyButton);
		
		userDefinedEnvelope.setSelected( _noteDesc.isUserDefinedNoteEnvelope() );
		
		userDefinedPitchBend.setSelected( _noteDesc.getFreqAndBend().isUserDefinedBend() );
		
		userDefinedVibrato.setSelected( _noteDesc.isUserDefinedVibrato() );
		
		userDefinedEnd.setSelected( _noteDesc.isUserDefinedEnd() );
		
		dumpWaveformButton.addActionListener(this);
		
		dumpWaveEnvelopeButton.addActionListener(this);
		
		dumpNoteEnvelopeButton.addActionListener(this);
		
		dumpActualNoteEnvelopeButton.addActionListener(this);
		
		dumpPitchBendButton.addActionListener(this);
		
		dumpFreqBendButton.addActionListener(this);
		
		editPortamento.addActionListener(this);
		
		editVibrato.addActionListener(this);

		applyButton.addActionListener(this);
	}

	/**
	* Gets the GUI of the property editor.
	*/
	public JComponent getGUI() {
		return (MyPan);
	}

	/**
	* Handles the destruction of the component by removing appropriate change listeners.
	*/
	public void handleDestroy() {
	}

	/**
	* Handles a button-press event from the Apply button by changing
	* the size of the client page size handler.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			
			if( e.getSource() == dumpWaveformButton )
			{
				GWaveForm gwv = noteDesc.getWaveform( core ).genWave( new HashMap() );
				
				HashSet<BuilderNode> s = new HashSet<BuilderNode>();
				
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				
				AazonTransChld.initialCoords( univ1 , s , new Random(757) , gwv );
				
				DraggableTransformNodeTest.start(univ1,univ2,s,insTrack,undoMgr,new PaletteClassesDump());
			}
			
			if( e.getSource() == dumpWaveEnvelopeButton )
			{
				GNonClampedCoefficient gwv = noteDesc.getWaveEnvelope( core ).genCoeff( new HashMap() );
				
				HashSet<BuilderNode> s = new HashSet<BuilderNode>();
				
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				
				AazonTransChld.initialCoords( univ1 , s , new Random(757) , gwv );
				
				DraggableTransformNodeTest.start(univ1,univ2,s,insTrack,undoMgr,new PaletteClassesDump());
			}
			
			if( e.getSource() == dumpNoteEnvelopeButton )
			{
				if( noteEnv == null )
				{
					GNonClampedCoefficient gwv = noteDesc.getNoteEnvelope( core ).genCoeff( new HashMap() );
					noteEnv = gwv;
					userDefinedEnvelope.setSelected( true );
				}
				
				HashSet<BuilderNode> s = new HashSet<BuilderNode>();
				
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				
				AazonTransChld.initialCoords( univ1 , s , new Random(757) , noteEnv );
				
				DraggableTransformNodeTest.start(univ1,univ2,s,insTrack,undoMgr,new PaletteClassesDump());
			}
			
			if( e.getSource() == dumpActualNoteEnvelopeButton )
			{
				GNonClampedCoefficient gwv = noteDesc.getActualNoteEnvelope( core ).genCoeff( new HashMap() );
				
				HashSet<BuilderNode> s = new HashSet<BuilderNode>();
				
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				
				AazonTransChld.initialCoords( univ1 , s , new Random(757) , gwv );
				
				DraggableTransformNodeTest.start(univ1,univ2,s,insTrack,undoMgr,new PaletteClassesDump());
			}
			
			
			if( e.getSource() == dumpPitchBendButton )
			{
				if( pitchEnv == null )
				{
					FreqAndBend freqAndBend = noteDesc.getFreqAndBend();
					PiecewiseCubicMonotoneBezierFlatMultiCore bendPerNoteU = freqAndBend.getBendPerNoteU();
					ArrayList<InterpolationPoint> interpPoints = bendPerNoteU.getInterpolationPoints();
					PiecewiseCubicMonotoneBezierFlat bez = new PiecewiseCubicMonotoneBezierFlat();
				
					ArrayList<InterpolationPoint> ipts = new ArrayList<InterpolationPoint>();
					for( final InterpolationPoint pt : interpPoints )
					{
						ipts.add( pt );
					}
				
					bez.setInterpolationPoints( ipts );
					bez.updateAll();
					GPiecewiseCubicMonotoneBezierFlat gwv = new GPiecewiseCubicMonotoneBezierFlat();
					gwv.load( bez );
					pitchEnv = gwv;
					userDefinedPitchBend.setSelected( true );
				}
				
				HashSet<BuilderNode> s = new HashSet<BuilderNode>();
				
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				
				AazonTransChld.initialCoords( univ1 , s , new Random(757) , pitchEnv );
				
				DraggableTransformNodeTest.start(univ1,univ2,s,insTrack,undoMgr,new PaletteClassesDump());
			}
			
			
			if( e.getSource() == dumpFreqBendButton )
			{
				FreqAndBend freqAndBend = noteDesc.getFreqAndBend();
				PiecewiseCubicMonotoneBezierFlatMultiCore freqPerNoteU = freqAndBend.getFreqPerNoteU();
				ArrayList<InterpolationPoint> interpPoints = freqPerNoteU.getInterpolationPoints();
				PiecewiseCubicMonotoneBezierFlat bez = new PiecewiseCubicMonotoneBezierFlat();
				
				ArrayList<InterpolationPoint> ipts = new ArrayList<InterpolationPoint>();
				for( final InterpolationPoint pt :  interpPoints )
				{
					ipts.add( pt );
				}
				
				bez.setInterpolationPoints( ipts );
				bez.updateAll();
				GPiecewiseCubicMonotoneBezierFlat gwv = new GPiecewiseCubicMonotoneBezierFlat();
				gwv.load( bez );
				
				HashSet<BuilderNode> s = new HashSet<BuilderNode>();
				
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				
				AazonTransChld.initialCoords( univ1 , s , new Random(757) , gwv );
				
				DraggableTransformNodeTest.start(univ1,univ2,s,insTrack,undoMgr,new PaletteClassesDump());
			}
			
			
			if( e.getSource() == editPortamento )
			{
				PortamentoDesc desc = noteDesc.getPortamentoDesc();
				if( desc != null )
				{
					PortamentoEditor vib = new PortamentoEditor( desc , insTrack , undoMgr );
					ProgramDirector.showPropertyEditor( vib , null , "Portamento Editor");
				}
				else
				{
					Toolkit.getDefaultToolkit().beep();
				}
			}
			
			
			if( e.getSource() == editVibrato )
			{
				VibratoNoteEditor vib = new VibratoNoteEditor( noteDesc , insTrack );
				ProgramDirector.showPropertyEditor( vib , null , "Vibrato Parameters");
				userDefinedVibrato.setSelected( true );
			}
			
			
			if( e.getSource() == applyButton )
			{
				if( noteEnv != null )
				{
					BezierCubicClampedCoefficient gwv = (BezierCubicClampedCoefficient)( noteEnv.genCoeff( new HashMap() ) );
					noteDesc.setUserDefinedNoteEnvelope( true );
					noteDesc.setNoteEnvelope( gwv );
					SongListeners.updateViewPanesVolumeChange();
				}
				
				if( pitchEnv != null )
				{
					PiecewiseCubicMonotoneBezierFlat gwv = (PiecewiseCubicMonotoneBezierFlat)( pitchEnv.genBez( new HashMap() ) );
					ArrayList<InterpolationPoint> interpPoints = gwv.getInterpolationPoints();
					FreqAndBend freqAndBend = noteDesc.getFreqAndBend();
					PiecewiseCubicMonotoneBezierFlatMultiCore bendPerNoteU = freqAndBend.getBendPerNoteU();
					ArrayList<InterpolationPoint> ipts = bendPerNoteU.getInterpolationPoints();
					ipts.clear();
					
					for( final InterpolationPoint pt :  interpPoints )
					{
						ipts.add( pt );
					}
					
					bendPerNoteU.updateAll();
					noteDesc.getFreqAndBend().setUserDefinedBend( true );
					noteDesc.getFreqAndBend().setWaveInfoDirty( true );
					noteDesc.updateWaveInfoDisplayOnly();
					SongListeners.updateViewPanesPitchChange();
				}
				
				if( userDefinedEnvelope.isSelected() != noteDesc.isUserDefinedNoteEnvelope() )
				{
					noteDesc.setUserDefinedNoteEnvelope( userDefinedEnvelope.isSelected() );
					SongListeners.updateViewPanesVolumeChange();
				}
				
				if( userDefinedPitchBend.isSelected() != noteDesc.getFreqAndBend().isUserDefinedBend() )
				{
					noteDesc.getFreqAndBend().setUserDefinedBend( userDefinedPitchBend.isSelected() );
					noteDesc.getFreqAndBend().setWaveInfoDirty( true );
					noteDesc.updateWaveInfoDisplayOnly();
					SongListeners.updateViewPanesPitchChange();
				}
				
				if( userDefinedVibrato.isSelected() != noteDesc.isUserDefinedVibrato() )
				{
					noteDesc.setUserDefinedVibrato( userDefinedVibrato.isSelected() );
					SongListeners.updateViewPanes();
				}
				
				if( userDefinedEnd.isSelected() != noteDesc.isUserDefinedEnd() )
				{
					noteDesc.setUserDefinedEnd( userDefinedEnd.isSelected() );
					SongListeners.updateViewPanes();
				}
				
			}

			
		//	EtherEvent send =
		//		new PropertyEditEtherEvent(
		//			this,
		//			PropertyEditEtherEvent.setPageSize,
		//			null,
		//			MyPage);
		//	send.setParameter(new Dimension((int) wid, (int) hei));
		//	ProgramDirector.fireEtherEvent(send, null);
		} catch (NumberFormatException ex) {
			handleThrow(
				new IllegalInputException(
					"Something input was not a number.",
					ex));
		} catch (Throwable ex) {
			handleThrow(ex);
		}
	}

	/**
	* Handles the throwing of an error or exception.
	*/
	public void handleThrow(Throwable in) {
		VerdantiumUtils.handleThrow(in, this, null);
	}

}
