




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

import java.util.WeakHashMap;

import aazon.AazonEnt;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonImmutableSharedEnt;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartLine;
import aazon.AazonSmartOutlineRectangle;
import aazon.vect.AazonBaseImmutableVect;
import aczon.AczonColor;

/**
 * Defines the palette button for the "delete child" mode.
 * 
 * @author tgreen
 *
 */
public class PaletteDelChld {
	
	/**
	 * Gets the AazonEnt for the palette button being in the "on" state.
	 * @param univ The universe ID for which to get the AazonEnt.
	 * @return The AazonEnt for the palette button being in the "on" state.
	 */
	public static AazonImmutableSharedEnt getRectGrpOn(Object univ) {
		if (rectGpOn.get(univ) == null) {
			final AazonEnt rect = AazonSmartFilledRectangle.construct(
					new AazonBaseImmutableVect(-0.1, -0.1),
					new AazonBaseImmutableVect(0.1, 0.1), AczonColor
							.getFillWhite(), false);

			final AazonEnt rectO = AazonSmartOutlineRectangle.construct(
					new AazonBaseImmutableVect(-0.1, -0.1),
					new AazonBaseImmutableVect(0.1, 0.1), AczonColor
							.getLineBlack(), false);
			
			final AazonEnt linea1 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(-0.075, -0.075),
					new AazonBaseImmutableVect(-0.0125, -0.0125), AczonColor
							.getLineMagenta(), false);
			
			final AazonEnt linea2 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(-0.0125, -0.075),
					new AazonBaseImmutableVect(-0.075, -0.0125), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner1 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(-0.05, 0.0125),
					new AazonBaseImmutableVect(0.075, 0.075), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner2 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.075, 0.075),
					new AazonBaseImmutableVect(0.075, 0.0), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner3 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.075, 0.075),
					new AazonBaseImmutableVect(0.0, 0.075), AczonColor
							.getLineMagenta(), false);

			final AazonEnt[] entsRect = { rect , rectO, linea1 , linea2 , liner1 , liner2 , liner3 };

			final AazonImmutableOrderedGroup rectGa = new AazonImmutableOrderedGroup(
					entsRect);

			rectGpOn.put(univ, new AazonImmutableSharedEnt(rectGa));
		}

		return ( rectGpOn.get(univ) );
	}

	/**
	 * Gets the AazonEnt for the palette button being in the "off" state.
	 * @param univ The universe ID for which to get the AazonEnt.
	 * @return The AazonEnt for the palette button being in the "off" state.
	 */
	public static AazonImmutableSharedEnt getRectGrpOff(Object univ) {
		if (rectGpOff.get(univ) == null) {
			final AazonEnt rect = AazonSmartFilledRectangle.construct(
					new AazonBaseImmutableVect(-0.1, -0.1),
					new AazonBaseImmutableVect(0.1, 0.1), AczonColor
							.getFillCyan(), false);

			final AazonEnt rectO = AazonSmartOutlineRectangle.construct(
					new AazonBaseImmutableVect(-0.1, -0.1),
					new AazonBaseImmutableVect(0.1, 0.1), AczonColor
							.getLineBlack(), false);
			
			final AazonEnt linea1 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(-0.075, -0.075),
					new AazonBaseImmutableVect(-0.0125, -0.0125), AczonColor
							.getLineMagenta(), false);
			
			final AazonEnt linea2 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(-0.0125, -0.075),
					new AazonBaseImmutableVect(-0.075, -0.0125), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner1 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(-0.05, 0.0125),
					new AazonBaseImmutableVect(0.075, 0.075), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner2 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.075, 0.075),
					new AazonBaseImmutableVect(0.075, 0.0), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner3 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.075, 0.075),
					new AazonBaseImmutableVect(0.0, 0.075), AczonColor
							.getLineMagenta(), false);

			final AazonEnt[] entsRect = { rect , rectO, linea1 , linea2 , liner1 , liner2 , liner3 };

			final AazonImmutableOrderedGroup rectGa = new AazonImmutableOrderedGroup(
					entsRect);

			rectGpOff.put(univ, new AazonImmutableSharedEnt(rectGa));
		}

		return ( rectGpOff.get(univ) );
	}

	/**
	 * Map containing the AazonEnt for the palette button being in the "on" state for each universe ID.
	 */
	protected static WeakHashMap<Object,AazonImmutableSharedEnt> rectGpOn = new WeakHashMap<Object,AazonImmutableSharedEnt>();

	/**
	 * Map containing the AazonEnt for the palette button being in the "off" state for each universe ID.
	 */
	protected static WeakHashMap<Object,AazonImmutableSharedEnt> rectGpOff = new WeakHashMap<Object,AazonImmutableSharedEnt>();

}
