





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
* A property editor for editing a GIntonationPlayerTwoSide node.
* <P>
* @author Thorn Green
*/
public class IntonationPlayerTwoSideEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The node being edited.
	 */
	protected GIntonationPlayerTwoSide waveForm = null;
	
	/**
	 * Combo box for editing the index on the scale at which to start playing.
	 */
	protected JComboBox<String> ind = new JComboBox<String>();
	
	/**
	 * Text field for entering the number of the melodic interval in which to calculate the intonation.
	 */
	protected JTextField melodicIntervalNumber = new JTextField();
	

	
	/**
	* Constructs the property editor for a given GIntonationPlayerTwoSide.
	* @param in The input GIntonationPlayerTwoSide.
	*/
	public IntonationPlayerTwoSideEditor(GIntonationPlayerTwoSide in ) {
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
		
		final String[] names = in.getI1().getScaleNames();
		int count;
		for( count = 0 ; count < names.length ; count++ )
		{
			ind.addItem( names[ count ] );
		}
		
		ind.setSelectedIndex( in.getInd() );
		
		melodicIntervalNumber.setText( "" + ( waveForm.getMelodicIntervalNumber() ) );
		
		JButton PlayButton = new JButton( "Play" );
		pan2.add( "any" , PlayButton );
		
		PlayButton.addActionListener( new ActionListener()
		{
			
			/**
			 * Handles the pressing of the "Play" button by playing the intonation.
			 */
			public void actionPerformed( ActionEvent ee )
			{
				final int indiA = ind.getSelectedIndex();
				final int melodicIntervalNumber = Integer.parseInt( IntonationPlayerTwoSideEditor.this.melodicIntervalNumber.getText() );
				final String selA = (String)( ind.getSelectedItem() );
				final double[] intonA = waveForm.getI1().genInton( new HashMap() ).calcIntonation();
				final double[] intonB = waveForm.getI2().genInton( new HashMap() ).calcIntonation();
				
				final int[] offsetsA = waveForm.getI1().genPriScaleIndices();
				final int[] offsetsB = waveForm.getI2().genPriScaleIndices();
				
				final double[] scaleA = new double[ offsetsA.length + 1 ];
				final double[] scaleB = new double[ offsetsB.length + 1 ];
				
				final double melr = waveForm.getI1().genInton( new HashMap() ).getMelodicIntervalRatio();
				
				int indiB = -1;
				final String[] scaleNamesB = waveForm.getI2().getScaleNames();
				int cntx;
				for( cntx = 0 ; cntx < scaleNamesB.length ; cntx++ )
				{
					if( selA.equals( scaleNamesB[ cntx ] ) )
					{
						indiB = cntx;
					}
				}
				if( indiB < 0 )
				{
					throw( new RuntimeException( "NotSupported" ) );
				}
				
				int cnta;
				for( cnta = 0 ; cnta < offsetsA.length ; cnta++ )
				{
					scaleA[ cnta ] = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber, indiA + offsetsA[ cnta ] - offsetsA[ 0 ] , intonA );
					if( cnta > 0 )
					{
						while( scaleA[ cnta ] > melr * scaleA[ 0 ] )
						{
							scaleA[ cnta ] = ( 1.0 / melr ) * scaleA[ cnta ];
						}
						while( scaleA[ cnta ] < scaleA[ cnta - 1 ] )
						{
							scaleA[ cnta ] = melr * scaleA[ cnta ];
						}
					}
				}
				
				for( cnta = 0 ; cnta < offsetsB.length ; cnta++ )
				{
					scaleB[ cnta ] = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber, indiB + offsetsB[ cnta ] - offsetsB[ 0 ] , intonB );
					if( cnta > 0 )
					{
						while( scaleB[ cnta ] > melr * scaleB[ 0 ] )
						{
							scaleB[ cnta ] = ( 1.0 / melr ) * scaleB[ cnta ];
						}
						while( scaleB[ cnta ] < scaleB[ cnta - 1 ] )
						{
							scaleB[ cnta ] = melr * scaleB[ cnta ];
						}
					}
				}
				
				scaleA[ offsetsA.length ] = melr * scaleA[ 0 ];
				scaleB[ offsetsB.length ] = melr * scaleB[ 0 ];
				
				try
				{
					int count;
					for( count = 0 ; count < scaleA.length ; count++ )
					{
						NoteTable.playNote( scaleA[ count ] );
						Thread.sleep( 50 );
					}
					for( count = scaleB.length - 1 ; count > -1 ; count-- )
					{
						NoteTable.playNote( scaleB[ count ] );
						Thread.sleep( 50 );
					}
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
	* the edited GIntonationPlayerTwoSide.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setInd( ind.getSelectedIndex() );
			waveForm.setMelodicIntervalNumber( Integer.parseInt( melodicIntervalNumber.getText() ) );

			
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
