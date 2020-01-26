




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
 * A portamento transition.  For instance, in western notation a "tie" connecting two notes.
 * Can also describe other transitions in western notation such as intentional pitch slides.
 * In this class the transition can "land" on an arbitrary pitch that doesn't need to be on any scale.
 * 
 * @author tgreen
 *
 */
public class PortamentoTransition implements Externalizable {

	/**
	 * Constructs the portamento transition.
	 */
	public PortamentoTransition() {
		super();
	}
	
	/**
	 * The ratio between the pitch where the portamento lands and the pitch where the portamento starts.
	 */
	protected double pitchRatio = 1.5;
	
	/**
	 * The transition time for the portamento in seconds.
	 */
	protected double transitionTimeSeconds = 0.13;
	
	/**
	 * The point in the note where the portamento transition is to happen.
	 * This is a normalized coefficient across the set of beats in the note.
	 */
	protected double transitionNoteU = 0.5;
	
	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			pitchRatio = myv.getDouble("PitchRatio");
			transitionTimeSeconds = myv.getDouble("TransitionTimeSeconds");
			transitionNoteU = myv.getDouble("TransitionNoteU");
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

		myv.setDouble("PitchRatio",pitchRatio);
		myv.setDouble("TransitionTimeSeconds",transitionTimeSeconds);
		myv.setDouble("TransitionNoteU",transitionNoteU);

		out.writeObject(myv);
	}

	/**
	 * Gets the ratio between the pitch where the portamento lands and the pitch where the portamento starts.
	 * @return Returns The ratio between the pitch where the portamento lands and the pitch where the portamento starts.
	 */
	public double getPitchRatio() {
		return pitchRatio;
	}

	/**
	 * Sets the ratio between the pitch where the portamento lands and the pitch where the portamento starts.
	 * @param pitchRatio The ratio between the pitch where the portamento lands and the pitch where the portamento starts.
	 */
	public void setPitchRatio(double pitchRatio) {
		this.pitchRatio = pitchRatio;
	}

	/**
	 * Gets the point in the note where the portamento transition is to happen.
	 * @return The point in the note where the portamento transition is to happen.
	 */
	public double getTransitionNoteU() {
		return transitionNoteU;
	}

	/**
	 * Sets the point in the note where the portamento transition is to happen.
	 * @param transitionNoteU The point in the note where the portamento transition is to happen.
	 */
	public void setTransitionNoteU(double transitionNoteU) {
		this.transitionNoteU = transitionNoteU;
	}

	/**
	 * Gets the transition time for the portamento in seconds.
	 * @return The transition time for the portamento in seconds.
	 */
	public double getTransitionTimeSeconds() {
		return transitionTimeSeconds;
	}

	/**
	 * Sets the transition time for the portamento in seconds.
	 * @param transitionTimeSeconds The transition time for the portamento in seconds.
	 */
	public void setTransitionTimeSeconds(double transitionTimeSeconds) {
		this.transitionTimeSeconds = transitionTimeSeconds;
	}
	

}

