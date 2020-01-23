





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







package noise;

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
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.swing.JComponent;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import jundo.runtime.ExtMilieuRef;
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
import core.InstrumentTrack;
import core.SongData;
import noise.undo.pdx_PwpFractInterpViewPaneModel_pdx_ObjectRef;


public class PwpFractInterpViewPane extends JComponent implements MouseListener,
		MouseMotionListener , PropertyChangeListener {
	
	/**
	 * Nodes representing the points on the waveform to be interpolated.
	 */
	ArrayList<FractInterpNode> fractInterpCurve;
	
	/**
	 * The number of subdivision iterations over which to evaluate the fractal.
	 */
	int numIter = 15;
	
	/**
	 * The waveform for the parameters being edited.
	 */
	PwpFractInterpWaveForm wv;
	 
	protected AczonRootFactory rootFactory = null;
	
	protected AazonMutableGroup bkgndLineLightGray = new AazonMutableGroup();
	
	protected AazonMutableGroup frontOrange = new AazonMutableGroup();
	
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
	

	protected double startParamNumber;

	protected double endParamNumber;

	protected double startLevel;

	protected double endLevel;
	
	protected pdx_PwpFractInterpViewPaneModel_pdx_ObjectRef model = null;

	protected static final double VERT_STRT_PCT = 0.0;

	protected static final double HORIZ_STRT_PCT = 0.2;

	protected static final double VERT_END_PCT = 0.8;

	protected static final double HORIZ_END_PCT = 1.0;

	protected static final int CRV_STROKE_SIZE = 100;

	protected int editMode = EDIT__INTERP_LEVEL;

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
	
	protected HashMap context = null;
	
	protected InstrumentTrack track = null;

	public static final int EDIT_MODE_ZOOM = 0;
	
	public static final int EDIT_MODE_ZOOM_X = 1;
	
	public static final int EDIT_MODE_ZOOM_Y = 2;

	public static final int EDIT_INSERT__INTERP_POINT_BEFORE = 3;
	
	public static final int EDIT_INSERT_PASTE_BUFFER__INTERP_POINT_BEFORE = 4;

	public static final int EDIT_INSERT__INTERP_POINT_AFTER = 5;
	
	public static final int EDIT_INSERT_PASTE_BUFFER__INTERP_POINT_AFTER = 6;

	public static final int EDIT_DEL__INTERP_POINT = 7;

	public static final int EDIT__INTERP_POSN = 8;

	public static final int EDIT__INTERP_LEVEL = 9;
	
	public static final int EDIT_MODE_SELECT_ = 10;
	
	public static final int EDIT_MODE_H = 11;
	
	public static final int EDIT_MODE_D = 12;
	
	public static final int EDIT_MODE_HH = 13;
	
	public static final int EDIT_MODE_L = 14;
	
	public static final int EDIT_MODE_M = 15;

	
	
	public PwpFractInterpViewPane( UndoManager _undoMgr ,
			HashMap _context , InstrumentTrack _track ,
			ArrayList<FractInterpNode> _fractInterpCurve , int _numIter ) {
		super();
		fractInterpCurve = _fractInterpCurve;
		numIter = _numIter;
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		undoMgr = _undoMgr;
		context = _context;
		track = _track;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		noise.undo.pdx_PwpFractInterpViewPaneModel_pdx_PairRef pair = noise.undo.pdx_PwpFractInterpViewPaneModel_pdx_ObjectRef.pdxm_new_PwpFractInterpViewPaneModel(
				mil , 0.0 , 0.0 , 0.0 , 0.0 );
		mil = pair.getMilieu();
		model = (noise.undo.pdx_PwpFractInterpViewPaneModel_pdx_ObjectRef)( pair.getObject() );
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
		startParamNumber = model.pdxm_getStartParamNumber( mil );
		endParamNumber = model.pdxm_getEndParamNumber( mil );
		startLevel = model.pdxm_getStartLevel( mil );
		endLevel = model.pdxm_getEndLevel( mil );
		refreshDisplayList();
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
		
		
		ArrayList<AazonEnt> frontOrangeLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontGreenLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontTxtBlack = new ArrayList<AazonEnt>();
		
		

		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


		ArrayList<Point3d> points = new ArrayList<Point3d>();

		final int core = 0;

		
		if (endParamNumber - startParamNumber > 10) {
				double delta = (endParamNumber - startParamNumber) / 10.0;
				double count;
				for (count = startParamNumber; count < endParamNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					String str = "" + iCount;
					calcUFromParam(iCount);
					calcXYFromU(startParamU, levelU);
					double x1 = paramX;
					Point3d lineSt = new Point3d(x1, -calcYRatio(), 0.0);
					Point3d lineEnd = new Point3d(x1, calcYRatio(), 0.0);
					points.add(lineSt);
					points.add(lineEnd);
					
					AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( x1 - 0.05 , 0.9 * ( calcYRatio() ) + 0.1 * ( -calcYRatio() ) ) , 
							str, AczonColor.getTextBlack(), "Helvetica", 8, Font.PLAIN, false);
					frontTxtBlack.add( tx );
					
				}
			} else {
				double delta = (endParamNumber - startParamNumber) / 10.0;
				double count;
				for (count = startParamNumber; count < endParamNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					int seg = (int) (1000.0 * (count - iCount));
					String str = "" + iCount + "." + seg;
					calcUFromParam(count);
					calcXYFromU(startParamU, levelU);
					double x1 = paramX;
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
			calcXYFromU(startParamU, levelU);
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
					ArrayList<AazonEnt> controllingLineGroupLine = null;
					AbzonMutableGeneralPathFactory controllingLineChild = null;
					AbzonMutableGeneralPathFactory controllingLineChildLine = null;
					AazonEnt shape = null;
					AazonEnt shapeLine = null;
					Vector interp = null;
						
						Appearance app = AczonColor.getLineGreen();
						Appearance appLine = AczonColor.getLineOrange();
						
						if( ( editMode == EDIT__INTERP_POSN ) || ( editMode == EDIT__INTERP_LEVEL ) )
						{
							GeneralPath pointsa = new GeneralPath();

							calcPoints(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						
							controllingLineChild = fac;
						}
						else
						{
							GeneralPath pointsa = new GeneralPath();

							calcPoints(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						
							controllingLineChild = fac;
						}
						
						
						switch( editMode )
						{
						
						case EDIT__INTERP_POSN:
						case EDIT__INTERP_LEVEL:
						{
							GeneralPath pointsa = new GeneralPath();

							calcPointsLine(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shapeLine = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), appLine, false);
						
							controllingLineChildLine = fac;
						}
						break;
							
						case EDIT_MODE_H:
						{
							GeneralPath pointsa = new GeneralPath();

							calcPointsLineH(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shapeLine = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), appLine, false);
						
							controllingLineChildLine = fac;
						}
						break;
						
						case EDIT_MODE_D:
						{
							GeneralPath pointsa = new GeneralPath();

							calcPointsLineD(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shapeLine = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), appLine, false);
						
							controllingLineChildLine = fac;
						}
						break;
						
						
						case EDIT_MODE_HH:
						{
							GeneralPath pointsa = new GeneralPath();

							calcPointsLineHh(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shapeLine = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), appLine, false);
						
							controllingLineChildLine = fac;
						}
						break;
						
						
						case EDIT_MODE_L:
						{
							GeneralPath pointsa = new GeneralPath();

							calcPointsLineL(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shapeLine = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), appLine, false);
						
							controllingLineChildLine = fac;
						}
						break;
						
						
						case EDIT_MODE_M:
						{
							GeneralPath pointsa = new GeneralPath();

							calcPointsLineM(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shapeLine = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), appLine, false);
						
							controllingLineChildLine = fac;
						}
						break;
						
						
						default:
						{
							GeneralPath pointsa = new GeneralPath();

							calcPointsLine(pointsa);

							AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );

							shapeLine = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), appLine, false);
						
							controllingLineChildLine = fac;
						}
						break;
						
						
						
						}
						
						
						
						controllingLineGroup = frontGreenLst;
						controllingLineGroupLine = frontOrangeLst;
						
						
						controllingLineGroup.add( shape );
						controllingLineGroupLine.add( shapeLine );
						Appearance lineApp = app;
						Appearance lineAppLine = appLine;
						
						
						switch( editMode )
						{
						
						case EDIT__INTERP_POSN:
						case EDIT__INTERP_LEVEL:
						{
							generateControlPoints(controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						break;
						
						case EDIT_MODE_H:
						{
							generateControlPointsH(controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						break;
						
						case EDIT_MODE_D:
						{
							generateControlPointsD(controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						break;	
							
						case EDIT_MODE_HH:
						{
							generateControlPointsHh(controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						break;	
							
						case EDIT_MODE_L:
						{
							generateControlPointsL(controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						break;	
							
						case EDIT_MODE_M:
						{
							generateControlPointsM(controllingLineChild,
									lineApp, AczonColor.getFillCyan() , true, controlPoints);
						}
						break;

						
						default:
							{
								generateControlPoints(controllingLineChild,
										lineApp, AczonColor.getFillCyan() , true, controlPoints);
							}
						}

						
				}
		frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
		
		
		frontOrange.setGrp( new AazonImmutableGroup( frontOrangeLst ) );
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

		
		
		
		
		AazonEnt[] objOrderEnts = { bkgndLineLightGray , frontOrange , frontGreen
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
	
	
	
	protected void generateControlPoints(AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<FractInterpNode> intp = fractInterpCurve;
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 0; cnta < sza; cnta++) {
				FractInterpNode pt = intp.get(cnta);
				calcUValuesActual(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
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
	
	
	protected void generateControlPointsH(AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<FractInterpNode> intp = fractInterpCurve;
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 0; cnta < sza; cnta++) {
				FractInterpNode pt = intp.get(cnta);
				calcUValuesActualH(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
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
	
	
	
	protected void generateControlPointsD(AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<FractInterpNode> intp = fractInterpCurve;
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 1; cnta < sza; cnta++) {
				FractInterpNode pt = intp.get(cnta);
				calcUValuesActualD(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
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
	
	
	protected void generateControlPointsHh(AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<FractInterpNode> intp = fractInterpCurve;
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 1; cnta < sza; cnta++) {
				FractInterpNode pt = intp.get(cnta);
				calcUValuesActualHh(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
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
	
	
	protected void generateControlPointsL(AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<FractInterpNode> intp = fractInterpCurve;
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 1; cnta < sza; cnta++) {
				FractInterpNode pt = intp.get(cnta);
				calcUValuesActualL(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
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
	
	
	
	protected void generateControlPointsM(AbzonMutableGeneralPathFactory controllingLineChild, 
			Appearance lineApp, Appearance ctrlApp, boolean asgnCntl , ArrayList<AazonEnt> controlPoints)
	{
		final int core = 0;
		
		final ArrayList<AazonBaseMutableVect> controlPointsLocal = new ArrayList<AazonBaseMutableVect>();
		
		final ArrayList<FractInterpNode> intp = fractInterpCurve;
		final int sza = intp.size();
		int cnta;
		
		final double ptSz = 0.005 * 400.0 / ( resizeVect.getX() );
		
			for (cnta = 1; cnta < sza; cnta++) {
				FractInterpNode pt = intp.get(cnta);
				calcUValuesActualM(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
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
		FractInterpNode pt;
		ArrayList<AazonBaseMutableVect> controlPointsLocal;
		
		public ControlSelectNode( int _id,
				Point3f _ctrlPoint,
				AbzonMutableGeneralPathFactory _controllingLineChild,
				AazonBaseMutableVect _controlPointVect,
				Appearance _lineApp,
				FractInterpNode _pt,
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
		public FractInterpNode getPt() {
			return pt;
		}


		/**
		 * @param pt The pt to set.
		 */
		public void setPt(FractInterpNode pt) {
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
	
	
	
protected void calcPointsInterp(Vector<AazonBaseMutableVect> out) {
		
	    int core = 0;
		int cnta;
		final ArrayList<FractInterpNode> intPoints = fractInterpCurve;
		final int sz = intPoints.size();
		
		for (cnta = 0; cnta < sz; cnta++) {
			FractInterpNode pt = intPoints.get(cnta);
			calcUValuesActual(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			
			final AazonBaseMutableVect centerVect = new AazonBaseMutableVect( x1 , y1 );
			
			out.add( centerVect );
		}
	}
	
	protected void calcPoints(GeneralPath out) {
		
		int core = 0;
		int cnta;

		startParamU = 0.0;
		endParamU = 1.0;
		
		final double strt = Math.max( wv.getX0() , getStartParamNumber() );
		final double end = Math.min( wv.getXN() , getEndParamNumber() );
		
		if( end < strt )
		{
			return;
		}
		
		final int MAX = 400;
		final FractInterpNode pt = new FractInterpNode();
		
		for (cnta = 0; cnta < MAX; cnta++) {
			
			final double u = ( (double) cnta ) / ( MAX - 1 );
			final double param = ( 1.0 - u ) * strt + u * end;
			final double value = wv.eval( param );
			
			if( cnta == 0 )
			{
				pt.setX( param );
				pt.setF( value );
				calcUValuesActual(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
				double y1 = levelY;
				out.moveTo((float)x1, (float)y1);
			}
			else
			{
				pt.setX( param );
				pt.setF( value );
				calcUValuesActual(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
				double y1 = levelY;
				out.lineTo((float)x1, (float)y1);
			}
			
		}
	
}
	
	
	
	
protected void calcPointsLine(GeneralPath out) {
		
		int core = 0;
		int cnta;

		final int sz = fractInterpCurve.size();
		
		for (cnta = 0; cnta < sz; cnta++) {
			
			FractInterpNode pt = fractInterpCurve.get( cnta );
			
			if( cnta == 0 )
			{
				calcUValuesActual(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
				double y1 = levelY;
				out.moveTo((float)x1, (float)y1);
			}
			else
			{
				calcUValuesActual(pt);
				calcXYFromU(startParamU, levelU);
				double x1 = paramX;
				double y1 = levelY;
				out.lineTo((float)x1, (float)y1);
			}
			
		}
	
}


protected void calcPointsLineH(GeneralPath out) {
	
	int core = 0;
	int cnta;

	final int sz = fractInterpCurve.size();
	
	for (cnta = 0; cnta < sz; cnta++) {
		
		FractInterpNode pt = fractInterpCurve.get( cnta );
		
		if( cnta == 0 )
		{
			calcUValuesActualH(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.moveTo((float)x1, (float)y1);
		}
		else
		{
			calcUValuesActualH(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.lineTo((float)x1, (float)y1);
		}
		
	}

}



protected void calcPointsLineD(GeneralPath out) {
	
	int core = 0;
	int cnta;

	final int sz = fractInterpCurve.size();
	
	for (cnta = 1; cnta < sz; cnta++) {
		
		FractInterpNode pt = fractInterpCurve.get( cnta );
		
		if( cnta == 1 )
		{
			calcUValuesActualD(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.moveTo((float)x1, (float)y1);
		}
		else
		{
			calcUValuesActualD(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.lineTo((float)x1, (float)y1);
		}
		
	}

}



protected void calcPointsLineHh(GeneralPath out) {
	
	int core = 0;
	int cnta;

	final int sz = fractInterpCurve.size();
	
	for (cnta = 1; cnta < sz; cnta++) {
		
		FractInterpNode pt = fractInterpCurve.get( cnta );
		
		if( cnta == 1 )
		{
			calcUValuesActualHh(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.moveTo((float)x1, (float)y1);
		}
		else
		{
			calcUValuesActualHh(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.lineTo((float)x1, (float)y1);
		}
		
	}

}





protected void calcPointsLineL(GeneralPath out) {
	
	int core = 0;
	int cnta;

	final int sz = fractInterpCurve.size();
	
	for (cnta = 1; cnta < sz; cnta++) {
		
		FractInterpNode pt = fractInterpCurve.get( cnta );
		
		if( cnta == 1 )
		{
			calcUValuesActualL(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.moveTo((float)x1, (float)y1);
		}
		else
		{
			calcUValuesActualL(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.lineTo((float)x1, (float)y1);
		}
		
	}

}
	




protected void calcPointsLineM(GeneralPath out) {
	
	int core = 0;
	int cnta;

	final int sz = fractInterpCurve.size();
	
	for (cnta = 1; cnta < sz; cnta++) {
		
		FractInterpNode pt = fractInterpCurve.get( cnta );
		
		if( cnta == 1 )
		{
			calcUValuesActualM(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.moveTo((float)x1, (float)y1);
		}
		else
		{
			calcUValuesActualM(pt);
			calcXYFromU(startParamU, levelU);
			double x1 = paramX;
			double y1 = levelY;
			out.lineTo((float)x1, (float)y1);
		}
		
	}

}





	protected double startParamU;

	protected double endParamU;

	protected double levelU;

	protected double paramX;

	protected double levelY;

	protected void calcUFromParam(double startParam) {
		startParamU = (startParam - startParamNumber)
				/ (endParamNumber - startParamNumber);
	}

	protected void calcUFromLevel(double level) {
		levelU = (level - startLevel) / (endLevel - startLevel);
	}
	
	protected void calcUValuesActual(double noteU) {
		final int core = 0;
		double paramNumber = (1-noteU)*startParamNumber + noteU*endParamNumber;
		double level = wv.eval(paramNumber);

		levelU = (level - startLevel) / (endLevel - startLevel);
		startParamU = 0.0;
		endParamU = 1.0;
	}
	
	protected void calcUValuesActual(FractInterpNode pt) {
		double level = pt.getF();
		double startParam = pt.getX();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startParamU = (startParam - startParamNumber)
				/ (endParamNumber - startParamNumber);
	}
	
	protected void calcUValuesActualH(FractInterpNode pt) {
		double level = pt.getH();
		double startParam = pt.getX();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startParamU = (startParam - startParamNumber)
				/ (endParamNumber - startParamNumber);
	}
	
	protected void calcUValuesActualD(FractInterpNode pt) {
		double level = pt.getD();
		double startParam = pt.getX();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startParamU = (startParam - startParamNumber)
				/ (endParamNumber - startParamNumber);
	}
	
	protected void calcUValuesActualHh(FractInterpNode pt) {
		double level = pt.getHh();
		double startParam = pt.getX();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startParamU = (startParam - startParamNumber)
				/ (endParamNumber - startParamNumber);
	}
	
	protected void calcUValuesActualL(FractInterpNode pt) {
		double level = pt.getL();
		double startParam = pt.getX();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startParamU = (startParam - startParamNumber)
				/ (endParamNumber - startParamNumber);
	}
	
	protected void calcUValuesActualM(FractInterpNode pt) {
		double level = pt.getM();
		double startParam = pt.getX();

		levelU = (level - startLevel) / (endLevel - startLevel);
		startParamU = (startParam - startParamNumber)
				/ (endParamNumber - startParamNumber);
	}
	

	
	
	
	protected void updateInterpLevel(double levelU,
			FractInterpNode pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setF(level);
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	protected void updateInterpLevelH(double levelU,
			FractInterpNode pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setH(level);
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	protected void updateInterpLevelD(double levelU,
			FractInterpNode pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setD(level);
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	protected void updateInterpLevelHh(double levelU,
			FractInterpNode pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setHh(level);
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	protected void updateInterpLevelL(double levelU,
			FractInterpNode pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setL(level);
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	protected void updateInterpLevelM(double levelU,
			FractInterpNode pt) throws Throwable {
		final int core = 0;
		double level = (1 - levelU) * startLevel + levelU * endLevel;
		pt.setM(level);
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	
	
	protected void updateInterpPosn(double paramU, int index) throws Throwable {
		final int core = 0;
		ArrayList<FractInterpNode> points = fractInterpCurve;
		FractInterpNode pt = (FractInterpNode) (points.get(index));
		FractInterpNode prev = (FractInterpNode) (points.get(index - 1));
		FractInterpNode nxt = (FractInterpNode) (points.get(index + 1));
		double param = (1 - paramU) * startParamNumber + paramU * endParamNumber;
		if ((param > prev.getX()) && (param < nxt.getX())) {
			pt.setX(param);
		}
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		// update efficiency
														// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	protected void updateZoom(double stParamU, double stLevelU, double endParamU,
			double endLevelU) {
		double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		double stParam = (1 - stParamU) * startParamNumber + stParamU
				* endParamNumber;

		double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		double endParam = (1 - endParamU) * startParamNumber + endParamU
				* endParamNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		setStartParamNumber(stParam);
		setEndParamNumber(endParam);
		setStartLevel(edLevel);
		setEndLevel(stLevel);
	}
	
	protected void updateZoomX(double stParamU, double stLevelU, double endParamU,
			double endLevelU) {
		//double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		double stParam = (1 - stParamU) * startParamNumber + stParamU
				* endParamNumber;

		//double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		double endParam = (1 - endParamU) * startParamNumber + endParamU
				* endParamNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		setStartParamNumber(stParam);
		setEndParamNumber(endParam);
		//setStartLevel(edLevel);
		//setEndLevel(stLevel);
	}
	
	protected void updateZoomY(double stParamU, double stLevelU, double endParamU,
			double endLevelU) {
		double stLevel = (1 - stLevelU) * startLevel + stLevelU * endLevel;

		//double stParam = (1 - stParamU) * startParamNumber + stParamU
		//		* endParamNumber;

		double edLevel = (1 - endLevelU) * startLevel + endLevelU * endLevel;

		//double endParam = (1 - endParamU) * startParamNumber + endParamU
		//		* endParamNumber;

		// Reverse levels to accommodate for fact that screen isn't in
		// first quadrant.
		//setStartParamNumber(stParam);
		//setEndParamNumber(endParam);
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
	
	protected void calcXYFromU(double paramU, double levelU) {
		double aX = -1.0;
		double aY = -calcYRatio();
		double bX = 1.0;
		double bY = calcYRatio();

		double cX = (1.0 - HORIZ_STRT_PCT) * aX + HORIZ_STRT_PCT * bX;
		double cY = (1.0 - VERT_STRT_PCT) * aY + VERT_STRT_PCT * bY;
		double dX = (1.0 - HORIZ_END_PCT) * aX + HORIZ_END_PCT * bX;
		double dY = (1.0 - VERT_END_PCT) * aY + VERT_END_PCT * bY;

		paramX = (1.0 - paramU) * cX + paramU * dX;
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

		startParamU = (x - cX) / (dX - cX);
		levelU = (y - cY) / (dY - cY);
	}
	

	public void refreshDisplayList() {
		ArrayList<AazonEnt> frontOrangeLst = new ArrayList<AazonEnt>();
		ArrayList<AazonEnt> frontGreenLst = new ArrayList<AazonEnt>();
		backSprites.setGrp( new AazonImmutableGroup() );
		frontSprites.setGrp( new AazonImmutableGroup() );
		ArrayList<AazonEnt> frontTxtBlack = new ArrayList<AazonEnt>();
		
		
		
		double bnd = 1.9; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		ArrayList<Point3d> points = new ArrayList<Point3d>();

		final int core = 0;
		
		
		if (endParamNumber - startParamNumber > 10) {
				double delta = (endParamNumber - startParamNumber) / 10.0;
				double count;
				for (count = startParamNumber; count < endParamNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					String str = "" + iCount;
					calcUFromParam(iCount);
					calcXYFromU(startParamU, levelU);
					double x1 = paramX;
					Point3d lineSt = new Point3d(x1, -calcYRatio(), 0.0);
					Point3d lineEnd = new Point3d(x1, calcYRatio(), 0.0);
					points.add(lineSt);
					points.add(lineEnd);
					
					AazonEnt tx = AazonSmartBlockText.construct( new AazonBaseImmutableVect( x1 - 0.05 , 0.9 * ( calcYRatio() ) + 0.1 * ( -calcYRatio() ) ) , 
							str, AczonColor.getTextBlack(), "Helvetica", 8, Font.PLAIN, false);
					frontTxtBlack.add( tx );
					
				}
			} else {
				double delta = (endParamNumber - startParamNumber) / 10.0;
				double count;
				for (count = startParamNumber; count < endParamNumber; count = count
						+ delta) {
					int iCount = (int) (count);
					int seg = (int) (1000.0 * (count - iCount));
					String str = "" + iCount + "." + seg;
					calcUFromParam(count);
					calcXYFromU(startParamU, levelU);
					double x1 = paramX;
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
			calcXYFromU(startParamU, levelU);
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
		
		
		switch( editMode )
		{
		
			case EDIT__INTERP_LEVEL :
			case EDIT__INTERP_POSN:
			{
				ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
				Appearance app = AczonColor.getLineGreen();
				Appearance appLine = AczonColor.getLineOrange();
				
				GeneralPath pointsa = new GeneralPath();
				GeneralPath pointsaLine = new GeneralPath();

				calcPoints(pointsa);
				calcPointsLine(pointsaLine);
				
				ArrayList<AazonEnt> controllingLineGroup = null;
				ArrayList<AazonEnt> controllingLineGroupLine = null;
				AbzonMutableGeneralPathFactory controllingLineChild = null;
				AbzonMutableGeneralPathFactory controllingLineChildLine = null;

				AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
				AbzonMutableGeneralPathFactory facLine = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsaLine ) );
					
					

					AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
					AazonEnt shapeLine = AbzonSmartShape.construct( facLine , flatness, new AffineTransform(), appLine, false);
					
					controllingLineGroup = frontGreenLst;
					controllingLineGroupLine = frontOrangeLst;
					
					
					controllingLineGroup.add( shape );
					controllingLineGroupLine.add( shapeLine );
					controllingLineChild = fac;
					controllingLineChildLine = facLine;
					Appearance lineApp = app;
					Appearance lineAppLine = appLine;
					
					generateControlPoints(controllingLineChild,
							lineApp, AczonColor.getFillCyan() , true, controlPoints);


					frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
			}
			break;
			
			case EDIT_MODE_H :
			{
				ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
				Appearance app = AczonColor.getLineGreen();
				Appearance appLine = AczonColor.getLineOrange();
				
				GeneralPath pointsa = new GeneralPath();
				GeneralPath pointsaLine = new GeneralPath();

				calcPoints(pointsa);
				calcPointsLineH(pointsaLine);
				
				ArrayList<AazonEnt> controllingLineGroup = null;
				ArrayList<AazonEnt> controllingLineGroupLine = null;
				AbzonMutableGeneralPathFactory controllingLineChild = null;
				AbzonMutableGeneralPathFactory controllingLineChildLine = null;

				AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
				AbzonMutableGeneralPathFactory facLine = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsaLine ) );
					
					

					AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
					AazonEnt shapeLine = AbzonSmartShape.construct( facLine , flatness, new AffineTransform(), appLine, false);
					
					controllingLineGroup = frontGreenLst;
					controllingLineGroupLine = frontOrangeLst;
					
					
					controllingLineGroup.add( shape );
					controllingLineGroupLine.add( shapeLine );
					controllingLineChild = fac;
					controllingLineChildLine = facLine;
					Appearance lineApp = app;
					Appearance lineAppLine = appLine;
					
					generateControlPointsH(controllingLineChild,
							lineApp, AczonColor.getFillCyan() , true, controlPoints);


					frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
			}
			break;
			
			case EDIT_MODE_D :
			{
				ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
				Appearance app = AczonColor.getLineGreen();
				Appearance appLine = AczonColor.getLineOrange();
				
				GeneralPath pointsa = new GeneralPath();
				GeneralPath pointsaLine = new GeneralPath();

				calcPoints(pointsa);
				calcPointsLineD(pointsaLine);
				
				ArrayList<AazonEnt> controllingLineGroup = null;
				ArrayList<AazonEnt> controllingLineGroupLine = null;
				AbzonMutableGeneralPathFactory controllingLineChild = null;
				AbzonMutableGeneralPathFactory controllingLineChildLine = null;

				AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
				AbzonMutableGeneralPathFactory facLine = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsaLine ) );
					
					

					AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
					AazonEnt shapeLine = AbzonSmartShape.construct( facLine , flatness, new AffineTransform(), appLine, false);
					
					controllingLineGroup = frontGreenLst;
					controllingLineGroupLine = frontOrangeLst;
					
					
					controllingLineGroup.add( shape );
					controllingLineGroupLine.add( shapeLine );
					controllingLineChild = fac;
					controllingLineChildLine = facLine;
					Appearance lineApp = app;
					Appearance lineAppLine = appLine;
					
					generateControlPointsD(controllingLineChild,
							lineApp, AczonColor.getFillCyan() , true, controlPoints);


					frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
			}
			break;
			
			case EDIT_MODE_HH :
			{
				ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
				Appearance app = AczonColor.getLineGreen();
				Appearance appLine = AczonColor.getLineOrange();
				
				GeneralPath pointsa = new GeneralPath();
				GeneralPath pointsaLine = new GeneralPath();

				calcPoints(pointsa);
				calcPointsLineHh(pointsaLine);
				
				ArrayList<AazonEnt> controllingLineGroup = null;
				ArrayList<AazonEnt> controllingLineGroupLine = null;
				AbzonMutableGeneralPathFactory controllingLineChild = null;
				AbzonMutableGeneralPathFactory controllingLineChildLine = null;

				AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
				AbzonMutableGeneralPathFactory facLine = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsaLine ) );
					
					

					AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
					AazonEnt shapeLine = AbzonSmartShape.construct( facLine , flatness, new AffineTransform(), appLine, false);
					
					controllingLineGroup = frontGreenLst;
					controllingLineGroupLine = frontOrangeLst;
					
					
					controllingLineGroup.add( shape );
					controllingLineGroupLine.add( shapeLine );
					controllingLineChild = fac;
					controllingLineChildLine = facLine;
					Appearance lineApp = app;
					Appearance lineAppLine = appLine;
					
					generateControlPointsHh(controllingLineChild,
							lineApp, AczonColor.getFillCyan() , true, controlPoints);


					frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
			}
			break;
			
			case EDIT_MODE_L :
			{
				ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
				Appearance app = AczonColor.getLineGreen();
				Appearance appLine = AczonColor.getLineOrange();
				
				GeneralPath pointsa = new GeneralPath();
				GeneralPath pointsaLine = new GeneralPath();

				calcPoints(pointsa);
				calcPointsLineL(pointsaLine);
				
				ArrayList<AazonEnt> controllingLineGroup = null;
				ArrayList<AazonEnt> controllingLineGroupLine = null;
				AbzonMutableGeneralPathFactory controllingLineChild = null;
				AbzonMutableGeneralPathFactory controllingLineChildLine = null;

				AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
				AbzonMutableGeneralPathFactory facLine = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsaLine ) );
					
					

					AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
					AazonEnt shapeLine = AbzonSmartShape.construct( facLine , flatness, new AffineTransform(), appLine, false);
					
					controllingLineGroup = frontGreenLst;
					controllingLineGroupLine = frontOrangeLst;
					
					
					controllingLineGroup.add( shape );
					controllingLineGroupLine.add( shapeLine );
					controllingLineChild = fac;
					controllingLineChildLine = facLine;
					Appearance lineApp = app;
					Appearance lineAppLine = appLine;
					
					generateControlPointsL(controllingLineChild,
							lineApp, AczonColor.getFillCyan() , true, controlPoints);


					frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
			}
			break;
			
			case EDIT_MODE_M :
			{
				ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
				Appearance app = AczonColor.getLineGreen();
				Appearance appLine = AczonColor.getLineOrange();
				
				GeneralPath pointsa = new GeneralPath();
				GeneralPath pointsaLine = new GeneralPath();

				calcPoints(pointsa);
				calcPointsLineM(pointsaLine);
				
				ArrayList<AazonEnt> controllingLineGroup = null;
				ArrayList<AazonEnt> controllingLineGroupLine = null;
				AbzonMutableGeneralPathFactory controllingLineChild = null;
				AbzonMutableGeneralPathFactory controllingLineChildLine = null;

				AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
				AbzonMutableGeneralPathFactory facLine = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsaLine ) );
					
					

					AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
					AazonEnt shapeLine = AbzonSmartShape.construct( facLine , flatness, new AffineTransform(), appLine, false);
					
					controllingLineGroup = frontGreenLst;
					controllingLineGroupLine = frontOrangeLst;
					
					
					controllingLineGroup.add( shape );
					controllingLineGroupLine.add( shapeLine );
					controllingLineChild = fac;
					controllingLineChildLine = facLine;
					Appearance lineApp = app;
					Appearance lineAppLine = appLine;
					
					generateControlPointsM(controllingLineChild,
							lineApp, AczonColor.getFillCyan() , true, controlPoints);


					frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
			}
			break;
		
			default:
			{
				ArrayList<AazonEnt> controlPoints = new ArrayList<AazonEnt>();
				Appearance app = AczonColor.getLineGreen();
				Appearance appLine = AczonColor.getLineOrange();
				
					GeneralPath pointsa = new GeneralPath();
					GeneralPath pointsaLine = new GeneralPath();

					calcPoints(pointsa);
					calcPointsLine(pointsaLine);
					
					ArrayList<AazonEnt> controllingLineGroup = null;
					ArrayList<AazonEnt> controllingLineGroupLine = null;
					AbzonMutableGeneralPathFactory controllingLineChild = null;
					AbzonMutableGeneralPathFactory controllingLineChildLine = null;

					AbzonMutableGeneralPathFactory fac = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsa ) );
					AbzonMutableGeneralPathFactory facLine = new AbzonMutableGeneralPathFactory( new AbzonImmutableGeneralPathFactory( pointsaLine ) );
								

						AazonEnt shape = AbzonSmartShape.construct( fac , flatness, new AffineTransform(), app, false);
						AazonEnt shapeLine = AbzonSmartShape.construct( facLine , flatness, new AffineTransform(), appLine, false);
						
						controllingLineGroup = frontGreenLst;
						controllingLineGroupLine = frontOrangeLst;
						
						
						controllingLineGroup.add( shape );
						controllingLineGroupLine.add( shapeLine );
						controllingLineChild = fac;
						controllingLineChildLine = facLine;
						Appearance lineApp = app;
						Appearance lineAppLine = appLine;
						
						generateControlPoints(controllingLineChild,
								lineApp, AczonColor.getFillCyan() , true, controlPoints);
		
		
						frontControlPointsCyan.setGrp( new AazonImmutableGroup( controlPoints ) );
			}
		}
		
		
		
		
		
		frontOrange.setGrp( new AazonImmutableGroup( frontOrangeLst ) );
		frontGreen.setGrp( new AazonImmutableGroup( frontGreenLst ) );
		frontTextBlack.setGrp( new AazonImmutableGroup( frontTxtBlack ) );
		
		rectAVectA.setCoords( -1.0 , calcYRatio() );
		
		rectAVectB.setCoords( 1.0 ,  ( calcYRatio() ) * 0.8 + -( calcYRatio() ) * 0.2 );
		
		rectBVectA.setCoords( -1.0 , -calcYRatio() );
		
		rectBVectB.setCoords( -1.0 * 0.8 + 1.0 * 0.2 , calcYRatio() );
		
	}
	
	public void insertInterpPointBefore() {
		editMode = EDIT_INSERT__INTERP_POINT_BEFORE;
		refreshDisplayList();
	}
	
	public void insertPasteBufferInterpPointBefore() {
		editMode = EDIT_INSERT_PASTE_BUFFER__INTERP_POINT_BEFORE;
		refreshDisplayList();
	}
	
	public void insertInterpPointAfter() {
		editMode = EDIT_INSERT__INTERP_POINT_AFTER;
		refreshDisplayList();
	}
	
	public void insertPasteBufferInterpPointAfter() {
		editMode = EDIT_INSERT_PASTE_BUFFER__INTERP_POINT_AFTER;
		refreshDisplayList();
	}
	
	public void deleteInterpPoint() {
		editMode = EDIT_DEL__INTERP_POINT;
		refreshDisplayList();
	}

	public void editInterpPosn() {
		editMode = EDIT__INTERP_POSN;
		refreshDisplayList();
	}
	
	public void editInterpLevel() {
		System.out.println("Set Edit  Interp Level");
		editMode = EDIT__INTERP_LEVEL;
		refreshDisplayList();
	}

	public void zoom() {
		System.out.println( "Set Zoom" );
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
	
	public void select() {
		editMode = EDIT_MODE_SELECT_;
		refreshDisplayList();
	}
	
	public void editH() {
		editMode = EDIT_MODE_H;
		refreshDisplayList();
	}
	
	public void editd() {
		editMode = EDIT_MODE_D;
		refreshDisplayList();
	}
	
	public void edithh() {
		editMode = EDIT_MODE_HH;
		refreshDisplayList();
	}
	
	public void editl() {
		editMode = EDIT_MODE_L;
		refreshDisplayList();
	}
	
	public void editm() {
		editMode = EDIT_MODE_M;
		refreshDisplayList();
	}
	
	public void setNumIter( int in )
	{
		numIter = in;
		wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
		refreshDisplayList();
	}
	
	public void generate() {
// !!!!!!!!!!!!!!!!!!!!!!!!		editMode = asdsadsasdsa EDIT_MODE_SELECT_;
//	!!!!!!!!!!!!!!!!!!!!!!!!!	refreshDisplayList();
	}
	
	public void unZoom() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-20.0);
		setEndLevel(200.0);
		setStartParamNumber( wv.getX0() -10 );
		setEndParamNumber( wv.getXN() +10);
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void unZoomLimits() {
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		setStartLevel(-0.1);
		setEndLevel(1.1);
		setStartParamNumber( wv.getX0() -10 );
		setEndParamNumber( wv.getXN() +10);
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
		double midParam = ( getStartParamNumber() + getEndParamNumber() ) / 2.0;
		double deltaParam = Math.abs( getEndParamNumber() - getStartParamNumber() );
		setStartParamNumber( midParam - deltaParam );
		setEndParamNumber( midParam + deltaParam );
		undoMgr.commitUndoableOp(utag, "UnZoom" );
		refreshDisplayList();
	}
	
	public void initializeToPasteBuffer()
	{
		throw( new RuntimeException( "NotSuppoted" ) );
		//final int core = 0;
		//PiecewiseCubicMonotoneBezierFlat bez = new PiecewiseCubicMonotoneBezierFlat();
		//bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , SongData.PASTE_BUFFER_ParamS_PER_MINUTE ) );
		//bez.getInterpolationPoints().add( new InterpolationPoint( SongData.measuresStore.getNumberOfParamsForMeasure( SongData.NUM_MEASURES ) , SongData.PASTE_BUFFER_ParamS_PER_MINUTE ) );
		//bez.updateAll();
		//track.updateTrackFrames( core );
		//piecewiseCubicMonotoneBezierFlat = bez;
		//SongData.handleUpdate( core );
	}

	/**
	 * @return Returns the endParamNumber.
	 */
	public double getEndParamNumber() {
		return endParamNumber;
	}

	/**
	 * @param endParamNumber
	 *            The endParamNumber to set.
	 */
	public void setEndParamNumber(double endParamNumber) {
		this.endParamNumber = endParamNumber;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setEndParamNumber(mil, endParamNumber);
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
		mil = model.pdxm_setEndLevel(mil, endLevel);
		undoMgr.handleCommitTempChange( mil );
	}

	/**
	 * @return Returns the startParamNumber.
	 */
	public double getStartParamNumber() {
		return startParamNumber;
	}

	/**
	 * @param startParamNumber
	 *            The startParamNumber to set.
	 */
	public void setStartParamNumber(double startParamNumber) {
		this.startParamNumber = startParamNumber;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		mil = model.pdxm_setStartParamNumber(mil, startParamNumber);
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
		mil = model.pdxm_setStartLevel(mil, startLevel);
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


		case EDIT__INTERP_LEVEL: 
		case EDIT_MODE_H:
		case EDIT_MODE_D:
		case EDIT_MODE_HH:
		case EDIT_MODE_L:
		case EDIT_MODE_M:
		{
			ControlSelectNode descn = getDescForLocn(e);
			if( descn != null ) {

				lastDragDesc = descn;
				mouseDragEnabled = true;
			}
		}
			break;
			
		case EDIT__INTERP_POSN: {
			int core = 0;
			ControlSelectNode descn = getDescForLocn(e);
			if( descn != null ) {

				int id = descn.getId();
				int sz = fractInterpCurve.size();
				if ((id > 0) && (id < (sz - 1))) {
					lastDragDesc = descn;
					mouseDragEnabled = true;
				} 
			}
		}
			break;

		case EDIT_MODE_ZOOM:
		case EDIT_MODE_ZOOM_X: 
		case EDIT_MODE_ZOOM_Y: {
			System.out.println( "Zoom Down Switch" );
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
			
		case EDIT_INSERT__INTERP_POINT_BEFORE: {
			insertLevelBefore(e);
		}
			break;
			
		case EDIT_INSERT_PASTE_BUFFER__INTERP_POINT_BEFORE: {
			insertPasteBufferLevelBefore(e);
		}
			break;
			
		case EDIT_INSERT__INTERP_POINT_AFTER: {
			insertLevelAfter(e);
		}
			break;
			
		case EDIT_INSERT_PASTE_BUFFER__INTERP_POINT_AFTER: {
			insertPasteBufferLevelAfter(e);
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
	
	protected void selectLevel(MouseEvent e) {
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		// NoteDesc desc = descn.getDesc();
		lastDragDesc = descn;
		System.out.println( "Volume Selected..." );
	}
	
	protected void insertPasteBufferLevelBefore(MouseEvent e) {
		throw( new RuntimeException( "NotSuppoted" ) );
		/* int core = 0;
		System.out.println("InsertingBwefore");
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlat bez = piecewiseCubicMonotoneBezierFlat;
		int sz = bez.getInterpolationPoints().size();
		if (id > 0) {
			ArrayList vect = new ArrayList();
			ArrayList<InterpolationPoint> interp = bez.getInterpolationPoints();
			int count;
			for (count = 0; count < id; count++) {
				vect.add(interp.get(count));
			}
			FractInterpNode apt = new FractInterpNode();
			vect.add(apt);
			FractInterpNode prev = (FractInterpNode) (interp.get(id - 1));
			FractInterpNode nxt = (FractInterpNode) (interp.get(id));
			apt.setValue( SongData.PASTE_BUFFER_ParamS_PER_MINUTE );
			apt.setParam((prev.getParam() + nxt.getParam()) / 2.0);
			for (count = id; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			bez.updateAll();
			track.updateTrackFrames( core );
			refreshDisplayList();
		} else {
			Toolkit.getDefaultToolkit().beep();
		} */
	}
	
	protected void insertPasteBufferLevelAfter(MouseEvent e) {
		throw( new RuntimeException( "NotSuppoted" ) );
		/* int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		PiecewiseCubicMonotoneBezierFlat bez = piecewiseCubicMonotoneBezierFlat;
		int sz = bez.getInterpolationPoints().size();
		if (id < (sz - 1)) {
			ArrayList vect = new ArrayList();
			ArrayList<InterpolationPoint> interp = bez.getInterpolationPoints();
			int count;
			for (count = 0; count <= id; count++) {
				vect.add(interp.get(count));
			}
			FractInterpNode apt = new FractInterpNode();
			vect.add(apt);
			FractInterpNode prev = (FractInterpNode) (interp.get(id));
			FractInterpNode nxt = (FractInterpNode) (interp.get(id + 1));
			apt.setValue( SongData.PASTE_BUFFER_ParamS_PER_MINUTE );
			apt.setParam((prev.getParam() + nxt.getParam()) / 2.0);
			for (count = id + 1; count < sz; count++) {
				vect.add(interp.get(count));
			}
			bez.setInterpolationPoints(vect);
			bez.updateAll();
			track.updateTrackFrames( core );
			refreshDisplayList();
		} else {
			Toolkit.getDefaultToolkit().beep();
		} */
	}
	
	protected void insertLevelBefore(MouseEvent e) {
		int core = 0;
		System.out.println("InsertingBwefore");
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		int sz = fractInterpCurve.size();
		if (id > 0) {
			FractInterpNode apt = new FractInterpNode();
			final double mult = 2.5;
			
			
			apt.setD( mult * 0.3 );
			
			
			apt.setHh( mult * 0.2 );
			
			
			apt.setL( mult * -0.1 );
			
			
			
			apt.setM( mult * 0.3 );
			
			FractInterpNode prev = (FractInterpNode) (fractInterpCurve
					.get(id - 1));
			FractInterpNode nxt = (FractInterpNode) (fractInterpCurve.get(id));
			fractInterpCurve.add(id, apt);
			apt.setX((prev.getX() + nxt.getX()) / 2.0);
			apt.setF((prev.getF() + nxt.getF()) / 2.0);
			apt.setH( apt.getX() );
			wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
			refreshDisplayList();
			try
			{
				track.updateTrackFrames( core );
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
			refreshDisplayList();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	protected void insertLevelAfter(MouseEvent e) {
		int core = 0;
		ControlSelectNode descn = getDescForLocn(e);
		if (descn == null) {
			return;
		}
		int id = descn.getId();
		int sz = fractInterpCurve.size();
		if (id < (sz - 1)) {
			FractInterpNode apt = new FractInterpNode();
			final double mult = 2.5;
			
			
			apt.setD( mult * 0.3 );
			
			
			apt.setHh( mult * 0.2 );
			
			
			apt.setL( mult * -0.1 );
			
			
			
			apt.setM( mult * 0.3 );
			
			FractInterpNode prev = (FractInterpNode) (fractInterpCurve
					.get(id));
			FractInterpNode nxt = (FractInterpNode) (fractInterpCurve
					.get(id + 1));
			fractInterpCurve.add(id+1, apt);
			apt.setX((prev.getX() + nxt.getX()) / 2.0);
			apt.setF((prev.getF() + nxt.getF()) / 2.0);
			apt.setH( apt.getX() );
			wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
			refreshDisplayList();
			try
			{
				track.updateTrackFrames( core );
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
			refreshDisplayList();
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

				
			case EDIT__INTERP_LEVEL: 
			case EDIT_MODE_H:
			case EDIT_MODE_D:
			case EDIT_MODE_HH:
			case EDIT_MODE_L:
			case EDIT_MODE_M:
			{
				calcUFromXY(pt.x,pt.y);

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;

			case EDIT_MODE_ZOOM:
			case EDIT_MODE_ZOOM_X:
			case EDIT_MODE_ZOOM_Y: {
				System.out.println( "Zoom Drag Switch" );
				newX = pt.x;
				newY = pt.y;
				
				lastDragDesc.getControlPointVect().setCoords(newX, newY);
			}
				break;
				
			case EDIT__INTERP_POSN: {
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
				double stParamU = startParamU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endParamU = startParamU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoom(stParamU, stLevelU, endParamU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
			
			case EDIT_MODE_ZOOM_X: {
				calcUFromXY(origX, origY);
				double stParamU = startParamU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endParamU = startParamU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoomX(stParamU, stLevelU, endParamU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
			
			case EDIT_MODE_ZOOM_Y: {
				calcUFromXY(origX, origY);
				double stParamU = startParamU;
				double stLevelU = levelU;

				calcUFromXY(newX, newY);
				double endParamU = startParamU;
				double endLevelU = levelU;

				UTag utag = new UTag();
				undoMgr.prepareForTempCommit(utag);
				updateZoomY(stParamU, stLevelU, endParamU, endLevelU);
				undoMgr.commitUndoableOp(utag, "Zoom" );
				lastDragDesc = null;
			}
			break;
				
			case EDIT__INTERP_LEVEL:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				int idx = lastDragDesc.getId();
				
				FractInterpNode interpPoint = (FractInterpNode) (fractInterpCurve
						.get(idx));
				updateInterpLevel(frU, interpPoint);
					

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_MODE_H:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				int idx = lastDragDesc.getId();
				
				FractInterpNode interpPoint = (FractInterpNode) (fractInterpCurve
						.get(idx));
				updateInterpLevelH(frU, interpPoint);
					

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_MODE_D:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				int idx = lastDragDesc.getId();
				
				FractInterpNode interpPoint = (FractInterpNode) (fractInterpCurve
						.get(idx));
				updateInterpLevelD(frU, interpPoint);
					

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_MODE_HH:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				int idx = lastDragDesc.getId();
				
				FractInterpNode interpPoint = (FractInterpNode) (fractInterpCurve
						.get(idx));
				updateInterpLevelHh(frU, interpPoint);
					

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_MODE_L:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				int idx = lastDragDesc.getId();
				
				FractInterpNode interpPoint = (FractInterpNode) (fractInterpCurve
						.get(idx));
				updateInterpLevelL(frU, interpPoint);
					

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT_MODE_M:
			{
				calcUFromXY(pt.x,pt.y);
				double frU = levelU;
				int idx = lastDragDesc.getId();
				
				FractInterpNode interpPoint = (FractInterpNode) (fractInterpCurve
						.get(idx));
				updateInterpLevelM(frU, interpPoint);
					

				lastDragDesc.getControlPointVect().setCoords( lastDragDesc.getControlPointVect().getX() , pt.y );
			}
				break;
				
			case EDIT__INTERP_POSN:
			{
				calcUFromXY(pt.x,pt.y);
				double btU = startParamU;
				int idx = lastDragDesc.getId();
				updateInterpPosn(btU, idx);
					

				lastDragDesc.getControlPointVect().setCoords( pt.x , lastDragDesc.getControlPointVect().getY() );
			}
				break;
				
				
				default:
				{ }
				break;
			}
			

			try
			{
				wv = new PwpFractInterpWaveForm( fractInterpCurve , numIter );
				track.updateTrackFrames( core );
				refreshDisplayList();
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
