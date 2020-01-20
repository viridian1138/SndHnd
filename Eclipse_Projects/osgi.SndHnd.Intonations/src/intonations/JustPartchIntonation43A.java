





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







package intonations;

import greditinton.GIntonation;

import java.util.HashMap;

/**
 * An just-intoned Partch 43-tone intonation in the key of A.
 * See https://en.wikipedia.org/wiki/Harry_Partch's_43-tone_scale
 * @author tgreen
 *
 */
public class JustPartchIntonation43A extends PartchIntonation43 {

	public static final double TUNE_0 = 1.0;
	
	public static final double TUNE_1 = 81.0/80.0;
	
    public static final double TUNE_2 = 33.0/32.0;
    
    public static final double TUNE_3 = 21.0/20.0;
    
    public static final double TUNE_4 = 16.0/15.0;
	
	public static final double TUNE_5 = 12.0/11.0;
			
	public static final double TUNE_6 = 11.0/10.0;
			
	public static final double TUNE_7 = 10.0/9.0;
			
	public static final double TUNE_8 = 9.0/8.0;
			
	public static final double TUNE_9 = 8.0/7.0;
			
	public static final double TUNE_10 = 7.0/6.0;
	
	public static final double TUNE_11 = 32.0/27.0;
			
	public static final double TUNE_12 = 6.0/5.0;
			
	public static final double TUNE_13 = 11.0/9.0;
			
	public static final double TUNE_14 = 5.0/4.0;
			
	public static final double TUNE_15 = 14.0/11.0;
			
	public static final double TUNE_16 = 9.0/7.0;
	
	public static final double TUNE_17 = 21.0/16.0;
			
	public static final double TUNE_18 = 4.0/3.0;
	
	public static final double TUNE_19 = 27.0/20.0;
			
	public static final double TUNE_20 = 11.0/8.0;
			
	public static final double TUNE_21 = 7.0/5.0;
			
	public static final double TUNE_22 = 10.0/7.0;
			
	public static final double TUNE_23 = 16.0/11.0;
	
	public static final double TUNE_24 = 40.0/27.0;
			
	public static final double TUNE_25 = 3.0/2.0;
	
	public static final double TUNE_26 = 32.0/21.0;
			
	public static final double TUNE_27 = 14.0/9.0;
			
	public static final double TUNE_28 = 11.0/7.0;
			
	public static final double TUNE_29 = 8.0/5.0;
			
	public static final double TUNE_30 = 18.0/11.0;
			
	public static final double TUNE_31 = 5.0/3.0;
	
	public static final double TUNE_32 = 27.0/16.0;
			
	public static final double TUNE_33 = 12.0/7.0;
			
	public static final double TUNE_34 = 7.0/4.0;
			
	public static final double TUNE_35 = 16.0/9.0;
			
	public static final double TUNE_36 = 9.0/5.0;
			
	public static final double TUNE_37 = 20.0/11.0;
			
	public static final double TUNE_38 = 11.0/6.0;
	
	public static final double TUNE_39 = 15.0/8.0;
	
    public static final double TUNE_40 = 40.0/21.0;
    
    public static final double TUNE_41 = 64.0/33.0;
    
    public static final double TUNE_42 = 160.0/81.0;
			
	public static final double TUNE_43 = 2.0;
	
	
	/**
	 * Constructs the intonation.
	 */
	public JustPartchIntonation43A()
	{
	}

	@Override
	public double[] calcIntonation() {
		final double[] ret = {
				TUNE_0 , TUNE_1 , TUNE_2 , TUNE_3 , TUNE_4 ,
				TUNE_5 , TUNE_6 , TUNE_7 , TUNE_8 , TUNE_9 ,
				TUNE_10 , TUNE_11 , TUNE_12 , TUNE_13 , TUNE_14 , 
				TUNE_15 , TUNE_16 , TUNE_17 , TUNE_18 , TUNE_19 ,
				TUNE_20 , TUNE_21 , TUNE_22 , TUNE_23 , TUNE_24 ,
				TUNE_25 , TUNE_26 , TUNE_27 , TUNE_28 , TUNE_29 ,
				TUNE_30 , TUNE_31 , TUNE_32 , TUNE_33 , TUNE_34 ,
				TUNE_35 , TUNE_36 , TUNE_37 , TUNE_38 , TUNE_39 ,
				TUNE_40 , TUNE_41 , TUNE_42 , TUNE_43 };
		return( ret );
	}
	
	/**
	 * Returns the pitch ratio for a particular number of Partch 43-tone steps.
	 * @param numSteps The number of Partch 43-tone steps.
	 * @return The pitch ratio.
	 */
	public static double calcIntonationVal( final int numSteps )
	{
		final double[] ret = {
				TUNE_0 , TUNE_1 , TUNE_2 , TUNE_3 , TUNE_4 ,
				TUNE_5 , TUNE_6 , TUNE_7 , TUNE_8 , TUNE_9 ,
				TUNE_10 , TUNE_11 , TUNE_12 , TUNE_13 , TUNE_14 , 
				TUNE_15 , TUNE_16 , TUNE_17 , TUNE_18 , TUNE_19 ,
				TUNE_20 , TUNE_21 , TUNE_22 , TUNE_23 , TUNE_24 ,
				TUNE_25 , TUNE_26 , TUNE_27 , TUNE_28 , TUNE_29 ,
				TUNE_30 , TUNE_31 , TUNE_32 , TUNE_33 , TUNE_34 ,
				TUNE_35 , TUNE_36 , TUNE_37 , TUNE_38 , TUNE_39 ,
				TUNE_40 , TUNE_41 , TUNE_42 , TUNE_43 };
		return( ret[ numSteps ] );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GJustPartchIntonation43A wv = new GJustPartchIntonation43A();
		s.put(this, wv);
		return( wv );
	}

	
}

