





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







package intonations;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import palettes.PaletteClassesIntonation;
import verdantium.clmgr.ClasspathManager;
import core.SongData;


public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		try{
			
		ClasspathManager.addClass( GJustIntonationAMinor.class );
			
		PaletteClassesIntonation.addClass( GJustIntonationAMinor.class );
		PaletteClassesIntonation.addClass( GPythagoreanIntonationAMinor.class );
		PaletteClassesIntonation.addClass( GConflate.class );
		PaletteClassesIntonation.addClass( GIntonationPlayer.class );
		PaletteClassesIntonation.addClass( GIntonationPlayerHarmony.class );
		PaletteClassesIntonation.addClass( GIntonationPlayerTwoSide.class );
		PaletteClassesIntonation.addClass( GTypicalMode.class );
		PaletteClassesIntonation.addClass( GQuarterCommaMeantoneAugmentedFourthAMinor.class );
		PaletteClassesIntonation.addClass( GQuarterCommaMeantoneDiminishedFifthAMinor.class );
		PaletteClassesIntonation.addClass( GMultiplicativeAdjust.class );
		PaletteClassesIntonation.addClass( GGroupIntonation.class );
		PaletteClassesIntonation.addClass( GDissonanceIntonation.class );
		PaletteClassesIntonation.addClass( GPythagoreanPentatonicIntonationA.class );
		PaletteClassesIntonation.addClass( GJustPenatonicIntonationA.class );
		PaletteClassesIntonation.addClass( GJustPartchIntonation43A.class );
		PaletteClassesIntonation.addClass( GCarlosAlphaIntonationA.class );
		PaletteClassesIntonation.addClass( GCarlosBetaIntonationA.class );
		PaletteClassesIntonation.addClass( GCarlosGammaIntonationA.class );
		PaletteClassesIntonation.addClass( GModifiedBetaIntonationA.class );
		PaletteClassesIntonation.addClass( GDiatonicQuartertoneIntonationA.class );
		PaletteClassesIntonation.addClass( GIntonationPrinter.class );
		PaletteClassesIntonation.addClass( GWesternHarmonoidAMinor.class );
		PaletteClassesIntonation.addClass( GJustPentatonicIntonationMinor.class );
		PaletteClassesIntonation.addClass( GJustPentatonicIntonationMajor.class );
		PaletteClassesIntonation.addClass( GHarmonoidPentatonicIntonationMinor.class );
		PaletteClassesIntonation.addClass( GDiatonicCarlosIntonation15A.class );
		
		
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
			
			ClasspathManager.removeClass( GJustIntonationAMinor.class );
			
			PaletteClassesIntonation.removeClass( GJustIntonationAMinor.class );
			PaletteClassesIntonation.removeClass( GPythagoreanIntonationAMinor.class );
			PaletteClassesIntonation.removeClass( GConflate.class );
			PaletteClassesIntonation.removeClass( GIntonationPlayer.class );
			PaletteClassesIntonation.removeClass( GIntonationPlayerHarmony.class );
			PaletteClassesIntonation.removeClass( GIntonationPlayerTwoSide.class );
			PaletteClassesIntonation.removeClass( GTypicalMode.class );
			PaletteClassesIntonation.removeClass( GQuarterCommaMeantoneAugmentedFourthAMinor.class );
			PaletteClassesIntonation.removeClass( GQuarterCommaMeantoneDiminishedFifthAMinor.class );
			PaletteClassesIntonation.removeClass( GMultiplicativeAdjust.class );
			PaletteClassesIntonation.removeClass( GGroupIntonation.class );
			PaletteClassesIntonation.removeClass( GDissonanceIntonation.class );
			PaletteClassesIntonation.removeClass( GPythagoreanPentatonicIntonationA.class );
			PaletteClassesIntonation.removeClass( GJustPenatonicIntonationA.class );
			PaletteClassesIntonation.removeClass( GJustPartchIntonation43A.class );
			PaletteClassesIntonation.removeClass( GCarlosAlphaIntonationA.class );
			PaletteClassesIntonation.removeClass( GCarlosBetaIntonationA.class );
			PaletteClassesIntonation.removeClass( GCarlosGammaIntonationA.class );
			PaletteClassesIntonation.removeClass( GModifiedBetaIntonationA.class );
			PaletteClassesIntonation.removeClass( GDiatonicQuartertoneIntonationA.class );
			PaletteClassesIntonation.removeClass( GIntonationPrinter.class );
			PaletteClassesIntonation.removeClass( GWesternHarmonoidAMinor.class );
			PaletteClassesIntonation.removeClass( GJustPentatonicIntonationMinor.class );
			PaletteClassesIntonation.removeClass( GJustPentatonicIntonationMajor.class );
			PaletteClassesIntonation.removeClass( GHarmonoidPentatonicIntonationMinor.class );
			PaletteClassesIntonation.removeClass( GDiatonicCarlosIntonation15A.class );
			
			
			int core = 0;
			SongData.handleWaveChange(core);
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
	}

}
