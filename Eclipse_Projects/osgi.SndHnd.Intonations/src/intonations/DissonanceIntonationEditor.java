





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







package intonations;


import grview.DraggableTransformNodeTest;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aazon.builderNode.BuilderNode;
import palettes.PaletteClassesWave;
import verdantium.VerdantiumUtils;
import verdantium.undo.UndoManager;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;
import aczon.AczonUnivAllocator;
import core.InstrumentTrack;



/**
* A property editor for editing a GDissonanceIntonation node.
* <P>
* @author Thorn Green
*/
public class DissonanceIntonationEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The intonation being edited.
	 */
	protected GDissonanceIntonation waveForm = null;
	
	/**
	 * Button for editing the waveform that stays at the base of the scale for the dissonance calculation.
	 */
	protected JButton editBase = new JButton( "Edit Base" );
	
	/**
	 * Button for editing the waveform that moves up the scale for the dissonance calculation.
	 */
	protected JButton editMoving = new JButton( "Edit Moving" );
	
	/**
	 * Button for applying changes to the intonation. 
	 */
	protected JButton applyButton = new JButton("Apply");
	
	/**
	 * Text field for entering the number of the melodic interval in which to calculate the intonation.
	 */
	protected JTextField melodicIntervalNumber = new JTextField();
	
	/**
	 * Combo box for selecting the name of the key in which to calculate the intonation.
	 */
	protected JComboBox<String> key = new JComboBox<String>();

	/**
	 * The context in which to evaluate EditPack instances.
	 */
	HashMap context;
	
	/**
	* Constructs the property editor for a given GDissonanceIntnation.
	* @param in The input GDissonanceIntonation.
	* @param _context The context in which to evaluate EditPack instances.
	*/
	public DissonanceIntonationEditor(GDissonanceIntonation in, HashMap _context ) {
		waveForm = in;
		context = _context;
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", editBase );
		pan2.add("any", editMoving );
		
		pan2.add("any", new JLabel( "Melodic Interval Number:" ));
		pan2.add("any", melodicIntervalNumber);
		
		pan2.add("any", new JLabel("Key : "));
		pan2.add("any", key);
		
		String[] keys = in.getI1().getScaleNames();
		int cnt;
		for( cnt = 0 ; cnt < keys.length ; cnt++ )
		{
			key.addItem( keys[ cnt ] );
		}
		
		melodicIntervalNumber.setText( "" + ( waveForm.getMelodicIntervalNumber() ) );
		key.setSelectedItem( in.getKey() );

		applyButton.addActionListener(this);
		editBase.addActionListener(this);
		editMoving.addActionListener(this);
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
	* Handles button-press events.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			if( e.getSource() == editBase )
			{
				HashSet<BuilderNode> s = waveForm.getWbase().getElem();
				InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
				UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
				if( /* ( track == null ) || */ ( undoMgr == null ) )
				{
					throw( new RuntimeException( "UndoIsNull" ) );
				}
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesWave());
			}
			
			if( e.getSource() == editMoving )
			{
				HashSet<BuilderNode> s = waveForm.getWmoving().getElem();
				InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
				UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
				if( /* ( track == null ) || */ ( undoMgr == null ) )
				{
					throw( new RuntimeException( "UndoIsNull" ) );
				}
				Object univ1 = AczonUnivAllocator.allocateUniv();
				Object univ2 = AczonUnivAllocator.allocateUniv();
				DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesWave());
			}
			
			if( e.getSource() == applyButton )
			{
				waveForm.setMelodicIntervalNumber( Integer.parseInt( melodicIntervalNumber.getText() ) );
				waveForm.setKey( (String)( key.getSelectedItem() ) );
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
