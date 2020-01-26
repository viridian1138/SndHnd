




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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class WaveViewPaneController extends JPanel implements ActionListener {
	
	protected JMenuItem zoom = new JMenuItem( "Zoom" );
	
	protected JMenuItem zoomX = new JMenuItem( "ZoomX" );
	
	protected JMenuItem zoomY = new JMenuItem( "ZoomY" );
	
	protected JMenuItem unZoom = new JMenuItem( "Unzoom" );
	
	protected JMenuItem unZoom40 = new JMenuItem( "Unzoom40" );
	
	protected JMenuItem unZoom2X = new JMenuItem( "Unzoom2X" );
	
	protected JMenuItem unZoomLimits = new JMenuItem( "Unzoom To Playback Limits" );
	
	
	
	
	
	protected WaveViewPane pane;

	public WaveViewPaneController( WaveViewPane _pane ) {
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
		menu.add(unZoom40);
		menu.add(unZoom2X);
		menu.add(unZoomLimits);
		


		zoom.addActionListener(this);
		zoomX.addActionListener(this);
		zoomY.addActionListener(this);
		unZoom.addActionListener(this);
		unZoom40.addActionListener(this);
		unZoom2X.addActionListener(this);
		unZoomLimits.addActionListener(this);
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
		
		if( e.getSource() == unZoom40 )
		{
			pane.unZoom40();
		}
		
		if( e.getSource() == unZoom2X )
		{
			pane.unZoom2X();
		}
		
		if( e.getSource() == unZoomLimits )
		{
			pane.unZoomLimits();
		}
		
		
	}

}

