





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
* A property editor for editing a GCloselySpacedChord node.
* <P>
* @author Thorn Green
*/
public class CloselySpacedChordEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The waveform being edited.
	 */
	protected GCloselySpacedChord waveForm = null;
	
	/**
	 * Text field for editing the number of pitch-shifted copies of the input wave to use in building the chord.
	 */
	private JTextField waveCount = new JTextField();
	
	/**
	 * Check box for editing whether to include a copy of the original input waveform.
	 */
	private JCheckBox includeZeroWave = new JCheckBox();
	
	/**
	 * Text field for editing the frequency multiplier to be applied to produce each successive pitch shift.
	 */
	private JTextField initialFreqMultiplier = new JTextField();
	
	/**
	 * Text field for editing the amplitude multiplier to be applied upon each successive pitch shift.
	 */
	private JTextField amplitudeMultiplier = new JTextField();
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack ins;
	

	
	/**
	* Constructs the property editor for a given GCloselySpacedChord.
	* @param in The input GCloselySpacedChord.
	* @param _ins The associated instrument track.
	*/
	public CloselySpacedChordEditor(GCloselySpacedChord in , InstrumentTrack _ins) {
		waveForm = in;
		ins = _ins;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", new JLabel("Wave Count : "));
		pan2.add("any", waveCount);
		
		pan2.add("any", includeZeroWave);
		
		pan2.add("any", new JLabel("Initial Freq Multiplier : "));
		pan2.add("any", initialFreqMultiplier);
		
		pan2.add("any", new JLabel("Amplitude Multiplier : "));
		pan2.add("any", amplitudeMultiplier);
		
		waveCount.setText( "" + waveForm.getWaveCount() );
		
		includeZeroWave.setSelected( waveForm.isIncludeZeroWave() );
		
		initialFreqMultiplier.setText( "" + waveForm.getInitialFreqMultiplier() );
		
		amplitudeMultiplier.setText( "" + waveForm.getAmplitudeMultiplier() );

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
	* the edited GCloselySpacedChord.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setWaveCount( Integer.parseInt( waveCount.getText() ) );
			
			waveForm.setIncludeZeroWave( includeZeroWave.isSelected() );
			
			waveForm.setInitialFreqMultiplier( Double.parseDouble( initialFreqMultiplier.getText() ) );
			
			waveForm.setAmplitudeMultiplier( Double.parseDouble( amplitudeMultiplier.getText() ) );
			
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

