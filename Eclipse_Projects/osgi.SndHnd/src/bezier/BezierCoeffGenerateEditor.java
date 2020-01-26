




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







package bezier;

import greditcoeffn.EditPackCoeffn;
import grview.DraggableTransformNodeTest;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import palettes.PaletteClassesWave;
import verdantium.VerdantiumUtils;
import verdantium.undo.UndoManager;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;
import aazon.builderNode.AazonTransChld;
import aazon.builderNode.BuilderNode;
import aczon.AczonUnivAllocator;
import core.InstrumentTrack;
import core.InterpolationPoint;
import core.NonClampedCoefficient;
import cwaves.GSineWaveform;

/**
 * A property editor that allows the size of the document page to be changed for
 * a client property editor or component.
 * <P>
 * 
 * @author Thorn Green
 */
public class BezierCoeffGenerateEditor extends PropertyEditAdapter implements
		ActionListener {

	/**
	 * The panel in which the property editor lies.
	 */
	private JPanel MyPan = new JPanel();

	protected PiecewiseCubicMonotoneBezierFlatViewPane bezierCoeffEditor = null;
	
	PiecewiseCubicMonotoneBezierFlat crv = null;
	
	EditPackCoeffn editPack = new EditPackCoeffn();
	
	InstrumentTrack track;
	
	HashMap context;
	
	protected JButton editCoeffButton = new JButton( "Edit Coeff" );
	
	protected JTextField startParam = new JTextField( "0.0" );
	
	protected JTextField endParam = new JTextField( "3.2" );
	
	protected JTextField numPts = new JTextField( "20" );
	
	protected JButton applyButton = new JButton( "Apply" );
	
	

	/**
	 * Constructs the property editor for a given PageSizeHandler.
	 */
	public BezierCoeffGenerateEditor(PiecewiseCubicMonotoneBezierFlatViewPane in , 
			PiecewiseCubicMonotoneBezierFlat _crv , InstrumentTrack _track , HashMap _context ) {
		bezierCoeffEditor = in;
		crv = _crv;
		track = _track;
		context = _context;
		MyPan.setLayout(new BorderLayout(0, 0));
		MyPan.add(BorderLayout.SOUTH, applyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));

		pan2.add("any", editCoeffButton);
		pan2.add("any", new JLabel("StartParam : "));
		pan2.add("any", startParam);
		pan2.add("any", new JLabel("EndParam : "));
		pan2.add("any", endParam);
		pan2.add("any", new JLabel("NumPts : "));
		pan2.add("any", numPts);
		pan2.add("any", applyButton);

		applyButton.addActionListener(this);
		editCoeffButton.addActionListener(this);
		
		HashSet elem = editPack.getElem();
		
		GSineWaveform cls = new GSineWaveform();
		editPack.getCoeffOut().performAssign( cls );
		
		AazonTransChld.initialCoords( AczonUnivAllocator.allocateUniv() , elem, null, editPack.getCoeffOut());
		
	}

	/**
	 * Gets the GUI of the property editor.
	 */
	public JComponent getGUI() {
		return (MyPan);
	}

	/**
	 * Handles the destruction of the component by removing appropriate change
	 * listeners.
	 */
	public void handleDestroy() {
	}
	
	
	protected static double sinh( double x )
	{
		return( ( Math.exp( x ) - Math.exp( -x ) ) / 2.0 );
	}
	
	
	protected static double asinh( double z )
	{
		return( Math.log( z + Math.sqrt( z * z + 1 ) ) );
	}
	
	
	protected void normalizeInterpolationPoints( ArrayList<InterpolationPoint> interpPoints , double minVal , double maxVal )
	{
		for( final InterpolationPoint pt : interpPoints )
		{
			double val = pt.getValue();
			double u = ( val - minVal ) / ( maxVal - minVal );
			pt.setValue( ( u - 0.5 ) * 4.0 );
		}
	}

	
	/**
	 * Handles a button-press event from the Apply button by changing the size
	 * of the client page size handler.
	 */
	public void actionPerformed(ActionEvent e) {
		final int core = 0;
		Object src = e.getSource();
		
		if( src == editCoeffButton )
		{
			HashSet<BuilderNode> s = editPack.getElem();
			InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
			UndoManager undoMgr = (UndoManager)( context.get( "UndoMgr" ) );
			if( ( track == null ) || ( undoMgr == null ) )
			{
				throw( new RuntimeException( "TrkIsNull" ) );
			}
			Object univ1 = AczonUnivAllocator.allocateUniv();
			Object univ2 = AczonUnivAllocator.allocateUniv();
			DraggableTransformNodeTest.start(univ1,univ2,s,track,undoMgr,new PaletteClassesWave());
		}

		if (src == applyButton) {
			try {
				
				
				double startP = Double.parseDouble( startParam.getText() );
				double endP = Double.parseDouble( endParam.getText() );
				int numPt = Integer.parseInt( numPts.getText() );
				
				crv.getInterpolationPoints().clear();
				int count;
				NonClampedCoefficient Coeff = editPack.processCoeff();
				for( count = 0 ; count < numPt ; count++ )
				{
					double u = 1.0 * count / ( numPt - 1 );
					double param = ( 1.0 - u ) * startP + u * endP;
					double val = Coeff.eval( param );
					InterpolationPoint pt = new InterpolationPoint( param , val );
					crv.getInterpolationPoints().add( pt );
				}
				crv.updateAll();
				track.updateTrackFrames(core);
				bezierCoeffEditor.refreshDisplayList();
				

				// EtherEvent send =
				// new PropertyEditEtherEvent(
				// this,
				// PropertyEditEtherEvent.setPageSize,
				// null,
				// MyPage);
				// send.setParameter(new Dimension((int) wid, (int) hei));
				// ProgramDirector.fireEtherEvent(send, null);
			} catch (NumberFormatException ex) {
				handleThrow(new IllegalInputException(
						"Something input was not a number.", ex));
			} catch (Throwable ex) {
				handleThrow(ex);
			}
		}

	}

	/**
	 * Handles the throwing of an error or exception.
	 */
	public void handleThrow(Throwable in) {
		VerdantiumUtils.handleThrow(in, this, null);
	}

}
