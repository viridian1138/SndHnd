




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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Stores the number of beats for each measure (the time signature).
 * For instance, it's possible to change time signature from a 4/4 measure to a 6/8 measure
 * by having a "4" followed by a "6" in the beatsPerMeasure array.
 * Measure numbers start at zero.
 * The denominator of the time signature (which western musical symbol assigns to one beat) is not stored in the software.
 * 
 * @author tgreen
 *
 */
public class MeasuresStore implements Externalizable {
	
	/**
	 * Constructor.
	 */
	public MeasuresStore()
	{
		int count;
		int numCores = CpuInfo.getNumCores();
		currentIndex = new int[ numCores ];
		for( count = 0 ; count < numCores ; count++ )
		{
			currentIndex[ count ] = 0;
		}
		final int[] bpm = { 4 , 4 , 4 , 3 , 3 , 3 , 5 , 5 , 5 };
		setBeatsPerMeasure( bpm );
	}
	
	/**
	 * The number of beats per each measure.  It is assumed that
	 * the last number defines the remaining time signatures out to infinity.
	 */
	private int[] beatsPerMeasure = { 4 };
	
	/**
	 * The cumulative number of total song beats at the start of each measure.
	 */
	private int[] cumulativeMeasureBeats = { 0 };
	
	/**
	 * The current index into the array of measures for each core thread.
	 */
	private int currentIndex[];
	
	
	/**
	 * Sets the number of beats per each measure.  It is assumed that
	 * the last number defines the remaining time signatures out to infinity.
	 * @param _beatsPerMeasure The number of beats per each measure.
	 */
	public void setBeatsPerMeasure( int[] _beatsPerMeasure )
	{
		int numCores = CpuInfo.getNumCores();
		int count;
		for( count = 0 ; count < numCores ; count++ )
		{
			if( currentIndex[ count ] >= _beatsPerMeasure.length )
			{
				currentIndex[ count ] = 0;
			}
		}
		beatsPerMeasure = _beatsPerMeasure;
		final int len = beatsPerMeasure.length;
		cumulativeMeasureBeats = new int[ len ];
		int cumulative = 0;
		for( count = 0 ; count < len ; count++ )
		{
			cumulativeMeasureBeats[ count ] = cumulative;
			cumulative += beatsPerMeasure[ count ];
		}
	}
	
	
	/**
	 * Gets the measure number for a particular beat number.
	 * @param beatNumber The input beat number.
	 * @param core The number of the core thread.
	 * @return The measure number.
	 */
	public int getMeasureNumberForBeatNumber( final double beatNumber , final int core )
	{
		final int curIntCor = currentIndex[ core ];
		if( ( beatNumber >= cumulativeMeasureBeats[ curIntCor ] ) && 
				( beatNumber < ( cumulativeMeasureBeats[ curIntCor ] + beatsPerMeasure[ curIntCor ] ) ) )
		{
			return( curIntCor );
		}
		
		final int len = beatsPerMeasure.length;
		if( beatNumber >= ( cumulativeMeasureBeats[ len - 1 ] + beatsPerMeasure[ len - 1 ] ) )
		{
			double delta = beatNumber - cumulativeMeasureBeats[ len - 1 ];
			return( (int)( delta / beatsPerMeasure[ len - 1 ] ) + ( len - 1 ) );
		}
		
		if( beatNumber < 0.0 )
		{
			return( ( - (int) ( - beatNumber / beatsPerMeasure[ 0 ] ) ) - 1 );
		}
		
		int measureNumber = curIntCor;
		while( beatNumber < cumulativeMeasureBeats[ measureNumber ] )
		{
			measureNumber--;
		}
		while( beatNumber >= ( cumulativeMeasureBeats[ measureNumber ] + beatsPerMeasure[ measureNumber ] ) )
		{
			measureNumber++;
		}
		currentIndex[ core ] = measureNumber;
		return( measureNumber );
	}
	
	
	/**
	 * Gets the number of beats for a particular measure.
	 * @param measureNumber The input number of the measure, starting at zero.
	 * @return The number of beats in the measure.
	 */
	public int getNumberOfBeatsForMeasure( int measureNumber )
	{
		final int len = beatsPerMeasure.length;
		if( measureNumber >= len )
		{
			return( beatsPerMeasure[ len - 1 ] );
		}
		else
		{
			if( measureNumber >= 0 )
			{
				return( beatsPerMeasure[ measureNumber ] );
			}
			else
			{
				return( beatsPerMeasure[ 0 ] );
			}
		}
	}
	
	
	/**
	 * Gets the beat number at which a measure starts.
	 * @param measureNumber The input measure number, starting at zero.
	 * @return The beat number at which the measure starts.
	 */
	public int getBeatNumberForMeasureNumber( int measureNumber )
	{
		final int len = beatsPerMeasure.length;
		if( measureNumber >= len )
		{
			final int cum = cumulativeMeasureBeats[ len - 1 ];
			final int delta = measureNumber - ( len - 1 );
			return( cum + delta * beatsPerMeasure[ len - 1 ] );
		}
		else
		{
			if( measureNumber >= 0 )
			{
				return( cumulativeMeasureBeats[ measureNumber ] );
			}
			else
			{
				return( measureNumber * beatsPerMeasure[ 0 ] );
			}
		}
	}
	
	
	/**
	 * Given the beat number gets the number of beats in the measure.
	 * @param beatNumber The input beat number.
	 * @param core The number of the core thread.
	 * @return The number of beats in the measure.
	 */
	public double getBeatOnMeasureForBeatNumber( final double beatNumber , final int core )
	{
		final int measureNumber = getMeasureNumberForBeatNumber( beatNumber , core );
		final int strt = getBeatNumberForMeasureNumber( measureNumber );
		return( beatNumber - strt );
	}
	
	/**
	 * Given a beat number, gets gets the number of the beat within the beat number's measure.
	 * @param beatNumber The input beat number.
	 * @param core The number of the core thread.
	 * @return The number of the beat within the measure.
	 */
	public int getIntBeatOnMeasureForBeatNumber( final double beatNumber , final int core )
	{
		return( (int)( getBeatOnMeasureForBeatNumber( beatNumber , core ) ) );
	}
	
	
	/**
	 * Gets a GUI-displayable measure number for a particular beat number.
	 * @param beatNumber The input beat number.
	 * @param core The number of the core thread.
	 * @return The GUI-displayable measure number.
	 */
	public double getGuiMeasureCountForBeatNumber( final double beatNumber , final int core )
	{
		final int measureNumber = getMeasureNumberForBeatNumber( beatNumber , core );
		final double beatOnMeasure = getBeatOnMeasureForBeatNumber( beatNumber , core );
		final double beatsForMeasure = getNumberOfBeatsForMeasure( measureNumber );
		return( measureNumber + beatOnMeasure / beatsForMeasure );
	}
	
	
	/**
	 * Calculates the total number of beats in the song.
	 * @param songDataNumMeasures The input total number of measures in the song.
	 * @return The total number of beats in the song.
	 */
	public int getTotalNumberBeats( int songDataNumMeasures )
	{
		final int len = Math.max( beatsPerMeasure.length , songDataNumMeasures );
		return( getBeatNumberForMeasureNumber( len ) + getNumberOfBeatsForMeasure( len ) );
	}
	
	
	/**
	 * The number of beats per each measure.  It is assumed that
	 * the last number defines the remaining time signatures out to infinity.
	 * @return The number of beats per each measure.
	 */
	public int[] getBeatsPerMeasure()
	{
		return( beatsPerMeasure );
	}
	
	
	/**
	 * Returns a clone of the MeasuresStore.
	 * @return A clone of the MeasuresStore.
	 */
	public MeasuresStore cloneMeasuresStore()
	{
		MeasuresStore ret = new MeasuresStore();
		ret.setBeatsPerMeasure( beatsPerMeasure );
		return( ret );
	}
	
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			setBeatsPerMeasure( (int[])( myv.getPropertyEx("BeatsPerMeasure") ) );
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("BeatsPerMeasure", beatsPerMeasure);

		out.writeObject(myv);
	}

	
	
}

