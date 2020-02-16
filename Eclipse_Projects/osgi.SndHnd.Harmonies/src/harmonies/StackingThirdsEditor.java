





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



/**
* A property editor for editing a GHarmonyStackingThirds node.
* <P>
* @author Thorn Green
*/
public class StackingThirdsEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The node being edited.
	 */
	protected GHarmonyStackingThirds waveForm = null;
	
	/**
	 * Combo box for selecting the name of the key for the tonic of the harmony.
	 */
	protected JComboBox<String> key = new JComboBox<String>();
	
	/**
	 * Text field for editing the number of thirds to be stacked.
	 */
	protected JTextField numThirds = new JTextField();
	

	
	/**
	* Constructs the property editor for a given GHarmonyStackingThirds.
	* @param in The input GHarmonyStackingThirds.
	*/
	public StackingThirdsEditor(GHarmonyStackingThirds in ) {
		waveForm = in;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", new JLabel("Key : "));
		pan2.add("any", key);
		
		String[] keys = in.getI1().getPriScaleNames();
		int cnt;
		for( cnt = 0 ; cnt < keys.length ; cnt++ )
		{
			key.addItem( keys[ cnt ] );
		}
		
		pan2.add("any", new JLabel("Num Thirds : "));
		pan2.add("any", numThirds); 
		pan2.add( "any" , new JLabel( "2 --> Triad" ) );
		pan2.add( "any" , new JLabel( "3 --> Seventh Chord" ) );
		pan2.add( "any" , new JLabel( "4 --> Ninth Chord" ) );
		pan2.add( "any" , new JLabel( "5 --> Eleventh Chord" ) );
		pan2.add( "any" , new JLabel( "6 --> Thirteenth Chord" ) );
		
		key.setSelectedItem( waveForm.getFirstNoteKey() );
		
		numThirds.setText( "" + ( waveForm.getNumThirds() ) );

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
	* the edited GHarmonyStackingThirds.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setNumThirds( Integer.parseInt( numThirds.getText() ) );
			
			waveForm.setFirstNoteKey( (String)( key.getSelectedItem() ) );

			
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

