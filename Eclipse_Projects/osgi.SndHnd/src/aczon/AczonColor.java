




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







package aczon;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.vecmath.Color3f;


/**
 * Defines a default set of colors for use with Aczon.
 * 
 * @author tgreen
 *
 */
public class AczonColor {
	
	
	/**
	 * Creates an appearance for a line color given the RGB of the line color.
	 * @param ca The RGB of the line color.
	 * @return The appearance of the line color.
	 */
	private static Appearance createLineAppearance( final java.awt.Color ca )
	{
		ColoringAttributes col = new ColoringAttributes();
		col
				.setColor((float) (ca.getRed() / 255.0),
						(float) (ca.getGreen() / 255.0),
						(float) (ca.getBlue() / 255.0));
		col.setShadeModel(ColoringAttributes.SHADE_FLAT);
		LineAttributes lin = new LineAttributes();
		lin.setLinePattern(LineAttributes.PATTERN_SOLID);
		Appearance app = new Appearance();
		app.setColoringAttributes(col);
		app.setLineAttributes(lin);
		return( app );
	}
	
	
	/**
	 * Creates an appearance for a fill color given the RGB of the fill color.
	 * @param ca The RGB of the fill color.
	 * @return The appearance of the fill color.
	 */
	private static Appearance createFillAppearance( final java.awt.Color ca )
	{
		ColoringAttributes col = new ColoringAttributes();
		col.setColor((float) (ca.getRed() / 255.0), (float) (ca
				.getGreen() / 255.0), (float) (ca.getBlue() / 255.0));
		col.setShadeModel(ColoringAttributes.SHADE_FLAT);
		// col.setShadeModel( ColoringAttributes.SHADE_GOURAUD );

		Material mat = new Material();
		mat.setAmbientColor((float) (ca.getRed() / 255.0), (float) (ca
				.getGreen() / 255.0), (float) (ca.getBlue() / 255.0));
		mat.setDiffuseColor((float) 0, (float) 0, (float) 0);
		mat.setShininess(0.0f);
		mat.setSpecularColor(0.0f, 0.0f, 0.0f);
		mat.setLightingEnable(false);
		PolygonAttributes polyAttrib = new PolygonAttributes();
		polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
		polyAttrib.setBackFaceNormalFlip(true); // bidirectional lighting

		Appearance app = new Appearance();
		app.setColoringAttributes(col);
		app.setMaterial(mat);
		app.setPolygonAttributes(polyAttrib);
		return( app );
	}
	
	
	/**
	 * The RGB definition for a dark cyan.
	 */
	protected static final Color AWT_DARK_CYAN = new Color(0, 192, 192);

	/**
	 * The RGB definition for a dark yellow.
	 */
	protected static final Color AWT_DARK_YELLOW = new Color(192, 192, 0);
	
	
	/**
	 * Stores a definition for a green line color.
	 */
	private static Appearance LINE_GREEN = null;
	
	/**
	 * Gets a green line color.
	 * @return The green line color.
	 */
	public static Appearance getLineGreen()
	{
		if( LINE_GREEN == null )
		{
			LINE_GREEN = createLineAppearance( java.awt.Color.GREEN );
		}
		
		return( LINE_GREEN );
	}
	
	
	/**
	 * Stores a definition for a magenta line color.
	 */
	private static Appearance LINE_MAGENTA = null;
	
	/**
	 * Gets a magenta line color.
	 * @return The magenta line color.
	 */
	public static Appearance getLineMagenta()
	{
		if( LINE_MAGENTA == null )
		{
			LINE_MAGENTA = createLineAppearance( java.awt.Color.MAGENTA );
		}
		
		return( LINE_MAGENTA );
	}
	
	
	/**
	 * Stores a definition for a orange line color.
	 */
	private static Appearance LINE_ORANGE = null;
	
	/**
	 * Gets a orange line color.
	 * @return The orange line color.
	 */
	public static Appearance getLineOrange()
	{
		if( LINE_ORANGE == null )
		{
			LINE_ORANGE = createLineAppearance( java.awt.Color.ORANGE );
		}
		
		return( LINE_ORANGE );
	}
	
	
	/**
	 * Stores a definition for a yellow line color.
	 */
	private static Appearance LINE_YELLOW = null;
	
	/**
	 * Gets a yellow line color.
	 * @return The yellow line color.
	 */
	public static Appearance getLineYellow()
	{
		if( LINE_YELLOW == null )
		{
			LINE_YELLOW = createLineAppearance( java.awt.Color.YELLOW );
		}
		
		return( LINE_YELLOW );
	}
	
	
	/**
	 * Stores a definition for a light gray line color.
	 */
	private static Appearance LINE_LIGHT_GRAY = null;
	
	/**
	 * Gets a light gray line color.
	 * @return The light gray line color.
	 */
	public static Appearance getLineLightGray()
	{
		if( LINE_LIGHT_GRAY == null )
		{
			LINE_LIGHT_GRAY = createLineAppearance( java.awt.Color.LIGHT_GRAY );
		}
		
		return( LINE_LIGHT_GRAY );
	}
	
	
	/**
	 * Stores a definition for a white line color.
	 */
	private static Appearance LINE_WHITE = null;
	
	/**
	 * Gets a white line color.
	 * @return The white line color.
	 */
	public static Appearance getLineWhite()
	{
		if( LINE_WHITE == null )
		{
			LINE_WHITE = createLineAppearance( java.awt.Color.WHITE );
		}
		
		return( LINE_WHITE );
	}
	
	
	/**
	 * Stores a definition for a red line color.
	 */
	private static Appearance LINE_RED = null;
	
	/**
	 * Gets a red line color.
	 * @return The red line color.
	 */
	public static Appearance getLineRed()
	{
		if( LINE_RED == null )
		{
			LINE_RED = createLineAppearance( java.awt.Color.RED );
		}
		
		return( LINE_RED );
	}
	
	
	/**
	 * Stores a definition for a dark cyan line color.
	 */
	private static Appearance LINE_DARK_CYAN = null;
	
	/**
	 * Gets a dark cyan line color.
	 * @return The dark cyan line color.
	 */
	public static Appearance getLineDarkCyan()
	{
		if( LINE_DARK_CYAN == null )
		{
			LINE_DARK_CYAN = createLineAppearance( AWT_DARK_CYAN );
		}
		
		return( LINE_DARK_CYAN );
	}
	
	
	/**
	 * Stores a definition for a dark yellow line color.
	 */
	private static Appearance LINE_DARK_YELLOW = null;
	
	/**
	 * Gets a dark yellow line color.
	 * @return The dark yellow line color.
	 */
	public static Appearance getLineDarkYellow()
	{
		if( LINE_DARK_YELLOW == null )
		{
			LINE_DARK_YELLOW = createLineAppearance( AWT_DARK_YELLOW );
		}
		
		return( LINE_DARK_YELLOW );
	}
	
	
	

	/**
	 * Stores a definition for a black line color.
	 */
	private static Appearance LINE_BLACK = null;
	
	/**
	 * Gets a black line color.
	 * @return The black line color.
	 */
	public static Appearance getLineBlack()
	{
		if( LINE_BLACK == null )
		{
			LINE_BLACK = createLineAppearance( java.awt.Color.BLACK );
		}
		
		return( LINE_BLACK );
	}
	
	
	
	/**
	 * Stores a definition for a cyan fill color.
	 */
	private static Appearance FILL_CYAN = null;
	
	/**
	 * Gets a cyan fill color.
	 * @return The cyan fill color.
	 */
	public static Appearance getFillCyan()
	{
		if( FILL_CYAN == null )
		{
			FILL_CYAN = createFillAppearance( java.awt.Color.CYAN );
		}
		
		return( FILL_CYAN );
	}
	
	
	
	/**
	 * Stores a definition for a white fill color.
	 */
	private static Appearance FILL_WHITE = null;
	
	/**
	 * Gets a white fill color.
	 * @return The white fill color.
	 */
	public static Appearance getFillWhite()
	{
		if( FILL_WHITE == null )
		{
			FILL_WHITE = createFillAppearance( java.awt.Color.WHITE );
		}
		
		return( FILL_WHITE );
	}
	
	
	
	/**
	 * Stores a definition for a orange fill color.
	 */
	private static Appearance FILL_ORANGE = null;
	
	/**
	 * Gets a orange fill color.
	 * @return The orange fill color.
	 */
	public static Appearance getFillOrange()
	{
		if( FILL_ORANGE == null )
		{
			FILL_ORANGE = createFillAppearance( java.awt.Color.ORANGE );
		}
		
		return( FILL_ORANGE );
	}
	
	
	
	/**
	 * Stores a definition for a yellow fill color.
	 */
	private static Appearance FILL_YELLOW = null;
	
	/**
	 * Gets a yellow fill color.
	 * @return The yellow fill color.
	 */
	public static Appearance getFillYellow()
	{
		if( FILL_YELLOW == null )
		{
			FILL_YELLOW = createFillAppearance( java.awt.Color.YELLOW );
		}
		
		return( FILL_YELLOW );
	}
	
	
	/**
	 * Stores a definition for a red fill color.
	 */
	private static Appearance FILL_RED = null;
	
	/**
	 * Gets a red fill color.
	 * @return The red fill color.
	 */
	public static Appearance getFillRed()
	{
		if( FILL_RED == null )
		{
			FILL_RED = createFillAppearance( java.awt.Color.RED );
		}
		
		return( FILL_RED );
	}
	
	
	/**
	 * Stores a definition for a green fill color.
	 */
	private static Appearance FILL_GREEN = null;
	
	/**
	 * Gets a green fill color.
	 * @return The green fill color.
	 */
	public static Appearance getFillGreen()
	{
		if( FILL_GREEN == null )
		{
			FILL_GREEN = createFillAppearance( java.awt.Color.GREEN );
		}
		
		return( FILL_GREEN );
	}
	
	
	/**
	 * Stores a definition for a black fill color.
	 */
	private static Appearance FILL_BLACK = null;
	
	/**
	 * Gets a black fill color.
	 * @return The black fill color.
	 */
	public static Appearance getFillBlack()
	{
		if( FILL_BLACK == null )
		{
			FILL_BLACK = createFillAppearance( java.awt.Color.BLACK );
		}
		
		return( FILL_BLACK );
	}
	
	
	/**
	 * Stores a definition for a black text color.
	 */
	private static Color3f TEXT_BLACK = null;
	
	/**
	 * Gets a black text color.
	 * @return The black text color.
	 */
	public static Color3f getTextBlack()
	{
		if( TEXT_BLACK == null )
		{
			TEXT_BLACK = new Color3f(0,0,0);
		}
		
		return( TEXT_BLACK );
	}
	
	
	/**
	 * Stores a definition for a green text color.
	 */
	private static Color3f TEXT_GREEN = null;
	
	/**
	 * Gets a green text color.
	 * @return The green text color.
	 */
	public static Color3f getTextGreen()
	{
		if( TEXT_GREEN == null )
		{
			TEXT_GREEN = new Color3f(0,1.0f,0);
		}
		
		return( TEXT_GREEN );
	}
	
	
	
	
	
	

}

