
public class Circle2 extends Circle
{

public Circle2(double x, double y, double radius)
{
   super(x,y,radius);//Fixed this
}

public boolean intersects(Circle other)
{
   double d;
   d = Math.sqrt(Math.pow(center.x - other.center.x, 2) + Math.pow(center.y - other.center.y, 2));
   //System.out.println(d + " " + radius + " " + other.radius);
   if (Math.abs(radius - other.radius) <= d && d < (radius + other.radius))//Fixed this
      return true;
   else
      return false;
}

}

