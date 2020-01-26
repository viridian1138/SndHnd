




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


import verdantium.VerdantiumPropertiesEditor;

/**
 * Interface that an agent implements as mix-in to provide the ability to edit the agent.
 * 
 * @author tgreen
 *
 */
public interface EditableAgent {
	
	/**
	 * Creates a property editor for changing an agent's parameters.
	 * @param ins The instrument track containing the agent.
	 * @return The property editor.
	 */
	public VerdantiumPropertiesEditor getEditor( InstrumentTrack ins );
	
	/**
	 * Creates a property editor for changing an agent's vibrato parameters.
	 * @param ins The instrument track containing the agent.
	 * @return The vibrato property editor.
	 */
	public VerdantiumPropertiesEditor getVibratoEditor( InstrumentTrack ins );
	
	/**
	 * Gets the vibrato parameters of the agent.
	 * @return The vibrato parameters of the agent.
	 */
	public VibratoParameters getVibratoParams();

	/**
	 * Sets the vibrato parameters of the agent.
	 * @param vibratoParams The vibrato parameters of thr agent.
	 */
	public void setVibratoParams(VibratoParameters vibratoParams);

}

