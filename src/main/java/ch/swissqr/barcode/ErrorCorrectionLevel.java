package ch.swissqr.barcode;

/**
 * Error Correction Levels for QR Codes
 *
 * @author pschatzmann
 */
public enum ErrorCorrectionLevel {

	/** L = ~7% correction */
	L(0x01),
	/** M = ~15% correction */
	M(0x00),
	/** Q = ~25% correction */
	Q(0x03),
	/** H = ~30% correction */
	H(0x02);

	/** Constant <code>FOR_BITS</code> */
	private static final ErrorCorrectionLevel[] FOR_BITS = { M, L, H, Q };

	private final int bits;
	
	ErrorCorrectionLevel(int bits) {
		this.bits = bits;
	}

	/**
	 * <p>Getter for the field <code>bits</code>.</p>
	 *
	 * @return a int
	 */
	public int getBits() {
		return bits;
	}

	/**
	 * <p>forBits.</p>
	 *
	 * @param bits
	 *            int containing the two bits encoding a QR Code's error correction
	 *            level
	 * @return ErrorCorrectionLevel representing the encoded error correction level
	 */
	public static ErrorCorrectionLevel forBits(int bits) {
		if (bits < 0 || bits >= FOR_BITS.length) {
			throw new IllegalArgumentException();
		}
		return FOR_BITS[bits];
	}
}
