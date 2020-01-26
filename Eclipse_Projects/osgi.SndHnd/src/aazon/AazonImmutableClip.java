




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

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Group;
import javax.media.j3d.ModelClip;
import javax.media.j3d.Node;
import javax.vecmath.Point3d;
import javax.vecmath.Vector4d;

import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableVect;


/**
 * Immutable AazonEnt that performs clipping on another AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableClip implements AazonImmutableEnt {
	
	/**
	 * The AazonEnt to be clipped.
	 */
	AazonEnt ent;
	
	/**
	 * Collected list of self-active objects.
	 */
	ArrayList ar;
	
	/**
	 * Vector to the clipping position.
	 */
	AazonImmutableVect p;
	
	/**
	 * Vector representing the clipping width and height.
	 */
	AazonImmutableVect wh;
	
	/**
	 * The ModelClip.
	 */
	final ModelClip clip = new ModelClip();
	
	/**
	 * The group.
	 */
	final Group grp = new Group();
	
	/**
	 * Whether the ModelClip has been initialized.
	 */
	boolean initClip = false;
	
	/**
	 * Whether the group has been initialized.
	 */
	boolean initGrp = false;
	
	/**
	 * Constructs the immutable clip.
	 * @param _ent The AazonEnt to be clipped.
	 * @param _p Vector to the clipping position.
	 * @param _wh Vector representing the clipping width and height.
	 */
	public AazonImmutableClip( AazonEnt _ent , AazonImmutableVect _p , AazonImmutableVect _wh )
	{
		ent = _ent;
		p = _p;
		wh = _wh;
	}
	
	/**
	 * Constructs the immutable clip for an extremely large clipping region.
	 * @param _ent The AazonEnt to be clipped.
	 */
	public AazonImmutableClip( AazonEnt _ent )
	{
		this( _ent , new AazonBaseImmutableVect( -10000.0 , -10000.0 ) , new AazonBaseImmutableVect( 20000.0 , 20000.0 ) );
	}

	
	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() {
		init();
		return( grp );
	}
	
	/**
	 * Initializes the clipping.
	 */
	protected void init()
	{
		if( !initClip )
		{
			boolean enables[] = { false, false, false, false, false, false };
			double x = p.getX();
			double y = p.getY();
			double width = wh.getX();
			double height = wh.getY();
		    Vector4d eqn1 = new Vector4d(1.0, 0.0, 0.0, x);
		    Vector4d eqn2 = new Vector4d(-1.0, 0.0, 0.0, x+width);
		    Vector4d eqn3 = new Vector4d(0.0, 1.0, 0.0, y);
		    Vector4d eqn4 = new Vector4d(0.0, -1.0, 0.0, y+height);
		    clip.setEnables(enables);
		    clip.setPlane(1, eqn1);
		    clip.setPlane(2, eqn2);
		    clip.setPlane(3, eqn3);
		    clip.setPlane(4, eqn4);
		    clip.setEnable(1, true);
		    clip.setEnable(2, true);
		    clip.setEnable(3, true);
		    clip.setEnable(4, true);
		    BoundingSphere bounds = new BoundingSphere(new Point3d(x, y, 0.0),
		            20000.0);
			clip.setInfluencingBounds(bounds);		    
			clip.removeAllScopes();
			clip.addScope( grp );
		    p = null;
		    wh = null;
			initClip = true;
		}
		
		if( !initGrp )
		{
			grp.removeAllChildren();
			if( ent instanceof AazonImmutableEnt )
			{
				grp.addChild( ( (AazonImmutableEnt) ent ).getNode() );
			}
			else
			{
				grp.addChild( ( (AazonMutableEnt) ent ).getBranch() );
			}
			grp.addChild( clip );
			ar = new ArrayList();
			ent.genSelfActive( ar );
			ent = null;
			initGrp = true;
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

