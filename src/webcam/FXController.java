package webcam;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;

public class FXController {

	@FXML
	private Button button;
	@FXML
	private ImageView currentFrame;
	
	private ScheduledExecutorService timer;
	private VideoCapture capture;
	private boolean cameraActive;
	private static final int cameraId = 0;
	
	protected void init() {
		capture = new VideoCapture();
		cameraActive = false;
		
	}
	
	@FXML
	protected void startCamera(ActionEvent event) {
		if (!cameraActive) {
			capture.open(cameraId);
			if (capture.isOpened()) {
				cameraActive = true;
				
				Runnable frameGrabber = () -> {
					Mat frame = grabFrame();
					Image imageToShow = Utils.mat2Image(frame);
					updateImageView(currentFrame, imageToShow);
				};
				
				timer = Executors.newSingleThreadScheduledExecutor();
				timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
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
