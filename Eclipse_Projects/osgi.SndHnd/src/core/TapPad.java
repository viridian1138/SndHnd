




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

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;



/**
 * Simple software instrument that stores the times the user has "tapped" the instrument by clicking on it.
 * 
 * @author thorngreen
 * 
 */
public class TapPad extends Canvas implements MouseListener {

	/**
	 * The sound player from which to get the current time.
	 */
	SoundPlayer soundPlayer;

	/**
	 * Stores the last time at which the mouse was pressed as a microsecond position.
	 */
	long startTime;

	/**
	 * The time intervals of the input mouse clicks.
	 */
	ArrayList<TimeInterval> timeIntervalArrayList = new ArrayList<TimeInterval>(100);

	/**
	 * Constructor.
	 * @param _soundPlayer The sound player from which to get the current time.
	 */
	public TapPad(SoundPlayer _soundPlayer) {
		super();
		try {
			setSoundPlayer(_soundPlayer);
			addMouseListener(this);
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Sets the sound player from which to get the current time.
	 * @param _soundPlayer The sound player from which to get the current time.
	 */
	public void setSoundPlayer(SoundPlayer _soundPlayer) {
		soundPlayer = _soundPlayer;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// Does Nothing

	}

	/**
	 * Handles a mouse-press by storing the current time.
	 */
	public void mousePressed(MouseEvent arg0) {
		try {
			startTime = soundPlayer.getMicrosecondPosition();
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Handles a mouse-release by storing the time interval of both the mouse-release and the previous mouse-press.
	 */
	public void mouseReleased(MouseEvent arg0) {
		try {
			final double TIME_SCALE = 1000000;
			long endTime = soundPlayer.getMicrosecondPosition();
			double startTimeSeconds = ((double) startTime) / TIME_SCALE;
			double endTimeSeconds = ((double) endTime) / TIME_SCALE;
			timeIntervalArrayList.add( new TimeInterval( startTimeSeconds , endTimeSeconds ) );
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// Does Nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// Does Nothing
	}

	@Override
	public Dimension getMinimumSize() {
		return (new Dimension(200, 200));
	}

	@Override
	public Dimension getPreferredSize() {
		return (new Dimension(200, 200));
	}

	/**
	 * Gets the time intervals of the input mouse clicks.
	 * @return The time intervals of the input mouse clicks.
	 */
	public ArrayList<TimeInterval> getTimeIntervalArrayList() {
		return timeIntervalArrayList;
	}

	
}

