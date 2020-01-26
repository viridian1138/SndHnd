




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


import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

import aczon.AczonImmutableEntFactory;
import aczon.AczonRootFactory;


/**
 * An immutable AazonEnt displaying a rasterized version (the sprite) of an input immutable AazonEnt.  This provides performance enhancements in cases where the raster can be displayed more quickly that the original AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableSprite implements AazonImmutableEnt {
	
	/**
	 * Generates canvas for the rasterization of the input AazonEnt.
	 */
	AczonRootFactory rf;

	/**
	 * Constructs the sprite.
	 * @param _sub Factory producing the input AazonEnt to be rasterized.
	 */
	public AazonImmutableSprite( AczonImmutableEntFactory  _sub) {
		rf = new AczonRootFactory( _sub );
	}
	
	/**
	 * Constructs the sprite.
	 * @param _sub The input AazonEnt to be rasterized.
	 */
	public AazonImmutableSprite( AazonImmutableEnt  _sub) {
		rf = new AczonRootFactory( _sub );
	}

	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() {
		
		// offscreen
		Canvas3D c = rf.getCanvas();
		final int width = 500;
		final int height = 500;

		c.getScreen3D().setSize(500, 500);
		c.getScreen3D().setPhysicalScreenWidth(0.0254 / 90.0 * 500);
		c.getScreen3D().setPhysicalScreenHeight(0.0254 / 90.0 * 500);

		BufferedImage bImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		ImageComponent2D buffer = new ImageComponent2D(
				ImageComponent.FORMAT_RGB, bImage);
		// buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

		System.out.println("Rendering...");
		c.setOffScreenBuffer(buffer);
		c.renderOffScreenBuffer();
		c.waitForOffScreenRendering();

		float x = 0.0f;
		float y = 0.0f;
		float z = 0.0f;

		// create the Raster for the image
		javax.media.j3d.Raster renderRaster = new javax.media.j3d.Raster(
				new Point3f(x, y, z), javax.media.j3d.Raster.RASTER_COLOR, 0,
				0, bImage.getWidth(), bImage.getHeight(), buffer, null);

		return new Shape3D(renderRaster);
	}
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		rf.genSelfActive(in);
	}

	
}

