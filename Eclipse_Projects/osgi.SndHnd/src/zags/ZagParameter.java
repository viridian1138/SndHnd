




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
import core.InterpolationPoint;;


public class ZagParameter {

	public ZagParameter() {
	}

	protected ArrayList<InterpolationPoint> interpPoints = new ArrayList<InterpolationPoint>();

	
	public ArrayList<InterpolationPoint> getInterpPoints() {
		return interpPoints;
	}

	public void setInterpPoints(ArrayList<InterpolationPoint> interpPoints) {
		this.interpPoints = interpPoints;
	}
	
	
	
	protected double calcRatio( double in )
	{
		return( Math.pow( 2.0 , in / 12.0 ) );
	}
	
	
	
	protected int getIndex( final double u )
	{
		for( int count = 0 ; count < interpPoints.size() ; count++  )
		{
			if( interpPoints.get( count ).getParam() > u  )
			{
				if( count == 0 )
				{
					return( 0 );
				}
				return( count - 1 );
			}
		}
		
		return( interpPoints.size() - 2 );
	}
	
	
	
	public double calcMult( final double u )
	{
		final int index = getIndex( u );
		final InterpolationPoint p0 = interpPoints.get(  index  );
		final InterpolationPoint p1 = interpPoints.get(  index + 1  );
		final double uu = ( u - p0.getParam() ) / ( p1.getParam() - p0.getParam() );
		final double vv = ( 1.0 - uu ) * ( p0.getValue() ) + uu * ( p1.getValue() );
		return( calcRatio( vv ) );
	}
	
	
	
}

