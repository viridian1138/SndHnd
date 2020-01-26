




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
* A property editor for editing the number of rows and 
* columns in the tenori view.
* <P>
* @author Thorn Green
*/
public class TenoriViewPaneGridEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The tenori view pane for which to edit the number of rows and columns.
	 */
	protected TenoriViewPane pane = null;
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton("Apply");
	
	/**
	 * Text field for editing the number of beats to be contained by the tenori columns.
	 */
	protected JTextField numBeats = new JTextField();
	
	/**
	 * Text field for editing the number of tenori columns for each beat.
	 */
	protected JTextField columnsPerBeat = new JTextField();
	
	/**
	 * Text field for editing the number of vertical levels in the tenori view.
	 */
	protected JTextField numLevels = new JTextField();
	
	
	/**
	 * Constructor.
	 * @param _pane The tenori view pane for which to edit the number of rows and columns.
	 */
	public TenoriViewPaneGridEditor(TenoriViewPane _pane ) {
		pane = _pane;
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", new JLabel( "Num Beats:" ));
		pan2.add("any", numBeats);
		
		pan2.add("any", new JLabel("Columns Per Beat : "));
		pan2.add("any", columnsPerBeat);
		
		pan2.add("any", new JLabel( "Num Levels:" ));
		pan2.add("any", numLevels);
		
		numBeats.setText( "" + ( pane.getNumBeats() ) );
		
		columnsPerBeat.setText( "" + ( pane.getColumnsPerBeat() ) );
		
		numLevels.setText( "" + ( pane.getNumLevels() ) );

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
	* Handles a button-press event from the Apply button by changing
	* the set of rows and columns in the tenori view pane.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			
			if( e.getSource() == applyButton )
			{
				pane.setNumBeats( Integer.parseInt( numBeats.getText() ) );
				pane.setColumnsPerBeat( Integer.parseInt( columnsPerBeat.getText() ) );
				pane.setNumLevels( Integer.parseInt( numLevels.getText() ) );
				pane.refreshStates();
				pane.refreshKey();
				pane.refreshSquares();
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

