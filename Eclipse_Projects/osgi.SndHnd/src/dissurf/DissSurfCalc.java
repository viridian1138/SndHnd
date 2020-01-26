




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
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

import core.DissonanceCalculator;
import core.DissonanceCalculatorMultiCore;
import core.InterpolationPoint;
import core.NoteDesc;
import core.SongData;



/**
 * Creates a surface for selecting the pitches of two notes out of a triad by showing
 * the dissonance changes as the pitches of the two notes are altered.  It is assumed that the
 * tonic of the triad (or equivalent) is already present and can remain fixed.
 * 
 * @author tgreen
 *
 */
public class DissSurfCalc {
	
	
	
	/**
	 * Calculates a surface for selecting the pitches of two notes out of a triad by showing
     * the dissonance changes as the pitches of the two notes are altered.  It is assumed that the
     * tonic of the triad (or equivalent) is already present and can remain fixed.
     * Hence, the NoteDesc for the tonic is not passed as a parameter here.
	 * @param beatNumber The beat number at which to calculate the dissonance.
	 * @param n1 The first note of the triad for which to change pitches.
	 * @param n2 The second note of the triad for which to change pitches.
	 * @param max The maximum number of frequencies to loop through on each domain axis.
	 * @param lowFreq The minimum frequency of the surface domain.
	 * @param highFreq The maximum frequency of the surface domain.
	 * @param out The output dissonance surface points.
	 * @throws Throwable
	 */
	public static void calcSurf( final double beatNumber , NoteDesc n1 , NoteDesc n2 , 
			final int max , final double lowFreq , final double highFreq , HashMap<Coord,Double> out ) throws Throwable
	{

		final int core = 0;
		
		final double n1f = n1.getFreqAndBend().getBaseFreq();
		
		final double n2f = n2.getFreqAndBend().getBaseFreq();
		
		final ArrayList<NoteDesc> notes1 = SongData.getNotesAtTime(beatNumber, n1, false, core);
		
		final ArrayList<NoteDesc> notes2 = SongData.getNotesAtTime(beatNumber, n2, false, core);

		System.out.println("Starting Dissonance Calculations...");

		
		

		
		int k;
		
		for( k = 0 ; k < max ; k++ )
		{
			System.out.println( "" + k + " " + max );
			
			final double u = ( (double) k ) / max;
			
			final double freq = (1-u) * lowFreq + u * highFreq;
			
			// Note: there's no idea of a real multi-element cross-dissonance in textbooks such as
			// Bill Sethares' "Tuning. Timbre, Specturum and Scale" hence the value is approximated here
			// by adding the dissonance of Note #1 vs. everything else to the dissonance of Note #2 vs.
			// everything else.
			
			{
				final ArrayList<InterpolationPoint> crv = new ArrayList<InterpolationPoint>();
				
				n1.getFreqAndBend().setBaseFreq( n1f );
				
				n2.getFreqAndBend().setBaseFreq( freq );
				
				final DissonanceCalculator calc1 = new DissonanceCalculatorMultiCore( notes1 ,
						n1 , max , beatNumber);
				
				calc1.calcCurve(lowFreq, highFreq, max, crv);
				
				int m;
				
				for( m = 0 ; m < max ; m++ )
				{
					InterpolationPoint mm = crv.get( m );
					Coord c = new Coord( k , m );
					Double d = out.get( c );
					if( d == null ) d = 0.0;
					d = d + mm.getValue();
					out.put( c , d );
				}
				
			}
			
			{
				final ArrayList<InterpolationPoint> crv = new ArrayList<InterpolationPoint>();
				
				n1.getFreqAndBend().setBaseFreq( freq );
				
				n2.getFreqAndBend().setBaseFreq( n2f );
				
				final DissonanceCalculator calc2 = new DissonanceCalculatorMultiCore( notes2 ,
						n2 , max , beatNumber);
				
				calc2.calcCurve(lowFreq, highFreq, max, crv);
				
				int m;
				
				for( m = 0 ; m < max ; m++ )
				{
					InterpolationPoint mm = crv.get( m );
					Coord c = new Coord( m , k );
					Double d = out.get( c );
					if( d == null ) d = 0.0;
					d = d + mm.getValue();
					out.put( c , d );
				}
				
			}
		}
		

		System.out.println("Ending Dissonance Calculations...");
		
		n1.getFreqAndBend().setBaseFreq( n1f );
		
		n2.getFreqAndBend().setBaseFreq( n2f );
	
		
	}

	
	
	
	/**
	 * Plots a surface for selecting the pitches of two notes out of a triad by showing
     * the dissonance changes as the pitches of the two notes are altered.  It is assumed that the
     * tonic of the triad (or equivalent) is already present and can remain fixed.
	 * @param beatNumber The beat number at which to calculate the dissonance.
	 * @param n1 The first note of the triad for which to change pitches.
	 * @param n2 The second note of the triad for which to change pitches.
	 * @param lowFreq The minimum frequency of the surface domain.
	 * @param highFreq The maximum frequency of the surface domain.
	 * @param sel Handles a user selection by clicking on the surface.
	 * @throws Throwable
	 */
	public static void plotSurf( final double beatNumber , NoteDesc n1 , NoteDesc n2 , final double lowFreq , final double highFreq , SelectionHandler sel ) throws Throwable
	{
		final HashMap<Coord,Double> data = new HashMap<Coord,Double>();
		
		final int max = 600;
		
		calcSurf( beatNumber , n1 , n2 , max , lowFreq , highFreq , data );
		
		System.out.println( "Working..." );
		
		final PointRemap pr = new PointRemap()
		{
			public double remap( double in )
			{
				final double u = in / max;
				final double freq = (1-u) * lowFreq + u * highFreq;
				final double ret = Math.log( freq ) / Math.log( 2 );
				return( ret );
				
			}
		};

		DissSurfRenderingPane2 pane = new DissSurfRenderingPane2( data , max , pr , sel );
		
		JFrame fr = new JFrame();
		
		fr.getContentPane().setLayout( new BorderLayout( 0 , 0 ) );
		
		fr.getContentPane().add( BorderLayout.CENTER , pane );
		
		fr.pack();
		
		fr.show();
		
		System.out.println( "Phase Done." );
		
	}
	
	
	
	
}


