package io.soabase.client.retry;

public interface RetryHandler
{
    /**
     * Return true if the given arguments require a retry
     *
     * @param retryContext the retry context
     * @param retryCount 0 based retry count
     * @param statusCode the response status code or 0
     * @param exception any exception (might be null)
     */
    public boolean shouldBeRetried(RetryContext retryContext, int retryCount, int statusCode, Throwable exception);
}
