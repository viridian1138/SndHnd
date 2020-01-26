




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


import gredit.GWaveForm;
import grview.DraggableTransformNodeTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.swing.JComponent;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import jundo.runtime.ExtMilieuRef;
import labdaw.undo.pdx_VolumeViewPaneModel_pdx_ObjectRef;
import labdaw.undo.pdx_VolumeViewPaneModel_pdx_PairRef;
import palettes.PaletteClassesDump;
import verdantium.ProgramDirector;
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
import aazon.builderNode.AazonTransChld;
import aazon.dbl.AazonBaseImmutableDbl;
import aazon.dbl.AazonBaseMutableDbl;
import aazon.dbl.AazonMutableDbl;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonVect;
import abzon.AbzonCubicMonotoneCurveGenerator;
import abzon.AbzonImmutableGeneralPathFactory;
import abzon.AbzonImmutableShape;
import abzon.AbzonMonotoneCubicSlopeGenerator;
import abzon.AbzonMutableGeneralPathFactory;
import abzon.AbzonPathIteratorFactory;
import abzon.AbzonSlopeGenerator;
import abzon.AbzonSmartShape;
import aczon.AczonColor;
import aczon.AczonCoordinateConvert;
import aczon.AczonResizeVect;
import aczon.AczonRootFactory;
import aczon.AczonUnivAllocator;
import bezier.BezierCubicNonClampedCoefficientFlat;
import bezier.CubicBezierCurve;
import bezier.PiecewiseCubicMonotoneBezierFlat;

public class VolumeViewPane2 extends JComponent implements MouseListener,
		MouseMotionListener , PropertyChangeListener {
	
	protected AczonRootFactory rootFactory = null;
	
	protected AazonMutableGroup bkgndLineLightGray = new AazonMutableGroup();
	
	protected AazonMutableGroup bkgndDarkCyan = new AazonMutableGroup();
	
	protected AazonMutableGroup frontGreen = new AazonMutableGroup();
	
	protected AazonMutableGroup frontMagenta = new AazonMutableGroup();
	
	protected AazonMutableGroup frontDarkYellow = new AazonMutableGroup();
	
	protected AazonMutableGroup frontOrange = new AazonMutableGroup();
	
	protected AazonMutableGroup backSprites = new AazonMutableGroup();
	
	protected AazonMutableGroup frontControlPointsCyan = new AazonMutableGroup();
	
	protected AazonMutableGroup frontSprites = new AazonMutableGroup();
	
	protected AazonMutableGroup frontTextBlack = new AazonMutableGroup();
	
	
	protected final AazonBaseMutableVect rectAVectA = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	protected final AazonBaseMutableVect rectAVectB = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	protected final AazonBaseMutableVect rectBVectA = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	protected final AazonBaseMutableVect rectBVectB = new AazonBaseMutableVect( 0.0 , 0.0 );
	
	
	protected final AazonMutableDbl flatness = SongData.display_flatness;
	
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

	protected int editMode = EDIT_NOTE_INTERP_LEVEL;

	protected boolean editTrackCurve = false;

	protected boolean mouseDragEnabled = false;

	protected ControlSelectNode lastDragDesc = null;

	protected int mouseDragMode = 0;

	protected double origX = 0;

	protected double origY = 0;

	protected double newX = 0;

	protected double newY = 0;

	protected final ArrayList<Double> volLevelList = new ArrayList<Double>();
	
	protected final ArrayList<ControlSelectNode> controlSelectList = new ArrayList<ControlSelectNode>();
	
	protected UndoManager undoMgr = null;

	public static final int EDIT_MODE_ZOOM = 0;
	
	public static final int EDIT_MODE_ZOOM_X = 1;
	
	public static final int EDIT_MODE_ZOOM_Y = 2;

	public static final int EDIT_INSERT_TRACK_INTERP_POINT_BEFORE = 3;

	public static final int EDIT_INSERT_NOTE_INTERP_POINT_BEFORE = 4;

	public static final int EDIT_INSERT_TRACK_INTERP_POINT_AFTER = 5;

	public static final int EDIT_INSERT_NOTE_INTERP_POINT_AFTER = 6;

	public static final int EDIT_DEL_TRACK_INTERP_POINT = 7;

	public static final int EDIT_DEL_NOTE_INTERP_POINT = 8;

	public static final int EDIT_TRACK_INTERP_POSN = 9;

	public static final int EDIT_NOTE_INTERP_POSN = 10;

	public static final int EDIT_TRACK_INTERP_LEVEL = 11;

	public static final int EDIT_NOTE_INTERP_LEVEL = 12;
	
	public static final int EDIT_MODE_SELECT_NOTE_LEVEL = 13;
	
	public static final int EDIT_MODE_SELECT_TRACK_LEVEL = 14;

	
	
	public VolumeViewPane2( UndoManager _undoMgr ) {
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
	
	public void refreshDisplayList() {
		if (editTrackCurve) {
			refreshDisplayListTrack();
		} else {
			refreshDisplayListNote();
		}
	}

protected void refreshDisplayListNote() {
		
		ArrayList<AazonEnt> bkgndDarkCyanLst = new ArrayList<AazonEnt>();
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
		double startMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber(startBeatNumber, core);
		double endMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber(endBeatNumber, core);

		if (((endMeasure - startMeasure) > 10)) {
			double delta = (endMeasure - startMeasure) / 10.0;
			double count;
			for (count = startMeasure; count < endMeasure; count = count
					+ delta) {
				int iCount = (int) (count);
				double qbeat = SongData.measuresStore.getBeatNumberForMeasureNumber( iCount );
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
			if (endBeatNumber - startBeatNumber > 10) {
				double delta = (endBeatNumber - startBeatNumber) / 10.0;
				double count;
				for (count = startBeatNumber; count < endBeatNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					int measure = SongData.measuresStore.getMeasureNumberForBeatNumber(iCount, core);
					int beat = SongData.measuresStore.getIntBeatOnMeasureForBeatNumber(iCount, core);
					double qbeat = iCount;
					String str = "";
					if (beat == 0) {
						str = "" + measure;
					} else {
						str = "" + measure + "." + beat;
					}
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
					int measure = SongData.measuresStore.getMeasureNumberForBeatNumber(iCount, core);
					int beat = SongData.measuresStore.getIntBeatOnMeasureForBeatNumber(iCount, core);
					int seg = (int) (1000.0 * (count - iCount));
					double qbeat = count;
					String str = "" + measure + "." + beat + "." + seg;
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


		for( final InstrumentTrack track : SongData.instrumentTracks ) {
			if (track != curTr) {
				for( final TrackFrame frame : track.getTrackFrames() ) {
					for ( final NoteDesc desc : frame.getNotes() ) {
						desc.updateWaveInfoDisplayOnly();
						if (isWithinRange(desc)) {
							GeneralPath pointsa = new GeneralPath();

							calcNoteDescPoints(desc, pointsa);

							AbzonImmutableGeneralPathFactory fac = new AbzonImmutableGeneralPathFactory( pointsa );

							bkgndDarkCyanLst.add( AbzonSmartShape.construct( fac , flatness, new AffineTransform(), AczonColor.getLineDarkCyan(), false) );
						}
					}

				}
			}
		}
		
		
		
		
		
		controlSelectList.clear();
		
		
		Integer frNum = new Integer(-1);
		ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
		for( final TrackFrame frame : curTr.getTrackFrames() )
		{
			frNum = new Integer(frNum.intValue() + 1);
			for ( final NoteDesc desc : frame.getNotes() ) {
				desc.updateWaveInfoDisplayOnly();
				if (isWithinRange(desc)) {
						
					ArrayList<AazonEnt> controllingLineGroup = null;
					AbzonMutableGeneralPathFactory controllingLineChild = null;
					AazonEnt shape = null;
					Vector<AazonBaseMutableVect> interp = null;
					
					Appearance app = null;
						switch (frNum.intValue()) {
						case 0: {
							app = AczonColor.getLineGreen();
						}
							break;

						case 1: {
							app = AczonColor.getLineMagenta();
						}
							break;

						case 2: {
							app = AczonColor.getLineDarkYellow();
						}
							break;

						case 3: {
							app = AczonColor.getLineOrange();
						}
							break;
						}
						
						if( app == null )
						{
							app = AczonColor.getLineGreen();
						}
						
						
						if( ( editMode == EDIT_NOTE_INTERP_POSN ) || ( editMode == EDIT_NOTE_INTERP_LEVEL ) )
						{
							
							interp = new Vector<AazonBaseMutableVect>();
							
							calcNoteDescPointsInterp(desc, interp);
							
							AbzonSlopeGenerator generator = new AbzonMonotoneCubicSlopeGenerator( interp );
							
							AbzonCubicMonotoneCurveGenerator crv = new AbzonCubicMonotoneCurveGenerator( interp, generator);
							
							AbzonPathIteratorFactory[] fac = crv.generateCurves();

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
							
							controllingLineChild = null;
						}
						else
						{
							GeneralPath pointsa = new GeneralPath();

							calcNoteDescPoints(desc, pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						
							controllingLineChild = fac;
						}
						
						
						switch (frNum.intValue()) {
						case 0: {
							controllingLineGroup = frontGreenLst;
						}
							break;

						case 1: {
							controllingLineGroup = frontMagentaLst;
						}
							break;

						case 2: {
							controllingLineGroup = frontDarkYellowLst;
						}
							break;

						case 3: {
							controllingLineGroup = frontOrangeLst;
						}
							break;
						}
						
						if( controllingLineGroup == null )
						{
							controllingLineGroup = frontGreenLst;
						}
						
						controllingLineGroup.add( shape );
						Appearance lineApp = app;
						
						if( ( editMode == EDIT_NOTE_INTERP_POSN ) || ( editMode == EDIT_NOTE_INTERP_LEVEL ) )
						{
							generateControlPointsInterp(desc,interp,
									controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						else
						{
							generateControlPoints(desc,
									controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
				}
			}
		}
		
		frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
		
		
		bkgndDarkCyan.setGrp( new AazonImmutableGroup( bkgndDarkCyanLst ) );
		frontGreen.setGrp( new AazonImmutableGroup( frontGreenLst ) );
		frontMagenta.setGrp( new AazonImmutableGroup( frontMagentaLst ) );
		frontDarkYellow.setGrp( new AazonImmutableGroup( frontDarkYellowLst ) );
		frontOrange.setGrp( new AazonImmutableGroup( frontOrangeLst ) );
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
		
		
		ArrayList<AazonEnt> bkgndDarkCyanLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontGreenLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontMagentaLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontDarkYellowLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontOrangeLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontTxtBlack = new ArrayList<AazonEnt>();
		

		InstrumentTrack curTr = SongData.getCurrentTrack();
		
		

		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


		ArrayList<Point3d> points = new ArrayList<Point3d>();

		final int core = 0;
		double startMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber(startBeatNumber, core);
		double endMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber(endBeatNumber, core);

		if (((endMeasure - startMeasure) > 10)) {
			double delta = (endMeasure - startMeasure) / 10.0;
			double count;
			for (count = startMeasure; count < endMeasure; count = count
					+ delta) {
				int iCount = (int) (count);
				double qbeat = SongData.measuresStore.getBeatNumberForMeasureNumber( iCount );
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
			if (endBeatNumber - startBeatNumber > 10) {
				double delta = (endBeatNumber - startBeatNumber) / 10.0;
				double count;
				for (count = startBeatNumber; count < endBeatNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					int measure = SongData.measuresStore.getMeasureNumberForBeatNumber(iCount, core);
					int beat = SongData.measuresStore.getIntBeatOnMeasureForBeatNumber(iCount, core);
					double qbeat = iCount;
					String str = "";
					if (beat == 0) {
						str = "" + measure;
					} else {
						str = "" + measure + "." + beat;
					}
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
					int measure = SongData.measuresStore.getMeasureNumberForBeatNumber(iCount, core);
					int beat = SongData.measuresStore.getIntBeatOnMeasureForBeatNumber(iCount, core);
					int seg = (int) (1000.0 * (count - iCount));
					double qbeat = count;
					String str = "" + measure + "." + beat + "." + seg;
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

		int sz;
		int count;
		updateBkgndLineLightGray( points );
		


		for( final InstrumentTrack track : SongData.instrumentTracks ) {
			if (track != curTr) {
				for( final TrackFrame frame : track.getTrackFrames() ) {
					for ( final NoteDesc desc : frame.getNotes() ) {
						desc.updateWaveInfoDisplayOnly();
						if (isWithinRange(desc)) {
							GeneralPath pointsa = new GeneralPath();

							calcNoteDescPoints(desc, pointsa);

							AbzonImmutableGeneralPathFactory fac = new AbzonImmutableGeneralPathFactory( pointsa );

							bkgndDarkCyanLst.add( AbzonSmartShape.construct( fac, flatness, new AffineTransform(), AczonColor.getLineDarkCyan(), false) );
						}
					}

				}
			}
		}

		bkgndDarkCyan.setGrp( new AazonImmutableGroup( bkgndDarkCyanLst ) );
		
		
		
		
		controlSelectList.clear();
		
		
		Integer frNum = new Integer(-1);
		ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
		for( final TrackFrame frame : curTr.getTrackFrames() )
		{
			frNum = new Integer(frNum.intValue() + 1);
			for ( final NoteDesc desc : frame.getNotes() ) {
				desc.updateWaveInfoDisplayOnly();
				if (isWithinRange(desc)) {
					
					ArrayList<AazonEnt> controllingLineGroup = null;
					AbzonMutableGeneralPathFactory controllingLineChild = null;
					AazonEnt shape = null;
					Vector<AazonBaseMutableVect> interp = null;
						
						Appearance app = null;
						switch (frNum.intValue()) {
						case 0: {
							app = AczonColor.getLineGreen();
						}
							break;

						case 1: {
							app = AczonColor.getLineMagenta();
						}
							break;

						case 2: {
							app = AczonColor.getLineDarkYellow();
						}
							break;

						case 3: {
							app = AczonColor.getLineOrange();
						}
							break;
						}
						
						if( app == null )
						{
							app = AczonColor.getLineGreen();
						}
						
						if( ( editMode == EDIT_NOTE_INTERP_POSN ) || ( editMode == EDIT_NOTE_INTERP_LEVEL ) )
						{
							interp = new Vector<AazonBaseMutableVect>();
							
							calcNoteDescPointsInterp(desc, interp);
							
							AbzonSlopeGenerator generator = new AbzonMonotoneCubicSlopeGenerator( interp );
							
							AbzonCubicMonotoneCurveGenerator crv = new AbzonCubicMonotoneCurveGenerator( interp, generator);
							
							AbzonPathIteratorFactory[] fac = crv.generateCurves();

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
							
							controllingLineChild = null;
						}
						else
						{
						GeneralPath pointsa = new GeneralPath();

						calcNoteDescPoints(desc, pointsa);

						AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

						shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						
						controllingLineChild = fac;
						}
						
						switch (frNum.intValue()) {
						case 0: {
							controllingLineGroup = frontGreenLst;
						}
							break;

						case 1: {
							controllingLineGroup = frontMagentaLst;
						}
							break;

						case 2: {
							controllingLineGroup = frontDarkYellowLst;
						}
							break;

						case 3: {
							controllingLineGroup = frontOrangeLst;
						}
							break;
						}
						
						if( controllingLineGroup == null )
						{
							controllingLineGroup = frontGreenLst;
						}
						
						controllingLineGroup.add( shape );
						Appearance lineApp = app;
						
						
						if( ( editMode == EDIT_NOTE_INTERP_POSN ) || ( editMode == EDIT_NOTE_INTERP_LEVEL ) )
						{
							generateControlPointsInterp(desc,interp,
									controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						else
						{
							generateControlPoints(desc,
									controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
				}
			}
		}
		frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
		
		
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
				, frontOrange , backSprites , frontControlPointsCyan , frontSprites  , rectA , rectB , frontTextBlack };
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
	
	protected void generateControlPoints(NoteDesc desc, AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<InterpolationPoint> intp = desc.getNoteEnvelope( core ).getBez().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				calcUValuesActual(desc, pt);
				calcXYFromU(startBeatU, levelU);
				double x1 = beatX;
				double y1 = levelY;
				
				Point3f ctrlPoint = new Point3f((float)x1,(float)y1,0);
				
				final AazonBaseMutableVect centerVect = new AazonBaseMutableVect( x1 , y1 );
				
				final AazonVect box0 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( -ptSz , -ptSz ) );
				
				final AazonVect box1 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( ptSz , ptSz ) );
				
				final AazonEnt controlPoint = AazonSmartFilledRectangle.construct( box0 , box1 , AczonColor.getFillCyan() , false );
				
				controlPoints.add(controlPoint);
				
				controlPointsLocal.add(centerVect);
				
				if( asgnCntl )
				{
					ControlSelectNode controlSelectNode = 
						new ControlSelectNode( cnta , ctrlPoint , desc , null, 
								controllingLineChild, 
								centerVect, lineApp, pt , controlPointsLocal );
					controlSelectList.add( controlSelectNode );
				}
			}
			
	}
	
	
	
	protected void generateControlPointsInterp(NoteDesc desc, Vector<AazonBaseMutableVect> interpPts , AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<InterpolationPoint> intp = desc.getNoteEnvelope( core ).getBez().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				
				final AazonBaseMutableVect centerVect = interpPts.get( cnta );
				
				Point3f ctrlPoint = new Point3f((float)( centerVect.getX() ),(float)( centerVect.getY() ),0);
				
				final AazonVect box0 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( -ptSz , -ptSz ) );
				
				final AazonVect box1 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( ptSz , ptSz ) );
				
				final AazonEnt controlPoint = AazonSmartFilledRectangle.construct( box0 , box1 , AczonColor.getFillCyan() , false );
				
				controlPoints.add(controlPoint);
				
				controlPointsLocal.add(centerVect);
				
				if( asgnCntl )
				{
					ControlSelectNode controlSelectNode = 
						new ControlSelectNode( cnta , ctrlPoint , desc , null, 
								controllingLineChild, 
								centerVect, lineApp, pt , controlPointsLocal );
					controlSelectList.add( controlSelectNode );
				}
			}
			
	}
	
	
	
	protected void generateControlPointsInterp(InstrumentTrack track, Vector<AazonBaseMutableVect> interpPts , AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<InterpolationPoint> intp = track.getTrackVolume(core).getBez().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				
				final AazonBaseMutableVect centerVect = interpPts.get( cnta );
				
				Point3f ctrlPoint = new Point3f((float)( centerVect.getX() ),(float)( centerVect.getY() ),0);
				
				final AazonVect box0 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( -ptSz , -ptSz ) );
				
				final AazonVect box1 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( ptSz , ptSz ) );
				
				final AazonEnt controlPoint = AazonSmartFilledRectangle.construct( box0 , box1 , AczonColor.getFillCyan() , false );
				
				controlPoints.add(controlPoint);
				
				controlPointsLocal.add(centerVect);
				
				if( asgnCntl )
				{
					ControlSelectNode controlSelectNode = 
						new ControlSelectNode( cnta , ctrlPoint , null , track, 
								controllingLineChild, 
								centerVect, lineApp, pt , controlPointsLocal );
					controlSelectList.add( controlSelectNode );
				}
			}
			
	}
	
	
	
	protected void generateControlPoints(InstrumentTrack track, AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<InterpolationPoint> intp = track.getTrackVolume(core).getBez().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				calcUValuesActual(track, pt);
				calcXYFromU(startBeatU, levelU);
				double x1 = beatX;
				double y1 = levelY;
				
				Point3f ctrlPoint = new Point3f((float)x1,(float)y1,0);
				
				final AazonBaseMutableVect centerVect = new AazonBaseMutableVect( x1 , y1 );
				
				final AazonVect box0 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( -ptSz , -ptSz ) );
				
				final AazonVect box1 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( ptSz , ptSz ) );
				
				final AazonEnt controlPoint = AazonSmartFilledRectangle.construct( box0 , box1 , AczonColor.getFillCyan() , false );
				
				controlPoints.add(controlPoint);
				
				controlPointsLocal.add(centerVect);
				
				if( asgnCntl )
				{
					ControlSelectNode controlSelectNode = 
						new ControlSelectNode( cnta , ctrlPoint , null , track, 
								controllingLineChild, 
								centerVect, lineApp, pt , controlPointsLocal );
					controlSelectList.add( controlSelectNode );
				}
			}
			
	}
	
	

	protected void updateControlPoints(NoteDesc desc, AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, ArrayList<AazonBaseMutableVect> locControlPoints)
	{
		final int core = 0;
		
		final ArrayList<InterpolationPoint> intp = desc.getNoteEnvelope( core ).getBez().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				calcUValuesActual(desc, pt);
				calcXYFromU(startBeatU, levelU);
				double x1 = beatX;
				double y1 = levelY;
				
				final AazonBaseMutableVect centerVect = locControlPoints.get( cnta );
				
				centerVect.setCoords( x1 , y1 );
			}
			
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
	
	protected void calcNoteDescPoints(NoteDesc desc, GeneralPath out) {
		
		    int core = 0;
			int cnta;
			PiecewiseCubicMonotoneBezierFlat bez = desc.getNoteEnvelope(core).getBez();
			int numCurves = bez.getNumCurves();
			
			double startBeat = desc.getActualStartBeatNumber();
			double endBeat = desc.getActualEndBeatNumber();

			
			startBeatU = (startBeat - startBeatNumber)
					/ (endBeatNumber - startBeatNumber);
			endBeatU = (endBeat - startBeatNumber)
					/ (endBeatNumber - startBeatNumber);
			
			for (cnta = 0; cnta < numCurves; cnta++) {
				CubicBezierCurve crv = bez.gCurve(cnta);
				
				double[] bezPts = crv.getBezPts();
				double strt = crv.getStartParam();
				double endp = crv.getEndParam();
				
				if( cnta == 0 )
				{
					InterpolationPoint pt = new InterpolationPoint( strt , bezPts[ 0 ] );
					calcUValuesActual(desc, pt);
					calcXYFromU(startBeatU, levelU);
					double x1 = beatX;
					double y1 = levelY;
					out.moveTo((float)x1, (float)y1);
				}
				
				InterpolationPoint pt = new InterpolationPoint( 0.75 * strt + 0.25 * endp , bezPts[ 1 ] );
				calcUValuesActual(desc, pt);
				calcXYFromU(startBeatU, levelU);
				double x1 = beatX;
				double y1 = levelY;
				
				pt = new InterpolationPoint( 0.25 * strt + 0.75 * endp , bezPts[ 2 ] );
				calcUValuesActual(desc, pt);
				calcXYFromU(startBeatU, levelU);
				double x2 = beatX;
				double y2 = levelY;
				
				pt = new InterpolationPoint( endp , bezPts[ 3 ] );
				calcUValuesActual(desc, pt);
				calcXYFromU(startBeatU, levelU);
				double x3 = beatX;
				double y3 = levelY;
				
				out.curveTo((float)x1, (float)y1, (float)x2, (float)y2, (float)x3, (float)y3);
			}
		
	}
	
	protected void calcNoteDescPointsInterp(NoteDesc desc, Vector<AazonBaseMutableVect> out) {
		
	    int core = 0;
		int cnta;
		PiecewiseCubicMonotoneBezierFlat bez = desc.getNoteEnvelope(core).getBez();
		ArrayList<InterpolationPoint> intPoints = bez.getInterpolationPoints();
		final int sz = intPoints.size();
		
		for (cnta = 0; cnta < sz; cnta++) {
			InterpolationPoint pt = intPoints.get(cnta);
			calcUValuesActual(desc, pt);
			calcXYFromU(startBeatU, levelU);
			double x1 = beatX;
			double y1 = levelY;
			
			final AazonBaseMutableVect centerVect = new AazonBaseMutableVect( x1 , y1 );
			
			out.add( centerVect );
		}
	}
	
protected void calcTrackPointsInterp(InstrumentTrack track, Vector<AazonBaseMutableVect> out) {
		
	    int core = 0;
		int cnta;
		final ArrayList<InterpolationPoint> intPoints = track.getTrackVolume(core).getBez().getInterpolationPoints();
		final int sz = intPoints.size();
		
		for (cnta = 0; cnta < sz; cnta++) {
			InterpolationPoint pt = intPoints.get(cnta);
			calcUValuesActual(track, pt);
			calcXYFromU(startBeatU, levelU);
			double x1 = beatX;
			double y1 = levelY;
			
			final AazonBaseMutableVect centerVect = new AazonBaseMutableVect( x1 , y1 );
			
			out.add( centerVect );
		}
	}
	
	protected void calcTrackPoints(InstrumentTrack track, GeneralPath out) {
		
		int core = 0;
		int cnta;
		PiecewiseCubicMonotoneBezierFlat bez = track.getTrackVolume(core).getBez();
		int numCurves = bez.getNumCurves();

		
		startBeatU = 0.0;
		endBeatU = 1.0;
		
		for (cnta = 0; cnta < numCurves; cnta++) {
			CubicBezierCurve crv = bez.gCurve(cnta);
			
			double[] bezPts = crv.getBezPts();
			double strt = crv.getStartParam();
			double endp = crv.getEndParam();
			
			if( cnta == 0 )
			{
				InterpolationPoint pt = new InterpolationPoint( strt , bezPts[ 0 ] );
				calcUValuesActual(track, pt);
				calcXYFromU(startBeatU, levelU);
				double x1 = beatX;
				double y1 = levelY;
				out.moveTo((float)x1, (float)y1);
			}
			
			InterpolationPoint pt = new InterpolationPoint( 0.75 * strt + 0.25 * endp , bezPts[ 1 ] );
			calcUValuesActual(track, pt);
			calcXYFromU(startBeatU, levelU);
			double x1 = beatX;
			double y1 = levelY;
			
			pt = new InterpolationPoint( 0.25 * strt + 0.75 * endp , bezPts[ 2 ] );
			calcUValuesActual(track, pt);
			calcXYFromU(startBeatU, levelU);
			double x2 = beatX;
			double y2 = levelY;
			
			pt = new InterpolationPoint( endp , bezPts[ 3 ] );
			calcUValuesActual(track, pt);
			calcXYFromU(startBeatU, levelU);
			double x3 = beatX;
			double y3 = levelY;
			
			out.curveTo((float)x1, (float)y1, (float)x2, (float)y2, (float)x3, (float)y3);
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

	protected void calcUValuesActual(NoteDesc desc, double noteU) {
		final int core = 0;
		PiecewiseCubicMonotoneBezierFlat bez = desc.getNoteEnvelope(core).getBez();
		double level = bez.eval(noteU);
		double startBeat = desc.getActualStartBeatNumber();
		double endBeat = desc.getActualEndBeatNumber();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
		endBeatU = (endBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}
	
	protected void calcUValuesActual(InstrumentTrack track, double noteU) {
		final int core = 0;
		PiecewiseCubicMonotoneBezierFlat bez = track.getTrackVolume(core).getBez();
		double beatNumber = (1-noteU)*startBeatNumber + noteU*endBeatNumber;
		double level = bez.eval(beatNumber);

		levelU = (level - startLevel) / (endLevel - startLevel);
		startBeatU = 0.0;
		endBeatU = 1.0;
	}

	protected void calcUValuesSpec(NoteDesc desc, double noteU) {
		final int core = 0;
		PiecewiseCubicMonotoneBezierFlat bez = desc.getNoteEnvelope(core).getBez();
		double level = bez.eval(noteU);
		double startBeat = desc.getStartBeatNumber();
		double endBeat = desc.getEndBeatNumber();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
		endBeatU = (endBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void calcUValuesActual(NoteDesc desc, InterpolationPoint pt) {
		double level = pt.getValue();
		double param = pt.getParam();
		double startBeat = (1 - param) * (desc.getActualStartBeatNumber())
				+ (param) * (desc.getActualEndBeatNumber());

		levelU = (level - startLevel) / (endLevel - startLevel);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}
	
	protected void calcUValuesActual(InstrumentTrack track, InterpolationPoint pt) {
		double level = pt.getValue();
		double startBeat = pt.getParam();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void calcUValuesSpec(NoteDesc desc, InterpolationPoint pt) {
		double level = pt.getValue();
		double param = pt.getParam();
		double startBeat = (1 - param) * (desc.getStartBeatNumber()) + (param)
				* (desc.getEndBeatNumber());

		levelU = (level - startLevel) / (endLevel - startLevel);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void updateInterpLevel(double levelU, NoteDesc desc,
			InterpolationPoint pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setValue(level);
		desc.setUserDefinedNoteEnvelope( true );
		desc.getNoteEnvelope(core).getBez().updateAll();
		desc.setNoteEnvelope( desc.getNoteEnvelope(core) );
	}
	
	protected void updateInterpLevel(double levelU, InstrumentTrack track,
			InterpolationPoint pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setValue(level);
		track.getTrackVolume(core).getBez().updateAll();
		track.setTrackVolume( track.getTrackVolume(core) );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	protected void updateNoteInterpPosn(double beatU, NoteDesc desc, int index) throws Throwable {
		final int core = 0;
		ArrayList<InterpolationPoint> points = desc.getNoteEnvelope(core).getBez()
				.getInterpolationPoints();
		InterpolationPoint pt = points.get(index);
		InterpolationPoint prev = points.get(index - 1);
		InterpolationPoint nxt = points.get(index + 1);
		double beat = (1 - beatU) * startBeatNumber + beatU * endBeatNumber;
		double noteU = (beat - desc.getActualStartBeatNumber())
				/ (desc.getActualEndBeatNumber() - desc
						.getActualStartBeatNumber());
		if ((noteU > prev.getParam()) && (noteU < nxt.getParam())) {
			pt.setParam(noteU);
		}
		desc.setUserDefinedNoteEnvelope( true );
		desc.getNoteEnvelope(core).getBez().updateAll();
		desc.setNoteEnvelope( desc.getNoteEnvelope(core) );
	}
	
	protected void updateTrackInterpPosn(double beatU, InstrumentTrack track, int index) throws Throwable {
		final int core = 0;
		ArrayList<InterpolationPoint> points = track.getTrackVolume(core).getBez()
				.getInterpolationPoints();
		InterpolationPoint pt = points.get(index);
		InterpolationPoint prev = points.get(index - 1);
		InterpolationPoint nxt = points.get(index + 1);
		double beat = (1 - beatU) * startBeatNumber + beatU * endBeatNumber;
		if ((beat > prev.getParam()) && (beat < nxt.getParam())) {
			pt.setParam(beat);
		}
		track.getTrackVolume(core).getBez().updateAll();
		track.setTrackVolume( track.getTrackVolume(core) );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
	

	public void refreshDisplayListTrack() {
		ArrayList<AazonEnt> bkgndDarkCyanLst = new ArrayList<AazonEnt>();
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
		double startMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber(startBeatNumber, core);
		double endMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber(endBeatNumber, core);

		if (((endMeasure - startMeasure) > 10)) {
			double delta = (endMeasure - startMeasure) / 10.0;
			double count;
			for (count = startMeasure; count < endMeasure; count = count
					+ delta) {
				int iCount = (int) (count);
				double qbeat = SongData.measuresStore.getBeatNumberForMeasureNumber( iCount );
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
			if (endBeatNumber - startBeatNumber > 10) {
				double delta = (endBeatNumber - startBeatNumber) / 10.0;
				double count;
				for (count = startBeatNumber; count < endBeatNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					int measure = SongData.measuresStore.getMeasureNumberForBeatNumber(iCount, core);
					int beat = SongData.measuresStore.getIntBeatOnMeasureForBeatNumber(iCount, core);
					double qbeat = iCount;
					String str = "";
					if (beat == 0) {
						str = "" + measure;
					} else {
						str = "" + measure + "." + beat;
					}
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
					int measure = SongData.measuresStore.getMeasureNumberForBeatNumber(iCount, core);
					int beat = SongData.measuresStore.getIntBeatOnMeasureForBeatNumber(iCount, core);
					int seg = (int) (1000.0 * (count - iCount));
					double qbeat = count;
					String str = "" + measure + "." + beat + "." + seg;
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
		


		for( final InstrumentTrack track : SongData.instrumentTracks ) {
			if (track != curTr) {
				BezierCubicNonClampedCoefficientFlat trackVolume = track.getTrackVolume(core);
				PiecewiseCubicMonotoneBezierFlat bez = trackVolume.getBez();
							GeneralPath pointsa = new GeneralPath();

							calcTrackPoints(track, pointsa);

							AbzonImmutableGeneralPathFactory fac = new AbzonImmutableGeneralPathFactory( pointsa );

							bkgndDarkCyanLst.add( AbzonSmartShape.construct( fac , flatness, new AffineTransform(), AczonColor.getLineDarkCyan(), false) );
			}
		}
		
		
		
		
		
		controlSelectList.clear();
		
		
		if( ( editMode == EDIT_TRACK_INTERP_LEVEL ) || ( editMode == EDIT_TRACK_INTERP_POSN ) )
		{
			ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
			
			ArrayList<AazonEnt> controllingLineGroup = null;
			AbzonMutableGeneralPathFactory controllingLineChild = null;
				
				
				Appearance app = AczonColor.getLineGreen();
				
				controllingLineGroup = frontGreenLst;
				
				Appearance lineApp = app;
				
				Vector<AazonBaseMutableVect> interp = new Vector<AazonBaseMutableVect>();
				
				calcTrackPointsInterp(curTr, interp);
				
				AbzonSlopeGenerator generator = new AbzonMonotoneCubicSlopeGenerator( interp );
				
				AbzonCubicMonotoneCurveGenerator crv = new AbzonCubicMonotoneCurveGenerator( interp, generator);
				
				AbzonPathIteratorFactory[] fac = crv.generateCurves();

				AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
				controllingLineGroup.add( shape );
				
				generateControlPointsInterp(curTr, interp,
						controllingLineChild,
						lineApp, AczonColor.getFillCyan() , true, controlPoints);


				frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
		}
		else
		{
		ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
					GeneralPath pointsa = new GeneralPath();

					calcTrackPoints(curTr, pointsa);
					
					ArrayList<AazonEnt> controllingLineGroup = null;
					AbzonMutableGeneralPathFactory controllingLineChild = null;

					AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
						
						
						Appearance app = AczonColor.getLineGreen();
						

						AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						
						controllingLineGroup = frontGreenLst;
						
						
						controllingLineGroup.add( shape );
						controllingLineChild = fac;
						Appearance lineApp = app;
						
						generateControlPoints(curTr,
								controllingLineChild,
								lineApp, AczonColor.getFillCyan() , true, controlPoints);
		
		
		frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
		}
		
		
		bkgndDarkCyan.setGrp( new AazonImmutableGroup( bkgndDarkCyanLst ) );
		frontGreen.setGrp( new AazonImmutableGroup( frontGreenLst ) );
		frontMagenta.setGrp( new AazonImmutableGroup( frontMagentaLst ) );
		frontDarkYellow.setGrp( new AazonImmutableGroup( frontDarkYellowLst ) );
		frontOrange.setGrp( new AazonImmutableGroup( frontOrangeLst ) );
		frontTextBlack.setGrp( new AazonImmutableGroup( frontTxtBlack ) );
		
		rectAVectA.setCoords( -1.0 , calcYRatio() );
		
		rectAVectB.setCoords( 1.0 ,  ( calcYRatio() ) * 0.8 + -( calcYRatio() ) * 0.2 );
		
		rectBVectA.setCoords( -1.0 , -calcYRatio() );
		
		rectBVectB.setCoords( -1.0 * 0.8 + 1.0 * 0.2 , calcYRatio() );
		
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

	public void insertTrackInterpPointBefore() {
		editMode = EDIT_INSERT_TRACK_INTERP_POINT_BEFORE;
		setEditTrackCurve( true );
		refreshDisplayList();
	}

	public void insertNoteInterpPointBefore() {
		editMode = EDIT_INSERT_NOTE_INTERP_POINT_BEFORE;
		setEditTrackCurve( false );
		refreshDisplayList();
	}

	public void insertTrackInterpPointAfter() {
		editMode = EDIT_INSERT_TRACK_INTERP_POINT_AFTER;
		setEditTrackCurve( true );
		refreshDisplayList();
	}

	public void insertNoteInterpPointAfter() {
		editMode = EDIT_INSERT_NOTE_INTERP_POINT_AFTER;
		setEditTrackCurve( false );
		refreshDisplayList();
	}

	public void deleteTrackInterpPoint() {
		editMode = EDIT_DEL_TRACK_INTERP_POINT;
		setEditTrackCurve( true );
		refreshDisplayList();
	}

	public void deleteNoteInterpPoint() {
		editMode = EDIT_DEL_NOTE_INTERP_POINT;
		setEditTrackCurve( false );
		refreshDisplayList();
	}

	public void editTrackInterpPosn() {
		editMode = EDIT_TRACK_INTERP_POSN;
		setEditTrackCurve( true );
		refreshDisplayList();
	}

	public void editNoteInterpPosn() {
		editMode = EDIT_NOTE_INTERP_POSN;
		setEditTrackCurve( false );
		refreshDisplayList();
	}

	public void editTrackInterpLevel() {
		System.out.println("Set Edit Track Interp Level");
		editMode = EDIT_TRACK_INTERP_LEVEL;
		setEditTrackCurve( true );
		refreshDisplayList();
	}

	public void editNoteInterpLevel() {
		editMode = EDIT_NOTE_INTERP_LEVEL;
		setEditTrackCurve( false );
		refreshDisplayList();
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
	
	public void selectNoteLevel() {
		editMode = EDIT_MODE_SELECT_NOTE_LEVEL;
		setEditTrackCurve( false );
		refreshDisplayList();
	}
	
	public void selectTrackLevel() {
		editMode = EDIT_MODE_SELECT_TRACK_LEVEL;
		setEditTrackCurve( true );
		refreshDisplayList();
	}

	public void unZoom() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-0.1);
		setEndLevel(1.1);
		setStartBeatNumber(-10);
		setEndBeatNumber( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10);
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void unZoomLimits() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-0.1);
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
		try
		{
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		
		switch (editMode) {

		case EDIT_NOTE_INTERP_LEVEL: {
			ControlSelectNode descn = getDescForLocn(e);

			if (descn != null) {
				lastDragDesc = descn;
				mouseDragEnabled = true;
			}
		}
			break;

		case EDIT_TRACK_INTERP_LEVEL: {
			ControlSelectNode descn = getDescForLocn(e);
			if( descn != null ) {
			InstrumentTrack track = descn.getTrack();

			if (track != null) {
				lastDragDesc = descn;
				mouseDragEnabled = true;
			}
			}
		}
			break;

		case EDIT_NOTE_INTERP_POSN: {
			final int core = 0;
			ControlSelectNode descn = getDescForLocn(e);

			if (descn != null) {
				NoteDesc desc = descn.getDesc();
				int id = descn.getId();
				int sz = desc.getNoteEnvelope(core).getBez().getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {
					lastDragDesc = descn;
					mouseDragEnabled = true;
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		}
			break;
			
		case EDIT_TRACK_INTERP_POSN: {
			int core = 0;
			ControlSelectNode descn = getDescForLocn(e);
			if( descn != null ) {
			InstrumentTrack track = descn.getTrack();

			if (track != null) {
				int id = descn.getId();
				int sz = track.getTrackVolume(core).getBez().getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {
					lastDragDesc = descn;
					mouseDragEnabled = true;
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
				
			}
			}
		}
			break;

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

		case EDIT_INSERT_NOTE_INTERP_POINT_BEFORE: {
			insertNoteLevelBefore(e);
		}
			break;
			
		case EDIT_INSERT_TRACK_INTERP_POINT_BEFORE: {
			insertTrackLevelBefore(e);
		}
			break;

		case EDIT_INSERT_NOTE_INTERP_POINT_AFTER: {
			insertNoteLevelAfter(e);
		}
			break;
			
		case EDIT_INSERT_TRACK_INTERP_POINT_AFTER: {
			insertTrackLevelAfter(e);
		}
			break;

		case EDIT_DEL_NOTE_INTERP_POINT: {
			delLevel(e);
		}
			break;
			
		case EDIT_MODE_SELECT_NOTE_LEVEL: {
			selectNoteLevel(e);
		}
			break;
			
		case EDIT_MODE_SELECT_TRACK_LEVEL: {
			selectTrackLevel(e);
		}
			break;

		}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}

	protected void insertNoteLevelBefore(MouseEvent e) throws Throwable {
		final int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}

		int id = descn.getId();
		NoteDesc desc = descn.getDesc();
		PiecewiseCubicMonotoneBezierFlat bez = desc.getNoteEnvelope(core).getBez();
		int sz = bez.getInterpolationPoints().size();
		if (id > 0) {
			ArrayList<InterpolationPoint> vect = new ArrayList<InterpolationPoint>();
			ArrayList<InterpolationPoint> interp = bez.getInterpolationPoints();
			int count;
			for (count = 0; count < id; count++) {
				vect.add(interp.get(count));
			}
			InterpolationPoint apt = new InterpolationPoint();
			vect.add(apt);
			InterpolationPoint prev = interp.get(id - 1);
			InterpolationPoint nxt = interp.get(id);
			apt.setValue((prev.getValue() + nxt.getValue()) / 2.0);
			apt.setParam((prev.getParam() + nxt.getParam()) / 2.0);
			for (count = id; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			desc.setUserDefinedNoteEnvelope( true );
			bez.updateAll();
			desc.setNoteEnvelope( desc.getNoteEnvelope(core) );
			SongListeners.updateViewPanesVolumeChange();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	protected void insertTrackLevelBefore(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		InstrumentTrack track = descn.getTrack();
		if( track == null )
		{
			return;
		}
		PiecewiseCubicMonotoneBezierFlat bez = track.getTrackVolume(core).getBez();
		int sz = bez.getInterpolationPoints().size();
		if (id > 0) {
			ArrayList<InterpolationPoint> vect = new ArrayList<InterpolationPoint>();
			ArrayList<InterpolationPoint> interp = bez.getInterpolationPoints();
			int count;
			for (count = 0; count < id; count++) {
				vect.add(interp.get(count));
			}
			InterpolationPoint apt = new InterpolationPoint();
			vect.add(apt);
			InterpolationPoint prev = interp.get(id - 1);
			InterpolationPoint nxt = interp.get(id);
			apt.setValue((prev.getValue() + nxt.getValue()) / 2.0);
			apt.setParam((prev.getParam() + nxt.getParam()) / 2.0);
			for (count = id; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			bez.updateAll();
			try
			{
			    track.setTrackVolume( track.getTrackVolume( core ) );
			}
			catch(Throwable ex)
			{
				return;
			}
			SongListeners.updateViewPanesVolumeChange();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	protected void insertNoteLevelAfter(MouseEvent e) throws Throwable {
		ControlSelectNode descn = getDescForLocn(e);
		final int core = 0;
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		NoteDesc desc = descn.getDesc();
		PiecewiseCubicMonotoneBezierFlat bez = desc.getNoteEnvelope(core).getBez();
		int sz = bez.getInterpolationPoints().size();
		if (id < (sz - 1)) {
			ArrayList<InterpolationPoint> vect = new ArrayList<InterpolationPoint>();
			ArrayList<InterpolationPoint> interp = bez.getInterpolationPoints();
			int count;
			for (count = 0; count <= id; count++) {
				vect.add(interp.get(count));
			}
			InterpolationPoint apt = new InterpolationPoint();
			vect.add(apt);
			InterpolationPoint prev = interp.get(id);
			InterpolationPoint nxt = interp.get(id + 1);
			apt.setValue((prev.getValue() + nxt.getValue()) / 2.0);
			apt.setParam((prev.getParam() + nxt.getParam()) / 2.0);
			for (count = id + 1; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			desc.setUserDefinedNoteEnvelope( true );
			bez.updateAll();
			desc.setNoteEnvelope( desc.getNoteEnvelope(core) );
			SongListeners.updateViewPanesVolumeChange();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	protected void selectNoteLevel(MouseEvent e) {
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		// NoteDesc desc = descn.getDesc();
		lastDragDesc = descn;
		System.out.println( "Volume Selected..." );
	}
	
	protected void selectTrackLevel(MouseEvent e) {
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		// NoteDesc desc = descn.getDesc();
		lastDragDesc = descn;
		System.out.println( "Volume Selected..." );
	}
	
	protected void insertTrackLevelAfter(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		InstrumentTrack track = descn.getTrack();
		if( track == null )
		{
			return;
		}
		PiecewiseCubicMonotoneBezierFlat bez = track.getTrackVolume(core).getBez();
		int sz = bez.getInterpolationPoints().size();
		if (id < (sz - 1)) {
			ArrayList<InterpolationPoint> vect = new ArrayList<InterpolationPoint>();
			ArrayList<InterpolationPoint> interp = bez.getInterpolationPoints();
			int count;
			for (count = 0; count <= id; count++) {
				vect.add(interp.get(count));
			}
			InterpolationPoint apt = new InterpolationPoint();
			vect.add(apt);
			InterpolationPoint prev = interp.get(id);
			InterpolationPoint nxt = interp.get(id + 1);
			apt.setValue((prev.getValue() + nxt.getValue()) / 2.0);
			apt.setParam((prev.getParam() + nxt.getParam()) / 2.0);
			for (count = id + 1; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			bez.updateAll();
			try
			{
			    track.setTrackVolume( track.getTrackVolume( core ) );
			}
			catch(Throwable ex)
			{
				return;
			}
			SongListeners.updateViewPanesVolumeChange();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	protected void delLevel(MouseEvent e) throws Throwable {
		ControlSelectNode descn = getDescForLocn(e);
		final int core = 0;
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		NoteDesc desc = descn.getDesc();
		PiecewiseCubicMonotoneBezierFlat bez = desc.getNoteEnvelope(core).getBez();
		int sz = bez.getInterpolationPoints().size();
		if ((id > 0) && (id < (sz - 1))) {
			ArrayList<InterpolationPoint> vect = new ArrayList<InterpolationPoint>();
			ArrayList<InterpolationPoint> interp = bez.getInterpolationPoints();
			int count;
			for (count = 0; count < id; count++) {
				vect.add(interp.get(count));
			}
			for (count = id + 1; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			desc.setUserDefinedNoteEnvelope( true );
			bez.updateAll();
			desc.setNoteEnvelope( desc.getNoteEnvelope(core) );
			SongListeners.updateViewPanesVolumeChange();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	protected void dumpNoteProperties()  throws Throwable{
		final int core = 0;
		InstrumentTrack track = SongData.getCurrentTrack();

		if (lastDragDesc != null) {
			NoteDesc desc = lastDragDesc.getDesc();
			
			NotePropertiesEditor editor = new NotePropertiesEditor( desc , track , undoMgr );
			ProgramDirector.showPropertyEditor(editor, null,
				"Note Properties");
			
			return;
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	protected void mouserDrag(MouseEvent e) {
		try{
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		final int core = 0;
		
		if (!mouseDragEnabled) {
			mouserDown(e);
		} else {
			switch (editMode) {

			case EDIT_NOTE_INTERP_LEVEL: {
				calcUFromXY(pt.x, pt.y);

					lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_TRACK_INTERP_LEVEL: {
				calcUFromXY(pt.x,pt.y);

					lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;

			case EDIT_MODE_ZOOM:
			case EDIT_MODE_ZOOM_X:
			case EDIT_MODE_ZOOM_Y: {
				newX = pt.x;
				newY = pt.y;
				
				lastDragDesc.getControlPointVect().setCoords(newX, newY);
			}
				break;

			case EDIT_NOTE_INTERP_POSN: {
				calcUFromXY(pt.x, pt.y);

					lastDragDesc.getControlPointVect().setCoords( pt.x , lastDragDesc.getControlPointVect().getY() );
			}
				break;
				
			case EDIT_TRACK_INTERP_POSN: {
				calcUFromXY(pt.x,pt.y);

					lastDragDesc.getControlPointVect().setCoords( pt.x , lastDragDesc.getControlPointVect().getY() );
				
			}
				break;

			}
		}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}

	public void mouserDragEnd(MouseEvent e) {
		try{
			Point3d pt = calcMouseCoord( e.getX() , e.getY() );
			final int core = 0;
			
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
				lastDragDesc = null;
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
				lastDragDesc = null;
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
				lastDragDesc = null;
			}
			break;
				
			case EDIT_NOTE_INTERP_LEVEL:
			{
				calcUFromXY(pt.x, pt.y);
				double lvU = levelU;
				NoteDesc desc = lastDragDesc.getDesc();
				int id = lastDragDesc.getId();
				ArrayList<InterpolationPoint> points = desc.getNoteEnvelope(core).getBez().getInterpolationPoints();
				InterpolationPoint interpPoint = points.get(id);
				updateInterpLevel(lvU, desc, interpPoint);
					

					lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_NOTE_INTERP_POSN:
			{
				calcUFromXY(pt.x, pt.y);
				double btU = startBeatU;
				NoteDesc desc = lastDragDesc.getDesc();
				int id = lastDragDesc.getId();
				updateNoteInterpPosn(btU, desc, id);
					

					lastDragDesc.getControlPointVect().setCoords( pt.x , lastDragDesc.getControlPointVect().getY() );
			}
				break;
				
			case EDIT_TRACK_INTERP_LEVEL:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				InstrumentTrack track = lastDragDesc.getTrack();
				int idx = lastDragDesc.getId();
				ArrayList<InterpolationPoint> points = track.getTrackVolume(core).getBez()
						.getInterpolationPoints();
				InterpolationPoint interpPoint = points.get(idx);
				updateInterpLevel(frU, track, interpPoint);
					

					lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_TRACK_INTERP_POSN:
			{
				calcUFromXY(pt.x,pt.y);
				double btU = startBeatU;
				InstrumentTrack track = lastDragDesc.getTrack();
				int idx = lastDragDesc.getId();
				updateTrackInterpPosn(btU, track, idx);
					

					lastDragDesc.getControlPointVect().setCoords( pt.x , lastDragDesc.getControlPointVect().getY() );
			}
				break;
				
				
				default:
				{ }
				break;
			}
			

			try
			{
				SongData.getCurrentTrack().updateTrackFrames(core);
				
				switch( editMode )
				{
				
				case EDIT_MODE_ZOOM: 
				case EDIT_MODE_ZOOM_X: 
				case EDIT_MODE_ZOOM_Y: 
					refreshDisplayList();
					break;
					
				case EDIT_NOTE_INTERP_LEVEL: 
				case EDIT_NOTE_INTERP_POSN: 
				case EDIT_TRACK_INTERP_LEVEL: 
				case EDIT_TRACK_INTERP_POSN: 
					SongListeners.updateViewPanesVolumeChange();
					break;
					
				default:
					SongListeners.updateViewPanes();
					break;
				}
				
				mouseDragEnabled = false;
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
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
