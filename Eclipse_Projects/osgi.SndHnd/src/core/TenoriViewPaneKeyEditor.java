




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
* A property editor for editing the key and tempo of the tenori view.
* 
* The possible selections for the "key" come from the current intonation,
* which could be a non-western intonation (depending on user selections).
* 
* <P>
* @author Thorn Green
*/
public class TenoriViewPaneKeyEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The tenori view pane for which to edit the key and the tempo.
	 */
	protected TenoriViewPane pane = null;
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton("Apply");
	
	/**
	 * Text field for editing the number of the melodic interval for the current key.  Melodic interval numbers start at zero.
	 */
	protected JTextField startMelodicInterval = new JTextField();
	
	/**
	 * Combo box for selecting the key.
	 * The possible selections for this combo box are a direct function of what the user has selected as an intonation.
	 */
	protected JComboBox key = new JComboBox();
	
	/**
	 * Text field for editing the tempo (in beats per minute).
	 */
	protected JTextField tempo = new JTextField();
	
	
	/**
	 * Constructor.
	 * @param _pane The tenori view pane for which to edit the key and the tempo.
	 */
	public TenoriViewPaneKeyEditor(TenoriViewPane _pane ) {
		pane = _pane;
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", new JLabel( "Start Melodic Interval Number:" ));
		pan2.add("any", startMelodicInterval);
		
		pan2.add("any", new JLabel("Key : "));
		pan2.add("any", key);
		
		pan2.add("any", new JLabel( "Tempo:" ));
		pan2.add("any", tempo);
		
		String[] keys = NoteTable.getScaleNamesDefaultScale_Key();
		int cnt;
		for( cnt = 0 ; cnt < keys.length ; cnt++ )
		{
			key.addItem( keys[cnt] );
		}
		
		startMelodicInterval.setText( "" + ( pane.getStartMelodicInterval() ) );
		
		try
		{
			key.setSelectedIndex( pane.getStartNote() );
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
		
		tempo.setText( "" + ( pane.getTempo() ) );

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
	* the key and the tempo of the tenori view pane.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			
			if( e.getSource() == applyButton )
			{
				pane.setStartMelodicInterval( Integer.parseInt( startMelodicInterval.getText() ) );
				pane.setTempo( Double.parseDouble( tempo.getText() ) );
				pane.setStartNote( key.getSelectedIndex() );
				pane.refreshKey();
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

