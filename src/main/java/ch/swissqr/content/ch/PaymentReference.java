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
 *
 */
public class PaymentReference {
	public enum ReferenceType {
		QRR, SCOR, NON
	}

	private ReferenceType referenceType = ReferenceType.NON;
	private String reference = "";
	private String unstructuredMessage = "";
	private String billInformation;

	public PaymentReference() {
	}

	public PaymentReference(String msg) {
		this.unstructuredMessage = msg;
	}

	public PaymentReference(ReferenceType referenceType, String ref, String msg) {
		this.referenceType = referenceType;
		this.reference = ref;
		this.unstructuredMessage = msg;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public PaymentReference referenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public PaymentReference reference(String reference) {
		this.reference = reference;
		return this;
	}

	public String getUnstructuredMessage() {
		return unstructuredMessage;
	}

	public PaymentReference unstructuredMessage(String unstructuredMessage) {
		this.unstructuredMessage = unstructuredMessage;
		return this;
	}

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

	public String getBillInformation() {
		return billInformation;
	}

	public PaymentReference billInformation(String billInformation) {
		this.billInformation = billInformation;
		return this;
	}

	/**
	 * End Payment Data)
	 * 
	 * @return
	 */
	public String getTrailer() {
		return "EPD";
	}

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
	
	public Boolean hasBillInformation() {
		return !StringUtils.isEmpty(this.getBillInformation());
	}

	private String toString(Object obj) {
		return obj == null ? "" : obj.toString();
	}

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
	 * @return
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
	 * @return
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
	 * @return
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
