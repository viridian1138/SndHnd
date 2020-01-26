




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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * A lightweight slides that is efficiently drawn using a pair of rectangles.
 * Used to render the amount of progress when using time-sensitive media.
 * 
 * @author tgreen
 *
 */
public class LightweightSlider extends Component {
	
	/**
	 * The low value for the bounds of the slider.
	 */
	int lowValue;
	
	/**
	 * The high value for the bounds of the slider.
	 */
	int highValue;
	
	/**
	 * The current value displayed by the slider.
	 */
	int value;
	
	/**
	 * Property change support for firing events when the value of the slider changes.
	 */
	PropertyChangeSupport pcs;

	
	/**
	 * Constructs the slider.
	 * @param _lowValue The low value for the bounds of the slider.
	 * @param _highValue The high value for the bounds of the slider.
	 * @param mode The mode of the slider.  Not used.  Future expansion.
	 */
	public LightweightSlider( int _lowValue , int _highValue , int mode ) {
		lowValue = _lowValue;
		highValue = _highValue;
		value = lowValue;
		pcs = new PropertyChangeSupport( this );
		
		this.addMouseListener( new MouseAdapter()
		{
			@Override public void mousePressed( MouseEvent e )
			{
				Rectangle bnd = bounds();
				int vv = ( e.getX() ) * ( highValue - lowValue ) / ( bnd.width );
				setValue( vv + lowValue );
			}
		} );
		
	}
	
	
	@Override 
	public void paint( Graphics g )
	{
		if( highValue == lowValue )
				return;
		final Rectangle bnd = bounds();
		final int vv = ( value ) * ( bnd.width ) / ( highValue - lowValue );
		g.setColor( Color.BLUE );
		g.fillRect( vv , 0 , bnd.width , bnd.height );
		g.setColor( Color.GREEN );
		g.fillRect( 0 , 0 , vv , bnd.height );
	}
	
	/**
	 * Gets the current value of the slider.
	 * @return The current value of the slider.
	 */
	public int getValue()
	{
		return( value );
	}
	
	/**
	 * Sets the current value of the slider.
	 * @param _value The current value of the slider.
	 */
	public void setValue( int _value )
	{
		int v = value;
		value = _value;
		repaint();
		pcs.firePropertyChange( "abcd" , v , value );
	}
	
	@Override
	public void addPropertyChangeListener( PropertyChangeListener e )
	{
		pcs.addPropertyChangeListener( e );
	}
	
	@Override
	public void removePropertyChangeListener( PropertyChangeListener e )
	{
		pcs.removePropertyChangeListener( e );
	}
	
	
	@Override 
	public Dimension getMinimumSize()
	{
		return( new Dimension( 250 , 50 ) );
	}
	
	
	@Override 
	public Dimension getPreferredSize()
	{
		return( new Dimension( 250 , 50 ) );
	}

	
}


