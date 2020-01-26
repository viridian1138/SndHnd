




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







package grview;


import gredit.GNode;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import palettes.IPalette;
import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.xapp.PropertyEditAdapter;
import aazon.vect.AazonImmutableVect;



/**
* A property editor for displaying a set of nodes that can be created from a palette, allowing the user to select what node should be created.
* <P>
* @author Thorn Green
*/
public class NodeCreationEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	/**
	 * The DraggableTransformNodeTest in which to create the node.
	 */
	protected DraggableTransformNodeTest in;
	
	/**
	 * Vector to the location at which to create the node.
	 */
	protected AazonImmutableVect ivect;
	
	/**
	 * The list of possible nodes that can be created.
	 */
	protected JList creationList = new JList();
	
	/**
	 * The classes the the list of possible nodes than can be created.
	 */
	protected ArrayList<Class<? extends GNode>> paletteClasses = null;
	
	/**
	 * The calling palette.
	 */
	protected IPalette palette;
	
	/**
	 * Runnable that is invoked upon a palette update by the calling palette.
	 */
	protected Runnable runn;

	
	/**
	* Constructs the property editor for a given DraggableTransformNodeTest.
	* @param in The input DraggableTransformNodeTest.
	* @param _ivect Vector to the location at which to create the node.
	* @param _palette The calling palette.
	*/
	public NodeCreationEditor(DraggableTransformNodeTest _in , AazonImmutableVect _ivect , IPalette _palette ) {
		in = _in;
		ivect = _ivect;
		palette = _palette;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		final JScrollPane jsp = new JScrollPane( creationList , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED , JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		MyPan.add(BorderLayout.CENTER, jsp);
		
		updatePalette();

		ApplyButton.addActionListener(this);
		
		runn = new Runnable()
		{
			/**
			 * Invokes the palette update.
			 */
			public void run()
			{
			SwingUtilities.invokeLater(
					new Runnable()
					{
						/**
						 * Invokes the palette update/
						 */
						public void run()
						{
							updatePalette();
							jsp.repaint();
						}
					} );
			}
		};
		
		palette.addPaletteListener( runn );
		
	}
	
	/**
	 * Handles a palette update event by updating the list of possible nodes that can be created.
	 */
	protected void updatePalette()
	{
		paletteClasses = palette.getPaletteClasses();
		DefaultListModel lm = new DefaultListModel();
		creationList.setModel(lm);
		for( Class<? extends GNode> cl : paletteClasses )
		{
			try
			{
				GNode node = cl.newInstance();
				lm.addElement( node.getName() );
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
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
		palette.removePaletteListener( runn );
		
		super.handleDestroy();
	}

	/**
	* Handles a button-press event from the Apply button by 
	* creating an instance of the selected node.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			int index = creationList.getSelectedIndex();
			
			if( index >= 0 )
			{
				Class<? extends GNode> cl = paletteClasses.get( index );
				in.handleCreation(cl, ivect);
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
