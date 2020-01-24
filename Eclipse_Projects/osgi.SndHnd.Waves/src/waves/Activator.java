





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







package waves;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import palettes.PaletteClassesWave;
import verdantium.clmgr.ClasspathManager;
import core.SongData;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		try {
		
		ClasspathManager.addClass( GAbsValWaveform.class );	
			
		PaletteClassesWave.addClass( GAbsValWaveform.class );
		PaletteClassesWave.addClass( GArith12Waveform.class );
		PaletteClassesWave.addClass( GArith14Waveform.class );
		PaletteClassesWave.addClass( GArith16Waveform.class );
		PaletteClassesWave.addClass( GArith18Waveform.class );
		PaletteClassesWave.addClass( GArith20Waveform.class );
		PaletteClassesWave.addClass( GArith22Waveform.class );
		PaletteClassesWave.addClass( GArith24Waveform.class );
		PaletteClassesWave.addClass( GArithAbsWaveform.class );
		PaletteClassesWave.addClass( GArithDecWaveform.class );
		PaletteClassesWave.addClass( GArithHexWaveform.class );
		PaletteClassesWave.addClass( GArithOctWaveform.class );
		PaletteClassesWave.addClass( GArithQuadWaveform.class );
		PaletteClassesWave.addClass( GArithSqrWaveform.class );
		PaletteClassesWave.addClass( GHardOverdriveWaveForm.class );
		PaletteClassesWave.addClass( GMultiSincWaveForm.class );
		PaletteClassesWave.addClass( GMultiSincCoWaveForm.class );
		PaletteClassesWave.addClass( GOffset30Waveform.class );
		PaletteClassesWave.addClass( GSawtoothWaveform.class );
		PaletteClassesWave.addClass( GSemicircularWaveform.class );
		PaletteClassesWave.addClass( GSincWaveform.class );
		PaletteClassesWave.addClass( GSincCoWaveform.class );
		PaletteClassesWave.addClass( GTriangleWaveform.class );
		PaletteClassesWave.addClass( GZeroWaveform.class );
		PaletteClassesWave.addClass( GBitcrushWaveform.class );
		PaletteClassesWave.addClass( GTimeDiscretizedWaveForm.class );
		PaletteClassesWave.addClass( GCloselySpacedChord.class );
		PaletteClassesWave.addClass( GArithCntWaveform.class );
		PaletteClassesWave.addClass( GArithGntWaveform.class );
		PaletteClassesWave.addClass( GKochSnowflakeWaveform.class );
		PaletteClassesWave.addClass( GXtalWaveform.class );
		PaletteClassesWave.addClass( GMultiXtalWaveform.class );
		PaletteClassesWave.addClass( GMMXtalWaveForm.class );
		PaletteClassesWave.addClass( GHarmonicDistortionWaveForm.class );
		PaletteClassesWave.addClass( GOverdrive.class );
		PaletteClassesWave.addClass( GOmniPresent.class );
		PaletteClassesWave.addClass( GXorWaveform.class );
		PaletteClassesWave.addClass( GGranularSynth.class );
		PaletteClassesWave.addClass( GConvolution.class );
		PaletteClassesWave.addClass( GFractalSawtoothWaveform.class );
		
		PaletteClassesWave.addClass( GGroupWave.class );
		PaletteClassesWave.addClass( GRoughDraftWaveSwitch.class );
		PaletteClassesWave.addClass( GRoughDraftSkipDistortion.class );
		PaletteClassesWave.addClass( GNoteSelectWaveSwitch.class );
		PaletteClassesWave.addClass( GAppxBezWaveform.class );
		PaletteClassesWave.addClass( GPrimitiveGuitarMeme.class );
		PaletteClassesWave.addClass( GChorusWaveform.class );
		PaletteClassesWave.addClass( GChorusLnWaveform.class );
		PaletteClassesWave.addClass( GAmplitudeLnWaveForm.class );
		PaletteClassesWave.addClass( GFrequencyLnWaveForm.class );
		
		PaletteClassesWave.addClass( GSpreadWaveform.class );
		
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
		
		try {
			
			ClasspathManager.removeClass( GAbsValWaveform.class );
			
			PaletteClassesWave.removeClass( GAbsValWaveform.class );
			PaletteClassesWave.removeClass( GArith12Waveform.class );
			PaletteClassesWave.removeClass( GArith14Waveform.class );
			PaletteClassesWave.removeClass( GArith16Waveform.class );
			PaletteClassesWave.removeClass( GArith18Waveform.class );
			PaletteClassesWave.removeClass( GArith20Waveform.class );
			PaletteClassesWave.removeClass( GArith22Waveform.class );
			PaletteClassesWave.removeClass( GArith24Waveform.class );
			PaletteClassesWave.removeClass( GArithAbsWaveform.class );
			PaletteClassesWave.removeClass( GArithDecWaveform.class );
			PaletteClassesWave.removeClass( GArithHexWaveform.class );
			PaletteClassesWave.removeClass( GArithOctWaveform.class );
			PaletteClassesWave.removeClass( GArithQuadWaveform.class );
			PaletteClassesWave.removeClass( GArithSqrWaveform.class );
			PaletteClassesWave.removeClass( GHardOverdriveWaveForm.class );
			PaletteClassesWave.removeClass( GMultiSincWaveForm.class );
			PaletteClassesWave.removeClass( GMultiSincCoWaveForm.class );
			PaletteClassesWave.removeClass( GOffset30Waveform.class );
			PaletteClassesWave.removeClass( GSawtoothWaveform.class );
			PaletteClassesWave.removeClass( GSemicircularWaveform.class );
			PaletteClassesWave.removeClass( GSincWaveform.class );
			PaletteClassesWave.removeClass( GSincCoWaveform.class );
			PaletteClassesWave.removeClass( GTriangleWaveform.class );
			PaletteClassesWave.removeClass( GZeroWaveform.class );
			PaletteClassesWave.removeClass( GBitcrushWaveform.class );
			PaletteClassesWave.removeClass( GTimeDiscretizedWaveForm.class );
			PaletteClassesWave.removeClass( GCloselySpacedChord.class );
			PaletteClassesWave.removeClass( GArithCntWaveform.class );
			PaletteClassesWave.removeClass( GArithGntWaveform.class );
			PaletteClassesWave.removeClass( GKochSnowflakeWaveform.class );
			PaletteClassesWave.removeClass( GXtalWaveform.class );
			PaletteClassesWave.removeClass( GMultiXtalWaveform.class );
			PaletteClassesWave.removeClass( GMMXtalWaveForm.class );
			PaletteClassesWave.removeClass( GHarmonicDistortionWaveForm.class );
			PaletteClassesWave.removeClass( GOverdrive.class );
			PaletteClassesWave.removeClass( GOmniPresent.class );
			PaletteClassesWave.removeClass( GXorWaveform.class );
			PaletteClassesWave.removeClass( GGranularSynth.class );
			PaletteClassesWave.removeClass( GConvolution.class );
			
			PaletteClassesWave.removeClass( GGroupWave.class );
			PaletteClassesWave.removeClass( GRoughDraftWaveSwitch.class );
			PaletteClassesWave.removeClass( GRoughDraftSkipDistortion.class );
			PaletteClassesWave.removeClass( GNoteSelectWaveSwitch.class );
			PaletteClassesWave.removeClass( GAppxBezWaveform.class );
			PaletteClassesWave.removeClass( GPrimitiveGuitarMeme.class );
			PaletteClassesWave.removeClass( GChorusWaveform.class );
			PaletteClassesWave.removeClass( GChorusLnWaveform.class );
			PaletteClassesWave.removeClass( GAmplitudeLnWaveForm.class );
			PaletteClassesWave.removeClass( GFrequencyLnWaveForm.class );
			
			PaletteClassesWave.removeClass( GSpreadWaveform.class );
			
			int core = 0;
			SongData.handleWaveChange(core);
			} 
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
		
	}

}
