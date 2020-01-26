




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


import grview.DraggableTransformNodeTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import jundo.runtime.ExtMilieuRef;
import kwaves.FourierRenderingPane;
import labdaw.undo.pdx_NoteViewPaneModel_pdx_ObjectRef;
import labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef;
import palettes.PaletteClassesHarmony;
import verdantium.ProgramDirector;
import verdantium.undo.UTag;
import verdantium.undo.UndoManager;
import aazon.AazonEnt;
import aazon.AazonImmutableGroup;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonListener;
import aazon.AazonMutableEnt;
import aazon.AazonMutableGroup;
import aazon.AazonSmartBlockText;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartOutlineRectangle;
import aazon.builderNode.BuilderNode;
import aazon.dbl.AazonBaseImmutableDbl;
import aazon.dbl.AazonDbl;
import aazon.dbl.AazonMutableDbl;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;
import abzon.AbzonCubicMonotoneCurveGenerator;
import abzon.AbzonImmutableGeneralPathFactory;
import abzon.AbzonImmutablePathIteratorFactory;
import abzon.AbzonImmutableShape;
import abzon.AbzonMonotoneCubicSlopeGenerator;
import abzon.AbzonMutableGeneralPathFactory;
import abzon.AbzonMutablePathIteratorFactory;
import abzon.AbzonPathIteratorFactory;
import abzon.AbzonSlopeGenerator;
import abzon.AbzonSmartShape;
import aczon.AczonColor;
import aczon.AczonCoordinateConvert;
import aczon.AczonResizeVect;
import aczon.AczonRootFactory;
import aczon.AczonUnivAllocator;
import bezier.CubicBezierCurve;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;
import dissurf.DissSurfCalc;
import dissurf.SelectionHandler;

public class NoteViewPane2 extends Panel implements MouseListener,
													 MouseMotionListener ,
													 PropertyChangeListener{
	
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
	
	
	protected AbzonMutableGeneralPathFactory hrmMinimaYellowPathIteratorFactory = genInitialYellowPathIteratorFactory();
	
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
	

	
	
	
	protected AbzonMutableGeneralPathFactory genInitialYellowPathIteratorFactory()
	{
		GeneralPath gp = new GeneralPath();
		AbzonImmutableGeneralPathFactory path = new AbzonImmutableGeneralPathFactory( gp );
		AbzonMutableGeneralPathFactory ret =  new AbzonMutableGeneralPathFactory( path );
		return( ret );
	}
	
	protected AazonMutableEnt genYellowShape()
	{
		AazonEnt ret = AbzonSmartShape.construct( hrmMinimaYellowPathIteratorFactory , new AazonBaseImmutableDbl( 0.01 ) , new AffineTransform() , AczonColor.getLineYellow() , false );
		return( (AazonMutableEnt) ret );
	}
	
	
	protected double startBeatNumber;

	protected double endBeatNumber;

	protected double startFrequency;

	protected double endFrequency;

	protected double startFrequencyLog;

	protected double endFrequencyLog;

	protected pdx_NoteViewPaneModel_pdx_ObjectRef model = null;

	protected static final double VERT_STRT_PCT = 0.0;

	protected static final double HORIZ_STRT_PCT = 0.2;

	protected static final double VERT_END_PCT = 0.8;

	protected static final double HORIZ_END_PCT = 1.0;

	protected static final int CRV_STROKE_SIZE = 100;

	protected int editMode = EDIT_MODE_ZOOM; // EDIT_MODE_INTERP_PITCH;

	protected boolean mouseDragEnabled = false;

	protected ControlSelectNode lastDragDesc = null;

	protected int mouseDragMode = 0;

	protected double origX = 0;

	protected double origY = 0;

	protected double newX = 0;

	protected double newY = 0;

	protected final ArrayList<Double> toneFreqList = new ArrayList<Double>();
	
	protected final ArrayList<ControlSelectNode> controlSelectList = new ArrayList<ControlSelectNode>();

	protected ArrayList<Double> hrmMinima = null;

	protected ArrayList<InterpolationPoint> hrmCurve = null;

	protected UndoManager undoMgr = null;

	protected static final Color DARK_CYAN = new Color(0, 192, 192);

	protected static final Color DARK_YELLOW = new Color(192, 192, 0);

	public static final int EDIT_MODE_BASE_PITCH = 0;

	public static final int EDIT_MODE_INTERP_PITCH = 1;

	public static final int EDIT_MODE_DURATION = 2;

	public static final int EDIT_MODE_ZOOM = 3;

	public static final int EDIT_MODE_ZOOM_X = 4;

	public static final int EDIT_MODE_ZOOM_Y = 5;

	public static final int EDIT_INSERT_PITCH_BEFORE = 6;

	public static final int EDIT_INSERT_PITCH_AFTER = 7;

	public static final int EDIT_DEL_PITCH = 8;

	public static final int EDIT_MODE_INTERP_POSN = 9;

	public static final int EDIT_MODE_GENERATE_HARMONIES = 10;

	public static final int EDIT_MODE_SNAP_BASE_PITCH_VOICE = 11;

	public static final int EDIT_MODE_SNAP_INTERP_PITCH_VOICE = 12;

	public static final int EDIT_MODE_INSERT_PORTAMENTO = 13;

	public static final int EDIT_SELECT_PITCH = 14;
	
	public static final int EDIT_MODE_PLOT_FOURIER_HRM = 15;
	
	public static final int EDIT_MODE_PLOT_VOICE_FOURIER_HRM = 16;
	
	public static final int EDIT_MODE_GENERATE_HARMONY_SURF = 17;

	
	
	public NoteViewPane2(UndoManager _undoMgr) {
		super();
		undoMgr = _undoMgr;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		pdx_NoteViewPaneModel_pdx_PairRef pair = pdx_NoteViewPaneModel_pdx_ObjectRef
				.pdxm_new_NoteViewPaneModel(mil, 0.0, 0.0, 0.0, 0.0);
		mil = pair.getMilieu();
		model = (pdx_NoteViewPaneModel_pdx_ObjectRef) (pair.getObject());
		undoMgr.handleCommitTempChange(mil);
		undoMgr.addPropertyChangeListener(this);
	}
	
	protected void setModeOptionalRefresh( int in )
	{
		int tmp = editMode;
		editMode = in;
		boolean t1 = tmp == EDIT_MODE_DURATION;
		boolean t2 = editMode == EDIT_MODE_DURATION;
		if( t1 != t2 )
		{
			refreshDisplayList();
			return;
		}
		t1 = ( tmp == EDIT_MODE_INTERP_POSN ) || ( tmp == EDIT_MODE_INTERP_PITCH );
		t2 = ( editMode == EDIT_MODE_INTERP_POSN ) || ( editMode == EDIT_MODE_INTERP_PITCH );
		if( t1 != t2 )
		{
			refreshDisplayList();
		}
	}

	/**
	 * Handles property change events.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == UndoManager.MODEL_STATE_CHANGED) {
			handleUndoStateChange();
		}

	}

	public void handleUndoStateChange() {
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		startBeatNumber = model.pdxm_getStartBeatNumber(mil);
		endBeatNumber = model.pdxm_getEndBeatNumber(mil);
		startFrequency = model.pdxm_getStartFrequency(mil);
		endFrequency = model.pdxm_getEndFrequency(mil);
		startFrequencyLog = Math.log(startFrequency) / Math.log(2);
		endFrequencyLog = Math.log(endFrequency) / Math.log(2);
		refreshDisplayList();
	}
	
	protected void refreshDisplayList() {
		
		ArrayList<AazonEnt> bkgndDarkCyanLst = new ArrayList<AazonEnt>();
		hrmMinimaYellowPathIteratorFactory.setCrv( new AbzonImmutableGeneralPathFactory( new GeneralPath() ) );
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
				calcXYFromU(startBeatU, freqU);
				double x1 = beatX;
				Point3d lineSt = new Point3d(x1, -calcYRatio(), 0.0);
				Point3d lineEnd = new Point3d(x1, calcYRatio(), 0.0);
				points.add(lineSt);
				points.add(lineEnd);
				
				AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( x1 - 0.05 , 0.9 * ( calcYRatio() ) + 0.1 * ( -calcYRatio() ) ) , 
						str, AczonColor.getTextBlack() , "Helvetica", 8, Font.PLAIN, false);
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
					calcXYFromU(startBeatU, freqU);
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
					calcXYFromU(startBeatU, freqU);
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

		toneFreqList.clear();

		int countMelodic;
		int countNote;

		TreeMap<Double,Integer> melodicMap = new TreeMap<Double,Integer>();
		TreeMap<Double,Integer> noteMap = new TreeMap<Double,Integer>();

		ArrayList<Integer> melodicVect = new ArrayList<Integer>();
		ArrayList<Integer> noteVect = new ArrayList<Integer>();

		final int cstrt = (int)( -3.0 * ( 2.0 / ( NoteTable.getMelodicIntervalRatio() ) ) );
		final int cend = (int)( 15.0 * ( 2.0 / ( NoteTable.getMelodicIntervalRatio() ) ) );
		final int szz = NoteTable.getScaleSize();
		for (countMelodic = cstrt; countMelodic < cend; countMelodic++) {
			for (countNote = 0; countNote < szz; countNote++) {
				double nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(
						countMelodic, countNote);
				if ((nFreq > startFrequency) && (nFreq < endFrequency)) {
					Double key = new Double(nFreq);
					melodicMap.put(key, new Integer(countMelodic));
					noteMap.put(key, new Integer(countNote));
				}
			}
		}

		for ( Entry<Double,Integer> e : melodicMap.entrySet() ) {
			melodicVect.add( e.getValue() );
			noteVect.add( noteMap.get( e.getKey() ) );
		}

		int oSz = melodicVect.size();
		int cSz = Math.min(melodicVect.size(), 10);
		if (cSz == 0) {
			cSz = 1;
		}
		int cDelta = oSz / cSz;
		int count;
		for (count = 0; count < oSz; count = count + cDelta) {
			int melodicIntervalIndex = ((Integer) (melodicVect.get(count))).intValue();
			int note = ((Integer) (noteVect.get(count))).intValue();
			String noteStr = NoteTable.getScaleNamesDefaultScale_Key()[note];
			double nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalIndex,
					note);
			toneFreqList.add(new Double(nFreq));
			String str = "" + melodicIntervalIndex + "." + noteStr;
			calcUFromFreq(nFreq);
			calcXYFromU(startBeatU, freqU);
			double y1 = freqY;
			Point3d lineSt = new Point3d(-1.0, y1, 0.0);
			Point3d lineEnd = new Point3d(1.0, y1, 0.0);
			points.add(lineSt);
			points.add(lineEnd);
			
			AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( -0.9 , y1 - 0.05 ) , 
					str, AczonColor.getTextBlack(), "Helvetica", 15, Font.PLAIN, false);
			frontTxtBlack.add( tx );
			
		}
		

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

							NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , desc );

							bkgndDarkCyanLst.add( AbzonSmartShape.construct( fac , flatness, new AffineTransform(), AczonColor.getLineDarkCyan(), false) );
						}
					}

				}
			}
		}
		
		
		
		generateHrmList();
		
		
		
		
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
							
						default:
							{ app = AczonColor.getLineGreen(); }
						break;	
							
						}
						
						
						if( ( editMode == EDIT_MODE_INTERP_PITCH ) || ( editMode == EDIT_MODE_INTERP_POSN ) )
						{
							
							interp = new Vector<AazonBaseMutableVect>();
							
							calcNoteDescPointsInterp(desc, interp);
							
							AbzonSlopeGenerator generator = new AbzonMonotoneCubicSlopeGenerator( interp );
							
							NoteViewCubicMonotoneCurveGenerator crv = new NoteViewCubicMonotoneCurveGenerator( interp, generator, desc);
							
							AbzonPathIteratorFactory[] fac = crv.generateCurves();

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
							
							controllingLineChild = null;
						}
						else
						{
							GeneralPath pointsa = new GeneralPath();

							calcNoteDescPoints(desc, pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new NoteViewImmutableGeneralPathFactory( pointsa , desc ) );

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
							
						default:
							{ controllingLineGroup = frontGreenLst; }
						break;
						
						}
					
						
						controllingLineGroup.add( shape );
						Appearance lineApp = app;
						
						if( ( editMode == EDIT_MODE_INTERP_PITCH ) || ( editMode == EDIT_MODE_INTERP_POSN ) )
						{
							generateControlPointsInterp(desc, interp,
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
				calcXYFromU(startBeatU, freqU);
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
					calcXYFromU(startBeatU, freqU);
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
					calcXYFromU(startBeatU, freqU);
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

		toneFreqList.clear();

		int countMelodic;
		int countNote;

		TreeMap<Double,Integer> melodicMap = new TreeMap<Double,Integer>();
		TreeMap<Double,Integer> noteMap = new TreeMap<Double,Integer>();

		ArrayList<Integer> melodicVect = new ArrayList<Integer>();
		ArrayList<Integer> noteVect = new ArrayList<Integer>();

		final int cstrt = (int)( -3.0 * ( 2.0 / ( NoteTable.getMelodicIntervalRatio() ) ) );
		final int cend = (int)( 15.0 * ( 2.0 / ( NoteTable.getMelodicIntervalRatio() ) ) );
		final int szz = NoteTable.getScaleSize();
		for (countMelodic = cstrt; countMelodic < cend; countMelodic++) {
			for (countNote = 0; countNote < szz; countNote++) {
				double nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(
						countMelodic, countNote);
				if ((nFreq > startFrequency) && (nFreq < endFrequency)) {
					Double key = new Double(nFreq);
					melodicMap.put(key, new Integer(countMelodic));
					noteMap.put(key, new Integer(countNote));
				}
			}
		}

		for( Entry<Double,Integer> e : melodicMap.entrySet() ) {
			melodicVect.add( e.getValue() );
			noteVect.add( noteMap.get( e.getKey() ) );
		}

		int oSz = melodicVect.size();
		int cSz = Math.min(melodicVect.size(), 10);
		if (cSz == 0) {
			cSz = 1;
		}
		int cDelta = oSz / cSz;
		int count;
		for (count = 0; count < oSz; count = count + cDelta) {
			int melodicIntervalIndex = ((Integer) (melodicVect.get(count))).intValue();
			int note = ((Integer) (noteVect.get(count))).intValue();
			String noteStr = NoteTable.getScaleNamesDefaultScale_Key()[note];
			double nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(melodicIntervalIndex,
					note);
			toneFreqList.add(new Double(nFreq));
			String str = "" + melodicIntervalIndex + "." + noteStr;
			calcUFromFreq(nFreq);
			calcXYFromU(startBeatU, freqU);
			double y1 = freqY;
			Point3d lineSt = new Point3d(-1.0, y1, 0.0);
			Point3d lineEnd = new Point3d(1.0, y1, 0.0);
			points.add(lineSt);
			points.add(lineEnd);
			
			AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( -0.9 , y1 - 0.05 ) , 
					str, AczonColor.getTextBlack(), "Helvetica", 15, Font.PLAIN, false);
			frontTxtBlack.add( tx );
			
		}
		
		


        updateBkgndLineLightGray( points );


		int sz;
		

		for( final InstrumentTrack track : SongData.instrumentTracks ) {
			if (track != curTr) {
				for( final TrackFrame frame : track.getTrackFrames() ) {
					for ( final NoteDesc desc : frame.getNotes() ) {
						desc.updateWaveInfoDisplayOnly();
						if (isWithinRange(desc)) {
							GeneralPath pointsa = new GeneralPath();

							calcNoteDescPoints(desc, pointsa);

							NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , desc );
							
							bkgndDarkCyanLst.add( AbzonSmartShape.construct( fac, flatness, new AffineTransform(), AczonColor.getLineDarkCyan(), false) );
						}
					}

				}
			}
		}

		bkgndDarkCyan.setGrp( new AazonImmutableGroup( bkgndDarkCyanLst ) );
		
		
		
		generateHrmList();
		
		
		
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
							
						default:
							{ app = AczonColor.getLineGreen(); }
						break;
						
						}
						
						
						if( ( editMode == EDIT_MODE_INTERP_PITCH ) || ( editMode == EDIT_MODE_INTERP_POSN ) )
						{
							
							interp = new Vector<AazonBaseMutableVect>();
							
							calcNoteDescPointsInterp(desc, interp);
							
							AbzonSlopeGenerator generator = new AbzonMonotoneCubicSlopeGenerator( interp );
							
							NoteViewCubicMonotoneCurveGenerator crv = new NoteViewCubicMonotoneCurveGenerator( interp, generator, desc);
							
							AbzonPathIteratorFactory[] fac = crv.generateCurves();

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
							
							controllingLineChild = null;
						}
						else
						{
							GeneralPath pointsa = new GeneralPath();

							calcNoteDescPoints(desc, pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new NoteViewImmutableGeneralPathFactory( pointsa , desc ) );

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
							
						default:
							{ controllingLineGroup = frontGreenLst; }
						break;
						
						}
						
						
						controllingLineGroup.add( shape );
						Appearance lineApp = app;
						
						if( ( editMode == EDIT_MODE_INTERP_PITCH ) || ( editMode == EDIT_MODE_INTERP_POSN ) )
						{
							generateControlPointsInterp(desc, interp,
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

		
		
		AazonEnt[] objOrderEnts = { bkgndLineLightGray , bkgndDarkCyan , genYellowShape() , frontGreen , frontMagenta , frontDarkYellow
				, frontOrange , backSprites , frontControlPointsCyan , frontSprites , rectA , rectB , frontTextBlack };
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
	
	protected void generateHrmList()
	{	
		if (hrmCurve != null) {
			int count;
			final double bndw = 0.3 * ( 1.0 ) + 0.7 * ( -1.0 );
			final int sz = hrmCurve.size();
			
			int frSz = 0;
			double vmax = -1E+8;
			double vmin = 1E+8;
			for (count = 0; count < sz; count++) {
				InterpolationPoint p1 = hrmCurve.get(count);
				
				double freq = p1.getParam();
				vmax = Math.max( vmax , p1.getValue() );
				vmin = Math.min( vmin , p1.getValue() );

				if( ( freq >= startFrequency ) && ( freq <= endFrequency ) )
				{
					frSz++;
				}
			}
			
			if( ( vmax - vmin) < 1E-5 )
			{
				vmax = vmin + 1E-5;
			}
			
			if( frSz < 2 )
			{
				hrmMinimaYellowPathIteratorFactory.setCrv( new AbzonImmutableGeneralPathFactory( new GeneralPath() ) );
				return;
			}
			
			GeneralPath gp = new GeneralPath();
			int frCnt = 0;

			for (count = 0; count < sz; count++) {
				InterpolationPoint p1 = hrmCurve.get(count);
				
				double freq = p1.getParam();

				if( ( freq >= startFrequency ) && ( freq <= endFrequency ) )
				{
					calcUFromFreq(p1.getParam());
					calcXYFromU(startBeatU, freqU);
					double y1 = freqY;
					
					double vu = ( ( p1.getValue() ) - vmin ) / ( vmax - vmin );
					double x1 = vu * 1.0 + ( 1.0 - vu ) * bndw;

					if( frCnt == 0 )
					{
						gp.moveTo( (float) x1 , (float) y1 );
					}
					else
					{
						gp.lineTo( (float) x1 , (float) y1 );
					}
					
					frCnt++;
				}
			}
			
			hrmMinimaYellowPathIteratorFactory.setCrv( new AbzonImmutableGeneralPathFactory( gp ) );
		}
		else
		{
			hrmMinimaYellowPathIteratorFactory.setCrv( new AbzonImmutableGeneralPathFactory( new GeneralPath() ) );
		}
		
	}
	
	protected void generateControlPointsInterp(NoteDesc desc, Vector<AazonBaseMutableVect> interpPts , AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		
		final ArrayList<AazonMutableVect> controlPointsLocal = new ArrayList<AazonMutableVect>();
		
		final ArrayList<InterpolationPoint> intp = desc.getFreqAndBend().getFreqPerNoteU().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
		for (cnta = 0; cnta < sza; cnta++) {
			final InterpolationPoint pt = intp.get(cnta);
			
			final AazonBaseMutableVect utCenterVect = interpPts.get( cnta );
			
			final AazonMutableVect centerVect = new NoteViewCrtlPtVect( utCenterVect , desc );
			
			Point3f ctrlPoint = new Point3f((float)( centerVect.getX() ),(float)( centerVect.getY() ),0);
				
			final AazonVect box0 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( -ptSz , -ptSz ) );
				
			final AazonVect box1 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( ptSz , ptSz ) );
				
			final AazonEnt controlPoint = AazonSmartFilledRectangle.construct( box0 , box1 , AczonColor.getFillCyan() , false );
				
			controlPoints.add(controlPoint);
				
			controlPointsLocal.add(centerVect);
				
			if( asgnCntl )
			{
				ControlSelectNode controlSelectNode = 
					new ControlSelectNode( cnta , ctrlPoint , desc , 
							controllingLineChild, 
							utCenterVect, lineApp, pt , controlPointsLocal );
				controlSelectList.add( controlSelectNode );
			}
		}
			
	}
	
	
	protected void generateControlPoints(NoteDesc desc, AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final boolean updateActual = editMode != EDIT_MODE_DURATION;
		
		final ArrayList<AazonMutableVect> controlPointsLocal = new ArrayList<AazonMutableVect>();
		
		final ArrayList<InterpolationPoint> intp = desc.getFreqAndBend().getFreqPerNoteU().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
		if( updateActual )
		{
			for (cnta = 0; cnta < sza; cnta++) {
				final InterpolationPoint pt = intp.get(cnta);
				calcUValuesActual(desc, pt);
				calcXYFromU(startBeatU, freqU);
				double x1 = beatX;
				double y1 = freqY;
				
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
						new ControlSelectNode( cnta , ctrlPoint , desc , 
								controllingLineChild, 
								centerVect, lineApp, pt , controlPointsLocal );
					controlSelectList.add( controlSelectNode );
				}
			}
		}
		else
		{
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				calcUValuesSpec(desc, pt);
				calcXYFromU(startBeatU, freqU);
				double x1 = beatX;
				double y1 = freqY;
				
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
						new ControlSelectNode( cnta , ctrlPoint , desc , 
								controllingLineChild, 
								centerVect, lineApp, pt , controlPointsLocal );
					controlSelectList.add( controlSelectNode );
				}
			}
		}
		
	}
	
	
	protected void updateControlPoints(NoteDesc desc, AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, ArrayList<AazonBaseMutableVect> locControlPoints)
	{
		final boolean updateActual = editMode != EDIT_MODE_DURATION;
		
		final ArrayList<InterpolationPoint> intp = desc.getFreqAndBend().getFreqPerNoteU().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
		if( updateActual )
		{
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				calcUValuesActual(desc, pt);
				calcXYFromU(startBeatU, freqU);
				double x1 = beatX;
				double y1 = freqY;
				
				final AazonBaseMutableVect centerVect = locControlPoints.get( cnta );
				
				centerVect.setCoords( x1 , y1 );
				
			}
		}
		else
		{
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				calcUValuesSpec(desc, pt);
				calcXYFromU(startBeatU, freqU);
				double x1 = beatX;
				double y1 = freqY;
				
				final AazonBaseMutableVect centerVect = locControlPoints.get( cnta );
				
				centerVect.setCoords( x1 , y1 );
				
			}
		}
		
	}
	
	
	
	public static class ControlSelectNode
	{
		int id;
		Point3f ctrlPoint;
		NoteDesc desc;
		AbzonMutableGeneralPathFactory controllingLineChild;
		AazonBaseMutableVect controlPointVect;
		Appearance lineApp;
		InterpolationPoint pt;
		ArrayList<AazonMutableVect> controlPointsLocal;
		
		public ControlSelectNode( int _id,
				Point3f _ctrlPoint,
				NoteDesc _desc,
				AbzonMutableGeneralPathFactory _controllingLineChild,
				AazonBaseMutableVect _controlPointVect,
				Appearance _lineApp,
				InterpolationPoint _pt,
				ArrayList<AazonMutableVect> _controlPointsLocal )
		{
			id = _id;
			ctrlPoint = _ctrlPoint;
			desc = _desc;
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
		public ArrayList<AazonMutableVect> getControlPointsLocal() {
			return controlPointsLocal;
		}

		
	}
	
	
protected void calcNoteDescPointsInterp(NoteDesc desc, Vector<AazonBaseMutableVect> out) {
		
		int cnta;
		FreqAndBend fab = desc.getFreqAndBend();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getFreqPerNoteU();
		ArrayList<InterpolationPoint> intPoints = bez.getInterpolationPoints();
		final int sz = intPoints.size();
		
		for (cnta = 0; cnta < sz; cnta++) {
			InterpolationPoint pt = intPoints.get(cnta);
			double x1 = pt.getParam();
			double y1 = pt.getValue();
			
			final AazonBaseMutableVect centerVect = new AazonBaseMutableVect( x1 , y1 );
			
			out.add( centerVect );
		}
	}
	
	
	protected void calcNoteDescPoints(NoteDesc desc, GeneralPath out) {
		int cnta;
		FreqAndBend fab = desc.getFreqAndBend();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getFreqPerNoteU();
		int numCurves = bez.getNumCurves();
		
		for (cnta = 0; cnta < numCurves; cnta++) {
			CubicBezierCurve crv = bez.gCurve(cnta);
			
			double[] bezPts = crv.getBezPts();
			double strt = crv.getStartParam();
			double endp = crv.getEndParam();
			
			if( cnta == 0 )
			{
				double x1 = strt;
				double y1 = bezPts[ 0 ];
				out.moveTo((float)x1, (float)y1);
			}
			
			double x1 = 0.75 * strt + 0.25 * endp;
			double y1 = bezPts[ 1 ];
			
			double x2 = 0.25 * strt + 0.75 * endp;
			double y2 = bezPts[ 2 ];
			
			double x3 = endp;
			double y3 = bezPts[ 3 ];
			
			out.curveTo((float)x1, (float)y1, (float)x2, (float)y2, (float)x3, (float)y3);
		}
		
	}
	
	
	protected class NoteViewImmutablePathIteratorFactory implements AbzonImmutablePathIteratorFactory
	{
		AbzonImmutablePathIteratorFactory fac;
		NoteDesc desc;
		
		public NoteViewImmutablePathIteratorFactory( AbzonImmutablePathIteratorFactory _fac , NoteDesc _desc )
		{
			fac = _fac;
			desc = _desc;
		}
		
		public PathIterator iterator(AffineTransform at, double flatness)
		{
			return( new NoteViewPathIterator( fac.iterator(at, flatness) , desc ) );
		}
	}
	
	
	
	protected class NoteViewMutablePathIteratorFactory extends AbzonMutablePathIteratorFactory implements AazonListener
	{
		AbzonMutablePathIteratorFactory fac;
		NoteDesc desc;
		
		public NoteViewMutablePathIteratorFactory( AbzonMutablePathIteratorFactory _fac , NoteDesc _desc )
		{
			fac = _fac;
			desc = _desc;
			fac.add( this );
		}
		
		public AbzonImmutablePathIteratorFactory getImmutableFactory()
		{
			return( new NoteViewImmutablePathIteratorFactory( fac.getImmutableFactory() , desc ) );
		}
		
		public void handleListen()
		{
			fire();
		}
		
		public PathIterator iterator(AffineTransform at, double flatness)
		{
			return( new NoteViewPathIterator( fac.iterator(at, flatness) , desc ) );
		}
		
	}
	
	
	protected AbzonPathIteratorFactory constructNoteViewPathIteratorFactory( AbzonPathIteratorFactory fac , NoteDesc desc )
	{
		if( fac instanceof AbzonMutablePathIteratorFactory )
		{
			return( new NoteViewMutablePathIteratorFactory( (AbzonMutablePathIteratorFactory) fac , desc ) );
		}
		return( new NoteViewImmutablePathIteratorFactory( (AbzonImmutablePathIteratorFactory) fac , desc ) );
	}
	
	
	protected class NoteViewCubicMonotoneCurveGenerator extends AbzonCubicMonotoneCurveGenerator
	{
		NoteDesc desc;
		
		public NoteViewCubicMonotoneCurveGenerator(Vector interps,
				AbzonSlopeGenerator generator, NoteDesc _desc) {
			super(interps, generator);
			desc = _desc;
		}

		protected AbzonPathIteratorFactory generateSegmentCurve(int index , AazonDbl[] slopes ) {
			AbzonPathIteratorFactory afac = super.generateSegmentCurve(index, slopes);
			AbzonPathIteratorFactory fac = constructNoteViewPathIteratorFactory( afac , desc );
			return( fac );
		}
	}
	
	
	protected class NoteViewImmutableGeneralPathFactory extends
		AbzonImmutableGeneralPathFactory {

		NoteDesc desc;

		public NoteViewImmutableGeneralPathFactory( GeneralPath _gp , NoteDesc _desc)
		{
			super( _gp );
			desc = _desc;
		}

		public PathIterator iterator(AffineTransform at, double flatness) {
			return( new NoteViewPathIterator( gp.getPathIterator(at) , desc ) );
		}

}
	
	protected class NoteViewCrtlPtVect extends AazonMutableVect implements AazonListener
	{
		AazonMutableVect ivect;
		NoteDesc desc;
		double x;
		double y;
		
		public NoteViewCrtlPtVect( AazonMutableVect _ivect , NoteDesc _desc )
		{
			ivect = _ivect;
			desc = _desc;
			ivect.add( this );
			handleListen();
		}
		
		public void handleListen()
		{
			boolean updateActual = editMode != EDIT_MODE_DURATION;
			double xv = ivect.getX();
			double yv = ivect.getY();
			if( updateActual )
			{
				double noteU = xv;
				double freqLog = Math.log(yv) / Math.log(2);
				double startBeat = desc.getActualStartBeatNumber();
				double endBeat = desc.getActualEndBeatNumber();

				freqU = (freqLog - startFrequencyLog)
						/ (endFrequencyLog - startFrequencyLog);
				startBeatU = (startBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				endBeatU = (endBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				double beatU = (1 - noteU) * startBeatU + noteU * endBeatU;
				calcXYFromU(beatU, freqU);
				x = beatX;
				y = freqY;
			}
			else
			{
				double noteU = xv;
				double freqLog = Math.log(yv) / Math.log(2);
				double startBeat = desc.getStartBeatNumber();
				double endBeat = desc.getEndBeatNumber();

				freqU = (freqLog - startFrequencyLog)
						/ (endFrequencyLog - startFrequencyLog);
				startBeatU = (startBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				endBeatU = (endBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				double beatU = (1 - noteU) * startBeatU + noteU * endBeatU;
				calcXYFromU(beatU, freqU);
				x = beatX;
				y = freqY;
			}
			fire();
		}
		
		public double getX()
		{
			return( x );
		}
		
		public double getY()
		{
			return( y );
		}
		
	}
	
	protected class NoteViewPathIterator extends FlatteningPathIteratorCoord {
		
		NoteDesc desc;
		
		public NoteViewPathIterator(PathIterator src, NoteDesc _desc)
		{
			super( src );
			desc = _desc;
		}

		final float[] fCoords = new float[ 2 ];
		
		public int currentSegment(float[] coords) {
			boolean updateActual = editMode != EDIT_MODE_DURATION;
			final int type = super.currentSegment( fCoords );
			if( updateActual )
			{
				double noteU = fCoords[0];
				double freqLog = Math.log(fCoords[1]) / Math.log(2);
				double startBeat = desc.getActualStartBeatNumber();
				double endBeat = desc.getActualEndBeatNumber();

				freqU = (freqLog - startFrequencyLog)
						/ (endFrequencyLog - startFrequencyLog);
				startBeatU = (startBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				endBeatU = (endBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				double beatU = (1 - noteU) * startBeatU + noteU * endBeatU;
				calcXYFromU(beatU, freqU);
				coords[0] = (float)beatX;
				coords[1] = (float)freqY;
			}
			else
			{
				double noteU = fCoords[0];
				double freqLog = Math.log(fCoords[1]) / Math.log(2);
				double startBeat = desc.getStartBeatNumber();
				double endBeat = desc.getEndBeatNumber();

				freqU = (freqLog - startFrequencyLog)
						/ (endFrequencyLog - startFrequencyLog);
				startBeatU = (startBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				endBeatU = (endBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				double beatU = (1 - noteU) * startBeatU + noteU * endBeatU;
				calcXYFromU(beatU, freqU);
				coords[0] = (float)beatX;
				coords[1] = (float)freqY;
			}
				return( type );
		}

		final double[] dCoords = new double[ 2 ];
		
		public int currentSegment(double[] coords) {
			boolean updateActual = editMode != EDIT_MODE_DURATION;
			final int type = super.currentSegment( dCoords );
			if( updateActual )
			{
				double noteU = dCoords[0];
				double freqLog = Math.log(dCoords[1]) / Math.log(2);
				double startBeat = desc.getActualStartBeatNumber();
				double endBeat = desc.getActualEndBeatNumber();

				freqU = (freqLog - startFrequencyLog)
						/ (endFrequencyLog - startFrequencyLog);
				startBeatU = (startBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				endBeatU = (endBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				double beatU = (1 - noteU) * startBeatU + noteU * endBeatU;
				calcXYFromU(beatU, freqU);
				coords[0] = beatX;
				coords[1] = freqY;
			}
			else
			{
				double noteU = dCoords[0];
				double freqLog = Math.log(dCoords[1]) / Math.log(2);
				double startBeat = desc.getStartBeatNumber();
				double endBeat = desc.getEndBeatNumber();

				freqU = (freqLog - startFrequencyLog)
						/ (endFrequencyLog - startFrequencyLog);
				startBeatU = (startBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				endBeatU = (endBeat - startBeatNumber)
						/ (endBeatNumber - startBeatNumber);
				double beatU = (1 - noteU) * startBeatU + noteU * endBeatU;
				calcXYFromU(beatU, freqU);
				coords[0] = beatX;
				coords[1] = freqY;
			}
				return( type );
		}

		public double getSquareFlat( double[] hold, int holdIndex )
		{
			final double fl = flatness.getX();
			final double startBeat = desc.getStartBeatNumber();
			final double endBeat = desc.getEndBeatNumber();
			startBeatU = (startBeat - startBeatNumber)
					/ (endBeatNumber - startBeatNumber);
			endBeatU = (endBeat - startBeatNumber)
					/ (endBeatNumber - startBeatNumber);
			final double xma = 2.0 * ( 1.0 - HORIZ_STRT_PCT ) * ( endBeatU - startBeatU );
			double flx = fl / xma;
			final double freqLogD = ( 1.0 / ( hold[ holdIndex + 1 ] ) ) / Math.log(2);
			final double freqUD = fl * freqLogD / (endFrequencyLog - startFrequencyLog);
			final double yma = 2.0 * ( 1.0 - VERT_STRT_PCT ) * freqUD;
			double fly = fl / yma;
			if( Double.isNaN( flx ) )
			{
				flx = 1E-6;
			}
			if( Double.isNaN( fly ) )
			{
				fly = 1E-6;
			}
			double ret = Math.min( flx * flx , fly * fly );
			return( ret );
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

	protected double freqU;

	protected double beatX;

	protected double freqY;

	protected void calcUFromBeat(double startBeat) {
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void calcUFromFreq(double freq) {
		double freqLog = Math.log(freq) / Math.log(2);

		freqU = (freqLog - startFrequencyLog)
				/ (endFrequencyLog - startFrequencyLog);
	}

	protected void calcUValuesActual(NoteDesc desc, double noteU) {
		final int core = 0;
		FreqAndBend fab = desc.getFreqAndBend();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getFreqPerNoteU();
		double freqLog = Math.log(bez.eval(noteU,core)) / Math.log(2);
		double startBeat = desc.getActualStartBeatNumber();
		double endBeat = desc.getActualEndBeatNumber();

		freqU = (freqLog - startFrequencyLog)
				/ (endFrequencyLog - startFrequencyLog);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
		endBeatU = (endBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void calcUValuesSpec(NoteDesc desc, double noteU) {
		final int core = 0;
		FreqAndBend fab = desc.getFreqAndBend();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getFreqPerNoteU();
		double freqLog = Math.log(bez.eval(noteU,core)) / Math.log(2);
		double startBeat = desc.getStartBeatNumber();
		double endBeat = desc.getEndBeatNumber();

		freqU = (freqLog - startFrequencyLog)
				/ (endFrequencyLog - startFrequencyLog);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
		endBeatU = (endBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void calcUValuesActual(NoteDesc desc, InterpolationPoint pt) {
		double freqLog = Math.log(pt.getValue()) / Math.log(2);
		double param = pt.getParam();
		double startBeat = (1 - param) * (desc.getActualStartBeatNumber())
				+ (param) * (desc.getActualEndBeatNumber());

		freqU = (freqLog - startFrequencyLog)
				/ (endFrequencyLog - startFrequencyLog);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void calcUValuesSpec(NoteDesc desc, InterpolationPoint pt) {
		double freqLog = Math.log(pt.getValue()) / Math.log(2);
		double param = pt.getParam();
		double startBeat = (1 - param) * (desc.getStartBeatNumber()) + (param)
				* (desc.getEndBeatNumber());

		freqU = (freqLog - startFrequencyLog)
				/ (endFrequencyLog - startFrequencyLog);
		startBeatU = (startBeat - startBeatNumber)
				/ (endBeatNumber - startBeatNumber);
	}

	protected void updateBaseFreq(double freqU, NoteDesc desc) {
		double freqLog = (1 - freqU) * startFrequencyLog + freqU
				* endFrequencyLog;
		double freq = Math.pow(2.0, freqLog);
		desc.getFreqAndBend().setBaseFreq(freq);
		desc.getFreqAndBend().setWaveInfoDirty(true);
	}

	protected void updateInterpFreq(double freqU, NoteDesc desc,
			InterpolationPoint pt) {
		double freqLog = (1 - freqU) * startFrequencyLog + freqU
				* endFrequencyLog;
		double freq = Math.pow(2.0, freqLog);
		pt.setValue(freq / (desc.getFreqAndBend().getBaseFreq()));
		desc.getFreqAndBend().setUserDefinedBend(true);
		desc.getFreqAndBend().setWaveInfoDirty(true);
	}

	protected void updateInterpPosn(double beatU, NoteDesc desc, int index) {
		ArrayList<InterpolationPoint> points = desc.getFreqAndBend().getBendPerNoteU()
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
			desc.getFreqAndBend().setUserDefinedBend(true);
			desc.getFreqAndBend().setWaveInfoDirty(true);
		}
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
	
	protected double calcYRatio()
	{
		double dx = resizeVect.getX();
		double dy = resizeVect.getY();
		if( dx < 1.0 ) dx = 1.0;
		return( Math.abs( dy ) / Math.abs( dx ) );
	}

	protected void calcXYFromU(double beatU, double freqU) {
		double aX = -1.0;
		double aY = -calcYRatio();
		double bX = 1.0;
		double bY = calcYRatio();

		double cX = (1.0 - HORIZ_STRT_PCT) * aX + HORIZ_STRT_PCT * bX;
		double cY = (1.0 - VERT_STRT_PCT) * aY + VERT_STRT_PCT * bY;
		double dX = (1.0 - HORIZ_END_PCT) * aX + HORIZ_END_PCT * bX;
		double dY = (1.0 - VERT_END_PCT) * aY + VERT_END_PCT * bY;

		beatX = (1.0 - beatU) * cX + beatU * dX;
		freqY = (1.0 - freqU) * cY + freqU * dY;
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
		freqU = (y - cY) / (dY - cY);
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
		undoMgr.handleCommitTempChange(mil);
	}

	/**
	 * @return Returns the endFrequency.
	 */
	public double getEndFrequency() {
		return endFrequency;
	}

	/**
	 * @param endFrequency
	 *            The endFrequency to set.
	 */
	public void setEndFrequency(double endFrequency) {
		this.endFrequency = endFrequency;
		endFrequencyLog = Math.log(endFrequency) / Math.log(2);
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setEndFrequency(mil, endFrequency);
		undoMgr.handleCommitTempChange(mil);
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
		undoMgr.handleCommitTempChange(mil);
	}

	/**
	 * @return Returns the startFrequency.
	 */
	public double getStartFrequency() {
		return startFrequency;
	}

	/**
	 * @param startFrequency
	 *            The startFrequency to set.
	 */
	public void setStartFrequency(double startFrequency) {
		this.startFrequency = startFrequency;
		startFrequencyLog = Math.log(startFrequency) / Math.log(2);
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setStartFrequency(mil, startFrequency);
		undoMgr.handleCommitTempChange(mil);
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
	

	protected void mouserDown(MouseEvent e) throws Throwable {
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		
		double x0 = 0.2 * 1.0 + 0.8 * ( -1.0 );
		
		if( pt.x < x0 )
		{
			System.out.println( "Playing Note..." );
			calcUFromXY(pt.x, pt.y);
			double freqLog = (1 - freqU) * startFrequencyLog + freqU * endFrequencyLog;
			double freq = Math.pow(2.0, freqLog);
			double freqDiv = freq / 261.6; // Divide frequency by middle C frequency.
			double flog2 = Math.log( freqDiv ) / Math.log( 2 );
			double fpitch = flog2 * 12.0 + 60.0;
			try
			{
				NoteChannelEmulator em = NoteChannelEmulator.allocateEmulator();
				em.play( fpitch );
				Thread.sleep( 250 );
				//noteChannel.reset();
				em.stop();
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
			return;
		}
		
		switch (editMode) {

		case EDIT_MODE_BASE_PITCH: {
			ControlSelectNode descn = getDescForLocn(e);

			if (descn != null) {
				lastDragDesc = descn;
				mouseDragEnabled = true;
				
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(descn.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , descn.getDesc() );
					
					
					Appearance app = descn.getLineApp();

					descn.getControllingLineChild().setCrv( fac );
					
					ArrayList tmp = lastDragDesc.getControlPointsLocal();
					
					updateControlPoints(lastDragDesc.getDesc(), lastDragDesc.getControllingLineChild(), 
							null, (ArrayList<AazonBaseMutableVect>)(tmp) );
				
			}
		}
			break;
			
		case EDIT_MODE_INTERP_PITCH: {
			ControlSelectNode descn = getDescForLocn(e);

			if (descn != null) {
				lastDragDesc = descn;
				mouseDragEnabled = true;
			}
		}
			break;

		case EDIT_MODE_INTERP_POSN: {
			ControlSelectNode descn = getDescForLocn(e);

			if (descn != null) {
				NoteDesc desc = descn.getDesc();
				int id = descn.getId();
				int sz = desc.getFreqAndBend().getBendPerNoteU()
						.getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {
					lastDragDesc = descn;
					mouseDragEnabled = true;
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		}
			break;

		case EDIT_MODE_GENERATE_HARMONIES: {
			ControlSelectNode descn = getDescForLocn(e);
			final int core = 0;

			if (descn != null) {
				NoteDesc desc = descn.getDesc();

				double freq = desc.getFreqAndBend().getBaseFreq();

				int id = descn.getId();
				int sz = desc.getFreqAndBend().getBendPerNoteU()
						.getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {

					InterpolationPoint ipt = desc
							.getFreqAndBend().getBendPerNoteU()
							.getInterpolationPoints().get(id);

					double u = ipt.getParam();
					double beatNumber = (1 - u)
							* (desc.getActualStartBeatNumber()) + u
							* (desc.getActualEndBeatNumber());
					ArrayList<NoteDesc> notes = SongData.getNotesAtTime(beatNumber, desc, false, core);

					System.out.println("Starting Dissonance Calculations...");

					DissonanceCalculator calc = new DissonanceCalculatorMultiCore(notes,
							desc, 100, beatNumber);

					ArrayList<Double> minima = new ArrayList<Double>();

					ArrayList<InterpolationPoint> crv = new ArrayList<InterpolationPoint>();

					int max = 6000;
					
					System.out.println("Starting Minimum Calculations...");

					calc.findMinimums(0.25 * freq, 4 * freq, max, minima);

					calc.calcCurve(0.25 * freq, 4 * freq, max, crv);

					System.out.println("Ending Dissonance Calculations...");

					hrmMinima = minima;

					hrmCurve = crv;

					generateHrmList();

				} else {
					Toolkit.getDefaultToolkit().beep();
				}

			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
			break;
			
		case EDIT_MODE_PLOT_FOURIER_HRM: {
			ControlSelectNode descn = getDescForLocn(e);

			if (descn != null) {
				NoteDesc desc = descn.getDesc();

				double freq = desc.getFreqAndBend().getBaseFreq();

				int id = descn.getId();
				int sz = desc.getFreqAndBend().getBendPerNoteU()
						.getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {

					final InterpolationPoint ipt = desc
							.getFreqAndBend().getBendPerNoteU()
							.getInterpolationPoints().get(id);
					final double actualFreq = freq * ( ipt.getValue() );

					int icore = 0;
					double u = ipt.getParam();
					double beatNumber = (1 - u)
							* (desc.getActualStartBeatNumber()) + u
							* (desc.getActualEndBeatNumber());
					double elapsedTimeSecondsGlobal = SongData.getElapsedTimeForBeatBeat(beatNumber, icore);
					FourierRenderingPane.waveStrt = desc.getWaveNumberElapsedTimeSeconds(elapsedTimeSecondsGlobal, icore);
					
					final double MIN_RATIO = FourierRenderingPane.MIN_RATIO;
					
					final double MAX_RATIO = FourierRenderingPane.MAX_RATIO;

					final int SAMPLE_LEN = FourierRenderingPane.SAMPLE_LEN;

					final double DIVIDER = FourierRenderingPane.DIVIDER;

					// final double DELTA = FourierRenderingPane.DELTA;
					
					
					System.out.println("Constructing Fourier Rendering...");
					final double[] xv = new double[SAMPLE_LEN];
					final double[] yv = new double[SAMPLE_LEN];
					int count;
					final double minLn = Math.log( MIN_RATIO );
					final double maxLn = Math.log( MAX_RATIO );

					
					System.out.println( "Generating Waves..." );
					
					final int NUM_CORES = CpuInfo.getNumCores();
					
					
					final WaveForm[] waves = desc.getWaveformArray();
					final boolean[] b = CpuInfo.createBool( false );
					final Runnable[] runn = new Runnable[ NUM_CORES ];
					
					
					System.out.println( "Generating Runnables..." );
					
					int ccnt;
					for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
					{
						final int core = ccnt;
						runn[ core ] = new Runnable()
						{
							public void run()
							{
								int count;
								for (count = core; count < SAMPLE_LEN; count += NUM_CORES ) {
									final double u = ((double) count) / ( SAMPLE_LEN - 1 );
									final double rl = (1-u) * minLn + u * maxLn;
									final double ratio = Math.exp( rl );
						
									xv[count] = actualFreq * ratio;
									yv[count] = FourierRenderingPane.evalCoeff2( waves[ core ] , ratio );
								}
								synchronized( this )
								{
									b[ core ] = true;
									this.notify();
								}
							}
						};
					}
					
					System.out.println( "Starting Threads..." );
					
					CpuInfo.start( runn );
					CpuInfo.wait(runn, b);
					
					double yMax = 0.0;
					for( count = 0 ; count < SAMPLE_LEN ; count++ )
					{
						if( count == 0 )
						{
							yMax = Math.max(yMax, Math.abs(yv[count]));
						}
						else
						{
							yMax = Math.max(yMax, Math.abs(yv[count]));
						}
					}
					
					ArrayList<Double> minima = new ArrayList<Double>();

					ArrayList<InterpolationPoint> crv = new ArrayList<InterpolationPoint>();
					
					for( count = 0 ; count < SAMPLE_LEN ; count++ )
					{
						InterpolationPoint oipt = new InterpolationPoint( xv[ count ] , yv[ count ] );
						crv.add( oipt );
					}
					
					System.out.println("Finished Const...");

					hrmMinima = minima;

					hrmCurve = crv;

					generateHrmList();

				} else {
					Toolkit.getDefaultToolkit().beep();
				}

			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
			break;
			
		case EDIT_MODE_PLOT_VOICE_FOURIER_HRM: {
			ControlSelectNode descn = getDescForLocn(e);

			if (descn != null) {
				NoteDesc desc = descn.getDesc();

				int id = descn.getId();
				int sz = desc.getFreqAndBend().getBendPerNoteU()
						.getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {

					final InterpolationPoint ipt = desc
							.getFreqAndBend().getBendPerNoteU()
							.getInterpolationPoints().get(id);

					int icore = 0;
					double u = ipt.getParam();
					double beatNumber = (1 - u)
							* (desc.getActualStartBeatNumber()) + u
							* (desc.getActualEndBeatNumber());
					double elapsedTimeSecondsGlobal = SongData.getElapsedTimeForBeatBeat(beatNumber, icore);
					
					InstrumentTrack voiceMark = SongData.getVoiceMarkTrack();
					
					ArrayList<NoteDesc> notes = SongData.getNotesAtTime(voiceMark, beatNumber, desc, icore);
					
					NoteDesc sampledNote = notes.get( 0 );
					
					FourierRenderingPane.waveStrt = ( sampledNote.getWaveNumberElapsedTimeSeconds(elapsedTimeSecondsGlobal, icore) ) *
						( FourierRenderingPane.BASE_AUTOCOR_FFREQ );
					
					final double MIN_RATIO = FourierRenderingPane.MIN_RATIO;
					
					final double MAX_RATIO = FourierRenderingPane.MAX_RATIO;

					final int SAMPLE_LEN = FourierRenderingPane.SAMPLE_LEN;

					final double DIVIDER = FourierRenderingPane.DIVIDER;

					// final double DELTA = FourierRenderingPane.DELTA;
					
					
					System.out.println("Constructing Fourier Rendering...");
					final double[] xv = new double[SAMPLE_LEN];
					final double[] yv = new double[SAMPLE_LEN];
					int count;
					final double minLn = Math.log( MIN_RATIO );
					final double maxLn = Math.log( MAX_RATIO );

					
					System.out.println( "Generating Waves..." );
					
					final int NUM_CORES = CpuInfo.getNumCores();
					
					
					final WaveForm[] waves = sampledNote.getWaveformArray();
					final boolean[] b = CpuInfo.createBool( false );
					final Runnable[] runn = new Runnable[ NUM_CORES ];
					
					
					System.out.println( "Generating Runnables..." );
					
					int ccnt;
					for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
					{
						final int core = ccnt;
						runn[ core ] = new Runnable()
						{
							public void run()
							{
								int count;
								for (count = core; count < SAMPLE_LEN; count += NUM_CORES ) {
									final double u = ((double) count) / ( SAMPLE_LEN - 1 );
									final double rl = (1-u) * minLn + u * maxLn;
									final double ratio = Math.exp( rl );
						
									xv[count] = FourierRenderingPane.BASE_AUTOCOR_FFREQ * ratio;
									yv[count] = FourierRenderingPane.evalCoeff2( waves[ core ] , ratio );
								}
								synchronized( this )
								{
									b[ core ] = true;
									this.notify();
								}
							}
						};
					}
					
					System.out.println( "Starting Threads..." );
					
					CpuInfo.start( runn );
					CpuInfo.wait(runn, b);
					
					double yMax = 0.0;
					for( count = 0 ; count < SAMPLE_LEN ; count++ )
					{
						if( count == 0 )
						{
							yMax = Math.max(yMax, Math.abs(yv[count]));
						}
						else
						{
							yMax = Math.max(yMax, Math.abs(yv[count]));
						}
					}
					
					ArrayList<Double> minima = new ArrayList<Double>();

					ArrayList<InterpolationPoint> crv = new ArrayList<InterpolationPoint>();
					
					for( count = 0 ; count < SAMPLE_LEN ; count++ )
					{
						InterpolationPoint oipt = new InterpolationPoint( xv[ count ] , yv[ count ] );
						crv.add( oipt );
					}
					
					System.out.println("Finished Const...");

					hrmMinima = minima;

					hrmCurve = crv;

					generateHrmList();

				} else {
					Toolkit.getDefaultToolkit().beep();
				}

			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
			break;
			
		case EDIT_MODE_SNAP_BASE_PITCH_VOICE: {
			ControlSelectNode descn = getDescForLocn(e);
			final int core = 0;

			if (descn != null) {
				NoteDesc desc = descn.getDesc();

				int id = descn.getId();
				int sz = desc.getFreqAndBend().getBendPerNoteU()
						.getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {

					InterpolationPoint ipt = desc
							.getFreqAndBend().getBendPerNoteU()
							.getInterpolationPoints().get(id);

					double u = ipt.getParam();
					double beatNumber = (1 - u)
							* (desc.getActualStartBeatNumber()) + u
							* (desc.getActualEndBeatNumber());
					
					InstrumentTrack voiceMark = SongData.getVoiceMarkTrack();
					
					ArrayList<NoteDesc> notes = SongData.getNotesAtTime(voiceMark, beatNumber, desc, core);
					
					NoteDesc sampledNote = notes.get( 0 );
					
					NotePitchDigitizer.setVoiceMarkMode();
					NotePitchDigitizer.determineNotePitch( sampledNote , desc , 
							SongData.getCurrentTrack().getAgent() );

					SongListeners.updateViewPanesPitchChange();

				} else {
					Toolkit.getDefaultToolkit().beep();
				}

			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
			break;
			
		case EDIT_MODE_SNAP_INTERP_PITCH_VOICE: {
			ControlSelectNode descn = getDescForLocn(e);
			final int core = 0;

			if (descn != null) {
				NoteDesc desc = descn.getDesc();

				int id = descn.getId();
				int sz = desc.getFreqAndBend().getBendPerNoteU()
						.getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {

					InterpolationPoint ipt = desc
							.getFreqAndBend().getBendPerNoteU()
							.getInterpolationPoints().get(id);

					double u = ipt.getParam();
					double beatNumber = (1 - u)
							* (desc.getActualStartBeatNumber()) + u
							* (desc.getActualEndBeatNumber());
					
					InstrumentTrack voiceMark = SongData.getVoiceMarkTrack();
					
					ArrayList<NoteDesc> notes = SongData.getNotesAtTime(voiceMark, beatNumber, desc, core);
					
					NoteDesc sampledNote = notes.get( 0 );
					
					NotePitchDigitizer.determineInterpPitch( sampledNote , desc , ipt , core );

					SongListeners.updateViewPanesPitchChange();

				} else {
					Toolkit.getDefaultToolkit().beep();
				}

			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
			break;

		case EDIT_MODE_DURATION: {
			ControlSelectNode descn = getDescForLocn(e);

			if (descn != null) {
				int id = descn.getId();
				if (id == 0) {
					mouseDragMode = 0;
				} else {
					mouseDragMode = 1;
				}
				lastDragDesc = descn;
				mouseDragEnabled = true;
				
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(descn.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , descn.getDesc() );
					
					
					Appearance app = descn.getLineApp();

					descn.getControllingLineChild().setCrv( fac );
					
					ArrayList tmp = lastDragDesc.getControlPointsLocal();
					
					updateControlPoints(lastDragDesc.getDesc(), lastDragDesc.getControllingLineChild(), 
							null, (ArrayList<AazonBaseMutableVect>)(tmp) );
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
					box1,
					null,
					null,
					null );
		}
			break;

		case EDIT_INSERT_PITCH_BEFORE: {
			insertPitchBefore(e);
		}
			break;

		case EDIT_INSERT_PITCH_AFTER: {
			insertPitchAfter(e);
		}
			break;

		case EDIT_DEL_PITCH: {
			delPitch(e);
		}
			break;
			
		case EDIT_MODE_INSERT_PORTAMENTO: {
			insertPortamento(e);
		}
			break;
			
		case EDIT_SELECT_PITCH: {
			selectPitch(e);
		}
			break;
			
		case EDIT_MODE_GENERATE_HARMONY_SURF: {
			ControlSelectNode descn = getDescForLocn(e);
			final int core = 0;

			if ( ( descn != null ) && ( descn != lastDragDesc ) ) {
				final NoteDesc desc = descn.getDesc();
				final NoteDesc ldesc = lastDragDesc.getDesc();

				double freq = desc.getFreqAndBend().getBaseFreq();

				int id = descn.getId();
				int sz = desc.getFreqAndBend().getBendPerNoteU()
						.getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {

					InterpolationPoint ipt = desc
							.getFreqAndBend().getBendPerNoteU()
							.getInterpolationPoints().get(id);

					double u = ipt.getParam();
					double beatNumber = (1 - u)
							* (desc.getActualStartBeatNumber()) + u
							* (desc.getActualEndBeatNumber());
					
					SelectionHandler sel = new SelectionHandler()
					{
						public void handleSelection( double au , double bu )
						{
							double f1 = (1-au) * Math.min(startFrequency, endFrequency) + au * Math.max(startFrequency, endFrequency);
							double f2 = (1-bu) * Math.min(startFrequency, endFrequency) + bu * Math.max(startFrequency, endFrequency);
							
							FreqAndBend fr1 = desc.getFreqAndBend();
							FreqAndBend fr2 = ldesc.getFreqAndBend();
							
							fr1.setBaseFreq( f1 );
							fr2.setBaseFreq( f2 );
							
							fr1.setWaveInfoDirty(true);
							fr2.setWaveInfoDirty(true);

							SongListeners.updateViewPanesPitchChange();
						}
					};
					
					DissSurfCalc.plotSurf(beatNumber, desc, ldesc,
							Math.min(startFrequency, endFrequency) , Math.max(startFrequency, endFrequency), sel );
					

				} else {
					Toolkit.getDefaultToolkit().beep();
				}

			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
			break;

			
		}
	}
	
	protected void mouserDrag(MouseEvent e) {
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		
		if (!mouseDragEnabled) {
			try
			{
			mouserDown(e);
			}
			catch( Throwable ex )
			{
				ex.printStackTrace(System.out);
			}
		} else {
			switch (editMode) {
			case EDIT_MODE_BASE_PITCH: {
				calcUFromXY(pt.x, pt.y);
				double frU = freqU;
				updateBaseFreq(frU, lastDragDesc.getDesc());
				lastDragDesc.getDesc().getFreqAndBend().updateWaveInfoDisplayOnly();
				
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(lastDragDesc.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , lastDragDesc.getDesc() );
					

					lastDragDesc.getControllingLineChild().setCrv( fac );
					
					ArrayList tmp = lastDragDesc.getControlPointsLocal();
					
					updateControlPoints(lastDragDesc.getDesc(), lastDragDesc.getControllingLineChild(), 
							null, (ArrayList<AazonBaseMutableVect>)(tmp) );
			}
				break;

			case EDIT_MODE_INTERP_PITCH: {
				calcUFromXY(pt.x, pt.y);
				double frU = freqU;
				NoteDesc desc = lastDragDesc.getDesc();
				int id = lastDragDesc.getId();
				FreqAndBend fab = desc.getFreqAndBend();
				ArrayList<InterpolationPoint> points = fab.getBendPerNoteU().getInterpolationPoints();
				InterpolationPoint interpPoint = points.get(id);
				updateInterpFreq(frU, desc, interpPoint);
				
				/* lastDragDesc.getDesc().getFreqAndBend().updateWaveInfoDisplayOnly();
				
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(lastDragDesc.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , lastDragDesc.getDesc() );
					

					lastDragDesc.getControllingLineChild().setCrv( fac ); */
					

					lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , 
							interpPoint.getValue()   * fab.getBaseFreq() );
			}
				break;

			case EDIT_MODE_DURATION: {
				calcUFromXY(pt.x, pt.y);
				double btU = startBeatU;
				switch (mouseDragMode) {
				case 0:
					updateStartBeat(btU, lastDragDesc.getDesc());
					break;

				case 1:
					updateEndBeat(btU, lastDragDesc.getDesc());
					break;

				case 2:
					break;
				}
				
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(lastDragDesc.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , lastDragDesc.getDesc() );
					

					lastDragDesc.getControllingLineChild().setCrv( fac );
					
					ArrayList tmp = lastDragDesc.getControlPointsLocal();
					
					updateControlPoints(lastDragDesc.getDesc(), lastDragDesc.getControllingLineChild(), 
							null, (ArrayList<AazonBaseMutableVect>)(tmp) );
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

			case EDIT_MODE_INTERP_POSN: {
				calcUFromXY(pt.x, pt.y);
				double btU = startBeatU;
				NoteDesc desc = lastDragDesc.getDesc();
				int id = lastDragDesc.getId();
				updateInterpPosn(btU, desc, id);
				FreqAndBend fab = desc.getFreqAndBend();
				ArrayList<InterpolationPoint> points = fab.getBendPerNoteU().getInterpolationPoints();
				InterpolationPoint interpPoint = points.get(id);
				
				/* lastDragDesc.getDesc().getFreqAndBend().updateWaveInfoDisplayOnly();
				
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(lastDragDesc.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , lastDragDesc.getDesc() );
					

					lastDragDesc.getControllingLineChild().setCrv( fac ); */
					

					lastDragDesc.getControlPointVect().setCoords( interpPoint.getParam() , lastDragDesc.getControlPointVect().getY() );
			}
				break;

			}
		}
	}

	public void mouserDragEnd(MouseEvent e) {
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
	try
	{
		if (mouseDragEnabled) {

			switch( editMode )
			{
			case EDIT_MODE_ZOOM: {
				calcUFromXY(origX, origY);
				double stBeatU = startBeatU;
				double stFreqU = freqU;

				calcUFromXY(newX, newY);
				double endBeatU = startBeatU;
				double endFreqU = freqU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoom(stBeatU, stFreqU, endBeatU, endFreqU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
			
			case EDIT_MODE_ZOOM_X: {
				calcUFromXY(origX, origY);
				double stBeatU = startBeatU;
				double stFreqU = freqU;

				calcUFromXY(newX, newY);
				double endBeatU = startBeatU;
				double endFreqU = freqU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoomX(stBeatU, stFreqU, endBeatU, endFreqU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
			
			case EDIT_MODE_ZOOM_Y: {
				calcUFromXY(origX, origY);
				double stBeatU = startBeatU;
				double stFreqU = freqU;

				calcUFromXY(newX, newY);
				double endBeatU = startBeatU;
				double endFreqU = freqU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoomY(stBeatU, stFreqU, endBeatU, endFreqU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
			
			case EDIT_MODE_BASE_PITCH:
			case EDIT_MODE_DURATION:
			{
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(lastDragDesc.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , lastDragDesc.getDesc() );
					
					
					Appearance app = lastDragDesc.getLineApp();

					lastDragDesc.getControllingLineChild().setCrv( fac );
					
					Appearance lineApp = app;
					
					
					ArrayList tmp = lastDragDesc.getControlPointsLocal();
					
					updateControlPoints(lastDragDesc.getDesc(), lastDragDesc.getControllingLineChild(), 
							lineApp, (ArrayList<AazonBaseMutableVect>)(tmp) );
					
					if( editMode == EDIT_MODE_BASE_PITCH )
					{
						mouseDragEnabled = false;
						return;
					}
			}
				break;
				
			case EDIT_MODE_INTERP_PITCH: {
				calcUFromXY(pt.x, pt.y);
				double frU = freqU;
				NoteDesc desc = lastDragDesc.getDesc();
				int id = lastDragDesc.getId();
				FreqAndBend fab = desc.getFreqAndBend();
				ArrayList<InterpolationPoint> points = fab.getBendPerNoteU().getInterpolationPoints();
				InterpolationPoint interpPoint = points.get(id);
				updateInterpFreq(frU, desc, interpPoint);
				
				/* lastDragDesc.getDesc().getFreqAndBend().updateWaveInfoDisplayOnly();
				
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(lastDragDesc.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , lastDragDesc.getDesc() );
					

					lastDragDesc.getControllingLineChild().setCrv( fac ); */
					

					lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , 
							interpPoint.getValue()   * fab.getBaseFreq() );
			}
				break;
				
			case EDIT_MODE_INTERP_POSN: {
				calcUFromXY(pt.x, pt.y);
				double btU = startBeatU;
				NoteDesc desc = lastDragDesc.getDesc();
				int id = lastDragDesc.getId();
				updateInterpPosn(btU, desc, id);
				FreqAndBend fab = desc.getFreqAndBend();
				ArrayList<InterpolationPoint> points = fab.getBendPerNoteU().getInterpolationPoints();
				InterpolationPoint interpPoint = points.get(id);
				
				/* lastDragDesc.getDesc().getFreqAndBend().updateWaveInfoDisplayOnly();
				
				GeneralPath pointsa = new GeneralPath();

				calcNoteDescPoints(lastDragDesc.getDesc(), pointsa);

				NoteViewImmutableGeneralPathFactory fac = new NoteViewImmutableGeneralPathFactory( pointsa , lastDragDesc.getDesc() );
					

					lastDragDesc.getControllingLineChild().setCrv( fac ); */
					

					lastDragDesc.getControlPointVect().setCoords( interpPoint.getParam() , 
							lastDragDesc.getControlPointVect().getY()  * fab.getBaseFreq() );
			}
				break;
				
				default:
				{ }
				break;
			}
			

			final int core = 0;
			SongData.getCurrentTrack().updateTrackFrames( core );
			
			switch( editMode )
			{
			
			case EDIT_MODE_ZOOM: 
			case EDIT_MODE_ZOOM_X: 
			case EDIT_MODE_ZOOM_Y: 
				refreshDisplayList();
				break;
			
			
			case EDIT_MODE_DURATION:
				SongListeners.updateViewPanesNoteChange();
				break;
				
			case EDIT_MODE_BASE_PITCH:
			case EDIT_MODE_INTERP_PITCH: 
			case EDIT_MODE_INTERP_POSN: 
				SongListeners.updateViewPanesPitchChange();
				break;
				
			default:
				SongListeners.updateViewPanes();
				break;
			}
			
			mouseDragEnabled = false;
		}
	}
	catch( Throwable ex )
	{
		ex.printStackTrace( System.out );
	}
	}
	
	protected void updateZoom(double stBeatU, double stFreqU, double endBeatU,
			double endFreqU) {
		double stFreqLog = (1 - stFreqU) * startFrequencyLog + stFreqU
				* endFrequencyLog;
		double stFreq = Math.pow(2.0, stFreqLog);

		double stBeat = (1 - stBeatU) * startBeatNumber + stBeatU
				* endBeatNumber;

		double endFreqLog = (1 - endFreqU) * startFrequencyLog + endFreqU
				* endFrequencyLog;
		double endFreq = Math.pow(2.0, endFreqLog);

		double endBeat = (1 - endBeatU) * startBeatNumber + endBeatU
				* endBeatNumber;

		setStartBeatNumber(Math.min(stBeat,endBeat));
		setEndBeatNumber(Math.max(stBeat,endBeat));
		setStartFrequency(Math.min(stFreq,endFreq));
		setEndFrequency(Math.max(stFreq,endFreq));
	}
	
	protected void updateZoomX(double stBeatU, double stFreqU, double endBeatU,
			double endFreqU) {
		//double stFreqLog = (1 - stFreqU) * startFrequencyLog + stFreqU
		//		* endFrequencyLog;
		//double stFreq = Math.pow(2.0, stFreqLog);

		double stBeat = (1 - stBeatU) * startBeatNumber + stBeatU
				* endBeatNumber;

		//double endFreqLog = (1 - endFreqU) * startFrequencyLog + endFreqU
		//		* endFrequencyLog;
		//double endFreq = Math.pow(2.0, endFreqLog);

		double endBeat = (1 - endBeatU) * startBeatNumber + endBeatU
				* endBeatNumber;

		setStartBeatNumber(Math.min(stBeat,endBeat));
		setEndBeatNumber(Math.max(stBeat,endBeat));
		//setStartFrequency(Math.min(stFreq,endFreq));
		//setEndFrequency(Math.max(stFreq,endFreq));
	}
	
	protected void updateZoomY(double stBeatU, double stFreqU, double endBeatU,
			double endFreqU) {
		double stFreqLog = (1 - stFreqU) * startFrequencyLog + stFreqU
				* endFrequencyLog;
		double stFreq = Math.pow(2.0, stFreqLog);

		//double stBeat = (1 - stBeatU) * startBeatNumber + stBeatU
		//		* endBeatNumber;

		double endFreqLog = (1 - endFreqU) * startFrequencyLog + endFreqU
				* endFrequencyLog;
		double endFreq = Math.pow(2.0, endFreqLog);

		//double endBeat = (1 - endBeatU) * startBeatNumber + endBeatU
		//		* endBeatNumber;

		//setStartBeatNumber(Math.min(stBeat,endBeat));
		//setEndBeatNumber(Math.max(stBeat,endBeat));
		setStartFrequency(Math.min(stFreq,endFreq));
		setEndFrequency(Math.max(stFreq,endFreq));
	}
	
	public void editBasePitch() {
		setModeOptionalRefresh( EDIT_MODE_BASE_PITCH );
	}

	public void editInterpPitch() {
		setModeOptionalRefresh( EDIT_MODE_INTERP_PITCH );
	}

	public void insertPitchBefore() {
		setModeOptionalRefresh( EDIT_INSERT_PITCH_BEFORE );
	}

	public void insertPitchAfter() {
		setModeOptionalRefresh( EDIT_INSERT_PITCH_AFTER );
	}

	public void delPitch() {
		setModeOptionalRefresh( EDIT_DEL_PITCH );
	}
	
	public void selectPitch() {
		setModeOptionalRefresh( EDIT_SELECT_PITCH );
	}

	public void editInterpPosn() {
		setModeOptionalRefresh( EDIT_MODE_INTERP_POSN );
	}

	public void generateHarmonies() {
		setModeOptionalRefresh( EDIT_MODE_GENERATE_HARMONIES );
	}
	
	public void generateHarmonySurf() {
		setModeOptionalRefresh( EDIT_MODE_GENERATE_HARMONY_SURF );
	}
	
	public void plotFourierHrm() {
		setModeOptionalRefresh( EDIT_MODE_PLOT_FOURIER_HRM );
	}
	
	public void plotVoiceFourierHrm() {
		setModeOptionalRefresh( EDIT_MODE_PLOT_VOICE_FOURIER_HRM );
	}
	
	public void insertPortamento()
	{
		setModeOptionalRefresh( EDIT_MODE_INSERT_PORTAMENTO );
	}
	
	public void clearHarmonies()
	{
		hrmMinima.clear();
		hrmCurve.clear();
		hrmMinimaYellowPathIteratorFactory.setCrv( new AbzonImmutableGeneralPathFactory( new GeneralPath() ) );
	}
	
	public void snapBasePitchVoice() {
		setModeOptionalRefresh( EDIT_MODE_SNAP_BASE_PITCH_VOICE );
	}
	
	public void deleteAllAfterTap() throws Throwable
	{
		System.out.println( "Deleting all after tap." );
		final int core = 0;
		Double beat = OutputChoiceInterface.getBeatNumberForFirstTap();
		if( beat == null )
		{
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		boolean dela = false;
		double cutBeatNumber = beat.doubleValue();
		System.out.println( "beat number " + cutBeatNumber );
		
		for( final InstrumentTrack track : SongData.instrumentTracks )
		{
			ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
			boolean del = false;
			for( final TrackFrame tr : trackFrames )
			{
				ArrayList<NoteDesc> notes = tr.getNotes();
				Iterator<NoteDesc> itb = notes.iterator();
				while( itb.hasNext() )
				{
					NoteDesc desc = itb.next();
					System.out.println( desc.getStartBeatNumber() );
					if( desc.getStartBeatNumber() > cutBeatNumber )
					{
						itb.remove();
						tr.clearNoteIndexMultiCore();
						del = true;
					}
				}
			}
			
			if( del )
			{
				track.updateTrackFrames(core);
				dela = true;
			}
		}
		
		if( !dela )
		{
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		SongListeners.updateViewPanesNoteChange();
	}
	
	public void deleteSelectedNote() throws Throwable
	{
		final int core = 0;
		InstrumentTrack track = SongData.getCurrentTrack();
		NoteDesc note = lastDragDesc.getDesc();
		ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
		boolean del = false;
		for( final TrackFrame tr : trackFrames )
		{
			ArrayList<NoteDesc> notes = tr.getNotes();
			if( notes.contains( note ) )
			{
				notes.remove( note );
				tr.clearNoteIndexMultiCore();
				del = true;
			}
		}
		
		if( !del )
		{
			Toolkit.getDefaultToolkit().beep();
		}
		
		if( del )
		{
			track.updateTrackFrames(core);
			SongListeners.updateViewPanesNoteChange();
		}
	}
	
	public void deleteAllNotesInTrack() throws Throwable
	{
		final int core = 0;
		InstrumentTrack track = SongData.getCurrentTrack();
		ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
		boolean del = false;
		for( final TrackFrame tr : trackFrames )
		{
			ArrayList<NoteDesc> notes = tr.getNotes();
			notes.clear();
			del = true;
		}
		
		if( !del )
		{
			Toolkit.getDefaultToolkit().beep();
		}
		
		if( del )
		{
			track.updateTrackFrames(core);
			SongListeners.updateViewPanesNoteChange();
		}
	}
	
	
	public void deleteTrackNotes() throws Throwable
	{
		System.out.println( "Deleting track notes." );
		final int core = 0;
		Double sbeat = OutputChoiceInterface.getBeatNumberForFirstTap();
		Double ebeat = OutputChoiceInterface.getBeatNumberForSecondTap();
		if( ( sbeat == null ) || ( ebeat == null ) )
		{
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		boolean dela = false;
		double cutStartBeatNumber = sbeat.doubleValue();
		double cutEndBeatNumber = ebeat.doubleValue();
		System.out.println( "beat number " + cutStartBeatNumber );
		System.out.println( "beat number " + cutEndBeatNumber );
		
		InstrumentTrack track = SongData.getCurrentTrack();
		ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
		boolean del = false;
		for( final TrackFrame tr : trackFrames )
		{
			ArrayList<NoteDesc> notes = tr.getNotes();
			Iterator<NoteDesc> ita = notes.iterator();
			while( ita.hasNext() )
			{
				NoteDesc desc = ita.next();
				System.out.println( desc.getStartBeatNumber() );
				if( ( desc.getStartBeatNumber() > cutStartBeatNumber ) && 
						( desc.getEndBeatNumber() < cutEndBeatNumber ) )
				{
					ita.remove();
					tr.clearNoteIndexMultiCore();
					del = true;
				}
			}
		}
		
		if( !del )
		{
			Toolkit.getDefaultToolkit().beep();
		}
		
		if( del )
		{
			track.updateTrackFrames(core);
			SongListeners.updateViewPanesNoteChange();
		}
	}

	
	public void snapInterpPitchVoice() {
		setModeOptionalRefresh( EDIT_MODE_SNAP_INTERP_PITCH_VOICE );
	}

	public void snapBasePitch() {
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			double freq = freqRect.getBaseFreq();

			int countMelodic;
			int countNote;

			double bestFreq = 0.0;
			double bestDiff = 1E+6;

			final int szz = NoteTable.getScaleSize();
			for (countMelodic = 0; countMelodic < 15; countMelodic++) {
				for (countNote = 0; countNote < szz; countNote++) {
					double nFreq = NoteTable
							.getNoteFrequencyDefaultScale_Key(countMelodic,
									countNote);
					if (Math.abs(nFreq - freq) < bestDiff) {
						bestDiff = Math.abs(nFreq - freq);
						bestFreq = nFreq;
					}
				}
			}

			if (bestFreq > 0.0) {
				freqRect.setBaseFreq(bestFreq);
				freqRect.setWaveInfoDirty(true);
			}

			SongListeners.updateViewPanesPitchChange();
		}
	}

	public void snapBasePitchHrm() {
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			double freq = freqRect.getBaseFreq();

			double bestFreq = 0.0;
			double bestDiff = 1E+6;

			for( final Double nFreqD : hrmMinima ) {
				double nFreq = nFreqD;
				if (Math.abs(nFreq - freq) < bestDiff) {
					bestDiff = Math.abs(nFreq - freq);
					bestFreq = nFreq;
				}
			}

			if (bestFreq > 0.0) {
				freqRect.setBaseFreq(bestFreq);
				freqRect.setWaveInfoDirty(true);
			}

			SongListeners.updateViewPanesPitchChange();
		}
	}
	
	public void snapPitchFromPasteBuffer() {
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			
			freqRect.setBaseFreq( SongData.PASTE_BUFFER_FREQ );
			freqRect.setWaveInfoDirty(true);

			SongListeners.updateViewPanesPitchChange();
		}
	}
	
	public void baseOctavePlus()
	{
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			double freq = freqRect.getBaseFreq();

			freqRect.setBaseFreq( freq * 2.0 );
			freqRect.setWaveInfoDirty(true);

			SongListeners.updateViewPanesPitchChange();
		}
	}
	
	public void baseOctaveMinus()
	{
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			double freq = freqRect.getBaseFreq();

			freqRect.setBaseFreq( freq / 2.0 );
			freqRect.setWaveInfoDirty(true);

			SongListeners.updateViewPanesPitchChange();
		}
	}
	
	public void copyPitchToPasteBuffer()
	{
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			double freq = freqRect.getBaseFreq();

			SongData.PASTE_BUFFER_FREQ = freq;

			System.out.println( "Set Paste Buffer Freq To " + freq );
		}
	}
	
	public void copyNoteToPasteBuffer()
	{
		if (lastDragDesc != null) {
			NoteDesc desc = lastDragDesc.getDesc();

			SongData.PASTE_BUFFER_NOTE = desc;

			System.out.println( "Set Paste Buffer Note To " + desc );
		}
	}
	
	public void snapBasePitchAgentOctave()
	{
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			double freq = freqRect.getBaseFreq();
			if( freq > 0.00001 )
			{
				double bfreq = SongData.getCurrentTrack().getAgent().getFrequencyA();

				while( freq > ( 2.0 * bfreq ) )
				{
					freq = freq / 2.0;
				}
			
				while( freq < bfreq )
				{
					freq = freq * 2.0;
				}
			
				freqRect.setBaseFreq(freq);
				freqRect.setWaveInfoDirty(true);

				SongListeners.updateViewPanesPitchChange();
			}
		}
	}

	public void snapInterpPitch() {
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			int id = lastDragDesc.getId();
			PiecewiseCubicMonotoneBezierFlatMultiCore crv = lastDragDesc.getDesc()
					.getFreqAndBend().getBendPerNoteU();
			InterpolationPoint pt = crv.getInterpolationPoints().get(id);
			double freq = freqRect.getBaseFreq() * pt.getValue();

			int countMelodic;
			int countNote;

			double bestFreq = 0.0;
			double bestDiff = 1E+6;

			final int szz = NoteTable.getScaleSize();
			for (countMelodic = 0; countMelodic < 15; countMelodic++) {
				for (countNote = 0; countNote < szz; countNote++) {
					double nFreq = NoteTable
							.getNoteFrequencyDefaultScale_Key(countMelodic,
									countNote);
					if (Math.abs(nFreq - freq) < bestDiff) {
						bestDiff = Math.abs(nFreq - freq);
						bestFreq = nFreq;
					}
				}
			}

			if (bestFreq > 0.0) {
				pt.setValue(bestFreq / freqRect.getBaseFreq());
				freqRect.setUserDefinedBend(true);
				freqRect.setWaveInfoDirty(true);
			}

			SongListeners.updateViewPanesPitchChange();
		}
	}

	public void snapInterpPitchHrm() {
		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();
			int id = lastDragDesc.getId();
			PiecewiseCubicMonotoneBezierFlatMultiCore crv = lastDragDesc.getDesc()
					.getFreqAndBend().getBendPerNoteU();
			InterpolationPoint pt = crv.getInterpolationPoints().get(id);
			double freq = freqRect.getBaseFreq() * pt.getValue();

			double bestFreq = 0.0;
			double bestDiff = 1E+6;

			for( final Double nFreqD : hrmMinima ) {
				double nFreq = nFreqD;
				if (Math.abs(nFreq - freq) < bestDiff) {
					bestDiff = Math.abs(nFreq - freq);
					bestFreq = nFreq;
				}
			}

			if (bestFreq > 0.0) {
				pt.setValue(bestFreq / freqRect.getBaseFreq());
				freqRect.setUserDefinedBend(true);
				freqRect.setWaveInfoDirty(true);
			}

			SongListeners.updateViewPanesPitchChange();
		} 
	}

	public void produceHarmony(final int trackNumber) throws Throwable {
		final int core = 0;
		InstrumentTrack track = SongData.getCurrentTrack();

		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();

			NoteDesc desc = new NoteDesc();
			FreqAndBend freqRect1 = desc.getFreqAndBend();
			
			final double harmonyRatio = getHarmonyRatio( trackNumber );

			freqRect1.setBaseFreq( harmonyRatio * freqRect.getBaseFreq() );
			freqRect1.setWaveInfoDirty(true);

			InterpolationPoint pt0 = new InterpolationPoint(0.0, 1.0);
			InterpolationPoint pt1 = new InterpolationPoint(0.5, 1.0);
			InterpolationPoint pt2 = new InterpolationPoint(1.0, 1.0);
			freqRect1.getBendPerNoteU().getInterpolationPoints().add(pt0);
			freqRect1.getBendPerNoteU().getInterpolationPoints().add(pt1);
			freqRect1.getBendPerNoteU().getInterpolationPoints().add(pt2);

			desc
					.setStartBeatNumber(lastDragDesc.getDesc()
							.getStartBeatNumber());
			desc.setEndBeatNumber(lastDragDesc.getDesc().getEndBeatNumber());
			desc.setWaveform(lastDragDesc.getDesc().getWaveform(core));
			desc.setNoteEnvelope(lastDragDesc.getDesc().getNoteEnvelope(core));
			desc
					.setActualNoteEnvelope(lastDragDesc.getDesc()
							.getNoteEnvelope(core));
			desc.setWaveEnvelope(lastDragDesc.getDesc().getWaveEnvelope(core));

			ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
			
			System.out.println( "Select Track Number " + trackNumber );

			int tsz = trackFrames.size();
			int count;
			if (tsz < (trackNumber + 1)) {
				for (count = tsz; count < (trackNumber + 1); count++) {
					trackFrames.add(new TrackFrame());
				}
			}

			TrackFrame tr = trackFrames.get(trackNumber);
			tr.getNotes().add(desc); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

			track.updateTrackFrames(core);
			SongListeners.updateViewPanesNoteChange();
		}
	}
	
	
	public void fillHarmony() throws Throwable
	{
		final Harmony h = TestPlayer2.editPackHarmony.processHarmony( new HashMap() );
		
		final double[] ratios = h.calcHarmony();
		
		int sz = Math.min( ratios.length - 1 , 3 );
		
		int count;
		
		final int[] ind = { 2 , 3 , 0 };
		
		for( count = 0 ; count < sz ; count++ )
		{
			produceHarmony( ind[ count ] );
		}
		
	}
	
	
	protected double getHarmonyRatio( final int trackNumber )
	{
		int tr = trackNumber;
		if( tr == 0 ) tr = 4;
		if( tr == 1 ) tr = 5;
		tr -= 1;
		
		final Harmony h = TestPlayer2.editPackHarmony.processHarmony( new HashMap() );
		
		final double[] ratios = h.calcHarmony();
		
		if( tr < ratios.length )
		{
			return( ratios[ tr ] );
		}
		
		if( ratios.length < 2 )
		{
			throw( new RuntimeException( "Not Supported" ) );
		}
		
		return( ratios[ 1 ] );
	}
	
	public void editHarmony()
	{
		InstrumentTrack track = SongData.getCurrentTrack();
		HashSet<BuilderNode> s = TestPlayer2.editPackHarmony.getElem();
		Object univ1 = AczonUnivAllocator.allocateUniv();
		Object univ2 = AczonUnivAllocator.allocateUniv();
		DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesHarmony());
		SongListeners.updateViewPanes();
	}
	
	public void produceHarmonyMohe() throws Throwable{
		final int core = 0;
		InstrumentTrack track = SongData.getCurrentTrack();

		if (lastDragDesc != null) {
			FreqAndBend freqRect = lastDragDesc.getDesc().getFreqAndBend();

			NoteDesc desc = new NoteDesc();
			FreqAndBend freqRect1 = desc.getFreqAndBend();

			freqRect1.setBaseFreq( 98.10625 );
			freqRect1.setWaveInfoDirty(true);

			InterpolationPoint pt0 = new InterpolationPoint(0.0, 1.0);
			InterpolationPoint pt1 = new InterpolationPoint(0.5, 1.0);
			InterpolationPoint pt2 = new InterpolationPoint(1.0, 1.0);
			freqRect1.getBendPerNoteU().getInterpolationPoints().add(pt0);
			freqRect1.getBendPerNoteU().getInterpolationPoints().add(pt1);
			freqRect1.getBendPerNoteU().getInterpolationPoints().add(pt2);

			desc
					.setStartBeatNumber(lastDragDesc.getDesc()
							.getStartBeatNumber());
			desc.setEndBeatNumber(lastDragDesc.getDesc().getEndBeatNumber());
			desc.setWaveform(lastDragDesc.getDesc().getWaveform(core));
			desc.setNoteEnvelope(lastDragDesc.getDesc().getNoteEnvelope(core));
			desc
					.setActualNoteEnvelope(lastDragDesc.getDesc()
							.getNoteEnvelope(core));
			desc.setWaveEnvelope(lastDragDesc.getDesc().getWaveEnvelope(core));

			ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
			
			int trackNumber = 2;
			
			System.out.println( "Select Track Number " + trackNumber );

			int tsz = trackFrames.size();
			int count;
			if (tsz < (trackNumber + 1)) {
				for (count = tsz; count < (trackNumber + 1); count++) {
					trackFrames.add(new TrackFrame());
				}
			}

			TrackFrame tr = trackFrames.get(trackNumber);
			tr.getNotes().add(desc); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

			track.updateTrackFrames(core);
			SongListeners.updateViewPanesNoteChange();
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
	
	protected void insertPitchBefore(MouseEvent e) {
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		NoteDesc desc = descn.getDesc();
		FreqAndBend fab = desc.getFreqAndBend();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getBendPerNoteU();
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
			fab.setUserDefinedBend(true);
			fab.setWaveInfoDirty(true);
			SongListeners.updateViewPanesPitchChange();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	protected void insertPitchAfter(MouseEvent e) {
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		NoteDesc desc = descn.getDesc();
		FreqAndBend fab = desc.getFreqAndBend();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getBendPerNoteU();
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
			fab.setUserDefinedBend(true);
			fab.setWaveInfoDirty(true);
			SongListeners.updateViewPanesPitchChange();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	public void editDuration() {
		setModeOptionalRefresh( EDIT_MODE_DURATION );
	}

	public void zoom() {
		setModeOptionalRefresh( EDIT_MODE_ZOOM );
	}
	
	public void zoomX() {
		setModeOptionalRefresh( EDIT_MODE_ZOOM_X );
	}
	
	public void zoomY() {
		setModeOptionalRefresh( EDIT_MODE_ZOOM_Y );
	}

	public void unZoomLimits() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartFrequency(2.0);
		setEndFrequency(100000.0);
		setStartBeatNumber( SongData.START_BEAT );
		setEndBeatNumber( SongData.END_BEAT );
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void unZoom() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartFrequency(2.0);
		setEndFrequency(100000.0);
		setStartBeatNumber( -0.01 * ( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) ) );
		setEndBeatNumber( ( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) ) * 1.01 + 2 );
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void unZoom2X() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		double midFrequency = ( getStartFrequency() + getEndFrequency() ) / 2.0;
		double deltaFrequency = Math.abs( getEndFrequency() - getStartFrequency() );
		setStartFrequency( Math.max( midFrequency - deltaFrequency  , 2.0 ) );
		setEndFrequency(midFrequency + deltaFrequency);
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
	
	protected void selectPitch(MouseEvent e) {
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		// NoteDesc desc = descn.getDesc();
		lastDragDesc = descn;
		System.out.println( "Pitch Selected..." );
	}
	
	protected void delPitch(MouseEvent e) {
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		NoteDesc desc = descn.getDesc();
		FreqAndBend fab = desc.getFreqAndBend();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getBendPerNoteU();
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
			fab.setUserDefinedBend(true);
			fab.setWaveInfoDirty(true);
			SongListeners.updateViewPanesPitchChange();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	protected void insertPortamento(MouseEvent e) throws Throwable {
		System.out.println("InsertingPortamento");
		final int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		NoteDesc desc = descn.getDesc();
		FreqAndBend fab = desc.getFreqAndBend();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getBendPerNoteU();
		InterpolationPoint pt = bez.getInterpolationPoints().get( id );
		if( desc.getPortamentoDesc() == null )
		{
			desc.setPortamentoDesc( new PortamentoDesc() );
		}
		PortamentoTransition transit = new PortamentoTransition();
		transit.setTransitionNoteU( pt.getParam() );
		transit.setPitchRatio( SongData.PASTE_BUFFER_FREQ / desc.getFreqAndBend().getBaseFreq() );
		desc.getPortamentoDesc().addTransition( transit );
		SongData.getCurrentTrack().updateTrackFrames(core);
		SongListeners.updateViewPanesPitchChange();
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
