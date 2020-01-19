





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
* A property editor for editing a GHarmonyPlayer node.
* <P>
* @author Thorn Green
*/
public class HarmonyPlayerEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The node being edited.
	 */
	protected GHarmonyPlayer waveForm = null;
	
	/**
	 * Combo box for editing the index on the scale at which to start playing.
	 */
	protected JComboBox ind = new JComboBox();
	
	/**
	 * Text field for entering the number of the melodic interval in which to calculate the harmony.
	 */
	protected JTextField melodicIntervalNumber = new JTextField();
	

	
	/**
	* Constructs the property editor for a given GHarmonyPlayer.
	* @param in The input GHarmonyPlayer.
	*/
	public HarmonyPlayerEditor(GHarmonyPlayer in ) {
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
		
		final String[] names = in.getI2().getScaleNames();
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
			 * Handles the pressing of the "Play" button by playing the harmony.
			 */
			public void actionPerformed( ActionEvent ee )
			{
				final int indi = ind.getSelectedIndex();
				final int melodicIntervalNumber = Integer.parseInt( HarmonyPlayerEditor.this.melodicIntervalNumber.getText() );
				final double[] inton = waveForm.getI2().genInton( new HashMap() ).calcIntonation();
				
				final double[] harmony = waveForm.getI1().genHarmony( new HashMap() ).calcHarmony();
				final double[] scale = new double[ harmony.length ];
				
				final double scale0 = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber, indi , inton );
				
				int cnta;
				for( cnta = 0 ; cnta < scale.length ; cnta++ )
				{
					scale[ cnta ] = harmony[ cnta ] * scale0;
				}
				
				try
				{
					int count;
					for( count = 0 ; count < scale.length ; count++ )
					{
						NoteTable.playNote( scale[ count ] );
						Thread.sleep( 50 );
					}
					Thread.sleep( 50 );
					NoteChannelEmulator[] ems = new NoteChannelEmulator[ scale.length ];
					for( count = 0 ; count < scale.length ; count++ )
					{
						ems[ count ] = NoteChannelEmulator.allocateEmulator();
					}
					for( count = 0 ; count < scale.length ; count++ )
					{
						double freqDiv = scale[ count ] / 261.6; // Divide frequency by middle C frequency.
						double flog2 = Math.log( freqDiv ) / Math.log( 2 );
						double fpitchI = flog2 * 12.0 + 60.0;
						ems[ count ].play( fpitchI );
					}
					Thread.sleep( 2500 );
					for( count = 0 ; count < scale.length ; count++ )
					{
						ems[ count ].stop();
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
	* the edited GHarmonyPlayer.
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

