




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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import jundo.runtime.ExtMilieuRef;
import labdaw.undo.pdx_TempoViewPaneModel_pdx_ObjectRef;
import labdaw.undo.pdx_TempoViewPaneModel_pdx_PairRef;
import verdantium.ProgramDirector;
import verdantium.VerdantiumPropertiesEditor;
import verdantium.undo.UTag;
import verdantium.undo.UndoManager;
import aazon.AazonEnt;
import aazon.AazonImmutableGroup;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonListener;
import aazon.AazonMutableGroup;
import aazon.AazonSmartBlockText;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartOutlineRectangle;
import aazon.dbl.AazonBaseImmutableDbl;
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
import bezier.BezierCubicNonClampedCoefficientFlatMultiCore;
import bezier.BezierCubicNonClampedCoefficientSloping;
import bezier.BezierQuarticNonClampedCoefficientSlopingMultiCore;
import bezier.CubicBezierCurve;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;
import bezier.PiecewiseCubicMonotoneBezierSloping;
import bezier.PiecewiseQuarticBezierSlopingMultiCore;


public class TempoViewPane2 extends JComponent implements MouseListener,
		MouseMotionListener , PropertyChangeListener {
	
	protected AczonRootFactory rootFactory = null;
	
	protected AazonMutableGroup bkgndLineLightGray = new AazonMutableGroup();
	
	protected AazonMutableGroup frontGreen = new AazonMutableGroup();
	
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
	

	protected double startSecondsNumber;

	protected double endSecondsNumber;

	protected double startLevel;

	protected double endLevel;
	
	protected ArrayList<InterpolationPoint> undoList = null;
	
	protected pdx_TempoViewPaneModel_pdx_ObjectRef model = null;

	protected static final double VERT_STRT_PCT = 0.0;

	protected static final double HORIZ_STRT_PCT = 0.2;

	protected static final double VERT_END_PCT = 0.8;

	protected static final double HORIZ_END_PCT = 1.0;

	protected static final int CRV_STROKE_SIZE = 100;

	protected int editMode = EDIT_TEMPO_INTERP_LEVEL;

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

	public static final int EDIT_INSERT_TEMPO_INTERP_POINT_BEFORE = 3;
	
	public static final int EDIT_INSERT_PASTE_BUFFER_TEMPO_INTERP_POINT_BEFORE = 4;

	public static final int EDIT_INSERT_TEMPO_INTERP_POINT_AFTER = 5;
	
	public static final int EDIT_INSERT_PASTE_BUFFER_TEMPO_INTERP_POINT_AFTER = 6;

	public static final int EDIT_DEL_TEMPO_INTERP_POINT = 7;

	public static final int EDIT_TEMPO_INTERP_POSN = 8;

	public static final int EDIT_TEMPO_INTERP_LEVEL = 9;
	
	public static final int EDIT_MODE_SELECT_TEMPO = 10;

	
	
	public TempoViewPane2( UndoManager _undoMgr ) {
		super();
		undoMgr = _undoMgr;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		pdx_TempoViewPaneModel_pdx_PairRef pair = pdx_TempoViewPaneModel_pdx_ObjectRef.pdxm_new_TempoViewPaneModel(
				mil , 0.0 , 0.0 , 0.0 , 0.0 );
		mil = pair.getMilieu();
		model = (pdx_TempoViewPaneModel_pdx_ObjectRef)( pair.getObject() );
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
		startSecondsNumber = model.pdxm_getStartSecondsNumber( mil );
		endSecondsNumber = model.pdxm_getEndSecondsNumber( mil );
		startLevel = model.pdxm_getStartTempo( mil );
		endLevel = model.pdxm_getEndTempo( mil );
		refreshDisplayList();
	}
	
	
	public void refreshDisplayList() {
		refreshDisplayListTempo();
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
		ArrayList<AazonEnt> frontTxtBlack = new ArrayList<AazonEnt>();
		
		

		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


		ArrayList<Point3d> points = new ArrayList<Point3d>();

		final int core = 0;
		final double startBeatNumber = SongData.getBeatNumber(startSecondsNumber, core);
		final double endBeatNumber = SongData.getBeatNumber(endSecondsNumber, core);
		final double startMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber( startBeatNumber , core);
		final double endMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber( endBeatNumber , core);

		if (((endMeasure - startMeasure) > 10)) {
			double delta = (endMeasure - startMeasure) / 10.0;
			double count;
			for (count = startMeasure; count < endMeasure; count = count
					+ delta) {
				int iCount = (int) (count);
				double qbeat = SongData.measuresStore.getBeatNumberForMeasureNumber( iCount );
				String str = "" + iCount;
				calcUFromSeconds( SongData.getElapsedTimeForBeatBeat( qbeat , core ) );
				calcXYFromU(startSecondsU, levelU);
				double x1 = secondsX;
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
					calcUFromSeconds( SongData.getElapsedTimeForBeatBeat( qbeat , core ) );
					calcXYFromU(startSecondsU, levelU);
					double x1 = secondsX;
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
					calcUFromSeconds( SongData.getElapsedTimeForBeatBeat( qbeat , core ) );
					calcXYFromU(startSecondsU, levelU);
					double x1 = secondsX;
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
			calcXYFromU(startSecondsU, levelU);
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
		
		
		
		
		
		controlSelectList.clear();
		
		
		ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
		{
					
					ArrayList<AazonEnt> controllingLineGroup = null;
					AbzonMutableGeneralPathFactory controllingLineChild = null;
					AazonEnt shape = null;
					Vector<AazonBaseMutableVect> interp = null;
						
						Appearance app = AczonColor.getLineGreen();
						
						if( ( editMode == EDIT_TEMPO_INTERP_POSN ) || ( editMode == EDIT_TEMPO_INTERP_LEVEL ) )
						{
							interp = new Vector<AazonBaseMutableVect>();
							
							calcTempoPointsInterp(interp);
							
							AbzonSlopeGenerator generator = new AbzonMonotoneCubicSlopeGenerator( interp );
							
							AbzonCubicMonotoneCurveGenerator crv = new AbzonCubicMonotoneCurveGenerator( interp, generator);
							
							AbzonPathIteratorFactory[] fac = crv.generateCurves();

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
							
							controllingLineChild = null;
						}
						else
						{
						GeneralPath pointsa = new GeneralPath();

						calcTempoPoints(pointsa);

						AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

						shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						
						controllingLineChild = fac;
						}
						
						controllingLineGroup = frontGreenLst;
						
						
						controllingLineGroup.add( shape );
						Appearance lineApp = app;
						
						
						if( ( editMode == EDIT_TEMPO_INTERP_POSN ) || ( editMode == EDIT_TEMPO_INTERP_LEVEL ) )
						{
							generateControlPointsInterp(interp,
									controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						else
						{
							generateControlPoints(controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
				}
		frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
		
		
		frontGreen.setGrp( new AazonImmutableGroup( frontGreenLst ) );
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

		
		
		
		
		AazonEnt[] objOrderEnts = { bkgndLineLightGray , frontGreen
				, backSprites , frontControlPointsCyan , frontSprites  , rectA , rectB , frontTextBlack };
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
	

	
	protected void generateControlPointsInterp(Vector<AazonBaseMutableVect> interpPts , AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<InterpolationPoint> intp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
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
						new ControlSelectNode( cnta , ctrlPoint , 
								controllingLineChild, 
								centerVect, lineApp, pt , controlPointsLocal );
					controlSelectList.add( controlSelectNode );
				}
			}
			
	}
	
	
	
	protected void generateControlPoints(AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<InterpolationPoint> intp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 0; cnta < sza; cnta++) {
				InterpolationPoint pt = intp.get(cnta);
				calcUValuesActual(pt);
				calcXYFromU(startSecondsU, levelU);
				double x1 = secondsX;
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
						new ControlSelectNode( cnta , ctrlPoint , 
								controllingLineChild, 
								centerVect, lineApp, pt , controlPointsLocal );
					controlSelectList.add( controlSelectNode );
				}
			}
			
	}
	
	
	
	public static class ControlSelectNode
	{
		int id;
		Point3f ctrlPoint;
		AbzonMutableGeneralPathFactory controllingLineChild;
		AazonBaseMutableVect controlPointVect;
		Appearance lineApp;
		InterpolationPoint pt;
		ArrayList<AazonBaseMutableVect> controlPointsLocal;
		
		public ControlSelectNode( int _id,
				Point3f _ctrlPoint,
				AbzonMutableGeneralPathFactory _controllingLineChild,
				AazonBaseMutableVect _controlPointVect,
				Appearance _lineApp,
				InterpolationPoint _pt,
				ArrayList<AazonBaseMutableVect> _controlPointsLocal )
		{
			id = _id;
			ctrlPoint = _ctrlPoint;
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


		
	}
	
	
	
protected void calcTempoPointsInterp(Vector<AazonBaseMutableVect> out) {
		
	    int core = 0;
		int cnta;
		final ArrayList<InterpolationPoint> intPoints = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
		final int sz = intPoints.size();
		
		for (cnta = 0; cnta < sz; cnta++) {
			InterpolationPoint pt = intPoints.get(cnta);
			calcUValuesActual(pt);
			calcXYFromU(startSecondsU, levelU);
			double x1 = secondsX;
			double y1 = levelY;
			
			final AazonBaseMutableVect centerVect = new AazonBaseMutableVect( x1 , y1 );
			
			out.add( centerVect );
		}
	}
	
	protected void calcTempoPoints(GeneralPath out) {
		
		int core = 0;
		int cnta;
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez();
		int numCurves = bez.getNumCurves();

		
		startSecondsU = 0.0;
		endSecondsU = 1.0;
		
		for (cnta = 0; cnta < numCurves; cnta++) {
			CubicBezierCurve crv = bez.gCurve(cnta);
			
			double[] bezPts = crv.getBezPts();
			double strt = crv.getStartParam();
			double endp = crv.getEndParam();
			
			if( cnta == 0 )
			{
				InterpolationPoint pt = new InterpolationPoint( strt , bezPts[ 0 ] );
				calcUValuesActual(pt);
				calcXYFromU(startSecondsU, levelU);
				double x1 = secondsX;
				double y1 = levelY;
				out.moveTo((float)x1, (float)y1);
			}
			
			InterpolationPoint pt = new InterpolationPoint( 0.75 * strt + 0.25 * endp , bezPts[ 1 ] );
			calcUValuesActual(pt);
			calcXYFromU(startSecondsU, levelU);
			double x1 = secondsX;
			double y1 = levelY;
			
			pt = new InterpolationPoint( 0.25 * strt + 0.75 * endp , bezPts[ 2 ] );
			calcUValuesActual(pt);
			calcXYFromU(startSecondsU, levelU);
			double x2 = secondsX;
			double y2 = levelY;
			
			pt = new InterpolationPoint( endp , bezPts[ 3 ] );
			calcUValuesActual(pt);
			calcXYFromU(startSecondsU, levelU);
			double x3 = secondsX;
			double y3 = levelY;
			
			out.curveTo((float)x1, (float)y1, (float)x2, (float)y2, (float)x3, (float)y3);
		}
	
}


	protected double startSecondsU;

	protected double endSecondsU;

	protected double levelU;

	protected double secondsX;

	protected double levelY;

	protected void calcUFromSeconds(double startSeconds) {
		startSecondsU = (startSeconds - startSecondsNumber)
				/ (endSecondsNumber - startSecondsNumber);
	}

	protected void calcUFromLevel(double level) {
		levelU = (level - startLevel) / (endLevel - startLevel);
	}
	
	protected void calcUValuesActual(double noteU) {
		final int core = 0;
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez();
		double secondsNumber = (1-noteU)*startSecondsNumber + noteU*endSecondsNumber;
		double level = bez.eval(secondsNumber,core);

		levelU = (level - startLevel) / (endLevel - startLevel);
		startSecondsU = 0.0;
		endSecondsU = 1.0;
	}
	
	protected void calcUValuesActual(InterpolationPoint pt) {
		double level = pt.getValue();
		double startSeconds = pt.getParam();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startSecondsU = (startSeconds - startSecondsNumber)
				/ (endSecondsNumber - startSecondsNumber);
	}
	
	protected void updateInterpLevel(double levelU,
			InterpolationPoint pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setValue(level);
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	protected void updateTempoInterpPosn(double secondsU, int index) throws Throwable {
		final int core = 0;
		ArrayList<InterpolationPoint> points = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez()
				.getInterpolationPoints();
		InterpolationPoint pt = points.get(index);
		InterpolationPoint prev = points.get(index - 1);
		InterpolationPoint nxt = points.get(index + 1);
		double seconds = (1 - secondsU) * startSecondsNumber + secondsU * endSecondsNumber;
		if ((seconds > prev.getParam()) && (seconds < nxt.getParam())) {
			pt.setParam(seconds);
		}
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	protected void updateZoom(double stSecondsU, double stLevelU, double endSecondsU,
			double endLevelU) {
		double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		double stSeconds = (1 - stSecondsU) * startSecondsNumber + stSecondsU
				* endSecondsNumber;

		double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		double endSeconds = (1 - endSecondsU) * startSecondsNumber + endSecondsU
				* endSecondsNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		setStartSecondsNumber(stSeconds);
		setEndSecondsNumber(endSeconds);
		setStartLevel(edLevel);
		setEndLevel(stLevel);
	}
	
	protected void updateZoomX(double stSecondsU, double stLevelU, double endSecondsU,
			double endLevelU) {
		//double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		double stSeconds = (1 - stSecondsU) * startSecondsNumber + stSecondsU
				* endSecondsNumber;

		//double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		double endSeconds = (1 - endSecondsU) * startSecondsNumber + endSecondsU
				* endSecondsNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		setStartSecondsNumber(stSeconds);
		setEndSecondsNumber(endSeconds);
		//setStartLevel(edLevel);
		//setEndLevel(stLevel);
	}
	
	protected void updateZoomY(double stSecondsU, double stLevelU, double endSecondsU,
			double endLevelU) {
		double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		//double stSeconds = (1 - stSecondsU) * startSecondsNumber + stSecondsU
		//		* endSecondsNumber;

		double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		//double endSeconds = (1 - endSecondsU) * startSecondsNumber + endSecondsU
		//		* endSecondsNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		//setStartSecondsNumber(stSeconds);
		//setEndSecondsNumber(endSeconds);
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

	protected void calcXYFromU(double secondsU, double levelU) {
		double aX = -1.0;
		double aY = -calcYRatio();
		double bX = 1.0;
		double bY = calcYRatio();

		double cX = (1.0 - HORIZ_STRT_PCT) * aX + HORIZ_STRT_PCT * bX;
		double cY = (1.0 - VERT_STRT_PCT) * aY + VERT_STRT_PCT * bY;
		double dX = (1.0 - HORIZ_END_PCT) * aX + HORIZ_END_PCT * bX;
		double dY = (1.0 - VERT_END_PCT) * aY + VERT_END_PCT * bY;

		secondsX = (1.0 - secondsU) * cX + secondsU * dX;
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

		startSecondsU = (x - cX) / (dX - cX);
		levelU = (y - cY) / (dY - cY);
	}
	

	public void refreshDisplayListTempo() {
		ArrayList<AazonEnt> frontGreenLst = new ArrayList<AazonEnt>();
		backSprites.setGrp( new AazonImmutableGroup() );
		frontSprites.setGrp( new AazonImmutableGroup() );
		ArrayList<AazonEnt> frontTxtBlack = new ArrayList<AazonEnt>();
		
		
		
		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		ArrayList<Point3d> points = new ArrayList<Point3d>();

		final int core = 0;
		final double startBeatNumber = SongData.getBeatNumber(startSecondsNumber, core);
		final double endBeatNumber = SongData.getBeatNumber(endSecondsNumber, core);
		final double startMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber( startBeatNumber , core);
		final double endMeasure = SongData.measuresStore.getGuiMeasureCountForBeatNumber( endBeatNumber , core);

		if (((endMeasure - startMeasure) > 10)) {
			double delta = (endMeasure - startMeasure) / 10.0;
			double count;
			for (count = startMeasure; count < endMeasure; count = count
					+ delta) {
				int iCount = (int) (count);
				double qbeat = SongData.measuresStore.getBeatNumberForMeasureNumber( iCount );
				String str = "" + iCount;
				calcUFromSeconds( SongData.getElapsedTimeForBeatBeat( qbeat , core ) );
				calcXYFromU(startSecondsU, levelU);
				double x1 = secondsX;
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
					calcUFromSeconds( SongData.getElapsedTimeForBeatBeat( qbeat , core ) );
					calcXYFromU(startSecondsU, levelU);
					double x1 = secondsX;
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
					calcUFromSeconds( SongData.getElapsedTimeForBeatBeat( qbeat , core ) );
					calcXYFromU(startSecondsU, levelU);
					double x1 = secondsX;
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
			calcXYFromU(startSecondsU, levelU);
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
		
		
		if( ( editMode == EDIT_TEMPO_INTERP_LEVEL ) || ( editMode == EDIT_TEMPO_INTERP_POSN ) )
		{
			ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
			
			ArrayList<AazonEnt> controllingLineGroup = null;
			AbzonMutableGeneralPathFactory controllingLineChild = null;
				
				
				Appearance app = AczonColor.getLineGreen();
				
				controllingLineGroup = frontGreenLst;
				
				Appearance lineApp = app;
				
				Vector<AazonBaseMutableVect> interp = new Vector<AazonBaseMutableVect>();
				
				calcTempoPointsInterp(interp);
				
				AbzonSlopeGenerator generator = new AbzonMonotoneCubicSlopeGenerator( interp );
				
				AbzonCubicMonotoneCurveGenerator crv = new AbzonCubicMonotoneCurveGenerator( interp, generator);
				
				AbzonPathIteratorFactory[] fac = crv.generateCurves();

				AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
				controllingLineGroup.add( shape );
				
				generateControlPointsInterp(interp,
						controllingLineChild,
						lineApp, AczonColor.getFillCyan() , true, controlPoints);


				frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
		}
		else
		{
		ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
					GeneralPath pointsa = new GeneralPath();

					calcTempoPoints(pointsa);
					
					ArrayList<AazonEnt> controllingLineGroup = null;
					AbzonMutableGeneralPathFactory controllingLineChild = null;

					AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
						
						
						Appearance app = AczonColor.getLineGreen();
						

						AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						
						controllingLineGroup = frontGreenLst;
						
						
						controllingLineGroup.add( shape );
						controllingLineChild = fac;
						Appearance lineApp = app;
						
						generateControlPoints(controllingLineChild,
								lineApp, AczonColor.getFillCyan() , true, controlPoints);
		
		
		frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
		}
		
		
		frontGreen.setGrp( new AazonImmutableGroup( frontGreenLst ) );
		frontTextBlack.setGrp( new AazonImmutableGroup( frontTxtBlack ) );
		
		rectAVectA.setCoords( -1.0 , calcYRatio() );
		
		rectAVectB.setCoords( 1.0 ,  ( calcYRatio() ) * 0.8 + -( calcYRatio() ) * 0.2 );
		
		rectBVectA.setCoords( -1.0 , -calcYRatio() );
		
		rectBVectB.setCoords( -1.0 * 0.8 + 1.0 * 0.2 , calcYRatio() );
		
	}
	
	public void insertTempoInterpPointBefore() {
		editMode = EDIT_INSERT_TEMPO_INTERP_POINT_BEFORE;
		refreshDisplayList();
	}
	
	public void insertPasteBufferTempoInterpPointBefore() {
		editMode = EDIT_INSERT_PASTE_BUFFER_TEMPO_INTERP_POINT_BEFORE;
		refreshDisplayList();
	}
	
	public void insertTempoInterpPointAfter() {
		editMode = EDIT_INSERT_TEMPO_INTERP_POINT_AFTER;
		refreshDisplayList();
	}
	
	public void insertPasteBufferTempoInterpPointAfter() {
		editMode = EDIT_INSERT_PASTE_BUFFER_TEMPO_INTERP_POINT_AFTER;
		refreshDisplayList();
	}
	
	public void deleteTempoInterpPoint() {
		editMode = EDIT_DEL_TEMPO_INTERP_POINT;
		refreshDisplayList();
	}

	public void editTempoInterpPosn() {
		editMode = EDIT_TEMPO_INTERP_POSN;
		refreshDisplayList();
	}
	
	public void editTempoInterpLevel() {
		System.out.println("Set Edit Tempo Interp Level");
		editMode = EDIT_TEMPO_INTERP_LEVEL;
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
	
	public void selectTempo() {
		editMode = EDIT_MODE_SELECT_TEMPO;
		refreshDisplayList();
	}

	public void unZoom() {
		final int core = 0;
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-20.0);
		setEndLevel(200.0);
		setStartSecondsNumber( SongData.getElapsedTimeForBeatBeat( -10 , core ) );
		setEndSecondsNumber( SongData.getElapsedTimeForBeatBeat( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10 , core ) );
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void unZoomLimits() {
		final int core = 0;
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-0.1);
		setEndLevel(1.1);
		setStartSecondsNumber( SongData.getElapsedTimeForBeatBeat( SongData.START_BEAT , core ) );
		setEndSecondsNumber( SongData.getElapsedTimeForBeatBeat( SongData.END_BEAT , core ) );
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
		double midSeconds = ( getStartSecondsNumber() + getEndSecondsNumber() ) / 2.0;
		double deltaSeconds = Math.abs( getEndSecondsNumber() - getStartSecondsNumber() );
		setStartSecondsNumber( midSeconds - deltaSeconds );
		setEndSecondsNumber( midSeconds + deltaSeconds );
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void initializeTempoToPasteBuffer()
	{
		final int core = 0;
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = new PiecewiseCubicMonotoneBezierFlatMultiCore();
		bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , SongData.PASTE_BUFFER_BEATS_PER_MINUTE ) );
		bez.getInterpolationPoints().add( new InterpolationPoint( SongData.getElapsedTimeForBeatBeat( SongData.measuresStore.getNumberOfBeatsForMeasure( SongData.NUM_MEASURES ) , core ) , SongData.PASTE_BUFFER_BEATS_PER_MINUTE ) );
		bez.updateAll();
		SongData.TEMPO_BEATS_PER_MINUTE_CRV = new BezierCubicNonClampedCoefficientFlatMultiCore( bez );
		SongData.handleTempoUpdate( core );
		SongListeners.updateViewPanes();
	}

	public void sequesterToWindow() {
		final int core = 0;
		SongData.START_BEAT = SongData.getBeatNumber(startSecondsNumber,core);
		SongData.END_BEAT = SongData.getBeatNumber(endSecondsNumber,core);
	}

	/**
	 * @return Returns the endSecondsNumber.
	 */
	public double getEndSecondsNumber() {
		return endSecondsNumber;
	}

	/**
	 * @param endSecondsNumber
	 *            The endSecondsNumber to set.
	 */
	public void setEndSecondsNumber(double endSecondsNumber) {
		this.endSecondsNumber = endSecondsNumber;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setEndSecondsNumber(mil, endSecondsNumber);
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
		mil = model.pdxm_setEndTempo(mil, endLevel);
		undoMgr.handleCommitTempChange( mil );
	}

	/**
	 * @return Returns the startSecondsNumber.
	 */
	public double getStartSecondsNumber() {
		return startSecondsNumber;
	}

	/**
	 * @param startSecondsNumber
	 *            The startSecondsNumber to set.
	 */
	public void setStartSecondsNumber(double startSecondsNumber) {
		this.startSecondsNumber = startSecondsNumber;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setStartSecondsNumber(mil, startSecondsNumber);
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
		mil = model.pdxm_setStartTempo(mil, startLevel);
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


		case EDIT_TEMPO_INTERP_LEVEL: {
			ControlSelectNode descn = getDescForLocn(e);
			if( descn != null ) {

				lastDragDesc = descn;
				mouseDragEnabled = true;
			}
		}
			break;
			
		case EDIT_TEMPO_INTERP_POSN: {
			int core = 0;
			ControlSelectNode descn = getDescForLocn(e);
			if( descn != null ) {

				int id = descn.getId();
				int sz = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints().size();
				if ((id > 0) && (id < (sz - 1))) {
					lastDragDesc = descn;
					mouseDragEnabled = true;
				} else {
					Toolkit.getDefaultToolkit().beep();
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
					box1,
					null,
					null,
					null );
		}
			break;
			
		case EDIT_INSERT_TEMPO_INTERP_POINT_BEFORE: {
			insertTempoLevelBefore(e);
		}
			break;
			
		case EDIT_INSERT_PASTE_BUFFER_TEMPO_INTERP_POINT_BEFORE: {
			insertPasteBufferTempoLevelBefore(e);
		}
			break;
			
		case EDIT_INSERT_TEMPO_INTERP_POINT_AFTER: {
			insertTempoLevelAfter(e);
		}
			break;
			
		case EDIT_INSERT_PASTE_BUFFER_TEMPO_INTERP_POINT_AFTER: {
			insertPasteBufferTempoLevelAfter(e);
		}
			break;

		/* case EDIT_DEL_NOTE_INTERP_POINT: {
			delLevel(e);
		}
			break; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */


		}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
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
	
	protected void selectTempoLevel(MouseEvent e) {
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		// NoteDesc desc = descn.getDesc();
		lastDragDesc = descn;
		System.out.println( "Volume Selected..." );
	}
	
	protected void insertPasteBufferTempoLevelBefore(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez();
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
			apt.setValue( SongData.PASTE_BUFFER_BEATS_PER_MINUTE );
			apt.setParam((prev.getParam() + nxt.getParam()) / 2.0);
			for (count = id; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			bez.updateAll();
			SongListeners.updateViewPanes();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	protected void insertPasteBufferTempoLevelAfter(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez();
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
			apt.setValue( SongData.PASTE_BUFFER_BEATS_PER_MINUTE );
			apt.setParam((prev.getParam() + nxt.getParam()) / 2.0);
			for (count = id + 1; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			bez.updateAll();
			SongListeners.updateViewPanes();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	protected void insertTempoLevelBefore(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez();
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
			SongListeners.updateViewPanes();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	protected void insertTempoLevelAfter(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez();
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
			SongListeners.updateViewPanes();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	
	public void calculateTempo() {
		try
		{
		TapPad tapPad = OutputChoiceInterface.getTapPad();
		if (tapPad != null) {
			ArrayList<TimeInterval> vect = tapPad.getTimeIntervalArrayList();
			if (vect.size() > 1) {
				int sz = vect.size();
				final TimeInterval strt = vect.get(0);
				final TimeInterval end = vect.get(sz - 1);
				final double delta = end.getStartTimeSeconds()
						- strt.getStartTimeSeconds();
				double meanDeltaSeconds = delta / (sz - 1);
				double meanTempoBeatsPerSecond = 1.0 / meanDeltaSeconds;
				double meanTempoBeatsPerMinute = 60.0 * meanTempoBeatsPerSecond;
				double variance = 0.0;
				int count;
				for( count = 0 ; count < ( sz - 1 ) ; count++ )
				{
					final TimeInterval st = vect.get(count);
					final TimeInterval ed = vect.get(count + 1);
					final double udelta = ed.getStartTimeSeconds()
						- st.getStartTimeSeconds();
					final double udel = udelta - meanDeltaSeconds;
					variance += udel * udel;
				}
				variance = variance / ( sz - 1 );
				final double sigmaSeconds = Math.sqrt( variance );
				System.out.println( "Avg. tempo beats per minute " + meanTempoBeatsPerMinute );
				System.out.println( "Sigma seconds " + sigmaSeconds );
				String msg = "Average tempo is " + meanTempoBeatsPerMinute
						+ "  Set paste buffer to this tempo?";
				int result = JOptionPane.showConfirmDialog(this, msg);
				if (result == JOptionPane.OK_OPTION) {
					System.out.println("User Said OK");
					SongData.PASTE_BUFFER_BEATS_PER_MINUTE = meanTempoBeatsPerMinute;
				} else {
					System.out.println("User Said Cancel");
				}
			}
			vect.clear();
		}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	public void interpTempo() {
		try
		{
		final int core = 0;
		TapPad tapPad = OutputChoiceInterface.getTapPad();
		if (tapPad != null) {
			ArrayList<TimeInterval> vect = tapPad.getTimeIntervalArrayList();
			if (vect.size() > 1) {
				final int sz = vect.size();
				TimeInterval strt = vect.get(0);
				final double startTime = strt.getStartTimeSeconds();
				final double startBeat = SongData.getBeatNumber(startTime, core);
				System.out.println( startBeat );
				try
				{
					VerdantiumPropertiesEditor editor = 
						new TempoInterpTapEditor( this , startBeat );
					ProgramDirector.showPropertyEditor(editor, null,
						"InterpTap Properties");
				}
				catch( Throwable ex )
				{
					ex.printStackTrace( System.out );
				}
			}
		}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	public void interpTempo( final int stBeat ) {
		try
		{
		final int core = 0;
		TapPad tapPad = OutputChoiceInterface.getTapPad();
		if (tapPad != null) {
			ArrayList<TimeInterval> vect = tapPad.getTimeIntervalArrayList();
			if (vect.size() > 1) {
				undoList = new ArrayList<InterpolationPoint>( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints() );
				final double onep = 1.0 / 3.0;
				final double twop = 2.0 / 3.0;
				
				final int numBeats = vect.size();
				ArrayList<InterpolationPoint> beatList = new ArrayList<InterpolationPoint>();
				final TreeMap<Double,InterpolationPoint> tm = new TreeMap<Double,InterpolationPoint>();
				final TreeMap<Double,InterpolationPoint> tm2 = new TreeMap<Double,InterpolationPoint>();
				
				int count;
				for( count = 0 ; count < numBeats ; count++ )
				{
					TimeInterval tmm = vect.get( count );
					InterpolationPoint intp = new InterpolationPoint( tmm.getStartTimeSeconds() , stBeat + count );
					beatList.add( intp );
					tm.put( intp.getParam() , intp );
				}
				
				final ArrayList<InterpolationPoint> origInteg = SongData.BEAT_NUMBER_FUNCTION_SECONDS.getBez().getInterpolationPoints();
				
				
				for( count = 0 ; count < origInteg.size() ; count++ )
				{
					InterpolationPoint i = origInteg.get( count );
					tm.put( i.getParam() , i );
				}
				
				
				final ArrayList<InterpolationPoint> origTempo = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
				
				for( count = 0 ; count < origTempo.size() ; count++ )
				{
					InterpolationPoint i = origTempo.get( count );
					tm2.put( i.getParam() , i );
				}
				
				
				final ArrayList<InterpolationPoint> newApproxInteg = new ArrayList<InterpolationPoint>();
				
				for( Entry<Double,InterpolationPoint> e : tm.entrySet() )
				{
					newApproxInteg.add( e.getValue() );
				}
				
				final BezierCubicNonClampedCoefficientSloping newApproxIntegCrv = new BezierCubicNonClampedCoefficientSloping( new PiecewiseCubicMonotoneBezierSloping() );
				
				newApproxIntegCrv.getBez().setInterpolationPoints( newApproxInteg );
				
				newApproxIntegCrv.setSlopes( undoList.get( 0 ).getValue() / 60.0 , undoList.get( undoList.size() - 1 ).getValue() / 60.0 );
				
				newApproxIntegCrv.getBez().updateAll();
				
				  
				
				final ArrayList<InterpolationPoint> testTempo = new ArrayList<InterpolationPoint>();
				
				for( count = 0 ; count < ( numBeats - 1 ) ; count++ )
				{
					
					InterpolationPoint i0 = beatList.get( count );
					InterpolationPoint i1 = beatList.get( count + 1 );
					InterpolationPoint ia = new InterpolationPoint( i0.getParam() , ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
					InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
					InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
					testTempo.add( ia );
					tm2.put( ia.getParam() , ia );
					tm2.put( ib.getParam() , ib );
					tm2.put( ic.getParam() , ic );
				}
				
				
				{
					final int ind0 = newApproxInteg.indexOf( beatList.get( 0 ) );
					InterpolationPoint i0 = newApproxInteg.get( ind0 - 1 );
					InterpolationPoint i1 = beatList.get(0);
					InterpolationPoint ia = new InterpolationPoint( i0.getParam() , /* ( ( ind0 - 1 ) == 0 ) ? ( (InterpolationPoint)( undoList.get( 0 ) ) ).getValue() 
							: */ ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
					if( ( ind0 - 1 ) == 0 ) System.out.println( "Hey !!!!!!!!!!!!!!!!1 " + ( ia.getValue() ) );
					InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
					InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
					tm2.put( ia.getParam() , ia );
					tm2.put( ib.getParam() , ib );
					tm2.put( ic.getParam() , ic );
				}
				
				{
					final int indn = newApproxInteg.indexOf( beatList.get( numBeats - 1 ) );
					InterpolationPoint i0 = beatList.get( numBeats - 1 );
					InterpolationPoint i1 = newApproxInteg.get( indn + 1 );
					InterpolationPoint ia = new InterpolationPoint( i0.getParam() , ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
					InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
					InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
					testTempo.add( ia );
					tm2.put( ia.getParam() , ia );
					tm2.put( ib.getParam() , ib );
					tm2.put( ic.getParam() , ic );
				}
				
				
				
				
				origTempo.clear();
				
				for( Entry<Double,InterpolationPoint> e : tm2.entrySet() )
				{
					origTempo.add( e.getValue() );
				}
				
				
				SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().setInterpolationPoints( origTempo );
				
				SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
				
				
				
				boolean done2 = false;
				int kcnt = 0;
				while( !done2 )
				{
					kcnt++;
					done2 = true;
					for( count = 0 ; count < numBeats ; count++ )
					{
						boolean done = false;
						double minFreq = 1E-60;
						double maxFreq = 1E+60;
						final InterpolationPoint c2 = testTempo.get( count );
						final double initialValue = c2.getValue();
						final double interpValueSeconds = c2.getParam();
						final double chgBeatNumber = ( beatList.get( count ) ).getValue();
						
						
						System.out.println( "***********************starting " + count );
						
						
						{
							SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
							PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
							quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
							BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
							double beatNumber = quartic.eval(interpValueSeconds, core);
							if( beatNumber > chgBeatNumber )
							{
								maxFreq = c2.getValue();
								minFreq = Math.min( minFreq ,  maxFreq / 100.0 );
								c2.setValue( maxFreq / 10.0 );
							}
							else
							{
								if( beatNumber < chgBeatNumber )
								{
									minFreq = c2.getValue();
									maxFreq = Math.max( maxFreq , minFreq * 100.0 );
									c2.setValue( minFreq * 10.0 );
								}
								else
								{
									minFreq = c2.getValue();
									maxFreq = c2.getValue();
								}
							}
						}
						
						
						if( c2.getValue() != initialValue )
						{
							SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
							PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
							quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
							BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
							double beatNumber = quartic.eval(interpValueSeconds, core);
							if( beatNumber > chgBeatNumber )
							{
								maxFreq = c2.getValue();
							}
							else
							{
								if( beatNumber < chgBeatNumber )
								{
									minFreq = c2.getValue();
								}
								else
								{
									minFreq = c2.getValue();
									maxFreq = c2.getValue();
								}
							}
						}
						
						
						c2.setValue( ( minFreq + maxFreq ) / 2.0 );
						
						int rcnt = 0;
					
						while( !done )
						{
							rcnt++;
							SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
							PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
							quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
							BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
							double beatNumber = quartic.eval(interpValueSeconds, core);
							final boolean chkDone =  ( ( maxFreq - minFreq ) < 1E-10 ) && ( Math.abs( chgBeatNumber - beatNumber ) < 1E-5 );
							if( chkDone || ( rcnt > 2000 ) ) 
							{
								done = true;
								final boolean chkBool = ( Math.abs( c2.getValue() - initialValue ) > 1E-5 ) || ( Math.abs( chgBeatNumber - beatNumber ) > 1E-5 );
								if( ( kcnt < 35 ) && chkBool )
								{
									done2 = false;
									System.out.println( kcnt + " // " + count + " // " + minFreq + "  " + maxFreq );
									System.out.println( c2.getValue() + "  " + initialValue );
									System.out.println( chgBeatNumber + "  " + beatNumber );
								}
								
							}
							else
							{
								if( beatNumber > chgBeatNumber )
								{
									maxFreq = ( minFreq + maxFreq ) / 2.0;
								}
								else
								{
									minFreq = ( minFreq + maxFreq ) / 2.0;
								}
								c2.setValue( ( minFreq + maxFreq ) / 2.0 );
								// System.out.println( minFreq + "  " + maxFreq );
								// System.out.println( chgBeatNumber + "  " + beatNumber );
							}
						}
					}
				}
				
				
				
				SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
				SongListeners.updateViewPanes();
			}
			vect.clear();
		}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	public void interpStat()
	{
		try
		{
			VerdantiumPropertiesEditor editor = 
				new TempoInterpStatEditor( this );
			ProgramDirector.showPropertyEditor(editor, null,
				"InterpStat Properties");
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	
	public void swingBeatsInMeasures() {
		try
		{
		final int core = 0;
			VerdantiumPropertiesEditor editor = 
				new SwingBeatsInMeasuresEditor( this );
			ProgramDirector.showPropertyEditor(editor, null,
				"SwingBeat Properties");
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	
	public void interpStat( final int measureNumber , final int ibeatNumber , final int numBeats , 
			final double tempoBeatsPerMinute , final double sigmaSeconds , final int randSeed ) {
		try
		{
		final int core = 0;
		
		final double onep = 1.0 / 3.0;
		final double twop = 2.0 / 3.0;
		
		undoList = new ArrayList<InterpolationPoint>( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints() );
		final int stBeat = SongData.measuresStore.getBeatNumberForMeasureNumber(measureNumber)
			+ ( ibeatNumber );
		final double startTime = SongData.getElapsedTimeForBeatBeat(stBeat, core);
		final Random rand = new Random( randSeed );
		final double tempoBeatsPerSecond = tempoBeatsPerMinute / 60.0;
		
		ArrayList<InterpolationPoint> beatList = new ArrayList<InterpolationPoint>();
		final TreeMap<Double,InterpolationPoint> tm = new TreeMap<Double,InterpolationPoint>();
		final TreeMap<Double,InterpolationPoint> tm2 = new TreeMap<Double,InterpolationPoint>();
		
		
		int count;
		for( count = 0 ; count < numBeats ; count++ )
		{
			final double interpTime = startTime + ( count / tempoBeatsPerSecond ) +
				( sigmaSeconds * ( rand.nextGaussian() ) );
			InterpolationPoint intp = new InterpolationPoint( interpTime , stBeat + count );
			beatList.add( intp );
			tm.put( intp.getParam() , intp );
		}
		
		final ArrayList<InterpolationPoint> origInteg = SongData.BEAT_NUMBER_FUNCTION_SECONDS.getBez().getInterpolationPoints();
		
		
		for( count = 0 ; count < origInteg.size() ; count++ )
		{
			InterpolationPoint i = origInteg.get( count );
			tm.put( i.getParam() , i );
		}
		
		
		final ArrayList<InterpolationPoint> origTempo = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
		
		for( count = 0 ; count < origTempo.size() ; count++ )
		{
			InterpolationPoint i = origTempo.get( count );
			tm2.put( i.getParam() , i );
		}
		
		
		final ArrayList<InterpolationPoint> newApproxInteg = new ArrayList<InterpolationPoint>();
		
		for( Entry<Double,InterpolationPoint> e : tm.entrySet() )
		{
			newApproxInteg.add( e.getValue() );
		}
		
		final BezierCubicNonClampedCoefficientSloping newApproxIntegCrv = new BezierCubicNonClampedCoefficientSloping( new PiecewiseCubicMonotoneBezierSloping() );
		
		newApproxIntegCrv.getBez().setInterpolationPoints( newApproxInteg );
		
		newApproxIntegCrv.setSlopes( undoList.get( 0 ).getValue() / 60.0 , undoList.get( undoList.size() - 1 ).getValue() / 60.0 );
		
		newApproxIntegCrv.getBez().updateAll();
		
		  
		
		final ArrayList<InterpolationPoint> testTempo = new ArrayList<InterpolationPoint>();
		
		for( count = 0 ; count < ( numBeats - 1 ) ; count++ )
		{
			
			InterpolationPoint i0 = beatList.get( count );
			InterpolationPoint i1 = beatList.get( count + 1 );
			InterpolationPoint ia = new InterpolationPoint( i0.getParam() , ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
			InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
			InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
			testTempo.add( ia );
			tm2.put( ia.getParam() , ia );
			tm2.put( ib.getParam() , ib );
			tm2.put( ic.getParam() , ic );
		}
		
		
		{
			final int ind0 = newApproxInteg.indexOf( beatList.get( 0 ) );
			InterpolationPoint i0 = newApproxInteg.get( ind0 - 1 );
			InterpolationPoint i1 = beatList.get(0);
			InterpolationPoint ia = new InterpolationPoint( i0.getParam() , /* ( ( ind0 - 1 ) == 0 ) ? ( (InterpolationPoint)( undoList.get( 0 ) ) ).getValue() 
					: */ ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
			if( ( ind0 - 1 ) == 0 ) System.out.println( "Hey !!!!!!!!!!!!!!!!1 " + ( ia.getValue() ) );
			InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
			InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
			tm2.put( ia.getParam() , ia );
			tm2.put( ib.getParam() , ib );
			tm2.put( ic.getParam() , ic );
		}
		
		{
			final int indn = newApproxInteg.indexOf( beatList.get( numBeats - 1 ) );
			InterpolationPoint i0 = beatList.get( numBeats - 1 );
			InterpolationPoint i1 = newApproxInteg.get( indn + 1 );
			InterpolationPoint ia = new InterpolationPoint( i0.getParam() , ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
			InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
			InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
			testTempo.add( ia );
			tm2.put( ia.getParam() , ia );
			tm2.put( ib.getParam() , ib );
			tm2.put( ic.getParam() , ic );
		}
		
		
		
		
		origTempo.clear();
		
		for( Entry<Double,InterpolationPoint> e : tm2.entrySet() )
		{
			origTempo.add( e.getValue() );
		}
		
		
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().setInterpolationPoints( origTempo );
		
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
		
		
		
		
		boolean done2 = false;
		int kcnt = 0;
		while( !done2 )
		{
			kcnt++;
			done2 = true;
			for( count = 0 ; count < numBeats ; count++ )
			{
				boolean done = false;
				double minFreq = 1E-60;
				double maxFreq = 1E+60;
				final InterpolationPoint c2 = testTempo.get( count );
				final double initialValue = c2.getValue();
				final double interpValueSeconds = c2.getParam();
				final double chgBeatNumber = ( beatList.get( count ) ).getValue();
				
				
				System.out.println( "***********************starting " + count );
				
				
				{
					SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
					PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
					quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
					BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
					double beatNumber = quartic.eval(interpValueSeconds, core);
					if( beatNumber > chgBeatNumber )
					{
						maxFreq = c2.getValue();
						minFreq = Math.min( minFreq ,  maxFreq / 100.0 );
						c2.setValue( maxFreq / 10.0 );
					}
					else
					{
						if( beatNumber < chgBeatNumber )
						{
							minFreq = c2.getValue();
							maxFreq = Math.max( maxFreq , minFreq * 100.0 );
							c2.setValue( minFreq * 10.0 );
						}
						else
						{
							minFreq = c2.getValue();
							maxFreq = c2.getValue();
						}
					}
				}
				
				
				if( c2.getValue() != initialValue )
				{
					SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
					PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
					quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
					BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
					double beatNumber = quartic.eval(interpValueSeconds, core);
					if( beatNumber > chgBeatNumber )
					{
						maxFreq = c2.getValue();
					}
					else
					{
						if( beatNumber < chgBeatNumber )
						{
							minFreq = c2.getValue();
						}
						else
						{
							minFreq = c2.getValue();
							maxFreq = c2.getValue();
						}
					}
				}
				
				
				c2.setValue( ( minFreq + maxFreq ) / 2.0 );
				
				int rcnt = 0;
			
				while( !done )
				{
					rcnt++;
					SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
					PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
					quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
					BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
					double beatNumber = quartic.eval(interpValueSeconds, core);
					final boolean chkDone =  ( ( maxFreq - minFreq ) < 1E-10 ) && ( Math.abs( chgBeatNumber - beatNumber ) < 1E-5 );
					if( chkDone || ( rcnt > 2000 ) ) 
					{
						done = true;
						final boolean chkBool = ( Math.abs( c2.getValue() - initialValue ) > 1E-5 ) || ( Math.abs( chgBeatNumber - beatNumber ) > 1E-5 );
						if( ( kcnt < 35 ) && chkBool )
						{
							done2 = false;
							System.out.println( kcnt + " // " + count + " // " + minFreq + "  " + maxFreq );
							System.out.println( c2.getValue() + "  " + initialValue );
							System.out.println( chgBeatNumber + "  " + beatNumber );
						}
						
					}
					else
					{
						if( beatNumber > chgBeatNumber )
						{
							maxFreq = ( minFreq + maxFreq ) / 2.0;
						}
						else
						{
							minFreq = ( minFreq + maxFreq ) / 2.0;
						}
						c2.setValue( ( minFreq + maxFreq ) / 2.0 );
						// System.out.println( minFreq + "  " + maxFreq );
						// System.out.println( chgBeatNumber + "  " + beatNumber );
					}
				}
			}
		}
		
		
		
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
		SongListeners.updateViewPanes();
		
		
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	
	
	public void swingBeatsInMeasures( int startMeasureNumber , int endMeasureNumber , int beatNumberInMeasure0 , double swingInBeats0 , int beatNumberInMeasure1 , double swingInBeats1 , int beatNumberInMeasure2 , double swingInBeats2 ) {
		try
		{
		final int core = 0;
		
		final double onep = 1.0 / 3.0;
		final double twop = 2.0 / 3.0;
		
		undoList = new ArrayList<InterpolationPoint>( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints() );
		
		final int stBeat = SongData.measuresStore.getBeatNumberForMeasureNumber( startMeasureNumber - 1 );
		
		final int endBeat = SongData.measuresStore.getBeatNumberForMeasureNumber( endMeasureNumber + 1 );
		
		final int numBeats = endBeat - stBeat + 1;
		
		
		final HashMap<Integer,Double> beatMap = new HashMap<Integer,Double>();
		
		for( int sm = startMeasureNumber ; sm <= endMeasureNumber ; sm++ )
		{
			final int smb = SongData.measuresStore.getBeatNumberForMeasureNumber( sm );
			
			if( beatNumberInMeasure0 >= 0 )
			{
				beatMap.put( smb + beatNumberInMeasure0 , swingInBeats0 );
			}
			
			if( beatNumberInMeasure1 >= 0 )
			{
				beatMap.put( smb + beatNumberInMeasure1 , swingInBeats1 );
			}
			
			if( beatNumberInMeasure2 >= 0 )
			{
				beatMap.put( smb + beatNumberInMeasure2 , swingInBeats2 );
			}
			
		}
		
		
		final double startTime = SongData.getElapsedTimeForBeatBeat(stBeat, core);
		
		ArrayList<InterpolationPoint> beatList = new ArrayList<InterpolationPoint>();
		final TreeMap<Double,InterpolationPoint> tm = new TreeMap<Double,InterpolationPoint>();
		final TreeMap<Double,InterpolationPoint> tm2 = new TreeMap<Double,InterpolationPoint>();
		
		
		int count;
		for( count = 0 ; count < numBeats ; count++ )
		{
			Double swingBeat = beatMap.get( stBeat + count );
			
			if( swingBeat == null )
			{
				swingBeat = 0.0;
			}
			
			
			final double interpTime = SongData.getElapsedTimeForBeatBeat( stBeat + count + swingBeat , core);
					
			InterpolationPoint intp = new InterpolationPoint( interpTime , stBeat + count );
			beatList.add( intp );
			tm.put( intp.getParam() , intp );	
		}
		
		
		final ArrayList<InterpolationPoint> origInteg = SongData.BEAT_NUMBER_FUNCTION_SECONDS.getBez().getInterpolationPoints();
		
		
		for( count = 0 ; count < origInteg.size() ; count++ )
		{
			InterpolationPoint i = origInteg.get( count );
			tm.put( i.getParam() , i );
		}
		
		
		final ArrayList<InterpolationPoint> origTempo = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
		
		for( count = 0 ; count < origTempo.size() ; count++ )
		{
			InterpolationPoint i = origTempo.get( count );
			tm2.put( i.getParam() , i );
		}
		
		
		final ArrayList<InterpolationPoint> newApproxInteg = new ArrayList<InterpolationPoint>();
		
		for( Entry<Double,InterpolationPoint> e : tm.entrySet() )
		{
			newApproxInteg.add( e.getValue() );
		}
		
		final BezierCubicNonClampedCoefficientSloping newApproxIntegCrv = new BezierCubicNonClampedCoefficientSloping( new PiecewiseCubicMonotoneBezierSloping() );
		
		newApproxIntegCrv.getBez().setInterpolationPoints( newApproxInteg );
		
		newApproxIntegCrv.setSlopes( undoList.get( 0 ).getValue() / 60.0 , undoList.get( undoList.size() - 1 ).getValue() / 60.0 );
		
		newApproxIntegCrv.getBez().updateAll();
		
		  
		
		final ArrayList<InterpolationPoint> testTempo = new ArrayList<InterpolationPoint>();
		
		for( count = 0 ; count < ( numBeats - 1 ) ; count++ )
		{
			
			InterpolationPoint i0 = beatList.get( count );
			InterpolationPoint i1 = beatList.get( count + 1 );
			InterpolationPoint ia = new InterpolationPoint( i0.getParam() , ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
			InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
			InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
			testTempo.add( ia );
			tm2.put( ia.getParam() , ia );
			tm2.put( ib.getParam() , ib );
			tm2.put( ic.getParam() , ic );
		}
		
		
		{
			final int ind0 = newApproxInteg.indexOf( beatList.get( 0 ) );
			InterpolationPoint i0 = newApproxInteg.get( ind0 - 1 );
			InterpolationPoint i1 = beatList.get(0);
			InterpolationPoint ia = new InterpolationPoint( i0.getParam() , /* ( ( ind0 - 1 ) == 0 ) ? ( (InterpolationPoint)( undoList.get( 0 ) ) ).getValue() 
					: */ ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
			if( ( ind0 - 1 ) == 0 ) System.out.println( "Hey !!!!!!!!!!!!!!!!1 " + ( ia.getValue() ) );
			InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
			InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
			tm2.put( ia.getParam() , ia );
			tm2.put( ib.getParam() , ib );
			tm2.put( ic.getParam() , ic );
		}
		
		{
			final int indn = newApproxInteg.indexOf( beatList.get( numBeats - 1 ) );
			InterpolationPoint i0 = beatList.get( numBeats - 1 );
			InterpolationPoint i1 = newApproxInteg.get( indn + 1 );
			InterpolationPoint ia = new InterpolationPoint( i0.getParam() , ( newApproxIntegCrv.evalDerivative( i0.getParam() ) ) * 60.0 ); 
			InterpolationPoint ib = new InterpolationPoint( twop * i0.getParam() + onep * i1.getParam() , ( newApproxIntegCrv.evalDerivative( twop * i0.getParam() + onep * i1.getParam() ) ) * 60.0 );
			InterpolationPoint ic = new InterpolationPoint( onep * i0.getParam() + twop * i1.getParam() , ( newApproxIntegCrv.evalDerivative( onep * i0.getParam() + twop * i1.getParam() ) ) * 60.0 );
			testTempo.add( ia );
			tm2.put( ia.getParam() , ia );
			tm2.put( ib.getParam() , ib );
			tm2.put( ic.getParam() , ic );
		}
		
		
		
		
		origTempo.clear();
		
		for( Entry<Double,InterpolationPoint> e : tm2.entrySet() )
		{
			origTempo.add( e.getValue() );
		}
		
		
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().setInterpolationPoints( origTempo );
		
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
		
		
		
		
		boolean done2 = false;
		int kcnt = 0;
		while( !done2 )
		{
			kcnt++;
			done2 = true;
			for( count = 0 ; count < numBeats ; count++ )
			{
				boolean done = false;
				double minFreq = 1E-60;
				double maxFreq = 1E+60;
				final InterpolationPoint c2 = testTempo.get( count );
				final double initialValue = c2.getValue();
				final double interpValueSeconds = c2.getParam();
				final double chgBeatNumber = ( beatList.get( count ) ).getValue();
				
				
				System.out.println( "***********************starting " + count );
				
				
				{
					SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
					PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
					quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
					BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
					double beatNumber = quartic.eval(interpValueSeconds, core);
					if( beatNumber > chgBeatNumber )
					{
						maxFreq = c2.getValue();
						minFreq = Math.min( minFreq ,  maxFreq / 100.0 );
						c2.setValue( maxFreq / 10.0 );
					}
					else
					{
						if( beatNumber < chgBeatNumber )
						{
							minFreq = c2.getValue();
							maxFreq = Math.max( maxFreq , minFreq * 100.0 );
							c2.setValue( minFreq * 10.0 );
						}
						else
						{
							minFreq = c2.getValue();
							maxFreq = c2.getValue();
						}
					}
				}
				
				
				if( c2.getValue() != initialValue )
				{
					SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
					PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
					quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
					BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
					double beatNumber = quartic.eval(interpValueSeconds, core);
					if( beatNumber > chgBeatNumber )
					{
						maxFreq = c2.getValue();
					}
					else
					{
						if( beatNumber < chgBeatNumber )
						{
							minFreq = c2.getValue();
						}
						else
						{
							minFreq = c2.getValue();
							maxFreq = c2.getValue();
						}
					}
				}
				
				
				c2.setValue( ( minFreq + maxFreq ) / 2.0 );
				
				int rcnt = 0;
			
				while( !done )
				{
					rcnt++;
					SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
					PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
					quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
					BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
					double beatNumber = quartic.eval(interpValueSeconds, core);
					final boolean chkDone =  ( ( maxFreq - minFreq ) < 1E-10 ) && ( Math.abs( chgBeatNumber - beatNumber ) < 1E-5 );
					if( chkDone || ( rcnt > 2000 ) ) 
					{
						done = true;
						final boolean chkBool = ( Math.abs( c2.getValue() - initialValue ) > 1E-5 ) || ( Math.abs( chgBeatNumber - beatNumber ) > 1E-5 );
						if( ( kcnt < 35 ) && chkBool )
						{
							done2 = false;
							System.out.println( kcnt + " // " + count + " // " + minFreq + "  " + maxFreq );
							System.out.println( c2.getValue() + "  " + initialValue );
							System.out.println( chgBeatNumber + "  " + beatNumber );
						}
						
					}
					else
					{
						if( beatNumber > chgBeatNumber )
						{
							maxFreq = ( minFreq + maxFreq ) / 2.0;
						}
						else
						{
							minFreq = ( minFreq + maxFreq ) / 2.0;
						}
						c2.setValue( ( minFreq + maxFreq ) / 2.0 );
						// System.out.println( minFreq + "  " + maxFreq );
						// System.out.println( chgBeatNumber + "  " + beatNumber );
					}
				}
			}
		}
		
		
		
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
		SongListeners.updateViewPanes();
		
		
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	
	
	public void insertFermata()
	{
		try
		{
			VerdantiumPropertiesEditor editor = 
				new TempoInsertFermataEditor( this );
			ProgramDirector.showPropertyEditor(editor, null,
				"Insert Fermata Properties");
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	
	public void insertAccelerando()
	{
		try
		{
			VerdantiumPropertiesEditor editor = 
				new TempoInsertAccelerandoEditor( this );
			ProgramDirector.showPropertyEditor(editor, null,
				"Insert Accelerando/Ritardando Properties");
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	
	public void insertRitardando()
	{
		insertAccelerando();
	}
	
	
	
	public void insertAccelerando( final int measureNumberStrt , final double iBeatNumStrt , 
			final int measureNumberEnd , final double iBeatNumEnd ,
			final double finalBeatsPerMinute )
	{
		try
		{
			int core = 0;
			int count;
			
			undoList = new ArrayList<InterpolationPoint>( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints() );
			
			final double chgBeatNumberStrt = SongData.measuresStore.getBeatNumberForMeasureNumber( measureNumberStrt ) + ( iBeatNumStrt );
			final double chgBeatNumberEnd = SongData.measuresStore.getBeatNumberForMeasureNumber( measureNumberEnd ) + ( iBeatNumEnd );
			
			double minOffset = 1E-2;
			double maxOffset = 1000.0;
			
			final double origChgSecondsStrt = SongData.getElapsedTimeForBeatBeat(chgBeatNumberStrt, core);
			final double origChgSecondsEnd = SongData.getElapsedTimeForBeatBeat(chgBeatNumberEnd, core);
			
			final double c0Sec = origChgSecondsStrt;
			final double c1Sec = origChgSecondsStrt + 0.001;
			final double c2Sec = origChgSecondsStrt + 0.002;
			double c3Sec = (minOffset+maxOffset)/2.0 - 0.002;
			double c4Sec = (minOffset+maxOffset)/2.0 - 0.001;
			double c5Sec = (minOffset+maxOffset)/2.0;
			
			
			
			final double c0tmp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( c0Sec , core);
			final double c1tmp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( c1Sec , core);
			final double c2tmp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( c2Sec , core);
			final double c3tmp = finalBeatsPerMinute;
			final double c4tmp = finalBeatsPerMinute;
			final double c5tmp = finalBeatsPerMinute;
			
			final InterpolationPoint c0 = new InterpolationPoint(c0Sec, c0tmp);
			final InterpolationPoint c1 = new InterpolationPoint(c1Sec, c1tmp);
			final InterpolationPoint c2 = new InterpolationPoint(c2Sec, c2tmp);
			final InterpolationPoint c3 = new InterpolationPoint(c3Sec, c3tmp);
			final InterpolationPoint c4 = new InterpolationPoint(c4Sec, c4tmp);
			final InterpolationPoint c5 = new InterpolationPoint(c5Sec, c5tmp);
			
			
			
			final TreeMap<Double,InterpolationPoint> tm = new TreeMap<Double,InterpolationPoint>();
			
			tm.put( c0Sec, c0 );
			tm.put( c1Sec, c1 );
			tm.put( c2Sec, c2 );
			tm.put( c3Sec, c3 );
			tm.put( c4Sec, c4 );
			tm.put( c5Sec, c5 );
			
			
			ArrayList<InterpolationPoint> origInterp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
			
			
			for( count = 0 ; count < origInterp.size() ; count++ ) 
			{
				InterpolationPoint i = origInterp.get( count );
				// if( i.getParam() > origChgSeconds )
				//	i.setParam( i.getParam() +  chgSecondsDelta ); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				tm.put( i.getParam() , i );
			}
			
			
			origInterp.clear();
			
			for( Entry<Double,InterpolationPoint> e : tm.entrySet() )
			{
				origInterp.add( e.getValue() );
			}
			
			
			SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().setInterpolationPoints( origInterp );
			SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
			
		
			
			
			boolean done = false;
			
			while( !done )
			{
				SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
				PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
		    	quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
		    	BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
		    	double beatNumber = quartic.eval(c5Sec, core);
		    	if( ( maxOffset - minOffset ) < 1E-10 )
		    	{
		    		done = true;
		    	}
		    	else
		    	{
		    		if( beatNumber > chgBeatNumberEnd )
		    		{
		    			maxOffset = ( minOffset + maxOffset ) / 2.0;
		    		}
		    		else
		    		{
		    			minOffset = ( minOffset + maxOffset ) / 2.0;
		    		}
		    		final double chgSecondsEnd = ( minOffset + maxOffset ) / 2.0;
		    		c3Sec = chgSecondsEnd - 0.002;
					c4Sec = chgSecondsEnd - 0.001;
					c5Sec = chgSecondsEnd;
		    		c3.setParam( c3Sec );
		    		c4.setParam( c4Sec );
		    		c5.setParam( c5Sec );
		    		System.out.println( minOffset + "  " + maxOffset );
		    		System.out.println( chgBeatNumberEnd + "  " + beatNumber );
		    	}
			}
			
			SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
			SongListeners.updateViewPanes();
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	
	public void insertFermata( final int measureNumber , final double iBeatNum , final double chgSecondsDelta )
	{
		try
		{
			int core = 0;
			int count;
			
			undoList = new ArrayList<InterpolationPoint>( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints() );
			
			final double chgBeatNumber = SongData.measuresStore.getBeatNumberForMeasureNumber( measureNumber ) + ( iBeatNum );
			
			final double origChgSeconds = SongData.getElapsedTimeForBeatBeat(chgBeatNumber, core);
			final double interpValueSeconds = origChgSeconds + chgSecondsDelta;
			
			double minFreq = 1E-60;
			double maxFreq = 1E+60;
			
			final double c0Sec = origChgSeconds - 0.009;
			final double c1Sec = origChgSeconds - 0.008;
			final double c2Sec = origChgSeconds - 0.007;
			final double c3Sec = origChgSeconds - 0.006 + chgSecondsDelta;
			final double c4Sec = origChgSeconds - 0.005 + chgSecondsDelta;
			final double c5Sec = origChgSeconds - 0.004 + chgSecondsDelta;
			
			
			
			final double c0tmp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( c0Sec , core);
			final double c1tmp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( c1Sec , core);
			final double c2tmp = ( minFreq + maxFreq ) / 2.0;
			final double c3tmp = ( minFreq + maxFreq ) / 2.0;
			final double c4tmp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( c4Sec , core);
			final double c5tmp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( c5Sec , core);
			
			final InterpolationPoint c0 = new InterpolationPoint(c0Sec, c0tmp);
			final InterpolationPoint c1 = new InterpolationPoint(c1Sec, c1tmp);
			final InterpolationPoint c2 = new InterpolationPoint(c2Sec, c2tmp);
			final InterpolationPoint c3 = new InterpolationPoint(c3Sec, c3tmp);
			final InterpolationPoint c4 = new InterpolationPoint(c4Sec, c4tmp);
			final InterpolationPoint c5 = new InterpolationPoint(c5Sec, c5tmp);
			
			
			final TreeMap<Double,InterpolationPoint> tm = new TreeMap<Double,InterpolationPoint>();
			
			tm.put( c0Sec, c0 );
			tm.put( c1Sec, c1 );
			tm.put( c2Sec, c2 );
			tm.put( c3Sec, c3 );
			tm.put( c4Sec, c4 );
			tm.put( c5Sec, c5 );
			
			
			ArrayList<InterpolationPoint> origInterp = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
			
			
			for( count = 0 ; count < origInterp.size() ; count++ )
			{
				InterpolationPoint i = origInterp.get( count );
				if( i.getParam() > origChgSeconds )
					i.setParam( i.getParam() +  chgSecondsDelta );
				tm.put( i.getParam() , i );
			}
			
			
			origInterp.clear();
			
			for( Entry<Double,InterpolationPoint> e : tm.entrySet() )
			{
				origInterp.add( e.getValue() );
			}
			
			
			SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().setInterpolationPoints( origInterp );
			SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
			
		
			
			
			boolean done = false;
			
			while( !done )
			{
				SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
				PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
		    	quartic.integrateCurve( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
		    	BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
		    	double beatNumber = quartic.eval(interpValueSeconds, core);
		    	if( ( maxFreq - minFreq ) < 1E-10 )
		    	{
		    		done = true;
		    	}
		    	else
		    	{
		    		if( beatNumber > chgBeatNumber )
		    		{
		    			maxFreq = ( minFreq + maxFreq ) / 2.0;
		    		}
		    		else
		    		{
		    			minFreq = ( minFreq + maxFreq ) / 2.0;
		    		}
		    		c2.setValue( ( minFreq + maxFreq ) / 2.0 );
		    		c3.setValue( ( minFreq + maxFreq ) / 2.0 );
		    		System.out.println( minFreq + "  " + maxFreq );
		    		System.out.println( chgBeatNumber + "  " + beatNumber );
		    	}
			}
			
			SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
			SongListeners.updateViewPanes();
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	


	
	public void undoTempo()
	{
		final int core = 0;
		ArrayList<InterpolationPoint> tmp = new ArrayList<InterpolationPoint>( SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints() );
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().setInterpolationPoints( undoList );
		SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
		SongData.handleTempoUpdate(core);
		undoList = tmp;
		SongListeners.updateViewPanes();
	}


	protected void mouserDrag(MouseEvent e) {
		try{
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		final int core = 0;
		
		if (!mouseDragEnabled) {
			mouserDown(e);
		} else {
			switch (editMode) {

				
			case EDIT_TEMPO_INTERP_LEVEL: {
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
				
			case EDIT_TEMPO_INTERP_POSN: {
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
				double stSecondsU = startSecondsU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endSecondsU = startSecondsU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoom(stSecondsU, stLevelU, endSecondsU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
			
			case EDIT_MODE_ZOOM_X: {
				calcUFromXY(origX, origY);
				double stSecondsU = startSecondsU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endSecondsU = startSecondsU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoomX(stSecondsU, stLevelU, endSecondsU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
			
			case EDIT_MODE_ZOOM_Y: {
				calcUFromXY(origX, origY);
				double stSecondsU = startSecondsU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endSecondsU = startSecondsU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoomY(stSecondsU, stLevelU, endSecondsU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
				
			case EDIT_TEMPO_INTERP_LEVEL:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				int idx = lastDragDesc.getId();
				ArrayList<InterpolationPoint> points = SongData.TEMPO_BEATS_PER_MINUTE_CRV.getBez()
						.getInterpolationPoints();
				InterpolationPoint interpPoint = points.get(idx);
				updateInterpLevel(frU, interpPoint);
					

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_TEMPO_INTERP_POSN:
			{
				calcUFromXY(pt.x,pt.y);
				double btU = startSecondsU;
				int idx = lastDragDesc.getId();
				updateTempoInterpPosn(btU, idx);
					

				lastDragDesc.getControlPointVect().setCoords( pt.x , lastDragDesc.getControlPointVect().getY() );
			}
				break;
				
				
				default:
				{ }
				break;
			}
			

			try
			{
				SongData.handleTempoUpdate( core );
				SongListeners.updateViewPanes();
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
