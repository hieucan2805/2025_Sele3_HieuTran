package com.auto.ht.listener;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.logging.Logger;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = Logger.getLogger(RetryAnalyzer.class.getName());
    private int retryCount = 0;
    public final static int maxRetryCount = Integer.parseInt(System.getProperty("max.retry.count"));

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (retryCount < maxRetryCount){
            retryCount ++;
            logger.info(String.format("Retrying test %s with status %s for the %d time(s).%n",
                iTestResult.getName(), getResultStatusName(iTestResult.getStatus()), retryCount));
            return true;
        }
        return false;
    }

    public String getResultStatusName(int status) {
        String resultName = null;
        if (status == 1)
            resultName = "SUCCESS";
        if (status == 2)
            resultName = "FAILURE";
        if (status == 3)
            resultName = "SKIP";
        return resultName;
    }

    private static int getMaxRetryCount() {
        String retryCountStr = System.getenv("MAX_RETRY_COUNT");
        if (retryCountStr == null) {
            retryCountStr = System.getProperty("maxRetryCount", "3");
        }
        return Integer.parseInt(retryCountStr);
    }
}
