




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







package kwaves;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;



import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;
import core.InstrumentTrack;
import core.SimplePitchEdit;
import core.WaveForm;



/**
* A property editor for rendering the Fourier spectrum a GAnalyzeFourierView node.
* <P>
* @author Thorn Green
*/
public class AnalyzeFourierViewEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The input GAnalyzeFourierView.
	 */
	protected GAnalyzeFourierView waveForm = null;
	
	/**
	 * Text field for the wave number at which to start the Fourier analysis (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 */
	protected JTextField waveStrt = new JTextField();
	
	/**
	 * Button controlling whether to create a Fourier view.
	 */
	protected JButton view = new JButton( "Show View..." ); 
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack ins;
	
	/**
	 * A copy for each core thread of the waveform to be analyzed.
	 */
	protected WaveForm[] waves;
	
	/**
	 * Text field for changing the pitch of the fundamental.
	 */
	protected final JTextField pitchField = new JTextField( "440.0" );
	

	
	/**
	* Constructs the property editor for a given GAnalyzeFourierView.
	* @param in The input GAnalyzeFourierView.
	* @param _ins The associated instrument track.
	* @param _waves A copy for each core thread of the waveform to be analyzed.
	*/
	public AnalyzeFourierViewEditor(GAnalyzeFourierView in , InstrumentTrack _ins , WaveForm[] _waves ) {
		waveForm = in;
		ins = _ins;
		waves = _waves;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Wave Strt : "));
		pan2.add("any", waveStrt);
		
		
		pan2.add( "any" , new JLabel( "Pitch : " ) );
		pan2.add( "any" , pitchField );
		pan2.add( "any" , new SimplePitchEdit( pitchField ) );
		
		pan2.add("any", view);
		
		waveStrt.setText( "" + 100 );
		

		ApplyButton.addActionListener(this);
		view.addActionListener(this);
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
	* Handles a button-press event from the "Show View..." button by showing
	* the FourierRenderingPane.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			if( e.getSource() == view )
			{
				double waveSt = Double.parseDouble( waveStrt.getText() );
				
				double pitch = Double.parseDouble( pitchField.getText() );
				
				System.out.println( "Working..." );
				
				FourierRenderingPane.setBaseAutocorFfreq( pitch );

				FourierRenderingPane pane = new FourierRenderingPane( waves , waveSt );
				
				JFrame fr = new JFrame();
				
				fr.getContentPane().setLayout( new BorderLayout( 0 , 0 ) );
				
				fr.getContentPane().add( BorderLayout.CENTER , pane );
				
				fr.pack();
				
				fr.show();
				
				System.out.println( "Fourier Done." );
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
