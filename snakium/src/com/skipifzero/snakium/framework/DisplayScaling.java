package com.skipifzero.snakium.framework;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * A class for scaling between different displays.
 * 
 * Density-independent pixel (dp):
 * Virtual pixels with a dpi of 160.
 * pixel = dp * (screenDPI / 160)
 * 
 * @author Peter Hillerström
 * @version 1
 */
public class DisplayScaling {
	public static final double INCH_TO_CM = 2.54;
	public static final double CM_TO_INCH = 1 / INCH_TO_CM;
	
	private final double pixelToInch;
	private final double inchToPixel;
	private final double pixelToDp;
	private final double dpToPixel;
	
	private View view;
	private DisplayMetrics metrics;
	
	public DisplayScaling(View view, WindowManager windowManager) {
		this.view = view;
		metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		
		inchToPixel = metrics.densityDpi;
		pixelToInch = 1.0 / inchToPixel;
		dpToPixel = metrics.densityDpi / 160.0;
		pixelToDp = 1.0 / dpToPixel;
	}
	
	
	/*
	 * View info
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns view width in pixels.
	 * Warning, view must be initialized, otherwise this will return 0.
	 * @return view width in pixels
	 */
	public int getViewWidthPixels() {
		return view.getWidth();
	}
	
	/**
	 * Returns view height in pixels.
	 * Warning, view must be initialized, otherwise this will return 0.
	 * @return view height in pixels
	 */
	public int getViewHeightPixels() {
		return view.getHeight();
	}
	
	/**
	 * Returns view width in inches.
	 * Warning, view must be initialized, otherwise this will return 0.
	 * @return view width in inches
	 */
	public double getViewWidthInches() {
		return pixelToInch(getViewWidthPixels());
	}
	
	/**
	 * Returns view height in inches.
	 * Warning, view must be initialized, otherwise this will return 0.
	 * @return view height in inches
	 */
	public double getViewHeightInches() {
		return pixelToInch(getViewHeightPixels());
	}
	
	/**
	 * Returns view width in centimeters.
	 * Warning, view must be initialized, otherwise this will return 0.
	 * @return view width in cms
	 */
	public double getViewWidthCms() {
		return getViewWidthInches() * INCH_TO_CM;
	}
	
	/**
	 * Returns view height in centimeters.
	 * Warning, view must be initialized, otherwise this will return 0.
	 * @return view height in cms
	 */
	public double getViewHeightCms() {
		return getViewHeightInches() * INCH_TO_CM;
	}
	
	/**
	 * Returns view width in density-independent pixels.
	 * Warning, view must be initialized, otherwise this will return 0.
	 * @return view width in density-independent pixels
	 */
	public double getViewWidthDps() {
		return pixelToDp(getViewWidthPixels());
	}
	
	/**
	 * Return view height in dps.
	 * Warning, view must be initialized, otherwise this will return 0.
	 * @return view height in dps
	 */
	public double getViewHeightDps() {
		return pixelToDp(getViewHeightPixels());
	}
	
	/*
	 * Screen info
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns screen DPI.
	 * @return screen dpi
	 */
	public int getDpi() {
		return metrics.densityDpi;
	}
	
	/**
	 * Returns actual screen width (excluding navigation buttons) in pixels.
	 * @return screen width in pixels
	 */
	public int getScreenWidthPixels(){
		return metrics.widthPixels;
	}
	
	/**
	 * Returns actual screen height (excluding navigation buttons) in pixels.
	 * @return screen height in pixels
	 */
	public int getScreenHeightPixels(){
		return metrics.heightPixels;
	}
	
	/*
	 * Size converters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Convert pixel to centimeter.
	 * Result varies depending on screen DPI.
	 * @param pixel
	 * @return cm
	 */
	public double pixelToCm(double pixel) {
		return pixel * pixelToInch * INCH_TO_CM;
	}
	
	/**
	 * Convert centimeter to pixel.
	 * Result varies depending on screen DPI.
	 * @param cm
	 * @return pixel
	 */
	public double cmToPixel(double cm) {
		return cm * CM_TO_INCH * inchToPixel;
	}
	
	/**
	 * Convert pixel to inch.
	 * Result varies depending on screen DPI.
	 * @param pixel
	 * @return inch
	 */
	public double pixelToInch(double pixel) {
		return pixel * pixelToInch;
	}
	
	/**
	 * Convert inch to pixel.
	 * Result varies depending on screen DPI.
	 * @param inch
	 * @return pixel
	 */
	public double inchToPixel(double inch) {
		return inch * inchToPixel;
	}
	
	/**
	 * Convert pixel to density-independent pixels (dp).
	 * Result varies depending on screen DPI.
	 * @param pixel
	 * @return dp
	 */
	public double pixelToDp(double pixel) {
		return pixel * pixelToDp;
	}
	
	/**
	 * Convert density-independent pixels (dp) to pixel.
	 * Result varies depending on screen DPI.
	 * @param dp
	 * @return pixel
	 */
	public double dpToPixel(double dp) {
		return dp * dpToPixel;
	}
	
	/**
	 * Convert x-coordinate from custom coordinate system to x-coordinate in views coordinate system.
	 * @param custom the x-coordinate to convert
	 * @param customWidth the width of the custom coordinate system
	 * @return x-coordinate in views coordinate system
	 */
	public double customToPixelWidth(double custom, double customWidth) {
		return custom * getViewWidthPixels() / customWidth;
	}
	
	/**
	 * Convert y-coordinate from custom coordinate system to y-coordinate in views coordinate system.
	 * @param custom the y-coordinate to convert
	 * @param customHeight the height of the custom coordinate system
	 * @return y-coordinate in views coordinate system
	 */
	public double customToPixelHeight(double custom, double customHeight) {
		return custom * getViewHeightPixels() / customHeight;
	}
}
