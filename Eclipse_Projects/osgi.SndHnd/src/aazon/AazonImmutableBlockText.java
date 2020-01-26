




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

import javax.media.j3d.BadTransformException;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import aazon.vect.AazonImmutableVect;

import com.sun.j3d.utils.geometry.Text2D;


/**
 * AazonEnt for an immutable block of text.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableBlockText implements AazonImmutableEnt {

	/**
	 * The vector to the position where the text starts rendering.
	 */
	AazonImmutableVect a1;
	
	/**
	 * The string to be displayed as block text.
	 */
	String str;
	
	/**
	 * The color of the text.
	 */
	Color3f txtCol;
	
	/**
	 * The name of the requested font for the block text.
	 */
	String fontName;
	
	/**
	 * The size of the requested font for the block text.
	 */
	int fontSize;
	
	/**
	 * The style of the requested font for the block text.  The possible styles can be obtained from the static final int members of class java.awt.Font.
	 */
	int fontStyle;
	
	
	/**
	 * Constructs the AazonImmutableBlockText.
	 * @param _a1 The vector to the position where the text starts rendering.
	 * @param _str The string to be displayed as block text.
	 * @param _txtCol The color of the text.
	 * @param _fontName The name of the requested font for the block text.
	 * @param _fontSize The size of the requested font for the block text.
	 * @param _fontStyle The style of the requested font for the block text.  The possible styles can be obtained from the static final int members of class java.awt.Font.
	 */
	public AazonImmutableBlockText( final AazonImmutableVect _a1 , final String _str , Color3f _txtCol , String _fontName , int _fontSize , int _fontStyle )
	{
		a1 = _a1;
		str = _str;
		txtCol = _txtCol;
		fontName = _fontName;
		fontSize = _fontSize;
		fontStyle = _fontStyle;
	}
	
	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() {
		Transform3D translate = new Transform3D();
		translate.setTranslation( new Vector3d( a1.getX() , a1.getY() , 0.0 ) );
		try
		{
			TransformGroup objTranslate = new TransformGroup(translate);
			Text2D label = new Text2D(str, txtCol,
					fontName, fontSize, fontStyle);
			objTranslate.addChild( label );
			return( objTranslate );
		}
		catch( BadTransformException ex )
		{
			ex.printStackTrace( System.out );
			System.out.println( a1.getX() );
			System.out.println( a1.getY() );
			translate.setTranslation( new Vector3d( 0.0 , 0.0 , 0.0 ) );
			TransformGroup objTranslate = new TransformGroup(translate);
			Text2D label = new Text2D(str, txtCol,
					fontName, fontSize, fontStyle);
			objTranslate.addChild( label );
			return( objTranslate );
		}
	}
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		// Does Nothing.
	}

	
}

