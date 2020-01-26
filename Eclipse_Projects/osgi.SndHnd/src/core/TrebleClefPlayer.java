




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
import java.awt.FlowLayout;
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
 * A mostly tutorial GUI for playing normal treble-clef pitches.
 * 
 * @author tgreen
 *
 */
public class TrebleClefPlayer extends PropertyEditAdapter implements
	ActionListener {
	
	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * Text field for entering the number of sharps or flats in the key signature.
	 */
	protected JTextField numSharpOrFlat = new JTextField( "0" );
	
	/**
	 * Combo fox for selecting whether the key signature has sharps or flats.
	 */
	protected JComboBox sharpOrFlat = new JComboBox();
	
	
	/**
	 * The apply button for the editor.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	/**
	 * Constructor.
	 */
	public TrebleClefPlayer( ) {
		super();
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		sharpOrFlat.addItem( "Sharp" );
		sharpOrFlat.addItem( "Flat" );

		pan2.add("any", producePanel( "*C" ) );
		pan2.add("any", producePanel( "*b" ) );
		pan2.add("any", producePanel( "*A" ) );
		pan2.add("any", producePanel( "*g" ) );
		pan2.add("any", producePanel( "F" ) );
		pan2.add("any", producePanel( "e" ) );
		pan2.add("any", producePanel( "D" ) );
		pan2.add("any", producePanel( "c" ) );
		pan2.add("any", producePanel( "B" ) );
		pan2.add("any", producePanel( "a" ) );
		pan2.add("any", producePanel( "G" ) );
		pan2.add("any", producePanel( "f" ) );
		pan2.add("any", producePanel( "E" ) );
		
		pan2.add("any", new JLabel("Num Sharp Or Flat : ") );
		pan2.add("any", numSharpOrFlat );
		pan2.add("any", sharpOrFlat );
		
		
		pan2.add("any", applyButton);

		// applyButton.addActionListener(this);
	}
	
	/**
	 * Produces a panel for a note, the sharp of the note, and the flat of the note.
	 * @param label The label of the note.
	 * @return The generated panel.
	 */
	protected JPanel producePanel( String label )
	{
		JPanel p = new JPanel();
		p.setLayout( new FlowLayout() );
		p.add( produceButton( label ) );
		p.add( produceButton( label + "b" ) );
		p.add( produceButton( label + "#" ) );
		return( p );
	}
	
	/**
	 * Produces a button with a particular label.
	 * @param label The label to be added to the button.
	 * @return The generated button.
	 */
	protected JButton produceButton( String label )
	{
		JButton jb = new JButton( label );
		jb.addActionListener( this );
		return( jb );
	}
	
	
	/**
	 * Gets the GUI of the property editor.
	 */
	public JComponent getGUI() {
		return (MyPan);
	}

	/**
	 * Handles the destruction of the component by removing appropriate change
	 * listeners.
	 */
	public void handleDestroy() {
	}
	
	/**
	 * Handles the pressing of the "Apply" button by doing nothing.
	 */
	protected void handleApply()
	{
	}

	
	/**
	 * Handles a button-press by asserting the Apply button actions or by playing one of the treble clef notes.
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if( src != applyButton )
		{
			JButton jb = (JButton) src;
			String lab = jb.getText();
			if( lab.charAt( 0 ) == '*' )
			{
				lab = lab.substring( 1 );
			}
			int shp = Integer.parseInt( numSharpOrFlat.getText() );
			if( sharpOrFlat.getSelectedIndex() != 0 )
			{
				shp = - shp;
			}
			if( lab.endsWith( "b" ) && ( lab.length() > 1 ) )
				shp -= 1;
			if( lab.endsWith( "#" ) && ( lab.length() > 1 ) )
				shp += 1;
			char ch = lab.charAt( 0 );
			double freq = 0.0;
			double fa = 0.0;
			switch( ch )
			{
			case 'E':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_E + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_E );
				break;
			case 'f':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_F + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_F );
				break;
			case 'G':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_G + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_G );
				break;
			case 'a':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_A + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_A );
				break;
			case 'B':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_B + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 4 , NoteTable.STEPS_B );
				break;
			case 'c':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_C + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_C );
				break;
			case 'D':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_D + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_D );
				break;
			case 'e':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_E + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_E );
				break;
			case 'F':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_F + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_F );
				break;
			case 'g':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_G + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_G );
				break;
			case 'A':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_A + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_A );
				break;
			case 'b':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_B + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 5 , NoteTable.STEPS_B );
				break;
			case 'C':
				freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 6 , NoteTable.STEPS_C + shp );
				fa = NoteTable.getNoteFrequencyEqualTemperedScale_Intl( 6 , NoteTable.STEPS_C );
				break;
			default:
				throw( new RuntimeException( "Bad!!!!!" ) );
			}
			if( shp > 0 )
			{
				while( freq < fa )
				{
					freq = freq * 2.0;
				}
			}
			if( shp < 0 )
			{
				while( freq > fa )
				{
					freq = freq / 2.0;
				}
			}
			double freqDiv = freq / 261.6; // Divide frequency by middle C frequency.
			double flog2 = Math.log( freqDiv ) / Math.log( 2 );
			double fpitch = flog2 * 12.0 + 60.0;
			try
			{
				NoteChannelEmulator em = NoteChannelEmulator.allocateEmulator();
				em.play( fpitch );
				Thread.sleep( 250 );
				//noteChannel.reset();
				em.stop();
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
		}

		if (src == applyButton) {
			try {
				
				
				handleApply();

				// EtherEvent send =
				// new PropertyEditEtherEvent(
				// this,
				// PropertyEditEtherEvent.setPageSize,
				// null,
				// MyPage);
				// send.setParameter(new Dimension((int) wid, (int) hei));
				// ProgramDirector.fireEtherEvent(send, null);
			} catch (NumberFormatException ex) {
				handleThrow(new IllegalInputException(
						"Something input was not a number.", ex));
			} catch (Throwable ex) {
				handleThrow(ex);
			}
		}

	}

	/**
	 * Handles the throwing of an error or exception.
	 */
	public void handleThrow(Throwable in) {
		VerdantiumUtils.handleThrow(in, this, null);
	}

}
