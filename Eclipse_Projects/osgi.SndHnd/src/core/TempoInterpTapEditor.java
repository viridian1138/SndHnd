




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

import javax.swing.JButton;
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
 * Property editor for applying a tempo whose elapsed time per beat interpolates the taps recorded by the tap pad.  That is to say, the taps set the tempo /accelerando / ritardando / swing.
 * 
 * @author tgreen
 *
 */
public class TempoInterpTapEditor extends PropertyEditAdapter implements
	ActionListener {
	
	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * The tempo view pane into which to submit the tempo change.
	 */
	protected TempoViewPane2 tvp = null;
	
	
	
	/**
	 * Text field for editing The measure number at which to insert the tempo change.  Measure numbers start at zero.
	 */
	protected JTextField imeasureNumber = new JTextField( "3" );
	
	/**
	 * Text field for editing the beat number in the measure at which to insert the tempo change.  Beat numbers start at zero.
	 */
	protected JTextField iibeatNumber = new JTextField( "0" ); 
	
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	/**
	 * Constructor.
	 * @param _tvp The tempo view pane into which to submit the tempo change.
	 * @param _stBeat The estimated beat number from the initial tap.
	 */
	public TempoInterpTapEditor( TempoViewPane2 _tvp , double _stBeat ) {
		super();
		tvp = _tvp;
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		

		pan2.add("any", new JLabel("Measure Number : ") );
		pan2.add("any", imeasureNumber );
		
		pan2.add("any", new JLabel("Beat Number : ") );
		pan2.add("any", iibeatNumber );
		
		final int core = 0;
		final long stb = Math.round( _stBeat );
		final int measN = SongData.measuresStore.getMeasureNumberForBeatNumber( stb , core);
		final int beatN = SongData.measuresStore.getIntBeatOnMeasureForBeatNumber( stb , core);
		
		imeasureNumber.setText( "" + measN );
		iibeatNumber.setText( "" + beatN );
		
		
		pan2.add("any", applyButton);

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
	 * Handles a button-press event from the Apply button by submitting a tempo change.
	 */
	protected void handleApply()
	{
		final int measureNumber = Integer.parseInt( imeasureNumber.getText() );
		
		final int ibeatNumber = Integer.parseInt( iibeatNumber.getText() );
		
		final int stBeat = SongData.measuresStore.getBeatNumberForMeasureNumber(measureNumber)
			+ ( ibeatNumber );
		
		tvp.interpTempo( stBeat );
	}

	
	/**
	 * Handles a button-press event from the Apply button by submitting a tempo change.
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == applyButton) {
			try {
				
				
				handleApply();

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

