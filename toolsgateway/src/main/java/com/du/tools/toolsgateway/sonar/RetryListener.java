package com.du.tools.toolsgateway.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Component
class RetryListener extends RetryListenerSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(RetryListenerSupport.class);
	private int retryCount;

	@Override
	public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {

		LOGGER.info("Unable to recover from  Exception");
		super.close(context, callback, throwable);
	}

	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
		setRetryCount(context.getRetryCount());
		LOGGER.info("Exception Occurred, Retry Count {} " + context.getRetryCount());
		super.onError(context, callback, throwable);
	}

	@Override
	public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
		LOGGER.info("Exception Occurred, Retry Session Started ");
		return super.open(context, callback);
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
}