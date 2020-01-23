





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







package noise;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import core.InstrumentTrack;

import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;

import java.awt.*;

public class PwpFractInterpWaveFormEditor extends JPanel implements ActionListener {
	
	protected GPwpFractInterpWaveForm fractInterpWave = null;
	
	protected InstrumentTrack track = null;
	
	protected JMenuItem zoom = new JMenuItem( "Zoom" );
	
	protected JMenuItem zoomX = new JMenuItem( "ZoomX" );
	
	protected JMenuItem zoomY = new JMenuItem( "ZoomY" );
	
	protected JMenuItem unZoom = new JMenuItem( "Unzoom" );
	
	protected JMenuItem unZoom2X = new JMenuItem( "Unzoom2X" );
	
	protected JMenuItem insertInterpPointBefore = new JMenuItem( "Insert Interp Point Before" );
	
	protected JMenuItem insertPasteBufferInterpPointBefore = new JMenuItem( "Insert Paste Buffer Interp Point Before" );
	
	protected JMenuItem insertInterpPointAfter = new JMenuItem( "Insert Interp Point After" );
	
	protected JMenuItem insertPasteBufferInterpPointAfter = new JMenuItem( "Insert Paste Buffer Interp Point After" );
	
	protected JMenuItem deleteInterpPoint = new JMenuItem( "Delete Interp Point" );
	
	protected JMenuItem editInterpPosn = new JMenuItem( "Edit Interp Posn" );
	
	protected JMenuItem editInterpLevel = new JMenuItem( "Edit Interp Level" );
	
	protected JMenuItem editInitializeToPasteBuffer = new JMenuItem( "Initialize To Paste Buffer" );
	
	protected JMenuItem selLevel = new JMenuItem( "Select Level" );
	
	protected JMenuItem editH  = new JMenuItem("Edit H");
	
	protected JMenuItem editd  = new JMenuItem("Edit d");
	
	protected JMenuItem edithh  = new JMenuItem("Edit hh");
	
	protected JMenuItem editl  = new JMenuItem("Edit l");
	
	protected JMenuItem editm  = new JMenuItem("Edit m");
	
	protected JMenuItem generate = new JMenuItem( "Generate..." );
	
	protected JTextField numIterField = new JTextField( "" );
	
	protected JButton applyButton = new JButton("Apply");
	
	
	
	
	
	protected PwpFractInterpViewPane pane;

	public PwpFractInterpWaveFormEditor( GPwpFractInterpWaveForm in , PwpFractInterpViewPane _pane , InstrumentTrack ins2 ) {
		super();
		fractInterpWave = in;
		pane = _pane;
		track = ins2;
		
		JMenuBar mbar = new JMenuBar();
		
		setLayout(new BorderLayout(0,0));
		add( BorderLayout.NORTH , mbar );
		add( BorderLayout.SOUTH , applyButton );
		
		JPanel pan2 = new JPanel();
		add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		
		pan2.add("any", new JLabel( "Num Iter:" ) );
		pan2.add("any", numIterField );
		numIterField.setText( "" + ( fractInterpWave.getIterCnt() ) );
		
		
		JMenu menu = new JMenu( "Edit" );
		mbar.add( menu );
		
		
		menu.add(zoom);
		menu.add(zoomX);
		menu.add(zoomY);
		menu.add(unZoom);
		menu.add(unZoom2X);
		menu.add(insertInterpPointBefore);
		menu.add(insertPasteBufferInterpPointBefore);
		menu.add(insertInterpPointAfter);
		menu.add(insertPasteBufferInterpPointAfter);
		menu.add(deleteInterpPoint);
		menu.add(editInterpPosn);
		menu.add(editInterpLevel);
		menu.add(editInitializeToPasteBuffer);
		menu.add(selLevel);
		menu.add(editH);
		menu.add(editd);
		menu.add(edithh);
		menu.add(editl);
		menu.add(editm);
		menu.add(generate);
		


		applyButton.addActionListener(this);
		zoom.addActionListener(this);
		zoomX.addActionListener(this);
		zoomY.addActionListener(this);
		unZoom.addActionListener(this);
		unZoom2X.addActionListener(this);
		insertInterpPointBefore.addActionListener(this);
		insertPasteBufferInterpPointBefore.addActionListener(this);
		insertInterpPointAfter.addActionListener(this);
		insertPasteBufferInterpPointAfter.addActionListener(this);
		deleteInterpPoint.addActionListener(this);
		editInterpPosn.addActionListener(this);
		editInterpLevel.addActionListener(this);
		editInitializeToPasteBuffer.addActionListener(this);
		selLevel.addActionListener(this);
		editH.addActionListener(this);
		editd.addActionListener(this);
		edithh.addActionListener(this);
		editl.addActionListener(this);
		editm.addActionListener(this);
		generate.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == applyButton) {
			try {
				final int core = 0;
				
				fractInterpWave.setIterCnt( Integer.parseInt( numIterField.getText() ) );
				
				pane.setNumIter( Integer.parseInt( numIterField.getText() ) );

				track.updateTrackFrames( core );

				// EtherEvent send =
				// new PropertyEditEtherEvent(
				// this,
				// PropertyEditEtherEvent.setPageSize,
				// null,
				// MyPage);
				// send.setParameter(new Dimension((int) wid, (int) hei));
				// ProgramDirector.fireEtherEvent(send, null);
			} catch (NumberFormatException ex) {
				handleThrow(new IllegalInputException(
						"Something input was not a number.", ex));
			} catch (Throwable ex) {
				handleThrow(ex);
			}
		}
		
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
		
		if( e.getSource() == insertInterpPointBefore )
		{
			pane.insertInterpPointBefore();
		}
		
		if( e.getSource() == insertPasteBufferInterpPointBefore )
		{
			pane.insertPasteBufferInterpPointBefore();
		}
		
		if( e.getSource() == insertInterpPointAfter )
		{
			pane.insertInterpPointAfter();
		}
		
		if( e.getSource() == insertPasteBufferInterpPointAfter )
		{
			pane.insertPasteBufferInterpPointAfter();
		}
		
		if( e.getSource() == deleteInterpPoint )
		{
			pane.deleteInterpPoint();
		}
		
		if( e.getSource() == editInterpPosn )
		{
			pane.editInterpPosn();
		}
		
		if( e.getSource() == editInterpLevel )
		{
			pane.editInterpLevel();
		}
		
		if( e.getSource() == editInitializeToPasteBuffer )
		{
			pane.initializeToPasteBuffer();
		}
		
		if( e.getSource() == selLevel )
		{
			pane.select();
		}
		
		if( e.getSource() == editH )
		{
			pane.editH();
		}
		
		if( e.getSource() == editd )
		{
			pane.editd();
		}
		
		if( e.getSource() == edithh )
		{
			System.out.println( "Entered edithh" );
			pane.edithh();
		}
		
		if( e.getSource() == editl )
		{
			pane.editl();
		}
		
		if( e.getSource() == editm )
		{
			pane.editm();
		}
		
		if( e.getSource() == generate )
		{
			pane.generate();
		}
		
		
	}
	
	/**
	 * Handles the throwing of an error or exception.
	 */
	public void handleThrow(Throwable in) {
		VerdantiumUtils.handleThrow(in, /*this!!!!!!!!!!!!!!!!!!!!!*/null, null);
	}

}

