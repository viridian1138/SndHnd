




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
 * Editor for vibrato parameters.
 * 
 * Note: this is an editor for "generic" vibrato, which can include either pitch vibrato and/or tremolo.
 * 
 * @author tgreen
 *
 */
public class VibratoParamsEditor extends PropertyEditAdapter implements
	ActionListener {

	/**
	 * The vibrato parameters to be edited.
	 */
	VibratoParameters params = null;
	
	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * Text field for editing the number of beats to delay before starting the vibrato.
	 */
	protected JTextField delayBeats = new JTextField( "xxx" );
	
	/**
	 * Text field for editing the number of beats from the start of the vibrato to the "creschendo" where the vibrato has full effect.
	 */
	protected JTextField creschenBeats = new JTextField( "xxx" );
	
	/**
	 * Text field for editing 
	 * the percentage (ratio) by which to alter the pitch for pitch vibrato.
	 * This is on a ratio scale where 0.0 means no change, and 1.0 means a
	 * pitch variation between zero on the trough and one octave on the peak.
	 */
	protected JTextField stripPctFreq = new JTextField( "xxx" );
	
	/**
	 * Text field for editing the percentage (ratio) by which to alter the envelope for tremolo.
	 */
	protected JTextField stripPctEnv = new JTextField( "xxx" );
	
	/**
	 * Text field for editing
	 * the minimum number of vibrato cycles to have in a note.  If the number of
	 * vibrato cycles in the note isn't at least this number then the vibrato is skipped.
	 */
	protected JTextField minCyclesToStart = new JTextField( "xxx" );
	
	/**
	 * Text field for editing
	 * the amount for which to slide the initial pitch for pitch vibrato.
	 * A value of 0.0 means no such slide.  A value of -1.0 means the 
	 * peak of the pitch vibrato hits the initial pitch.
	 */
	protected JTextField pitchSlide = new JTextField( "xxx" );
	
	/**
	 * The mode of the vibrato (e.g. whether to use pitch vibrato or tremolo).
	 */
	protected JComboBox vibratoMode = new JComboBox();
	
	/**
	 * The apply button for applying the changes.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	/**
	 * Constructor.
	 * @param _params The vibrato parameters to be edited.
	 */
	public VibratoParamsEditor( VibratoParameters _params ) {
		super();
		params = _params;
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		vibratoMode.addItem( "None" );
		vibratoMode.addItem( "Tremolo" );
		vibratoMode.addItem( "Pitch Vibrato" );
		vibratoMode.addItem( "Tremolo And Pitch Vibrato" );
		
		delayBeats.setText( "" + params.getDelayBeats() );
		creschenBeats.setText( "" + params.getCreschenBeats() );
		stripPctFreq.setText( "" + params.getStripPctFreq() );
		stripPctEnv.setText( "" + params.getStripPctEnv() );
		minCyclesToStart.setText( "" + params.getMinCyclesToStart() );
		pitchSlide.setText( "" + params.getPitchSlide() );
		vibratoMode.setSelectedIndex( params.getVibratoMode() );

		pan2.add("any", new JLabel("Delay Beats : ") );
		pan2.add("any", delayBeats );
		pan2.add("any", new JLabel("CreschenBeats : ") );
		pan2.add("any", creschenBeats );
		pan2.add("any", new JLabel("Strip Pct Freq : ") );
		pan2.add("any", stripPctFreq );
		pan2.add("any", new JLabel("Strip Pct Env : ") );
		pan2.add("any", stripPctEnv );
		pan2.add("any", new JLabel("Min Cycles To Start : ") );
		pan2.add("any", minCyclesToStart );
		pan2.add("any", new JLabel("Pitch Slide : ") );
		pan2.add("any", pitchSlide );
		pan2.add("any", new JLabel("Vibrato Mode : ") );
		pan2.add("any", vibratoMode );
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
	 * Handles the pressing of the apply button.  Override this method to implement specific behavior.
	 */
	protected void handleApply()
	{
	}
	

	
	/**
	 * Handles a button-press event from the Apply button by setting the vibrato parameters.
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == applyButton) {
			try {
				
				
				params.setDelayBeats( Double.parseDouble( delayBeats.getText() ) );
				params.setCreschenBeats( Double.parseDouble( creschenBeats.getText() ) );
				params.setStripPctFreq( Double.parseDouble( stripPctFreq.getText() ) );
				params.setStripPctEnv( Double.parseDouble( stripPctEnv.getText() ) );
				params.setMinCyclesToStart( Double.parseDouble( minCyclesToStart.getText() ) );
				params.setPitchSlide( Double.parseDouble( pitchSlide.getText() ) );
				params.setVibratoMode( vibratoMode.getSelectedIndex() );
				
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

