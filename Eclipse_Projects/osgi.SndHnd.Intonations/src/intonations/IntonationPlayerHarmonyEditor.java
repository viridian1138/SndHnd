





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
import core.NoteTable;



/**
* A property editor for editing a GIntonationPlayerHarmony node.
* <P>
* @author Thorn Green
*/
public class IntonationPlayerHarmonyEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The node being edited.
	 */
	protected GIntonationPlayerHarmony waveForm = null;
	
	/**
	 * Combo box for selecting the index on the scale at which to start playing for the ascending scale.
	 */
	protected JComboBox<String> ind = new JComboBox<String>();
	
	/**
	 * Text field for editing the number of the melodic interval in which to play the intonation for the ascending scale.
	 */
	protected JTextField melodicIntervalNumber = new JTextField();
	
	/**
	 * Combo box for selecting the index on the scale at which to start playing for the descending scale.
	 */
	protected JComboBox<String> ind2 = new JComboBox<String>();
	
	/**
	 * Text field for editing the number of the melodic interval in which to play the intonation for the descending scale.
	 */
	protected JTextField melodicIntervalNumber2 = new JTextField();
	

	
	/**
	* Constructs the property editor for a given GIntonationPlayerHarmony.
	* @param in The input GIntonationPlayerHarmony.
	*/
	public IntonationPlayerHarmonyEditor(GIntonationPlayerHarmony in ) {
		waveForm = in;
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
		
		final String[] names = in.getI1().getScaleNames();
		int count;
		for( count = 0 ; count < names.length ; count++ )
		{
			ind.addItem( names[ count ] );
			ind2.addItem( names[ count ] );
		}
		
		ind.setSelectedIndex( in.getInd() );
		
		melodicIntervalNumber.setText( "" + ( waveForm.getMelodicIntervalNumber() ) );
		
		ind2.setSelectedIndex( in.getInd2() );
		
		melodicIntervalNumber2.setText( "" + ( waveForm.getMelodicIntervalNumber2() ) );
		
		JButton PlayButton = new JButton( "Play" );
		pan2.add( "any" , PlayButton );
		
		JButton PlayOneButton = new JButton( "Play One" );
		pan2.add( "any" , PlayOneButton );
		
		PlayButton.addActionListener( new ActionListener()
		{
			
			/**
			 * Handles the pressing of the "Play" button by playing the intonations.
			 */
			public void actionPerformed( ActionEvent ee )
			{
				final int indi = ind.getSelectedIndex();
				final int melodicIntervalNumber = Integer.parseInt( IntonationPlayerHarmonyEditor.this.melodicIntervalNumber.getText() );
				final int indi2 = ind2.getSelectedIndex();
				final int melodicIntervalNumber2 = Integer.parseInt( IntonationPlayerHarmonyEditor.this.melodicIntervalNumber2.getText() );
				final double[] inton = waveForm.getI1().genInton( new HashMap() ).calcIntonation();
				
				final int[] offsets = waveForm.getI1().genPriScaleIndices();
				final double melr = waveForm.getI1().genInton( new HashMap() ).getMelodicIntervalRatio();
				
				final double[] scale = new double[ offsets.length + 1 ];
				
				int cnta;
				for( cnta = 0 ; cnta < offsets.length ; cnta++ )
				{
					scale[ cnta ] = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber, indi + offsets[ cnta ] - offsets[ 0 ] , inton );
					if( cnta > 0 )
					{
						while( scale[ cnta ] > melr * scale[ 0 ] )
						{
							scale[ cnta ] = ( 1.0 / melr ) * scale[ cnta ];
						}
						while( scale[ cnta ] < scale[ cnta - 1 ] )
						{
							scale[ cnta ] = melr * scale[ cnta ];
						}
					}
				}
				
				scale[ offsets.length ] = melr * scale[ 0 ];
				
				final double sc2 = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber2, indi2 , inton );
				
				try
				{
					int count;
					for( count = 0 ; count < scale.length ; count++ )
					{
						NoteTable.playNotes( scale[ count ] , sc2 );
						Thread.sleep( 50 );
					}
					for( count = scale.length - 1 ; count > -1 ; count-- )
					{
						NoteTable.playNotes( scale[ count ] , sc2 );
						Thread.sleep( 50 );
					}
				}
				catch( Throwable ex )
				{
					ex.printStackTrace( System.out );
				}
				
			}
			
		});
		
		
		PlayOneButton.addActionListener( new ActionListener()
		{
			
			/**
			 * Handles the pressing of the "Play One" button by playing just the first tone of the intonations.
			 */
			public void actionPerformed( ActionEvent ee )
			{
				final int indi = ind.getSelectedIndex();
				final int melodicIntervalNumber = Integer.parseInt( IntonationPlayerHarmonyEditor.this.melodicIntervalNumber.getText() );
				final int indi2 = ind2.getSelectedIndex();
				final int melodicIntervalNumber2 = Integer.parseInt( IntonationPlayerHarmonyEditor.this.melodicIntervalNumber2.getText() );
				final double[] inton = waveForm.getI1().genInton( new HashMap() ).calcIntonation();
				
				final int[] offsets = waveForm.getI1().genPriScaleIndices();
				
				
				final double sc = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber, indi , inton );
				
				final double sc2 = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber2, indi2 , inton );
				
				try
				{
					NoteTable.playNotes( sc , sc2 );
					Thread.sleep( 50 );
				}
				catch( Throwable ex )
				{
					ex.printStackTrace( System.out );
				}
				
			}
			
		});

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
	* the edited GIntonationPlayerHarmony.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setInd( ind.getSelectedIndex() );
			waveForm.setMelodicIntervalNumber( Integer.parseInt( melodicIntervalNumber.getText() ) );
			
			waveForm.setInd2( ind2.getSelectedIndex() );
			waveForm.setMelodicIntervalNumber2( Integer.parseInt( melodicIntervalNumber2.getText() ) );

			
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
