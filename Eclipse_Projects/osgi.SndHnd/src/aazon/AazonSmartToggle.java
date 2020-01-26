




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

import aazon.bool.AazonBool;
import aazon.bool.AazonImmutableBool;
import aazon.bool.AazonMutableBool;


/**
 * AazonEnt that toggles from one input AazonEnt to another based on the state of a input boolean.
 * 
 * @author tgreen
 *
 */
public class AazonSmartToggle implements
		AazonImmutableEnt , AazonListener {
	
	/**
	 * The previous toggle value used in the switch group.
	 */
	int prevToggleVal = -1;
	
	/**
	 * The AazonEnt to be displayed when the toggle is on.
	 */
	AazonEnt onEnt;
	
	/**
	 * The AazonEnt to be displayed when the toggle is off.
	 */
	AazonEnt offEnt;

	/**
	 * Boolean controlling the toggle position.
	 */
	AazonMutableBool bool;
	
	/**
	 * Whether the toggle has been initialized.
	 */
	boolean init = false;
	
	/**
	 * The switch group for the toggle.
	 */
	Switch switchGroup;
	
	
	/**
	 * Private constructor.
	 * @param _onEnt The AazonEnt to be displayed when the toggle is on.
	 * @param _offEnt The AazonEnt to be displayed when the toggle is off.
	 * @param _bool Boolean controlling the toggle position.
	 */
	private AazonSmartToggle( AazonEnt _onEnt , AazonEnt _offEnt , AazonMutableBool _bool )
	{
		onEnt = _onEnt;
		offEnt = _offEnt;
		bool = _bool;
		
		bool.add( this );
	}

	/**
	 * Initializes the toggle.
	 */
	protected void init()
	{
		if( !init )
		{
			switchGroup = new Switch( Switch.CHILD_MASK );
			switchGroup.setCapability( Switch.ALLOW_SWITCH_WRITE );
			
			boolean toggle = bool.getBool().getBoolVal();
			int toggleVal = toggle ? 1 : 0;
			
			if( offEnt instanceof AazonImmutableEnt )
			{
				switchGroup.addChild( ( (AazonImmutableEnt) offEnt ).getNode() );
			}
			else
			{
				switchGroup.addChild( ( (AazonMutableEnt) offEnt ).getBranch() );
			}
			
			if( onEnt instanceof AazonImmutableEnt )
			{
				switchGroup.addChild( ( (AazonImmutableEnt) onEnt ).getNode() );
			}
			else
			{
				switchGroup.addChild( ( (AazonMutableEnt) onEnt ).getBranch() );
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
	 * Handles a change to the toggle boolean by updating the state.
	 */
	public void handleListen() {
		if( init )
		{
			boolean toggle = bool.getBool().getBoolVal();
			int toggleVal = toggle ? 1 : 0;
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
	 * Returns an AazonEnt that toggles from one input AazonEnt to another based on the state of a input boolean.
	 * @param _onEnt The AazonEnt to be displayed when the toggle is on.
	 * @param _offEnt The AazonEnt to be displayed when the toggle is off.
	 * @param _bool Boolean controlling the toggle position.
	 * @return AazonEnt that toggles from one input AazonEnt to another based on the state of a input boolean.
	 */
	public static AazonEnt construct( AazonEnt _onEnt , AazonEnt _offEnt , AazonBool _bool )
	{
		if( _bool instanceof AazonImmutableBool )
		{
			return( _bool.getBool().getBoolVal() ? _onEnt : _offEnt );
		}
		
		return( new AazonImmutableSharedEnt( new AazonSmartToggle( _onEnt , _offEnt , (AazonMutableBool) _bool ) ) );
	}
	
	/**
	 * Returns an AazonEnt that toggles from one input AazonEnt to another based on the state of a input boolean.
	 * @param _onEnt The AazonEnt to be displayed when the toggle is on (nothing displayed when thr toggle is off).
	 * @param _bool Boolean controlling the toggle position.
	 * @return AazonEnt that toggles from one input AazonEnt to another based on the state of a input boolean.
	 */
	public static AazonEnt construct( AazonEnt _onEnt , AazonBool _bool )
	{
		return( construct( _onEnt , new AazonImmutableGroup() , _bool ) );
	}

	
}

