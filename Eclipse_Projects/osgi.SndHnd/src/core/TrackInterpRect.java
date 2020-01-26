




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

import java.awt.geom.*;


/**
 * Visual interpolation point rectangle for an instrument track.  This class is no longer in use.
 * @author tgreen
 *
 */
public class TrackInterpRect extends Rectangle2D.Double {
	
	/**
	 * A reference to context data that the generating code can associate with the rectangle.  See various references to "refcon" in MacOS programming.
	 */
	Object refcon;
	
	/**
	 * The instrument track associated with the rectangle.
	 */
	InstrumentTrack track;

	/**
	 * Constructor.
	 * @param _track The instrument track associated with the rectangle.
	 * @param _refcon A reference to context data that the generating code can associate with the rectangle.  See various references to "refcon" in MacOS programming.
	 */
	public TrackInterpRect( InstrumentTrack _track , Object _refcon ) {
		super();
		track = _track;
		refcon = _refcon;
	}

	/**
	 * Gets the instrument track associated with the rectangle.
	 * @return The instrument track associated with the rectangle.
	 */
	public InstrumentTrack getTrack() {
		return track;
	}

	/**
	 * Gets the reference to context data that the generating code can associate with the rectangle.
	 * @return The reference to context data that the generating code can associate with the rectangle.
	 */
	public Object getRefcon() {
		return refcon;
	}

}


