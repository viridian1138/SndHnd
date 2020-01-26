




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



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * Class for playing a 32-bit .wav audio file in uncompressed PCM (Pulse-Code Modulation) while showing a simplified progress bar.
 * Also provides a button for starting / stopping the playing of the audio file.
 * 
 * This class was originally written for QTJ (QuickTime for Java) using
 * examples from the book "QuickTime for Java : A Developer's Notebook" by Chris Adamson, 
 * but has since been ported from QTJ to JavaSound.  Porting used geberal JavaSound examples from the web.
 * 
 * @author tgreen
 *
 */
public class SoundPlayer extends JComponent {
	
	/**
	 * The size of the buffer to use when exchanging data from the .wav
	 * file to the audio device.
	 */
	private final int EXTERNAL_BUFFER_SIZE = 768 * 1024;
	
	/**
	 * The audio device to which to play the audio.
	 */
	volatile SourceDataLine auLine;
    
	/**
	 * Whether the sound is currently playing.
	 */
    volatile boolean playing = false;

    /**
     * The length of the audio file in milliseconds.
     */
    int audioLength;
    
    /**
     * The approximate current position within the audio file in milliseconds.
     */
    int audioPosition = 0;
    
    /**
     * The initial position in milliseconds at which playback restarted.
     * The playback position is reset to exactly this time when
     * playback restarts.
     */
    volatile int initialAudioPosition = 0;

    
    /**
     * The Play/Stop button.
     */
    JButton play;
    
    /**
     * Progress bar showing the current position in the audio file.
     */
    LightweightSlider progress;
    
    /**
     * Updates the progress bar every 100 milliseconds.
     * Progress bar updates are intentionally kept somewhat
     * coarse so as to avoid compromising the timing of the
     * audio playback.
     */
    Timer timer;
    
    /**
     * The 32-bit .wav audio file in uncompressed PCM (Pulse-Code Modulation) to be played.
     */
    File soundFile;

    
    /**
     * Test driver that creates a SoundPlayer in a frame and displays it.
     * 
     * @param args Input arguments.  Not used.
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public static void main(String[  ] args) 
        throws IOException,
               UnsupportedAudioFileException,
               LineUnavailableException
    {
        SoundPlayer player;

        File file = new File( "/home/tgreen/BounceFinalA.wav" );   // This is the file we'll be playing

        // Create a SoundPlayer object to play the sound.
        player = new SoundPlayer(file);

        // Put it in a window and play it
        JFrame f = new JFrame("SoundPlayer");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.getContentPane( ).add(player, "Center");
        f.pack( );
        f.setVisible(true);
    }

    
    /**
     * Constructor.
     * @param f The 32-bit .wav audio file in uncompressed PCM (Pulse-Code Modulation) to be played.
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public SoundPlayer(File f)
        throws IOException,
               UnsupportedAudioFileException,
               LineUnavailableException
    {
    	    soundFile = f;
    	    System.out.println( f.getAbsolutePath() );
    	    
            // Getting a Clip object for a file of sampled audio data is kind
            // of cumbersome.  The following lines do what we need.
    	    final WavReader_32 wavReader_32 = new WavReader_32( soundFile );
			
			wavReader_32.start();
			final long totBytesRead = wavReader_32.getSampChunkSize();
    			
    		final float sRate = wavReader_32.getSampleRate();
    		final int sSizeBits = wavReader_32.getBitsPerSample();
    			
    		audioLength = (int)( ( 1000.0 * totBytesRead * 8 ) / ( sRate * sSizeBits ) );
    			
    		wavReader_32.close();
    			
                
            // Get the clip length in microseconds and convert to milliseconds
            
        // Now create the basic GUI
        play = new JButton("Play");                // Play/stop button
        progress = new LightweightSlider(0, audioLength, 0); // Shows position in sound

        // When clicked, start or stop playing the sound
        play.addActionListener(new ActionListener( ) {
        	
        	    /**
        	     * Handles the pressing of the "Play" button by starting or stopping playback.
        	     */
                public void actionPerformed(ActionEvent e) {
                    if (playing) stop( ); else play( );
                }
                
            });

        // Whenever the slider value changes, first update the time label.
        // Next, if we're not already at the new position, skip to it.
        progress.addPropertyChangeListener(new PropertyChangeListener( ) {
        	
        	/**
        	 * Handles a property change by updating the approximate audio position.
        	 */
            public void propertyChange(PropertyChangeEvent e) {
                int value = progress.getValue( );
                    // If we're not already there, skip there.
                    audioPosition = value;
                }
            
            });
        
        // This timer calls the tick( ) method 5 times a second to keep 
        // our slider in sync with the music.
        timer = new javax.swing.Timer(200, new ActionListener( ) {
        	
        		/**
        		 * Handles a timer expiration by updating the progress bar.
        		 */
                public void actionPerformed(ActionEvent e) { tick( ); }
                
            });
        
        // put those controls in a row
        Box row = Box.createHorizontalBox( );
        row.add(play);
        row.add(progress);
        
        // And add them to this component.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(row);
    }
    
    /**
     * Starts playing the sound at the current position.
     */
    public void play( ) {
        
        try
        {
			if( !( soundFile.exists() ) )
			{
				throw( new RuntimeException( "Fail" ) );
			}
			
			final WavReader_32 wavReader_32 = new WavReader_32( soundFile );
			
			wavReader_32.start();
			
			final AudioFormat format = new AudioFormat( 
					wavReader_32.getSampleRate() , wavReader_32.getBitsPerSample() , 
					wavReader_32.getNumberOfChannels() , true /* signed */ , false /* bigEndian */ );
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			
			Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
			
			Mixer mixer = SoundControlPanel.getMixer();
			
			
			// if( str.contains( "plughw:1,0" ) ) // usb output
			// if( str.contains( "plughw:0,3" ) ) // hdmi output
			//if( str.contains( "plughw:0,0" ) )	// speaker output	
			
			
			
			auLine = (SourceDataLine)( mixer.getLine( info ) );
			
			
			
			auLine.open();
			auLine.start();
			
			Thread th = new Thread()
			{
				
				/**
				 * Sends audio data to the audio device as long as the user has not pressed the button to stop or reset.
				 */
				public void run()
				{
					try
					{
						final float sRate = format.getSampleRate();
		    			final int sSizeBits = format.getSampleSizeInBits();
		    			
		    			initialAudioPosition = audioPosition;
		    			wavReader_32.chunkPosition(  (int)( audioPosition * sRate * ( sSizeBits / 8 ) / 1000 ) );
						
						int nBytesRead = 0;
						byte[] abData = new byte[ EXTERNAL_BUFFER_SIZE ];
						
						while( ( nBytesRead !=-1 ) && playing )
						{
							nBytesRead = wavReader_32.read(abData, 0, abData.length);
							if( nBytesRead >= 0 )
							{
								auLine.write(abData, 0, nBytesRead);
							}
						}
						
						auLine.drain();
						auLine.close();
						
					}
					catch( Throwable ex )
					{
						ex.printStackTrace( System.out );
					}
				}
				
			};
			
			th.start();
			
			playing = true;
        }
        catch( Throwable ex )
        {
        	ex.printStackTrace( System.out );
        	throw( new RuntimeException() );
        }
        
        timer.start( );
        play.setText("Stop");
    }

    
    /** 
     * Stops playing the sound, but retains the current position. 
     */
    public void stop( ) {
        timer.stop( );
        play.setText("Play");
        playing = false;
    }

    /** 
     * Stops playing the sound and resets the current position to 0.
     */
    public void reset( ) {
        stop( );
        try
        {
        	Thread.sleep( 2000 );
        }
        catch( Throwable ex )
        {
        	ex.printStackTrace( System.out );
        }
        audioPosition = 0; 
        initialAudioPosition = 0;
        progress.setValue(0);
    }
    
    
    /**
     * Gets the length of the audio to be played in milliseconds.
     * @return The length of the audio to be played in milliseconds.
     */
    public int getLength( ) { return audioLength; }

    
    /**
     * An internal method that updates the progress bar.
     * The Timer object calls it 10 times a second.
     * If the sound has finished, it resets to the beginning
     */
    void tick( ) {
        
        if ( playing ) {
            audioPosition = (int)(getMicrosecondPosition( )/1000);
            progress.setValue(audioPosition);
        }
        else reset( );  
    }


    /**
     * Gets the current time position in the audio file in microseconds.
     * Unlike the "audioPosition" member, this is an exact time.
     * @return The current time position in the audio file in microseconds.
     */
    public long getMicrosecondPosition()
    {
    	SourceDataLine au = auLine;
    	if( au != null )
    	{
    		return( au.getMicrosecondPosition() + ( 1000 * initialAudioPosition ) );
    	}
    	return( 0 );
    }

    
}

