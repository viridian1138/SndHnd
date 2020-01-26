




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


import grview.DraggableTransformNodeTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;

import javax.swing.*;

import aazon.builderNode.BuilderNode;
import palettes.PaletteClassesHarmony;
import aczon.AczonUnivAllocator;

import java.util.*;

import meta.WrapRuntimeException;
import verdantium.EtherEvent;
import verdantium.EtherEventHandler;
import verdantium.ProgramDirector;
import verdantium.StandardEtherEvent;
import verdantium.VerdantiumUtils;
import verdantium.undo.UndoManager;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;


/**
 * A property editor for editing a portamento transition.
 * <P>
 * 
 * @author Thorn Green
 */
public class PortamentoTransitionEditor extends PropertyEditAdapter implements
		ActionListener {

	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * The pitch ratio of the transition.
	 */
	protected JTextField pitchRatio = new JTextField( "xxx" );
	
	/**
	 * The time in seconds over which the portamento transitions.
	 */
	protected JTextField transitionTimeSeconds = new JTextField( "xxx" );
	
	/**
	 * Button for producing an editor for generating the pitch ratio
	 * for a portamento transition.  The ratio is generated through use of a harmony.
	 */
	protected JButton generateRatioButton = new JButton( "Generate Ratio" );
	
	/**
	 * Button for producing an editor for changing the pitch ratio
	 * for a portamento transition.  The ratio is generated through use of a harmony.
	 */
	protected JButton editRatioButton = new JButton( "Edit Ratio..." );
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	/**
	 * The portamento transition to be edited.
	 */
	PortamentoTransition transit;
	
	/**
	 * The instrument track containing the portamento transition to be edited.
	 */
	InstrumentTrack track;
	
	

	/**
	 * Constructor.
	 * @param _transit The portamento transition to be edited.
	 * @param _track The instrument track containing the portamento transition to be edited.
	 * @param _undoMgr The undo manager.
	 */
	public PortamentoTransitionEditor( PortamentoTransition _transit , InstrumentTrack _track , final UndoManager _undoMgr ) {
		transit = _transit;
		track = _track;
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pitchRatio.setText( "" + transit.getPitchRatio() );
		transitionTimeSeconds.setText( "" + transit.getTransitionTimeSeconds() );

		pan2.add("any", new JLabel("Pitch Ratio : ") );
		pan2.add("any", pitchRatio );
		pan2.add("any", generateRatioButton);
		pan2.add("any", editRatioButton);
		pan2.add("any", new JLabel("Transition Time Seconds : ") );
		pan2.add("any", transitionTimeSeconds );
		pan2.add("any", applyButton);

		applyButton.addActionListener(this);
		
		generateRatioButton.addActionListener( new ActionListener()
		{
			/**
			 * Handles the pressing of the "generate" button by
			 * producing the harmony editor for the pitch ratio.
			 */
			public void actionPerformed( ActionEvent e )
			{
				final Harmony h = TestPlayer2.editPackHarmony.processHarmony( new HashMap() );
				
				final double[] ratios = h.calcHarmony();
				
				pitchRatio.setText( "" + ( ratios[ 1 ] ) );
			}
		});
		
		editRatioButton.addActionListener( new ActionListener()
		{
			/**
			 * Handles the pressing of the "edit" button by
			 * producing the harmony editor for the pitch ratio.
			 */
			public void actionPerformed( ActionEvent e )
			{
				InstrumentTrack track = SongData.getCurrentTrack();
				HashSet<BuilderNode> s = TestPlayer2.editPackHarmony.getElem();
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				DraggableTransformNodeTest.start(univ1,univ2,s,track,_undoMgr,new PaletteClassesHarmony());
				SongListeners.updateViewPanes();
			}
		});
		
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
	 * Handles a button-press event from the Apply button by applying the
	 * change to the portamento transition.
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == applyButton) {
			try {
				
				final int core = 0;
				
				
				double pRatio = Double.parseDouble( pitchRatio.getText() );
				double trTime = Double.parseDouble( transitionTimeSeconds.getText() );
				transit.setPitchRatio( pRatio );
				transit.setTransitionTimeSeconds( trTime );
				
				track.updateTrackFrames( core );
				SongListeners.updateViewPanes();
				
				// outputChoice.updateNoteViewPane2(); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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

