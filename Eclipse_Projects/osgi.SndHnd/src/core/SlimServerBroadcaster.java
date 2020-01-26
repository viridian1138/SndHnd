




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


import java.io.*;
import java.util.*;
import java.net.*;



/**
 * Class for broadcasting audio files to a SlimDevices SlimServer under program control.  It should be noted that
 * this class wouldn't have happened without the help of the people on the SlimDevices forum.
 * 
 * Note: the hardware for which this class was written is no longer in production.
 * 
 * @author thorngreen
 *
 */
public class SlimServerBroadcaster {
	
	/**
	 * The list of informational strings collected from the SlimServer.
	 */
	protected Vector<String> vect = new Vector<String>();
	
	/**
	 * Incrementing number used to ensure that each file copied to the shared area has a unique name.
	 */
	protected static int sndHndVal = 0;
	
	
	/**
	 * Scans information coming back from the SlimServer and places it into a vector.
	 * 
	 * @author tgreen
	 *
	 */
	protected class ReadRun implements Runnable
	{
		/**
		 * The stream coming back from the SlimServer.
		 */
		protected LineNumberReader li;
		
		/**
		 * Constructor.
		 * @param _li The stream coming back from the SlimServer.
		 */
		public ReadRun( LineNumberReader _li )
		{
			li = _li;
		}
		
		
		/**
		 * Scans information coming back from the SlimServer and places it into a vector.
		 */
		public void run()
		{
			try
			{
				String line = li.readLine();
				while( line != null )
				{
					vect.add( line );
					System.out.println("*** "+line);
					line = li.readLine();
				}
				li.close();
			}
			catch( Throwable ex )
			{
				if( !( ex instanceof SocketException ) )
				{
					ex.printStackTrace( System.out );
				}
			}
		}
	}
	
	
	/**
	 * Constructor.
	 */
	public SlimServerBroadcaster()
	{
		
	}
	
	
	/**
	 * Gets the Mac address of the SlimDevices player (usually a SlimDevices Squeezebox) from the responses provided by the local SlimServer.
	 * @return The Mac address of the SlimDevices player.
	 * @throws Throwable
	 */
	protected String getMacAddress() throws Throwable
	{
		String ret = null;
		Thread.sleep( 500 );
		int sz = vect.size();
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			String str = vect.elementAt( count );
			StringTokenizer tok = new StringTokenizer( str , " " );
			while( tok.hasMoreTokens() )
			{
				String token = tok.nextToken();
				if( token.startsWith( "playerid%3A" ) )
				{
					ret = token.substring(11);
				}
			}
		}
		return( ret );
	}
	
	
	/**
	 * Copies an audio file to a shared location, and then broadcasts the shared location to a SlimServer to be played.
	 * @param sound The file to be played.
	 * @throws Throwable
	 */
	public void broadcastToSlimServer( File sound ) throws Throwable
	{
		
		String filePath = "/Users/shared/SndHnd" + sndHndVal + ".wav";
		
		System.out.println( filePath );
		
		sndHndVal++;
		
		File fi = new File( filePath );

		
		
		FileInputStream is = new FileInputStream( sound );
		FileOutputStream os = new FileOutputStream( fi );
		
		byte[] buffer = new byte[ 8192 ];
		
		int length;
		while( ( length = is.read( buffer ) ) > 0 )
		{
			os.write(buffer, 0, length);
		}
		
		is.close();
		os.close();
		
		
		
		URL url = fi.toURL();
		
		String uName = "" + url;
		
		System.out.println( uName );
		
		broadcastToSlimServer( uName );
	}

	
	/**
	 * Broadcasts a URL to the SlimServer to be played.
	 * @param urlString The URL to be played.
	 * @throws Throwable
	 */
	public void broadcastToSlimServer( String urlString ) throws Throwable
	{
		
		int port = 9090;
		System.out.println("Addr");
		InetAddress address = InetAddress.getLocalHost();
		System.out.println("Sock");
		Socket sock = new Socket(address, port);
		System.out.println("outs");
		PrintStream outs = new PrintStream(sock.getOutputStream());
		System.out.println("ins");
		LineNumberReader li = new LineNumberReader(new InputStreamReader(sock.getInputStream()));
		System.out.println("pname");
		ReadRun rr = new ReadRun(li);
		Thread thd = new Thread(rr);
		thd.start();
		System.out.println("sthread");
		outs.println("players 0 2");
		String macAddress = getMacAddress();
		if( macAddress == null )
		{
			throw(new RuntimeException("No Mac Address Found."));
		}
		System.out.println( "macAddress " + macAddress );
		
		outs.println( macAddress + " playlist play " + urlString );

		outs.close();
	}

	
}


