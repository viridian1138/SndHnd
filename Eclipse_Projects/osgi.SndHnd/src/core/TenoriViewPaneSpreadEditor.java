




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
* A property editor for adding statistical variation to a tenori view to model human error in timing and pitch.
* <P>
* @author Thorn Green
*/
public class TenoriViewPaneSpreadEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The tenori view pane for which to edit the statistical variation.
	 */
	protected TenoriViewPane pane = null;
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton("Apply");
	
	/**
	 * Check box for editing whether to have statistical variation in pitch.
	 */
	protected JCheckBox spreadPitchOn = new JCheckBox();
	
	/**
	 * Text field for editing the size of the random variation of the pitch (in hertz).
	 */
	protected JTextField spreadPitchSize = new JTextField();
	
	/**
	 * Text field for editing the random number seed with respect to pitch.
	 */
	protected JTextField spreadPitchRandseed = new JTextField();
	
	/**
	 * Check box for editing whether to have statistical variation in time.
	 */
	protected JCheckBox spreadTimeOn = new JCheckBox();
	
	/**
	 * Text field for editing the size of the random variation of the time (in seconds).
	 */
	protected JTextField spreadTimeSize = new JTextField();
	
	/**
	 * Text field for editing the random number seed with respect to time.
	 */
	protected JTextField spreadTimeRandseed = new JTextField();
	
	/**
	 * Constructor.
	 * @param _pane The tenori view pane for which to edit the statistical variation.
	 */
	public TenoriViewPaneSpreadEditor(TenoriViewPane _pane ) {
		pane = _pane;
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", new JLabel( "Spread Pitch On:" ));
		pan2.add("any", spreadPitchOn);
		
		pan2.add("any", new JLabel("Spread Pitch Size (Cents) : "));
		pan2.add("any", spreadPitchSize);
		
		pan2.add("any", new JLabel( "Spread Pitch Randseed:" ));
		pan2.add("any", spreadPitchRandseed);
		
		pan2.add("any", new JLabel( "Spread Time On:" ));
		pan2.add("any", spreadTimeOn);
		
		pan2.add("any", new JLabel("Spread Time Size (Seconds) : "));
		pan2.add("any", spreadTimeSize);
		
		pan2.add("any", new JLabel( "Spread Time Randseed:" ));
		pan2.add("any", spreadTimeRandseed);
		
		spreadPitchOn.setSelected( pane.isSpreadPitchOn() );
		
		spreadPitchSize.setText( "" + ( pane.getSpreadPitchSize() ) );
		
		spreadPitchRandseed.setText( "" + ( pane.getSpreadPitchRandseed() ) );
		
		spreadTimeOn.setSelected( pane.isSpreadTimeOn() );
		
		spreadTimeSize.setText( "" + ( pane.getSpreadTimeSize() ) );
		
		spreadTimeRandseed.setText( "" + ( pane.getSpreadTimeRandseed() ) );

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
	* the statistical variation in the tenori view pane.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			
			if( e.getSource() == applyButton )
			{
				pane.setSpreadPitchOn( spreadPitchOn.isSelected() );
				pane.setSpreadPitchSize( Double.parseDouble( spreadPitchSize.getText() ) );
				pane.setSpreadPitchRandseed( Long.parseLong( spreadPitchRandseed.getText() ) );
				pane.setSpreadTimeOn( spreadTimeOn.isSelected() );
				pane.setSpreadTimeSize( Double.parseDouble( spreadTimeSize.getText() ) );
				pane.setSpreadTimeRandseed( Long.parseLong( spreadTimeRandseed.getText() ) );
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

