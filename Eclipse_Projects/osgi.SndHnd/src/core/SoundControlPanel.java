




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



import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.JOptionPane;



/**
 * Stores the current audio device, and displays a "control panel" dialog allowing the user to select an audio device for use.
 * 
 * @author tgreen
 *
 */
public class SoundControlPanel {
	
	/**
	 * An audio device with one of more lines.
	 */
	protected static Mixer mixer = null;
	
	/**
	 * Displays a "control panel" dialog allowing the user to select an audio device for use.
	 */
	public static void showControlPanel()
	{
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		
		Object[] ob = new Object[ mixerInfo.length ];
		for( int cnt = 0 ; cnt < mixerInfo.length ; cnt++ )
		{
			ob[ cnt ] = " " + ( mixerInfo[ cnt ] );
		}
		Object sel = JOptionPane.showInputDialog(null, "Choose One", "Title", JOptionPane.QUESTION_MESSAGE, null, ob, null);
		for( int cnt = 0 ; cnt < mixerInfo.length ; cnt++ )
		{
			if( sel.equals( " " + ( mixerInfo[ cnt ] ) ) )
			{
				mixer = AudioSystem.getMixer( mixerInfo[ cnt ] );
			}
		}
	}
	
	
	/**
	 * Gets the current audio device.
	 * @return The current audio device having one of more lines.
	 */
	public static Mixer getMixer()
	{
		if( mixer == null )
		{
			showControlPanel();
		}
		return( mixer );
	}

	
}

