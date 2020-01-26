




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







package aazon.bool;


/**
 * An abstract base class for an Aazon immutable boolean.
 * 
 * The actual allocation uses a version of the Flyweight pattern on the two possible boolean values.  See https://en.wikipedia.org/wiki/Flyweight_pattern
 * 
 * @author tgreen
 *
 */
public abstract class AazonBaseImmutableBool extends AazonImmutableBool {

	@Override
	public AazonBaseImmutableBool getBool()
	{
		return( this );
	}
	
	/**
	 * Gets the value of the boolean.
	 * @return The value of the boolean.
	 */
	public boolean getBoolVal()
	{
		return( this == TRUE );
	}
	
	/**
	 * Gets the negation of the boolean.
	 * @return The negation of the boolean.
	 */
	public abstract AazonBaseImmutableBool not();
	
	/**
	 * Returns the result of performing an "and" operation on the boolean.
	 * @param in The boolean to be the second argument of the boolean "and".
	 * @return The result of performing an "and" operation on the boolean.
	 */
	public abstract AazonBaseImmutableBool and( AazonBaseImmutableBool in );
	
	/**
	 * Returns the result of performing an "or" operation on the boolean.
	 * @param in The boolean to be the second argument of the boolean "or".
	 * @return The result of performing an "or" operation on the boolean.
	 */
	public abstract AazonBaseImmutableBool or( AazonBaseImmutableBool in );
	
	/**
	 * Private constructor.
	 */
	private AazonBaseImmutableBool()
	{
	}
	
	/**
	 * Immutable boolean for "true".
	 */
	public static final AazonBaseImmutableBool TRUE = new AazonBaseImmutableBool()
	{	
		@Override
		public AazonBaseImmutableBool not()
		{
			return( FALSE );
		}
		
		@Override
		public AazonBaseImmutableBool and( AazonBaseImmutableBool in )
		{
			return( in );
		}
		
		@Override
		public AazonBaseImmutableBool or( AazonBaseImmutableBool in )
		{
			return( this );
		}
		
	};
	
	/**
	 * Immutable boolean for "false".
	 */
	public static final AazonBaseImmutableBool FALSE = new AazonBaseImmutableBool()
	{	
		@Override
		public AazonBaseImmutableBool not()
		{
			return( TRUE );
		}
		
		@Override
		public AazonBaseImmutableBool and( AazonBaseImmutableBool in )
		{
			return( this );
		}
		
		@Override
		public AazonBaseImmutableBool or( AazonBaseImmutableBool in )
		{
			return( in );
		}
		
	};
	
	/**
	 * Returns an immutable boolean corresponding to the input value.
	 * @param in The input value.
	 * @return The immutable boolean.
	 */
	public static AazonBaseImmutableBool construct( boolean in )
	{
		return( in ? TRUE : FALSE );
	}
	
	

}

