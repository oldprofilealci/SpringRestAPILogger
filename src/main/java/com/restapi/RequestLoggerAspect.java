package com.restapi;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
public class RequestLoggerAspect {

	private Logger logger = LoggerFactory.getLogger(RequestLoggerAspect.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Around("execution(@RequestLogger * *(..)) && @annotation(requestLogger)")
	public Object logRequest(ProceedingJoinPoint joinPoint, RequestLogger requestLogger) throws Throwable {

		ContentCachingRequestWrapper request = getWrapper(joinPoint);

		StringBuilder apiLog = new StringBuilder();

		apiLog.append("Rest API: ").append(request.getRequestURL().toString()).append("\n");

		apiLog.append("Body:").append(getRequestBody(request)).append("\n");

		for (String header : Collections.list(request.getHeaderNames())) {
			apiLog.append(header).append(":").append(request.getHeader(header))
					.append("\n");
		}

		logger.debug(apiLog.toString());

		Object retVal = joinPoint.proceed();

		logger.debug("Response:" + objectMapper.writeValueAsString(retVal));

		return retVal;

	}

	private String getRequestBody(final ContentCachingRequestWrapper wrapper) {
		String payload = null;
		if (wrapper != null) {

			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				try {
					int maxLength = buf.length > 500 ? 500 : buf.length;

					payload = new String(buf, 0, maxLength,
							wrapper.getCharacterEncoding());
				} catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncoding.", e);
				}
			}
		}
		return payload;
	}

	private ContentCachingRequestWrapper getWrapper(ProceedingJoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		ContentCachingRequestWrapper request = null;

		for (Object arg : args) {
			if (arg instanceof ContentCachingRequestWrapper) {
				request = (ContentCachingRequestWrapper) arg;
				break;
			}
		}

		return request;
	}

}