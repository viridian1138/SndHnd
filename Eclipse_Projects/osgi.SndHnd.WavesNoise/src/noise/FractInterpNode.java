





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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * From Chapter 4 of the book "Fractals Everywhere" by Michael F. Barnsley.  This is an interpolation point
 * that is interpolated by a Barnsley fractal.
 * 
 * @author thorngreen
 *
 */
public class FractInterpNode implements Externalizable {
	
	/**
	 * The X-Axis domain value of the point to be interpolated.
	 */
	double x;
	
	/**
	 * The function F(x) (on Y-Axis) value at the point to be interpolated.
	 */
	double F;
	
	double H;
	
	double d;
	
	double hh;
	
	double l;
	
	double m;
	
	
	
	
	
	double a;
	
	double c;
	
	double k;
	
	double e;
	
	double g;
	
	double fF;
	
	
	/**
	 * Clones the node.
	 * @return A clone of the node.
	 */
	public FractInterpNode clone()
	{
		FractInterpNode ret = new FractInterpNode();
		
		ret.x = x;
		ret.F = F;
		
		ret.H = H;
		ret.d = d;
		
		ret.hh = hh;
		ret.l = l;
		
		ret.m = m;
		ret.a = a;
		
		ret.c = c;
		ret.k = k;
		
		ret.e = e;
		ret.g = g;
		
		ret.fF = fF;
		
		return( ret );
	}
	
	
	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("x", x);
		myv.setDouble("F", F);
		
		myv.setDouble("H", H);
		myv.setDouble("d", d);
		
		myv.setDouble("hh", hh);
		myv.setDouble("l", l);
		
		myv.setDouble("m", m);
		myv.setDouble("a", a);
		
		myv.setDouble("c", c);
		myv.setDouble("k", k);
		
		myv.setDouble("e", e);
		myv.setDouble("g", g);
		
		myv.setDouble("fF", fF);
		
		

		out.writeObject(myv);
	}

	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			x = myv.getDouble("x");
			F = myv.getDouble("F");
			
			H = myv.getDouble("H");
			d = myv.getDouble("d");
			
			hh = myv.getDouble("hh");
			l = myv.getDouble("l");
			
			m = myv.getDouble("m");
			a = myv.getDouble("a");
			
			c = myv.getDouble("c");
			k = myv.getDouble("k");
			
			e = myv.getDouble("e");
			g = myv.getDouble("g");
			
			fF = myv.getDouble("fF");

			
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
	
	/**
	 * Gets the X-Axis domain value of the point to be interpolated.
	 * @return The X-Axis domain value of the point to be interpolated.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the X-Axis domain value of the point to be interpolated.
	 * @param x The X-Axis domain value of the point to be interpolated.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the function F(x) (on Y-Axis) value at the point to be interpolated.
	 * @return The function F(x) (on Y-Axis) value at the point to be interpolated.
	 */
	public double getF() {
		return F;
	}

	/**
	 * Sets the function F(x) (on Y-Axis) value at the point to be interpolated.
	 * @param f The function F(x) (on Y-Axis) value at the point to be interpolated.
	 */
	public void setF(double f) {
		F = f;
	}

	/**
	 * @return the h
	 */
	public double getH() {
		return H;
	}

	/**
	 * @param h the h to set
	 */
	public void setH(double h) {
		H = h;
	}

	/**
	 * @return the d
	 */
	public double getD() {
		return d;
	}

	/**
	 * @param d the d to set
	 */
	public void setD(double d) {
		this.d = d;
	}

	/**
	 * @return the hh
	 */
	public double getHh() {
		return hh;
	}

	/**
	 * @param hh the hh to set
	 */
	public void setHh(double hh) {
		this.hh = hh;
	}

	/**
	 * @return the l
	 */
	public double getL() {
		return l;
	}

	/**
	 * @param l the l to set
	 */
	public void setL(double l) {
		this.l = l;
	}

	/**
	 * @return the m
	 */
	public double getM() {
		return m;
	}

	/**
	 * @param m the m to set
	 */
	public void setM(double m) {
		this.m = m;
	}

	/**
	 * @return the a
	 */
	public double getA() {
		return a;
	}

	/**
	 * @param a the a to set
	 */
	public void setA(double a) {
		this.a = a;
	}

	/**
	 * @return the c
	 */
	public double getC() {
		return c;
	}

	/**
	 * @param c the c to set
	 */
	public void setC(double c) {
		this.c = c;
	}

	/**
	 * @return the k
	 */
	public double getK() {
		return k;
	}

	/**
	 * @param k the k to set
	 */
	public void setK(double k) {
		this.k = k;
	}

	/**
	 * @return the e
	 */
	public double getE() {
		return e;
	}

	/**
	 * @param e the e to set
	 */
	public void setE(double e) {
		this.e = e;
	}

	/**
	 * @return the g
	 */
	public double getG() {
		return g;
	}

	/**
	 * @param g the g to set
	 */
	public void setG(double g) {
		this.g = g;
	}

	/**
	 * @return the fF
	 */
	public double getfF() {
		return fF;
	}

	/**
	 * @param fF the fF to set
	 */
	public void setfF(double fF) {
		this.fF = fF;
	}
	
	

}

