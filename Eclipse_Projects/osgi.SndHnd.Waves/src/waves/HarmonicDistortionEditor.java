





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
* A property editor for editing a GHarmonicDistortionWaveForm node.
* <P>
* @author Thorn Green
*/
public class HarmonicDistortionEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The input waveform.
	 */
	protected GHarmonicDistortionWaveForm waveForm = null;
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack track = null;
	
	/**
	 * Text field for editing the amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 */
	protected JTextField distortionCoeff = new JTextField();
	
	/**
	 * Text field for editing the maximum number of harmonics to generate.
	 */
	protected JTextField maxHarmonicNum = new JTextField();
	
	/**
	 * Text field for editing the amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 */
	protected JTextField subDistortionCoeff = new JTextField();
	
	/**
	 * Text field for editing the maximum number of subharmonics to generate.
	 */
	protected JTextField maxSubHarmonicNum = new JTextField();
	
	/**
	 * Check box for editing whether to generate odd harmonics and/or subharmonics.
	 */
	protected JCheckBox oddHarmonics = new JCheckBox( "Odd Harmonics" );
	
	/**
	 * Check box for editing whether to generate even harmonics and/or subharmonics.
	 */
	protected JCheckBox evenHarmonics = new JCheckBox( "Even Harmonics" );
	
	/**
	 * Check box for editing whether to calculate a sum or harmonic magnitudes to normalize the result.
	 */
	protected JCheckBox useDivisor = new JCheckBox( "Use Divisor" );

	
	/**
	* Constructs the property editor for a given GHarmonicDistortionWaveForm..
	* @param in The input GHarmonicDistortionWaveForm.
	* @param _ins The associated instrument track.
	*/
	public HarmonicDistortionEditor(GHarmonicDistortionWaveForm in , InstrumentTrack ins2 ) {
		waveForm = in;
		track = ins2;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Distortion Coeff : "));
		pan2.add("any", distortionCoeff);
		pan2.add("any", new JTextField("Max Harmonic Num : "));
		pan2.add("any", maxHarmonicNum);
		pan2.add("any", new JLabel("Sub Distortion Coeff : "));
		pan2.add("any", subDistortionCoeff);
		pan2.add("any", new JTextField("Max Sub Harmonic Num : "));
		pan2.add("any", maxSubHarmonicNum);
		pan2.add("any", oddHarmonics);
		pan2.add("any", evenHarmonics);
		pan2.add("any", useDivisor);
		
		distortionCoeff.setText( "" + waveForm.getFirstHarmonicDistortion() );
		maxHarmonicNum.setText("" + waveForm.getMaxHarmonicNum());
		subDistortionCoeff.setText( "" + waveForm.getFirstSubHarmonicDistortion() );
		maxSubHarmonicNum.setText("" + waveForm.getMaxSubHarmonicNum());
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
	* the edited GHarmonicDistortionWaveForm.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setFirstHarmonicDistortion( Double.parseDouble( distortionCoeff.getText() ) );
			
			waveForm.setMaxHarmonicNum( Integer.parseInt( maxHarmonicNum.getText() ) );
			
			waveForm.setFirstSubHarmonicDistortion( Double.parseDouble( subDistortionCoeff.getText() ) );
			
			waveForm.setMaxSubHarmonicNum( Integer.parseInt( maxSubHarmonicNum.getText() ) );
			
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


