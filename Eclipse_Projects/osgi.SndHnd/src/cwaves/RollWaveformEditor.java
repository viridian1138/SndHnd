




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







package cwaves;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;
import core.InstrumentTrack;



/**
* A property editor for editing a GRollWaveform node.
* <P>
* @author Thorn Green
*/
public class RollWaveformEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The input waveform.
	 */
	protected GRollWaveform waveForm = null;
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack track = null;
	
	/**
	 * Text field for editing the proportion into the wave period at which to put the trough.
	 */
	protected JTextField atk = new JTextField();
	
	/**
	 * Text field for editing the exponential decay rate going from the peak to a trough to the right of the peak.
	 */
	protected JTextField powa = new JTextField();
	
	/**
	 * Text field for editing the exponential decay rate going from the peak to a trough to the left of the peak.
	 */
	protected JTextField powb = new JTextField();

	
	/**
	* Constructs the property editor for a given GRollWaveform.
	* @param in The input GRollWaveform.
	* @param _ins The associated instrument track.
	*/
	public RollWaveformEditor(GRollWaveform in , InstrumentTrack ins2 ) {
		waveForm = in;
		track = ins2;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Atk : "));
		pan2.add("any", atk);
		pan2.add("any", new JTextField("Powa : "));
		pan2.add("any", powa);
		pan2.add("any", new JLabel("Powb : "));
		pan2.add("any", powb);
		
		atk.setText( "" + waveForm.getAtk() );
		powa.setText("" + waveForm.getPowa());
		powb.setText( "" + waveForm.getPowb() );

		ApplyButton.addActionListener(this);
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
	* the edited GRollWaveform.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setAtk( Double.parseDouble( atk.getText() ) );
			
			waveForm.setPowa( Double.parseDouble( powa.getText() ) );
			
			waveForm.setPowb( Double.parseDouble( powb.getText() ) );
			
			
			track.updateTrackFrames( core );

			
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
