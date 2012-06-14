package org.pushingpixels.substance.internal.utils;

import java.awt.Shape;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WindowOpacityUtilities {
    public static enum Translucency {
        PERPIXEL_TRANSPARENT,
        TRANSLUCENT,
        PERPIXEL_TRANSLUCENT;
    }		

	private static Method isTranslucencySupported = null;
	private static Method getWindowShape = null;
	private static Method setWindowShape = null;
	
	static {
		try {
			Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
			
			isTranslucencySupported = awtUtilitiesClass.getMethod("isTranslucencySupported", Window.class);
			getWindowShape = awtUtilitiesClass.getMethod("getWindowShape", Window.class);
			setWindowShape = awtUtilitiesClass.getMethod("setWindowShape", Window.class, Shape.class);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isTranslucencySupported(Translucency t) {
		if (isTranslucencySupported == null)
			return false;
		
		try {
			return (Boolean)isTranslucencySupported.invoke(null, t);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("could not invoke method", e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("could not invoke method", e);
		}
	}

	public static void setWindowShape(Window window, Shape shape) {
		if (setWindowShape == null)
			return;
		
		try {
			setWindowShape.invoke(null, window, shape);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("could not invoke method", e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("could not invoke method", e);
		}
	}
	
	public static Shape getWindowShape(Window window) {
		if (getWindowShape == null)
			return null;
		
		try {
			return (Shape)getWindowShape.invoke(null, window);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("could not invoke method", e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("could not invoke method", e);
		}
	}
	
}
