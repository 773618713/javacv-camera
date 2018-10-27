package com.scy.javacvcamera.test;

/**
 * 文件名：javavcCameraTest.java
 * 描述：调用windows平台的摄像头窗口视频
 * 修改时间：2016年6月13日
 * 修改内容：
 */

import javax.swing.JFrame;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 * 调用本地摄像头窗口视频
 * 
 * @author eguid
 * @version 2016年6月13日
 * @see javavcCameraTest
 * @since javacv1.2
 */

public class JavaCvCameraTest {
	/*public static void main(String[] args) throws Exception,
			InterruptedException {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		grabber.start(); // 开始获取摄像头数据
		CanvasFrame canvas = new CanvasFrame("摄像头");// 新建一个窗口
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setAlwaysOnTop(true);

		while (true) {
			if (!canvas.isDisplayable()) {// 窗口是否关闭
				grabber.stop();// 停止抓取
				System.exit(2);// 退出
			}
			canvas.showImage(grabber.grab());// 获取摄像头图像并放到窗口上显示， 这里的Frame
												// frame=grabber.grab();
												// frame是一帧视频图像

			Thread.sleep(50);// 50毫秒刷新一次图像
		}
	}*/
	
	 public static void main(String[] args) throws Exception, InterruptedException, FrameRecorder.Exception {
	        //(http://www.ossrs.net/players/srs_player.html)这个网站可以在线测试，不用自己部署推流服务器
	        //推流服务器的地址
	        recordCamera("rtmp://www.ossrs.net:1935/live/demo", 25);
	    }

	    public static void recordCamera(String outputFile, double frameRate)
	            throws Exception, InterruptedException, FrameRecorder.Exception {
	        // 0代表的是第一个摄像头，如果是笔记本外接的usb摄像头时应该改为1，以此类推
	        // 这里使用javacv的抓取器，至于使用的是ffmpeg还是opencv，请自行查看源码
	        FrameGrabber grabber = FrameGrabber.createDefault(0);
	        // 开启抓取器
	        grabber.start();

	        // 转换器
	        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	        // 抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
	        opencv_core.IplImage grabbedImage = converter.convert(grabber.grab());
	        int width = grabbedImage.width();
	        int height = grabbedImage.height();

	        FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height);
	        // avcodec.AV_CODEC_ID_H264，编码
	        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
	        // 封装格式，如果是推送到rtmp就必须是flv封装格式
	        recorder.setFormat("flv");
	        recorder.setFrameRate(frameRate);

	        recorder.start();// 开启录制器
	        long startTime = 0;
	        long videoTS = 0;
	        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setAlwaysOnTop(true);
	        // 不知道为什么这里不做转换就不能推到rtmp
	        Frame rotatedFrame = converter.convert(grabbedImage);
	        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
	            rotatedFrame = converter.convert(grabbedImage);
	            frame.showImage(rotatedFrame);
	            if (startTime == 0) {
	                startTime = System.currentTimeMillis();
	            }
	            videoTS = 1000 * (System.currentTimeMillis() - startTime);
	            recorder.setTimestamp(videoTS);
	            recorder.record(rotatedFrame);
	            Thread.sleep(40);
	        }
	        frame.dispose();
	        recorder.stop();
	        recorder.release();
	        grabber.stop();

	    }

}
