





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







package noise;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.InstrumentTrack;

import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;



/**
* A property editor for editing a GVfbmWaveform node.
* <P>
* @author Thorn Green
*/
public class VfbmWaveformEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The waveform being edited.
	 */
	protected GVfbmWaveform waveForm = null;
	
	/**
	 * Text field for editing the input noise function from which the vfBm is generated.
	 */
	protected JTextField maxoctaves = new JTextField();
	
	/**
	 * Text field for editing the lacunarity parameter of the vfBm.
	 */
	protected JTextField lacunarity = new JTextField(); 
	
	/**
	 * Text field for editing the gain parameter of the vfBm.
	 */
	protected JTextField gain = new JTextField(); 
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack ins;
	

	
	/**
	* Constructs the property editor for a given GVfbmWaveform.
	* @param in The input GVfbmWaveform.
	* @param _ins The associated instrument track.
	*/
	public VfbmWaveformEditor(GVfbmWaveform in , InstrumentTrack _ins ) {
		waveForm = in;
		ins = _ins;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("maxoctaves : "));
		pan2.add("any", maxoctaves);
		pan2.add("any", new JLabel("lacunarity : "));
		pan2.add("any", lacunarity);
		pan2.add("any", new JLabel("gain : "));
		pan2.add("any", gain);
		
		maxoctaves.setText( "" + waveForm.getMaxoctaves() );
		lacunarity.setText( "" + waveForm.getLacunarity() );
		gain.setText( "" + waveForm.getGain() );

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
	* the edited GVfbmWaveform.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setMaxoctaves( Double.parseDouble( maxoctaves.getText() ) );
			
			waveForm.setLacunarity( Double.parseDouble( lacunarity.getText() ) );
			
			waveForm.setGain( Double.parseDouble( gain.getText() ) );
			
			ins.updateTrackFrames(core);

			
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

