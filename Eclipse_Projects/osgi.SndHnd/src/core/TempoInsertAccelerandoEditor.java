




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
 * Property editor for manually inserting an accelerando or ritardando.
 * 
 * @author tgreen
 *
 */
public class TempoInsertAccelerandoEditor extends PropertyEditAdapter implements
	ActionListener {
	
	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * The tempo view pane into which to submit the accelerando or ritardando.
	 */
	protected TempoViewPane2 tvp = null;
	
	
	
	/**
	 * Text field for editing The measure number at which to start the accelerando or ritardando.  Measure numbers start at zero.
	 */
	protected JTextField imeasureNumberStrt = new JTextField( "3" );
	
	/**
	 * Text field for editing the beat number in the measure at which to start the accelerando or ritardando.  Beat numbers start at zero.
	 */
	protected JTextField iibeatNumberStrt = new JTextField( "0" ); 
	
	/**
	 * Text field for editing the measure number at which to end the accelerando or ritardando.  Measure numbers start at zero.
	 */
	protected JTextField imeasureNumberEnd = new JTextField( "5" );
	
	/**
	 * Text field for editing the beat number in the measure at which to end the accelerando or ritardando.  Beat numbers start at zero.
	 */
	protected JTextField iibeatNumberEnd = new JTextField( "0" );
	
	/**
	 * Text field for editing the tempo (in beats per minute) at which the accelerando or ritardando ends.
	 */
	protected JTextField ifinalBeatsPerMinute = new JTextField( "90.0" );
	
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	
	/**
	 * Constructor.
	 * @param _tvp The tempo view pane into which to submit the accelerando or ritardando.
	 */
	public TempoInsertAccelerandoEditor( TempoViewPane2 _tvp ) {
		super();
		tvp = _tvp;
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		

		pan2.add("any", new JLabel("Start Measure Number : ") );
		pan2.add("any", imeasureNumberStrt );
		
		pan2.add("any", new JLabel("Start Beat Number : ") );
		pan2.add("any", iibeatNumberStrt );
		
		pan2.add("any", new JLabel("End Measure Number : ") );
		pan2.add("any", imeasureNumberEnd );
		
		pan2.add("any", new JLabel("End Beat Number : ") );
		pan2.add("any", iibeatNumberEnd );
		
		pan2.add("any", new JLabel("Final Beats Per Minute : ") );
		pan2.add("any", ifinalBeatsPerMinute );
		
		
		
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
	 * Handles a button-press event from the Apply button by submitting an accelerando or ritardando.
	 */
	protected void handleApply()
	{
		final int measureNumberStrt = Integer.parseInt( imeasureNumberStrt.getText() );
		
		final double beatNumberStrt = Double.parseDouble( iibeatNumberStrt.getText() );
		
		final int measureNumberEnd = Integer.parseInt( imeasureNumberEnd.getText() );
		
		final double beatNumberEnd = Double.parseDouble( iibeatNumberEnd.getText() );
		
		final double finalBeatsPerMinute = Double.parseDouble( ifinalBeatsPerMinute.getText() );
		
		tvp.insertAccelerando( measureNumberStrt , beatNumberStrt , measureNumberEnd , beatNumberEnd , finalBeatsPerMinute );
	}

	
	/**
	 * Handles a button-press event from the Apply button by submitting an accelerando or ritardando.
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

