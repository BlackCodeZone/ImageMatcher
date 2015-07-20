package com.vigossjjj.main;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub

		// Create a Parser
		String sourceImage = null;
		String subImage = null;
		CommandLine commandLine = null;
		CommandLineParser parser = new BasicParser();
		Options options = new Options();
		options.addOption("h", "help", false, "help message");
		options.addOption("s", "source", true, "source image");
		options.addOption("t", "template", true, "sub image");
		// Parse the program arguments
		try {
			commandLine = parser.parse(options, args);
		} catch (Exception e) {
			// TODO: handle exception
			printUsage();
		}

		// Set the appropriate variables based on supplied options
		if (commandLine.hasOption('h')) {
			printUsage();
			System.exit(0);
		}
		if (commandLine.hasOption('s')) {
			sourceImage = commandLine.getOptionValue('s');
		}
		if (commandLine.hasOption('t')) {
			subImage = commandLine.getOptionValue('t');
		}

		verifyImage(sourceImage, subImage);

	}

	public static void printUsage() {
		String usage = "Usage:\n" + "\t-s, --source: The source image abs path;\n" + "\t-t, --template: The template image adb path\n";
		System.out.println(usage);
	}

	public static void verifyImage(String sourceImage, String subImgPath) {
		String currImgPath = sourceImage;
		TemplateMatch tm = new TemplateMatch();
		tm.load(subImgPath);// 加载带比对图片，注此图片必须小于源图
		boolean result = tm.matchTemplate(cvLoadImage(currImgPath));
		if (result) {
			System.out.println("match");
		} else {
			System.out.println("unmatch");
		}
	}

}
