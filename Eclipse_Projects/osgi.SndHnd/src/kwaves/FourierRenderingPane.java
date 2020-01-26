




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








package kwaves;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.vecmath.Point3d;

import aazon.AazonImmutableEnt;
import aazon.dbl.AazonBaseImmutableDbl;
import aazon.dbl.AazonImmutableDbl;
import abzon.AbzonImmutableGeneralPathFactory;
import abzon.AbzonSmartShape;
import aczon.AczonColor;
import aczon.AczonRootFactory;
import core.CpuInfo;
import core.NoteDesc;
import core.NoteTable;
import core.WaveForm;
import cwaves.CosineWaveform;
import cwaves.SineWaveform;


/**
 * Pane for rendering the Fourier spectrum of a WaveForm.
 * 
 * @author tgreen
 *
 */
public class FourierRenderingPane extends Panel implements MouseListener {
	
	/**
	 * The AczonRootFactory for the Fourier spectrum scene graph.
	 */
	protected AczonRootFactory rootFactory = null;
	
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
	
	
	//public static final double BASE_AUTOCOR_FFREQ = NoteTable.getNoteFrequencyEqualTemperedScale_Intl(/*2*/ 4,
	//		NoteTable.STEPS_E);
	
	//public static final double BASE_AUTOCOR_FFREQ = NoteTable.getNoteFrequencyEqualTemperedScale_Intl(3,
	//		NoteTable.STEPS_D);
	
	/**
	 * The base autocorrelation estimate of the fundamental pitch from which estimate the Fourier spectrum (if needed).
	 */
	public static double BASE_AUTOCOR_FFREQ = NoteTable.getNoteFrequencyEqualTemperedScale_Intl(1,
			NoteTable.STEPS_A);
	
	/**
	 * The desired flatness for the curve iteration.
	 */
	protected AazonImmutableDbl flatness = new AazonBaseImmutableDbl( 0.01 ); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	
	/**
	 * Sets the base autocorrelation estimate of the fundamental pitch from which estimate the Fourier spectrum (if needed).
	 * @param in The base autocorrelation estimate of the fundamental pitch from which estimate the Fourier spectrum (if needed).
	 */
	public static void setBaseAutocorFfreq( double in ) // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	{
		BASE_AUTOCOR_FFREQ = in;
	}

//	public static double evalCoeff( NoteDesc note , double ratio )
//	{
//		int count;
//		final double freq = 2.0;
//		//final double freq = BASE_AUTOCOR_FFREQ;
//		double avgSin = 0.0;
//		double avgCos = 0.0;
//		for( count = 0 ; count < SAMPLE_LEN ; count++ )
//		{
//			double u = ((double) count) / DIVIDER;
//			avgSin += sine.eval( ratio * u );
//			avgCos += cosine.eval( ratio * u );
//			
//		}
//		avgSin = avgSin / SAMPLE_LEN;
//		avgCos = avgCos / SAMPLE_LEN;
//		double totCos = 0.0;
//		double totSin = 0.0;
//		for (count = 0; count < SAMPLE_LEN; count++) {
//			double u = ((double) count) / DIVIDER;
//			WaveForm w = note.getWaveform();
//			double theta = u + note.getSteadyStateWaveNum();
//			double eval1 = w.eval( ( theta ) / freq );
//			totSin += ( eval1 ) * ( sine.eval( ratio * u ) - avgSin );
//			totCos += ( eval1 ) * ( cosine.eval( ratio * u ) - avgCos );
//		}
//		return( Math.sqrt( totSin * totSin + totCos * totCos ) );
//	}
	
	
	/**
	 * The wave number at which to start the Fourier analysis (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 */
	public static double waveStrt;
	
	/**
	 * Evaluates a Fourier coefficient.
	 * @param wave The WaveForm for which to evaluate the Fourier coefficient.
	 * @param ratio The ratio of a wavelength over which to evaluate the Fourier coefficient.
	 * @return The evaluated Fourier coefficient.
	 */
	public static double evalCoeff2( WaveForm wave , double ratio )
	{
		int count;
		double ffreq = 1.0;
		if( wave.useAutocorrelation() )
		{
			ffreq = BASE_AUTOCOR_FFREQ;
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
		return( Math.sqrt( totSin * totSin + totCos * totCos ) );
	}
	
	
	/**
	 * Constructs the Fourier rendering pane.
	 * @param note The note for which to calculate the Fourier coefficients.
	 * @throws Throwable
	 */
	public FourierRenderingPane(NoteDesc note) throws Throwable {
		System.out.println("Constructing Fourier Rendering...");
		xv = new double[SAMPLE_LEN];
		yv = new double[SAMPLE_LEN];
		int count;
		yMax = 0.0;
		final double minLn = Math.log( MIN_RATIO );
		final double maxLn = Math.log( MAX_RATIO );

		
		System.out.println( "Generating Waves..." );
		
		final int NUM_CORES = CpuInfo.getNumCores();
		
		waveStrt = note.getSteadyStateWaveNum();
		final WaveForm[] waves = note.getWaveformArray();
		final boolean[] b = CpuInfo.createBool( false );
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		
		
		System.out.println( "Generating Runnables..." );
		
		int ccnt;
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
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
		
		for( count = 0 ; count < SAMPLE_LEN ; count++ )
		{
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
		AczonRootFactory scene = createSceneGraph();

		add(BorderLayout.CENTER, scene.getCanvas() );
	}
	
	
	/**
	 * Constructs the FourierRenderingPane.
	 * @param waves A copy for each core thread of the waveform to be analyzed.
	 * @param _waveStrt The wave number at which to start the Fourier analysis (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 * @throws Throwable
	 */
	public FourierRenderingPane( final WaveForm[] waves , final double _waveStrt ) throws Throwable {
		System.out.println("Constructing Fourier Rendering...");
		xv = new double[SAMPLE_LEN];
		yv = new double[SAMPLE_LEN];
		int count;
		yMax = 0.0;
		final double minLn = Math.log( MIN_RATIO );
		final double maxLn = Math.log( MAX_RATIO );

		
		System.out.println( "Generating Waves..." );
		
		final int NUM_CORES = CpuInfo.getNumCores();
		
		waveStrt = _waveStrt;
		final boolean[] b = CpuInfo.createBool( false );
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		
		
		System.out.println( "Generating Runnables..." );
		
		int ccnt;
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				/**
				 * Evaluates Fourier coefficients.
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
		
		for( count = 0 ; count < SAMPLE_LEN ; count++ )
		{
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
		AczonRootFactory scene = createSceneGraph();

		add(BorderLayout.CENTER, scene.getCanvas() );
	}
	
	
	/**
	 * Returns the scene graph for the Fourier spectrum.
	 * @return The scene graph for the Fourier spectrum.
	 */
	protected AczonRootFactory createSceneGraph()
	{
		System.out.println("Making Build...");
		
		int sz = xv.length;
		GeneralPath pointsa = new GeneralPath();
		
		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		int count;
		System.out.println("Made Attributes...");
		
		   System.out.println( "Painting..." );
			double mult = bnd;
			for (count = 0; count < sz; count++) {
				double x2 = ( xv[count] - xMin ) / ( xMax - xMin ) * mult - ( mult / 2.0 );
				double y2 = ( yv[count] ) / ( yMax ) * mult - ( mult / 2.0 );
				if( count == 0 )
				{
					pointsa.moveTo((float)x2, (float)y2);
				}
				else
				{
					pointsa.lineTo((float)x2, (float)y2);
				}
			}
		
		AbzonImmutableGeneralPathFactory fac = new AbzonImmutableGeneralPathFactory( pointsa );
		
		AazonImmutableEnt shape = (AazonImmutableEnt)( AbzonSmartShape.construct( fac , flatness, new AffineTransform(), AczonColor.getLineYellow(), false) );
		
		System.out.println("Finished Build...");
		
		rootFactory = new AczonRootFactory( shape );
		
		
		rootFactory.getCanvas().addMouseListener(this);

		return (rootFactory);
	}
	
	
	/**
	 * The minimum ratio to the wavelength at which to evaluate Fourier coefficients.
	 */
	public static final double MIN_RATIO = 0.02; // = 0.2;
	
	/**
	 * The maximum ratio to the wavelength at which to evaluate Fourier coefficients.
	 */
	public static final double MAX_RATIO = 20;

	/**
	 * The number of samples to use in evaluating a Fourier coefficient.  This is also the number of X-Y points displayed in the Fourier spectrum graph.
	 */
	public static final int SAMPLE_LEN = 400;

	/**
	 * The inverse of the fraction of a wavelength (multiplied by the ratio) over which to sample Fourier coefficients.
	 */
	public static final double DIVIDER = 100.0;

	/**
	 * The calculated X-Coordinates of the Fourier spectrum graph.
	 */
	double[] xv;

	/**
	 * The calculated Y-Coordinates of the Fourier spectrum graph.
	 */
	double[] yv;
	
	/**
	 * The calculated minimum X-coordinate of the Fourier spectrum graph.
	 */
	double xMin = 0.0;
	
	/**
	 * The calculated maximum X-coordinate of the Fourier spectrum graph.
	 */
	double xMax = 0.0;

	/**
	 * The calculated maximum Y-coordinate of the Fourier spectrum graph.
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
	 * The normalized Fourier frequency of the last mouse click.
	 */
	protected double freqU;

	/**
	 * The normalized Fourier coefficient of the last mouse click.
	 */
	protected double coeffU;
	
	/**
	 * Converts X-Y to normalized Fourier coefficient vs. frequency for the Fourier spectrum.
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
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

