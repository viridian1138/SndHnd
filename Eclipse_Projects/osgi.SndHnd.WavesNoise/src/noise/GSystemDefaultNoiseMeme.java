





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

import waves.GWaveMeme;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;

/**
 * Meme for generating a default white noise using HashWhiteWaveForm.
 * 
 * @author tgreen
 *
 */
public class GSystemDefaultNoiseMeme extends GWaveMeme implements Externalizable {

	/**
	 * Constructs the meme.
	 */
	public GSystemDefaultNoiseMeme() {
		setName( "Meme -- Noise -- System Default Noise" );
	}

	@Override
	public void initMeme(EditPackWave edit) {
		HashSet elem = edit.getElem();
		
		GHashWhiteWaveForm noise = new GHashWhiteWaveForm();
		edit.getWaveOut().performAssign( noise );
		
		AazonTransChld.initialCoords( AczonUnivAllocator.allocateUniv() , elem, null, edit.getWaveOut());

	}

	
	
}


