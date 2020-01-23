





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

import gredit.GNode;
import gredit.GWaveForm;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import verdantium.undo.UTag;
import verdantium.undo.UndoManager;
import core.InstrumentTrack;
import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * A node representing a waveform approximating a fractal interpolation as described in Chapter 4 of the book "Fractals Everywhere" by Michael F. Barnsley.
 * 
 * @author thorngreen
 *
 */
public class GFractInterpWaveForm extends GWaveForm  implements Externalizable {

	/**
	 * The interpolation points of the fractal.
	 */
	private ArrayList<FractInterpNode> nodes;
	
	/**
	 * The number of iterations over which to evaluate the fractal.
	 */
	private int iterCnt;
	
	/**
	 * View pane for editing the node.
	 */
	transient FractInterpViewPane fractInterpWaveViewPane = null;
	
	
	/**
	 * Constructs the node.
	 */
	public GFractInterpWaveForm()
	{
		iterCnt = 15;
		nodes = new ArrayList<FractInterpNode>();
		
		
		final FractInterpNode n0 = new FractInterpNode();
		final FractInterpNode n1 = new FractInterpNode();
		final FractInterpNode n2 = new FractInterpNode();
		final FractInterpNode n3 = new FractInterpNode();
		
		
		
		n0.setX( 0.0 );
		n1.setX( 0.3 );
		n2.setX( 0.6 );
		n3.setX( 1.0 );
		
		
		n0.setF( 0.0 );
		n1.setF( -0.5 );
		n2.setF( 0.4 );
		n3.setF( 0.0 );
		
		
		n0.setH( 0.0 );
		n1.setH( 0.3 );
		n2.setH( 0.6 );
		n3.setH( 1.0 );
		
		
		final double mult = 2.5;
		
		
		n1.setD( mult * 0.3 );
		n2.setD( mult * 0.3 );
		n3.setD( mult * 0.3 );
		
		
		n1.setHh( mult * 0.2 );
		n2.setHh( mult * 0.2 );
		n3.setHh( mult * 0.1 );
		
		
		n1.setL( mult * -0.1 );
		n2.setL( mult * -0.1 );
		n3.setL( mult * -0.1 );
		
		
		
		n1.setM( mult * 0.3 );
		n2.setM( mult * 0.0 );
		n3.setM( mult * -0.1 );
		
		
		
		nodes.add( n0 );
		nodes.add( n1 );
		nodes.add( n2 );
		nodes.add( n3 );
		
		
	}

	
	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm wv = new FractInterpWaveForm(nodes, iterCnt);
		s.put(this, wv);
		
		return( wv );
	}
	
	
	/**
	 * Loads new values into the node.
	 * @param _nodes  The interpolation points of the fractal.
	 * @param _iterCnt  The number of iterations over which to evaluate the fractal.
	 */
	public void load( ArrayList<FractInterpNode> _nodes , int _iterCnt )
	{
		nodes = _nodes;
		iterCnt = _iterCnt;
	}

	
	public Object getChldNodes() {
		return( null );
	}

	@Override
	public String getName() {
		return( "Noise -- FractInterp" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( false );
	}

	@Override
	public void performAssign(GNode in) {
		throw( new RuntimeException( "Not Supported" ) );

	}

	@Override
	public void removeChld() {
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		if (fractInterpWaveViewPane == null) {
			UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
			InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
			if( ( undoMgr == null ) || ( track == null ) )
			{
				throw( new RuntimeException( "UndoMgrIsNull" ) );
			}
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			fractInterpWaveViewPane = new FractInterpViewPane( undoMgr , context , track , nodes , iterCnt );
			FractInterpWaveFormEditor controller = new FractInterpWaveFormEditor(
					this, fractInterpWaveViewPane, track);
			fractInterpWaveViewPane.setStartLevel(-40.0);
			fractInterpWaveViewPane.setEndLevel(40.0);
			fractInterpWaveViewPane.setStartParamNumber( - 0.2 );
			fractInterpWaveViewPane.setEndParamNumber( 1.2 );
			fractInterpWaveViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create FractInterpWaveFormEditor" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, fractInterpWaveViewPane);
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
						fractInterpWaveViewPane = null;
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
						fractInterpWaveViewPane = null;
					}
				}
			} );
		}
	}

	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("IterCnt", iterCnt);

		myv.setInt("NodeSize", nodes.size());
		int slen = nodes.size();
		int count;
		for (count = 0; count < slen; count++) {
			myv.setProperty("Node_" + count, nodes.get(count));
		}

		out.writeObject(myv);
	}

	
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			iterCnt = myv.getInt("IterCnt");

			int slen = myv.getInt("NodeSize");
			nodes = new ArrayList<FractInterpNode>();
			System.out.println( "size : " + slen );
			int count;
			for (count = 0; count < slen; count++) {
				nodes.add( (FractInterpNode)(myv.getPropertyEx("Node_" + count)) );
			}
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Gets the interpolation points of the fractal.
	 * @return The interpolation points of the fractal.
	 */
	public ArrayList<FractInterpNode> getNodes() {
		return nodes;
	}

	/**
	 * Sets the interpolation points of the fractal.
	 * @param nodes The interpolation points of the fractal.
	 */
	public void setNodes(ArrayList<FractInterpNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * Gets the number of iterations over which to evaluate the fractal.
	 * @return The number of iterations over which to evaluate the fractal.
	 */
	public int getIterCnt() {
		return iterCnt;
	}

	/**
	 * Sets the number of iterations over which to evaluate the fractal.
	 * @param iterCnt The number of iterations over which to evaluate the fractal.
	 */
	public void setIterCnt(int iterCnt) {
		this.iterCnt = iterCnt;
	}

	
}

