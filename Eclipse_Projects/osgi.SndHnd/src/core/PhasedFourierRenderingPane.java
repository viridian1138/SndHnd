




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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

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

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import cwaves.CosineWaveform;
import cwaves.SineWaveform;




/**
 * Performs a Fourier transform on a note, and then displays a 3-D view of the
 * phases of the Fourier coefficients versus frequency.
 * 
 * @author tgreen
 *
 */
public class PhasedFourierRenderingPane extends Panel /* implements MouseListener */ {
	
	/**
	 * The universe in which to plot the view.
	 */
	private SimpleUniverse u = null;

	/**
	 * The canvas in which to plot the surface.
	 */
	protected Canvas3D canvas = null;
	
	/**
	 * Sine function used to perform Fourier transforms.
	 * Note: the wavelength of the function is unity rather than 2 * PI.
	 */
	protected static final SineWaveform sine = new SineWaveform();
	
	/**
	 * Cosine function used to perform Fourier transforms.
	 * Note: the wavelength of the function is unity rather than 2 * PI.
	 */
	protected static final CosineWaveform cosine = new CosineWaveform();
	
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
	 * The steady-state wave number at which to start the Fourier analysis.
	 */
	static double waveStrt;
	
	
	/**
	 * Evaluates the complex-number Fourier coefficient.
	 * @param wave The WaveForm for which to evaluate the Fourier coefficient.
	 * @param ratio The frequency ratio at which to evaluate the Fourier coefficient.
	 * @return Array of two elements containing the complex-number Fourier coefficients.
	 */
	public static double[] evalCoeff2( WaveForm wave , double ratio )
	{
		int count;
		double ffreq = 1.0;
		if( wave.useAutocorrelation() )
		{
			ffreq = NoteTable.getNoteFrequencyEqualTemperedScale_Intl(/*2*/ 4,
					NoteTable.STEPS_E);
		}
		final double freq = ffreq;
		double avgSamp = 0.0;
		for( count = 0 ; count < SAMPLE_LEN ; count++ )
		{
			double u = ((double) count) / DIVIDER;
			WaveForm w = wave;
			double theta = u + waveStrt;
			double eval1 = w.eval( ( theta ) / freq );
			avgSamp += eval1;
			
		}
		avgSamp = avgSamp / SAMPLE_LEN;
		double totCos = 0.0;
		double totSin = 0.0;
		for (count = 0; count < SAMPLE_LEN; count++) {
			double u = ((double) count) / DIVIDER;
			WaveForm w = wave;
			double theta = u + waveStrt;
			double eval1 = w.eval( ( theta ) / freq );
			totSin += ( eval1 - avgSamp ) * ( sine.eval( ratio * u ) );
			totCos += ( eval1 - avgSamp ) * ( cosine.eval( ratio * u ) );
		}
		double[] dd = { totCos , totSin };
		return( dd );
	}
	
	
	/**
	 * Takes in a set of complex-number Fourier coefficients, and produces a matching set of complex-number Fourier 
	 * coefficients that is normalized to the Fourier coefficient with the largest magnitude.
	 * @param yv The input set of complex-number Fourier coefficients.
	 * @param yv2 The output set of complex-number Fourier coefficients normalized to the Fourier coefficient with the largest magnitude.
	 */
	protected void processYv( Object[] yv , Object[] yv2 )
	{
		int count;
		final int len = yv.length;
		
		double max = 0.0;
		int maxIndex = 0;
		
		for( count = 0 ; count < len ; count++ )
		{
			final double[] d = (double[])( yv[ count ] );
			final double d0 = d[ 0 ];
			final double d1 = d[ 1 ];
			final double dm = Math.sqrt( d0 * d0 + d1 + d1 );
			if( dm > max )
			{
				max = dm;
				maxIndex = count;
			}
		}
		
		final double[] ds = (double[])( yv[ maxIndex ] );
		final double da0 = ds[ 0 ];
		final double da1 = - ds[ 1 ];
		
		for( count = 0 ; count < len ; count++ )
		{
			final double[] d = (double[])( yv[ count ] );
			final double d0 = d[ 0 ];
			final double d1 = d[ 1 ];
			final double dd0 = d0 * da0 - d1 * da1;
			final double dd1 = d0 * da1 + d1 * da0;
			final double[] dx = { dd0 , dd1 };
			yv2[ count ] = dx;
		}
	}
	
	
	/**
	 * Constructor.
	 * @param note The note for which to display Fourier coefficients.
	 * @throws Throwable
	 */
	public PhasedFourierRenderingPane(NoteDesc note) throws Throwable {
		System.out.println("Constructing Fourier Rendering...");
		xv = new double[SAMPLE_LEN];
		yv = new Object[SAMPLE_LEN];
		yv2 = new Object[SAMPLE_LEN];
		int count;
		yMax = 0.0;
		final double minLn = Math.log( MIN_RATIO );
		final double maxLn = Math.log( MAX_RATIO );

		
		System.out.println( "Generaating Waves..." );
		
		final int NUM_CORES = CpuInfo.getNumCores();
		
		waveStrt = note.getSteadyStateWaveNum();
		final WaveForm[] waves = note.getWaveformArray();
		final boolean[] b = CpuInfo.createBool( false );
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		
		
		System.out.println( "Generaating Runnables..." );
		
		int ccnt;
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				/**
				 * Calculates the Fourier coefficients.
				 */
				public void run()
				{
					int count;
					for (count = core; count < SAMPLE_LEN; count += NUM_CORES ) {
						final double u = ((double) count) / ( SAMPLE_LEN - 1 );
						final double rl = (1-u) * minLn + u * maxLn;
						final double ratio = Math.exp( rl );
			
						xv[count] = count;
						yv[count] = evalCoeff2( waves[ core ] , ratio );
					}
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
		
		processYv( yv , yv2 );
		
		for( count = 0 ; count < SAMPLE_LEN ; count++ )
		{
			if( count == 0 )
			{
				xMin = xv[ count ];
				xMax = xv[ count ];
				final double[] d = (double[])( yv2[ count ] );
				yMax = Math.max(yMax, Math.abs(d[0]));
				yMax = Math.max(yMax, Math.abs(d[1]));
			}
			else
			{
				xMin = Math.min(xMin, xv[count]);
				xMax = Math.max(xMax, xv[count]);
				final double[] d = (double[])( yv2[ count ] );
				yMax = Math.max(yMax, Math.abs(d[0]));
				yMax = Math.max(yMax, Math.abs(d[1]));
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
		OrbitBehavior orbit = new OrbitBehavior(canvas);
		orbit.setSchedulingBounds(infiniteBounds);
		platform.setViewPlatformBehavior(orbit);

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
	 * Creates the scene graph for the view.
	 * @return The scene graph for the view.
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
				double[] d1 = (double[])( yv2[ count - 1 ] );
				double[] d2 = (double[])( yv2[ count ] );
				
				double x1 = ( xv[count-1] - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y1 = ( d1[0] ) / ( yMax ) * mult - ( mult / 2.0 );
				double z1 = ( d1[1] ) / ( yMax ) * mult - ( mult / 2.0 );
				
				double x2 = ( xv[count] - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y2 = ( d2[0] ) / ( yMax ) * mult - ( mult / 2.0 );
				double z2 = ( d2[1] ) / ( yMax ) * mult - ( mult / 2.0 );
				
				myCoords[ 2 * ( count - 1 ) ] = new Point3d(x1,y1,z1);
				myCoords[ 2 * ( count - 1 ) + 1 ] = new Point3d(x2,y2,z2);
			}
		
		LineArray larr = new LineArray(myCoords.length,
				GeometryArray.COORDINATES);
		larr.setCoordinates(0, myCoords);

		Shape3D shape = new Shape3D(larr, AczonColor.getLineYellow() );
		objTrans.addChild(shape);
		
		System.out.println("Finished Build...");
		
		objRoot.compile();
		
		//canvas.addMouseListener( this );
		
		return( objRoot );
	}
	
	
	/**
	 * The minimum frequency ratio for which to calculate Fourier coefficients.
	 */
	public static final double MIN_RATIO = 0.02; // = 0.2;
	
	/**
	 * The maximum frequency ratio for which to calculate Fourier coefficients.
	 */
	public static final double MAX_RATIO = 20;

	/**
	 * The number of samples over which to evaluate each Fourier coefficient, and also
	 * the number of frequency ratios over which to generate Fourier coefficients.
	 */
	public static final int SAMPLE_LEN = 800;

	/**
	 * The amount by which to divide the sample number in order to get the parameter value in phase space.
	 */
	public static final double DIVIDER = 100.0;

	/**
	 * Array of X-axis values along the frequency range.
	 */
	double[] xv;

	/**
	 * Array of complex-number phased Fourier coefficients.
	 */
	Object[] yv;
	
	/**
	 * Array of complex-number phased Fourier coefficients normalized to the Fourier coefficient with the largest magnitude.
	 */
	Object[] yv2;
	
	
	/**
	 * The minimum X-Axis position to be plotted in the initial view before the plot is rotated.
	 */
	double xMin = 0.0;
	
	/**
	 * The maximum X-Axis position to be plotted in the initial view before the plot is rotated.
	 */
	double xMax = 0.0;

	/**
	 * The maximum of the absolute value of the Y-Axis positions in the initial view before the plot is rotated.
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
	 * Returns a Point3D corresponding to a set of mouse coordinates.
	 * @param x The X-Axis mouse coordinate.
	 * @param y The Y-Axis mouse coordinate.
	 * @return The corresponding Point3D.
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
	 * This is a frequency axis in the initial view before the plot is rotated.
	 */
	protected double freqU;

	/**
	 * NDC Y-coordinate.
	 * This is a Fourier coefficient axis in the initial view before the plot is rotated.
	 */
	protected double coeffU;
	
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

		freqU = (x - cX) / (dX - cX);
		coeffU = (y - cY) / (dY - cY);
	}

	/**
	 * Assuming that the plot has remained in the initial view before the plot is rotated,
	 * handles a mouse-press by printing the frequency ratio corresponding to the
	 * X-Axis position of the mouse.
	 * @param e The input mouse event.
	 */
	public void mousePressed(MouseEvent e) {
		final double minLn = Math.log( MIN_RATIO );
		final double maxLn = Math.log( MAX_RATIO );
		
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		calcUFromXY(pt.x, pt.y);
		double u = freqU;
		System.out.println( "---" );
		System.out.println( u );
		final double rl = (1-u) * minLn + u * maxLn;
		final double ratio = Math.exp( rl );
		System.out.println( ratio );
		
		
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

