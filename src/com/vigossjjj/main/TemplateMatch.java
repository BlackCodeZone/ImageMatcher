package com.vigossjjj.main;

import static com.googlecode.javacv.cpp.opencv_core.CV_FONT_HERSHEY_PLAIN;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvMinMaxLoc;
import static com.googlecode.javacv.cpp.opencv_core.cvPutText;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_TM_CCORR_NORMED;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMatchTemplate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;

import java.util.logging.Logger;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.Template;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

public class TemplateMatch {

	private static final Logger LOG = Logger.getLogger(Template.class.getSimpleName());
	private opencv_core.IplImage image;
	private String name;

	public void load(String filename) {
		image = cvLoadImage(filename); // CV_LOAD_IMAGE_COLOR);
	}

	public boolean matchTemplate(IplImage source) {
		boolean matchRes;
		IplImage result = cvCreateImage(opencv_core.cvSize(source.width() - this.image.width() + 1, source.height() - this.image.height() + 1),
				opencv_core.IPL_DEPTH_32F, 1);
		opencv_core.cvZero(result);

		cvMatchTemplate(source, this.image, result, CV_TM_CCORR_NORMED);

		opencv_core.CvPoint maxLoc = new opencv_core.CvPoint();
		opencv_core.CvPoint minLoc = new opencv_core.CvPoint();
		double[] minVal = new double[2];
		double[] maxVal = new double[2];

		cvMinMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null);
		matchRes = maxVal[0] > 0.99f ? true : false;

		cvReleaseImage(result);
		return matchRes;
	}

	IplImage convertToFloat32(IplImage img) {
		IplImage img32f = cvCreateImage(cvGetSize(img), IPL_DEPTH_32F, img.nChannels());

		for (int i = 0; i < img.height(); i++) {
			for (int j = 0; j < img.width(); j++) {
				cvSet2D(img32f, i, j, cvGet2D(img, i, j));
			}
		}
		return img32f;
	}

	public void drawTemplateLocation(opencv_core.IplImage source, opencv_core.CvPoint templatePosition) {
		opencv_core.CvFont font = new opencv_core.CvFont(CV_FONT_HERSHEY_PLAIN, 1, 1);

		opencv_core.CvPoint point = new opencv_core.CvPoint();
		point.x(templatePosition.x() + this.image.width() + 4);
		point.y(templatePosition.y() + this.image.height() + 4);
		templatePosition.x(templatePosition.x() - 4);
		templatePosition.y(templatePosition.y() - 4);
		cvRectangle(source, templatePosition, point, opencv_core.CvScalar.GREEN, 2, 8, 0);

		point.x(templatePosition.x() + 3);
		point.y(point.y() - 3);

		cvPutText(source, this.name, point, font, opencv_core.CvScalar.GREEN);
	}

	public opencv_core.IplImage getImage() {
		return image;
	}

	public void setImage(IplImage image) {
		this.image = image;
	}

	public void resize(int width, int height) {
		IplImage destination = opencv_core.cvCreateImage(cvSize(width, height), image.depth(), image.nChannels());

		cvResize(this.image, destination);

		cvReleaseImage(this.image);
		this.image = destination;
	}

	public void convertToGrayscale() {
		IplImage image = this.getImage();
		IplImage gray = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 1);

		cvCvtColor(image, gray, CV_BGR2GRAY);
		cvCvtColor(gray, image, CV_GRAY2BGR); // call by reference

		cvReleaseImage(gray);
	}

	public void freeNativeMemory() {
		cvReleaseImage(this.image);
		this.image = null;
	}

	@Override
	protected void finalize() throws Throwable {
		if (this.image != null) {
			LOG.severe("Memory from OpenCV was not cleaned up before finalize. Possible memory leak.");
		}

		super.finalize();
	}
}
