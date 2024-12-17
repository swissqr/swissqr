package ch.swissqr.content.ch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Payment reference section of swiss barcode payload
 *
 * @author pschatzmann
 */
public class PaymentReference {
	public enum ReferenceType {
		QRR, SCOR, NON
	}

	private ReferenceType referenceType = ReferenceType.NON;
	private String reference = "";
	private String unstructuredMessage = "";
	private String billInformation;

	/**
	 * <p>Constructor for PaymentReference.</p>
	 */
	public PaymentReference() {
	}

	/**
	 * <p>Constructor for PaymentReference.</p>
	 *
	 * @param msg a {@link java.lang.String} object
	 */
	public PaymentReference(String msg) {
		this.unstructuredMessage = msg;
	}

	/**
	 * <p>Constructor for PaymentReference.</p>
	 *
	 * @param referenceType a {@link ch.swissqr.content.ch.PaymentReference.ReferenceType} object
	 * @param ref a {@link java.lang.String} object
	 * @param msg a {@link java.lang.String} object
	 */
	public PaymentReference(ReferenceType referenceType, String ref, String msg) {
		this.referenceType = referenceType;
		this.reference = ref;
		this.unstructuredMessage = msg;
	}

	/**
	 * <p>Getter for the field <code>referenceType</code>.</p>
	 *
	 * @return a {@link ch.swissqr.content.ch.PaymentReference.ReferenceType} object
	 */
	public ReferenceType getReferenceType() {
		return referenceType;
	}

	/**
	 * <p>referenceType.</p>
	 *
	 * @param referenceType a {@link ch.swissqr.content.ch.PaymentReference.ReferenceType} object
	 * @return a {@link ch.swissqr.content.ch.PaymentReference} object
	 */
	public PaymentReference referenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
		return this;
	}

	/**
	 * <p>Getter for the field <code>reference</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * <p>reference.</p>
	 *
	 * @param reference a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.PaymentReference} object
	 */
	public PaymentReference reference(String reference) {
		this.reference = reference;
		return this;
	}

	/**
	 * <p>Getter for the field <code>unstructuredMessage</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getUnstructuredMessage() {
		return unstructuredMessage;
	}

	/**
	 * <p>unstructuredMessage.</p>
	 *
	 * @param unstructuredMessage a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.PaymentReference} object
	 */
	public PaymentReference unstructuredMessage(String unstructuredMessage) {
		this.unstructuredMessage = unstructuredMessage;
		return this;
	}

	/**
	 * <p>check.</p>
	 *
	 * @return a {@link java.util.List} object
	 */
	public List<Error> check() {
		List<Error> result = new ArrayList();
		StringUtils.check("referenceType", true, Arrays.asList("QRR", "SCOR", "NON"), referenceType.name(), result);
		StringUtils.check("unstructuredMessage", false, 140, reference, result);
		if (this.referenceType != ReferenceType.NON) {
			StringUtils.check("reference", true, 27, reference, result);
		} else {
			StringUtils.check("reference", false, 27, reference, result);
		}
		return result;
	}

	/**
	 * <p>Getter for the field <code>billInformation</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getBillInformation() {
		return billInformation;
	}

	/**
	 * <p>billInformation.</p>
	 *
	 * @param billInformation a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.PaymentReference} object
	 */
	public PaymentReference billInformation(String billInformation) {
		this.billInformation = billInformation;
		return this;
	}

	/**
	 * End Payment Data)
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getTrailer() {
		return "EPD";
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.referenceType);
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.str(this.reference));
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.str(this.unstructuredMessage));
		sb.append(StringUtils.CRLF);
		sb.append(this.getTrailer());
		if (hasBillInformation()){
			sb.append(StringUtils.CRLF);
			sb.append(toString(this.getBillInformation()));			
		}
		return sb.toString();
	}
	
	/**
	 * <p>hasBillInformation.</p>
	 *
	 * @return a {@link java.lang.Boolean} object
	 */
	public Boolean hasBillInformation() {
		return !StringUtils.isEmpty(this.getBillInformation());
	}

	private String toString(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	/**
	 * <p>toStringExt.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toStringExt() {
		StringBuffer sb = new StringBuffer();
		sb.append("ReferenceType: ");
		sb.append(this.referenceType);
		sb.append(StringUtils.CRLF);
		sb.append("Reference: ");
		sb.append(StringUtils.str(this.reference));
		sb.append(StringUtils.CRLF);
		sb.append("UnstructuredMessage: ");
		sb.append(StringUtils.str(this.unstructuredMessage));
		sb.append(StringUtils.CRLF);
		return sb.toString();
	}

	/**
	 * Defines how the reference number should be printed / grouped.
	 *
	 * @return a int
	 */
	protected int getGroupingLength() {
		switch (this.getReferenceType()) {
		case QRR:
			return 5;
		case SCOR:
			return 4;
		default:
			return 0;
		}
	}

	/**
	 * Defines if the remaining numbers are left or right
	 *
	 * @return a boolean
	 */
	protected boolean isGroupingLeft() {
		switch (this.getReferenceType()) {
		case QRR:
			return false;
		default:
			return true;
		}
	}

	/**
	 * Returns the reference number formatted for printing with the relevant
	 * groupings.
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getReferenceFormatted() {
		String result = "";
		if (getReferenceType() != ReferenceType.NON) {
			String ref = this.getReference();
			ref = ref.replaceAll(" ", "");
			if (isGroupingLeft()) {
				result = StringUtils.formatInGroups(getGroupingLength(), ref);
			} else {
				result = StringUtils.formatInGroupsRight(getGroupingLength(), ref);
			}
		}
		return result;
	}

}
