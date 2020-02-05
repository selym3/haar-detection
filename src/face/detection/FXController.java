package face.detection;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.CascadeFilter;
import utils.Scale;
import utils.Utils;

public class FXController {
	
	@FXML
	private Button toggle;
	@FXML
	private Button button;
	@FXML
	private ImageView currentFrame;
	
	private ScheduledExecutorService timer;
	private VideoCapture capture;
	private boolean cameraActive;
	private static final int cameraId = 0;
	private boolean tracking;
	
	// haar-cascade
	private CascadeClassifier faceCascade;
	private static final String xmlPath = "resources/haarcascades/haarcascade_frontalface_alt.xml";
	private double absFaceSize;
	private Scale ratio; // ratio to transform 
	
	private CascadeFilter cascade;
	
	private float minArea;
	
	protected void init(Scale ratio, float minArea) {
		capture = new VideoCapture();
		cameraActive = false;
		tracking = false;
		
		faceCascade = new CascadeClassifier(xmlPath);
		absFaceSize = 0;
		
		this.ratio = ratio;
		this.minArea = minArea;
		
		cascade = new CascadeFilter((CascadeClassifier c,Mat mat) -> {
			MatOfRect rects = new MatOfRect();
			c.detectMultiScale(mat, rects, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(absFaceSize,absFaceSize));
		
			Rect rect = ratio.scaleRect(Utils.largestRect(rects));
			rect.x = (int) Utils.clamp(rect.x,0,mat.width() - rect.width);
			rect.y = (int) Utils.clamp(rect.y,0,mat.height() - rect.height);
			
			return rect;
		}, faceCascade);
		
	}
	
	@FXML
	protected void toggleTrack(ActionEvent event) {
		tracking = !tracking;
	}
	
	@FXML
	protected void startCamera(ActionEvent event) {
		if (!cameraActive) {
			capture.open(cameraId);
			if (capture.isOpened()) {
				cameraActive = true;
				
				Runnable frameGrabber = () -> {
					Mat frame = grabFrame();
					Mat grayFrame = new Mat();
					Imgproc.cvtColor(frame,grayFrame,Imgproc.COLOR_BGR2GRAY);
					Imgproc.equalizeHist(grayFrame, grayFrame);
					
					absFaceSize = grayFrame.height() * minArea;
					
					Rect targetFace = cascade.process(grayFrame)[0];
					
					if (!tracking) {
						Imgproc.rectangle(frame, targetFace.tl(), targetFace.br(),new Scalar(0,255,0),3);
					} else {
						if (targetFace.area() > 0) {
							frame = Utils.resize(frame.submat(targetFace), new Size(frame.width(),frame.height()));
						}
					}
					
					Image imageToShow = Utils.mat2Image(frame);
					updateImageView(currentFrame, imageToShow);
				};
				
				timer = Executors.newSingleThreadScheduledExecutor();
				timer.scheduleAtFixedRate(frameGrabber, 0, 8, TimeUnit.MILLISECONDS);
				
				button.setText("Stop Camera");
			}
			else {
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else {
			cameraActive = false;
			button.setText("Start Camera");
			
			stopAcquisition();
		}
	}
	
	private Mat grabFrame() {
		Mat frame = new Mat();
		
		if (capture.isOpened()) {
			try {
				capture.read(frame);
				Core.flip(frame, frame, 1);
			}
			catch (Exception e) {
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	private void stopAcquisition() {
		if (timer!=null && !timer.isShutdown()) {
			try {
				timer.shutdown();
				timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (capture.isOpened()) {
			capture.release();
		}
	}
	
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}
	
	
	protected void setClosed() {
		stopAcquisition();
	}
	
}
