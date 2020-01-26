




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


import java.util.*;


/**
 * Class storing the current key and scale used by the software.
 * 
 * Note: the current intonation also defines a key, but that key is separate from this key.
 * 
 * @author tgreen
 *
 */
public class KeyAndScaleData {

	/**
	 * The frequency (pitch) of the current key in hertz.  The initial value is based on the assumption that the software will initially start in a default western intonation.  However, it can be transitioned to any other intonation using the determineKey() and confirmKey() methods.
	 */
	protected static double freq = 440;
	
	/**
	 * The melodic interval number of the current key.  The initial value is based on the assumption that the software will initially start in a default western intonation.  However, it can be transitioned to any other intonation using the determineKey() and confirmKey() methods.
	 */
	protected static int melodicIntervalNumber = 4;
	
	/**
	 * The number of steps on the scale for the current key.  The initial value is based on the assumption that the software will initially start in a default western intonation.  However, it can be transitioned to any other intonation using the determineKey() and confirmKey() methods.
	 */
	protected static int noteSteps = NoteTable.STEPS_A;
	
	
	/**
	 * Gets the frequency (pitch) of the current key in hertz.
	 * @return The frequency (pitch) of the current key in hertz.
	 */
	public static double getFreq()
	{
		return( freq );
	}
	
	
	/**
	 * Gets the melodic interval number of the current key.
	 * @return The melodic interval number of the current key.
	 */
	public static int getMelodicIntervalNumber()
	{
		return( melodicIntervalNumber );
	}
	
	
	/**
	 * Gets the number of steps on the scale for the current key.
	 * @return The number of steps on the scale for the current key.
	 */
	public static int getNoteSteps()
	{
		return( noteSteps );
	}
	
	
	/**
	 * Gets the frequency (pitch) of a particular note in hertz.
	 * @param note The note for which to get the frequency.
	 * @param noteFreqMap Map used for duplicate elimination, particularly where auto correlation is required.
	 * @param beatNumber The beat number at which to get the frequency.
	 * @param core The number of the core thread.
	 * @return The frequency (pitch) of the note in hertz.
	 */
	protected static double getNoteFrequency(NoteDesc note, HashMap<NoteDesc,Double> noteFreqMap, double beatNumber, final int core) {
		if (!(note.getWaveform( core ).useAutocorrelation())) {
			return (note.getFreqAndBend().getBaseFreq());
		} else {
			Double dbl = noteFreqMap.get(note);
			if (dbl != null) {
				return (dbl.doubleValue());
			} else {
				NotePitchDigitizer.setVoiceMarkMode();
				double val = NotePitchDigitizer.calcAutoCorrelation(note,
					beatNumber, core);
				dbl = new Double(val);
				noteFreqMap.put(note, dbl);
				return (val);
			}
		}
	}
	
	
	/**
	 * The preliminary frequency (pitch) in hertz extracted from the input note by determineKey().
	 */
	protected static double tempFrequency = -1.0;
	
	/**
	 * The preliminary melodic interval number for the current intonation found by determineKey().
	 */
	protected static int tempMelodicIntervalNumber = -1;
	
	/**
	 * The preliminary number of the step on the scale for the current intonation found by determineKey().
	 */
	protected static int tempScl = -1;
	
	
	/**
	 * Handles a confirmation of the results of a previous determineKey() call 
	 * by committing the preliminary results of determineKey() as the current
	 * key used by the software.
	 */
	public static void confirmKey()
	{
		freq = tempFrequency;
		melodicIntervalNumber = tempMelodicIntervalNumber;
		noteSteps = tempScl;
	}
	
	
	/**
	 * Determines a preliminary "key" by taking the input note (presumably a note that defines the desired key), and finding the pitch in the current intonation that is the closest to the pitch of the note.
	 * @param inputNote The input note, presumably a note that defines the desired key.
	 * @param core The number of the core thread.
	 */
	public static void determineKey( NoteDesc inputNote , final int core )
	{
		HashMap<NoteDesc,Double> noteFreqMap = new HashMap<NoteDesc,Double>();
		double beatNumber = /* 0.5 + */ inputNote.getActualStartBeatNumber(); // !!!!!!!!!!!!!!!!!!!!! Note: this may not be the best starting point of there is an ongoing portamento transition.  Then again, if you have a note with a portamento transition then how exactly do you know which part of that portamento defines the key?
		double frequency = getNoteFrequency( inputNote , noteFreqMap , beatNumber , core );
		double bestVal = 1E+16;
		int melodicIntervalNumber = -1;
		int scaleNumber = -1;
		int oct = -1;
		int scl = -1;
		double freq = -1.0;
		final int szz = NoteTable.getScaleSize();
		for (melodicIntervalNumber = 0; melodicIntervalNumber < 10; melodicIntervalNumber++) {
			for (scaleNumber = 0; scaleNumber < szz; scaleNumber++) {
				freq = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalNumber,
						scaleNumber);
				if (freq < 2000) {
					if(  Math.abs( frequency - freq ) < bestVal ) {
						System.out.println(">>> " + freq);
						oct = melodicIntervalNumber;
						scl = scaleNumber;
						bestVal = Math.abs( frequency - freq );
					}
				}
			}
		}
		
		tempFrequency = frequency;
		tempMelodicIntervalNumber = oct;
		tempScl = scl;
		System.out.println( "Results : " );
		System.out.println( tempFrequency );
		System.out.println( tempMelodicIntervalNumber );
		System.out.println( tempScl );
		System.out.println( Math.abs( frequency - freq ) );
	}
	

}


