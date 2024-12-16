package ch.swissqr.service.web;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import ch.swissqr.errors.ErrorInformation;
import ch.swissqr.errors.LicenceError;

/**
 * REST ExceptionMapper which provides the Exception information as json to the
 * caller
 * 
 * @author pschatzmann
 *
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
	private static Logger LOG = Logger.getLogger(GenericExceptionMapper.class);

	public GenericExceptionMapper() {
	}

	@Override
	public Response toResponse(Throwable ex) {
		LOG.error(ex, ex);
		ErrorInformation ei = new ErrorInformation(400, ex.getMessage());
		if (ex instanceof LicenceError) {
			ei = new ErrorInformation(401, ex.getMessage());
		}
		return Response.status(ei.getStatus()).entity(ei).type(MediaType.APPLICATION_JSON).build();
	}

}
