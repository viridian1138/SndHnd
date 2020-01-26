




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

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

public class Tracer {

	protected static final Random rand = new Random(737);

	protected static class Vector {
		double x;

		double y;

		double z;
	}

	/**
	 * Divide by speed of sound in feet per second.
	 * 
	 * @param dist
	 * @return
	 */
	protected static final double getTimeForDistance(double dist) {
		return (dist / 344.0);
	}

	protected static double getMagSq(Vector v) {
		double x = v.x;
		double y = v.y;
		double z = v.z;
		return (x * x + y * y + z * z);
	}

	protected static double getDist(Vector v) {
		return (Math.sqrt(getMagSq(v)));
	}

	protected static double getDotProd(Vector a, Vector b) {
		return (a.x * b.x + a.y * b.y + a.z * b.z);
	}

	protected static void unitVector(Vector in, Vector out) {
		double mag = Math.sqrt(getMagSq(in));
		out.x = in.x / mag;
		out.y = in.y / mag;
		out.z = in.z / mag;
	}

	protected static double getZPlaneIntersec(Vector stPt, Vector dir) {
		double sz = stPt.z;
		double dz = dir.z;
		double t = -(sz / dz);
		if (Double.isNaN(t)) {
			t = 1E+36;
		}
		if (t < 1E-7) {
			t = 1E+36;
		}
		return (t);
	}

	protected static void getSphereIntersec(Vector S, Vector d) {
		final double r = 144 / 2.0;
		Vector V = S;
		double dsq = getMagSq(d);
		double denom = 2.0 * dsq;
		double vDotD2 = 2.0 * getDotProd(V, d);
		double urad = Math.sqrt(vDotD2 * vDotD2 - 4.0 * dsq
				* (getMagSq(V) - r * r));
		t0 = (-vDotD2 - urad) / denom;
		t1 = (-vDotD2 + urad) / denom;
		if (Double.isNaN(t0)) {
			t0 = -1E+36;
		}
		if (Double.isNaN(t1)) {
			t1 = -1E+36;
		}
		if (t0 < 1E-7) {
			t0 = -1E+36;
		}
		if (t1 < 1E-7) {
			t1 = -1E+36;
		}
		calcPt(S,d,t0);
		if( pt.x > 0.0 )
		{
			t0 = -1E+36;
		}
		calcPt(S,d,t1);
		if( pt.x > 0.0 )
		{
			t1 = -1E+36;
		}
	}

	protected static double oTime;

	protected static int oType;

	protected static double t0;

	protected static double t1;

	protected static Vector pt = new Vector();

	protected static Vector N = new Vector();

	protected static double diffuseReflectionCoeff = 0.0;

	protected static double specularReflectionCoeff = 0.0;

	protected static double calcCoeffPrime(Vector minusL, Vector V) {
		Vector Nu = getUnitVect(N);
		Vector minusLu = getUnitVect(minusL);
		double NdotL = getDotProd(minusLu, Nu);
		if (Double.isNaN(NdotL)) {
			NdotL = 0.0;
		}
		double diffuseIllum = -diffuseReflectionCoeff * NdotL;
		diffuseIllum = Math.max(diffuseIllum, 0.0);
		Vector R = new Vector();
		R.x = 2.0 * Nu.x * NdotL + minusL.x;
		R.y = 2.0 * Nu.y * NdotL + minusL.y;
		R.x = 2.0 * Nu.z * NdotL + minusL.z;
		Vector Ru = getUnitVect(R);
		Vector Vu = getUnitVect(V);
		double RdotV = getDotProd(Ru, Vu);
		if (Double.isNaN(RdotV)) {
			RdotV = 0.0;
		}
		RdotV = Math.max(RdotV, 0.0);
		double specularIllum = specularReflectionCoeff * RdotV * RdotV * RdotV;
		return (diffuseIllum + specularIllum);
	}

	protected static Vector getUnitVect(Vector v) {
		double dist = getDist(v);
		Vector vu = new Vector();
		vu.x = v.x / dist;
		vu.y = v.y / dist;
		vu.z = v.z / dist;
		return (vu);
	}

	protected static Vector calcDirPrime() {
		Vector dir = calcRandomDirection();
		while (getDotProd(N, dir) < 0.001) {
			dir = calcRandomDirection();
		}
		return (dir);
	}

	protected static void calcIntersects(Vector stPt, Vector dir) {
		oType = 0;
		double t = getZPlaneIntersec(stPt, dir);
		getSphereIntersec(stPt, dir);
		if ((t0 > 0) && (t0 < t)) {
			t = t0;
			oType = 1;
		}
		if ((t1 > 0) && (t1 < t)) {
			t = t1;
			oType = 1;
		}
		oTime = t;
	}

	protected static void calcPlaneNormal(Vector out) {
		out.x = 0;
		out.y = 0;
		out.z = 1.0;
	}

	protected static void calcSphereNormal(Vector pos, Vector out) {
		out.x = -pos.x;
		out.y = -pos.y;
		out.z = -pos.z;
	}

	protected static void sub(Vector a, Vector b, Vector c) {
		c.x = a.x - b.x;
		c.y = a.y - b.y;
		c.z = a.z - b.z;
	}
	
	protected static double dot(Vector a, Vector b) {
		double tot = a.x * b.x + a.y * b.y + a.z * b.z;
		return( tot );
	}
	
	protected static void calcPt(Vector stPt, Vector dir, double t)
	{
		double delx = dir.x * t;
		double dely = dir.y * t;
		double delz = dir.z * t;
		pt.x = stPt.x + delx;
		pt.y = stPt.y + dely;
		pt.z = stPt.z + delz;
	}

	protected static void calcReflectionCoeffs(Vector stPt, Vector dir) {
		calcIntersects(stPt, dir);
		double t = oTime;
		calcPt( stPt , dir , t );
		if (oType == 0) {
			calcPlaneNormal(N);
			diffuseReflectionCoeff = 0.7;
			specularReflectionCoeff = 0.3;
		} else {
			calcSphereNormal(pt, N);
			diffuseReflectionCoeff = 0.7;
			specularReflectionCoeff = 0.3;
		}
	}

	protected static void commitToMap(double time, double coeff, HashMap<Double,Double> out) {
		if (coeff > 1E-8) {
			Double tm = new Double(time);
			Double dbl = (Double) (out.get(tm));
			if (dbl != null) {
				out.put(tm, new Double(dbl.doubleValue() + coeff));
			} else {
				out.put(tm, new Double(coeff));
			}
		}
	}
	
	static Vector tmp = new Vector();

	protected static void calcNextPoint(Vector stPt, Vector dir,
			Vector microphonePos, Vector speakerPosLeft, Vector speakerPosRight,
			double coeff, double totDist, HashMap<Double,Double> leftOut, HashMap<Double,Double> rightOut) {
		calcReflectionCoeffs(stPt, dir);
		if (coeff > 0.01) {
			Vector stPtPrime = new Vector();
			stPtPrime.x = pt.x;
			stPtPrime.y = pt.y;
			stPtPrime.z = pt.z;
			sub(stPtPrime,microphonePos,tmp);
			double distMicPlayer = getDist(tmp);
			double coeffLight = calcCoeffPrime(dir,tmp);
			sub(stPt, stPtPrime, tmp);
			double ptToPrimeDist = getDist(tmp);
			double timeMicPlayer = getTimeForDistance(distMicPlayer + ptToPrimeDist + totDist);
			calcLeftRightCoeff(stPtPrime,microphonePos,speakerPosLeft,speakerPosRight);
			commitToMap(timeMicPlayer - leftSpeakerMicTime ,coeff*coeffLight*leftCoeff,leftOut);
			commitToMap(timeMicPlayer - rightSpeakerMicTime ,coeff*coeffLight*rightCoeff,rightOut);
			Vector dirPrime = calcDirPrime();
			double coeffPrime = calcCoeffPrime(dir, dirPrime);
			calcNextPoint(stPtPrime, dirPrime, microphonePos, speakerPosLeft, speakerPosRight, coeffPrime
					* coeff, totDist + ptToPrimeDist, leftOut, rightOut);
		}
	}

	protected static Vector calcRandomDirection() {
		Vector vect = new Vector();
		double d = rand.nextDouble() * Math.PI * 2.0;
		vect.x = 0.5 * Math.sin( d );
		vect.y = 0.5 * Math.cos( d );
		vect.z = rand.nextDouble() - 0.5;
		return (vect);
	}
	
	static Vector ta1 = new Vector();
	static Vector ta2 = new Vector();
	static Vector ta3 = new Vector();
	
	protected static void calcLeftRightCoeff(Vector stPtPrime, Vector microphonePos, Vector speakerPosLeft, Vector speakerPosRight)
	{
		sub(microphonePos,stPtPrime,ta1);
		sub(microphonePos,speakerPosLeft,ta2);
		sub(microphonePos,speakerPosRight,ta3);
		double dotLeft = dot( ta1 , ta2 );
		double dotRight = dot( ta1 , ta3 );
		if( ( dotLeft < 0.0 ) && ( dotRight < 0.0 ) )
		{
			leftCoeff = 0.0;
			rightCoeff = 0.0;
			return;
		}
		Vector ta2a = getUnitVect( ta2 );
		Vector ta3a = getUnitVect( ta3 );
		
		if( ( dotLeft <= dotRight ) && ( dotLeft > 0.0 ) )
		{
			double leftRes = dot(ta1,ta2a);
			double rightRes = calcResultant(ta1,ta2a,ta3a);
			leftCoeff = leftRes / ( Math.abs( leftRes ) + Math.abs( rightRes ) );
			rightCoeff = rightRes / ( Math.abs( leftRes ) + Math.abs( rightRes ) );
			return;
		}
		
		if( ( dotRight <= dotLeft ) && ( dotRight > 0.0 ) )
		{
			double rightRes = dot(ta1,ta3a);
			double leftRes = calcResultant(ta1,ta3a,ta2a);
			leftCoeff = leftRes / ( Math.abs( leftRes ) + Math.abs( rightRes ) );
			rightCoeff = rightRes / ( Math.abs( leftRes ) + Math.abs( rightRes ) );
			return;
		}
		
		if( dotLeft > 0.0 )
		{
			double leftRes = dot(ta1,ta2a);
			double rightRes = 0.0;
			leftCoeff = leftRes / ( Math.abs( leftRes ) + Math.abs( rightRes ) );
			rightCoeff = rightRes / ( Math.abs( leftRes ) + Math.abs( rightRes ) );
			return;
		}
		
		if( dotRight > 0.0 )
		{
			double rightRes = dot(ta1,ta3a);
			double leftRes = 0.0;
			leftCoeff = leftRes / ( Math.abs( leftRes ) + Math.abs( rightRes ) );
			rightCoeff = rightRes / ( Math.abs( leftRes ) + Math.abs( rightRes ) );
			return;
		}
		
	}
	
	protected static double calcResultant(Vector ta1, Vector ta2a, Vector ta3a)
	{
		final double a1 = dot(ta1,ta2a);
		final double xv = ta1.x - ta2a.x * a1;
		final double yv = ta1.y - ta2a.y * a1;
		final double zv = ta1.z - ta2a.z * a1;
		return( ta3a.x * xv + ta3a.y * yv + ta3a.z * zv );
	}

	protected static final int NUM_ITER = 2000;

	protected static void calcUnNormalizedMap(Vector playerPos,
			Vector speakerPosLeft, Vector speakerPosRight, HashMap<Double,Double> leftOut, HashMap<Double,Double> rightOut) {
		int count;
		for (count = 0; count < NUM_ITER; count++) {
			System.out.println(count);
			microphonePos.z = 10.0 + 6.0 * ( Math.random() ) - 3.0;
			microphonePos.y = 0.0 + 6.0 * ( Math.random() ) - 3.0;
			microphonePos.x = 30.0 + 3.0 * ( Math.random() ) - 1.5;
			sub( microphonePos , speakerPosLeft , subLeft );
			sub( microphonePos , speakerPosRight , subRight );
			sub( microphonePos , playerPos , subOrigin );
			leftSpeakerMicTime = getTimeForDistance( getDist( subLeft ) );
			rightSpeakerMicTime = getTimeForDistance( getDist( subRight ) );
			originTime = getTimeForDistance( getDist( subOrigin ) );
			calcLeftRightCoeff(playerPos,microphonePos,speakerPosLeft,speakerPosRight);
			commitToMap(originTime-leftSpeakerMicTime,0.5*leftCoeff,leftOut);
			commitToMap(originTime-rightSpeakerMicTime,0.5*rightCoeff,rightOut);
			Vector dir = calcRandomDirection();
			calcNextPoint(playerPos, dir, microphonePos, speakerPosLeft, speakerPosRight,
					1.0, 0.0, leftOut, rightOut);
		}
	}
	
	
	static Vector playerPos = new Vector();
	static Vector microphonePos = new Vector();
	static Vector speakerPosLeft = new Vector();
	static Vector speakerPosRight = new Vector();
	static Vector subLeft = new Vector();
	static Vector subRight = new Vector();
	static Vector subOrigin = new Vector();
	static double leftSpeakerMicTime;
	static double rightSpeakerMicTime;
	static double originTime;
	static double leftCoeff;
	static double rightCoeff;
	

	public static void main(String[] in) {
		try {
			HashMap<Double,Double> leftOut = new HashMap<Double,Double>();
			HashMap<Double,Double> rightOut = new HashMap<Double,Double>();
			
			playerPos.z = 10.0;
			playerPos.x = -5.0;
			playerPos.y = 3.0;
			speakerPosLeft.x = 24.0;
			speakerPosLeft.y = -8.0;
			speakerPosLeft.z = 10.0;
			speakerPosRight.x = 24.0;
			speakerPosRight.y = 8.0;
			speakerPosRight.z = 10.0;
			
			calcUnNormalizedMap(playerPos, speakerPosLeft, speakerPosRight, leftOut, rightOut);
			TreeMap<Double,Double> leftTout = new TreeMap<Double,Double>(leftOut);
			TreeMap<Double,Double> rightTout = new TreeMap<Double,Double>(rightOut);
			System.out.println("==== " + (leftTout.keySet().size()));
			System.out.println("==== " + (rightTout.keySet().size()));
			Iterator<Double> itx = leftTout.keySet().iterator();
			double mtime = (  itx.next() ).doubleValue();
			itx = rightTout.keySet().iterator();
			mtime = Math.min( mtime , ( itx.next() ).doubleValue() );
			itx = null;
			
			double leftSum = 0.0;
			for( final Entry<Double,Double> e : leftTout.entrySet() )
			{
				Double key = e.getKey();
				Double val = e.getValue();
				leftSum += val.doubleValue();
			}
			double rightSum = 0.0;
			for( final Entry<Double,Double> e : rightTout.entrySet() )
			{
				Double key = e.getKey();
				Double val = e.getValue();
				rightSum += val.doubleValue();
			}
			
			final int kmax = 1000;
			
			
			FileOutputStream leftFout = new FileOutputStream( "leftReverb.txt" );
			FileOutputStream rightFout = new FileOutputStream( "rightReverb.txt" );
			PrintStream leftPout = new PrintStream( leftFout );
			PrintStream rightPout = new PrintStream( rightFout );
			double dmax = 0.0;
			double dsum = 0.0;
			for( final Entry<Double,Double> e : leftTout.entrySet() ) {
				Double key = e.getKey();
				Double value = e.getValue();
				dsum += value.doubleValue();
				if( dsum >= ( dmax / kmax ) * leftSum )
				{
					System.out.println((key.doubleValue()-mtime) + "  "
							+ dmax );
					leftPout.println((key.doubleValue()-mtime) + ","
							+ dmax );
					while( ( dmax / kmax ) * leftSum <= dsum )
					{
						dmax++;
					}
				}
			}
			dmax = 0.0;
			dsum = 0.0;
			System.out.println( "====" );
			for( final Entry<Double,Double> e : rightTout.entrySet() ) {
				Double key = e.getKey();
				Double value = e.getValue();
				dsum += value.doubleValue();
				if( dsum >= ( dmax / kmax ) * rightSum )
				{
					System.out.println((key.doubleValue()-mtime) + "  "
							+ dmax );
					rightPout.println((key.doubleValue()-mtime) + ","
							+ dmax );
					while( ( dmax / kmax ) * rightSum <= dsum )
					{
						dmax++;
					}
				}
			}
			leftPout.close();
			rightPout.close();
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}

}
