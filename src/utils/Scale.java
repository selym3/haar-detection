package utils;

import org.opencv.core.Rect;

/**
 * A simple class to store an aspect ratio
 * 
 * @author mp3bl
 *
 */
public class Scale {
	
	private double ws;
	private boolean isWidth;
	private double hs;
	
	public Scale(double scale, double base) {
		this.isWidth = true;
		hs = isWidth ? base : scale;
		ws = isWidth ? scale : base;
	}
	
	public Scale(double scale, double base, boolean isWidth) {
		this.isWidth = isWidth;
		hs = isWidth ? base : scale;
		ws = isWidth ? scale : base;
	}
	
	public double base() {
		return isWidth ? hs : ws;
	}
	
	public double scale() {
		return isWidth ? ws : hs;
	}
	
	public void setBase(double bs) {
		if (isWidth) {
			hs = bs;
		} else {
			ws = bs;
		}
	}
	
	public void setScale(double bs) {
		if (isWidth) {
			ws = bs;
		} else {
			hs = bs;
		}
	}
	
	public void reScale(double bs) {
		if (isWidth) {
			double tempScale = ws/hs;
			ws = bs;
			hs = bs / tempScale;
		} else {
			double tempScale = hs/ws;
			hs = bs;
			ws = bs / tempScale;
		}
	}
	
	public void reBase(double bs) {
		if (isWidth) {
			double tempScale = ws/hs;
			hs = bs;
			ws = bs * tempScale;
		} else {
			double tempScale = hs/ws;
			ws = bs;
			hs = bs * tempScale;
		}
	}
	
	public Rect scaleRect(Rect original) {
		/*TODO:
		 * Check if rectangle is in the desired proportion,
		 * THEN change its proportions
		 */
		// Assume origin is in the center of the rectangle
		int x = original.x + original.width/2;
		int y = original.y + original.height/2;
		double w = original.width * (isWidth ? ws : hs);
		double h = original.height * (isWidth ? hs : ws);
		return new Rect((int) (x-w/2), (int) (y-h/2), (int) w, (int) h);
	}
	
	public String toString() {
		return "{ scale: "+ (isWidth ? ws : hs) +", base: "+(isWidth ? hs : ws)+"}";
	}
	
}
