




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








package core;


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



/**
* A property editor for inserting the tenori-generated motif into the current song.
* <P>
* @author Thorn Green
*/
public class TenoriViewPaneInsertEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The tenori view pane containing the motif to be inserted.
	 */
	protected TenoriViewPane pane = null;
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton("Apply");
	
	/**
	 * Text field for editing the measure number at which to begin inserting notes from the tenori.  Measure numbers start at zero.
	 */
	protected JTextField startMeasure = new JTextField( "2" );
	
	/**
	 * Text field for editing the beat number in the measure at which to begin inserting notes from the tenori.  Beat numbers start at zero.
	 */
	protected JTextField startBeatPerMeasure = new JTextField( "1.0" );
	
	/**
	 * Text field for editing the number of times the tenori is repeated during the insertion.
	 */
	protected JTextField numRepeats = new JTextField( "3" );
	
	/**
	 * Combo box for selecting whether the inserted notes have user-defined ends, as opposed to agent-defined ends.
	 */
	protected JCheckBox userDefinedEnd = new JCheckBox( "User Defined End" , true );
	
	/**
	 * Constructor.
	 * @param _pane The tenori view pane containing the motif to be inserted.
	 */
	public TenoriViewPaneInsertEditor(TenoriViewPane _pane ) {
		pane = _pane;
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", new JLabel( "Start Measure:" ));
		pan2.add("any", startMeasure);
		
		pan2.add("any", new JLabel("Start Beat Per Measure : "));
		pan2.add("any", startBeatPerMeasure);
		
		pan2.add("any", new JLabel( "Num Repeats:" ));
		pan2.add("any", numRepeats);
		
		pan2.add("any", userDefinedEnd);

		applyButton.addActionListener(this);
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
	* Handles a button-press event from the Apply button by inserting
	* notes from the tenori view pane into the song.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			
			if( e.getSource() == applyButton )
			{
				int startMeasurei = Integer.parseInt( startMeasure.getText() );
				double startBeatPerMeasurei = Double.parseDouble( startBeatPerMeasure.getText() );
				int numRepeatsi = Integer.parseInt( numRepeats.getText() );
				boolean userDefinedEndi = userDefinedEnd.isSelected();
				pane.insertNotes( startMeasurei , startBeatPerMeasurei , 
						numRepeatsi , userDefinedEndi );
			}

			
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
