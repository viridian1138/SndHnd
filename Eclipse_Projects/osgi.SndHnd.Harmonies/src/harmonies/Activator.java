





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







package harmonies;

import harmonies.GHarmonyPrinter;
import harmonies.GTestTriad;
import harmonies.GBrightingPentad;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import palettes.PaletteClassesHarmony;
import verdantium.clmgr.ClasspathManager;
import core.SongData;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		try{
			
		ClasspathManager.addClass( GHarmonyPrinter.class );
			
		PaletteClassesHarmony.addClass( GHarmonyPrinter.class );
		PaletteClassesHarmony.addClass( GTestTriad.class );
		PaletteClassesHarmony.addClass( GBrightingPentad.class );
		PaletteClassesHarmony.addClass( GHarmonyCutter.class );
		PaletteClassesHarmony.addClass( GHarmonyStackingThirds.class );
		PaletteClassesHarmony.addClass( GGroupHarmony.class );
		PaletteClassesHarmony.addClass( GHarmonyMerge.class );
		PaletteClassesHarmony.addClass( GHarmonyPlayer.class );
		PaletteClassesHarmony.addClass( GHarmonyInterval.class );
		PaletteClassesHarmony.addClass( GHarmonyPowerChord.class );
		PaletteClassesHarmony.addClass( GHarmonyMelodicIntervalDoubling.class );
		
		
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
			
			ClasspathManager.removeClass( GHarmonyPrinter.class );
			
			PaletteClassesHarmony.removeClass( GHarmonyPrinter.class );
			PaletteClassesHarmony.removeClass( GTestTriad.class );
			PaletteClassesHarmony.removeClass( GBrightingPentad.class );
			PaletteClassesHarmony.removeClass( GHarmonyCutter.class );
			PaletteClassesHarmony.removeClass( GHarmonyStackingThirds.class );
			PaletteClassesHarmony.removeClass( GGroupHarmony.class );
			PaletteClassesHarmony.removeClass( GHarmonyMerge.class );
			PaletteClassesHarmony.removeClass( GHarmonyPlayer.class );
			PaletteClassesHarmony.removeClass( GHarmonyPowerChord.class );
			PaletteClassesHarmony.removeClass( GHarmonyMelodicIntervalDoubling.class );
			
			
			int core = 0;
			SongData.handleWaveChange(core);
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
	}

}
