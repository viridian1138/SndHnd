





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







package intonations;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;



/**
* A property editor for editing a GTypicalMode node.
* <P>
* @author Thorn Green
*/
public class TypicalModeEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The intonation being edited.
	 */
	protected GTypicalMode waveForm = null;
	
	/**
	 * Combo box for selecting the kind of mode to be applied.
	 */
	protected JComboBox<String> ind = new JComboBox<String>();
	
	/**
	 * Combo box for selecting the key
	 * (for instance key of C#) of the intonation.
	 */
	protected JComboBox<String> key = new JComboBox<String>();
	

	
	/**
	* Constructs the property editor for a given GTypicalMode.
	* @param in The input GTypicalMode.
	*/
	public TypicalModeEditor(GTypicalMode in ) {
		waveForm = in;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Mode : "));
		pan2.add("any", ind);
		
		ind.addItem( "Major" );
		ind.addItem( "Natural Minor" );
		ind.addItem( "Harmonic Minor" );
		ind.addItem( "Melodic Minor Mode Ascending" );
		ind.addItem( "Melodic Minor Mode Descending" );
		ind.addItem( "Ionian" );
		ind.addItem( "Dorian" );
		ind.addItem( "Phrygian" );
		ind.addItem( "Lydian" );
		ind.addItem( "Mixolydian" );
		ind.addItem( "Aeolian" );
		ind.addItem( "Locrian" );
		ind.addItem( "Neapolitan Minor" );
		ind.addItem( "Neapolitan Major" );
		ind.addItem( "Neapolitan Dorian" );
		ind.addItem( "Neapolitan Mixolidian" );
		ind.addItem( "Enigmatic" );
		ind.addItem( "Minor Locrian" );
		ind.addItem( "Major Locrian" );
		ind.addItem( "Leading Whole-Tone" );
		ind.addItem( "Jazz Minor" );
		ind.addItem( "Blues Seven Note" );
		
		ind.setSelectedIndex( in.getMode() );
		
		pan2.add("any", new JLabel("Key : "));
		pan2.add("any", key);
		
		String[] keys = in.getI1().getPriScaleNames();
		int cnt;
		for( cnt = 0 ; cnt < keys.length ; cnt++ )
		{
			key.addItem( keys[ cnt ] );
		}
		
		key.setSelectedItem( in.getKey() );

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
	* the edited GTypicalMode.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setMode( ind.getSelectedIndex() );
			waveForm.setKey( (String)( key.getSelectedItem() ) );

			
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
