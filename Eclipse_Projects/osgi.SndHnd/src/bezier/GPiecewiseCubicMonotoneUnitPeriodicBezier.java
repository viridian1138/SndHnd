




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







package bezier;

import gredit.GNode;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import javax.swing.JFrame;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import verdantium.undo.UTag;
import verdantium.undo.UndoManager;
import core.InstrumentTrack;
import core.InterpolationPoint;
import core.WaveForm;

/**
 * Node representing a piecewise cubic Bezier curve for a wave that is periodic across the unit domain, and uses Fritsch-Carlson monotonicity constraints.
 * 
 * @author tgreen
 *
 */
public class GPiecewiseCubicMonotoneUnitPeriodicBezier extends GNode implements Externalizable {
	
	/**
	 * Piecewise cubic Bezier curve for a wave that is periodic across the unit domain, and uses Fritsch-Carlson monotonicity constraints.
	 */
	PiecewiseCubicMonotoneUnitPeriodicBezier bez;
	
	/**
	 * View pane for editing the Bezier curve.
	 */
	transient PiecewiseCubicMonotoneUnitPeriodicBezierViewPane piecewiseCubicMonotoneUnitPeriodicBezierViewPane = null;

	
	/**
	 * Constructs the node.
	 */
	public GPiecewiseCubicMonotoneUnitPeriodicBezier()
	{
		bez = new PiecewiseCubicMonotoneUnitPeriodicBezier();
		bez.getInterpolationPoints().clear();
		InterpolationPoint pt0 = new InterpolationPoint( 0.25 , 1.0 );
		InterpolationPoint pt1 = new InterpolationPoint( 0.5 , 0.0 );
		InterpolationPoint pt2 = new InterpolationPoint( 0.75 , -1.0 );
		bez.getInterpolationPoints().add( pt0 );
		bez.getInterpolationPoints().add( pt1 );
		bez.getInterpolationPoints().add( pt2 );
		bez.updateAll();
	}
	
	/**
	 * Generates the corresponding PiecewiseCubicMonotoneUnitPeriodicBezier for this curve.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding PiecewiseCubicMonotoneUnitPeriodicBezier for this curve.
	 */
	public PiecewiseCubicMonotoneUnitPeriodicBezier genBez( HashMap s )
	{
		return( bez );
	}

	@Override
	public Object genObj(HashMap s) {
		return( genBez( s ) );
	}

	public Object getChldNodes() {
		return( null );
	}

	@Override
	public String getName() {
		return( "PiecewiseCubicMonotoneUnitPeriodicBezier" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return false;
	}

	@Override
	public void performAssign(GNode in) {
		throw( new RuntimeException( "NotSupported" ) );
	}

	@Override
	public void removeChld() {
	}
	
	/**
	 * Loads new values into the node.
	 * @param in Piecewise cubic Bezier curve for a wave that is periodic across the unit domain, and uses Fritsch-Carlson monotonicity constraints.
	 */
	public void load( PiecewiseCubicMonotoneUnitPeriodicBezier in )
	{
		bez = in;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context ) throws Throwable
	{
		if (piecewiseCubicMonotoneUnitPeriodicBezierViewPane == null) {
			UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
			InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
			if( ( undoMgr == null ) || ( track == null ) )
			{
				throw( new RuntimeException( "UndoMgrIsNull" ) );
			}
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			piecewiseCubicMonotoneUnitPeriodicBezierViewPane = new PiecewiseCubicMonotoneUnitPeriodicBezierViewPane( undoMgr , context , track , bez );
			PiecewiseCubicMonotoneUnitPeriodicBezierViewPaneController controller = new PiecewiseCubicMonotoneUnitPeriodicBezierViewPaneController(
					piecewiseCubicMonotoneUnitPeriodicBezierViewPane);
			piecewiseCubicMonotoneUnitPeriodicBezierViewPane.setStartLevel(-40.0);
			piecewiseCubicMonotoneUnitPeriodicBezierViewPane.setEndLevel(40.0);
			piecewiseCubicMonotoneUnitPeriodicBezierViewPane.setStartParamNumber( - 0.2 );
			piecewiseCubicMonotoneUnitPeriodicBezierViewPane.setEndParamNumber( 1.2 );
			piecewiseCubicMonotoneUnitPeriodicBezierViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create piecewiseCubicMonotoneUnitPeriodicBezierViewPane" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, piecewiseCubicMonotoneUnitPeriodicBezierViewPane);
			fr.getContentPane().add(BorderLayout.SOUTH, controller);
			fr.pack();
			fr.show();
			
			fr.addWindowListener( new WindowAdapter()
			{
				boolean closed = false;
				
				@Override
				public void windowClosing( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						piecewiseCubicMonotoneUnitPeriodicBezierViewPane = null;
					}
				}
				
				@Override
				public void windowClosed( WindowEvent e )
				{
					if( !closed )
					{
						fr.setVisible( false );
						fr.dispose();
						closed = true;
						piecewiseCubicMonotoneUnitPeriodicBezierViewPane = null;
					}
				}
			} );
		}
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( bez != null ) myv.setProperty("Bez", bez);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (PiecewiseCubicMonotoneUnitPeriodicBezier)( myv.getProperty("Bez") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

