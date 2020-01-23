





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







package noise;

import gredit.EditPackWave;

import java.io.Externalizable;
import java.util.HashSet;

import waves.GSawtoothWaveform;
import waves.GSpreadWaveform;
import waves.GWaveMeme;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;

/**
 * Node representing a waveform resembling an initial guitar chirp.
 * @author tgreen
 *
 */
public class GGuitarChirpNoiseMeme extends GWaveMeme implements Externalizable {

	/**
	 * Constructs the node.
	 */
	public GGuitarChirpNoiseMeme() {
		setName( "Meme -- Noise -- Build Noise From Simulated Guitar Chirp" );
	}

	@Override
	public void initMeme(EditPackWave edit) {
		HashSet elem = edit.getElem();
		
		GHybridMultifractalWaveForm frac = new GHybridMultifractalWaveForm();
		edit.getWaveOut().performAssign( frac );
		frac.setOctaves( 6 );
		
		GSpreadWaveform spread = new GSpreadWaveform();
		frac.performAssign( spread );
		spread.setNum_op2( 75.0 );
		
		GSawtoothWaveform saw = new GSawtoothWaveform();
		spread.performAssign( saw );
		
		AazonTransChld.initialCoords( AczonUnivAllocator.allocateUniv() , elem, null, edit.getWaveOut());

	}

	
}


