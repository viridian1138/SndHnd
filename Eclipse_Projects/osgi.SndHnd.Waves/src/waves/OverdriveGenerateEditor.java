





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







package waves;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;

import javax.swing.*;

import core.InstrumentTrack;
import core.InterpolationPoint;
import core.SongData;


import bezier.PiecewiseCubicMonotoneBezierFlat;

import java.util.*;

import meta.WrapRuntimeException;
import verdantium.EtherEvent;
import verdantium.EtherEventHandler;
import verdantium.ProgramDirector;
import verdantium.StandardEtherEvent;
import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;

/**
 * A property editor that allows the size of the document page to be changed for
 * a client property editor or component.
 * <P>
 * 
 * @author Thorn Green
 */
public class OverdriveGenerateEditor extends PropertyEditAdapter implements
		ActionListener {

	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();

	protected OverdriveViewPane overdriveEditor = null;
	
	PiecewiseCubicMonotoneBezierFlat crv = null;
	
	InstrumentTrack track;
	
	HashMap context;
	
	protected JCheckBox symmetric = new JCheckBox( "Symmetric" , true );
	
	protected JRadioButton ovdr = new JRadioButton( "Cool / Overdrive / Distort" , true );
	
	protected JRadioButton undr = new JRadioButton( "Warm / Underdrive / Compress" , false );
	
	protected JRadioButton elbow = new JRadioButton( "Elbow Curve" , false );
	
	protected JTextField evalWidth = new JTextField( "1.0" );
	
	protected JTextField numPts = new JTextField( "20" );
	
	protected JButton applyButton = new JButton( "Apply" );
	
	

	/**
	 * Constructs the property editor for a given PageSizeHandler.
	 */
	public OverdriveGenerateEditor(OverdriveViewPane in , 
			PiecewiseCubicMonotoneBezierFlat _crv , InstrumentTrack _track , HashMap _context ) {
		overdriveEditor = in;
		crv = _crv;
		track = _track;
		context = _context;
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));

		pan2.add("any", symmetric);
		pan2.add("any", ovdr);
		pan2.add("any", undr);
		pan2.add("any", elbow);
		pan2.add("any", new JLabel("Eval Width : ") );
		pan2.add("any", evalWidth);
		pan2.add("any", new JLabel("NumPts : "));
		pan2.add("any", numPts);
		pan2.add("any", applyButton);

		applyButton.addActionListener(this);
	}

	/**
	 * Gets the GUI of the property editor.
	 */
	public JComponent getGUI() {
		return (MyPan);
	}

	/**
	 * Handles the destruction of the component by removing appropriate change
	 * listeners.
	 */
	public void handleDestroy() {
	}
	
	
	protected static double sinh( double x )
	{
		return( ( Math.exp( x ) - Math.exp( -x ) ) / 2.0 );
	}
	
	
	protected static double asinh( double z )
	{
		return( Math.log( z + Math.sqrt( z * z + 1 ) ) );
	}
	
	
	protected void normalizeInterpolationPoints( ArrayList<InterpolationPoint> interpPoints , double minVal , double maxVal )
	{
		for( final InterpolationPoint pt : interpPoints )
		{
			double val = pt.getValue();
			double u = ( val - minVal ) / ( maxVal - minVal );
			pt.setValue( ( u - 0.5 ) * 4.0 );
		}
	}

	
	/**
	 * Handles a button-press event from the Apply button by changing the size
	 * of the client page size handler.
	 */
	public void actionPerformed(ActionEvent e) {
		final int core = 0;
		Object src = e.getSource();

		if (src == applyButton) {
			try {
				
				
				
				if( elbow.isSelected() )
				{
					SongData.buildOverdriveCurve( crv );
					crv.updateAll();
					track.updateTrackFrames(core);
					overdriveEditor.refreshDisplayList();
					return;
				}
				
				int numPt = Integer.parseInt( numPts.getText() );
				
				double evalWid = Double.parseDouble( evalWidth.getText() );
				
				
				if( symmetric.isSelected() )
				{
					if( ovdr.isSelected() )
					{
						crv.getInterpolationPoints().clear();
						int count;
						for( count = - ( numPt - 1 ) ; count < numPt ; count++ )
						{
							double param = 2.0 * count / ( numPt - 1 );
							double val = asinh( param * evalWid );
							InterpolationPoint pt = new InterpolationPoint( param , val );
							crv.getInterpolationPoints().add( pt );
						}
						normalizeInterpolationPoints( crv.getInterpolationPoints() , 
								asinh( -evalWid ) , asinh( evalWid ) );
						crv.updateAll();
						track.updateTrackFrames(core);
						overdriveEditor.refreshDisplayList();
						return;
					}
					
					if( undr.isSelected() )
					{
						crv.getInterpolationPoints().clear();
						int count;
						for( count = - ( numPt - 1 ) ; count < numPt ; count++ )
						{
							double param = 2.0 * count / ( numPt - 1 );
							double val = sinh( param * evalWid );
							InterpolationPoint pt = new InterpolationPoint( param , val );
							crv.getInterpolationPoints().add( pt );
						}
						normalizeInterpolationPoints( crv.getInterpolationPoints() , 
								sinh( -evalWid ) , sinh( evalWid ) );
						crv.updateAll();
						track.updateTrackFrames(core);
						overdriveEditor.refreshDisplayList();
						return;
					}
					
				}
				else
				{
					if( ovdr.isSelected() )
					{
						// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						return;
					}
					
					if( undr.isSelected() )
					{
						crv.getInterpolationPoints().clear();
						int count;
						for( count = - ( numPt - 1 ) ; count < numPt ; count++ )
						{
							double param = 2.0 * count / ( numPt - 1 );
							double val = Math.exp( param * evalWid );
							InterpolationPoint pt = new InterpolationPoint( param , val );
							crv.getInterpolationPoints().add( pt );
						}
						normalizeInterpolationPoints( crv.getInterpolationPoints() , 
								Math.exp( -evalWid ) , Math.exp( evalWid ) );
						crv.updateAll();
						track.updateTrackFrames(core);
						overdriveEditor.refreshDisplayList();
						return;
					}
				}
				

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

	}

	/**
	 * Handles the throwing of an error or exception.
	 */
	public void handleThrow(Throwable in) {
		VerdantiumUtils.handleThrow(in, this, null);
	}

}
