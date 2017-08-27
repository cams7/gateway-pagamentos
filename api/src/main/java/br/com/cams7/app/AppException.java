/**
 * 
 */
package br.com.cams7.app;

/**
 * @author César Magalhães
 *
 */
@SuppressWarnings("serial")
public class AppException extends RuntimeException {

	private int statusCode;

	/**
	 * @param message
	 * @param statusCode
	 */
	public AppException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	/**
	 * @param message
	 */
	public AppException(String message) {
		this(message, 500);
	}

	/**
	 * @param cause
	 * @param statusCode
	 */
	public AppException(Throwable cause, int statusCode) {
		super(cause);
		this.statusCode = statusCode;
	}

	/**
	 * @param cause
	 */
	public AppException(Throwable cause) {
		this(cause, 500);
	}

	public int getStatusCode() {
		return statusCode;
	}

}
