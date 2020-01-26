




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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Panel;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Light;
import javax.media.j3d.LineArray;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import aczon.AczonColor;

import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;



/**
 * 
 * Plots a phase diagram of the waveform as described by e.g. Chaotic Dynamics
 * by G. L. Baker and J. P. Gollub, Cambridge University Press, 1990. ISBN
 * 0-521-38897-X
 * 
 * @author thorngreen
 * 
 */
public class WaveRenderingPane extends Panel {
	
	/**
	 * The universe in which to plot the phase diagram.
	 */
	private SimpleUniverse u = null;

	/**
	 * The canvas in which to plot the phase diagram.
	 */
	protected Canvas3D canvas = null;
	

	/**
	 * Constructor.
	 * @param note The nore for which to render the waveform.
	 * @throws Throwable
	 */
	public WaveRenderingPane(NoteDesc note) throws Throwable {
		System.out.println("Constructing...");
		xv = new double[SAMPLE_LEN];
		yv = new double[SAMPLE_LEN];
		int count;
		yMax = 0.0;
		final double freq = 2.0;
		// final double freq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl(/*2*/ 4,
		//		NoteTable.STEPS_E);
		
		final int NUM_CORES = CpuInfo.getNumCores();
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		
		final double steadyState = note.getSteadyStateWaveNum();;
		final WaveForm[] waves  = note.getWaveformArray();
		
		
		int ccnt;
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				/**
				 * Generates part of the dataset to be rendered.
				 */
				public void run()
				{
					System.out.println( "Started R1..." );
					int count;
					for (count = core; count < SAMPLE_LEN; count += NUM_CORES) {
						double u = ((double) count) / DIVIDER;
						final WaveForm w = waves[ core ];
						double theta = u + steadyState;
						double eval1 = w.eval( ( theta ) / freq );
						xv[count] = theta;
						yv[count] = eval1;
					}
					System.out.println( "Notify..." );
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		
		System.out.println( "Starting Threads..." );
		
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);
		
		
		for (count = 0; count < SAMPLE_LEN; count++) {
			if( count == 0 )
			{
				xMin = xv[ count ];
				xMax = xv[ count ];
				yMax = Math.max(yMax, Math.abs(yv[count]));
			}
			else
			{
				xMin = Math.min(xMin, xv[count]);
				xMax = Math.max(xMax, xv[count]);
				yMax = Math.max(yMax, Math.abs(yv[count]));
			}
		}
		
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
		//OrbitBehavior orbit = new OrbitBehavior(canvas);
		//orbit.setSchedulingBounds(infiniteBounds);
		//platform.setViewPlatformBehavior(orbit);

		// setup the lights
		Color3f ambientLightColor = new Color3f(0.30f, 0.30f, 0.30f);
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

		u.addBranchGraph(scene);
	}
	
	
	/**
	 * Creates the scene graph for the phase diagram.
	 * @return The scene graph for the phase diagram.
	 */
	protected BranchGroup createSceneGraph()
	{
		System.out.println("Making Build...");
		
		BranchGroup objRoot = new BranchGroup();

		TransformGroup objTrans = new TransformGroup();
		objRoot.addChild(objTrans);
		
		int sz = xv.length;
		Point3d[] myCoords = new Point3d[ 2 * ( sz - 1 ) ];
		
		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		int count;
		System.out.println("Made Attributes...");
		
		   System.out.println( "Painting..." );
			double mult = bnd;
			for (count = 1; count < sz; count++) {
				double x1 = ( xv[count-1] - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y1 = ( yv[count-1] + yMax ) / ( 2.0 * yMax ) * mult - ( mult / 2.0 );
				double x2 = ( xv[count] - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y2 = ( yv[count] + yMax ) / ( 2.0 * yMax ) * mult - ( mult / 2.0 );
				myCoords[ 2 * ( count - 1 ) ] = new Point3d(x1,y1,0);
				myCoords[ 2 * ( count - 1 ) + 1 ] = new Point3d(x2,y2,0);
			}
		
		LineArray larr = new LineArray(myCoords.length,
				GeometryArray.COORDINATES);
		larr.setCoordinates(0, myCoords);

		Shape3D shape = new Shape3D(larr, AczonColor.getLineYellow() );
		objTrans.addChild(shape);
		
		System.out.println("Finished Build...");
		
		objRoot.compile();
		
		return( objRoot );
	}
	
	

	/**
	 * The number of samples to be collected for the plot.
	 */
	public static final int SAMPLE_LEN = 16000;

	/**
	 * The amount by which to divide the sample number in order to get the parameter value in phase space.
	 */
	public static final double DIVIDER = 4000.0;

	/**
	 * The X-Axis positions to be plotted.
	 */
	double[] xv;

	/**
	 * The Y-Axis positions to be plotted.
	 */
	double[] yv;
	
	/**
	 * The minimum X-Axis position to be plotted.
	 */
	double xMin = 0.0;
	
	/**
	 * The maximum X-Axis position to be plotted.
	 */
	double xMax = 0.0;

	/**
	 * The maximum of the absolute value of the Y-Axis positions.
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

}

