




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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import verdantium.utils.VerticalLayout;


/**
 * Simple editor for allowing the user to select a pitch from the current scale.
 * 
 * @author tgreen
 *
 */
public class SimplePitchEdit extends JPanel {
	
	/**
	 * Constructor.
	 * @param _insField Text field for editing the pitch in hertz.
	 */
	public SimplePitchEdit( final JTextField _insField )
	{
		final JButton insertButton = new JButton( "Insert" );
		final JTextField melodicIntervalField = new JTextField( "2" );
		final JComboBox pitchBox = new JComboBox();
		
		int count = 0;
		String[] names = NoteTable.getScaleNamesDefaultScale_Key();
		int len = names.length;
		for( count = 0 ; count < len ; count++ )
		{
			pitchBox.addItem( names[ count ] );
		}
		
		/* melodicIntervalBox.setMinimumSize( insertButton.getMinimumSize() );
		pitchBox.setMinimumSize( insertButton.getMinimumSize() );
		melodicIntervalBox.setPreferredSize( insertButton.getPreferredSize() );
		pitchBox.setPreferredSize( insertButton.getPreferredSize() ); */
		
		setLayout(new VerticalLayout(1));
		add( "any" , new JLabel( "Insert Default Scale : " ) );
		add( "any" , new JLabel( "Melodic Interval : " ) );
		add( "any" , melodicIntervalField );
		add( "any" , new JLabel( "Note : " ) );
		add( "any" , pitchBox );
		add( "any" , insertButton );
		
		insertButton.addActionListener( new ActionListener()
		{
			/**
			 * Handles a button press by setting the pitch in hertz.
			 */
			public void actionPerformed( ActionEvent e )
			{
				try
				{
					int melodicIntervalNumber = Integer.parseInt( "" + ( melodicIntervalField.getText() ) );
					int pitchSteps = pitchBox.getSelectedIndex();
					double pitch = NoteTable.getNoteFrequencyDefaultScale_Key( melodicIntervalNumber , pitchSteps );
					_insField.setText( "" + pitch );
					_insField.repaint();
				}
				catch( Throwable ex )
				{
					ex.printStackTrace( System.out );
				}
			}
		} );
	}

	
}

