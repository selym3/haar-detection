package utils;

import java.util.ArrayList;
import java.util.List;

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
	
	private List<CascadeClassifier> classifierList;
	private CascadeFunction lambda;
	
	public CascadeFilter(CascadeFunction lambda, CascadeClassifier... filters) {	
		this.lambda = lambda;
		classifierList = new ArrayList<CascadeClassifier>();
		
		for (CascadeClassifier filter : filters) {
			if (filter.empty()) {
				System.err.println(this.getClass().getName() + ": CascadeClassifier not loaded");
			} else {
				classifierList.add(filter);
			}
		}
		
		if (classifierList.size() == 0) {
			System.err.println(this.getClass().getName() + ": No valid CascadeClassifier found");
		}
	}
	
	public Rect[] process(Mat mat) {
		Rect[] results = new Rect[classifierList.size()];
		for (int i = 0;i < results.length;i++) {
			results[i] = lambda.process(classifierList.get(i),mat);
		}
		
		return results;
	}
}
