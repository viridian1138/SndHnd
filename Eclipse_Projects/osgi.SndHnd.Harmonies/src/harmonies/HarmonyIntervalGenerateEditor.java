





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







package harmonies;


import greditinton.GIntonation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

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
import core.Intonation;
import core.NoteTable;



/**
* A property editor for editing a HarmonyIntervalEditor by calculating a pitch ratio from the input intonation.
* <P>
* @author Thorn Green
*/
public class HarmonyIntervalGenerateEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The node being edited.
	 */
	protected HarmonyIntervalEditor in = null;
	
	/**
	 * The node being edited.
	 */
	protected GHarmonyInterval in2 = null;
	
	/**
	 * Combo box for selecting the index into the intonation for the tonic.
	 */
	protected JComboBox<String> ind = new JComboBox<String>();
	
	/**
	 * Text field for entering the number of the melodic interval for the tonic.
	 */
	protected JTextField melodicIntervalNumber = new JTextField();
	
	/**
	 * Combo box for selecting the index into the intonation for the harmony.
	 */
	protected JComboBox<String> ind2 = new JComboBox<String>();
	
	/**
	 * Text field for entering the number of the melodic interval for the harmony.
	 */
	protected JTextField melodicIntervalNumber2 = new JTextField();
	

	
	/**
	 * Constructs the property editor for a given HarmonyIntervalEditor.
	 * @param _in The input HarmonyIntervalEditor.
	 * @param _in2 The input GHarmonyInterval.
	 */
	public HarmonyIntervalGenerateEditor( HarmonyIntervalEditor _in , GHarmonyInterval _in2 ) {
		in = _in;
		in2 = _in2;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", new JLabel("Start : "));
		pan2.add("any", ind);
		pan2.add("any", new JLabel( "Melodic Interval Number:" ));
		pan2.add("any", melodicIntervalNumber);
		
		pan2.add("any", new JLabel("Start2 : "));
		pan2.add("any", ind2);
		pan2.add("any", new JLabel( "Melodic Interval Number2:" ));
		pan2.add("any", melodicIntervalNumber2);
		
		final GIntonation i = in2.getI1();
		
		final String[] names = i.getScaleNames();
		int count;
		for( count = 0 ; count < names.length ; count++ )
		{
			ind.addItem( names[ count ] );
			ind2.addItem( names[ count ] );
		}

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
	* the edited HarmonyIntervalEditor.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			final GIntonation i = in2.getI1();
			
			final int indi = ind.getSelectedIndex();
			final int melodicIntervalNumber = Integer.parseInt( HarmonyIntervalGenerateEditor.this.melodicIntervalNumber.getText() );
			final int indi2 = ind2.getSelectedIndex();
			final int melodicIntervalNumber2 = Integer.parseInt( HarmonyIntervalGenerateEditor.this.melodicIntervalNumber2.getText() );
			final double[] inton = i.genInton( new HashMap() ).calcIntonation();
			
			final double sc1 = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber, indi , inton );
			final double sc2 = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber2, indi2 , inton );
			
			in.setVal( sc2 / sc1 );

			
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

