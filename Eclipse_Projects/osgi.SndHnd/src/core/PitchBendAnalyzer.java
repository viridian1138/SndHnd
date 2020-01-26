




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


import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Given an input note (almost always a SampledAgent note from a digitized source),
 * estimates the shape of the note's pitch bend and dumps the results to a file.
 * 
 * @author tgreen
 *
 */
public class PitchBendAnalyzer {

	/**
	 * The input note.
	 */
	protected NoteDesc inputNote;
	
	/**
	 * The number of points to be generated on the estimated pitch bend.
	 */
	public static final int NUM_STEPS = 502;
	
	/**
	 * The current beat number of the position at which to sample the pitch bend.
	 */
	protected double beatNumber = -1.0;

	/**
	 * Map used for duplicate elimination, particularly where auto correlation is required.
	 */
	protected final HashMap<NoteDesc,Double> noteFreqMap = new HashMap<NoteDesc,Double>();

	/**
	 * Constructor.
	 * @param _note The input note.
	 */
	public PitchBendAnalyzer(NoteDesc _note) {
		inputNote = _note;
	}

	/**
	 * Gets the frequency (pitch) of a particular note in hertz.
	 * @param note The note for which to get the frequency.
	 * @return The frequency (pitch) of the note in hertz.
	 */
	protected double getNoteFrequency(NoteDesc note) {
		final int core = 0;
		if (!(note.getWaveform( core ).useAutocorrelation())) {
			return (note.getFreqAndBend().getBaseFreq());
		} else {
			Double dbl = (Double) (noteFreqMap.get(note));
			if (dbl != null) {
				return (dbl.doubleValue());
			} else {
				// double val = NotePitchDigitizer.calcAutoCorrelation(note,
				//		beatNumber);
				//double val = NoteTable.getNoteFrequencyEqualTemperedScale_Intl(/*2*/4,
				//		NoteTable.STEPS_E);
				//double val = NoteTable.getNoteFrequencyEqualTemperedScale_Intl(3,
				//		NoteTable.STEPS_D);
				double val = NoteTable.getNoteFrequencyEqualTemperedScale_Intl(4,
						NoteTable.STEPS_E);
				dbl = new Double(val);
				noteFreqMap.put(note, dbl);
				return (val);
			}
		}
	}
	
	
	/**
	 * Returns the distance between the pitch bend and the default pitch bend of unity.
	 * @param fa The input pitch bend.
	 * @return The distance between the pitch bend and the default pitch bend of unity.
	 */
	protected static double fdist( double fa )
	{
		return( Math.abs( 1.0 - fa ) );
	}
	
	
	/**
	 * Estimates the envelope, and dumps the results to a file.
	 * @throws Throwable
	 */
	public void calculate()  throws Throwable {
		
		final int core = 0;
		
		PrintStream ps = new PrintStream( new FileOutputStream( "/users/thorngreen/OutputResults.txt" ) );
		
		double startBeatNumber = inputNote.getActualStartBeatNumber();
		double endBeatNumber = inputNote.getActualEndBeatNumber();
		
		double freq = getNoteFrequency( inputNote );
		
		int count;
		double sum = 0.0;
		ArrayList<InterpolationPoint> arr = new ArrayList<InterpolationPoint>();
		System.out.println( "Outputs : " );
		for( count = 0 ; count < NUM_STEPS ; count++ )
		{
			double u = ( (double) count ) / ( NUM_STEPS - 1 );
			beatNumber = (1-u) * startBeatNumber + u * endBeatNumber;
			double val = NotePitchDigitizer.calcAutoCorrelation(inputNote, beatNumber, core);
			System.out.println( "Match Freq " + freq );
			double bend = val / freq;
			while( fdist( bend / 2.0 ) < fdist( bend ) )
			{
				bend = bend / 2.0;
			}
			while( fdist( bend * 2.0 ) < fdist( bend ) )
			{
				bend = bend * 2.0;
			}
			System.out.println( "Final Bend : " + bend );
			if( ( bend > 0.8 ) && ( bend < 1.25 ) )
			{
				InterpolationPoint ppt = new InterpolationPoint(u, bend);
				arr.add( ppt );
				sum += bend;
			}
		}
		
		int sz = arr.size();
		double avg = sum / sz;
		for( count = 0 ; count < sz ; count++ )
		{
			InterpolationPoint pt = arr.get( count );
			String out = "interps.add( new InterpolationPoint( " + 
				pt.getParam() + " , " + pt.getValue() / avg + " ) );";
			System.out.println( out );
			ps.println( out );
		}
		
		ps.close();

	}

	
}

