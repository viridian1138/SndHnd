




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

import verdantium.undo.UTag;
import verdantium.undo.UndoManager;

import core.InstrumentTrack;
import core.InterpolationPoint;
import core.SongData;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Node representing a piecewise cubic monotone Bezier curve with a zero slope extending off from each end of the piecewise domain.
 * 
 * @author tgreen
 *
 */
public class GPiecewiseCubicMonotoneBezierFlat extends GNode implements Externalizable {
	
	/**
	 * Piecewise cubic monotone Bezier curve with a zero slope extending off from each end of the piecewise domain.
	 */
	PiecewiseCubicMonotoneBezierFlat bez;
	
	/**
	 * View pane for editing the Bezier curve.
	 */
	transient PiecewiseCubicMonotoneBezierFlatViewPane piecewiseCubicMonotoneBezierFlatViewPane = null;
	
	
	/**
	 * Constructs the node.
	 */
	public GPiecewiseCubicMonotoneBezierFlat()
	{
		bez = new PiecewiseCubicMonotoneBezierFlat();
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
	 * Generates the corresponding PiecewiseCubicMonotoneBezierFlat for this curve.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding PiecewiseCubicMonotoneBezierFlat for this curve.
	 */
	public PiecewiseCubicMonotoneBezierFlat genBez( HashMap s )
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
		return( "PiecewiseCubicMonotoneBezierFlat" );
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
	 * @param in Piecewise cubic Bezier curve with a zero slope extending off from each end of the piecewise domain.
	 */
	public void load( PiecewiseCubicMonotoneBezierFlat in )
	{
		bez = in;
	}

	@Override
	public void editProperties( HashMap<String,Object> context ) throws Throwable
	{
		if (piecewiseCubicMonotoneBezierFlatViewPane == null) {
			UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
			InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
			if( ( undoMgr == null ) || ( track == null ) )
			{
				throw( new RuntimeException( "UndoMgrIsNull" ) );
			}
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			piecewiseCubicMonotoneBezierFlatViewPane = new PiecewiseCubicMonotoneBezierFlatViewPane( undoMgr ,context , track , bez );
			PiecewiseCubicMonotoneBezierFlatViewPaneController controller = new PiecewiseCubicMonotoneBezierFlatViewPaneController(
					piecewiseCubicMonotoneBezierFlatViewPane);
			piecewiseCubicMonotoneBezierFlatViewPane.setStartLevel(-20.0);
			piecewiseCubicMonotoneBezierFlatViewPane.setEndLevel(220.0);
			piecewiseCubicMonotoneBezierFlatViewPane.setStartParamNumber( bez.getFirstPoint().getParam() - 10.0 );
			piecewiseCubicMonotoneBezierFlatViewPane.setEndParamNumber( bez.getLastPoint().getParam() + 10 );
			piecewiseCubicMonotoneBezierFlatViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create piecewiseCubicMonotoneBezierFlatViewPane" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, piecewiseCubicMonotoneBezierFlatViewPane);
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
						piecewiseCubicMonotoneBezierFlatViewPane = null;
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
						piecewiseCubicMonotoneBezierFlatViewPane = null;
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

			bez = (PiecewiseCubicMonotoneBezierFlat)( myv.getProperty("Bez") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

