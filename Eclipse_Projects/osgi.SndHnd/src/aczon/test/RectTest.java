




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







package aczon.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import aazon.AazonEnt;
import aazon.AazonImmutableGroup;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartLine;
import aazon.vect.AazonBaseImmutableVect;
import aczon.AczonColor;
import aczon.AczonRootFactory;

/**
 * Tests the ability to display a rectangle.
 * 
 * @author tgreen
 *
 */
public class RectTest {

	/**
	 * Tests the ability to display a rectangle.
	 * 
	 * @param args Args.
	 */
	public static void main(String[] args) {
		
		final AazonEnt line1 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.0 , 0.0 ) , new AazonBaseImmutableVect( 1.0 , 1.0 ) , AczonColor.getLineMagenta() , false );
		
		final AazonEnt line2 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.0 , 0.0 ) , new AazonBaseImmutableVect( -1.0 , 1.0 ) , AczonColor.getLineGreen() , false );
		
		final AazonEnt line3 = AazonSmartLine.construct( new AazonBaseImmutableVect( -0.2 , -0.2 ) , new AazonBaseImmutableVect( -0.4 , -0.4 ) , AczonColor.getLineGreen() , false );
		
		final AazonEnt[] ents0 = { line1 , line2 , line3 };
		
		final AazonImmutableGroup gp0 = new AazonImmutableGroup( ents0 );
		
		final AazonEnt rect = AazonSmartFilledRectangle.construct( new AazonBaseImmutableVect( -0.1 , -0.1 ) , new AazonBaseImmutableVect( 0.1 , 0.1 ) , AczonColor.getFillCyan() , false );
		
		final AazonEnt[] ents1 = { gp0 , rect };
		
		final AazonImmutableOrderedGroup gp1 = new AazonImmutableOrderedGroup( ents1 );
		
		final AczonRootFactory af = new AczonRootFactory( gp1 );
		
		final JFrame fr = new JFrame();
		
		final JPanel pn = new JPanel();
		
		fr.getContentPane().setLayout( new BorderLayout( 0 , 0 ) );
		
		pn.setLayout( new BorderLayout( 0 , 0 ) );
		
		fr.getContentPane().add( BorderLayout.CENTER , pn );
		
		pn.add( BorderLayout.CENTER , af.getCanvas() );
		
		pn.setMinimumSize( new Dimension( 500 , 500 ) );
		
		pn.setPreferredSize( new Dimension( 500 , 500 ) );
		
		fr.pack();
		
		fr.show();

	}

}
