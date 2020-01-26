




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
* A property editor for editing a GHarmonicDistortionSampleWaveForm node.
* <P>
* @author Thorn Green
*/
public class HarmonicDistortionSampleEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The input waveform.
	 */
	protected GHarmonicDistortionSampleWaveForm waveForm = null;
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack track = null;
	
	/**
	 * Text field for editing the amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 */
	protected JTextField firstHarmonicDistortion = new JTextField();
	
	/**
	 * Text field for editing the maximum harmonic number for which to add harmonic distortions. 
	 */
	protected JTextField maxHarmonicNum = new JTextField();
	
	/**
	 * Check-box for editing whether to add odd harmonics.
	 */
	protected JCheckBox oddHarmonics = new JCheckBox( "Odd Harmonics" );
	
	/**
	 * Check-box for editing whether to add even harmonics.
	 */
	protected JCheckBox evenHarmonics = new JCheckBox( "Even Harmonics" );
	
	/**
	 * Check-box for editing whether to use a divisor to normalize the amplitude of the resulting wave.
	 */
	protected JCheckBox useDivisor = new JCheckBox( "Use Divisor" );

	
	/**
	* Constructs the property editor for a given GHarmonicDistortionSampleWaveForm.
	* @param in The input GHarmonicDistortionSampleWaveForm.
	* @param _ins The associated instrument track.
	*/
	public HarmonicDistortionSampleEditor(GHarmonicDistortionSampleWaveForm in , InstrumentTrack ins2 ) {
		waveForm = in;
		track = ins2;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("First Harmonic Distortion : "));
		pan2.add("any", firstHarmonicDistortion);
		pan2.add("any", new JTextField("Max Harmonic Num : "));
		pan2.add("any", maxHarmonicNum);
		pan2.add("any", oddHarmonics);
		pan2.add("any", evenHarmonics);
		pan2.add("any", useDivisor);
		
		firstHarmonicDistortion.setText( "" + waveForm.getFirstHarmonicDistortion() );
		maxHarmonicNum.setText("" + waveForm.getMaxHarmonicNum());
		oddHarmonics.setSelected( waveForm.isOddHarmonics() );
		evenHarmonics.setSelected( waveForm.isEvenHarmonics() );
		useDivisor.setSelected( waveForm.isUseDivisor() );

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
	* the edited GHarmonicDistortionSampleWaveForm.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setFirstHarmonicDistortion( Double.parseDouble( firstHarmonicDistortion.getText() ) );
			
			waveForm.setMaxHarmonicNum( Integer.parseInt( maxHarmonicNum.getText() ) );
			
			waveForm.setOddHarmonics( oddHarmonics.isSelected() );
			
			waveForm.setEvenHarmonics( evenHarmonics.isSelected() );
			
			waveForm.setUseDivisor( useDivisor.isSelected() );
			
			
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

