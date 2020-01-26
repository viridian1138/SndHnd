




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


import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;



/**
 * Records a live audio segment while a mix is playing similar to how a typical
 * DAW (Digital Audio Workstation) can record audio segments into an existing mix.
 * 
 * This class was originally written for QTJ (QuickTime for Java) using
 * examples from the book "QuickTime for Java : A Developer's Notebook" by Chris Adamson, 
 * but has since been ported from QTJ to JavaSound.
 * 
 * @author tgreen
 *
 */
public class SoundDigitizer extends Frame implements ItemListener , ActionListener {
	
//	Choice deviceChoice;
	
	/**
	 * Button to start recording.
	 */
	Button startButton;
	
	/**
	 * Button to end recording.
	 */
	Button endButton;
	
	
	/**
	 * The sound player that is playing the current mix.
	 */
	SoundPlayer soundPlayer;
	
	/**
	 * The start time in microseconds for the mix where recording started.
	 */
	long startTime;
	
	/**
	 * The end time in microseconds for the mix where recording ended.
	 */
	long endTime;

	/**
	 * Time scale for converting from seconds to microseconds, or vice-versa.
	 */
	final int TIME_SCALE = 1000000;
	
	
//	int startTime2;
	
//	long digiTime2;
	
//	int digiScale2;
	
	
	/**
	 * Whether audio is currently being actively recorded.
	 */
	volatile boolean activelyGrabbing = false;
	
	/**
	 * Reads PCM (Pulse Code Modulation) audio data from the audio device for the live recording.
	 */
	PcmContentReader preader;
	
	
	
	/**
	 * Constructor.
	 * @param _soundPlayer The sound player that is playing the current mix.
	 */
	public SoundDigitizer( SoundPlayer _soundPlayer ) 
	{
		super( "Audio Preview" );
		
		setLayout( new GridLayout(/*3*/2,1));
//		deviceChoice = new Choice();
//		deviceChoice.addItemListener(this);
//		add(deviceChoice);
		
		startButton = new Button( "Start" );
		endButton = new Button( "End" );
		add( startButton );
		add( endButton );
		startButton.addActionListener(this);
		endButton.addActionListener(this);
		
		try
		{
			DigitizerData.setUpAudioGrab( );
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
		
		
		DigitizerData.grabbing = false;
		
		preader = new PcmContentReader()
		{
			/**
			 * Reads a one-second frame of PCM (Pulse Code Modulation) audio data from the audio device for the live recording.
			 * Also initializes the start/end time and other members.
			 * @param frameNumber The number of the one-second frame from which to get the data.
			 * @return The bytes in the one-second frame.
			 * @throws Throwable
			 */
			public byte[] handleRead( int frameNumber ) throws Throwable
			{
				if( activelyGrabbing )
				{
					if( frameNumber == 0 )
					{
						startTime = soundPlayer.getMicrosecondPosition();
					}
					return( DigitizerData.preader.handleRead(frameNumber) );
				}
				else
				{
					endTime = soundPlayer.getMicrosecondPosition();
					double startTimeSeconds = ((double) startTime) / TIME_SCALE;
					double endTimeSeconds = ((double) endTime) / TIME_SCALE;
					System.out.println( " Start/End " );
					System.out.println( startTimeSeconds );
					System.out.println( endTimeSeconds );
					DigitizerData.startTimeSeconds = startTimeSeconds;
					DigitizerData.endTimeSeconds = endTimeSeconds;
					DigitizerData.setUpAudioGrab();
					return( null );
				}
			}
		};
		
		
		DigitizerData.grabbing = true;
		setSoundPlayer( _soundPlayer );
	}
	
	
	/**
	 * Sets the sound player that is playing the current mix.
	 * @param _soundPlayer The sound player that is playing the current mix.
	 */
	public void setSoundPlayer(SoundPlayer _soundPlayer) {
		soundPlayer = _soundPlayer;
	}
	
	
	/**
	 * Ignores change events.
	 */
	public void itemStateChanged( ItemEvent e )
	{
//		try
//		{
//			
//			if( e.getSource() == deviceChoice )
//			{
//				System.out.println( "changed device to " + deviceChoice.getSelectedItem() );
//				DigitizerData.grabbing = false;
//				// grabber.stop();
//				DigitizerData.soundChannel.setDevice( deviceChoice.getSelectedItem() );
//				// also reset inputDriver
//				DigitizerData.inputDriver = DigitizerData.soundChannel.getInputDriver();
//				DigitizerData.inputDriver.setLevelMeterOnOff(true);
//				DigitizerData.grabbing = true;
//			}
//			
//		}
//		catch( QTException qte )
//		{
//			qte.printStackTrace();
//		}
	}

	
	/**
	 * Handles the pressing of the start button by initiating recording.
	 */
	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == startButton )
		{
			activelyGrabbing = true;
			
			Thread th = new Thread()
			{
				/**
				 * Records audio into a 32-bit .wav file.
				 */
				public void run()
				{
					try
					{
						
					final AudioFormat af = DigitizerData.af;
					
					final TargetDataLine line = DigitizerData.line;
					
					final int sampleSize = af.getSampleSizeInBits() / 8;
					
					final int sampleRate = (int)( af.getSampleRate() );
						
					final int secondsElapsed = 25;
					
					final AudioFormat.Encoding encoding = af.getEncoding();
					
					if( ( encoding.equals( AudioFormat.Encoding.ALAW ) ) || ( encoding.equals( AudioFormat.Encoding.ULAW ) ) )
					{
						throw( new RuntimeException( "Fail" ) );
					}
					
					final boolean signed = encoding.equals( AudioFormat.Encoding.PCM_SIGNED );
					
					
					
					final PcmInputStream ti = new PcmInputStream( preader , 
							sampleRate , sampleSize , af.getChannels() , signed , af.isBigEndian() , secondsElapsed );
					
					final AudioInputStream stream = new AudioInputStream( ti , ti.getAudioFormat() , ti.getStreamSize() );
					
					
					System.out.println( "Starting..." );
					
					
					line.open( af );
					line.start();
					
					
					AudioSystem.write( stream , 
							AudioFileFormat.Type.WAVE , DigitizerData.getDigiFile() );
					
					
					line.stop();
					line.close();
					
					
					System.out.println( "Done" );
					}
					catch( Throwable ex )
					{
						ex.printStackTrace( System.out );
					}
				}
			};
			
			th.start();
		}
		
		if( e.getSource() == endButton )
		{
			activelyGrabbing = false;
		}
	}
	

}

