




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


import java.io.File;
import java.math.BigDecimal;


/**
 * This class is no longer in use.  Candidate for deletion.
 * 
 * @author tgreen
 *
 */
public class TestPlayer {
	
	public static final int BYTES_PER_SAMPLE = 2;
	
	public static final int BITS_PER_SAMPLE = 8 * BYTES_PER_SAMPLE;

	public static final int SAMPLING = 44100;

	static final byte[] ONE_SECOND_SAMPLE = new byte[SAMPLING * BYTES_PER_SAMPLE];

	public static final int FREQUENCY = 440;
	
	public static int maxTime;
	
	public static double calcEnvelope( double[] envelope , double u )
	{
		double b0 = envelope[ 0 ];
		double b1 = envelope[ 1 ];
		double b2 = envelope[ 2 ];
		double b3 = envelope[ 3 ];
		double b10 = (1-u)*b0 + u*b1;
		double b11 = (1-u)*b1 + u*b2;
		double b12 = (1-u)*b2 + u*b3;
		double b20 = (1-u)*b10 + u*b11;
		double b21 = (1-u)*b11 + u*b12;
		double b30 = (1-u)*b20 + u*b21;
		return( b30 );
	}

	public static byte[] buildOneSecondSample(int inTime) {
		final int len = ONE_SECOND_SAMPLE.length;
		double[] envelopeRgn = { 0.0 , 1.4 , 0.0 , 0.0 };
		int wavelengthInSamples = SAMPLING / FREQUENCY;
		int sample = inTime * SAMPLING;
		double twoPi = 2 * Math.PI;
		double radiansPerSample = twoPi / wavelengthInSamples;
		// BigDecimal dec = new BigDecimal( 0.3 );

		int i;
		for (i = 0; i < len; i += BYTES_PER_SAMPLE) {
			int index = sample + i / BYTES_PER_SAMPLE;
			double angle = index * radiansPerSample;
			double u = (double) index / maxTime;
			double envelope = calcEnvelope( envelopeRgn , u );
			double sine = envelope * Math.sin(angle);
			double heightD = ( sine + envelope ) * (0x7fff / 2);
			short height = (short) heightD;
			ONE_SECOND_SAMPLE[i + 1] = (byte) ((height & 0xff00) >> 8);  // !!!!!!!!!!!!!!!!!!!!!!!!!! Switched To Little Endian
			ONE_SECOND_SAMPLE[i] = (byte) (height & 0xff); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Switched To Little Endian
		}

		return ( ONE_SECOND_SAMPLE );
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			final int numChannels = 1;
			
			final int numFrames = 5;
			
			maxTime = numFrames * SAMPLING;
			
			PcmContentReader preader = new PcmContentReader()
			{
				public byte[] handleRead( int frameNumber )
				{
					return( buildOneSecondSample( frameNumber ) );
				}
			};
			
		
			
			final WavWriter_32 writer = new WavWriter_32(  new File( "/home/tgreen/out.wav" )  );
			
			writer.setSampleRate( SAMPLING );
    		writer.setBitsPerSample(  BITS_PER_SAMPLE  );
    		writer.setNumberOfChannels(  numChannels  );  
    		
    		
    		writer.start();
    		
    		
    		for( int frameNumber = 0 ; frameNumber < numFrames ; frameNumber++ )
    		{
    			final byte[] bytes = preader.handleRead(frameNumber);
    			writer.write( bytes );
    		}
    		
    		writer.close();
    		

			System.out.println("Done.");

			System.exit(0);
		} catch (Throwable qte) {
			qte.printStackTrace(System.out);
		}
	}

}


