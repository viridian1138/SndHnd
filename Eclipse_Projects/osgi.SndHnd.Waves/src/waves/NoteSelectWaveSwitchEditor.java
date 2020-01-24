





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







package waves;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;
import core.InstrumentTrack;
import core.NoteDesc;
import core.SongData;



/**
* A property editor for editing a GNoteSelectWaveSwitch node.
* <P>
* @author Thorn Green
*/
public class NoteSelectWaveSwitchEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The waveform being edited.
	 */
	protected GNoteSelectWaveSwitch waveForm = null;
	
	/**
	 * Label displaying the object ID of the note upon which to select.
	 */
	protected JLabel sel = new JLabel();
	
	/**
	 * The note upon which to select.
	 */
	protected NoteDesc note = null;
	
	/**
	 * The associated instrument track.
	 */
	protected InstrumentTrack ins;
	

	
	/**
	* Constructs the property editor for a given GNoteSelectWaveSwitch.
	* @param in The input GNoteSelectWaveSwitch.
	* @param _ins The associated instrument track.
	*/
	public NoteSelectWaveSwitchEditor(GNoteSelectWaveSwitch in , InstrumentTrack _ins) {
		waveForm = in;
		ins = _ins;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton PasteButton = new JButton("Paste Note");
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Switch Desc : "));
		pan2.add("any", sel);
		
		PasteButton.setMinimumSize( new Dimension( 50 , 50 ) );
		PasteButton.setPreferredSize( new Dimension( 50 , 50 ) );
		pan2.add( "any" , PasteButton );
		
		note = in.getSwitchDesc();
		sel.setText( "" + note );
		
		PasteButton.addActionListener( new ActionListener()
		{
			/**
			 * Handles the pressing of the Paste button by pasting the note from the paste buffer.
			 */
			public void actionPerformed( ActionEvent e )
			{
				note = SongData.PASTE_BUFFER_NOTE;
				sel.setText( "" + note );
				sel.repaint();
			}
		} );

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
	* the edited GNoteSelectWaveSwitch.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			waveForm.setSwitchDesc( note );
			
			ins.updateTrackFrames(core);

			
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

