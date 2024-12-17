package ch.swissqr.content.ch;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.swissqr.utils.Error;

/**
 * Interface to support different alternative schema implementations
 *
 * @author pschatzmann
 */
@JsonDeserialize(as = AlternativeSchema.class)
public interface IAlternativeSchema {
	/**
	 * The title which is printed on the Payment Slip
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getTitle();

	/**
	 * The content that is encoded in the bardoce and printed on the payment slip
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getContent();

	/**
	 * Validation
	 *
	 * @return list of errors
	 */
	public Collection<? extends Error> check();

}
