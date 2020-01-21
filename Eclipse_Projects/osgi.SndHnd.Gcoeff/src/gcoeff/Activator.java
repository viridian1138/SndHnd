





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







package gcoeff;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import core.SongData;

import palettes.PaletteClassesCoeff;
import verdantium.clmgr.ClasspathManager;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		try
		{
		ClasspathManager.addClass( GReverb.class );
		PaletteClassesCoeff.addClass( GOversamplingCoefficient.class );
		PaletteClassesCoeff.addClass( GMicroPhaseAdjustment.class );
		PaletteClassesCoeff.addClass( GMicroPhaseAdjustmentCv.class );
		PaletteClassesCoeff.addClass( GHighPassFilter.class );
		PaletteClassesCoeff.addClass( GLowPassFilter.class );
		PaletteClassesCoeff.addClass( GReverb.class );
		PaletteClassesCoeff.addClass( GRoughDraftCoeffSwitch.class );
		PaletteClassesCoeff.addClass( GRoughDraftSkipDistortion.class );
		PaletteClassesCoeff.addClass( GGroupCoeff.class );
		
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
		
		try
		{
		ClasspathManager.removeClass( GReverb.class );
		PaletteClassesCoeff.removeClass( GOversamplingCoefficient.class );
		PaletteClassesCoeff.removeClass( GMicroPhaseAdjustment.class );
		PaletteClassesCoeff.removeClass( GMicroPhaseAdjustmentCv.class );
		PaletteClassesCoeff.removeClass( GHighPassFilter.class );
		PaletteClassesCoeff.removeClass( GLowPassFilter.class );
		PaletteClassesCoeff.removeClass( GReverb.class );
		PaletteClassesCoeff.removeClass( GRoughDraftCoeffSwitch.class );
		PaletteClassesCoeff.removeClass( GRoughDraftSkipDistortion.class );
		PaletteClassesCoeff.removeClass( GGroupCoeff.class );
		
		int core = 0;
		SongData.handleWaveChange(core);
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
		
	}

}
