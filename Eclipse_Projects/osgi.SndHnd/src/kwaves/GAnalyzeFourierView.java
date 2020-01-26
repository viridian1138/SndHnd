




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







package kwaves;

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import verdantium.undo.UndoManager;
import core.CpuInfo;
import core.InstrumentTrack;
import core.WaveForm;


/**
 * Node that displays the Fourier spectrum of an input waveform.
 * @author tgreen
 *
 */
public class GAnalyzeFourierView extends GNode implements Externalizable {
	
	/**
	 * The node for the input waveform on which to generate the Fourier spectrum.
	 */
	private GWaveForm chld;

	@Override
	public Object genObj(HashMap s) {
		throw( new RuntimeException( "NotSupported" ) );
	}

	@Override
	public String getName() {
		return( "Analyze -- View FourierSpectrum" );
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
	
	public Object getChldNodes() {
		return( chld );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context ) throws Throwable
	{
		final int NUM_CORES = CpuInfo.getNumCores();
		
		UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
		
		final WaveForm[] waves = new WaveForm[ NUM_CORES ];
		int cnt;
		for( cnt = 0 ; cnt < NUM_CORES ; cnt++ )
		{
			HashMap s = new HashMap();
			waves[ cnt ] = chld.genWave(s);
		}
		
		if( ( waves[0] == null ) || ( undoMgr == null )  )
		{
			throw( new RuntimeException( "Null" ) );
		}
		
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		AnalyzeFourierViewEditor editor = new AnalyzeFourierViewEditor( this , track , waves );
		ProgramDirector.showPropertyEditor(editor, null,
			"Analyze Fourier View Properties");
		
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);

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

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

