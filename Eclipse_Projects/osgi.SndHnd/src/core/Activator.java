




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

import javax.swing.SwingUtilities;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import verdantium.clmgr.ClasspathManager;
import verdantium.clmgr.ComponentManager;


public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		ClasspathManager.addClass( TestPlayer2.class );
		
		ClasspathManager.addClass( InterpolationPoint.class );
				
		ComponentManager.addClass( TestPlayer2.class , "SndHnd" );

		
		System.out.println( "Starting..." );
		
		final Runnable runn = new Runnable()
		{
			public void run()
			{
				try
				{
					String[] str = {};
					TestPlayer2.main( str );
				}
				catch( Throwable ex )
				{
					ex.printStackTrace( System.out );
				}
			}
		};
		
		SwingUtilities.invokeLater( runn );
		
		System.out.println( "Started..." );
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		ClasspathManager.removeClass( TestPlayer2.class );
		
		ClasspathManager.removeClass( InterpolationPoint.class );
		
		ComponentManager.removeClass( TestPlayer2.class , "SndHnd" );
		
		
	}

}
