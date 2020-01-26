




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
 * Editor for editing the name of an agent.
 * 
 * @author tgreen
 *
 */
public class AgentNameEditor extends PropertyEditAdapter implements
	ActionListener {
	
	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();
	
	/**
	 * The agent to be edited.
	 */
	protected IntelligentAgent agent = null;
	
	/**
	 * The OutputChoiceInterface to be notified upon a change to the agent name.
	 */
	protected OutputChoiceInterface intf = null;
	
	/**
	 * Text field used to edit the agent name.
	 */
	protected JTextField name = new JTextField( "xxx" );
	
	/**
	 * The "Apply" button for committing a change to the agent name.
	 */
	protected JButton applyButton = new JButton( "Apply" );
	
	
	/**
	 * Constructs the editor.
	 * @param _agent The agent to be edited.
	 * @param _intf The OutputChoiceInterface to be notified upon a change to the agent name.
	 */
	public AgentNameEditor( IntelligentAgent _agent , OutputChoiceInterface _intf ) {
		super();
		agent = _agent;
		intf = _intf;
		
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		name.setText( agent.getHname() );

		pan2.add("any", new JLabel("Agent Name : ") );
		pan2.add("any", name );
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
	 * Handles a button-press event from the Apply button by changing the
	 * name of the agent.
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == applyButton) {
			try {
				
				
				agent.setHname( name.getText() );
				intf.refreshList();
				

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
