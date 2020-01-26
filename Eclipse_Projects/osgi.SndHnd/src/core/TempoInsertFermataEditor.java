




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
 * Property editor for manually inserting a fermata.
 * 
 * @author tgreen
 *
 */
public class TempoInsertFermataEditor extends PropertyEditAdapter implements
	ActionListener {
	
	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * The tempo view pane into which to submit the fermata.
	 */
	protected TempoViewPane2 tvp = null;
	
	
	
	/**
	 * Text field for editing The measure number at which to insert the fermata.  Measure numbers start at zero.
	 */
	protected JTextField imeasureNumber = new JTextField( "3" );
	
	/**
	 * Text field for editing the beat number in the measure at which to insert the fermata.  Beat numbers start at zero.
	 */
	protected JTextField iibeatNumber = new JTextField( "0" ); 
	
	/**
	 * Text field for editing the number of seconds for the fermata to pause.
	 */
	protected JTextField inumSeconds = new JTextField( "3" );
	
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	/**
	 * Constructor.
	 * @param _tvp The tempo view pane into which to submit the fermata.
	 */
	public TempoInsertFermataEditor( TempoViewPane2 _tvp ) {
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
		
		pan2.add("any", new JLabel("Number Of Seconds To Pause : ") );
		pan2.add("any", inumSeconds );
		
		pan2.add("any", new JLabel("Fermata is inserted immediately before the beat. ") );
		
		
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
	 * Handles a button-press event from the Apply button by submitting a fermata.
	 */
	protected void handleApply()
	{
		final int measureNumber = Integer.parseInt( imeasureNumber.getText() );
		
		final double beatNumber = Double.parseDouble( iibeatNumber.getText() );
		
		final double numSeconds = Double.parseDouble( inumSeconds.getText() );
		
		tvp.insertFermata( measureNumber, beatNumber, numSeconds );
	}

	
	/**
	 * Handles a button-press event from the Apply button by submitting a fermata.
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

