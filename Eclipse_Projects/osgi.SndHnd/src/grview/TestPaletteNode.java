




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







package grview;

import aazon.AazonEnt;
import aazon.AazonSmartToggle;
import aazon.AazonSmartTranslation;
import aazon.bool.AazonBool;
import aazon.bool.AazonMutableCompareBool;
import aazon.intg.AazonBaseImmutableInt;
import aazon.intg.AazonInt;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonVect;
import aczon.node.PaletteNode;


/**
 * A palette node used by class grview.DraggableTransformNodeTest.
 * 
 * @author tgreen
 *
 */
public class TestPaletteNode implements PaletteNode {
	
	/**
	 * The vector to the center of the palette button.
	 */
	protected AazonVect centerVect;
	
	/**
	 * The editing mode associated with the selection of the palette button.
	 */
	protected int mode;
	
	/**
	 * AazonEnt for the palette button being in the "on" state.
	 */
	protected AazonEnt shOn;
	
	/**
	 * AazonEnt for the palette button being in the "off" state.
	 */
	protected AazonEnt shOff;
	
	/**
	 * The current editing mode of the overall system.
	 */
	protected AazonInt sysMode;
	
	/**
	 * The location of the lower-left corner of the palette button.
	 */
	protected AazonVect box0;
	
	/**
	 * The location of the upper-right corner of the palette button.
	 */
	protected AazonVect box1;
	
	/**
	 * The translation required to put the palette button entity at its center location.
	 */
	protected AazonEnt entTrans;
	
	/**
	 * Whether the palette node is initialized.
	 */
	protected boolean init = false;
	
	
	/**
	 * Constructs the node.
	 * @param _centerVect The vector to the center of the palette button.
	 * @param _mode The editing mode associated with the selection of the palette button.
	 * @param _shOn AazonEnt for the palette button being in the "on" state.
	 * @param _shOff AazonEnt for the palette button being in the "off" state.
	 * @param _sysMode The current editing mode of the overall system.
	 */
	public TestPaletteNode( AazonVect _centerVect , int _mode , AazonEnt _shOn , AazonEnt _shOff , AazonInt _sysMode )
	{
		centerVect = _centerVect;
		mode = _mode;
		shOn = _shOn;
		shOff = _shOff;
		sysMode = _sysMode;
	}
	
	
	/**
	 * Initializes the palette node.
	 */
	protected void init()
	{
		if( !init )
		{
			box0 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( -0.1 , -0.1 ) );
			box1 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( 0.1 , 0.1 ) );
			final AazonBool comps = AazonMutableCompareBool.construct( sysMode , new AazonBaseImmutableInt( mode ) );
			final AazonEnt ent = AazonSmartToggle.construct( shOn , shOff , comps );
			entTrans = AazonSmartTranslation.construct( ent , centerVect );
		}
		init = true;
	}

	/**
	 * Gets the location of the lower-left corner of the palette button.
	 * @return The location of the lower-left corner of the palette button.
	 */
	public AazonVect getBox0() {
		init();
		return( box0 );
	}

	/**
	 * Gets the location of the upper-right corner of the palette button.
	 * @return The location of the upper-right corner of the palette button.
	 */
	public AazonVect getBox1() {
		init();
		return( box1 );
	}

	/**
	 * Gets the AazonEnt to be displayed for the palette button.
	 * @return The AazonEnt to be displayed for the palette button.
	 */
	public AazonEnt getEnt() {
		init();
		return( entTrans );
	}

	/**
	 * Gets the editing mode associated with the selection of the palette button.
	 * @return The editing mode associated with the selection of the palette button.
	 */
	public int getMode() {
		return( mode );
	}

	
}

