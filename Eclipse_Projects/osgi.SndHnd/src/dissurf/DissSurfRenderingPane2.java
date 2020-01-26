




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








package dissurf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Light;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import aczon.AczonColor;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import cwaves.CosineWaveform;
import cwaves.SineWaveform;



/**
 * Plots a surface for selecting the pitches of two notes out of a triad by showing
 * the dissonance changes as the pitches of the two notes are altered.  It is assumed that the
 * tonic of the triad (or equivalent) is already present and can remain fixed.
 * 
 * @author tgreen
 *
 */
public class DissSurfRenderingPane2 extends Panel implements MouseListener {
	
	/**
	 * The universe in which to plot the surface.
	 */
	private SimpleUniverse u = null;

	/**
	 * The canvas in which to plot the surface.
	 */
	protected Canvas3D canvas = null;
	
	/**
	 * The NDC Y-Coordinate for the bottom of the display area.
	 */
	protected static final double VERT_STRT_PCT = 0.0;

	/**
	 * The NDC X-Coordinate for the left-hand side of the display area.
	 */
	protected static final double HORIZ_STRT_PCT = 0.0;

	/**
	 * The NDC Y-Coordinate for the top of the display area.
	 */
	protected static final double VERT_END_PCT = 1.0;

	/**
	 * The NDC X-Coordinate for the right-hand side of the display area.
	 */
	protected static final double HORIZ_END_PCT = 1.0;

	
	/**
	 * The coordinates of the surface, where the map key is the
	 * X-Y point of the surface, and the map value is the calculated dissonance.
	 */
	HashMap<Coord,Double> data;
	
	/**
	 * The maximum number of frequencies on each domain axis.
	 */
	int sz;
	
	/**
	 * Interface for taking a coordinate index proportional to a frequency value in the domain (either X or Y) and mapping it to a domain value that can be used for rendering.
	 */
	PointRemap remap;
	
	/**
	 * Handles a user selection by clicking on the surface.
	 */
	SelectionHandler sel;
	
	/**
	 * The calculated local maxima on the surface, where the map key is the
	 * X-Y point of the surface maximum, and the map value is the calculated dissonance of the maximum.
	 */
	final HashMap<Coord,Shape3D> maxima = new HashMap<Coord,Shape3D>();
	
	
	/**
	 * Constructs the rendering pane.
	 * @param _data The coordinates of the surface, where the map key is the X-Y point of the surface, and the map value is the calculated dissonance.
	 * @param _sz The maximum number of frequencies on each domain axis.
	 * @param _remap Interface for taking a coordinate index proportional to a frequency value in the domain (either X or Y) and mapping it to a domain value that can be used for rendering.
	 * @param _sel Handles a user selection by clicking on the surface.
	 * @throws Throwable
	 */
	public DissSurfRenderingPane2( HashMap<Coord,Double> _data , int _sz , PointRemap _remap , SelectionHandler _sel ) throws Throwable {
		System.out.println("Constructing Fourier Rendering...");
		data = _data;
		sz = _sz;
		remap = _remap;
		sel = _sel;
		int count;
		yMax = 0.0;

		
		System.out.println( "Generaating Waves..." );
		
		
		count = 0;
		Iterator<Double> it = data.values().iterator();
		while( it.hasNext() )
		{
			if( count == 0 )
			{
				final double d = it.next();
				yMax = Math.max(yMax, Math.abs(d));
				yMax = Math.max(yMax, Math.abs(d));
			}
			else
			{
				final double d = it.next();
				yMax = Math.max(yMax, Math.abs(d));
				yMax = Math.max(yMax, Math.abs(d));
			}
			count++;
		}
		
		xMin = remap.remap( 0.0 );
		xMax = remap.remap( sz );
		
		System.out.println("Finished Const...");
		
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();

		canvas = new Canvas3D(config);
		add("Center", canvas);

		// Create a simple scene and attach it to the virtual universe
		BranchGroup scene = createSceneGraph();
		u = new SimpleUniverse(canvas);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		u.getViewingPlatform().setNominalViewingTransform();

		ViewingPlatform platform = u.getViewingPlatform();
		BoundingSphere infiniteBounds = new BoundingSphere(new Point3d(),
				10000.0);
		OrbitBehavior orbit = new OrbitBehavior(canvas);
		orbit.setSchedulingBounds(infiniteBounds);
		platform.setViewPlatformBehavior(orbit);

		// setup the lights
		Color3f ambientLightColor = new Color3f(0.30f, 0.30f, 0.30f);
		Color3f diffuseLightColor = new Color3f(0.75f, 0.75f, 0.75f);
		Vector3f diffuseLightDirection = new Vector3f(-1, -1, -1);
		
		PlatformGeometry pg = new PlatformGeometry();

		Light ambient = new AmbientLight(true, ambientLightColor);
		ambient.setInfluencingBounds(infiniteBounds);
		pg.addChild(ambient);

		Light directional = new DirectionalLight(true, diffuseLightColor,
		    diffuseLightDirection);
		directional.setInfluencingBounds(infiniteBounds);
		pg.addChild(directional);

		PointLight point = new PointLight();
		point.setEnable(true);
		point.setColor(new Color3f(2.0f, 2.0f, 2.0f));
		point.setAttenuation(0.75f, 0.25f, 0.0f);
		point.setInfluencingBounds(infiniteBounds);
		pg.addChild(point);

		platform.setPlatformGeometry(pg);

		u.addBranchGraph(scene);
		
	}
	
	/**
	 * Creates the scene graph for the surface.
	 * @return The scene graph for the surface.
	 */
	protected BranchGroup createSceneGraph()
	{
		System.out.println("Making Build...");
		
		BranchGroup objRoot = new BranchGroup();

		TransformGroup objTrans = new TransformGroup();
		objRoot.addChild(objTrans);
		
		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		int count;
		System.out.println("Made Attributes...");
		
		System.out.println( "Painting..." );
		double mult = bnd;
			
		
		int j;
		int k;
		
		final int ksz = ( sz - 1 ) * ( sz - 1 );
		final int bsz = 3 * 2 * ( ksz / 2 );
		final int asz = bsz + 3 * 2 * ( ksz % 2 );
		
		TriangleArray surfGeometryA = new TriangleArray( asz , TriangleArray.COORDINATES );
		TriangleArray surfGeometryB = new TriangleArray( bsz , TriangleArray.COORDINATES );
		
		int coordA = 0;
		int coordB = 0;
		int acnt = 0;
		
		for( j = 0 ; j < ( sz - 1 ) ; j++ )
		{
			
			for( k = 0 ; k < ( sz - 1 ) ; k++ )
			{
				final Coord c1 = new Coord( j , k );
				final Coord c2 = new Coord( j + 1 , k );
				final Coord c3 = new Coord( j , k + 1 );
				final Coord c4 = new Coord( j + 1 , k + 1 );
				double v1 = data.get( c1 );
				double v2 = data.get( c2 );
				double v3 = data.get( c3 );
				double v4 = data.get( c4 );
				if( Math.abs( ( j ) - ( k ) ) < 30 ) v1 = yMax;
				if( Math.abs( ( j + 1 ) - ( k ) ) < 30 ) v2 = yMax;
				if( Math.abs( ( j ) - ( k + 1 ) ) < 30 ) v3 = yMax;
				if( Math.abs( ( j + 1 ) - ( k + 1 ) ) < 30 ) v4 = yMax;
				
				double x1 = ( remap.remap( j ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y1 = ( remap.remap( k ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double z1 = ( v1 ) / ( yMax ) * mult - ( mult / 2.0 );
				
				double x2 = ( remap.remap( j + 1 ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y2 = ( remap.remap( k ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double z2 = ( v2 ) / ( yMax ) * mult - ( mult / 2.0 );
				
				double x3 = ( remap.remap( j ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y3 = ( remap.remap( k + 1 ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double z3 = ( v3 ) / ( yMax ) * mult - ( mult / 2.0 );
				
				double x4 = ( remap.remap( j + 1 ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y4 = ( remap.remap( k + 1 ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double z4 = ( v4 ) / ( yMax ) * mult - ( mult / 2.0 );
				
				if( acnt % 2 == 0 )
				{
					surfGeometryA.setCoordinate( coordA ,  new Point3d( x1 , y1 , z1 ) );
					coordA++;
				
					surfGeometryA.setCoordinate( coordA ,  new Point3d( x2 , y2 , z2 ) );
					coordA++;
				
					surfGeometryA.setCoordinate( coordA ,  new Point3d( x3 , y3 , z3 ) );
					coordA++;
				
					surfGeometryA.setCoordinate( coordA ,  new Point3d( x2 , y2 , z2 ) );
					coordA++;
				
					surfGeometryA.setCoordinate( coordA ,  new Point3d( x4 , y4 , z4 ) );
					coordA++;
				
					surfGeometryA.setCoordinate( coordA ,  new Point3d( x3 , y3 , z3 ) );
					coordA++;
				}
				else
				{
					surfGeometryB.setCoordinate( coordB ,  new Point3d( x1 , y1 , z1 ) );
					coordB++;
				
					surfGeometryB.setCoordinate( coordB ,  new Point3d( x2 , y2 , z2 ) );
					coordB++;
				
					surfGeometryB.setCoordinate( coordB ,  new Point3d( x3 , y3 , z3 ) );
					coordB++;
				
					surfGeometryB.setCoordinate( coordB ,  new Point3d( x2 , y2 , z2 ) );
					coordB++;
				
					surfGeometryB.setCoordinate( coordB ,  new Point3d( x4 , y4 , z4 ) );
					coordB++;
				
					surfGeometryB.setCoordinate( coordB ,  new Point3d( x3 , y3 , z3 ) );
					coordB++;
				}
				
				acnt++;
			}
			
		}
		
		GeometryInfo geometryInfoA = new GeometryInfo( surfGeometryA );
		NormalGenerator ngA = new NormalGenerator();
		ngA.generateNormals( geometryInfoA );
		GeometryArray resultA = geometryInfoA.getGeometryArray();
		
		GeometryInfo geometryInfoB = new GeometryInfo( surfGeometryB );
		NormalGenerator ngB = new NormalGenerator();
		ngB.generateNormals( geometryInfoB );
		GeometryArray resultB = geometryInfoB.getGeometryArray();
		
		Appearance appearanceA = new Appearance();
		Appearance appearanceB = new Appearance();
		
		{
			Color3f color = new Color3f( new Color( 255 , 255 , 0 ) );
			Color3f black = new Color3f( 0.0f , 0.0f , 0.0f );
			Color3f white = new Color3f( 1.0f , 1.0f , 0.9f );
			Texture texture = new Texture2D();
			TextureAttributes textAttr = new TextureAttributes();
			textAttr.setTextureMode( TextureAttributes.MODULATE );
			texture.setBoundaryModeS( Texture.WRAP );
			texture.setBoundaryModeT( Texture.WRAP );
			texture.setBoundaryColor( new Color4f( 0.0f , 1.0f , 0.0f , 0.0f ) );
			Material mat = new Material( color , black , color , white , 70f );
			appearanceA.setTextureAttributes( textAttr );
			appearanceA.setMaterial( mat );
			appearanceA.setTexture( texture );
			PolygonAttributes pa = new PolygonAttributes();
			pa.setBackFaceNormalFlip( true );
			pa.setCullFace( PolygonAttributes.CULL_NONE );
			appearanceA.setPolygonAttributes( pa );
		}
		
		{
			Color3f color = new Color3f( new Color( 192 , 192 , 0 ) );
			Color3f black = new Color3f( 0.0f , 0.0f , 0.0f );
			Color3f white = new Color3f( 0.75f , 0.75f , 0.675f );
			Texture texture = new Texture2D();
			TextureAttributes textAttr = new TextureAttributes();
			textAttr.setTextureMode( TextureAttributes.MODULATE );
			texture.setBoundaryModeS( Texture.WRAP );
			texture.setBoundaryModeT( Texture.WRAP );
			texture.setBoundaryColor( new Color4f( 0.0f , 1.0f , 0.0f , 0.0f ) );
			Material mat = new Material( color , black , color , white , 70f );
			appearanceB.setTextureAttributes( textAttr );
			appearanceB.setMaterial( mat );
			appearanceB.setTexture( texture );
			PolygonAttributes pa = new PolygonAttributes();
			pa.setBackFaceNormalFlip( true );
			pa.setCullFace( PolygonAttributes.CULL_NONE );
			appearanceB.setPolygonAttributes( pa );
		}
		
		Shape3D shapeA = new Shape3D( resultA , appearanceA );
		objTrans.addChild(shapeA);
		
		Shape3D shapeB = new Shape3D( resultB , appearanceB );
		objTrans.addChild(shapeB);
		
		
		
		for( j = 1 ; j < ( sz - 1 ) ; j++ )
		{
			
			for( k = 1 ; k < ( sz - 1 ) ; k++ )
			{
				final Coord c1 = new Coord( j , k );
				double v1 = data.get( c1 );
				if( Math.abs( ( j ) - ( k ) ) >= 30 )
				{
					boolean pass = true;
					int jj;
					int kk;
					for( jj = -1 ; jj <= 1 ; jj++ )
					{
						for( kk = -1 ; kk <= 1 ; kk++ )
						{
							if( ( jj != 0 ) || ( kk != 0 ) )
							{
								final Coord c2 = new Coord( j + jj , k + kk );
								double v2 = data.get( c2 );
								if( Math.abs( ( j + jj ) - ( k + kk ) ) < 30 ) v2 = yMax;
								pass = pass && ( v1 < v2 );
							}
						}
					}
					
					
					if( pass )
					{
						double x1 = ( remap.remap( j ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
						double y1 = ( remap.remap( k ) - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
						double z1 = ( v1 ) / ( yMax ) * mult - ( mult / 2.0 );
						double z1p = ( -yMax ) / ( yMax ) * mult - ( mult / 2.0 );
						Point3d[] myCoords = new Point3d[ 2 ];
						myCoords[ 0 ] = new Point3d(x1, y1, z1);
						myCoords[ 1 ] = new Point3d(x1, y1, 0.95 * z1 + 0.05 * z1p);
						
						LineArray larr = new LineArray(myCoords.length,
								GeometryArray.COORDINATES);
						larr.setCoordinates(0, myCoords);

						Shape3D shape = new Shape3D(larr, AczonColor.getLineGreen() );
						shape.setCapability( Shape3D.ALLOW_APPEARANCE_WRITE );
						objTrans.addChild(shape);
						maxima.put( c1 , shape );
					}
					
					
				}
				
			}
			
		}
		
		
		
		System.out.println("Finished Build...");
		
		objRoot.compile();
		
		canvas.addMouseListener( this );
		
		return( objRoot );
	}
	
	/**
	 * The minimum rendering value in the surface domain (either domain axis).
	 */
	double xMin = 0.0;
	
	/**
	 * The maximum rendering value in the surface domain (either domain axis).
	 */
	double xMax = 0.0;

	/**
	 * The maximum value in the range of surface elevations.
	 */
	double yMax = 0.0;
	
	@Override
	public Dimension minimumSize()
	{
		return( new Dimension( 400 , 400 ) );
	}
	
	@Override
	public Dimension preferredSize()
	{
		return( new Dimension( 400 , 400 ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Calculates a Point3D equivalent to a mouse coordinate.
	 * @param x The input mouse coordinate X.
	 * @param y The input mouse coordinate Y.
	 * @return The output Point3D.
	 */
	protected Point3d calcMouseCoord( double x , double y )
	{
		Rectangle bnd = bounds();
		double max = Math.max( bnd.height , bnd.width ) / 2.0;
		double xv = ( x - ( bnd.width / 2.0 ) ) / max;
		double yv = - ( y - ( bnd.height / 2.0 ) ) / max;
		return( new Point3d( xv , yv , 0 ) );
	}
	
	/**
	 * NDC X-coordinate.
	 */
	protected double ndcAU;

	/**
	 * NDC Y-coordinate.
	 */
	protected double ndcBU;
	
	/**
	 * Converts X-Y to NDC coordinates.
	 * @param x The input X-coordinate.
	 * @param y The input Y-coordinate.
	 */
	protected void calcUFromXY(double x, double y) {
		double aX = -1.0;
		double aY = -1.0;
		double bX = 1.0;
		double bY = 1.0;

		double cX = (1.0 - HORIZ_STRT_PCT) * aX + HORIZ_STRT_PCT * bX;
		double cY = (1.0 - VERT_STRT_PCT) * aY + VERT_STRT_PCT * bY;
		double dX = (1.0 - HORIZ_END_PCT) * aX + HORIZ_END_PCT * bX;
		double dY = (1.0 - VERT_END_PCT) * aY + VERT_END_PCT * bY;

		ndcAU = (x - cX) / (dX - cX);
		ndcBU = (y - cY) / (dY - cY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		
		System.out.println( "Got Mouse Press..." );
		
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		calcUFromXY(pt.x, pt.y);
		final double au = ndcAU;
		final double bu = ndcBU;
		Shape3D shape = null;
		double dist = 9E+9;
		
		Iterator<Coord> it = maxima.keySet().iterator();
		while( it.hasNext() )
		{
			Coord c = it.next();
			double pau = ( remap.remap( c.x ) - xMin ) / ( xMax - xMin );
			double pbu = ( remap.remap( c.y ) - xMin ) / ( xMax - xMin );
			double dst = ( au - pau ) * ( au - pau ) + ( bu - pbu ) * ( bu - pbu );
			if( dst < dist )
			{
				dist = dst;
				shape = maxima.get( c );
			}
		}
		
		it = maxima.keySet().iterator();
		while( it.hasNext() )
		{
			Coord c = it.next();
			Shape3D s = maxima.get( c );
			if( s == shape )
			{
				System.out.println( "**************" );
				System.out.println( au );
				System.out.println( bu );
				s.setAppearance( AczonColor.getLineGreen() );
				if( e.isShiftDown() )
				{
					double pau = ( remap.remap( c.x ) - xMin ) / ( xMax - xMin );
					double pbu = ( remap.remap( c.y ) - xMin ) / ( xMax - xMin );
					sel.handleSelection(pau, pbu);
				}
			}
			else
			{
				s.setAppearance( AczonColor.getLineOrange() );
			}
		}
		
		System.out.println( "---" );
		System.out.println( shape );
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	
}

