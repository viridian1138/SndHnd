





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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import core.SongData;

import palettes.PaletteClassesWave;
import verdantium.clmgr.ClasspathManager;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		try{
			
		ClasspathManager.addClass( GCloudWave.class );
			
		PaletteClassesWave.addClass( GHashWhiteWaveForm.class );
		PaletteClassesWave.addClass( GCloudWave.class );
		PaletteClassesWave.addClass( GFractalSum.class );
		PaletteClassesWave.addClass( GSmokeDensity.class );
		PaletteClassesWave.addClass( GTurbulenceWave.class );
		PaletteClassesWave.addClass( GMarbleWave.class );
		PaletteClassesWave.addClass( GFbmWaveForm.class );
		PaletteClassesWave.addClass( GDistNoise.class );
		PaletteClassesWave.addClass( GDistFbm.class );
		PaletteClassesWave.addClass( GMultifractal.class );
		PaletteClassesWave.addClass( GVfbmWaveform.class );
		PaletteClassesWave.addClass( GHeteroTerrainWaveForm.class );
		PaletteClassesWave.addClass( GRidgedMultifractalWaveForm.class );
		PaletteClassesWave.addClass( GBasicGasWaveForm.class );
		PaletteClassesWave.addClass( GPlanetCloudsA.class );
		PaletteClassesWave.addClass( GPlanetCloudsB.class );
		PaletteClassesWave.addClass( GWardNoiseWaveform.class );
		PaletteClassesWave.addClass( GVnoiseWaveform.class );
		PaletteClassesWave.addClass( GGNoiseWaveform.class );
		PaletteClassesWave.addClass( GVGNoiseWaveForm.class );
		PaletteClassesWave.addClass( GHashTimeWaveForm.class );
		PaletteClassesWave.addClass( GHashDistortionWaveForm.class );
		PaletteClassesWave.addClass( GTexturePerturbWaveForm.class );
		PaletteClassesWave.addClass( GHybridMultifractalWaveForm.class );
		PaletteClassesWave.addClass( GGuitarChirpNoiseMeme.class );
		PaletteClassesWave.addClass( GSystemDefaultNoiseMeme.class );
		PaletteClassesWave.addClass( GRandWhiteWaveForm.class );
		PaletteClassesWave.addClass( GNksNoiseWaveForm.class );
		
		PaletteClassesWave.addClass( GFractInterpWaveForm.class );
		PaletteClassesWave.addClass( GPwpFractInterpWaveForm.class );
		PaletteClassesWave.addClass( GPwFractInterpWaveForm.class );
		
		
		int core = 0;
		SongData.handleWaveChange(core);
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		try{
			
			ClasspathManager.removeClass( GCloudWave.class );
			
			PaletteClassesWave.removeClass( GCloudWave.class );
			PaletteClassesWave.removeClass( GFractalSum.class );
			PaletteClassesWave.removeClass( GSmokeDensity.class );
			PaletteClassesWave.removeClass( GTurbulenceWave.class );
			PaletteClassesWave.removeClass( GMarbleWave.class );
			PaletteClassesWave.removeClass( GFbmWaveForm.class );
			PaletteClassesWave.removeClass( GDistNoise.class );
			PaletteClassesWave.removeClass( GDistFbm.class );
			PaletteClassesWave.removeClass( GMultifractal.class );
			PaletteClassesWave.removeClass( GVfbmWaveform.class );
			PaletteClassesWave.removeClass( GHeteroTerrainWaveForm.class );
			PaletteClassesWave.removeClass( GRidgedMultifractalWaveForm.class );
			PaletteClassesWave.removeClass( GBasicGasWaveForm.class );
			PaletteClassesWave.removeClass( GPlanetCloudsA.class );
			PaletteClassesWave.removeClass( GPlanetCloudsB.class );
			PaletteClassesWave.removeClass( GWardNoiseWaveform.class );
			PaletteClassesWave.removeClass( GVnoiseWaveform.class );
			PaletteClassesWave.removeClass( GGNoiseWaveform.class );
			PaletteClassesWave.removeClass( GVGNoiseWaveForm.class );
			PaletteClassesWave.removeClass( GHashTimeWaveForm.class );
			PaletteClassesWave.removeClass( GHashDistortionWaveForm.class );
			PaletteClassesWave.removeClass( GTexturePerturbWaveForm.class );
			PaletteClassesWave.removeClass( GHybridMultifractalWaveForm.class );
			PaletteClassesWave.removeClass( GGuitarChirpNoiseMeme.class );
			PaletteClassesWave.removeClass( GRandWhiteWaveForm.class );
			
			PaletteClassesWave.removeClass( GFractInterpWaveForm.class );
			PaletteClassesWave.removeClass( GPwpFractInterpWaveForm.class );
			PaletteClassesWave.removeClass( GPwFractInterpWaveForm.class );
			
			
			int core = 0;
			SongData.handleWaveChange(core);
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
	}

}
