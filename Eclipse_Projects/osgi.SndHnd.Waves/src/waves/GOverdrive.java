





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

import gredit.GNode;
import gredit.GWaveForm;

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
import bezier.PiecewiseCubicMonotoneBezierFlat;
import core.InstrumentTrack;
import core.SongData;
import core.WaveForm;

/**
 * 
 * Node representing a waveform applying overdrive to an input waveform.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Distortion_(music)">https://en.wikipedia.org/wiki/Distortion_(music)</A>
 * 
 * @author tgreen
 *
 */
public class GOverdrive extends GWaveForm  implements Externalizable {
	
	/**
	 * Overdrive curve from which to generate the overdrive.
	 */
	protected PiecewiseCubicMonotoneBezierFlat overdrive = SongData.buildOverdriveCurve();

	/**
	 * Input waveform.
	 */
	private GWaveForm chld;
	
	/**
	 * View pane for editing the node.
	 */
	transient OverdriveViewPane overdriveViewPane = null;
	
	/**
	 * Constructs the node.
	 */
	public GOverdrive()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = chld.genWave(s);
		
		WaveForm wv = SongData.buildOverdrive( w , overdrive );;
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param in Input waveform.
	 * @param ovd Overdrive curve from which to generate the overdrive.
	 */
	public void load( GWaveForm in , PiecewiseCubicMonotoneBezierFlat ovd  )
	{
		chld = in;
		overdrive = ovd;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		if (overdriveViewPane == null) {
			UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
			InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
			if( ( undoMgr == null ) || ( track == null ) )
			{
				throw( new RuntimeException( "UndoMgrIsNull" ) );
			}
			UTag utag = new UTag();
			undoMgr.prepareForTempCommit(utag);
			overdriveViewPane = new OverdriveViewPane( undoMgr , context , track, overdrive );
			OverdriveEditor controller = new OverdriveEditor(
					overdriveViewPane );
			overdriveViewPane.setStartLevel(-40.0);
			overdriveViewPane.setEndLevel(40.0);
			overdriveViewPane.setStartParamNumber( - 0.2 );
			overdriveViewPane.setEndParamNumber( 1.2 );
			overdriveViewPane.buildDisplayList();
			undoMgr.commitUndoableOp(utag, "Create FractInterpWaveFormEditor" );
			undoMgr.clearUndoMemory();
			final JFrame fr = new JFrame();
			fr.getContentPane().setLayout(new BorderLayout(0, 0));
			fr.getContentPane().add(BorderLayout.CENTER, overdriveViewPane);
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
						overdriveViewPane = null;
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
						overdriveViewPane = null;
					}
				}
			} );
		}
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Overdrive/Compression" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		chld = (GWaveForm) in;

	}

	@Override
	public void removeChld() {
		chld = null;
	}

	/**
	 * Gets the default overdrive curve from which to generate the overdrive.
	 * @return Default overdrive curve from which to generate the overdrive.
	 */
	public PiecewiseCubicMonotoneBezierFlat getOverdrive() {
		return overdrive;
	}

	/**
	 * Sets the default overdrive curve from which to generate the overdrive.
	 * @param overdrive Default overdrive curve from which to generate the overdrive.
	 */
	public void setOverdrive(PiecewiseCubicMonotoneBezierFlat overdrive) {
		this.overdrive = overdrive;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setProperty("Overdrive", overdrive);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			chld = (GWaveForm)( myv.getProperty("Chld") );
			overdrive = (PiecewiseCubicMonotoneBezierFlat)( myv.getProperty("Overdrive") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

