




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


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import bezier.CubicBezierCurve;
import bezier.PiecewiseCubicMonotoneBezierFlat;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Describes the portamentos in a musical note.
 * 
 * @author tgreen
 *
 */
public class PortamentoDesc implements Externalizable {

	/**
	 * Constructs the portamento description.
	 */
	public PortamentoDesc() {
		super();
	}
	
	/**
	 * Ordered list of portamento transitions.
	 */
	protected ArrayList<PortamentoTransition> portamentoTransitions = new ArrayList<PortamentoTransition>();
	
	
	/**
	 * Clears all portamento transitions.
	 */
	public void clearTransitions()
	{
		portamentoTransitions.clear();
	}
	
	
	/**
	 * Gets the ordered list of portamento transitions.
	 * @return Ordered list of portamento transitions.
	 */
	public ArrayList<PortamentoTransition> getTransitions()
	{
		return( portamentoTransitions );
	}
	
	
	/**
	 * Adds a portamento transition.
	 * @param transit The portamento transition to be added.
	 */
	public void addTransition( PortamentoTransition transit )
	{
		TreeMap<Double,PortamentoTransition> t = new TreeMap<Double,PortamentoTransition>();
		for( final PortamentoTransition tr : portamentoTransitions )
		{
			t.put( new Double( tr.getTransitionNoteU() ) , tr );
		}
		t.put( new Double( transit.getTransitionNoteU() ) , transit );
		portamentoTransitions.clear();
		Iterator<Double> itb = t.keySet().iterator();
		while( itb.hasNext() )
		{
			PortamentoTransition tr = t.get( itb.next() );
			portamentoTransitions.add( tr );
		}
	}
	
	
	/**
	 * Builds the pitch-bend for the portamento.
	 * @param desc The note to which the portamento is to be applied.
	 * @param nxt The note after the note to which the portamento is to be applied.
	 * @param interps The input interpolation points of the portamento.
	 * @param core The number of the core thread.
	 */
	public void buildBend( NoteDesc desc , NoteDesc nxt , ArrayList<InterpolationPoint> interps , final int core )
	{
		
		double startBeat = desc.getActualStartBeatNumber();
		double endBeat = desc.getActualEndBeatNumber();
		
//		if( nxt != null )
//		{
//			if( nxt.getStartBeatNumber() < endBeat )
//			{
//				endBeat = nxt.getStartBeatNumber();
//			}
//		}
		
		
		System.out.println( "Generating Portamentos..." );
		
		PiecewiseCubicMonotoneBezierFlat ibend = new PiecewiseCubicMonotoneBezierFlat();
		ArrayList<InterpolationPoint> interp2 = ibend.getInterpolationPoints();
		for( final InterpolationPoint pt : interps )
		{
			interp2.add( pt );
		}
		interps.clear();
		ibend.updateAll();
		
		ArrayList<InterpolationPoint> interps3 = new ArrayList<InterpolationPoint>();
		
		
		PiecewiseCubicMonotoneBezierFlat ratioCrv = new PiecewiseCubicMonotoneBezierFlat();
		ArrayList<InterpolationPoint> interps4 = ratioCrv.getInterpolationPoints();
		
		
		double prevRatio = 1.0;
		interps4.add( new InterpolationPoint( 0.0 , prevRatio ) );
		
		System.out.println( "Starting Portaemto It..." );
		
		for( final PortamentoTransition transit : portamentoTransitions )
		{
			double u = transit.getTransitionNoteU();
			double newRatio = transit.getPitchRatio();
			double transitTime = transit.getTransitionTimeSeconds();
			
			double startTrBeat = (1-u)*startBeat + u * endBeat;
			
			double startTrTime = SongData.getElapsedTimeForBeatBeat( startTrBeat , core );
			double endTrTime = startTrTime + transitTime;
			
			double startVal = Math.log( prevRatio );
			double endVal = Math.log( newRatio );
			
			CubicBezierCurve pitchCrv = new CubicBezierCurve();
			pitchCrv.getBezPts()[ 0 ] = startVal;
			pitchCrv.getBezPts()[ 1 ] = startVal;
			pitchCrv.getBezPts()[ 2 ] = endVal;
			pitchCrv.getBezPts()[ 3 ] = endVal;
			pitchCrv.setStartParam( 0.0 );
			pitchCrv.setEndParam( 1.0 );
			
			int count;
			final int tr_sz = 64;
			for( count = 0 ; count < tr_sz ; count++ )
			{
				double v = count / (double)( tr_sz - 1 );
				double time = (1-v) * startTrTime + v * endTrTime;
				double beat = SongData.getBeatNumber( time , core );
				double beatU = ( beat - startBeat ) / ( endBeat - startBeat );
				double baseFr = ibend.eval( beatU );
				double rval = pitchCrv.eval( v );
				double fr = baseFr * Math.exp( rval );
				InterpolationPoint pt = new InterpolationPoint( beatU , fr );
				interps3.add( pt );
				pt = new InterpolationPoint( beatU , Math.exp( rval ) );
				interps4.add( pt );
			}
			
			
			prevRatio = newRatio;
		}
		
		interps4.add( new InterpolationPoint( 1.0 , prevRatio ) );
		ratioCrv.updateAll();
		
		System.out.println( "Started Stitch..." );
		
		int acnt = 0;
		int bcnt = 0;
		InterpolationPoint apt = null;
		InterpolationPoint bpt = null;
		if( acnt < interp2.size() )
		{
			apt = interp2.get( acnt );
		}
		else
		{
			apt = null;
		}
		if( bcnt < interps3.size() )
		{
			bpt = interps3.get( bcnt );
		}
		else
		{
			bpt = null;
		}
		double prevParam = -50.0;
		while( ( apt != null ) || ( bpt != null ) )
		{
			if( apt == null )
			{
				if( Math.abs( bpt.getParam() - prevParam ) > 1E-9 )
				{
					interps.add( bpt );
				}
				prevParam = bpt.getParam();
				bcnt++;
			}
			else if( bpt == null )
			{
				if( Math.abs( apt.getParam() - prevParam ) > 1E-9 )
				{
					double u = apt.getParam();
					double val = apt.getValue() * ratioCrv.eval( u );
					interps.add( new InterpolationPoint( u , val ) );
				}
				prevParam = apt.getParam();
				acnt++;
			}
			else
			{
				if( apt.getParam() < bpt.getParam() )
				{
					if( Math.abs( apt.getParam() - prevParam ) > 1E-9 )
					{
						double u = apt.getParam();
						double val = apt.getValue() * ratioCrv.eval( u );
						interps.add( new InterpolationPoint( u , val ) );
					}
					prevParam = apt.getParam();
					acnt++;
				}
				else
				{
					if( Math.abs( bpt.getParam() - prevParam ) > 1E-9 )
					{
						interps.add( bpt );
					}
					prevParam = bpt.getParam();
					bcnt++;
				}
			}
			
			if( acnt < interp2.size() )
			{
				apt = interp2.get( acnt );
			}
			else
			{
				apt = null;
			}
			if( bcnt < interps3.size() )
			{
				bpt = interps3.get( bcnt );
			}
			else
			{
				bpt = null;
			}
			
		}
		
		System.out.println( "Finished Stitch..." );
		
	}
	
	
	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			
			int sz = myv.getInt( "PortamentoSize" );
			int count;
			
			for( count = 0 ; count < sz ; count++ )
			{
				PortamentoTransition transit = (PortamentoTransition)( myv.getProperty( "Portamento_" + count ) );
				portamentoTransitions.add( transit );
			}

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		int sz = portamentoTransitions.size();
		int count;
		
		myv.setInt("PortamentoSize", sz);
		
		for( count = 0 ; count < sz ; count++ )
		{
			myv.setProperty( "Portamento_" + count , portamentoTransitions.get( count ) );
		}

		out.writeObject(myv);
	}

}

