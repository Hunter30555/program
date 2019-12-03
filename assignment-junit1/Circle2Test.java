
/***
* Example JUnit testing class for Circle2 (and Circle)
*
* - must have your classpath set to include the JUnit jarfiles
* - to run the test do:
*     java org.junit.runner.JUnitCore Circle2Test
* - note that the commented out main is another way to run tests
* - note that normally you would not have print statements in
*   a JUnit testing class; they are here just so you see what is
*   happening. You should not have them in your test cases.
***/

import org.junit.*;

public class Circle2Test
{
   // Data you need for each test case
   private Circle1 circle1;
   private Circle2 circle2;
   private Circle1 circle3;
   private Circle2 circle4;

// 
// Stuff you want to do before each test case
//
@Before
public void setup()
{
   System.out.println("\nTest starting...");
   circle1 = new Circle1(1,2,3);
   circle2 = new Circle2(1,2,3);
   
   circle3 = new Circle1(1,6,3);
   circle4 = new Circle2(6,6,3);
}

//
// Stuff you want to do after each test case
//
@After
public void teardown()
{
   System.out.println("\nTest finished.");
}

//
// Test a simple positive move
//
@Test
public void simpleMove()
{
   Point p;
   System.out.println("Running test simpleMove.");
   p = circle2.moveBy(1,1);
   Assert.assertTrue(p.x == 2 && p.y == 3);
}

// 
// Test a simple negative move
//
@Test
public void simpleMoveNeg()
{
   Point p;
   System.out.println("Running test simpleMoveNeg.");
   p = circle2.moveBy(-1,-1);
   Assert.assertTrue(p.x == 0 && p.y == 1);
}

//
//Test to check if circles intersect (same size, true)
//
@Test
public void IntersectingCirclesTrue()
{
   System.out.println("Running test intersectingCirclesTrue.");
   boolean b = circle2.intersects(circle1);
   Assert.assertTrue(b == true);
}

//
//Test to check if circles intersect (an edge case that do intersect, false)
//
@Test
public void intersectingCirclesFalse()
{
   System.out.println("Running test intersectingCirclesFalse.");
   boolean b = circle4.intersects(circle3);
   Assert.assertTrue(b == true);
}

//
//Test to check if circles intersect (a circle inside another circle, false)
//
@Test
public void intersectingCirclesFalse2()
{
System.out.println("Running test intersectingCirclesFalse2.");
circle2.scale(.5);
boolean b = circle2.intersects(circle1);
Assert.assertTrue(b == false);
}

//
//Test to check if scaling circles work
//
@Test
public void scalingCircle()
{
   System.out.println("Running test scalingCircles.");
   circle2.scale(2);
   Assert.assertTrue(circle2.radius == 6);
}


/*** NOT USED
public static void main(String args[])
{
   try {
      org.junit.runner.JUnitCore.runClasses(
               java.lang.Class.forName("Circle2Test"));
   } catch (Exception e) {
      System.out.println("Exception: " + e);
   }
}
***/

}

