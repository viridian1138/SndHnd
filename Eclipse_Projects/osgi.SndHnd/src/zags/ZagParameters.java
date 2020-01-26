




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







package zags;

import java.util.ArrayList;

import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;
import core.InterpolationPoint;
import core.NoteDesc;

public class ZagParameters {

	
	public ZagParameters() {
	}
	
	
	protected ArrayList<ZagParameter> zagParameters = new ArrayList<ZagParameter>();


	public ArrayList<ZagParameter> getZagParameters() {
		return zagParameters;
	}


	public void setZagParameters(ArrayList<ZagParameter> zagParameters) {
		this.zagParameters = zagParameters;
	}
	
	
	public double calcMult( double u )
	{
		double mult = 1.0;
		for( final ZagParameter z : zagParameters )
		{
			mult = mult * z.calcMult( u );
		}
		
		return( mult );
	}
	
	
	public void applyZags( final NoteDesc nd )
	{
		
		nd.getFreqAndBend().setUserDefinedBend( true );
		
		nd.getFreqAndBend().setWaveInfoDirty( true );
		
		PiecewiseCubicMonotoneBezierFlatMultiCore a = nd.getFreqAndBend().getBendPerNoteU();
		
		
		System.out.println( "// " + ( a.getInterpolationPoints().size() ) );
		
		
		for( InterpolationPoint pt : a.getInterpolationPoints() )
		{
			final double iparam = pt.getParam();
			final double ivalue = pt.getValue();
		
			final double ovalue = ivalue * calcMult( iparam );
			
			pt.setValue( ovalue );
		}
		
		
		a.updateAll();
	}
	

	
}


