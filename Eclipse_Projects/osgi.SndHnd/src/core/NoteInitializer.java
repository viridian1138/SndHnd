




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

import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * Class containing parameters for initializing an instance of class NoteDesc.
 * @author tgreen
 *
 */
public class NoteInitializer implements Externalizable
{
	
	/**
	 * The frequency for the note to be populated.
	 */
	protected double baseFreq = 440.0;
	
	/**
	 * Interpolation points of the pitch-bend ratios of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 */
	protected ArrayList<InterpolationPoint> bendPerNoteUInterpPoints = null;
	
	/**
	 * Interpolation points of the user-defined (as opposed to agent-defined) envelope of the note.
	 */
	protected ArrayList<InterpolationPoint> noteEnvelopeInterpPoints = null;
	
	/**
	 * The vibrato parameters of the note to be populated.
	 */
	protected VibratoParameters vibratoParams = null;
	
	/**
	 * The portamento description for the note to be created.
	 */
	protected PortamentoDesc portamentoDesc = null;
	
	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			if( myv.getProperty( "BendInterpSize" ) != null )
			{
				int plen = myv.getInt("BendInterpSize");
				bendPerNoteUInterpPoints = new ArrayList<InterpolationPoint>(plen);
				int count;
				for (count = 0; count < plen; count++) {
					bendPerNoteUInterpPoints.add((InterpolationPoint) (myv.getPropertyEx("BendInterp_" + count)));
				}
			}
			
			if( myv.getProperty( "EnvInterpSize" ) != null )
			{
				int plen = myv.getInt("EnvInterpSize");
				noteEnvelopeInterpPoints = new ArrayList<InterpolationPoint>(plen);
				int count;
				for (count = 0; count < plen; count++) {
					noteEnvelopeInterpPoints.add((InterpolationPoint) (myv.getPropertyEx("EnvInterp_" + count)));
				}
			}
			
			vibratoParams = (VibratoParameters)( myv.getProperty("VibratoParameters") );
			portamentoDesc = (PortamentoDesc)( myv.getProperty("PortamentoDesc") );
			baseFreq = myv.getDouble("BaseFreq");
			
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
		
		if(bendPerNoteUInterpPoints != null)
		{
			myv.setInt("BendInterpSize", bendPerNoteUInterpPoints.size());
			int plen = bendPerNoteUInterpPoints.size();
			int count;
			for (count = 0; count < plen; count++) {
				myv.setProperty("BendInterp_" + count, bendPerNoteUInterpPoints.get(count));
			}
		}
		
		if(noteEnvelopeInterpPoints != null)
		{
			myv.setInt("EnvInterpSize", noteEnvelopeInterpPoints.size());
			int plen = noteEnvelopeInterpPoints.size();
			int count;
			for (count = 0; count < plen; count++) {
				myv.setProperty("EnvInterp_" + count, noteEnvelopeInterpPoints.get(count));
			}
		}
		
		if( vibratoParams != null )
		{
			myv.setProperty("VibratoParams", vibratoParams);
		}
		
		if( portamentoDesc != null )
		{
			myv.setProperty("PortamentoDesc", portamentoDesc);
		}

		myv.setDouble("BaseFreq",baseFreq);

		out.writeObject(myv);
	}

	/**
	 * Constructs the NoteInitializer.
	 */
	public NoteInitializer() {
		super();
	}
	
	/**
	 * Constructs the NoteInitializer.
	 * @param freq The frequency for the note to be populated.
	 */
	public NoteInitializer( double freq )
	{
		baseFreq = freq;
	}
	
	/**
	 * Gets the frequency for the note to be populated.
	 * @return The frequency for the note to be populated.
	 */
	public double getBaseFreq()
	{
		return( baseFreq );
	}
	
	/**
	 * Populates this instance from a NoteDesc.
	 * @param in The input NoteDesc.
	 * @param core The number of the core thread.
	 */
	public void initFromNoteDesc( NoteDesc in , final int core )
	{
		baseFreq = in.getFreqAndBend().getBaseFreq();
		
		if( in.getFreqAndBend().isUserDefinedBend() )
		{
			bendPerNoteUInterpPoints = new ArrayList<InterpolationPoint>();
			copyInterpPoints( in.getFreqAndBend().getBendPerNoteU().getInterpolationPoints() , 
					bendPerNoteUInterpPoints );
		}
		
		if( in.isUserDefinedNoteEnvelope() )
		{
			noteEnvelopeInterpPoints = new ArrayList<InterpolationPoint>();
			copyInterpPoints( in.getNoteEnvelope( core ).getBez().getInterpolationPoints() ,
					noteEnvelopeInterpPoints );
		}
		
		if( in.isUserDefinedVibrato() )
		{
			vibratoParams = new VibratoParameters( in.getVibratoParams() );
		}
		
		portamentoDesc = in.getPortamentoDesc();
		
	}
	
	/**
	 * Populates a NoteDesc from this instance.
	 * @param out The output NoteDesc.
	 * @param core The number of the core thread.
	 * @throws Throwable
	 */
	public void initToNoteDesc( NoteDesc out , final int core ) throws Throwable
	{
		out.setFrequency( baseFreq );
		
		if( bendPerNoteUInterpPoints != null )
		{
			copyInterpPoints( bendPerNoteUInterpPoints , 
					out.getFreqAndBend().getBendPerNoteU().getInterpolationPoints() );
			out.getFreqAndBend().getBendPerNoteU().updateAll();
			out.getFreqAndBend().setUserDefinedBend( true );
		}
		
		if( noteEnvelopeInterpPoints != null )
		{
			copyInterpPoints( noteEnvelopeInterpPoints , 
					out.getNoteEnvelope( core ).getBez().getInterpolationPoints() );
			out.getNoteEnvelope( core ).getBez().updateAll();
			out.setNoteEnvelope( out.getNoteEnvelope( core ) );
			out.setUserDefinedNoteEnvelope( true );
		}
		
		if( vibratoParams != null )
		{
			out.setVibratoParams( new VibratoParameters( vibratoParams ) );
			out.setUserDefinedVibrato( true );
		}
		
		out.setPortamentoDesc( portamentoDesc );
		
		out.getFreqAndBend().setWaveInfoDirty( true );
	}
	
	/**
	 * Copies interpolation points from one ArrayList to another.
	 * @param in The input ArrayList.
	 * @param out The output ArrayList.
	 */
	protected void copyInterpPoints( ArrayList<InterpolationPoint> in , ArrayList<InterpolationPoint> out )
	{
		out.clear();
		for( final InterpolationPoint ip : in )
		{
			InterpolationPoint op = new InterpolationPoint( ip.getParam() , ip.getValue() );
			out.add( op );
		}
	}

}


