




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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Vector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.media.j3d.Appearance;
import javax.swing.JComponent;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import jundo.runtime.ExtMilieuRef;

import labdaw.undo.pdx_VibratoViewPaneModel_pdx_ObjectRef;
import labdaw.undo.pdx_VibratoViewPaneModel_pdx_PairRef;
import core.TempoViewPane2.ControlSelectNode;

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
import bezier.BezierCubicNonClampedCoefficientFlat;
import bezier.BezierCubicNonClampedCoefficientFlatMultiCore;
import bezier.CubicBezierCurve;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;

public class VibratoViewPane2 extends JComponent implements MouseListener,
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
	
	protected pdx_VibratoViewPaneModel_pdx_ObjectRef model = null;

	protected static final double VERT_STRT_PCT = 0.0;

	protected static final double HORIZ_STRT_PCT = 0.2;

	protected static final double VERT_END_PCT = 0.8;

	protected static final double HORIZ_END_PCT = 1.0;

	protected static final int CRV_STROKE_SIZE = 100;

	protected int editMode = EDIT_VIBRATO_INTERP_LEVEL;

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

	public static final int EDIT_INSERT_VIBRATO_INTERP_POINT_BEFORE = 3;
	
	public static final int EDIT_INSERT_PASTE_BUFFER_VIBRATO_INTERP_POINT_BEFORE = 4;

	public static final int EDIT_INSERT_VIBRATO_INTERP_POINT_AFTER = 5;
	
	public static final int EDIT_INSERT_PASTE_BUFFER_VIBRATO_INTERP_POINT_AFTER = 6;

	public static final int EDIT_DEL_VIBRATO_INTERP_POINT = 7;

	public static final int EDIT_VIBRATO_INTERP_POSN = 8;

	public static final int EDIT_VIBRATO_INTERP_LEVEL = 9;
	
	public static final int EDIT_MODE_SELECT_VIBRATO = 10;

	
	public VibratoViewPane2( UndoManager _undoMgr ) {
		super();
		undoMgr = _undoMgr;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		pdx_VibratoViewPaneModel_pdx_PairRef pair = pdx_VibratoViewPaneModel_pdx_ObjectRef.pdxm_new_VibratoViewPaneModel(
				mil , 0.0 , 0.0 , 0.0 , 0.0 );
		mil = pair.getMilieu();
		model = (pdx_VibratoViewPaneModel_pdx_ObjectRef)( pair.getObject() );
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
		refreshDisplayListVibrato();
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
						
						if( ( editMode == EDIT_VIBRATO_INTERP_POSN ) || ( editMode == EDIT_VIBRATO_INTERP_LEVEL ) )
						{
							interp = new Vector<AazonBaseMutableVect>();
							
							calcVibratoPointsInterp(interp);
							
							AbzonSlopeGenerator generator = new AbzonMonotoneCubicSlopeGenerator( interp );
							
							AbzonCubicMonotoneCurveGenerator crv = new AbzonCubicMonotoneCurveGenerator( interp, generator);
							
							AbzonPathIteratorFactory[] fac = crv.generateCurves();

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
							
							controllingLineChild = null;
						}
						else
						{
						GeneralPath pointsa = new GeneralPath();

						calcVibratoPoints(pointsa);

						AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

						shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						
						controllingLineChild = fac;
						}
						
						controllingLineGroup = frontGreenLst;
						
						
						controllingLineGroup.add( shape );
						Appearance lineApp = app;
						
						
						if( ( editMode == EDIT_VIBRATO_INTERP_POSN ) || ( editMode == EDIT_VIBRATO_INTERP_LEVEL ) )
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
		
		final ArrayList<InterpolationPoint> intp = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez().getInterpolationPoints();
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
		
		final ArrayList<InterpolationPoint> intp = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez().getInterpolationPoints();
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
	
	
	
protected void calcVibratoPointsInterp(Vector<AazonBaseMutableVect> out) {
		
	    int core = 0;
		int cnta;
		final ArrayList<InterpolationPoint> intPoints = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez().getInterpolationPoints();
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
	
	protected void calcVibratoPoints(GeneralPath out) {
		
		int core = 0;
		int cnta;
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez();
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
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez();
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
	
	protected void updateInterpLevel(double levelU, InterpolationPoint pt) {
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setValue(level);
		SongData.VIBR_WAVES_PER_SECOND_CRV.getBez().updateAll(); // update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	protected void updateVibratoInterpPosn(double secondsU, int index) throws Throwable {
		final int core = 0;
		ArrayList<InterpolationPoint> points = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez()
				.getInterpolationPoints();
		InterpolationPoint pt = points.get(index);
		InterpolationPoint prev = points.get(index - 1);
		InterpolationPoint nxt = points.get(index + 1);
		double seconds = (1 - secondsU) * startSecondsNumber + secondsU * endSecondsNumber;
		if ((seconds > prev.getParam()) && (seconds < nxt.getParam())) {
			pt.setParam(seconds);
		}
		SongData.VIBR_WAVES_PER_SECOND_CRV.getBez().updateAll();
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

	public void refreshDisplayListVibrato() {
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
		
		
		if( ( editMode == EDIT_VIBRATO_INTERP_LEVEL ) || ( editMode == EDIT_VIBRATO_INTERP_POSN ) )
		{
			ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
			
			ArrayList<AazonEnt> controllingLineGroup = null;
			AbzonMutableGeneralPathFactory controllingLineChild = null;
				
				
				Appearance app = AczonColor.getLineGreen();
				
				controllingLineGroup = frontGreenLst;
				
				Appearance lineApp = app;
				
				Vector<AazonBaseMutableVect> interp = new Vector<AazonBaseMutableVect>();
				
				calcVibratoPointsInterp(interp);
				
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

					calcVibratoPoints(pointsa);
					
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

	public void insertVibratoInterpPointBefore() {
		editMode = EDIT_INSERT_VIBRATO_INTERP_POINT_BEFORE;
		refreshDisplayList();
	}
	
	public void insertPasteBufferVibratoInterpPointBefore() {
		editMode = EDIT_INSERT_PASTE_BUFFER_VIBRATO_INTERP_POINT_BEFORE;
		refreshDisplayList();
	}

	public void insertVibratoInterpPointAfter() {
		editMode = EDIT_INSERT_VIBRATO_INTERP_POINT_AFTER;
		refreshDisplayList();
	}
	
	public void insertPasteBufferVibratoInterpPointAfter() {
		editMode = EDIT_INSERT_PASTE_BUFFER_VIBRATO_INTERP_POINT_AFTER;
		refreshDisplayList();
	}

	public void deleteVibratoInterpPoint() {
		editMode = EDIT_DEL_VIBRATO_INTERP_POINT;
		refreshDisplayList();
	}

	public void editVibratoInterpPosn() {
		editMode = EDIT_VIBRATO_INTERP_POSN;
		refreshDisplayList();
	}

	public void editVibratoInterpLevel() {
		System.out.println("Set Edit Vibrato Interp Level");
		editMode = EDIT_VIBRATO_INTERP_LEVEL;
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
	
	public void selectVibrato() {
		editMode = EDIT_MODE_SELECT_VIBRATO;
		refreshDisplayList();
	}

	public void unZoom() {
		final int core = 0;
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-1.0);
		setEndLevel(20.0);
		setStartSecondsNumber( SongData.getElapsedTimeForBeatBeat( -10 , core ) );
		setEndSecondsNumber( SongData.getElapsedTimeForBeatBeat( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) +10 , core ) );
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
	
	public void initializeVibratoToPasteBuffer()
	{
		final int core = 0;
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = new PiecewiseCubicMonotoneBezierFlatMultiCore();
		bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , SongData.PASTE_BUFFER_BEATS_PER_MINUTE ) );
		bez.getInterpolationPoints().add( new InterpolationPoint( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) , SongData.PASTE_BUFFER_BEATS_PER_MINUTE ) );
		bez.updateAll();
		SongData.VIBR_WAVES_PER_SECOND_CRV = new BezierCubicNonClampedCoefficientFlatMultiCore( bez );
		SongData.handleVibratoUpdate( core );
	}

	public void sequesterToWindow() {
		final int core = 0;
		SongData.START_BEAT = SongData.getBeatNumber(startSecondsNumber,core);
		SongData.END_BEAT = SongData.getBeatNumber(endSecondsNumber,core);
	}

	/**
	 * @return Returns the endBeatNumber.
	 */
	public double getEndSecondsNumber() {
		return endSecondsNumber;
	}

	/**
	 * @param endBeatNumber
	 *            The endBeatNumber to set.
	 */
	public void setEndSecondsNumber(double endBeatNumber) {
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
	 * @param startBeatNumber
	 *            The startBeatNumber to set.
	 */
	public void setStartSecondsNumber(double startBeatNumber) {
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
			
		
		case EDIT_VIBRATO_INTERP_LEVEL: {
			ControlSelectNode descn = getDescForLocn(e);
			if( descn != null ) {

				lastDragDesc = descn;
				mouseDragEnabled = true;
			}
		}
			break;
			
		case EDIT_VIBRATO_INTERP_POSN: {
			int core = 0;
			ControlSelectNode descn = getDescForLocn(e);
			if (descn != null) {
				int id = descn.getId();
				int sz = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez()
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
			
		case EDIT_INSERT_VIBRATO_INTERP_POINT_BEFORE: {
			insertVibratoLevelBefore(e);
		}
			break;
			
		case EDIT_INSERT_PASTE_BUFFER_VIBRATO_INTERP_POINT_BEFORE: {
			insertPasteBufferVibratoLevelBefore(e);
		}
			break;
			
		case EDIT_INSERT_VIBRATO_INTERP_POINT_AFTER: {
			insertVibratoLevelAfter(e);
		}
			break;
			
		case EDIT_INSERT_PASTE_BUFFER_VIBRATO_INTERP_POINT_AFTER: {
			insertPasteBufferVibratoLevelAfter(e);
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
	
	protected void insertVibratoLevelBefore(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez();
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
	
	protected void insertPasteBufferVibratoLevelBefore(MouseEvent e) {
		int core = 0;
		System.out.println("InsertingBwefore");
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez();
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
	
	protected void insertVibratoLevelAfter(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez();
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
	
	protected void insertPasteBufferVibratoLevelAfter(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez();
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

	/* protected void delLevel(MouseEvent e) {
		DescInterpRect descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		Integer idx = (Integer) (descn.getRefcon());
		int id = idx.intValue();
		NoteDesc desc = descn.getDesc();
		PiecewiseCubicMonotoneBezierFlat bez = desc.getNoteEnvelope().getBez();
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
			bez.updateAll();
			SongListeners.updateViewPanes();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	} !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
	
	
	protected void mouserDrag(MouseEvent e) {
		try{
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		final int core = 0;
		
		if (!mouseDragEnabled) {
			mouserDown(e);
		} else {
			switch (editMode) {

				
			case EDIT_VIBRATO_INTERP_LEVEL: {
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
				
			case EDIT_VIBRATO_INTERP_POSN: {
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
				
			case EDIT_VIBRATO_INTERP_LEVEL:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				int idx = lastDragDesc.getId();
				ArrayList<InterpolationPoint> points = SongData.VIBR_WAVES_PER_SECOND_CRV.getBez()
						.getInterpolationPoints();
				InterpolationPoint interpPoint = points.get(idx);
				updateInterpLevel(frU, interpPoint);
					

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_VIBRATO_INTERP_POSN:
			{
				calcUFromXY(pt.x,pt.y);
				double btU = startSecondsU;
				int idx = lastDragDesc.getId();
				updateVibratoInterpPosn(btU, idx);
					

				lastDragDesc.getControlPointVect().setCoords( pt.x , lastDragDesc.getControlPointVect().getY() );
			}
				break;
				
				
				default:
				{ }
				break;
			}
			

			try
			{
				SongData.handleVibratoUpdate( core );
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
