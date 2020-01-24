





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







package waves;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
* A property editor for editing a GOmniPresent node.
* <P>
* @author Thorn Green
*/
public class OmniPresentEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The input waveform.
	 */
	protected GOmniPresent waveForm = null;
	
	/**
	 * Text field for editing the contribution at the tonic.
	 */
	protected JTextField a1 = new JTextField();
	
	/**
	 * Text field for editing the contribution at 1 / 2 (i.e. one octave down).
	 */
	protected JTextField ao2 = new JTextField();
	
	/**
	 * Text field for editing the contribution at 1 / 4 (i.e. two octaves down).
	 */
	protected JTextField ao4 = new JTextField();
	
	/**
	 * Text field for editing the contribution at 1 / 8 (i.e. three octaves down).
	 */
	protected JTextField ao8 = new JTextField();
	
	/**
	 * Text field for editing the contribution at 1 / 16 (i.e. four octaves down).
	 */
	protected JTextField ao16 = new JTextField();
	
	/**
	 * Text field for editing the contribution at 1 / 32 (i.e. five octaves down).
	 */
	protected JTextField ao32 = new JTextField();
	
	/**
	 * Text field for editing the contribution at 1 / 64 (i.e. six octaves down).
	 */
	protected JTextField ao64 = new JTextField();
	
	/**
	 * Text field for editing the contribution at 1 / 128 (i.e. seven octaves down).
	 */
	protected JTextField ao128 = new JTextField();
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack ins;
	

	
	/**
	* Constructs the property editor for a given GOmniPresent.
	* @param in The input GOmniPresent.
	* @param _ins The associated instrument track.
	*/
	public OmniPresentEditor(GOmniPresent in, InstrumentTrack _ins ) {
		waveForm = in;
		ins = _ins;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("1 : "));
		pan2.add("any", a1);
		pan2.add("any", new JLabel("1/2 : "));
		pan2.add("any", ao2);
		pan2.add("any", new JLabel("1/4 : "));
		pan2.add("any", ao4);
		pan2.add("any", new JLabel("1/8 : "));
		pan2.add("any", ao8);
		pan2.add("any", new JLabel("1/16 : "));
		pan2.add("any", ao16);
		pan2.add("any", new JLabel("1/32 : "));
		pan2.add("any", ao32);
		pan2.add("any", new JLabel("1/64 : "));
		pan2.add("any", ao64);
		pan2.add("any", new JLabel("1/128 : "));
		pan2.add("any", ao128);
		
		a1.setText( "" + waveForm.getA1() );
		ao2.setText( "" + waveForm.getAo2() );
		ao4.setText( "" + waveForm.getAo4() );
		ao8.setText( "" + waveForm.getAo8() );
		ao16.setText( "" + waveForm.getAo16() );
		ao32.setText( "" + waveForm.getAo32() );
		ao64.setText( "" + waveForm.getAo64() );
		ao128.setText( "" + waveForm.getAo128() );

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
	* the edited GOmniPresent.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setA1( Double.parseDouble( a1.getText() ) );
			
			waveForm.setAo2( Double.parseDouble( ao2.getText() ) );
			
			waveForm.setAo4( Double.parseDouble( ao4.getText() ) );
			
			waveForm.setAo8( Double.parseDouble( ao8.getText() ) );
			
			waveForm.setAo16( Double.parseDouble( ao16.getText() ) );
			
			waveForm.setAo32( Double.parseDouble( ao32.getText() ) );
			
			waveForm.setAo64( Double.parseDouble( ao64.getText() ) );
			
			waveForm.setAo128( Double.parseDouble( ao128.getText() ) );
			
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
