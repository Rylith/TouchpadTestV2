package network.Impl;

import java.awt.Point;
import java.util.List;

public class Util {

  /** Read a signed 32bit value */
  static public int readInt32(byte bytes[], int offset) {
    int val;
    val = ((bytes[offset] & 0xFF) << 24);
    val |= ((bytes[offset+1] & 0xFF) << 16);
    val |= ((bytes[offset+2] & 0xFF) << 8);
    val |= (bytes[offset+3] & 0xFF);
    return val;
  }


  /** Write a signed 32bit value */
  static public void writeInt32(byte[] bytes, int offset, int value) {
    bytes[offset]= (byte)((value >> 24) & 0xff);
    bytes[offset+1]= (byte)((value >> 16) & 0xff);
    bytes[offset+2]= (byte)((value >> 8) & 0xff);
    bytes[offset+3]= (byte)(value & 0xff);
  }
  
  static public double distance(Point p1, Point p2){
	     double deltaX = p1.x - p2.x;
	     double deltaY = p1.y - p2.y;
	     double result = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	      return result;
	  }

	  static public double angle(Point center, Point target){
	      double angle = Math.toDegrees(Math.atan2(target.y - center.y, target.x - center.x));

	      if(angle<0){
	          angle+=360;
	      }
	      return angle;

	  }
	    /**
	     * result linear coef with coef[0]=a and coef[1]=b in a*x+b*/
	    static public double[] regress(List<Float> bufferY,List<Float> bufferX){
	        double[] coefs = new double[2];

	        // first pass: read in data, compute xbar and ybar
	        double sumx = 0.0, sumy = 0.0/*, sumx2 = 0.0*/;
	        int offset=0;
	        int length = bufferX.size();
	        double R2=0;
	        while(R2 < 0.75 && offset<length-1){
	            for(int i = offset; i<length ; i++){
	                if(bufferX.get(i) != Integer.MIN_VALUE && bufferY.get(i) != Integer.MIN_VALUE){
	                    sumx  += bufferX.get(i);
	                    //sumx2 += x[i] * x[i];
	                    sumy  += bufferY.get(i);
	                }
	            }
	            double xbar = sumx / length;
	            double ybar = sumy / length;

	            // second pass: compute summary statistics
	            double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
	            for (int i = offset; i < length; i++) {
	                if(bufferX.get(i) != Integer.MIN_VALUE && bufferY.get(i) != Integer.MIN_VALUE){
	                    xxbar += (bufferX.get(i) - xbar) * (bufferX.get(i) - xbar);
	                    yybar += (bufferY.get(i) - ybar) * (bufferY.get(i) - ybar);
	                    xybar += (bufferX.get(i) - xbar) * (bufferY.get(i) - ybar);
	                }
	            }
	            double a = xybar / xxbar;
	            double b = ybar - a * xbar;
	            //System.out.println("Equation: "+ a+"*x+"+b);
	            coefs[0]=a;
	            coefs[1]=b;

	            // analyze results

	            //int df = length - 2;
	            //double rss = 0.0;      // residual sum of squares
	            double ssr = 0.0;      // regression sum of squares
	            for (int i = offset; i < length; i++) {
	                if(bufferX.get(i) != Integer.MIN_VALUE && bufferY.get(i) != Integer.MIN_VALUE){
	                    double fit = a*bufferX.get(i) + b;
	              //      rss += (fit - y[i]) * (fit - y[i]);
	                    ssr += (fit - ybar) * (fit - ybar);
	                }
	            }
	            R2    = ssr / yybar;
	            //double svar  = rss / df;
	            //double svar1 = svar / xxbar;
	            //double svar0 = svar/length + xbar*xbar*svar1;
	            //svar0 = svar * sumx2 / (length * xxbar);
	            //System.out.println("Coefficient of correlation : " + R2);
	            offset++;
	        }
	        return coefs;
	    }


		public static double fluidity(int x, int y, int test, int  multi) {
			int sub = 1;
			int max = Math.max(Math.abs(x), Math.abs(y));
			if(max > test){
				sub=multi*max;
			}
			return sub;
		}

}
