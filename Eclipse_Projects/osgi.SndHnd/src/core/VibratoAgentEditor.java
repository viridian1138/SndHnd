




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


/**
 * Subclass of VibratoParamsEditor for editing the vibrato parameters of an agent.
 * 
 * @author tgreen
 *
 */
public class VibratoAgentEditor extends VibratoParamsEditor {
	
	/**
	 * The instrument track containing the agent to be edited.
	 */
	InstrumentTrack ins;

	/**
	 * Constructor.
	 * @param params The vibrato parameters to be edited.
	 * @param ins2 The instrument track containing the agent to be edited.
	 */
	public VibratoAgentEditor(VibratoParameters params, InstrumentTrack ins2) {
		super( params );
		ins = ins2;
	}
	
	@Override
	protected void handleApply()
	{
		try
		{
			final int core = 0;
			ins.updateTrackFrames( core );
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}

}

