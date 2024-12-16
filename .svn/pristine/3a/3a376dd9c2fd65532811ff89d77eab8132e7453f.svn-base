package ch.swissqr.content.ch;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.swissqr.utils.StringUtils;

/**
 * Payment amout section of swiss barcode payload
 * 
 * @author pschatzmann
 *
 */

public class PaymentAmount {
 	private BigDecimal amount = null;
 	private String currency="CHF";
 	private Date dueDate=null;
	private NumberFormat format = DecimalFormat.getInstance(new Locale("de_CH"));
	private DecimalFormat printFormat = new DecimalFormat();

	public PaymentAmount() {
		format.setGroupingUsed(false);
		format.setMinimumFractionDigits(2);

		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');
		printFormat.setGroupingUsed(true);
		printFormat.setDecimalFormatSymbols(symbols);
		printFormat.setMinimumFractionDigits(2);
	}

	public PaymentAmount(BigDecimal amount,String currency,Date dueDate) {
		this();
		this.amount = amount;
		this.currency = currency;
		this.dueDate = dueDate;
	}

	public PaymentAmount(BigDecimal amount) {
		this();
		this.amount = amount;
	}
	
	public PaymentAmount(double amount) {
		this();
		this.amount = BigDecimal.valueOf(amount);
	}

	
	public BigDecimal getAmount() {
		return amount;
	}
	
	/**
	 * String format in SwissQR message
	 * @return
	 */
	@JsonIgnore
	public String getAmountStr() {
		return amount == null ? "" : format.format(amount);
	}

	/**
	 * String format as printed on payment slip
	 * @return
	 */
	@JsonIgnore
	public String getAmountPrinted() {
		return amount == null ? "" : printFormat.format(amount);
	}


	public PaymentAmount amount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}
	
	public String getCurrency() {
		return currency;
	}
	public PaymentAmount currency(String currency) {
		this.currency = StringUtils.isEmpty(currency) ? "CHF" : currency;
		return this;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public PaymentAmount dueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}
	
	private String getDateString() {
		String result = "";
		if (dueDate!=null) {
			result = new SimpleDateFormat("yyyy-MM-dd").format(dueDate);
		}
		return result;
	}
	
	public List<String> check() {
		List result =  new ArrayList();
		StringUtils.check("amount", false, 12, getAmountStr(), result);
		StringUtils.check("currency", false, 3, currency, result);
		StringUtils.check("currency", true, Arrays.asList("CHF", "EUR"), currency, result);
		StringUtils.check("dueDate", false, 10, currency, result);		
		return result;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getAmountStr());
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.str(this.getCurrency()));
		//Removed with version 2
		//sb.append(StringUtils.CRLF);
		//sb.append(getDateString());
		sb.append(StringUtils.CRLF);
		return sb.toString();
	}

	public String toStringExt() {
		StringBuffer sb = new StringBuffer();
		sb.append("Amount: ");
		sb.append(getAmountStr());
		sb.append(StringUtils.CRLF);
		sb.append("Currency: ");
		sb.append(StringUtils.str(this.getCurrency()));
		sb.append(StringUtils.CRLF);
		sb.append("Date: ");
		sb.append(getDateString());
		sb.append(StringUtils.CRLF);
		return sb.toString();
	}

}
