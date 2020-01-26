




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



import java.util.ArrayList;
import java.util.Iterator;



/**
 * Stores the set of relevant listener instances, and provides methods for distributing change events to relevant listeners.
 * 
 * @author tgreen
 *
 */
public class SongListeners {
	
	

	/**
	 * The set of view panes for editing the set of notes.
	 */
	public static ArrayList<NoteViewPane2> noteViewPanes = new ArrayList<NoteViewPane2>();
	
	/**
	 * The set of view panes for viewing waveforms.
	 */
	public static ArrayList<WaveViewPane> waveViewPanes = new ArrayList<WaveViewPane>();
	
	/**
	 * The set of view panes for editing track volumes and note envelopes.
	 */
	public static ArrayList<VolumeViewPane2> volumeViewPanes = new ArrayList<VolumeViewPane2>();
	
	/**
	 * The set of view panes for editing the tempo.
	 */
	public static ArrayList<TempoViewPane2> tempoViewPanes = new ArrayList<TempoViewPane2>();
	
	/**
	 * The set of view panes for editing "generic" vibrato (which could include either pitch vibrato or tremolo).
	 */
	public static ArrayList<VibratoViewPane2> vibratoViewPanes = new ArrayList<VibratoViewPane2>();
	
	/**
	 * The set of view panes for editing the set of measures.
	 */
	public static ArrayList<MeasureViewPane> measuresViewPanes = new ArrayList<MeasureViewPane>();
	
	/**
	 * The set of tenori view panes.
	 */
	public static ArrayList<TenoriViewPane> tenoriViewPanes = new ArrayList<TenoriViewPane>();
	
	/**
	 * The set of viee panes for editing the set of track frames.
	 */
	public static ArrayList<TrackFrameViewPane> trackFrameViewPanes = new ArrayList<TrackFrameViewPane>();
	
	
	
	/**
	 * Notifies relevate view panes of a change in at least one pitch.
	 */
	public static void updateViewPanesPitchChange()
	{
		{
			Iterator<NoteViewPane2> it = noteViewPanes.iterator();
			while( it.hasNext() )
			{
				NoteViewPane2 noteViewPane = it.next();
				noteViewPane.refreshDisplayList();
				noteViewPane.repaint();
			}
		}
		{
			Iterator<WaveViewPane> it = waveViewPanes.iterator();
			while( it.hasNext() )
			{
				WaveViewPane waveViewPane = it.next();
				waveViewPane.refreshDisplayList();
				waveViewPane.repaint();
			}
		}
	}
	
	
	/**
	 * Notifies relevant view panes of a change in at least one track volume.
	 */
	public static void updateViewPanesVolumeChange()
	{
		{
			Iterator<WaveViewPane> it = waveViewPanes.iterator();
			while( it.hasNext() )
			{
				WaveViewPane waveViewPane = it.next();
				waveViewPane.refreshDisplayList();
				waveViewPane.repaint();
			}
		}
		{
			Iterator<VolumeViewPane2> it = volumeViewPanes.iterator();
			while( it.hasNext() )
			{
				VolumeViewPane2 volumeViewPane = it.next();
				volumeViewPane.refreshDisplayList();
				volumeViewPane.repaint();
			}
		}
	}
	
	
	/**
	 * Notifies relevant view panes of a change in at least one note.
	 */
	public static void updateViewPanesNoteChange()
	{
		{
			Iterator<NoteViewPane2> it = noteViewPanes.iterator();
			while( it.hasNext() )
			{
				NoteViewPane2 noteViewPane = it.next();
				noteViewPane.refreshDisplayList();
				noteViewPane.repaint();
			}
		}
		{
			Iterator<WaveViewPane> it = waveViewPanes.iterator();
			while( it.hasNext() )
			{
				WaveViewPane waveViewPane = it.next();
				waveViewPane.refreshDisplayList();
				waveViewPane.repaint();
			}
		}
		{
			Iterator<VolumeViewPane2> it = volumeViewPanes.iterator();
			while( it.hasNext() )
			{
				VolumeViewPane2 volumeViewPane = it.next();
				volumeViewPane.refreshDisplayList();
				volumeViewPane.repaint();
			}
		}
	}
	
	
	/**
	 * Sends updates to all of the view panes.
	 */
	public static void updateViewPanes() {
		{
			Iterator<NoteViewPane2> it = noteViewPanes.iterator();
			while( it.hasNext() )
			{
				NoteViewPane2 noteViewPane = it.next();
				noteViewPane.refreshDisplayList();
				noteViewPane.repaint();
			}
		}
		{
			Iterator<WaveViewPane> it = waveViewPanes.iterator();
			while( it.hasNext() )
			{
				WaveViewPane waveViewPane = it.next();
				waveViewPane.refreshDisplayList();
				waveViewPane.repaint();
			}
		}
		{
			Iterator<VolumeViewPane2> it = volumeViewPanes.iterator();
			while( it.hasNext() )
			{
				VolumeViewPane2 volumeViewPane = it.next();
				volumeViewPane.refreshDisplayList();
				volumeViewPane.repaint();
			}
		}
		{
			Iterator<TempoViewPane2> it = tempoViewPanes.iterator();
			while( it.hasNext() )
			{
				TempoViewPane2 tempoViewPane = it.next();
				tempoViewPane.refreshDisplayList();
				tempoViewPane.repaint();
			}
		}
		{
			Iterator<VibratoViewPane2> it = vibratoViewPanes.iterator();
			while( it.hasNext() )
			{
				VibratoViewPane2 vibratoViewPane = it.next();
				vibratoViewPane.refreshDisplayList();
				vibratoViewPane.repaint();
			}
		}
		{
			Iterator<MeasureViewPane> it = measuresViewPanes.iterator();
			while( it.hasNext() )
			{
				MeasureViewPane measureViewPane = it.next();
				measureViewPane.refreshMeasures();
				measureViewPane.repaint();
			}
		}
		{
			Iterator<TrackFrameViewPane> it = trackFrameViewPanes.iterator();
			while( it.hasNext() )
			{
				TrackFrameViewPane trackFrameViewPane = it.next();
				trackFrameViewPane.refreshTrackFrames();
				trackFrameViewPane.repaint();
			}
		}
		{
		Iterator<TenoriViewPane> it = tenoriViewPanes.iterator();
		while( it.hasNext() )
		{
			TenoriViewPane tenoriViewPane = it.next();
			tenoriViewPane.refreshKey();
		}
		}
	}

}


