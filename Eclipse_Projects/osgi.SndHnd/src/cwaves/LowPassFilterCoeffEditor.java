




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
* A property editor for editing a GLowPassFilterCoeff node.
* <P>
* @author Thorn Green
*/
public class LowPassFilterCoeffEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The input non-clamped coefficient.
	 */
	protected GLowPassFilterCoeff waveForm = null;
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack track = null;
	
	/**
	 * Text field for editing the half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 */
    protected JTextField intervalHalfLength = new JTextField();
	
    /**
     * Text field for editing the number of samples used to perform the filtering.
     */
	protected JTextField sampleLen = new JTextField();

	
	/**
	* Constructs the property editor for a given GLowPassFilterCoeff.
	* @param in The input GLowPassFilterCoeff.
	* @param _ins The associated instrument track.
	*/
	public LowPassFilterCoeffEditor(GLowPassFilterCoeff in , InstrumentTrack ins2 ) {
		waveForm = in;
		track = ins2;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Interval Half Length : "));
		pan2.add("any", intervalHalfLength);
		pan2.add("any", new JTextField("Sample Len : "));
		pan2.add("any", sampleLen);
		
		intervalHalfLength.setText( "" + waveForm.getIntervalHalfLength() );
		sampleLen.setText("" + waveForm.getSampleLen());

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
	* the edited GLowPassFilterCoeff.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setIntervalHalfLength( Double.parseDouble( intervalHalfLength.getText() ) );
			
			waveForm.setSampleLen( Integer.parseInt( sampleLen.getText() ) );
			
			
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
