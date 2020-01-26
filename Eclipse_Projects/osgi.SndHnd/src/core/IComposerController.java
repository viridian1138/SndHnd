




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

/**
 * Interface for controlling automated composition.  This definition is preliminary, and will be completely rewritten at some point in the future.
 * 
 * @author tgreen
 *
 */
public interface IComposerController {
	
	/**
	 * Builds a round of music.
	 * @param oc Receives refresh requests after the composition is complete.
	 * @throws Throwable
	 */
	public void buildRoundMain( OutputChoiceInterface oc ) throws Throwable;
	
	/**
	 * Builds a round of music harmonizing with the main round.
	 * @param oc Receives refresh requests after the composition is complete.
	 * @throws Throwable
	 */
	public void buildRoundHarmony( OutputChoiceInterface oc ) throws Throwable;
	
	/**
	 * Prints the results of the previous attempt to build a round of music.
	 */
	public void printResults();

}
