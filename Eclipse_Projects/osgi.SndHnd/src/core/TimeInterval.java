




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

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * An interval in elapsed time.
 * 
 * @author tgreen
 *
 */
public class TimeInterval implements Externalizable {

	/**
	 * The start time in seconds.
	 */
	protected double startTimeSeconds;

	/**
	 * The end time in seconds.
	 */
	protected double endTimeSeconds;

	/**
	 * Constructor.
	 * @param _startTimeSeconds The start time in seconds.
	 * @param _endTimeSeconds The end time in seconds.
	 */
	public TimeInterval(double _startTimeSeconds, double _endTimeSeconds) {
		super();
		startTimeSeconds = _startTimeSeconds;
		endTimeSeconds = _endTimeSeconds;
	}
	
	/**
	 * Null constructor.  Used for persistence only.
	 */
	public TimeInterval()
	{
	}

	/**
	 * Gets the start time in seconds.
	 * @return The start time in seconds.
	 */
	public double getEndTimeSeconds() {
		return endTimeSeconds;
	}

	/**
	 * Gets the end time in seconds.
	 * @return The end time in seconds.
	 */
	public double getStartTimeSeconds() {
		return startTimeSeconds;
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			startTimeSeconds = myv.getDouble("StartTimeSeconds");
			endTimeSeconds = myv.getDouble("EndTimeSeconds");
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("StartTimeSeconds", startTimeSeconds);
		myv.setDouble("EndTimeSeconds", endTimeSeconds);

		out.writeObject(myv);
	}

}

