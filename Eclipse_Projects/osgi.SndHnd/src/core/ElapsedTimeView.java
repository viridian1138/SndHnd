




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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.*;

import aazon.AazonEnt;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartIntSwitch;
import aazon.dbl.AazonBaseImmutableDbl;
import aazon.intg.AazonBaseMutableInt;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonMutableSubtractVect;
import aazon.vect.AazonVect;
import abzon.AbzonImmutableGeneralPathFactory;
import abzon.AbzonImmutableShape;
import aczon.AczonColor;
import aczon.AczonRootFactory;
import bezier.BezierQuarticNonClampedCoefficientSlopingMultiCore;
import bezier.CubicBezierCurve;
import bezier.PiecewiseQuarticBezierSlopingMultiCore;

/**
 * Displays a label showing the current elapsed time position of the sound being played.
 * 
 * @author thorngreen
 * 
 */
public class ElapsedTimeView {

	/**
	 * The sound player.
	 */
	SoundPlayer soundPlayer;

	/**
	 * Thread synchronization instance.
	 */
	final Integer syncObj = new Integer( -1 );

	/**
	 * Whether the thread for updating the label is running.
	 */
	volatile boolean runThread = true;
	
	/**
	 * Test field showing the elapsed time at which the soundPlayer is positioned.
	 */
	JLabel time = new JLabel("0");
	
	

	/**
	 * Constructor.
	 */
	public ElapsedTimeView() {
		super();
	}
	
	/**
	 * Constructs a panel showing the "time" label.
	 * @return A panel showing the "time" label.
	 */
	public JPanel constructPanel() {
		try {
			
			final JPanel pn = new JPanel();
			
			pn.setLayout( new BorderLayout( 0 , 0 ) );
			
			pn.add( BorderLayout.CENTER , time );
			
			pn.setMinimumSize( new Dimension( 400 , 100 ) );
			
			pn.setPreferredSize( new Dimension( 400 , 100 ) );
			
			return( pn );
			
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
			throw( new RuntimeException( "Failed." ) );
		}
	}
	
	/**
	 * Handles the showing of the elapsed time view.
	 * @param _soundPlayer The input sound player.
	 */
	public void handleShow(SoundPlayer _soundPlayer)
	{
		try
		{
		startThread();
		setSoundPlayer(_soundPlayer);
	} catch (Throwable ex) {
		ex.printStackTrace(System.out);
		throw( new RuntimeException( "Failed." ) );
	}
	}

	/**
	 * Sets the sound player for which to display the elapsed time.
	 * @param _soundPlayer The input sound player.
	 */
	public void setSoundPlayer(SoundPlayer _soundPlayer) {
		synchronized( syncObj ) {
		soundPlayer = _soundPlayer;
		final long pos = soundPlayer.getMicrosecondPosition();
		final long apos = pos / 100000;
		final String st = "" + ( apos / 10 ) + "." + ( apos % 10 );
		time.setText( st );
	}
	}
	
	

	/**
	 * Starts a thread that updates the elapsed time in the "time" member.
	 */
	private void startThread() {
		final Runnable runn = new Runnable() {
			/**
			 * Updates the elapsed time in the "time" member.
			 */
			public void run() {
				try {
					final int core = 0;
					while (runThread) {
						synchronized( syncObj )
						{
							final long pos = soundPlayer.getMicrosecondPosition();
							final long apos = pos / 100000;
							final String st = "" + ( apos / 10 ) + "." + ( apos % 10 );
							time.setText( st );
								
						}
						Thread.sleep( 200 );
					}
				} catch (Throwable ex) {
					ex.printStackTrace(System.out);
				}
			}
		};

		final Thread th = new Thread(runn);
		th.start();
	}

}

