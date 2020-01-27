package utils;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

/**
 * 
 * A class to handle the result of a cascade classifiers
 * 
 * @author mp3bl
 *
 */
public class CascadeFilter {
	
	private CascadeClassifier classifier;
	private CascadeFunction lambda;
	
	public CascadeFilter(CascadeFunction lambda, CascadeClassifier filter) {	
		this.lambda = lambda;
		if (filter.empty()) {
			System.err.println(this.getClass().getName() + ": CascadeClassifier is empty");
		} else { 
			classifier = filter;
		}
	}
	
	public Rect process(Mat mat) {
		return lambda.process(this.classifier, mat);
	}
}
