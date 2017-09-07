/**
 * 
 */
package Itau;

/**
 * @author César Magalhães
 *
 */
public class ShoplineException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public ShoplineException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ShoplineException(Throwable cause) {
		super(cause);
	}

}
