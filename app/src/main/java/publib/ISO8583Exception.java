package publib;

public class ISO8583Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ISO8583Exception() {
	}

	public ISO8583Exception(String strException) {
		super(strException);
	}
}
