





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
import core.NoteChannelEmulator;
import core.NoteTable;



/**
* A property editor for editing a GHarmonyMelodicIntervalDoubling node.
* <P>
* @author Thorn Green
*/
public class HarmonyMelodicIntervalDoublingEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The node being edited.
	 */
	protected GHarmonyMelodicIntervalDoubling waveForm = null;
	
	/**
	 * Text field for editing the string listing which indices of the harmony should be used.
	 */
	protected JTextField ind = new JTextField();
	
	/**
	 * Text field for editing the number of melodic intervals to jump upward when stacking.
	 */
	protected JTextField noteMelodicInterval = new JTextField();
	

	
	/**
	* Constructs the property editor for a given GHarmonyMelodicIntervalDoubling.
	* @param in The input GHarmonyMelodicIntervalDoubling.
	*/
	public HarmonyMelodicIntervalDoublingEditor(GHarmonyMelodicIntervalDoubling in ) {
		waveForm = in;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Start : "));
		pan2.add("any", ind);
		pan2.add("any", new JLabel( "Note MelodicInterval:" ));
		pan2.add("any", noteMelodicInterval);
		
		ind.setText( waveForm.getInd() );
		
		noteMelodicInterval.setText( "" + ( waveForm.getNoteMelodicInterval() ) );

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
	* the edited GHarmonyMelodicIntervalDoubling.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setInd( ind.getText() );
			waveForm.setNoteMelodicInterval( Integer.parseInt( noteMelodicInterval.getText() ) );

			
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

