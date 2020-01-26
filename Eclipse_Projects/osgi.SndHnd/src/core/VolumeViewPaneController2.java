




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


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.awt.*;

public class VolumeViewPaneController2 extends JPanel implements ActionListener {
	
	protected JMenuItem zoom = new JMenuItem( "Zoom" );
	
	protected JMenuItem zoomX = new JMenuItem( "ZoomX" );
	
	protected JMenuItem zoomY = new JMenuItem( "ZoomY" );
	
	protected JMenuItem unZoom = new JMenuItem( "Unzoom" );
	
	protected JMenuItem unZoom2X = new JMenuItem( "Unzoom2X" );
	
	protected JMenuItem unZoomLimits = new JMenuItem( "Unzoom To Playback Limits" );
	
	protected JMenuItem seq = new JMenuItem( "Sequester To Window" );
	
	protected JMenuItem insertTrackInterpPointBefore = new JMenuItem( "Insert Track Interp Point Before" );
	
	protected JMenuItem insertNoteInterpPointBefore = new JMenuItem( "Insert Note Interp Point Before" );
	
	protected JMenuItem insertTrackInterpPointAfter = new JMenuItem( "Insert Track Interp Point After" );
	
	protected JMenuItem insertNoteInterpPointAfter = new JMenuItem( "Insert Note Interp Point After" );
	
	protected JMenuItem deleteTrackInterpPoint = new JMenuItem( "Delete Track Interp Point" );
	
	protected JMenuItem deleteNoteInterpPoint = new JMenuItem( "Delete Note Interp Point" );
	
	protected JMenuItem editTrackInterpPosn = new JMenuItem( "Edit Track Interp Posn" );
	
	protected JMenuItem editNoteInterpPosn = new JMenuItem( "Edit Note Interp Posn" );
	
	protected JMenuItem editTrackInterpLevel = new JMenuItem( "Edit Track Interp Level" );
	
	protected JMenuItem editNoteInterpLevel = new JMenuItem( "Edit Note Interp Level" );
	
	protected JMenuItem selNoteLevel = new JMenuItem( "Select Note Level" );
	
	protected JMenuItem selTrackLevel = new JMenuItem( "Select Track Level" );
	
	protected JMenuItem dumpNoteProperties = new JMenuItem( "Dump Note Properties..." );
	
	
	
	
	
	protected VolumeViewPane2 pane;

	public VolumeViewPaneController2( VolumeViewPane2 _pane ) {
		super();
		pane = _pane;
		
		JMenuBar mbar = new JMenuBar();
		
		setLayout(new BorderLayout(0,0));
		add( BorderLayout.NORTH , mbar );
		
		JMenu menu = new JMenu( "Edit" );
		mbar.add( menu );
		
		
		menu.add(zoom);
		menu.add(zoomX);
		menu.add(zoomY);
		menu.add(unZoom);
		menu.add(unZoom2X);
		menu.add(unZoomLimits);
		menu.add(seq);
		menu.add(insertTrackInterpPointBefore);
		menu.add(insertNoteInterpPointBefore);
		menu.add(insertTrackInterpPointAfter);
		menu.add(insertNoteInterpPointAfter);
		menu.add(deleteTrackInterpPoint);
		menu.add(deleteNoteInterpPoint);
		menu.add(editTrackInterpPosn);
		menu.add(editNoteInterpPosn);
		menu.add(editTrackInterpLevel);
		menu.add(editNoteInterpLevel);
		menu.add(selNoteLevel);
		menu.add(selTrackLevel);
		menu.add(dumpNoteProperties);
		


		zoom.addActionListener(this);
		zoomX.addActionListener(this);
		zoomY.addActionListener(this);
		unZoom.addActionListener(this);
		unZoom2X.addActionListener(this);
		unZoomLimits.addActionListener(this);
		seq.addActionListener(this);
		insertTrackInterpPointBefore.addActionListener(this);
		insertNoteInterpPointBefore.addActionListener(this);
		insertTrackInterpPointAfter.addActionListener(this);
		insertNoteInterpPointAfter.addActionListener(this);
		deleteTrackInterpPoint.addActionListener(this);
		deleteNoteInterpPoint.addActionListener(this);
		editTrackInterpPosn.addActionListener(this);
		editNoteInterpPosn.addActionListener(this);
		editTrackInterpLevel.addActionListener(this);
		editNoteInterpLevel.addActionListener(this);
		selNoteLevel.addActionListener(this);
		selTrackLevel.addActionListener(this);
		dumpNoteProperties.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		try {
		
		if( e.getSource() == zoom )
		{
			pane.zoom();
		}
		
		if( e.getSource() == zoomX )
		{
			pane.zoomX();
		}
		
		if( e.getSource() == zoomY )
		{
			pane.zoomY();
		}
		
		if( e.getSource() == unZoom )
		{
			pane.unZoom();
		}
		
		if( e.getSource() == unZoom2X )
		{
			pane.unZoom2X();
		}
		
		if( e.getSource() == unZoomLimits )
		{
			pane.unZoomLimits();
		}
		
		if( e.getSource() == seq )
		{
			pane.sequesterToWindow();
		}
		
		if( e.getSource() == insertTrackInterpPointBefore )
		{
			pane.insertTrackInterpPointBefore();
		}
		
		if( e.getSource() == insertNoteInterpPointBefore )
		{
			pane.insertNoteInterpPointBefore();
		}
		
		if( e.getSource() == insertTrackInterpPointAfter )
		{
			pane.insertTrackInterpPointAfter();
		}
		
		if( e.getSource() == insertNoteInterpPointAfter )
		{
			pane.insertNoteInterpPointAfter();
		}
		
		if( e.getSource() == deleteTrackInterpPoint )
		{
			pane.deleteTrackInterpPoint();
		}
		
		if( e.getSource() == deleteNoteInterpPoint )
		{
			pane.deleteNoteInterpPoint();
		}
		
		if( e.getSource() == editTrackInterpPosn )
		{
			pane.editTrackInterpPosn();
		}
		
		if( e.getSource() == editNoteInterpPosn )
		{
			pane.editNoteInterpPosn();
		}
		
		if( e.getSource() == editTrackInterpLevel )
		{
			pane.editTrackInterpLevel();
		}
		
		if( e.getSource() == editNoteInterpLevel )
		{
			pane.editNoteInterpLevel();
		}
		
		if( e.getSource() == selNoteLevel )
		{
			pane.selectNoteLevel();
		}
		
		if( e.getSource() == selTrackLevel )
		{
			pane.selectTrackLevel();
		}
		
		if( e.getSource() == dumpNoteProperties )
		{
			pane.dumpNoteProperties();
		}
		
		
		} catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
		
		
	}

}

