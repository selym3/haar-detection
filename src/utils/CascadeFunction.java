package utils;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

/**
 * 
 * Process function for a CascadeFilter.
 * Use if a group CascadeClassifier should only return 
 * the same type of result
 * 
 * @author mp3bl
 *
 */
public interface CascadeFunction {
	/**
	 * 
	 * 
	 *  
	 * @param c CascadeClassifier filter with
	 * @param mat Matrix to input into CascadeClassifier
	 * @return A wanted rectangle from the CascadeClassifier results
	 */
	Rect process(CascadeClassifier c,Mat mat);
}
