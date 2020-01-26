




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
 * Property editor for swinging up to three beats in a set of measures.
 * 
 * See https://en.wikipedia.org/wiki/Swing_(jazz_performance_style)
 * 
 * @author tgreen
 *
 */
public class SwingBeatsInMeasuresEditor extends PropertyEditAdapter implements
	ActionListener {
	
	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * The tempo view pane into which to submit the swing request.
	 */
	protected TempoViewPane2 tvp = null;
	
	
	/**
	 * Text field for entering the number of the first measure to which to apply the swing.  Measure numbers start from zero.
	 */
	protected JTextField istartMeasureNumber = new JTextField( "3" );
	
	/**
	 * Text field for entering the number of the last measure to which to apply the swing.  Measure numbers start from zero.
	 */
	protected JTextField iendMeasureNumber = new JTextField( "10" );
	
	
	
	/**
	 * The beat number in the measure to make the 0-listed beat.  Beat numbers start at zero.  A negative number indicates that swinging should be skipped for the 0-listed beat.
	 */
	protected JTextField ibeatNumberInMeasure0 = new JTextField( "1" );
	
	/**
	 * The number of beats to swing the 0-listed beat.  Negative numbers indicate backward swing.  Positive numbers indicate forward swing.
	 */
	protected JTextField iswingInBeats0 = new JTextField( "-.0625" );
	
	
	
	/**
	 * The beat number in the measure to make the 1-listed beat.  Beat numbers start at zero.  A negative number indicates that swinging should be skipped for the 1-listed beat.
	 */
	protected JTextField ibeatNumberInMeasure1 = new JTextField( "2" );
	
	/**
	 * The number of beats to swing the 1-listed beat.  Negative numbers indicate backward swing.  Positive numbers indicate forward swing.
	 */
	protected JTextField iswingInBeats1 = new JTextField( "+.0625" );
	
	
	
	/**
	 * The beat number in the measure to make the 2-listed beat.  Beat numbers start at zero.  A negative number indicates that swinging should be skipped for the 2-listed beat.
	 */
	protected JTextField ibeatNumberInMeasure2 = new JTextField( "3" );
	
	/**
	 * The number of beats to swing the 2-listed beat.  Negative numbers indicate backward swing.  Positive numbers indicate forward swing.
	 */
	protected JTextField iswingInBeats2 = new JTextField( "-.0625" );
	
	
	
	
	
	
	/**
	 * The apply button.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	/**
	 * Constructor.
	 * @param _tvp The tempo view pane into which to submit the swing request.
	 */
	public SwingBeatsInMeasuresEditor( TempoViewPane2 _tvp ) {
		super();
		tvp = _tvp;
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		

		pan2.add("any", new JLabel("Start Measure Number : ") );
		pan2.add("any", istartMeasureNumber );
		
		pan2.add("any", new JLabel("End Measure Number : ") );
		pan2.add("any", iendMeasureNumber );
		
		
		
		
		pan2.add("any", new JLabel("Beat Number In Measure -- 0 : ") );
		pan2.add("any", ibeatNumberInMeasure0 );
		
		pan2.add("any", new JLabel("Number Of Beats To Swing -- 0 : ") );
		pan2.add("any", iswingInBeats0 );
		
		
		
		
		pan2.add("any", new JLabel("Beat Number In Measure -- 1 : ") );
		pan2.add("any", ibeatNumberInMeasure1 );
		
		pan2.add("any", new JLabel("Number Of Beats To Swing -- 1 : ") );
		pan2.add("any", iswingInBeats1 );
		
		
		
		
		pan2.add("any", new JLabel("Beat Number In Measure -- 2 : ") );
		pan2.add("any", ibeatNumberInMeasure2 );
		
		pan2.add("any", new JLabel("Number Of Beats To Swing -- 2 : ") );
		pan2.add("any", iswingInBeats2 );
		
		
		
		
		pan2.add("any", applyButton);

		applyButton.addActionListener(this);
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
	 * Handles a button-press event from the Apply button by swinging the beats in the measures.
	 */
	protected void handleApply()
	{
		
		final int startMeasureNumber = Integer.parseInt( istartMeasureNumber.getText() );
		
		final int endMeasureNumber = Integer.parseInt( iendMeasureNumber.getText() );
		
		
		
		
		final int beatNumberInMeasure0 = Integer.parseInt( ibeatNumberInMeasure0.getText() );
		
		final double swingInBeats0 = Double.parseDouble( iswingInBeats0.getText() );
		
		
		
		
		final int beatNumberInMeasure1 = Integer.parseInt( ibeatNumberInMeasure1.getText() );
		
		final double swingInBeats1 = Double.parseDouble( iswingInBeats1.getText() );
		
		
		
		
		final int beatNumberInMeasure2 = Integer.parseInt( ibeatNumberInMeasure2.getText() );
		
		final double swingInBeats2 = Double.parseDouble( iswingInBeats2.getText() );
		
		
	
		
		
		
		tvp.swingBeatsInMeasures( startMeasureNumber , endMeasureNumber , beatNumberInMeasure0 , swingInBeats0 , beatNumberInMeasure1 , swingInBeats1 , beatNumberInMeasure2 , swingInBeats2 );
	}

	
	/**
	 * Handles a button-press event from the Apply button by swinging the beats in the measures.
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

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

