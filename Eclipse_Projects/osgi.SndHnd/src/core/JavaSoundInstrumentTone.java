




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



import java.awt.EventQueue;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.*;
import javax.sound.midi.*;


/**
 * User interface for changing the tone of the MIDI synthesizer.
 * 
 * @author tgreen
 *
 */
public class JavaSoundInstrumentTone {
	
	/**
	 * Displays a user interface for changing the tone of the MIDI synthesizer.
	 */
	 public void display() {
		 
		 try
		 {
		 
		 	Synthesizer s = MidiSystem.getSynthesizer();
			s.open();
			System.out.println( s );
			MidiChannel[] channels = s.getChannels();
			
			Instrument[] i = s.getAvailableInstruments();
			
			final int len = i.length;
			
			final String[] items = new String[ len ];
			
			int cnt;
			for( cnt = 0 ; cnt< len ; cnt++ )
			{
				items[ cnt ] = i[ cnt ].getName();
			}
	        
	        JList list = new JList(items);
	        
	        JScrollPane jsp = new JScrollPane( list );
	        
	        JPanel panel = new JPanel();
	        panel.add( jsp );
	        JOptionPane.showMessageDialog(null, panel);
	        
	        final int index = list.getSelectedIndex();
	        
	        if( index >= 0 )
	        {
	        	Instrument ii = i[ index ];
	        	NoteChannelEmulator.setInstrument( ii );
	        }
	        
		 }
		 catch( Throwable ex )
		 {
			 ex.printStackTrace( System.out );
		 }
	    }

	 	/**
	 	 * Test driver.
	 	 * @param args Input arguments not used.
	 	 */
	    public static void main(String[] args) {
	        EventQueue.invokeLater(new Runnable() {

	        	/**
	        	 * Runs the test driver.
	        	 */
	            public void run() {
	                new JavaSoundInstrumentTone().display();
	            }
	        });
	    }

}

