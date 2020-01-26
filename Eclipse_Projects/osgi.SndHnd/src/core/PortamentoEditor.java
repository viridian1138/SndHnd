




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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import verdantium.Adapters;
import verdantium.ProgramDirector;
import verdantium.VerdantiumUtils;
import verdantium.undo.UndoManager;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;



/**
* Interface for editing the portamentos in a musical note.
* <P>
* @author Thorn Green
*/
public class PortamentoEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The set of portamentos in the musical note to be edited.
	 */
	protected PortamentoDesc desc = null;
	
	/**
	 * The track containing the note.
	 */
	protected InstrumentTrack track = null;
	
	/**
	 * The undo manager.
	 */
	protected UndoManager undoMgr = null;
	
	/**
	 * The list model for managing the list of portamentos.
	 */
	protected DefaultListModel model = new DefaultListModel();
	
	/**
	 * The displayed list of portamentos.
	 */
	protected JList transitionList = new JList();
	
	/**
	 * Button to edit a portamento transition.
	 */
	protected JButton editTransition = new JButton( "Edit Transition..." );
	
	/**
	 * Button to clear the set of portamento transitrions.
	 */
	protected JButton clearTransitions = new JButton( "Clear Transitions" );

	
	/**
	 * Constructor.
	 * @param in The set of portamentos in the musical note to be edited.
	 * @param ins2 The track containing the note.
	 * @param _undoMgr The undo manager.
	 */
	public PortamentoEditor(PortamentoDesc in , InstrumentTrack ins2 , UndoManager _undoMgr ) {
		desc = in;
		track = ins2;
		undoMgr = _undoMgr;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		pan2.add("any", new JLabel("Transition List : "));
		pan2.add("any", transitionList);
		pan2.add("any", editTransition);
		pan2.add("any", clearTransitions);
		
		
		transitionList.setModel( model );
		
		for( final PortamentoTransition tr : desc.getTransitions() )
		{
			String str = tr.getTransitionNoteU() + " [ " + tr.getPitchRatio() + 
				" , " + tr.getTransitionTimeSeconds() + " ] ";
			model.addElement( str );
		}

		ApplyButton.addActionListener(this);
		
		ActionListener ButtonL = Adapters.createGActionListener(this, "handleEditTransition");
		editTransition.addActionListener( ButtonL );
		
		ButtonL = Adapters.createGActionListener(this, "handleClearTransitions");
		clearTransitions.addActionListener( ButtonL );
	}
	
	
	/**
	 * Handles a request to edit a portamento transition.
	 * @param e The event.
	 */
	public void handleEditTransition( ActionEvent e )
	{
		int index = transitionList.getSelectedIndex();
		if( index >= 0 )
		{
			PortamentoTransition tr = (PortamentoTransition)( desc.getTransitions().get( index ) );
			PortamentoTransitionEditor editor = new PortamentoTransitionEditor( tr , track , undoMgr );
			ProgramDirector.showPropertyEditor(editor, null, "Portamento Transition");
		}
		else
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	
	/**
	 * Handles a request to clear the portamento transitions.
	 * @param e The event.
	 */
	public void handleClearTransitions( ActionEvent e )
	{
		try
		{
			System.out.println( "Clearing..." );
			final int core = 0;
			desc.clearTransitions();
			track.updateTrackFrames( core );
			SongListeners.updateViewPanes();
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
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
	* Handles a button-press event from the Apply button by
	* updating the track.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			
			final int core = 0;
			
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
			track.updateTrackFrames( core );

			
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

