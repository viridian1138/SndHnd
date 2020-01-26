




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

/*
 * Test.java
 *
 * Created on 30 June 2003, 18:40
 */

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Screen3D;
import javax.media.j3d.View;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.ImageComponent;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * Tests the ability to display an image.
 * 
 * @author tgreen
 *
 */
public class ImageTest {

	  /**
	   * Universe for displaying the image.
	   */
      private SimpleUniverse u;

      /**
       * Constructs the test class.
       */
      public ImageTest(){}

      /**
       * Creates the scene graph for the test.
       * 
       * @return The scene graph for the test.
       */
      public BranchGroup createSceneGraph() {
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

        // Create the TransformGroup node and initialize it to the
        // identity. Enable the TRANSFORM_WRITE capability so that
        // our behavior code can modify it at run time. Add it to
        // the root of the subgraph.
        TransformGroup objTrans = new TransformGroup();
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRoot.addChild(objTrans);

        // Create a simple Shape3D node; add it to the scene graph.
        objTrans.addChild(new ColorCube(0.4));

        // Create a new Behavior object that will perform the
        // desired operation on the specified transform and add
        // it into the scene graph.
        Transform3D yAxis = new Transform3D();
        Alpha rotationAlpha = new Alpha(-1, 4000);

        RotationInterpolator rotator =
            new RotationInterpolator(rotationAlpha, objTrans, yAxis,
                                     0.0f, (float) Math.PI*2.0f);
        BoundingSphere bounds =
            new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        rotator.setSchedulingBounds(bounds);
        objRoot.addChild(rotator);

        // Have Java 3D perform optimizations on this scene graph.
        objRoot.compile();

        return objRoot;
    }

      /**
       * Tests the ability to display an image.
       * 
       * @param args Args.
       */
      public static void main(String[] args) {
          int width = 100;
          int height = 100;

              ImageTest test = new ImageTest();
              GraphicsConfiguration config = 
SimpleUniverse.getPreferredConfiguration();

              // offscreen
              Canvas3D c = new Canvas3D(config,true);

              c.getScreen3D().setSize(500,500);
              c.getScreen3D().setPhysicalScreenWidth(0.0254/90.0 * 500);
              c.getScreen3D().setPhysicalScreenHeight(0.0254/90.0 * 500);

              BranchGroup scene = test.createSceneGraph();
              test.u = new SimpleUniverse(c);
              test.u.addBranchGraph(scene);
              test.u.getViewingPlatform().setNominalViewingTransform();

              BufferedImage bImage = new BufferedImage(width, height, 
BufferedImage.TYPE_INT_RGB);
              ImageComponent2D buffer = new 
ImageComponent2D(ImageComponent.FORMAT_RGB,bImage);
              //buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

              System.out.println("Rendering...");
              c.setOffScreenBuffer(buffer);
              c.renderOffScreenBuffer();
              c.waitForOffScreenRendering();

              bImage = c.getOffScreenBuffer().getImage();
              System.out.println("Saving..");


              try {
                  DataOutputStream output = new DataOutputStream(new 
FileOutputStream("test-image.jpg"));
                  ImageIO.write(bImage,"JPEG",output);

                  output.close();
              } catch (Exception e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
              }

              System.out.println("DONE!");
              System.exit(0);
      }
}

