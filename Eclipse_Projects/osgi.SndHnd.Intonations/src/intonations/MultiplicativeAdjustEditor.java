





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

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
* A property editor for editing a GMultiplicativeAdjust node.
* 
* Several of the definitions here were taken from the book "Science And Music"
* by Sir James Jeans.
* <P>
* @author Thorn Green
*/
public class MultiplicativeAdjustEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The intonation being edited.
	 */
	protected GMultiplicativeAdjust waveForm = null;
	
	/**
	 * Text field for the pitch ratio to be applied.
	 */
	protected JTextField ratio = new JTextField();
	
	/**
	 * Check box used to indicate whether to use the multiplicative inverse of the number in the text field.
	 */
	protected JCheckBox invert = new JCheckBox( "Invert" );
	
	/**
	 * Combo box from which to select from default ratio settings.
	 */
	protected JComboBox ind = new JComboBox();
	
	
	public static final int RATIO_444 = 0;
	
	public static final int STRASSBURG_CATHEDRAL = 1;
	
	public static final int SCHNITGER_HAMBURG = 2;
	
	public static final int NORTHERN_GERMANY_CHURCH_PITCH = 3;
	
	public static final int TRINITY_COLLEGE_CAMBRIDGE = 4;
	
	public static final int DURHAM_CATHEDRAL = 5;
	
	public static final int COVENT_GARDEN_ORCHESTRA = 6;
	
	public static final int AMERICAN_CONCERT_PITCH = 7;
	
	public static final int FRENCH_GOVERNMENT_COMMISSION = 8;
	
	public static final int ONE_SHARP_DIATONIC = 9;
	
	public static final int ONE_SHARP_JUST = 10;
	
	public static final int ONE_SHARP_PYTHAGOREAN = 11;
	
	public static final int ONE_SHARP_QUARTER_COMMA_MEANTONE = 12;
	
	public static final int ENGLAND_STANDARD = 13;
	
	public static final int RATIO_441 = 14;
	
	public static final int RATIO_480 = 15;
	

	
	
	/**
	* Constructs the property editor for a given GMultiplicativeAdjust .
	* @param in The input GMultiplicativeAdjust.
	*/
	public MultiplicativeAdjustEditor(GMultiplicativeAdjust in ) {
		waveForm = in;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);
		
		final JButton set = new JButton( "Set" );

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Ratio : "));
		pan2.add("any", ratio);
		pan2.add("any", invert);
		pan2.add("any", new JLabel("Setting : "));
		pan2.add("any", ind);
		pan2.add("any", set);
		
		ind.addItem( "A=440 --> A=444 (You Know Who)" );
		ind.addItem( "A=440 --> A=393 (Strassburg Cathedral)" );
		ind.addItem( "A=440 --> A=489 (Schnitger Hamburg)" );
		ind.addItem( "A=440 --> A=567 (Northern Germany Church Pitch)" );
		ind.addItem( "A=440 --> A=395 (Trinity College Cambridge)" );
		ind.addItem( "A=440 --> A=474.1 (Durham Cathedral)" );
		ind.addItem( "A=440 --> A=450 (Covent Graden Orchestra)" );
		ind.addItem( "A=440 --> A=461.6 (American Concert Pitch)" );
		ind.addItem( "A=440 --> A=435 (French Government Commission)" );
		ind.addItem( "A=440 --> A=466.2 (One Sharp Diatonic)" );
		ind.addItem( "A=440 --> A=458.3 (One Sharp Just)" );
		ind.addItem( "A=440 --> A=463.5 (One Sharp Pythagorean)" );
		ind.addItem( "A=440 --> A=470.8 (One Sharp Quarter Comma Meantone)" );
		ind.addItem( "A=440 --> A=438.9 (England Standard)" );
		ind.addItem( "A=440 --> A=441.0 (Possibly Better For CD)" );
		ind.addItem( "A=440 --> A=480.0 (Possibly Better For DVD)" );
		
		
		ratio.setText( "" + ( in.getMultiplier() ) );
		invert.setSelected( in.isInvertM() );
		
		set.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				final int idx = ind.getSelectedIndex();
				
				switch( idx )
				{
					case RATIO_444:
					{
						ratio.setText( "" + ( 444.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case STRASSBURG_CATHEDRAL:
					{
						ratio.setText( "" + ( 393.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case SCHNITGER_HAMBURG:
					{
						ratio.setText( "" + ( 489.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case NORTHERN_GERMANY_CHURCH_PITCH:
					{
						ratio.setText( "" + ( 567.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case TRINITY_COLLEGE_CAMBRIDGE:
					{
						ratio.setText( "" + ( 395.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case DURHAM_CATHEDRAL:
					{
						ratio.setText( "" + ( 474.1 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case COVENT_GARDEN_ORCHESTRA:
					{
						ratio.setText( "" + ( 450.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case AMERICAN_CONCERT_PITCH:
					{
						ratio.setText( "" + ( 461.6 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case FRENCH_GOVERNMENT_COMMISSION:
					{
						ratio.setText( "" + ( 435.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case ONE_SHARP_DIATONIC:
					{
						ratio.setText( "" + ( Math.pow( 2.0 , 1.0 / 12.0 ) ) );
						ratio.repaint();
					}
					break;
					
					case ONE_SHARP_JUST:
					{
						ratio.setText( "" + ( JustIntonationAMinor.JUST_TUNE_1 ) );
						ratio.repaint();
					}
					break;
					
					case ONE_SHARP_PYTHAGOREAN:
					{
						ratio.setText( "" + ( PythagoreanIntonationAMinor.PYTHAGOREAN_TUNE_1 ) );
						ratio.repaint();
					}
					break;
					
					case ONE_SHARP_QUARTER_COMMA_MEANTONE:
					{
						ratio.setText( "" + ( QuarterCommaMeantoneDiminishedFifthAMinor.TUNE_1 ) );
						ratio.repaint();
					}
					break;
					
					case ENGLAND_STANDARD:
					{
						ratio.setText( "" + ( 435.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case RATIO_441:
					{
						ratio.setText( "" + ( 441.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
					case RATIO_480:
					{
						ratio.setText( "" + ( 480.0 / 440.0 ) );
						ratio.repaint();
					}
					break;
					
				}
			}
		} );

		ApplyButton.addActionListener(this);
	}
	
	/* public static void main( String[] in )
	{
		System.out.println( 440.0 * ( QuarterCommaMeantoneDiminishedFifthAMinor.TUNE_1 ) );
	} */

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
	* the edited GMultiplicativeAdjust.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setMultiplier( Double.parseDouble( ratio.getText() ) );
			waveForm.setInvertM( invert.isSelected() );

			
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
