




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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JPanel;

import aazon.AazonEnt;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartIntSwitch;
import aazon.dbl.AazonBaseImmutableDbl;
import aazon.intg.AazonBaseMutableInt;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonMutableSubtractVect;
import aazon.vect.AazonVect;
import abzon.AbzonImmutableGeneralPathFactory;
import abzon.AbzonImmutableShape;
import aczon.AczonColor;
import aczon.AczonRootFactory;
import bezier.BezierQuarticNonClampedCoefficientSlopingMultiCore;
import bezier.CubicBezierCurve;
import bezier.PiecewiseQuarticBezierSlopingMultiCore;


/**
 * Visual display that serves the same basic purpose as watching a matronome-- to observe and remain synchronized with the beat.
 * 
 * Essentially the current measure is divided into a semicircular curve for each beat in the measure, with the beat number superimposed onto the correct curve.
 * 
 * This class also took some inspiration from the manner in which choral conductors move their hands to show the beat.
 * 
 * @author thorngreen
 * 
 */
public class VisualMetronome {
	
	/**
	 * The maximum number of beats in a measure, also defines the maximum number of semicircular curves.
	 */
	private final static int MAX_CRV = 25;

	/**
	 * The sound player from which to get the current time.
	 */
	SoundPlayer soundPlayer;

	/**
	 * Thread synchronization object.
	 */
	final Integer syncObj = new Integer( -1 );

	/**
	 * Volatile boolean used to terminate the execution of the update thread.  The update thread polls this boolean routinely.
	 */
	volatile boolean runThread = true;
	
	/**
	 * Thebeat number as a function of elapsed seconds.  This is updated at the point the SoundPlayer is set.
	 */
	volatile BezierQuarticNonClampedCoefficientSlopingMultiCore beatNumberFunctionSeconds = null;
	
	/**
	 * The current beat number.
	 */
	double currentBeatNumber = -1E+5;
	
	/**
	 * The current number of beats per measure.
	 */
	AazonBaseMutableInt currentBeatsPerMeasure = new AazonBaseMutableInt( 5 );
	
	/**
	 * The current store of measures.  This is updated at the point the SoundPlayer is set.
	 */
	MeasuresStore measuresStore = new MeasuresStore();
	
	/**
	 * Array of functional curves for evaluating the X-Position within a particular fraction-of-beat.
	 */
	Object[] perBpmBezCurvesX = new Object[ MAX_CRV ];
	
	/**
	 * Array of functional curves for evaluating the Y-Position within a particular fraction-of-beat.
	 */
	Object[] perBpmBezCurvesY = new Object[ MAX_CRV ];
	
	/**
	 * Previously plotted X-position.  Used to determine whether the previous position has moved.
	 */
	double prevX = 1E+6;
	
	/**
	 * Previously plotted Y-position.  Used to determine whether the previous position has moved.
	 */
	double prevY = 1E+6;
	
	/**
	 * The current position of the point indicating the beat number that is superimposed on the curve.
	 */
	private final AazonBaseMutableVect movingVect = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	/**
	 * The root factory.
	 */
	private AczonRootFactory af = null;
	

	/**
	 * Constructor.
	 */
	public VisualMetronome() {
		super();
	}
	
	/**
	 * Constructs the panel containing the visual metronome.
	 * @return The panel containing the visual visual metronome.
	 */
	public JPanel constructPanel() {
		try {
			
			int count;
			AazonEnt[] curves = new AazonEnt[ MAX_CRV ];
			for( count = 1 ; count < MAX_CRV ; count++ )
			{
				curves[ count ] = createBeatsPerMeasureShape( count );
			}
			
			final AazonBaseImmutableVect offset = new AazonBaseImmutableVect( 0.01 , 0.01 );
			final AazonVect a = AazonMutableSubtractVect.construct( movingVect , offset );
			final AazonVect b = AazonMutableAdditiveVect.construct( movingVect , offset );
			final AazonEnt movingRect = AazonSmartFilledRectangle.construct(a, b, AczonColor.getFillCyan(), false);
			
			final AazonEnt bkgndPolygons = AazonSmartIntSwitch.construct( curves , currentBeatsPerMeasure );
			
			final AazonEnt[] ents = { bkgndPolygons , movingRect };
			
			final AazonImmutableOrderedGroup gp = new AazonImmutableOrderedGroup( ents );
			
			af = new AczonRootFactory( gp );
			
			final JPanel pn = new JPanel();
			
			pn.setLayout( new BorderLayout( 0 , 0 ) );
			
			pn.add( BorderLayout.CENTER , af.getCanvas() );
			
			pn.setMinimumSize( new Dimension( 500 , 500 ) );
			
			pn.setPreferredSize( new Dimension( 500 , 500 ) );
			
			return( pn );
			
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
			throw( new RuntimeException( "Failed." ) );
		}
	}
	
	/**
	 * Handles the showing of the metronome.
	 * @param _soundPlayer The sound player from which to get the current time.
	 */
	public void handleShow(SoundPlayer _soundPlayer)
	{
		try
		{
		startThread();
		setSoundPlayer(_soundPlayer);
	} catch (Throwable ex) {
		ex.printStackTrace(System.out);
		throw( new RuntimeException( "Failed." ) );
	}
	}

	/**
	 * Sets the sound player from which to get the current time.
	 * @param _soundPlayer The sound player from which to get the current time.
	 */
	public void setSoundPlayer(SoundPlayer _soundPlayer) {
		synchronized( syncObj ) {
		soundPlayer = _soundPlayer;
		PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
    	quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
    	beatNumberFunctionSeconds = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
    	measuresStore = SongData.measuresStore.cloneMeasuresStore();
	}
	}
	
	/**
	 * Creates the set of semicircular curves for the number of beats per measure.
	 * @param beatsPerMeasure The number of beats per measure.
	 * @return The set of semicircular curves for the number of beats per measure.
	 */
	private AazonEnt createBeatsPerMeasureShape( int beatsPerMeasure )
	{
		
		final CubicBezierCurve[] curvesX = new CubicBezierCurve[ beatsPerMeasure ];
		final CubicBezierCurve[] curvesY = new CubicBezierCurve[ beatsPerMeasure ];
		final GeneralPath gp = new GeneralPath();
		final double mult = 2.0 * Math.PI / beatsPerMeasure;
		double x2 = 0.95 * Math.sin( 0.0 );
		double y2 = 0.95 * Math.cos( 0.0 );
		gp.moveTo( (float)( x2 ) , (float)( y2 ) );
		int count;
		for( count = 1 ; count < ( beatsPerMeasure + 1 ) ; count++ )
		{
			final double x1 = x2;
			final double y1 = y2;
			x2 = 0.95 * Math.sin( mult * count );
			y2 = 0.95 * Math.cos( mult * count );
			final double b1x = 0.5 * x1;
			final double b1y = 0.5 * y1;
			final double b2x = 0.5 * x2;
			final double b2y = 0.5 * y2;
			gp.curveTo( (float) b1x, (float) b1y, (float) b2x, (float) b2y, (float) x2, (float) y2 );
			CubicBezierCurve crvX = new CubicBezierCurve();
			CubicBezierCurve crvY = new CubicBezierCurve();
			final double[] ptsX = { x1 , b1x , b2x , x2 };
			final double[] ptsY = { y1 , b1y , b2y , y2 };
			crvX.setBezPts( ptsX );
			crvY.setBezPts( ptsY );
			curvesX[ count - 1 ] = crvX;
			curvesY[ count - 1 ] = crvY;
		}
		
		perBpmBezCurvesX[ beatsPerMeasure ] = curvesX;
		perBpmBezCurvesY[ beatsPerMeasure ] = curvesY;
		AbzonImmutableGeneralPathFactory ig = new AbzonImmutableGeneralPathFactory( gp );
		AbzonImmutableShape bkgndPolygon = new AbzonImmutableShape( 
				ig , new AazonBaseImmutableDbl( 0.001 ) , new AffineTransform() , AczonColor.getLineDarkYellow() );
		return( bkgndPolygon );
	}
	

	/**
	 * Starts a thread that continuously updates the position of the beat number that is superimposed on the other curves, and the number of the current beats per measure.
	 */
	private void startThread() {
		final Runnable runn = new Runnable() {
			/**
			 * Continuously updates the position of the beat number that is superimposed on the other curves, and the number of the current beats per measure.
			 */
			public void run() {
				try {
					final int core = 0;
					while (runThread) {
						synchronized( syncObj )
						{
								final double TIME_SCALE = 1000000;
								final long endTime = soundPlayer.getMicrosecondPosition();
								final double endTimeSeconds = ((double) endTime) / TIME_SCALE;
								currentBeatNumber = beatNumberFunctionSeconds.eval( endTimeSeconds , core);
								final double beatNum = currentBeatNumber;
								final double beatFrac = beatNum - (int) beatNum;
								final int measureNumber = measuresStore.getMeasureNumberForBeatNumber(beatNum, core);
								final int measureBeat = measuresStore.getIntBeatOnMeasureForBeatNumber(beatNum, core);
								final int beatsPerMeasure = measuresStore.getNumberOfBeatsForMeasure(measureNumber);
								if( beatsPerMeasure != currentBeatsPerMeasure.getX() )
								{
									currentBeatsPerMeasure.setCoords( beatsPerMeasure );
								}
								CubicBezierCurve[] bezCurvesX = (CubicBezierCurve[])( perBpmBezCurvesX[ beatsPerMeasure ] );
								CubicBezierCurve[] bezCurvesY = (CubicBezierCurve[])( perBpmBezCurvesY[ beatsPerMeasure ] );
								final CubicBezierCurve crvX = bezCurvesX[ measureBeat ];
								final CubicBezierCurve crvY = bezCurvesY[ measureBeat ];
								crvX.setStartParam( 0.0 );
								crvX.setEndParam( 1.0 );
								crvY.setStartParam( 0.0 );
								crvY.setEndParam( 1.0 );
								final double x = crvX.eval( beatFrac );
								final double y = crvY.eval( beatFrac );
								if( ( ( Math.abs( x - prevX ) ) > 1E-6 ) || ( ( Math.abs( y - prevY ) ) > 1E-6 ) )
								{
									movingVect.setCoords(x, y);
									prevX = x;
									prevY = y;
								}
						}
						Thread.sleep( 20 );
					}
				} catch (Throwable ex) {
					ex.printStackTrace(System.out);
				}
			}
		};

		final Thread th = new Thread(runn);
		th.start();
	}

}

