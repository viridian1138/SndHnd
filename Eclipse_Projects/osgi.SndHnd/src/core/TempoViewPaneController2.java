




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

public class TempoViewPaneController2 extends JPanel implements ActionListener {
	
	protected JMenuItem zoom = new JMenuItem( "Zoom" );
	
	protected JMenuItem zoomX = new JMenuItem( "ZoomX" );
	
	protected JMenuItem zoomY = new JMenuItem( "ZoomY" );
	
	protected JMenuItem unZoom = new JMenuItem( "Unzoom" );
	
	protected JMenuItem unZoom2X = new JMenuItem( "Unzoom2X" );
	
	protected JMenuItem seq = new JMenuItem( "Sequester To Window" );
	
	protected JMenuItem insertTempoInterpPointBefore = new JMenuItem( "Insert Tempo Interp Point Before" );
	
	protected JMenuItem insertPasteBufferTempoInterpPointBefore = new JMenuItem( "Insert Paste Buffer Tempo Interp Point Before" );
	
	protected JMenuItem insertTempoInterpPointAfter = new JMenuItem( "Insert Tempo Interp Point After" );
	
	protected JMenuItem insertPasteBufferTempoInterpPointAfter = new JMenuItem( "Insert Paste Buffer Tempo Interp Point After" );
	
	protected JMenuItem deleteTempoInterpPoint = new JMenuItem( "Delete Tempo Interp Point" );
	
	protected JMenuItem editTempoInterpPosn = new JMenuItem( "Edit Tempo Interp Posn" );
	
	protected JMenuItem editTempoInterpLevel = new JMenuItem( "Edit Tempo Interp Level" );
	
	protected JMenuItem editInitializeToPasteBuffer = new JMenuItem( "Initialize Tempo To Paste Buffer" );
	
	protected JMenuItem selTempoLevel = new JMenuItem( "Select Tempo Level" );
	
	
	
	
	JMenuItem tempoButton = new JMenuItem("Set Paste Buffer Tempo from Tap Pad");
	
	JMenuItem interpTempo = new JMenuItem("Interpolate Tempo from Tap Pad...");
	
	JMenuItem interpStat = new JMenuItem("Interpolate Tempo from Statistics...");
	
	JMenuItem insertFermata = new JMenuItem("Insert Fermata...");
	
	JMenuItem insertAccelerando = new JMenuItem("Insert Accelerando...");
	
	JMenuItem insertRitardando = new JMenuItem("Insert Ritardando...");
	
	JMenuItem undoTempo = new JMenuItem("Undo Tempo");
	
	
	
	/**
	 * Menu item for swinging the beats in measures.
	 */
	JMenuItem swingBeatsInMeasures = new JMenuItem( "Swing Beats In Measures..." );
	
	
	
	
	
	/**
	 * The tempo view pane to edit.
	 */
	protected TempoViewPane2 pane;

	/**
	 * Constructor.
	 * @param _pane The tempo view pane to edit.
	 */
	public TempoViewPaneController2( TempoViewPane2 _pane ) {
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
		menu.add(insertTempoInterpPointBefore);
		menu.add(insertPasteBufferTempoInterpPointBefore);
		menu.add(insertTempoInterpPointAfter);
		menu.add(insertPasteBufferTempoInterpPointAfter);
		menu.add(deleteTempoInterpPoint);
		menu.add(editTempoInterpPosn);
		menu.add(editTempoInterpLevel);
		menu.add(editInitializeToPasteBuffer);
		menu.add(selTempoLevel);
		
		
		
		menu = new JMenu( "Tap" );
		mbar.add( menu );
		
		
		menu.add( tempoButton );
		menu.add( interpTempo );
		menu.add( interpStat );
		menu.add( insertFermata );
		menu.add( insertAccelerando );
		menu.add( insertRitardando );
		menu.add( undoTempo );
		
		
		
		menu = new JMenu( "Swing" );
		mbar.add( menu );
		
		
		menu.add( swingBeatsInMeasures );
		
		


		
		zoom.addActionListener(this);
		zoomX.addActionListener(this);
		zoomY.addActionListener(this);
		unZoom.addActionListener(this);
		unZoom2X.addActionListener(this);
		seq.addActionListener(this);
		insertTempoInterpPointBefore.addActionListener(this);
		insertPasteBufferTempoInterpPointBefore.addActionListener(this);
		insertTempoInterpPointAfter.addActionListener(this);
		insertPasteBufferTempoInterpPointAfter.addActionListener(this);
		deleteTempoInterpPoint.addActionListener(this);
		editTempoInterpPosn.addActionListener(this);
		editTempoInterpLevel.addActionListener(this);
		editInitializeToPasteBuffer.addActionListener(this);
		selTempoLevel.addActionListener(this);
		
		
		
		
		tempoButton.addActionListener( this );
		interpTempo.addActionListener( this );
		interpStat.addActionListener( this );
		insertFermata.addActionListener( this );
		insertAccelerando.addActionListener( this );
		insertRitardando.addActionListener( this );
		undoTempo.addActionListener( this );
		
		
		
		swingBeatsInMeasures.addActionListener( this );
		
	}
	
	/**
	 * Handles a button press by executing the appropriate command.
	 */
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
		
		if( e.getSource() == insertTempoInterpPointBefore )
		{
			pane.insertTempoInterpPointBefore();
		}
		
		if( e.getSource() == insertPasteBufferTempoInterpPointBefore )
		{
			pane.insertPasteBufferTempoInterpPointBefore();
		}
		
		if( e.getSource() == insertTempoInterpPointAfter )
		{
			pane.insertTempoInterpPointAfter();
		}
		
		if( e.getSource() == insertPasteBufferTempoInterpPointAfter )
		{
			pane.insertPasteBufferTempoInterpPointAfter();
		}
		
		if( e.getSource() == deleteTempoInterpPoint )
		{
			pane.deleteTempoInterpPoint();
		}
		
		if( e.getSource() == editTempoInterpPosn )
		{
			pane.editTempoInterpPosn();
		}
		
		if( e.getSource() == editTempoInterpLevel )
		{
			pane.editTempoInterpLevel();
		}
		
		if( e.getSource() == editInitializeToPasteBuffer )
		{
			pane.initializeTempoToPasteBuffer();
		}
		
		if( e.getSource() == selTempoLevel )
		{
			pane.selectTempo();
		}
		
		
		
		
		
		if( e.getSource() == tempoButton )
		{
			pane.calculateTempo();
		}
		
		if( e.getSource() == interpTempo )
		{
			pane.interpTempo();
		}
		
		if( e.getSource() == interpStat )
		{
			pane.interpStat();
		}
		
		if( e.getSource() == insertFermata )
		{
			pane.insertFermata();
		}
		
		if( e.getSource() == insertAccelerando )
		{
			pane.insertAccelerando();
		}
		
		if( e.getSource() == insertRitardando )
		{
			pane.insertRitardando();
		}
		
		if( e.getSource() == undoTempo )
		{
			pane.undoTempo();
		}
		
		
		
		
		
		if( e.getSource() == swingBeatsInMeasures )
		{
			pane.swingBeatsInMeasures();
		}
		
		
		
		
	}

}

