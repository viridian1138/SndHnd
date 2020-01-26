




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
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import aazon.AazonImmutableEnt;
import aazon.AazonSelfActive;
import aazon.bool.AazonMutableBool;
import aazon.vect.AazonMutableVect;

import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewInfo;
import com.sun.j3d.utils.universe.ViewingPlatform;


/**
 * Factory producing a root for an Aczon canvas.
 * 
 * @author tgreen
 *
 */
public class AczonRootFactory implements AazonSelfActive {
	
	/**
	 * The root of the scene graph.
	 */
	private BranchGroup objRoot;
	
	/**
	 * The universe of the root factory.
	 */
	protected volatile SimpleUniverse universe = null;

	/**
	 * The canvas of the root factory.
	 */
	protected volatile AczonCanvas canvas = null;
	
	/**
	 * The view info of the root factory.
	 */
	protected volatile ViewInfo viewInfo = null;
	
	/**
	 * The root factory's collection of self-active instances.
	 */
	protected ArrayList ar = null;
	
	
	/**
	 * Creates the scene graph for the root factory.
	 * @param ent The input AczonImmutableEntFactory producing the AazonImmutableEnt to be displayed.
	 * @return The root of the scene graph.
	 */
	private BranchGroup createSceneGraph( final AczonImmutableEntFactory ent ) // For Instance, AazonImmutableGroup
	{
		objRoot = new BranchGroup();

		TransformGroup objTrans = new TransformGroup();
		objRoot.addChild(objTrans);
		final AazonImmutableEnt nt = ent.getEnt( this );
		ar = new ArrayList();
		nt.genSelfActive( ar );
		Node objNode = nt.getNode();
		objTrans.addChild(objNode);

		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(1000);
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(new Color3f(1.0f, 1.0f, 1.0f));
		ambient.setInfluencingBounds(bounds);
		objRoot.addChild(ambient);

		Color co = Color.GRAY;
		Background bkg = new Background((float) (co.getRed() / 255.0),
				(float) (co.getGreen() / 255.0), (float) (co.getBlue() / 255.0));
		BoundingSphere infiniteBounds = new BoundingSphere(new Point3d(),
				10000.0);
		bkg.setApplicationBounds(infiniteBounds);
		objRoot.addChild(bkg);
		
		return( objRoot );
	}
	
	/**
	 * Initializes the root factory.
	 * @param ent The input AczonImmutableEntFactory producing the AazonImmutableEnt to be displayed.
	 */
	private void init( AczonImmutableEntFactory ent )
	{
		getCanvas().setBackground(Color.GRAY);
		
		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		getUniverse().getViewingPlatform().setNominalViewingTransform();

		// Create a simple scene and attach it to the virtual universe
		BranchGroup scene = createSceneGraph( ent );

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		getUniverse().getViewingPlatform().setNominalViewingTransform();

		ViewingPlatform platform = getUniverse().getViewingPlatform();
		BoundingSphere infiniteBounds = new BoundingSphere(new Point3d(),
				10000.0);
		// OrbitBehavior orbit = new OrbitBehavior(canvas);
		// orbit.setSchedulingBounds(infiniteBounds);
		// platform.setViewPlatformBehavior(orbit);

		// setup the lights
		Color3f ambientLightColor = new Color3f(1.0f, 1.0f, 1.0f);
		/*
		 * Color3f diffuseLightColor = new Color3f(0.75f, 0.75f, 0.75f);
		 * Vector3f diffuseLightDirection = new Vector3f(-1, -1, -1);
		 */
		PlatformGeometry pg = new PlatformGeometry();

		Light ambient = new AmbientLight(true, ambientLightColor);
		ambient.setInfluencingBounds(infiniteBounds);
		pg.addChild(ambient);

		/*
		 * Light directional = new DirectionalLight(true, diffuseLightColor,
		 * diffuseLightDirection);
		 * directional.setInfluencingBounds(infiniteBounds);
		 * pg.addChild(directional);
		 */

		PointLight point = new PointLight();
		point.setEnable(true);
		point.setColor(new Color3f(2.0f, 2.0f, 2.0f));
		point.setAttenuation(0.75f, 0.25f, 0.0f);
		point.setInfluencingBounds(infiniteBounds);
		pg.addChild(point);

		platform.setPlatformGeometry(pg);
		
		getUniverse().addBranchGraph(scene);
	}
	
	/**
	 * Constructs the factory for an AczonImmutableEntFactory.
	 * @param ent The input AczonImmutableEntFactory producing the AazonImmutableEnt to be displayed.
	 */
	public AczonRootFactory( AczonImmutableEntFactory ent )
	{
		init( ent );
	}
	
	/**
	 * Constructs the factory for an AazonImmutableEnt.
	 * @param ent The input AazonImmutableEnt to be displayed.
	 */
	public AczonRootFactory( final AazonImmutableEnt ent )
	{
		this( new AczonImmutableEntFactory()
		{
			public AazonImmutableEnt getEnt( AczonRootFactory fac )
			{
				return( ent );
			}
		} );
	}
	
	/**
	 * Gets the canvas for the root factory.
	 * @return The canvas for the root factory.
	 */
	public synchronized AczonCanvas getCanvas()
	{
		if( canvas == null )
		{
			GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
			canvas = new AczonCanvas(config);
		}
		
		return( canvas );
	}
	
	/**
	 * Returns the universe for the root factory, constructing one if necessary.
	 * @return The universe for the root factory.
	 */
	public synchronized SimpleUniverse getUniverse()
	{
		if( universe == null )
		{
			universe = new SimpleUniverse( getCanvas() );
			ViewingPlatform viewingPlatform = universe.getViewingPlatform(); 
			ViewPlatform vp = viewingPlatform.getViewPlatform(); 
			viewingPlatform.detach(); 
			vp.setCapability( ViewPlatform.ALLOW_POLICY_READ ); 
			vp.setCapability( Node.ALLOW_LOCAL_TO_VWORLD_READ ); 
			universe.addBranchGraph( viewingPlatform ); 
		}
		
		return( universe );
	}
	
	/**
	 * Deletes the graph from the universe.
	 */
	public void delete()
	{
		getUniverse().cleanup();
		getUniverse().removeAllLocales();
	}
	
	/**
	 * Constructs a boolean indicating whether the mouse is inside the window.
	 * @param mf Factory for producing booleans indicating whether the mouse is inside the window.
	 * @return A boolean indicating whether the mouse is inside the window.
	 */
	public AazonMutableBool constructMouseEnterBool( AczonMouseEnterBoolFactory mf )
	{
		AczonMouseEnterBool bool = mf.createBool( this );
		getCanvas().addMouseListener( bool );
		return( bool );
	}
	
	/**
	 * Constructs a boolean indicating whether the mouse button is down.
	 * @param mf Factory for producing booleans indicating whether the mouse is down.
	 * @return A boolean indicating whether the mouse button is down.
	 */
	public AazonMutableBool constructMouseDownBool( AczonMouseDownBoolFactory mf )
	{
		AczonMouseDownBool bool = mf.createBool( this );
		getCanvas().addMouseListener( bool );
		return( bool );
	}
	
	/**
	 * Constructs a vector indicating the resizing of the window.
	 * @param mf Factory for producing vectors indicating the resizing of the window.
	 * @return A vector indicating the resizing of the window.
	 */
	public AazonMutableVect constructResizeVect( AczonResizeVectFactory mf )
	{
		AczonResizeVect vect = mf.createVect( this );
		getCanvas().addResizeVect( vect );
		return( vect );
	}
	
	/**
	 * Returns the view info of the root factory, constructing one if necessary.
	 * @return The view info of the root factory.
	 */
	public ViewInfo getViewInfo()
	{
		if( viewInfo == null )
		{
			viewInfo = new ViewInfo( getCanvas().getView() );
		}
		
		return( viewInfo );
	}
	
	/**
	 * Resets the view info of the root factory.
	 */
	public void resetView()
	{
		viewInfo = null;
	}
	
	/**
	 * Translates an x-y point into virtual world coordinates.
	 * @param x The X-coordinate of the point.
	 * @param y The Y-coordinate of the point.
	 * @return The matching virtual world coordinate point.
	 */
	public Point3d translatePoint( final int x , final int y ) {

		ViewInfo viewInfo = getViewInfo();

		// construct an empty point 
		Point3d p3 = new Point3d();

		// after this, p3 will contain the 'metres' distance of awtPoint 
		// from the origin of the image plate 
		viewInfo.getPixelLocationInImagePlate( getCanvas() , x , y , p3 );

		// construct an empty transform to hold our plate location 
		Transform3D toVirtualUniverse = new Transform3D();

		// stores the transform from the image plate to the virtual world 
		viewInfo.getImagePlateToVworld( getCanvas() , toVirtualUniverse , null );

		// apply the image plate transform to p3 - p3 is now awtPoint in 
		// virtual world coordinates! 
		toVirtualUniverse.transform(p3);

		return p3; 
		} 
	
	/**
	 * Adds a list of self-active instances to the factory's collection.
	 * @param in The input list of self-active instances.
	 */
	public void genSelfActive( ArrayList in )
	{
		ar.add( in );
	}
	

}

