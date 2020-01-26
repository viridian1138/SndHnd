




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

public class VibratoViewPaneController2 extends JPanel implements ActionListener {
	
	protected JMenuItem zoom = new JMenuItem( "Zoom" );
	
	protected JMenuItem zoomX = new JMenuItem( "ZoomX" );
	
	protected JMenuItem zoomY = new JMenuItem( "ZoomY" );
	
	protected JMenuItem unZoom = new JMenuItem( "Unzoom" );
	
	protected JMenuItem unZoom2X = new JMenuItem( "Unzoom2X" );
	
	protected JMenuItem seq = new JMenuItem( "Sequester To Window" );
	
	protected JMenuItem insertVibratoInterpPointBefore = new JMenuItem( "Insert Vibrato Interp Point Before" );
	
	protected JMenuItem insertPasteBufferVibratoInterpPointBefore = new JMenuItem( "Insert Paste Buffer Vibrato Interp Point Before" );
	
	protected JMenuItem insertVibratoInterpPointAfter = new JMenuItem( "Insert Vibrato Interp Point After" );
	
	protected JMenuItem insertPasteBufferVibratoInterpPointAfter = new JMenuItem( "Insert Paste Buffer Vibrato Interp Point After" );
	
	protected JMenuItem deleteVibratoInterpPoint = new JMenuItem( "Delete Vibrato Interp Point" );
	
	protected JMenuItem editVibratoInterpPosn = new JMenuItem( "Edit Vibrato Interp Posn" );
	
	protected JMenuItem editVibratoInterpLevel = new JMenuItem( "Edit Vibrato Interp Level" );
	
	protected JMenuItem editInitializeToPasteBuffer = new JMenuItem( "Initialize Vibrato To Paste Buffer" );
	
	protected JMenuItem selVibratoLevel = new JMenuItem( "Select Vibrato Level" );
	
	
	
	
	
	protected VibratoViewPane2 pane;

	public VibratoViewPaneController2( VibratoViewPane2 _pane ) {
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
		menu.add(seq);
		menu.add(insertVibratoInterpPointBefore);
		menu.add(insertPasteBufferVibratoInterpPointBefore);
		menu.add(insertVibratoInterpPointAfter);
		menu.add(insertPasteBufferVibratoInterpPointAfter);
		menu.add(deleteVibratoInterpPoint);
		menu.add(editVibratoInterpPosn);
		menu.add(editVibratoInterpLevel);
		menu.add(editInitializeToPasteBuffer);
		menu.add(selVibratoLevel);
		


		zoom.addActionListener(this);
		zoomX.addActionListener(this);
		zoomY.addActionListener(this);
		unZoom.addActionListener(this);
		unZoom2X.addActionListener(this);
		seq.addActionListener(this);
		insertVibratoInterpPointBefore.addActionListener(this);
		insertPasteBufferVibratoInterpPointBefore.addActionListener(this);
		insertVibratoInterpPointAfter.addActionListener(this);
		insertPasteBufferVibratoInterpPointAfter.addActionListener(this);
		deleteVibratoInterpPoint.addActionListener(this);
		editVibratoInterpPosn.addActionListener(this);
		editVibratoInterpLevel.addActionListener(this);
		editInitializeToPasteBuffer.addActionListener(this);
		selVibratoLevel.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		
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
		
		if( e.getSource() == seq )
		{
			pane.sequesterToWindow();
		}
		
		if( e.getSource() == insertVibratoInterpPointBefore )
		{
			pane.insertVibratoInterpPointBefore();
		}
		
		if( e.getSource() == insertPasteBufferVibratoInterpPointBefore )
		{
			pane.insertPasteBufferVibratoInterpPointBefore();
		}
		
		if( e.getSource() == insertVibratoInterpPointAfter )
		{
			pane.insertVibratoInterpPointAfter();
		}
		
		if( e.getSource() == insertPasteBufferVibratoInterpPointAfter )
		{
			pane.insertPasteBufferVibratoInterpPointAfter();
		}
		
		if( e.getSource() == deleteVibratoInterpPoint )
		{
			pane.deleteVibratoInterpPoint();
		}
		
		if( e.getSource() == editVibratoInterpPosn )
		{
			pane.editVibratoInterpPosn();
		}
		
		if( e.getSource() == editVibratoInterpLevel )
		{
			pane.editVibratoInterpLevel();
		}
		
		if( e.getSource() == editInitializeToPasteBuffer )
		{
			pane.initializeVibratoToPasteBuffer();
		}
		
		if( e.getSource() == selVibratoLevel )
		{
			pane.selectVibrato();
		}
		
		
	}

}

