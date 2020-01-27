package utils;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

public interface CascadeFunction {
	Rect process(CascadeClassifier c,Mat mat);
}
