/**
 * 
 */
package br.com.cams7.app.util;

/**
 * @author César Magalhães
 *
 */
@SuppressWarnings("serial")
public class AppException extends RuntimeException {

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(String message) {
		super(message);
	}

}
