




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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.swing.JComponent;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import jundo.runtime.ExtMilieuRef;
import labdaw.undo.pdx_VolumeViewPaneModel_pdx_ObjectRef;
import labdaw.undo.pdx_VolumeViewPaneModel_pdx_PairRef;
import verdantium.undo.UTag;
import verdantium.undo.UndoManager;
import aazon.AazonEnt;
import aazon.AazonImmutableFilledRectangle;
import aazon.AazonImmutableGroup;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonListener;
import aazon.AazonMutableGroup;
import aazon.AazonSmartBlockText;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartOutlineRectangle;
import aazon.dbl.AazonBaseImmutableDbl;
import aazon.dbl.AazonBaseMutableDbl;
import aazon.dbl.AazonMutableDbl;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonVect;
import abzon.AbzonImmutableGeneralPathFactory;
import abzon.AbzonImmutableShape;
import abzon.AbzonMutableGeneralPathFactory;
import abzon.AbzonSmartShape;
import aczon.AczonColor;
import aczon.AczonCoordinateConvert;
import aczon.AczonResizeVect;
import aczon.AczonRootFactory;

public class WaveViewPane extends JComponent implements MouseListener,
		MouseMotionListener , PropertyChangeListener {
	
	
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! track display / update modes !!!!!!!!!!!!!!!!!!!!!!!!
	
	// track xy to u for mouse zoom. !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	
	protected AczonRootFactory rootFactory = null;
	
	protected AazonMutableGroup bkgndLineLightGray = new AazonMutableGroup();
	
	protected AazonMutableGroup bkgndDarkCyan = new AazonMutableGroup();
	
	protected AazonMutableGroup frontGreen = new AazonMutableGroup();
	
	protected AazonMutableGroup frontMagenta = new AazonMutableGroup();
	
	protected AazonMutableGroup frontDarkYellow = new AazonMutableGroup();
	
	protected AazonMutableGroup frontOrange = new AazonMutableGroup();
	
	protected AazonMutableGroup backSprites = new AazonMutableGroup();
	
	protected AazonMutableGroup frontSprites = new AazonMutableGroup();
	
	protected AazonMutableGroup frontTextBlack = new AazonMutableGroup();
	
	
	protected final AazonBaseMutableVect rectAVectA = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	protected final AazonBaseMutableVect rectAVectB = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	protected final AazonBaseMutableVect rectBVectA = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	protected final AazonBaseMutableVect rectBVectB = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	
	protected AazonMutableDbl flatness = new AazonBaseMutableDbl( 0.01 ); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	protected AczonResizeVect resizeVect = new AczonResizeVect()
	{
		protected double getInnerX() {
			return (400);
		}

		protected double getInnerY() {
			return (400);
		}
	};
	
	protected AazonListener resizeListener = null;
	
	

	protected double startBeatNumber;

	protected double endBeatNumber;

	protected double startLevel;

	protected double endLevel;
	
	protected pdx_VolumeViewPaneModel_pdx_ObjectRef model = null;

	protected static final double VERT_STRT_PCT = 0.0;

	protected static final double HORIZ_STRT_PCT = 0.2;

	protected static final double VERT_END_PCT = 0.8;

	protected static final double HORIZ_END_PCT = 1.0;

	protected static final int CRV_STROKE_SIZE = 100;

	protected int editMode = EDIT_MODE_ZOOM;

	protected boolean editTrackCurve = false;

	protected boolean mouseDragEnabled = false;

	protected ControlSelectNode lastDragDesc = null;
	
	protected TrackInterpRect lastDragTrack = null;

	protected int mouseDragMode = 0;

	protected double origX = 0;

	protected double origY = 0;

	protected double newX = 0;

	protected double newY = 0;

	protected final ArrayList<Double> volLevelList = new ArrayList<Double>();
	
	protected final ArrayList<ControlSelectNode> controlSelectList = new ArrayList<ControlSelectNode>();
	
	protected UndoManager undoMgr = null;

	protected static final Color DARK_CYAN = new Color(0, 192, 192);

	protected static final Color DARK_YELLOW = new Color(192, 192, 0);

	public static final int EDIT_MODE_ZOOM = 0;
	
	public static final int EDIT_MODE_ZOOM_X = 1;
	
	public static final int EDIT_MODE_ZOOM_Y = 2;


	public WaveViewPane( UndoManager _undoMgr ) {
		super();
		undoMgr = _undoMgr;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		pdx_VolumeViewPaneModel_pdx_PairRef pair = pdx_VolumeViewPaneModel_pdx_ObjectRef.pdxm_new_VolumeViewPaneModel(
				mil , 0.0 , 0.0 , 0.0 , 0.0 );
		mil = pair.getMilieu();
		model = (pdx_VolumeViewPaneModel_pdx_ObjectRef)( pair.getObject() );
		undoMgr.handleCommitTempChange( mil );
		undoMgr.addPropertyChangeListener( this );
	}
	
	/**
	* Handles property change events.
	*/
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == UndoManager.MODEL_STATE_CHANGED) {
			handleUndoStateChange();
		}

	}
	
	public void handleUndoStateChange()
	{
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		startBeatNumber = model.pdxm_getStartBeatNumber( mil );
		endBeatNumber = model.pdxm_getEndBeatNumber( mil );
		startLevel = model.pdxm_getStartVolume( mil );
		endLevel = model.pdxm_getEndVolume( mil );
		refreshDisplayList();
	}

	
	

protected void refreshDisplayList() {
		
		//ArrayList bkgndDarkCyanLst = new ArrayList();
		ArrayList<AazonEnt> frontGreenLst = new ArrayList<AazonEnt>();
		//ArrayList frontMagentaLst = new ArrayList();
		//ArrayList frontDarkYellowLst = new ArrayList();
		//ArrayList frontOrangeLst = new ArrayList();
		backSprites.setGrp( new AazonImmutableGroup() );
		frontSprites.setGrp( new AazonImmutableGroup() );
		ArrayList<AazonEnt> frontTxtBlack = new ArrayList<AazonEnt>();
		
		

		InstrumentTrack curTr = SongData.getCurrentTrack();


		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


		ArrayList<Point3d> points = new ArrayList<Point3d>();

		final int core = 0;

		
			if (endBeatNumber - startBeatNumber > 10) {
				double delta = (endBeatNumber - startBeatNumber) / 10.0;
				double count;
				for (count = startBeatNumber; count < endBeatNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					double qbeat = iCount;
					String str = "" + iCount;
					calcUFromBeat(qbeat);
					calcXYFromU(startBeatU, levelU);
					double x1 = beatX;
					Point3d lineSt = new Point3d(x1, -calcYRatio(), 0.0);
					Point3d lineEnd = new Point3d(x1, calcYRatio(), 0.0);
					points.add(lineSt);
					points.add(lineEnd);
					
					AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( x1 - 0.05 , 0.9 * ( calcYRatio() ) + 0.1 * ( -calcYRatio() ) ) , 
							str, AczonColor.getTextBlack(), "Helvetica", 8, Font.PLAIN, false);
					frontTxtBlack.add( tx );
					
				}
			} else {
				double delta = (endBeatNumber - startBeatNumber) / 10.0;
				double count;
				for (count = startBeatNumber; count < endBeatNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					int seg = (int) (1000.0 * (count - iCount));
					double qbeat = count;
					String str = "" + iCount + "." + seg;
					calcUFromBeat(qbeat);
					calcXYFromU(startBeatU, levelU);
					double x1 = beatX;
					Point3d lineSt = new Point3d(x1, -calcYRatio(), 0.0);
					Point3d lineEnd = new Point3d(x1, calcYRatio(), 0.0);
					points.add(lineSt);
					points.add(lineEnd);
					
					AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( x1 - 0.05 , 0.9 * ( calcYRatio() ) + 0.1 * ( -calcYRatio() ) ) , 
							str, AczonColor.getTextBlack(), "Helvetica", 8, Font.PLAIN, false);
					frontTxtBlack.add( tx );
					
				}
			}

		volLevelList.clear();

		{
		double delta = (endLevel - startLevel) / 10.0;
		double count;

		for (count = startLevel; count < endLevel; count = count + delta) {
			volLevelList.add(new Double(count));
			String str = "" + count;
			
			double level = count;
			calcUFromLevel(level);
			calcXYFromU(startBeatU, levelU);
			double y1 = levelY;

			Point3d lineSt = new Point3d(-1.0, y1, 0.0);
			Point3d lineEnd = new Point3d(1.0, y1, 0.0);
			points.add(lineSt);
			points.add(lineEnd);
			
			AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( -0.95 , y1 - 0.05 ) , 
					str, AczonColor.getTextBlack(), "Helvetica", 15, Font.PLAIN, false);
			frontTxtBlack.add( tx );
			
		}
		}
		

		int count;
		
		int sz;
		
		updateBkgndLineLightGray( points );
		
		
		
		
		
		controlSelectList.clear();
		
		
		GeneralPath pointsa = new GeneralPath();
		calcInsTrackPoints(curTr, pointsa);
		
		AbzonImmutableGeneralPathFactory fac = new AbzonImmutableGeneralPathFactory( pointsa );

		frontGreenLst.add( AbzonSmartShape.construct( fac , flatness, new AffineTransform(), AczonColor.getLineGreen() , false) );
		
		
		//bkgndDarkCyan.setGrp( new AazonImmutableGroup( bkgndDarkCyanLst ) );
		frontGreen.setGrp( new AazonImmutableGroup( frontGreenLst ) );
		//frontMagenta.setGrp( new AazonImmutableGroup( frontMagentaLst ) );
		//frontDarkYellow.setGrp( new AazonImmutableGroup( frontDarkYellowLst ) );
		//frontOrange.setGrp( new AazonImmutableGroup( frontOrangeLst ) );
		frontTextBlack.setGrp( new AazonImmutableGroup( frontTxtBlack ) );
		
		rectAVectA.setCoords( -1.0 , calcYRatio() );
		
		rectAVectB.setCoords( 1.0 ,  ( calcYRatio() ) * 0.8 + -( calcYRatio() ) * 0.2 );
		
		rectBVectA.setCoords( -1.0 , -calcYRatio() );
		
		rectBVectB.setCoords( -1.0 * 0.8 + 1.0 * 0.2 , calcYRatio() );
		
		
	}

	
public void buildDisplayList() {
	
	setLayout(new BorderLayout(0,0));
	AczonRootFactory scene = createSceneGraph();

	add(BorderLayout.CENTER, scene.getCanvas() );
	
	final AazonListener al = new AazonListener()
	{

		public void handleListen() {
			refreshDisplayList();
		}
		
	};
	resizeListener = al;
	resizeVect.setFactory( scene );
	scene.getCanvas().addResizeVect( resizeVect );
	resizeVect.add( resizeListener );
}

	protected AczonRootFactory createSceneGraph() {
		
		ArrayList<AazonEnt> frontGreenLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontMagentaLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontDarkYellowLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontOrangeLst = new ArrayList<AazonEnt>();
		backSprites.setGrp( new AazonImmutableGroup() );
		frontSprites.setGrp( new AazonImmutableGroup() );
		ArrayList<AazonEnt> frontTxtBlack = new ArrayList<AazonEnt>();
		

		InstrumentTrack curTr = SongData.getCurrentTrack();

		

		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		
		ArrayList<Point3d> points = new ArrayList<Point3d>();

		final int core = 0;

			if (endBeatNumber - startBeatNumber > 10) {
				double delta = (endBeatNumber - startBeatNumber) / 10.0;
				double count;
				for (count = startBeatNumber; count < endBeatNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					double qbeat = iCount;
					String str = "" + iCount;
					calcUFromBeat(qbeat);
					calcXYFromU(startBeatU, levelU);
					double x1 = beatX;
					Point3d lineSt = new Point3d(x1, -calcYRatio(), 0.0);
					Point3d lineEnd = new Point3d(x1, calcYRatio(), 0.0);
					points.add(lineSt);
					points.add(lineEnd);
					
					AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( x1 - 0.05 , 0.9 * ( calcYRatio() ) + 0.1 * ( -calcYRatio() ) ) , 
							str, AczonColor.getTextBlack(), "Helvetica", 8, Font.PLAIN, false);
					frontTxtBlack.add( tx );
				}
			} else {
				double delta = (endBeatNumber - startBeatNumber) / 10.0;
				double count;
				for (count = startBeatNumber; count < endBeatNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					int seg = (int) (1000.0 * (count - iCount));
					double qbeat = count;
					String str = "" + iCount + "." + seg;
					calcUFromBeat(qbeat);
					calcXYFromU(startBeatU, levelU);
					double x1 = beatX;
					Point3d lineSt = new Point3d(x1, -calcYRatio(), 0.0);
					Point3d lineEnd = new Point3d(x1, calcYRatio(), 0.0);
					points.add(lineSt);
					points.add(lineEnd);
					
					AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( x1 - 0.05 , 0.9 * ( calcYRatio() ) + 0.1 * ( -calcYRatio() ) ) , 
							str, AczonColor.getTextBlack(), "Helvetica", 8, Font.PLAIN, false);
					frontTxtBlack.add( tx );
				}
			}

		volLevelList.clear();

		{
		double delta = (endLevel - startLevel) / 10.0;
		double count;

		for (count = startLevel; count < endLevel; count = count + delta) {
			volLevelList.add(new Double(count));
			String str = "" + count;
			
			double level = count;
			calcUFromLevel(level);
			calcXYFromU(startBeatU, levelU);
			double y1 = levelY;

			Point3d lineSt = new Point3d(-1.0, y1, 0.0);
			Point3d lineEnd = new Point3d(1.0, y1, 0.0);
			points.add(lineSt);
			points.add(lineEnd);
			
			AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( -0.95 , y1 - 0.05 ) , 
					str, AczonColor.getTextBlack(), "Helvetica", 15, Font.PLAIN, false);
			frontTxtBlack.add( tx );
		}
		}
		
		int count;
		int sz;

		updateBkgndLineLightGray( points );

		
		
		
		controlSelectList.clear();
		
		
		GeneralPath pointsa = new GeneralPath();
		calcInsTrackPoints(curTr, pointsa);
		
		AbzonImmutableGeneralPathFactory fac = new AbzonImmutableGeneralPathFactory( pointsa );
		

		frontGreenLst.add( AbzonSmartShape.construct( fac , flatness, new AffineTransform(), AczonColor.getLineGreen() , false) );
		
		
		frontGreen.setGrp( new AazonImmutableGroup( frontGreenLst ) );
		frontMagenta.setGrp( new AazonImmutableGroup( frontMagentaLst ) );
		frontDarkYellow.setGrp( new AazonImmutableGroup( frontDarkYellowLst ) );
		frontOrange.setGrp( new AazonImmutableGroup( frontOrangeLst ) );
		frontTextBlack.setGrp( new AazonImmutableGroup( frontTxtBlack ) );
		
		
		rectAVectA.setCoords( -1.0 , calcYRatio() );
		
		rectAVectB.setCoords( 1.0 ,  ( calcYRatio() ) * 0.8 + -( calcYRatio() ) * 0.2 );
		
		rectBVectA.setCoords( -1.0 , -calcYRatio() );
		
		rectBVectB.setCoords( -1.0 * 0.8 + 1.0 * 0.2 , calcYRatio() );
		
		
		AazonEnt rectA = AazonSmartFilledRectangle.construct( 
				rectAVectA , rectAVectB ,
				AczonColor.getFillWhite() , false );
		
		AazonEnt rectB = AazonSmartFilledRectangle.construct( 
				rectBVectA , rectBVectB ,
				AczonColor.getFillWhite() , false );

		
		
		
		AazonEnt[] objOrderEnts = { bkgndLineLightGray , bkgndDarkCyan , frontGreen , frontMagenta , frontDarkYellow
				, frontOrange , backSprites , frontSprites  , rectA , rectB , frontTextBlack };
		final AazonImmutableOrderedGroup objOrderEnt = new AazonImmutableOrderedGroup( objOrderEnts );
		
		rootFactory = new AczonRootFactory( objOrderEnt );
		
		
		rootFactory.getCanvas().addMouseListener(this);
		rootFactory.getCanvas().addMouseMotionListener(this);

		return (rootFactory);
	}
	
	
	protected void updateBkgndLineLightGray( ArrayList<Point3d> points )
	{
		int sz = points.size();
		GeneralPath gp = new GeneralPath();
		int count;
		if (sz > 0) {
			for (count = 0; count < ( sz - 1 ); count+=2 ) {
				Point3d p0 = points.get(count);
				Point3d p1 = points.get(count+1);
				gp.moveTo( (float)( p0.x ) , (float)( p0.y ) );
				gp.lineTo( (float)( p1.x ) , (float)( p1.y ) );
			}

		}
		AbzonImmutableGeneralPathFactory ig = new AbzonImmutableGeneralPathFactory( gp );
		AbzonImmutableShape sp = new AbzonImmutableShape( ig , new AazonBaseImmutableDbl( 0.001 ) , new AffineTransform() , AczonColor.getLineLightGray() );
		AazonEnt[] ents = { sp };
		AazonImmutableGroup igrp = new AazonImmutableGroup( ents );
		bkgndLineLightGray.setGrp( igrp );
	}
	
	
	public static class ControlSelectNode
	{
		int id;
		Point3f ctrlPoint;
		NoteDesc desc;
		InstrumentTrack track;
		AbzonMutableGeneralPathFactory controllingLineChild;
		AazonBaseMutableVect controlPointVect;
		Appearance lineApp;
		InterpolationPoint pt;
		ArrayList<AazonBaseMutableVect> controlPointsLocal;
		
		public ControlSelectNode( int _id,
				Point3f _ctrlPoint,
				NoteDesc _desc,
				InstrumentTrack _track,
				AbzonMutableGeneralPathFactory _controllingLineChild,
				AazonBaseMutableVect _controlPointVect,
				Appearance _lineApp,
				InterpolationPoint _pt,
				ArrayList<AazonBaseMutableVect> _controlPointsLocal )
		{
			id = _id;
			ctrlPoint = _ctrlPoint;
			desc = _desc;
			track = _track;
			controllingLineChild = _controllingLineChild;
			controlPointVect = _controlPointVect;
			lineApp = _lineApp;
			pt = _pt;
			controlPointsLocal = _controlPointsLocal;
		}
		
		
		public double calcDist( Point3d in )
		{
			double ret = Math.abs( ctrlPoint.x - in.x ) +
				Math.abs( ctrlPoint.y - in.y );
			return( ret );
		}
		
		
		/**
		 * @return Returns the controllingLineChild.
		 */
		public AbzonMutableGeneralPathFactory getControllingLineChild() {
			return controllingLineChild;
		}
		/**
		 * @param controllingLineChild The controllingLineChild to set.
		 */
		public void setControllingLineChild(AbzonMutableGeneralPathFactory controllingLineChild) {
			this.controllingLineChild = controllingLineChild;
		}
		/**
		 * @return Returns the controlPointVect.
		 */
		public AazonBaseMutableVect getControlPointVect() {
			return controlPointVect;
		}
		/**
		 * @return Returns the ctrlPoint.
		 */
		public Point3f getCtrlPoint() {
			return ctrlPoint;
		}
		/**
		 * @param ctrlPoint The ctrlPoint to set.
		 */
		public void setCtrlPoint(Point3f ctrlPoint) {
			this.ctrlPoint = ctrlPoint;
		}
		/**
		 * @return Returns the desc.
		 */
		public NoteDesc getDesc() {
			return desc;
		}
		/**
		 * @param desc The desc to set.
		 */
		public void setDesc(NoteDesc desc) {
			this.desc = desc;
		}


		/**
		 * @return Returns the pt.
		 */
		public InterpolationPoint getPt() {
			return pt;
		}


		/**
		 * @param pt The pt to set.
		 */
		public void setPt(InterpolationPoint pt) {
			this.pt = pt;
		}


		/**
		 * @return Returns the id.
		 */
		public int getId() {
			return id;
		}


		/**
		 * @param id The id to set.
		 */
		public void setId(int id) {
			this.id = id;
		}


		/**
		 * @return Returns the lineApp.
		 */
		public Appearance getLineApp() {
			return lineApp;
		}


		/**
		 * @param lineApp The lineApp to set.
		 */
		public void setLineApp(Appearance lineApp) {
			this.lineApp = lineApp;
		}


		/**
		 * @return the controlPointsLocal
		 */
		public ArrayList<AazonBaseMutableVect> getControlPointsLocal() {
			return controlPointsLocal;
		}


		/**
		 * @return the track
		 */
		public InstrumentTrack getTrack() {
			return track;
		}


		/**
		 * @param track the track to set
		 */
		public void setTrack(InstrumentTrack track) {
			this.track = track;
		}

		
	}
	
	protected void calcInsTrackPoints(InstrumentTrack track, GeneralPath out)
	{
		final int max = 800;
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			double noteU = ((double) count) / 799;
			calcUValuesActual(track, noteU);
			double beatU = (1 - noteU) * startBeatU + noteU * endBeatU;
			calcXYFromU(beatU, levelU);
			double x1 = beatX;
			double y1 = levelY;

			if( count == 0 )
			{
				out.moveTo( (float)x1, (float)y1);
			}
			else
			{
				out.lineTo( (float)x1, (float)y1);
			}
		}
	}
	

	protected boolean isWithinRange(NoteDesc desc) {
		double startBeat = desc.getStartBeatNumber();
		double endBeat = Math.max( desc.getEndBeatNumber(), desc.getActualEndBeatNumber());

		if ((startBeat < startBeatNumber) && (endBeat < startBeatNumber)) {
			return (false);
		}

		if ((startBeat > endBeatNumber) && (endBeat > endBeatNumber)) {
			return (false);
		}

		return (true);
	}

	protected double startBeatU;

	protected double endBeatU;

	protected double levelU;

	protected double beatX;

	protected double levelY;

	protected void calcUFromBeat(double startBeat) {
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void calcUFromLevel(double level) {
		levelU = (level - startLevel) / (endLevel - startLevel);
	}
	
	protected void calcUValuesActual(InstrumentTrack track, double noteU) {
		final int core = 0;
		double beat_number = (1-noteU)*startBeatNumber + noteU*endBeatNumber;
		double elapsed_time_seconds = SongData.getElapsedTimeForBeatBeat( beat_number , core );
		double totalWave = 0.0;
		if (track.isTrackOn()) {
			NonClampedCoefficient volumeCoeff = track.getTrackVolume(core);
			double volume = volumeCoeff.eval(beat_number);
			ArrayList<TrackFrame> frames = track.getTrackFrames();
			int cf;
			int sf = frames.size();
			for (cf = 0; cf < sf; cf++) {
				TrackFrame tr = frames.get(cf);
				NoteDesc noteDesc = tr.getNoteDescActual(beat_number,core);
				if (noteDesc != null) {
					// double frequency = noteDesc.getFrequency();
					// double wavesPerSecond = frequency;
					// double note_seconds =
					// getElapsedTimeForNoteSecondsActual( tr ,
					// elapsed_time_seconds );
					double u = TestPlayer2.getNoteUActual(tr, beat_number,core);
					// double waveNumber = ( elapsed_time_seconds -
					// note_seconds ) * wavesPerSecond;
					//double waveNumber = noteDesc.getWaveNumber(u);
					double waveNumber = noteDesc.getWaveNumberElapsedTimeSeconds(elapsed_time_seconds,core);
					double envelope = (noteDesc.getActualNoteEnvelope(core)
							.eval(u))
							* (noteDesc.getWaveEnvelope(core).eval(waveNumber));
					double ret = 0.0;
					if (envelope > 1E-20) {
						WaveForm wave = noteDesc.getWaveform(core);
						double sine = envelope * wave.eval(waveNumber);
						// ret = sine + envelope;
						ret = sine;
					}

					switch(noteDesc.getTotalEnvelopeMode()) {
					case NoteDesc.TOTAL_ENVELOPE_MODE_DRUM :
					{
						double multiplier = 1.0 - envelope
								* (noteDesc.getTotalEnvelopeCoeff());
						totalWave = totalWave * multiplier + ret * volume;
					}
					break;
					case NoteDesc.TOTAL_ENVELOPE_MODE_NONE :
						totalWave += ret * volume;
						break;
					case NoteDesc.TOTAL_ENVELOPE_MODE_MULT :
						totalWave += ret * volume * (noteDesc.getTotalEnvelopeCoeff());
						break;
					}
				}
			}
		}

		levelU = (totalWave - startLevel) / (endLevel - startLevel);
		startBeatU = 0.0;
		endBeatU = 1.0;
	}

	protected void updateStartBeat(double beatU, NoteDesc desc) {
		double beat = (1 - beatU) * startBeatNumber + beatU * endBeatNumber;
		if (beat < desc.getEndBeatNumber()) {
			desc.setStartBeatNumber(beat);
		}
	}

	protected void updateEndBeat(double beatU, NoteDesc desc) {
		double beat = (1 - beatU) * startBeatNumber + beatU * endBeatNumber;
		if (beat > desc.getStartBeatNumber()) {
			desc.setEndBeatNumber(beat);
		}
	}

	protected void updateZoom(double stBeatU, double stLevelU, double endBeatU,
			double endLevelU) {
		double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		double stBeat = (1 - stBeatU) * startBeatNumber + stBeatU
				* endBeatNumber;

		double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		double endBeat = (1 - endBeatU) * startBeatNumber + endBeatU
				* endBeatNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		setStartBeatNumber(stBeat);
		setEndBeatNumber(endBeat);
		setStartLevel(edLevel);
		setEndLevel(stLevel);
	}
	
	protected void updateZoomX(double stBeatU, double stLevelU, double endBeatU,
			double endLevelU) {
		//double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		double stBeat = (1 - stBeatU) * startBeatNumber + stBeatU
				* endBeatNumber;

		//double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		double endBeat = (1 - endBeatU) * startBeatNumber + endBeatU
				* endBeatNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		setStartBeatNumber(stBeat);
		setEndBeatNumber(endBeat);
		//setStartLevel(edLevel);
		//setEndLevel(stLevel);
	}
	
	protected void updateZoomY(double stBeatU, double stLevelU, double endBeatU,
			double endLevelU) {
		double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		//double stBeat = (1 - stBeatU) * startBeatNumber + stBeatU
		//		* endBeatNumber;

		double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		//double endBeat = (1 - endBeatU) * startBeatNumber + endBeatU
		//		* endBeatNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		//setStartBeatNumber(stBeat);
		//setEndBeatNumber(endBeat);
		setStartLevel(edLevel);
		setEndLevel(stLevel);
	}
	
	protected double calcYRatio()
	{
		double dx = resizeVect.getX();
		double dy = resizeVect.getY();
		if( dx < 1.0 ) dx = 1.0;
		return( Math.abs( dy ) / Math.abs( dx ) );
	}

	protected void calcXYFromU(double beatU, double levelU) {
		double aX = -1.0;
		double aY = -calcYRatio();
		double bX = 1.0;
		double bY = calcYRatio();

		double cX = (1.0 - HORIZ_STRT_PCT) * aX + HORIZ_STRT_PCT * bX;
		double cY = (1.0 - VERT_STRT_PCT) * aY + VERT_STRT_PCT * bY;
		double dX = (1.0 - HORIZ_END_PCT) * aX + HORIZ_END_PCT * bX;
		double dY = (1.0 - VERT_END_PCT) * aY + VERT_END_PCT * bY;

		beatX = (1.0 - beatU) * cX + beatU * dX;
		levelY = (1.0 - levelU) * cY + levelU * dY;
	}

	protected void calcUFromXY(double x, double y) {
		double aX = -1.0;
		double aY = -calcYRatio();
		double bX = 1.0;
		double bY = calcYRatio();

		double cX = (1.0 - HORIZ_STRT_PCT) * aX + HORIZ_STRT_PCT * bX;
		double cY = (1.0 - VERT_STRT_PCT) * aY + VERT_STRT_PCT * bY;
		double dX = (1.0 - HORIZ_END_PCT) * aX + HORIZ_END_PCT * bX;
		double dY = (1.0 - VERT_END_PCT) * aY + VERT_END_PCT * bY;

		startBeatU = (x - cX) / (dX - cX);
		levelU = (y - cY) / (dY - cY);
	}
	
	
	protected void setEditTrackCurve( boolean in )
	{
		boolean flag = in != editTrackCurve;
		editTrackCurve = in;
		if( flag )
		{
			refreshDisplayList();
		}
	}

	public void zoom() {
		editMode = EDIT_MODE_ZOOM;
		refreshDisplayList();
	}
	
	public void zoomX() {
		editMode = EDIT_MODE_ZOOM_X;
		refreshDisplayList();
	}
	
	public void zoomY() {
		editMode = EDIT_MODE_ZOOM_Y;
		refreshDisplayList();
	}

	public void unZoom() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-1.1);
		setEndLevel(1.1);
		setStartBeatNumber(-10);
		setEndBeatNumber( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10);
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void unZoom40() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-40.0);
		setEndLevel(40.0);
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void unZoomLimits() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-1.1);
		setEndLevel(1.1);
		setStartBeatNumber( SongData.START_BEAT );
		setEndBeatNumber( SongData.END_BEAT );
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void unZoom2X() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		double midLevel = ( getStartLevel() + getEndLevel() ) / 2.0;
		double deltaLevel = Math.abs( getEndLevel() - getStartLevel() );
		setStartLevel(midLevel - deltaLevel);
		setEndLevel(midLevel + deltaLevel);
		double midBeat = ( getStartBeatNumber() + getEndBeatNumber() ) / 2.0;
		double deltaBeat = Math.abs( getEndBeatNumber() - getStartBeatNumber() );
		setStartBeatNumber( midBeat - deltaBeat );
		setEndBeatNumber( midBeat + deltaBeat );
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}

	public void sequesterToWindow() {
		SongData.START_BEAT = startBeatNumber;
		SongData.END_BEAT = endBeatNumber;
	}

	/**
	 * @return Returns the endBeatNumber.
	 */
	public double getEndBeatNumber() {
		return endBeatNumber;
	}

	/**
	 * @param endBeatNumber
	 *            The endBeatNumber to set.
	 */
	public void setEndBeatNumber(double endBeatNumber) {
		this.endBeatNumber = endBeatNumber;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setEndBeatNumber(mil, endBeatNumber);
		undoMgr.handleCommitTempChange( mil );
	}

	/**
	 * @return Returns the endLevel.
	 */
	public double getEndLevel() {
		return endLevel;
	}

	/**
	 * @param endLevel
	 *            The endLevel to set.
	 */
	public void setEndLevel(double endLevel) {
		this.endLevel = endLevel;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setEndVolume(mil, endLevel);
		undoMgr.handleCommitTempChange( mil );
	}

	/**
	 * @return Returns the startBeatNumber.
	 */
	public double getStartBeatNumber() {
		return startBeatNumber;
	}

	/**
	 * @param startBeatNumber
	 *            The startBeatNumber to set.
	 */
	public void setStartBeatNumber(double startBeatNumber) {
		this.startBeatNumber = startBeatNumber;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setStartBeatNumber(mil, startBeatNumber);
		undoMgr.handleCommitTempChange( mil );
	}

	/**
	 * @return Returns the startLevel.
	 */
	public double getStartLevel() {
		return startLevel;
	}

	/**
	 * @param startLevel
	 *            The startLevel to set.
	 */
	public void setStartLevel(double startLevel) {
		this.startLevel = startLevel;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setStartVolume(mil, startLevel);
		undoMgr.handleCommitTempChange( mil );
	}

	protected ControlSelectNode getDescForLocn(MouseEvent e) {

		// updatePrims(bnd); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		int count;
		int sz = controlSelectList.size();

		Point3d mouseCoord = calcMouseCoord( e.getX() , e.getY() );
		double tdist = 1E+10;
		ControlSelectNode sel = null;
		

		for (count = 0; count < sz; count++) {
			
			ControlSelectNode desc = controlSelectList.get(count);
			
			double dist = desc.calcDist( mouseCoord );
			
			if( dist < tdist )
			{
				tdist = dist;
				sel = desc;
			}
		}
		
		if( tdist > ( 0.25 * 400.0 / ( resizeVect.getX() ) ) )
		{
			sel = null;
		}

		return ( sel );
	}
	
	protected Point3d calcMouseCoord( int x , int y )
	{
		return( AczonCoordinateConvert.convertCoords(x, y, rootFactory ) );
	}
	

	protected void mouserDown(MouseEvent e) {
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		
		switch (editMode) {

		case EDIT_MODE_ZOOM:
		case EDIT_MODE_ZOOM_X: 
		case EDIT_MODE_ZOOM_Y: {
			mouseDragEnabled = true;
			origX = pt.x;
			origY = pt.y;
			newX = origX;
			newY = origY;
			
			
			final AazonVect box0 = new AazonBaseImmutableVect( origX , origY );
			
			final AazonBaseMutableVect box1 = new AazonBaseMutableVect( newX , newY );
			
			final AazonEnt controlPoint = AazonSmartOutlineRectangle.construct( box0 , box1 , AczonColor.getLineOrange() , false );
			
			frontSprites.setGrp( new AazonImmutableGroup( controlPoint ) );
			
			lastDragDesc = new ControlSelectNode( 0,
					null,
					null,
					null,
					null,
					box1,
					null,
					null,
					null );
		}
			break;

		}
	}


	protected void mouserDrag(MouseEvent e) {
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		
		if (!mouseDragEnabled) {
			mouserDown(e);
		} else {
			switch (editMode) {

			case EDIT_MODE_ZOOM:
			case EDIT_MODE_ZOOM_X:
			case EDIT_MODE_ZOOM_Y: {
				newX = pt.x;
				newY = pt.y;
				
				lastDragDesc.getControlPointVect().setCoords(newX, newY);
			}
				break;

			}
		}
	}

	public void mouserDragEnd(MouseEvent e) {
	try
	{
		if (mouseDragEnabled) {

			switch( editMode )
			{
			case EDIT_MODE_ZOOM: {
				calcUFromXY(origX, origY);
				double stBeatU = startBeatU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endBeatU = startBeatU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoom(stBeatU, stLevelU, endBeatU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
			}
			break;
			
			case EDIT_MODE_ZOOM_X: {
				calcUFromXY(origX, origY);
				double stBeatU = startBeatU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endBeatU = startBeatU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoomX(stBeatU, stLevelU, endBeatU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
			}
			break;
			
			case EDIT_MODE_ZOOM_Y: {
				calcUFromXY(origX, origY);
				double stBeatU = startBeatU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endBeatU = startBeatU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoomY(stBeatU, stLevelU, endBeatU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
			}
			break;
				
				default:
				{ }
				break;
			}
			

			final int core = 0;
			SongData.getCurrentTrack().updateTrackFrames( core );
			refreshDisplayList();
			mouseDragEnabled = false;
		}
	}
	catch( Throwable ex )
	{
		ex.printStackTrace( System.out );
	}
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent arg0) {
		mouserDrag(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		mouserDragEnd(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		mouserDragEnd(arg0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		mouserDragEnd(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		mouserDrag(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		mouserDragEnd(arg0);
	}
	
	public Dimension minimumSize() {
		return (new Dimension(400, 400));
	}

	public Dimension preferredSize() {
		return (new Dimension(400, 400));
	}

}
