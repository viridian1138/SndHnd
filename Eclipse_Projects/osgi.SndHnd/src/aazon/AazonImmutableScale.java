




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
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableVect;



/**
 * AazonEnt performing an immutable scaling of another AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableScale implements AazonImmutableEnt {
	
	/**
	 * Scaling group.
	 */
	TransformGroup objTranslate = new TransformGroup( new Transform3D() );
	
	/**
	 * The AazonEnt being scaled.
	 */
	AazonEnt ent;
	
	/**
	 * Vector holding the coordinate scaling for the X-Axis and the Y-Axis.
	 */
	AazonImmutableVect trans;
	
	/**
	 * List of self-active objects.
	 */
	ArrayList ar = null;
	
	/**
	 * Whether the group has been initialized.
	 */
	boolean initGrp = false;
	
	/**
	 * Whether the coordinate transform has been initialized.
	 */
	boolean initCoord = false;
	
	/**
	 * Constructs the scaling.
	 * @param _ent The AazonEnt being scaled.
	 * @param _trans Vector holding the coordinate scaling for the X-Axis and the Y-Axis.
	 */
	public AazonImmutableScale( AazonEnt _ent , AazonImmutableVect _trans )
	{
		ent = _ent;
		trans = _trans;
	}
	
	/**
	 * Constructs the scaling for a zero scale.
	 * @param _ent The AazonEnt to be scaled.
	 */
	public AazonImmutableScale( AazonEnt _ent )
	{
		this( _ent , new AazonBaseImmutableVect( 0.0 , 0.0 ) );
	}
	
	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode()
	{
		init();
		return( objTranslate );
	}
	
	/**
	 * Initializes the scaling.
	 */
	protected void init()
	{
		if( !initGrp )
		{
			objTranslate.removeAllChildren();
			if( ent instanceof AazonImmutableEnt )
			{
				objTranslate.addChild( ( (AazonImmutableEnt) ent ).getNode() );
			}
			else
			{
				objTranslate.addChild( ( (AazonMutableEnt) ent ).getBranch() );
			}
			ar = new ArrayList();
			ent.genSelfActive( ar );
			ent = null;
			initGrp = true;
		}
		
		if( !initCoord )
		{
			Transform3D scale = new Transform3D();
			scale.setScale(new Vector3d( trans.getX() , trans.getY() , 0.0));
			objTranslate.setTransform( scale );
			trans = null;
			initCoord = true;
		}
		
	}
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		if( ar != null )
		{
			in.add( ar );
			return;
		}
		
		ent.genSelfActive(in);
	}

	
}

