




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

import gredit.GNonClampedCoefficient;
import greditcoeff.EditPackCoeff;
import greditinton.EditPackIntonation;
import greditharmon.EditPackHarmony;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import javax.swing.JComponent;

import jundo.runtime.ExtMilieuRef;
import meta.DataFormatException;
import meta.VersionBuffer;
import palettes.PaletteClassesDump;
import palettes.PaletteClassesWave;
import verdantium.EtherEvent;
import verdantium.ProgramDirector;
import verdantium.StandardEtherEvent;
import verdantium.TransVersionBuffer;
import verdantium.TransVersionBufferFlavor;
import verdantium.UrlHolder;
import verdantium.VerdantiumPropertiesEditor;
import verdantium.core.DefaultPropertyEditor;
import verdantium.core.DesignerControl;
import verdantium.core.PropertyEditEtherEvent;
import verdantium.undo.UndoManager;
import verdantium.utils.VerdantiumDragUtils;
import verdantium.utils.VerdantiumDropUtils;
import verdantium.xapp.DocPageFormat;
import verdantium.xapp.JcApplicationAdapter;
import verdantium.xapp.MacroTreeMap;
import verdantium.xapp.OnlyDesignerEdits;
import bezier.BezierCubicNonClampedCoefficientFlatMultiCore;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;



/**
 * A test sound player.
 * 
 * This class was originally written for QTJ (QuickTime for Java) using
 * examples from the book "QuickTime for Java : A Developer's Notebook" by Chris Adamson, 
 * but has since been ported from QTJ to JavaSound.  Porting used geberal JavaSound examples from the web.
 * 
 * @author tgreen
 *
 */
public class TestPlayer2 extends JcApplicationAdapter {

	/**
	 * The number of bytes in a sample of generated audio.
	 */
	protected static int BYTES_PER_SAMPLE = 4;

	/**
	 * The number of bits in a sample of generated audio.
	 */
	protected static int BITS_PER_SAMPLE = 8 * BYTES_PER_SAMPLE;

	// Sampling Rate Constants
	
	/**
	 * 12 khz sampling date (for lo-fi) in hertz.
	 */
	public static final int SMPL_12 = 12000;
	
	/**
	 * 24 khz sampling date (for lo-fi) in hertz.
	 */
	public static final int SMPL_24 = 24000;

	/**
	 * The sampling rate (in hertz) of CD (Compast Disc) audio.
	 */
	public static final int SMPL_CD_ROM = 44100;

	/**
	 * The sampling rate (in hertz) of the DVD (Digital Versatile Disc) 48 khz PCM (Pulse-Code Modulation) format.
	 */
	public static final int SMPL_DVD_48 = 48000;

	/**
	 * The sampling rate (in hertz) of the DVD (Digital Versatile Disc) 96 khz PCM (Pulse-Code Modulation) format.
	 */
	public static final int SMPL_DVD_96 = 96000;

	/**
	 * The maximum sampling rate (in hertz) of the SlimDevices Squeezebox 2.
	 */
	public static final int SMPL_SLIM_DEVICES_SQUEEZEBOX_2 = 48000;

	/**
	 * The maximum sampling rate (in hertz) of the SlimDevices Transporter.
	 */
	public static final int SMPL_SLIM_DEVICES_TRANSPORTER = 96000;

	/**
	 * The maximum sampling rate for which audio content can be generated.
	 * Note: JavaSound usually cannot play audio at this rate.
	 * However, this is an primarily an audio generation system, not a playback system.
	 */
	public static final int SMPL_MAX_SAFE = 384000;

	/**
	 * The maximum sample rate (in hertz) of the 32-bit WAV format (384 khz).
	 * Note: JavaSound usually cannot play WAV files at this rate.
	 * However, this is an primarily an audio generation system, not a playback system.
	 */
	public static final int SMPL_MAX_WAV_FORMAT = 384000;

	/**
	 * The default sampling rate for rough drafts.
	 */
	public static final int SMPL_DRAFT = SMPL_DVD_48;

	public static final int SMPL_REALLY_BAD = 2200;
	
	/**
	 * The initial default sampling rate in hertz.
	 */
	public static final int SMPL_START = SMPL_24;
	

	// Quantization Constants

	/**
	 * 32-bit quantization.
	 */
	public static final long QUAN_32_BIT = 1;

	/**
	 * 24-bit quantization.
	 */
	public static final long QUAN_24_BIT = 256;

	/**
	 * 20-bit quantization.
	 */
	public static final long QUAN_20_BIT = 4096;

	/**
	 * 16-bit quantization.
	 */
	public static final long QUAN_16_BIT = 65536;

	/**
	 * 14-bit quantization.
	 */
	public static final long QUAN_14_BIT = 4 * QUAN_16_BIT;

	/**
	 * 12-but quantization.
	 */
	public static final long QUAN_12_BIT = 4 * QUAN_14_BIT;

	/**
	 * 10-bit quantization (lo-fi).
	 */
	public static final long QUAN_10_BIT = 4 * QUAN_12_BIT;

	/**
	 * 8-bit quantization (lo-fi).
	 */
	public static final long QUAN_8_BIT = 4 * QUAN_10_BIT;

	/**
	 * 6-bit quantization (lo-fi).
	 */
	public static final long QUAN_6_BIT = 4 * QUAN_8_BIT;

	/**
	 * 4-bit quantization (lo-fi).
	 */
	public static final long QUAN_4_BIT = 4 * QUAN_6_BIT;

	
	// The word "supposed" is used because the actual digital-to-audio converter
	// in a player may not accurately reproduce all the bits being played (e.g.
	// the
	// actual digital-to-audio converter in a CD player may only convert the top
	// twelve bits of the 16 bits encoded on the CD).

	/**
	 * The supposed per-spec quantization of CD (Compact Disc) digital audio.
	 * That is to say, the quantization is supposed to be 16 bits, but there are
	 * a fair number of devices that e.g. only use the most significant 12 bits of the 16.
	 */
	public static final long QUAN_SUPPOSED_CD_ROM_QUANTIZATION = QUAN_16_BIT;

	/**
	 * The supposed per-spec quantization of DVD (Digital Versatile Disc) digital audio.
	 * That is to say, the quantization is supposed to be 24 bits, but there are
	 * a fair number of devices that e.g. only use the most significant 20 bits of the 24.
	 */
	public static final long QUAN_SUPPOSED_DVD_QUANTIZATION = QUAN_24_BIT;

	/**
	 * The supposed per-spec quantization of SlimDevices hardware.
	 */
	public static final long QUAN_SUPPOSED_SLIM_DEVICES_QUANTIZATION = QUAN_24_BIT;
	

	/**
	 * The sampling rate (in hertz) of the generated audio.
	 */
	protected static int sampling = SMPL_START;
	
	/**
	 * Divisor for putting higher-sample-rate audio in a "tower-sample-rate" output file for burning to a disc.
	 * For instance, there are some audiophile CD (Compact Disc) players that will play the
	 * disc at 88.2 khz even though the CD standard specified the format as 44.1 khz.  It was easier
	 * for vendors to put a different audio quality onto an existing disc format than
	 * it was for them to attempt to market a completely new disc format with completely
	 * new manufacturing techniques.
	 */
	protected static int pseudonymicDivisor = 1;

	
	// Should use PCM96 for DVD, but SMPL_DVD_96 not working with 32-bit depth, instead use pseudonymic divisor of 2 as described in wiki.

	/**
	 * The quantization (number of bits) of the generated audio.
	 */
	protected static long quantization = QUAN_32_BIT /* QUAN_6_BIT */;

	/**
	 * Bytes array for a one-second sample of PCM (Pulse-Code Modulation) data.
	 */
	protected static byte[] one_second_sample = new byte[ SMPL_START
			* BYTES_PER_SAMPLE];
	
	protected static EditPackCoeff editPackCoeff = new EditPackCoeffTp();
	
	public static EditPackIntonation editPackIntonation = new EditPackIntonationTp();
	
	public static EditPackHarmony editPackHarmony = new EditPackHarmonyTp();
	
	private static NonClampedCoefficient randWave = null;

	/**
	 * Coefficient for evaluating the audio.
	 */
	public static NonClampedCoefficientMultiCore evalCoefficient = null;
	
	/**
	 * Coefficient for taking evalCoefficient and evaluating it to left-side stereo.
	 */
	public static StereoEvalCoefficient stereoLeftCoefficient = null;
	
	/**
	 * Coefficient for taking evalCoefficient and evaluating it to right-side stereo.
	 */
	public static StereoEvalCoefficient stereoRightCoefficient = null;

	/**
	 * In modes there normalization through a global maximum value is used,
	 * stores the calculated global maximum value.
	 */
	static double globalMaxVal;
	
	
	public static NonClampedCoefficient getRandWave()
	{
		while( randWave == null )
		{
			try
			{
				Class clss = (Class)( PaletteClassesWave.getMapClone().get( "Meme -- Noise -- System Default Noise" ) );
			
				if( clss == null )
				{
					synchronized( Thread.currentThread() )
					{
						Thread.currentThread().wait( 250 );
					}
					System.out.println( "Waiting For Agent..." );
				}
				else
				{
					GNonClampedCoefficient nonClamped = (GNonClampedCoefficient)( clss.newInstance() );
					randWave = nonClamped.genCoeff( new HashMap() );
				}
			
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
		}
		return( randWave );
	}
	
	
	public static void initStereoCoeff()
	{
		evalCoefficient = editPackCoeff.processCoeff( );
		stereoLeftCoefficient = new StereoEvalCoefficient( evalCoefficient , -SongData.STEREO_SEP_SECONDS );
		stereoRightCoefficient = new StereoEvalCoefficient( evalCoefficient , SongData.STEREO_SEP_SECONDS );
	}
	
	
	/**
	 * Sets the number of bytes per sample for the generated audio.
	 * @param bytesPerSample The number of bytes per sample for the generated audio.
	 */
	public static void setBytesPerSample( int bytesPerSample )
	{
		BYTES_PER_SAMPLE = bytesPerSample;
		BITS_PER_SAMPLE = 8 * BYTES_PER_SAMPLE;
		int sz = sampling * BYTES_PER_SAMPLE;
		if( SongData.STEREO_ON )
		{
			sz = sz * 2;
		}
		one_second_sample = new byte[ sz ];
	}
	
	public static void setStereoOn( boolean stereo_on )
	{
		SongData.STEREO_ON = stereo_on;
		int sz = sampling * BYTES_PER_SAMPLE;
		if( SongData.STEREO_ON )
		{
			sz = sz * 2;
		}
		one_second_sample = new byte[ sz ];
	}
	
	/**
	 * Gets the number of bytes per sample for the generated audio.
	 * @return The number of bytes per sample for the generated audio.
	 */
	public static int getBytesPerSample()
	{
		return( BYTES_PER_SAMPLE );
	}
	
	/**
	 * Gets the quantization (number of bits) of the generated audio.
	 * @return The quantization (number of bits) of the generated audio.
	 */
	public static long getQuantization()
	{
		return( quantization );
	}
	
	/**
	 * Sets the quantization (number of bits) of the generated audio.
	 * @param _quantization The quantization (number of bits) of the generated audio.
	 */
	public static void setQuantization( long _quantization )
	{
		quantization = _quantization;
	}
	
	/**
	 * Gets the sampling rate (in hertz) of the generated audio.
	 * @return The sampling rate (in hertz) of the generated audio.
	 */
	public static int getSamplingRate()
	{
		return( sampling );
	}
	
	/**
	 * Sets the sampling rate (in hertz) of the generated audio.
	 * @param samplingRate The sampling rate (in hertz) of the generated audio.
	 */
	public static void setSamplingRate( int samplingRate )
	{
		System.out.println( "Setting sampling rate to " + samplingRate );
		sampling = samplingRate;
		int sz = samplingRate * BYTES_PER_SAMPLE;
		if( SongData.STEREO_ON )
		{
			sz = sz * 2;
		}
		one_second_sample = new byte[ sz ];
	}
	
	/**
	 * Gets a divisor for putting higher-sample-rate audio in a "tower-sample-rate" output file for burning to a disc.
	 * For instance, there are some audiophile CD (Compact Disc) players that will play the
	 * disc at 88.2 khz even though the CD standard specified the format as 44.1 khz.  It was easier
	 * for vendors to put a different audio quality onto an existing disc format than
	 * it was for them to attempt to market a completely new disc format with completely
	 * new manufacturing techniques.
	 * @return The sample-rate divisor.
	 */
	public static int getPseudonymicDivisor()
	{
		return( pseudonymicDivisor );
	}
	
	
	/**
	 * Sets a divisor for putting higher-sample-rate audio in a "tower-sample-rate" output file for burning to a disc.
	 * For instance, there are some audiophile CD (Compact Disc) players that will play the
	 * disc at 88.2 khz even though the CD standard specified the format as 44.1 khz.  It was easier
	 * for vendors to put a different audio quality onto an existing disc format than
	 * it was for them to attempt to market a completely new disc format with completely
	 * new manufacturing techniques.
	 * @param pseudoDiv The sample-rate divisor.
	 */
	public static void setPseudonymicDivisor( int pseudoDiv )
	{
		System.out.println( "Setting pseudonymic divisor to " + pseudoDiv );
		pseudonymicDivisor = pseudoDiv;
	}

	
	
	public static double getNoteUActual(TrackFrame tr, double beat_number, final int core) {
		NoteDesc dsc = tr.getNoteDescActual(beat_number, core);
		double u = (beat_number - dsc.actualStartBeatNumber)
				/ (dsc.actualEndBeatNumber - dsc.actualStartBeatNumber);
		return (u);
	}

	
	/**
	 * Given a track frame and an elapsed time in seconds, gets the elapsed time in seconds for the actual start of the closest note in the frame.
	 * @param tr The input frame containing the notes to be scanned.
	 * @param elapsed_time_seconds
	 * @param core The number of the core thread.
	 * @return The elapsed time in seconds for the actual start of the closest note in the frame.
	 */
	public static double getElapsedTimeForNoteSecondsActual(TrackFrame tr,
			double elapsed_time_seconds,final int core) {
		double beatNumber = SongData.getBeatNumber(elapsed_time_seconds,core);
		NoteDesc desc = tr.getNoteDescActual(beatNumber, core);
		return (SongData.getElapsedTimeForNoteSecondsActual(desc,core));
	}

	/**
	 * Calculates a raw sample value for PCM (Pulse-Code Modulation) audio.
	 * @param elapsed_time_seconds The elapsed time in seconds at which to determine the sample value.
	 * @param core The number of the core thread.
	 * @return The raw sample value for PCM (Pulse-Code Modulation) audio.
	 */
	public static double getSampleValueRaw(final double elapsed_time_seconds , final int core ) {
		double totalWave = 0.0;
		double totalEnvelope = 0.0;

		double beat_number = SongData.getBeatNumber(elapsed_time_seconds,core);
		ArrayList<InstrumentTrack> tracks = SongData.instrumentTracks;
		int ct;
		int st = tracks.size();
		for (ct = 0; ct < st; ct++) {
			InstrumentTrack track = tracks.get(ct);
			if (track.isTrackOn()) {
				NonClampedCoefficient volumeCoeff = track.getTrackVolume(core);
				double volume = volumeCoeff.eval(beat_number);
				ArrayList<TrackFrame> frames = track.getTrackFrames();
				int cf;
				int sf = frames.size();
				for (cf = 0; cf < sf; cf++) {
					TrackFrame tr = frames.get(cf);
					NoteDesc noteDesc = tr.getNoteDescActual(beat_number,core);
					if (noteDesc != null) {
						// double frequency = noteDesc.getFrequency();
						// double wavesPerSecond = frequency;
						// double note_seconds =
						// getElapsedTimeForNoteSecondsActual( tr ,
						// elapsed_time_seconds );
						double u = getNoteUActual(tr, beat_number, core);
						// double waveNumber = ( elapsed_time_seconds -
						// note_seconds ) * wavesPerSecond;
						//double waveNumber = noteDesc.getWaveNumber(u);
						double waveNumber = noteDesc.getWaveNumberElapsedTimeSeconds(elapsed_time_seconds,core);
						double envelope = (noteDesc.getActualNoteEnvelope(core)
								.eval(u))
								* (noteDesc.getWaveEnvelope(core).eval(waveNumber));
						double ret = 0.0;
						if (envelope > 1E-20) {
							WaveForm wave = noteDesc.getWaveform(core);
							double sine = envelope * wave.eval(waveNumber);
							// ret = sine + envelope;
							ret = sine;
						}

						switch(noteDesc.getTotalEnvelopeMode()) {
						case NoteDesc.TOTAL_ENVELOPE_MODE_DRUM :
						{
							double multiplier = 1.0 - envelope
									* (noteDesc.getTotalEnvelopeCoeff());
							totalEnvelope = totalEnvelope * multiplier
									+ envelope * volume;
							totalWave = totalWave * multiplier + ret * volume;
						}
						break;
						case NoteDesc.TOTAL_ENVELOPE_MODE_NONE :
							totalEnvelope += envelope * volume;
							totalWave += ret * volume;
							break;
						case NoteDesc.TOTAL_ENVELOPE_MODE_MULT :
							totalEnvelope += envelope * volume * (noteDesc.getTotalEnvelopeCoeff());
							totalWave += ret * volume * (noteDesc.getTotalEnvelopeCoeff());
							break;
						}
					}
				}

			}
		}

		if (!(SongData.CALC_GLOBAL_MAX_VAL ) && (totalEnvelope > 1.0)) {
			totalWave = totalWave / totalEnvelope;
		}
		return (totalWave);
	}

	/**
	 * Builds a one-second sample of PCM (Pulse-Code Modulation) mono-stereo audio with a depth of 32 bits.
	 * @param inTime The starting elapsed time in seconds for the one-second sample.
	 * @return The bytes of the one-second sample of PCM (Pulse-Code Modulation) audio.
	 * @throws Throwable
	 */
	public static byte[] buildOneSecondSample32bMono(int inTime) throws Throwable {
		final int SAMPLING = sampling;
		final long QUANTIZATION = quantization;
		final byte[] ONE_SECOND_SAMPLE = one_second_sample;
		final int len = ONE_SECOND_SAMPLE.length;
		final int sample = inTime * SAMPLING;
		final int NUM_CORES = CpuInfo.getNumCores();
		final int STEP_BYTES = BYTES_PER_SAMPLE * NUM_CORES;
		int ccnt;
		
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final NonClampedCoefficient randWave = getRandWave();

		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					int i;
					try{
						for (i = core * BYTES_PER_SAMPLE ; i < len ; i += STEP_BYTES ) {
							int index = sample + i / BYTES_PER_SAMPLE;
							double elapsed_time_seconds = (double) (index) / SAMPLING;

							double sampleVal = evalCoefficient.eval(elapsed_time_seconds,core)
								/ globalMaxVal;

							// double heightD = ( sampleVal ) * (0x7fffffff / 2);
							double heightD = (sampleVal) * (0x7fffffff) / QUANTIZATION;
							long heightLng = (long) heightD;
							double htRng = heightD - heightLng;
							htRng = Math.max(htRng, 0.0);
							htRng = Math.min(htRng, 1.0);
							long height = heightLng * QUANTIZATION;
							if (htRng > ( ( randWave.eval( elapsed_time_seconds ) ) / 2.0 ) ) {
								height = height + QUANTIZATION;
							}

							ONE_SECOND_SAMPLE[i + 3] = (byte) ((height & 0xff000000) >> 24);   // Little Endian
							ONE_SECOND_SAMPLE[i + 2] = (byte) ((height & 0xff0000) >> 16);
							ONE_SECOND_SAMPLE[i + 1] = (byte) ((height & 0xff00) >> 8);
							ONE_SECOND_SAMPLE[i] = (byte) (height & 0xff);
						}
					} catch( Error ex ) { ex.printStackTrace( System.out ); }
					catch( Throwable ex ) { ex.printStackTrace( System.out ); }
					
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);

		return ( ONE_SECOND_SAMPLE );
	}
	
	/**
	 * Builds a one-second sample of PCM (Pulse-Code Modulation) mono-stereo audio with a depth of 24 bits.
	 * @param inTime The starting elapsed time in seconds for the one-second sample.
	 * @return The bytes of the one-second sample of PCM (Pulse-Code Modulation) audio.
	 * @throws Throwable
	 */
	public static byte[] buildOneSecondSample24bMono(int inTime) throws Throwable {
		final int SAMPLING = sampling;
		final long QUANTIZATION = quantization;
		final byte[] ONE_SECOND_SAMPLE = one_second_sample;
		final int len = ONE_SECOND_SAMPLE.length;
		final int sample = inTime * SAMPLING;
		final int NUM_CORES = CpuInfo.getNumCores();
		final int STEP_BYTES = BYTES_PER_SAMPLE * NUM_CORES;
		int ccnt;
		
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final NonClampedCoefficient randWave = getRandWave();

		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					int i;
					try {
						for (i = core * BYTES_PER_SAMPLE ; i < len ; i += STEP_BYTES ) {
							int index = sample + i / BYTES_PER_SAMPLE;
							double elapsed_time_seconds = (double) (index) / SAMPLING;

							double sampleVal = evalCoefficient.eval(elapsed_time_seconds,core)
								/ globalMaxVal;

							// double heightD = ( sampleVal ) * (0x7fffffff / 2);
							double heightD = (sampleVal) * (0x7fffffff) / QUANTIZATION;
							long heightLng = (long) heightD;
							double htRng = heightD - heightLng;
							htRng = Math.max(htRng, 0.0);
							htRng = Math.min(htRng, 1.0);
							long height = heightLng * QUANTIZATION;
							if (htRng > ( ( randWave.eval( elapsed_time_seconds ) ) / 2.0 ) ) {
								height = height + QUANTIZATION;
							}

							ONE_SECOND_SAMPLE[i + 2] = (byte) ((height & 0xff000000) >> 24); // Little Endian
							ONE_SECOND_SAMPLE[i + 1] = (byte) ((height & 0xff0000) >> 16);
							ONE_SECOND_SAMPLE[i] = (byte) ((height & 0xff00) >> 8);
						}
					} catch( Error ex ) { ex.printStackTrace( System.out ); }
					catch( Throwable ex ) { ex.printStackTrace( System.out ); }
					
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);

		return ( ONE_SECOND_SAMPLE );
	}
	
	/**
	 * Builds a one-second sample of PCM (Pulse-Code Modulation) stereo audio with a depth of 24 bits.
	 * @param inTime The starting elapsed time in seconds for the one-second sample.
	 * @return The bytes of the one-second sample of PCM (Pulse-Code Modulation) audio.
	 * @throws Throwable
	 */
	public static byte[] buildOneSecondSample24bStereo(int inTime) throws Throwable {
		final int SAMPLING = sampling;
		final long QUANTIZATION = quantization;
		final byte[] ONE_SECOND_SAMPLE = one_second_sample;
		final int len = ONE_SECOND_SAMPLE.length;
		final int step = 2 * BYTES_PER_SAMPLE;
		final int sample = inTime * SAMPLING;
		final int NUM_CORES = CpuInfo.getNumCores();
		final int STEP_BYTES = step * NUM_CORES;
		int ccnt;
		
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final NonClampedCoefficient randWave = getRandWave();

		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					int i;
					try {
						for (i = core * step; i < len; i += STEP_BYTES) {
							int index = sample + i / step;
							double elapsed_time_seconds = (double) (index) / SAMPLING;

							double sampleVal = stereoLeftCoefficient.eval(elapsed_time_seconds,core)
								/ globalMaxVal;

							// double heightD = ( sampleVal ) * (0x7fffffff / 2);
							double heightD = (sampleVal) * (0x7fffffff) / QUANTIZATION;
							long heightLng = (long) heightD;
							double htRng = heightD - heightLng;
							htRng = Math.max(htRng, 0.0);
							htRng = Math.min(htRng, 1.0);
							long height = heightLng * QUANTIZATION;
							if (htRng > ( ( randWave.eval( elapsed_time_seconds ) ) / 2.0 ) ) {
								height = height + QUANTIZATION;
							}

							ONE_SECOND_SAMPLE[i + 2] = (byte) ((height & 0xff000000) >> 24); // Little Endian
							ONE_SECOND_SAMPLE[i + 1] = (byte) ((height & 0xff0000) >> 16);
							ONE_SECOND_SAMPLE[i] = (byte) ((height & 0xff00) >> 8);
						}
		
						for (i = core * step; i < len; i += STEP_BYTES) {
							int index = sample + i / step;
							double elapsed_time_seconds = (double) (index) / SAMPLING;

							double sampleVal = stereoRightCoefficient.eval(elapsed_time_seconds,core)
								/ globalMaxVal;

							// double heightD = ( sampleVal ) * (0x7fffffff / 2);
							double heightD = (sampleVal) * (0x7fffffff) / QUANTIZATION;
							long heightLng = (long) heightD;
							double htRng = heightD - heightLng;
							htRng = Math.max(htRng, 0.0);
							htRng = Math.min(htRng, 1.0);
							long height = heightLng * QUANTIZATION;
							if (htRng > ( ( randWave.eval( elapsed_time_seconds ) ) / 2.0 ) ) {
								height = height + QUANTIZATION;
							}

							ONE_SECOND_SAMPLE[i + 5] = (byte) ((height & 0xff000000) >> 24); // Little Endian
							ONE_SECOND_SAMPLE[i + 4] = (byte) ((height & 0xff0000) >> 16);
							ONE_SECOND_SAMPLE[i + 3] = (byte) ((height & 0xff00) >> 8);
						}
					} catch( Error ex ) { ex.printStackTrace( System.out ); }
					catch( Throwable ex ) { ex.printStackTrace( System.out ); }
					
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);

		return ( ONE_SECOND_SAMPLE );
	}
	
	
	/**
	 * Builds a one-second sample of PCM (Pulse-Code Modulation) stereo audio with a depth of 32 bits.
	 * @param inTime The starting elapsed time in seconds for the one-second sample.
	 * @return The bytes of the one-second sample of PCM (Pulse-Code Modulation) audio.
	 * @throws Throwable
	 */
	public static byte[] buildOneSecondSample32bStereo(int inTime) throws Throwable {
		final int SAMPLING = sampling;
		final long QUANTIZATION = quantization;
		final byte[] ONE_SECOND_SAMPLE = one_second_sample;
		final int len = ONE_SECOND_SAMPLE.length;
		final int step = 2 * BYTES_PER_SAMPLE;
		final int sample = inTime * SAMPLING;
		final int NUM_CORES = CpuInfo.getNumCores();
		final int STEP_BYTES = step * NUM_CORES;
		int ccnt;
		
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final NonClampedCoefficient randWave = getRandWave();

		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					int i;
					try {
						for (i = core * step; i < len; i += STEP_BYTES) {
							int index = sample + i / step;
							double elapsed_time_seconds = (double) (index) / SAMPLING;

							double sampleVal = stereoLeftCoefficient.eval(elapsed_time_seconds,core)
								/ globalMaxVal;

							// double heightD = ( sampleVal ) * (0x7fffffff / 2);
							double heightD = (sampleVal) * (0x7fffffff) / QUANTIZATION;
							long heightLng = (long) heightD;
							double htRng = heightD - heightLng;
							htRng = Math.max(htRng, 0.0);
							htRng = Math.min(htRng, 1.0);
							long height = heightLng * QUANTIZATION;
							if (htRng > ( ( randWave.eval( elapsed_time_seconds ) ) / 2.0 ) ) {
								height = height + QUANTIZATION;
							}

							ONE_SECOND_SAMPLE[i + 3] = (byte) ((height & 0xff000000) >> 24); // Little Endian
							ONE_SECOND_SAMPLE[i + 2] = (byte) ((height & 0xff0000) >> 16);
							ONE_SECOND_SAMPLE[i + 1] = (byte) ((height & 0xff00) >> 8);
							ONE_SECOND_SAMPLE[i] = (byte) (height & 0xff);
						}
		
						for (i = core * step; i < len; i += STEP_BYTES) {
							int index = sample + i / step;
							double elapsed_time_seconds = (double) (index) / SAMPLING;

							double sampleVal = stereoRightCoefficient.eval(elapsed_time_seconds,core)
								/ globalMaxVal;

							// double heightD = ( sampleVal ) * (0x7fffffff / 2);
							double heightD = (sampleVal) * (0x7fffffff) / QUANTIZATION;
							long heightLng = (long) heightD;
							double htRng = heightD - heightLng;
							htRng = Math.max(htRng, 0.0);
							htRng = Math.min(htRng, 1.0);
							long height = heightLng * QUANTIZATION;
							if (htRng > ( ( randWave.eval( elapsed_time_seconds ) ) / 2.0 ) ) {
								height = height + QUANTIZATION;
							}

							ONE_SECOND_SAMPLE[i + 7] = (byte) ((height & 0xff000000) >> 24); // Little Endian
							ONE_SECOND_SAMPLE[i + 6] = (byte) ((height & 0xff0000) >> 16);
							ONE_SECOND_SAMPLE[i + 5] = (byte) ((height & 0xff00) >> 8);
							ONE_SECOND_SAMPLE[i + 4] = (byte) (height & 0xff);
						}
					} catch( Error ex ) { ex.printStackTrace( System.out ); }
					catch( Throwable ex ) { ex.printStackTrace( System.out ); }
					
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);

		return ( ONE_SECOND_SAMPLE );
	}

	
	/**
	 * Calculates the maximum value of samples of the evalCoefficient member for a one-second sample to be used for normalization.
	 * @param inTime  The starting elapsed time in seconds for the one-second sample.
	 * @param imax The previous largest normalization value against which to compare.
	 * @return The maximum value of samples of the evalCoefficient member for a one-second sample to be used for normalization.
	 * @throws Throwable
	 */
	protected static double buildSampleMax(int inTime, final double imax) throws Throwable {
		final int SAMPLING = sampling;
		final byte[] ONE_SECOND_SAMPLE = one_second_sample;
		final int len = ONE_SECOND_SAMPLE.length;
		final int sample = inTime * SAMPLING;
		final int NUM_CORES = CpuInfo.getNumCores();
		final int STEP_BYTES = BYTES_PER_SAMPLE * NUM_CORES;
		int ccnt;
		
		final double[] max = CpuInfo.createDbl( imax );
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );

		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					int i;
					for (i = core * BYTES_PER_SAMPLE ; i < len ; i += STEP_BYTES ) {
						int index = sample + i / BYTES_PER_SAMPLE;
						double elapsed_time_seconds = (double) (index) / SAMPLING;

						double sampleVal = evalCoefficient.eval(elapsed_time_seconds,core);

						max[ core ] = Math.max(Math.abs(sampleVal), max[core]);
					}
					
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);

		return( CpuInfo.max( max ) );
	}

	
	/**
	 * Calculates the global maximum value to be used for normalization for a mono-stereo mix.
	 * @param start_time_seconds The start time in seconds.
	 * @param max The number of time points to sample.
	 * @param deltaSeconds The number of seconds over which to sample.
	 * @return The global maximum value to be used for normalization.
	 * @throws Throwable
	 */
	protected static double buildSampleMaxMono(int start_time_seconds, int max,
			int deltaSeconds) throws Throwable {
		int i;
		double maxVal = 0.0;

		System.out.println("Started Sampling For Max...");

		for (i = start_time_seconds; i < max; i++) {

			System.out.println("Sampled " + (i - start_time_seconds)
					+ " seconds out of " + deltaSeconds);

			maxVal = buildSampleMax(i, maxVal);
		}

		return (maxVal);
	}
	
	/**
	 * Calculates the global maximum value to be used for normalization for a stereo mix.
	 * @param start_time_seconds The start time in seconds.
	 * @param max The number of time points to sample.
	 * @param deltaSeconds The number of seconds over which to sample.
	 * @return The global maximum value to be used for normalization.
	 * @throws Throwable
	 */
	protected static double buildSampleMaxStereo(int start_time_seconds, int max,
			int deltaSeconds) throws Throwable {
		int i;
		double maxVal = 0.0;
		
		NonClampedCoefficientMultiCore tmp = evalCoefficient;

		System.out.println("Started Sampling For Max...");

		for (i = start_time_seconds; i < max; i++) {

			System.out.println("Sampled " + (i - start_time_seconds)
					+ " seconds out of " + deltaSeconds);
			
			evalCoefficient = stereoLeftCoefficient;

			maxVal = buildSampleMax(i, maxVal);
			
			evalCoefficient = stereoRightCoefficient;

			maxVal = buildSampleMax(i, maxVal);
		}
		
		evalCoefficient = tmp;

		return (maxVal);
	}
	
	
	/**
	 * Calculates the global maximum value to be used for normalization.
	 * @param start_time_seconds The start time in seconds.
	 * @param max The number of time points to sample.
	 * @param deltaSeconds The number of seconds over which to sample.
	 * @return The global maximum value to be used for normalization.
	 * @throws Throwable
	 */
	protected static double buildSampleMax(int start_time_seconds, int max,
			int deltaSeconds) throws Throwable {
		if( SongData.STEREO_ON )
		{
			return( buildSampleMaxStereo(start_time_seconds, max, deltaSeconds) );
		}
		else
		{
			return( buildSampleMaxMono(start_time_seconds, max, deltaSeconds) );
		}
	}
	
	
	/**
	 * Estimates and prints the approximate time (number of days and/or hours) required to complete the generation of a song with the current set of parameters.
	 * @throws Throwable
	 */
	public static void estimateTimeToComplete() throws Throwable
	{
		initStereoCoeff();
		final int max_seconds = 45;
		
		System.out.println( "Results in " + max_seconds + "seconds." );
		
		final int runs_per_second = 10;
		final int start_time_seconds = SongData.getStartTimeSeconds( 0 );
		final double end_time_seconds = SongData.getEndTimeSeconds( 0 );
		int max = (int) (end_time_seconds) + 2;
		int deltaSeconds = max - start_time_seconds;
		
		double val = 0.0;
		
		int count;
		
		
		final int NUM_CORES = CpuInfo.getNumCores();
		final Random[] rand = CpuInfo.createRand( 6502 );
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final int[] sCountTen = CpuInfo.createInt(0);
		int ccnt;
		
		long startTime = System.currentTimeMillis();
		final long stopTime = startTime + max_seconds * 1000;
		
		final long[] ctime = new long[ NUM_CORES ];
		
		
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					int count;
					double rTime = 0.0;
					double val = 0.0;
					long curTime = System.currentTimeMillis();
					while( curTime < stopTime )
					{
						for( count = 0 ; count < runs_per_second ; count++ )
						{
							rTime = ( rand[ core ].nextDouble() ) * ( end_time_seconds - start_time_seconds ) 
							+ start_time_seconds;
							val = val + evalCoefficient.eval( rTime , core );
						}
						( sCountTen[ core ] )++;
						curTime = System.currentTimeMillis();
					}
					ctime[ core ] = curTime;
					
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);
		
		int sampCountTen = CpuInfo.sum( sCountTen );
		double avgDiff = CpuInfo.avgDiff( ctime , startTime );
		double sampPerSec = sampCountTen * 1.0 * runs_per_second / ( avgDiff / 1000.0 );
		double estTimeSeconds = sampling * deltaSeconds / sampPerSec;
		if( SongData.CALC_GLOBAL_MAX_VAL )
		{
			estTimeSeconds = estTimeSeconds * 2.0;
		}
		if( SongData.STEREO_ON )
		{
			estTimeSeconds = estTimeSeconds * 2.0;
		}
		
		int estTimeDays = (int)( estTimeSeconds / 86400.0 );
		int estTimeHours = (int)( estTimeSeconds / 3600.0 - estTimeDays * 24 ) % 24;
		int estTimeMinutes = (int)( estTimeSeconds / 60.0 - estTimeHours * 60 - estTimeDays * 1440 ) % 60;
		int estTimeSecDelta = (int)( estTimeSeconds - estTimeMinutes * 60 - estTimeHours * 3600 - estTimeDays * 86400 ) % 60;
		System.out.println( "freq " + sampling );
		System.out.println( "rough draft " + SongData.ROUGH_DRAFT_MODE );
		System.out.println( "global max " + SongData.CALC_GLOBAL_MAX_VAL );
		System.out.println( "res " + val );
		System.out.println( "** Estimated Time To complete (seconds)-- " + estTimeDays + " / " + estTimeHours + 
				" : " + estTimeMinutes + " : " + estTimeSecDelta );
	}
	
	
	
	protected static double estimateCoreCapability( final int ncores , final int max_seconds ) throws Throwable
	{
		initStereoCoeff();
		
		System.out.println( "Results in " + max_seconds + "seconds." );
		
		final int runs_per_second = 10;
		final int start_time_seconds = SongData.getStartTimeSeconds( 0 );
		final double end_time_seconds = SongData.getEndTimeSeconds( 0 );
		int max = (int) (end_time_seconds) + 2;
		int deltaSeconds = max - start_time_seconds;
		
		double val = 0.0;
		
		int count;
		
		
		final int NUM_CORES = ncores;
		final Random[] rand = CpuInfo.createRand( 6502 );
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final int[] sCountTen = CpuInfo.createInt(0);
		int ccnt;
		
		long startTime = System.currentTimeMillis();
		final long stopTime = startTime + max_seconds * 1000;
		
		final long[] ctime = new long[ NUM_CORES ];
		
		
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					int count;
					double rTime = 0.0;
					double val = 0.0;
					long curTime = System.currentTimeMillis();
					while( curTime < stopTime )
					{
						for( count = 0 ; count < runs_per_second ; count++ )
						{
							rTime = ( rand[ core ].nextDouble() ) * ( end_time_seconds - start_time_seconds ) 
							+ start_time_seconds;
							val = val + evalCoefficient.eval( rTime , core );
						}
						( sCountTen[ core ] )++;
						curTime = System.currentTimeMillis();
					}
					ctime[ core ] = curTime;
					
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);
		
		int sampCountTen = CpuInfo.sum( sCountTen );
		double avgDiff = CpuInfo.avgDiff( ctime , startTime );
		double sampPerSec = sampCountTen * 1.0 * runs_per_second / ( avgDiff / 1000.0 );
		return( sampPerSec );
	}
	
	
	
	protected static void estimateCoreCapability( final int ncores , final int max_seconds , final int sampSize ) throws Throwable
	{
		final double[] samples = new double[ sampSize ];
		double sum = 0.0;
		int count;
		for( count = 0 ; count < sampSize ; count++ )
		{
			samples[ count ] = estimateCoreCapability( ncores , max_seconds );
			sum += samples[ count ];
		}
		sum = sum / sampSize;
		final double avg = sum;
		sum = 0.0;
		for( count = 0 ; count < sampSize ; count++ )
		{
			final double del = samples[ count ] - avg;
			sum += del * del;
		}
		sum = sum / sampSize;
		final double sigma = Math.sqrt( sum );
		System.out.println( "******************" );
		System.out.println( "cores-- " + ncores );
		System.out.println( "sigma-- " + sigma );
		System.out.println( "avg-- " + avg );
		System.out.println( "strt-- " + ( avg - 3.0 * sigma ) );
		System.out.println( "end-- " + ( avg + 3.0 * sigma ) );
	}
	
	
	
	public static void estimateCoreCapability() throws Throwable
	{
		int ncores;
		for( ncores = 12 ; ncores >= 1 ; ncores-- )
		{
			// CpuInfo.setNumCores( ncores ); !!!!!!!!!!!!!!!!!!!!!!!!!!!
			estimateCoreCapability( ncores , 45 , 10 );
		}
		// CpuInfo.resetNumCores(); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	
	
	/**
	 * Incrementing count used to ensure that each generated audio file has a unique name.
	 */
	protected static int movieCount = 0;

	
	/**
	 * Generates a 32-bit WAV format file containing a PCM (Pulse-Code Modulation) representation of the song.
	 * @return The generated 32-bit WAV file.
	 * @throws Throwable
	 */
	public static File generateSound() throws Throwable {
		final int SAMPLING = sampling;
		File fi = File.createTempFile("abc",".wav");
		// File fi = new File("buildaudio" + movieCount + ".mov");

		int timeScale = SAMPLING;

		final int numChannels = SongData.STEREO_ON ? 2 : 1;
		

		final int sampleRate = SAMPLING / pseudonymicDivisor;
		
		final boolean bigEndian = true;
		
		final boolean signed = true;

		
		initStereoCoeff();
		final int start_time_seconds = SongData.getStartTimeSeconds(0);
		double end_time_seconds = SongData.getEndTimeSeconds(0);
		final int max = (int) (end_time_seconds) + 2;
		final int deltaSeconds = max - start_time_seconds;

		System.out
				.println("Data Set Constructed.  Started Sampling Waveforms.");

		System.out.println("Started " + (Calendar.getInstance()));

		System.out.println(SongData.instrumentTracks.size());
		InstrumentTrack itrack = SongData.instrumentTracks.get(0);
		System.out.println(itrack.getTrackFrames().size());

		if (SongData.CALC_GLOBAL_MAX_VAL) {
			globalMaxVal = buildSampleMax(start_time_seconds, max, deltaSeconds);
			System.out.println("Global Max Val " + globalMaxVal);
			globalMaxVal = Math.max( globalMaxVal , 1E-150 );
			System.out.println("Global Max Val 2 " + globalMaxVal);
		} else {
			globalMaxVal = 1.0;
		}
		
		final int numFrames = Math.max( max - start_time_seconds , 0 );

		
		
		
		PcmContentReader preader = new PcmContentReader()
		{
			public byte[] handleRead( int frameNumber ) throws Throwable
			{
			final int i = frameNumber + start_time_seconds;
				
			System.out.println("Sampled " + (i - start_time_seconds)
					+ " seconds out of " + deltaSeconds);

			if( BYTES_PER_SAMPLE == 4 )
			{
				if( SongData.STEREO_ON )
				{
					return( buildOneSecondSample32bStereo(i) );

				}
				else
				{
					return( buildOneSecondSample32bMono(i) );

				}
			}
			else
			{
				if( SongData.STEREO_ON )
				{
					return( buildOneSecondSample24bStereo(i) );

				}
				else
				{
					return( buildOneSecondSample24bMono(i) );

				}
			}
		}
	};

		
		System.out.println("Waveform Sampling Completed.");
		
		System.out.println( fi.getAbsolutePath() );
		
		final WavWriter_32 writer = new WavWriter_32(  fi  );
		
		writer.setSampleRate( sampleRate );
		writer.setBitsPerSample(  BYTES_PER_SAMPLE * 8  );
		writer.setNumberOfChannels(  numChannels  );  
		
		
		writer.start();
		
		
		for( int frameNumber = 0 ; frameNumber < numFrames ; frameNumber++ )
		{
			final byte[] bytes = preader.handleRead(frameNumber);
			writer.write( bytes );
		}
		
		writer.close();

		System.out.println("Inserted Media.");

		movieCount++;

		return ( fi );
	}

	/**
	 * Constructor.
	 */
	public TestPlayer2() {
		initializeUndoMgr();
		macroMap = new MacroTreeMap(undoMgr);
		docPageFormat = new DocPageFormat(undoMgr);
		onlyDesignerEdits = new OnlyDesignerEdits(undoMgr);
		undoMgr.addPropertyChangeListener(this);
		PropL = new PropertyChangeSupport(this);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		setOpaque(true);
		setMinimumSize(new Dimension(2, 2));
		setPreferredSize(new Dimension(400, 400));
		setDoubleBuffered(false);
		setToolTipText("Right-Click to change color");
		VerdantiumDragUtils.setDragUtil(this, this);
		VerdantiumDropUtils.setDropUtil(this, this, this);
		configureForEtherEvents();
		/* MyTime.start(); */
	}

	protected void initializeUndoMgr() {
		ExtMilieuRef mil = jundo.runtime.Runtime.getInitialMilieu();

		undoMgr = UndoManager.createInstanceUndoManager(mil);
	}

	/**
	 * Returns the GUI of the component.
	 */
	public JComponent getGUI() {
		return (this);
	}

	/**
	 * Handles a mouse event by switching whether the component is animated.
	 */
	public void processMouseEvent(MouseEvent e) {
		switch (e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			try {
				if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
					handlePopupTrigger(e);
			} catch (Throwable ex) {
				handleThrow(ex);
			}
			break;

		}

		super.processMouseEvent(e);
	}

	/**
	 * Handles a popup trigger by displaying the component's property editor.
	 */
	public void handlePopupTrigger(MouseEvent e) {
		try {
			if (((DesignerControl.isDesignTime()) || (!isOnlyDesignerEdits()))
					&& !(e.isAltDown())) {
				EtherEvent send = new StandardEtherEvent(this,
						StandardEtherEvent.showPropertiesEditor, null, this);
				send.setParameter(e.getPoint());
				ProgramDirector.fireEtherEvent(send, null);
			}
		} catch (Throwable ex) {
			handleThrow(ex);
		}
	}

	/**
	 * Handles Ether Events to alter the properties of the component.
	 */
	public Object processObjEtherEvent(EtherEvent in, Object refcon)
			throws Throwable {

		Object ret = super.processObjEtherEvent(in, refcon);
		if (ret != EtherEvent.EVENT_NOT_HANDLED) {
			return (ret);
		}

		if (in instanceof PropertyEditEtherEvent) {

		}

		return (null);
	}

	public void handleOnlyDesignerEditsChange() {
		if (isOnlyDesignerEdits())
			setToolTipText(null);
		else
			setToolTipText("Right-Click to edit properties");
	}

	/**
	 * Creates the properties editor for the component.
	 */
	public VerdantiumPropertiesEditor makePropertiesEditor() {
		Properties MyP = new Properties();
		DefaultPropertyEditor MyEdit = new SndHndPropertyEditor(this, MyP, outputChoice);
		MyEdit.setClickPoint(new Point(10, 10));
		return (MyEdit);
	}

	/**
	 * Shows the properties editor for the component.
	 */
	public void showPropertiesEditor(EtherEvent e) {
		VerdantiumPropertiesEditor MyEdit = makePropertiesEditor();
		((DefaultPropertyEditor) MyEdit).setClickPoint((Point) (e
				.getParameter()));
		ProgramDirector.showPropertyEditor(MyEdit, getGUI(),
				"SndHnd Property Editor");
	}

	/**
	 * Returns the persistent data flavors from which the component can load its
	 * state.
	 */
	public static DataFlavor[] getPersistentInputDataFlavorsSupported() {
		DataFlavor[] MyF = { new TransVersionBufferFlavor("SndHnd", "SndHnd") };
		return (MyF);
	}

	/**
	 * Returns the persistent data flavors to which the component can save its
	 * state.
	 */
	public DataFlavor[] getPersistentOutputDataFlavorsSupported() {
		DataFlavor[] MyF = { new TransVersionBufferFlavor("SndHnd", "SndHnd") };
		return (MyF);
	}

	/**
	 * Loads the component's data from persistent storage.
	 */
	public void loadPersistentData(DataFlavor flavor, Transferable trans)
			throws IOException {
		if (trans instanceof UrlHolder) {
			fileSaveURL = ((UrlHolder) trans).getUrl();
			fileSaveFlavor = flavor;
		}

		if (trans == null) {
			onlyDesignerEdits.setOnlyDesignerEdits(false);
			macroMap.clear();
			docPageFormat.setDocPageFormat(null);
			
			SongData.NUM_MEASURES = 3;
			SongData.measuresStore = new MeasuresStore();
			
			PiecewiseCubicMonotoneBezierFlatMultiCore bez = new PiecewiseCubicMonotoneBezierFlatMultiCore();
			bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 110.0 ) );
			bez.getInterpolationPoints().add( new InterpolationPoint( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) , 110.0 ) );
			bez.updateAll();
			SongData.TEMPO_BEATS_PER_MINUTE_CRV = new BezierCubicNonClampedCoefficientFlatMultiCore( bez );
			SongData.handleTempoUpdate(0);
			
			SongData.START_BEAT = 0;
			SongData.END_BEAT = SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES );
			editPackCoeff = new EditPackCoeff();
			editPackIntonation = new EditPackIntonation();
			editPackHarmony = new EditPackHarmony();
			initStereoCoeff();
			SongData.instrumentTracks = new ArrayList<InstrumentTrack>();
			
			SongData.pseudoLoopMap.clear();
			SongData.skipVibratoForRoughDraft = true;
			
			if( outputChoice != null )
			{
				outputChoice.refreshList();
			}
			SongListeners.updateViewPanes();
			
			repaint();
		} else {
			try {
				TransVersionBuffer MyF = (TransVersionBuffer) trans;
				VersionBuffer.chkNul(MyF);
				onlyDesignerEdits.setOnlyDesignerEdits(MyF
						.getBoolean("OnlyDesignerEdits"));
				macroMap.readData(MyF);

				SongData.TEMPO_BEATS_PER_MINUTE_CRV = (BezierCubicNonClampedCoefficientFlatMultiCore)( MyF.getPropertyEx("TempoBeatsPerMinute") );
				SongData.VIBR_WAVES_PER_SECOND_CRV = (BezierCubicNonClampedCoefficientFlatMultiCore)( MyF.getPropertyEx("VibrWavesPerSecond") );
				SongData.handleTempoUpdate(0);
				SongData.NUM_MEASURES = MyF.getInt("NumMeasures");
				SongData.measuresStore = (MeasuresStore)( MyF.getProperty("MeasuresStore") );
				SongData.START_BEAT = MyF.getDouble("StartBeat");
				SongData.END_BEAT = MyF.getDouble("EndBeat");
				editPackCoeff = (EditPackCoeff)( MyF.getProperty( "EditPackCoeff" ) );
				editPackIntonation = (EditPackIntonation)( MyF.getProperty( "EditPackIntonation" ) );
				editPackHarmony = (EditPackHarmony)( MyF.getProperty( "EditPackHarmony" ) );
				NoteTable.handleRatiosUpdate();
				initStereoCoeff();

				int plen = MyF.getInt("TrackSize");
				SongData.instrumentTracks = new ArrayList<InstrumentTrack>(plen);
				int count;
				for (count = 0; count < plen; count++) {
					InstrumentTrack track = (InstrumentTrack) (MyF
							.getPropertyEx("Track_" + count));
					SongData.instrumentTracks.add(track);
					track.updateTrackFrames(0);
				}
				
				plen = MyF.getInt("PseudoLoopMapSize");
				count = 0;
				for( count = 0 ; count < plen ; count++ )
				{
					String key = (String)( MyF.getProperty( "PseudoLoopMapKey_" + count ) );
					ArrayList<NoteInitializer> arr = readPseudoLoopArrList( "PseudoLoopMapVal_" + count , MyF );
					SongData.pseudoLoopMap.put( key , arr );
				}
				SongData.skipVibratoForRoughDraft = MyF.getBoolean("SkipVibratoForRoughDraft");

			} catch (IOException ex) {
				throw (ex);
			} catch (Throwable ex) {
				throw (new DataFormatException(ex));
			}
			
			outputChoice.refreshList();
			SongListeners.updateViewPanes();

			try {
				outputChoice.regenerateMovie(0,1.0);
			} catch (Throwable ex) {
				ex.printStackTrace(System.out);
			}

			repaint();
		}

	}

	/**
	 * Saves the component's data to persistent storage.
	 */
	public Transferable savePersistentData(DataFlavor flavor) {
		TransVersionBuffer MyF = new TransVersionBuffer("SndHnd", "SndHnd");

		MyF.setProperty("TempoBeatsPerMinute", SongData.TEMPO_BEATS_PER_MINUTE_CRV);
		MyF.setProperty("VibrWavesPerSecond", SongData.VIBR_WAVES_PER_SECOND_CRV);
		MyF.setInt("NumMeasures", SongData.NUM_MEASURES);
		MyF.setProperty("MeasuresStore", SongData.measuresStore);
		MyF.setDouble("StartBeat", SongData.START_BEAT);
		MyF.setDouble("EndBeat", SongData.END_BEAT);
		MyF.setProperty("EditPackCoeff", editPackCoeff);
		MyF.setProperty("EditPackIntonation", editPackIntonation);
		MyF.setProperty("EditPackHarmony", editPackHarmony);

		MyF.setInt("TrackSize", SongData.instrumentTracks.size());
		int plen = SongData.instrumentTracks.size();
		int count;
		for (count = 0; count < plen; count++) {
			MyF.setProperty("Track_" + count, SongData.instrumentTracks
					.get(count));
		}
		
		plen = SongData.pseudoLoopMap.keySet().size();
		MyF.setInt("PseudoLoopMapSize", plen);
		count = 0;
		for( Entry<String,ArrayList<NoteInitializer>> e : SongData.pseudoLoopMap.entrySet()  )
		{
			String key = e.getKey();
			MyF.setProperty( "PseudoLoopMapKey_" + count , key );
			ArrayList<NoteInitializer> arr = e.getValue();
			writePseudoLoopArrList( "PseudoLoopMapVal_" + count , arr , MyF );
			count++;
		}
		MyF.setBoolean("SkipVibratoForRoughDraft",SongData.skipVibratoForRoughDraft);

		MyF.setBoolean("OnlyDesignerEdits", isOnlyDesignerEdits());
		macroMap.writeData(MyF);
		return (MyF);
	}
	
	protected void writePseudoLoopArrList( String key , ArrayList<NoteInitializer> in , TransVersionBuffer MyF )
	{
		int sz = in.size();
		int count;
		MyF.setInt( key + "_sz", sz);
		for( count = 0 ; count < sz ; count++ )
		{
			MyF.setProperty(  key + "_index_" + count , in.get( count ) );
		}
	}
	
	protected ArrayList<NoteInitializer> readPseudoLoopArrList( String key , TransVersionBuffer MyF ) throws DataFormatException
	{
		ArrayList<NoteInitializer> arr = new ArrayList<NoteInitializer>();
		int sz = MyF.getInt( key + "_sz");
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			NoteInitializer noti = (NoteInitializer)( MyF.getProperty(  key + "_index_" + count ) );
			arr.add( noti );
		}
		return( arr );
	}

	protected static OutputChoiceInterface outputChoice = null;

	/**
	 * Test driver.
	 * @param args Input arguments.
	 */
	public static void main(String[] argv) {
		System.out.println( "Starting..." );
		
		try {
			
			setBytesPerSample( 3 );
			
			PiecewiseCubicMonotoneBezierFlatMultiCore bez = new PiecewiseCubicMonotoneBezierFlatMultiCore();
			bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , /* 110.0 */ 121.22 ) );
			bez.getInterpolationPoints().add( new InterpolationPoint( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) , /* 110.0 */ 121.22 ) );
			bez.updateAll();
			SongData.TEMPO_BEATS_PER_MINUTE_CRV = new BezierCubicNonClampedCoefficientFlatMultiCore( bez );
			
			bez = new PiecewiseCubicMonotoneBezierFlatMultiCore();
			bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , SongData.INITIAL_VIBRATO_RATE ) );
			bez.getInterpolationPoints().add( new InterpolationPoint( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) , SongData.INITIAL_VIBRATO_RATE ) );
			bez.updateAll();
			SongData.VIBR_WAVES_PER_SECOND_CRV = new BezierCubicNonClampedCoefficientFlatMultiCore( bez );
			
			SongData.handleTempoUpdate(0);

			InstrumentTrack track = new InstrumentTrack();
			SongData.instrumentTracks.add(track);
			track.getTrackFrames().add(SongData.buildNotes());
			
			Class<? extends IntelligentAgent> clss = null;
			System.out.println( "Reached Loop..." );
			while( clss == null )
			{
				clss = AgentManager.getMapClone().get( "High Guitar Agent" );
				
				if( clss == null )
				{
					synchronized( Thread.currentThread() )
					{
						Thread.currentThread().wait( 250 );
					}
					System.out.println( "Waiting For Agent..." );
				}
			}
			
			track.setAgent( clss.newInstance() );
			track.getAgent().setHname( clss.getName() );

			File sound = generateSound();

			ProgramDirector.initUI();
			TestPlayer2 MyComp = new TestPlayer2();
			ProgramDirector.showComponent(MyComp, "SndHnd", argv, false);

			outputChoice = OutputChoiceInterface.show(sound, MyComp.undoMgr);
			
			PaletteClassesDump.addListener( new Runnable()
			{
				public void run()
				{
					randWave = null;
				}
			} );

		} catch (Throwable qte) {
			qte.printStackTrace(System.out);
		}
	}

	
}

