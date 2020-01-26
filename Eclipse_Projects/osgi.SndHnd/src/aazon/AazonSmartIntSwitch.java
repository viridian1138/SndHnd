




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









package aazon;



import java.util.ArrayList;

import javax.media.j3d.Node;
import javax.media.j3d.Switch;

import aazon.intg.AazonInt;
import aazon.intg.AazonImmutableInt;
import aazon.intg.AazonMutableInt;


/**
 * AazonEnt that switches from one input AazonEnt to another based on the state of a input integer.
 * 
 * @author tgreen
 *
 */
public class AazonSmartIntSwitch implements
		AazonImmutableEnt , AazonListener {
	
	/**
	 * The previous switch value used in the switch group.
	 */
	int prevToggleVal = -1;
	
	/**
	 * The array of input AazonEnts to switch between based on the integer value.
	 */
	AazonEnt[] ents;
	
	/**
	 * Integer controlling the switch position.
	 */
	AazonMutableInt intg;
	
	/**
	 * Whether the switch has been initialized.
	 */
	boolean init = false;
	
	/**
	 * The switch group.
	 */
	Switch switchGroup;
	
	/**
	 * Private constructor.
	 * @param _ents The array of input AazonEnts to switch between based on the integer value.
	 * @param _intg Integer controlling the switch position.
	 */
	private AazonSmartIntSwitch( AazonEnt[] _ents , AazonMutableInt _intg )
	{
		ents = _ents;
		intg = _intg;
		
		intg.add( this );
	}

	/**
	 * Initializes the AazonSmartIntSwitch.
	 */
	protected void init()
	{
		if( !init )
		{
			switchGroup = new Switch( Switch.CHILD_MASK );
			switchGroup.setCapability( Switch.ALLOW_SWITCH_WRITE );
			
			int toggleVal = intg.getX();
			
			int count;
			final int len = ents.length;
			for( count = 0 ; count < len ; count++ )
			{
				final AazonEnt ent = ents[ count ];
				if( ent != null )
				{
					if( ent instanceof AazonImmutableEnt )
					{
						switchGroup.addChild( ( (AazonImmutableEnt) ent ).getNode() );
					}
					else
					{
						switchGroup.addChild( ( (AazonMutableEnt) ent ).getBranch() );
					}
				}
				else
				{
					switchGroup.addChild( new AazonImmutableGroup().getNode() );
				}
			}
		
			switchGroup.setWhichChild( toggleVal );
			prevToggleVal = toggleVal;
			init = true;
		}
	}
	
	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode()
	{
		init();
		return( switchGroup );
	}

	/**
	 * Handles a change to the switch state by updating the state.
	 */
	public void handleListen() {
		if( init )
		{
			int toggleVal = intg.getX();
			if( toggleVal != prevToggleVal )
			{
				switchGroup.setWhichChild( toggleVal );
				prevToggleVal = toggleVal;
			}
		}
	}
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		in.add( this );
	}
	
	/**
	 * Returns an AazonEnt that switches from one input AazonEnt to another based on the state of a input integer.
	 * @param _ents The array of input AazonEnts to switch between based on the integer value.
	 * @param _intg Integer controlling the switch position.
	 * @return AazonEnt that switches from one input AazonEnt to another based on the state of a input integer.
	 */
	public static AazonEnt construct( AazonEnt[] _ents , AazonInt _intg )
	{
		if( _intg instanceof AazonImmutableInt )
		{
			return( _ents[ _intg.getX() ] );
		}
		
		return( new AazonImmutableSharedEnt( new AazonSmartIntSwitch( _ents , (AazonMutableInt) _intg ) ) );
	}

	
}

