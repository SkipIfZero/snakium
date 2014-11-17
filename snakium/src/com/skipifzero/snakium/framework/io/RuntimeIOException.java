package com.skipifzero.snakium.framework.io;

/**
 * Simple class extending RuntimeException, used to get rid of annoying checked IOExceptions.
 * @author Peter Hillerström
 */
public class RuntimeIOException extends RuntimeException {
	private static final long serialVersionUID = -926289325313166969L;
	public RuntimeIOException() {
		super();
	}
	public RuntimeIOException(String detailMessage) {
		super(detailMessage);
	}
	public RuntimeIOException(Throwable throwable) {
		super(throwable);
	}
	public RuntimeIOException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
