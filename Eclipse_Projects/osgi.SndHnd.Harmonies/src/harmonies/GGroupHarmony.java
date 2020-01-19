





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

import gredit.GNode;
import greditharmon.EditPackHarmony;
import greditharmon.GHarmony;
import grview.DraggableTransformNodeTest;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;

import aazon.builderNode.BuilderNode;
import meta.DataFormatException;
import meta.VersionBuffer;
import palettes.PaletteClassesHarmony;
import verdantium.undo.UndoManager;
import aczon.AczonUnivAllocator;
import core.InstrumentTrack;
import core.Harmony;

/**
 * Node for a group of objects contributing to a larger harmony.
 * @author tgreen
 *
 */
public class GGroupHarmony extends GHarmony implements Externalizable {
	
	/**
	 * The child input harmony.
	 */
	GHarmony chld = null;
	
	/**
	 * The edit pack for editing the harmony.
	 */
	protected EditPackHarmony edit = new EditPackHarmony();
	
	/**
	 * The name of the harmony group.
	 */
	protected String name = "Group -- Harmony";
	
	
	@Override
	public Harmony genHarmony(HashMap s) {
		return( edit.processHarmony( s ) );
	}
	
	@Override
	public String[] getHarmonyNames() {
		return( edit.getHarmonyNames( ) );
	}

	@Override
	public String getName() {
		return( name );
	}
	
	/**
	 * Sets the name of the intonation group.
	 * @param in The name of the intonation group.
	 */
	public void setName( String in )
	{
		name = in;
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GHarmony );
	}

	@Override
	public void performAssign(GNode in) {
		setChld( (GHarmony) in );
	}

	@Override
	public void removeChld() {
		setChld( null );
	}

	public Object getChldNodes() {
		return( chld );
	}
	
	/**
	 * Sets the child input harmony.
	 * @param in The child input harmony.
	 */
	private void setChld( GHarmony in )
	{
		chld = in;
		edit.getHarmonyIn().setHarmony( in );
	}
	
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		HashSet<BuilderNode> s = edit.getElem();
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
		if( ( track == null ) || ( undoMgr == null ) )
		{
			throw( new RuntimeException( "TrkIsNull" ) );
		}
		Object univ1 = AczonUnivAllocator.allocateUniv();
		Object univ2 = AczonUnivAllocator.allocateUniv();
		DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesHarmony());
	}

	/**
	 * Gets the edit pack for editing the harmony.
	 * @return The edit pack for editing the harmony.
	 */
	public EditPackHarmony getEdit() {
		return edit;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setProperty("Edit", edit);
		myv.setProperty("Name", name);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			setChld( (GHarmony)( myv.getProperty("Chld") ) );
			edit = (EditPackHarmony)( myv.getProperty("Edit") );
			name = (String)( myv.getProperty("Name") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	

}

