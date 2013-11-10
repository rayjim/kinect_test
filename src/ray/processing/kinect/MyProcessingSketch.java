package ray.processing.kinect;

import processing.core.*;
import SimpleOpenNI.*;

public class MyProcessingSketch extends PApplet {

  /**
	 * 
	 */
	SimpleOpenNI  context;
	int[]       userClr = new int[]{ color(255,0,0),
	                                     color(0,255,0),
	                                     color(0,0,255),
	                                     color(255,255,0),
	                                     color(255,0,255),
	                                     color(0,255,255)
	                                   };
	PVector com = new PVector();                                   
	PVector com2d = new PVector();      
	
	private static final long serialVersionUID = 1L;

	public void setup()
	{ 
		 size(640,480);
		  
		  context = new SimpleOpenNI(this);
		  if(context.isInit() == false)
		  {
		     println("Can't init SimpleOpenNI, maybe the camera is not connected!"); 
		     exit();
		     return;  
		  }
		  
		  // enable depthMap generation 
		  context.enableDepth();
		   
		  // enable skeleton generation for all joints
		  context.enableUser();
		 
		  background(200,0,0);

		  stroke(0,0,255);
		  strokeWeight(3);
		  smooth();  
		  context.mirror();
	}

	public void draw()
	{
		// update the cam
		  context.update();
		  
		  // draw depthImageMap
		  //image(context.depthImage(),0,0);
		  image(context.userImage().get(),0,0);
		 // image(co,0,0);
		  
		  // draw the skeleton if it's available
		  int[] userList = context.getUsers();
		  for(int i=0;i<userList.length;i++)
		  {
		    if(context.isTrackingSkeleton(userList[i]))
		    {
		      stroke(userClr[ (userList[i] - 1) % userClr.length ] );
		      drawSkeleton(userList[i]);
		    }      
		      
		    // draw the center of mass
		    if(context.getCoM(userList[i],com))
		    {
		      context.convertRealWorldToProjective(com,com2d);
		      stroke(100,255,0);
		      strokeWeight(1);
		      beginShape(LINES);
		        vertex(com2d.x,com2d.y - 5);
		        vertex(com2d.x,com2d.y + 5);

		        vertex(com2d.x - 5,com2d.y);
		        vertex(com2d.x + 5,com2d.y);
		      endShape();
		      
		      fill(0,255,100);
		      text(Integer.toString(userList[i]),com2d.x,com2d.y);
		    }
		  }    
	}
	
	// draw the skeleton with the selected joints
	public void drawSkeleton(int userId)
	{
	  // to get the 3d joint data
	  /*
	  PVector jointPos = new PVector();
	  context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_NECK,jointPos);
	  println(jointPos);
	  */
	  
	  context.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

	  context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

	  context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

	  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

	  context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

	  context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
	  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);  
	}

	// -----------------------------------------------------------------
	// SimpleOpenNI events

	public void onNewUser(SimpleOpenNI curContext, int userId)
	{
	  println("onNewUser - userId: " + userId);
	  println("\tstart tracking skeleton");
	  
	  curContext.startTrackingSkeleton(userId);
	}

	public void onLostUser(SimpleOpenNI curContext, int userId)
	{
	  println("onLostUser - userId: " + userId);
	}

	public void onVisibleUser(SimpleOpenNI curContext, int userId)
	{
	  //println("onVisibleUser - userId: " + userId);
	}


	public void keyPressed()
	{
	  switch(key)
	  {
	  case ' ':
	    context.setMirror(!context.mirror());
	    break;
	  }
	}
	
}