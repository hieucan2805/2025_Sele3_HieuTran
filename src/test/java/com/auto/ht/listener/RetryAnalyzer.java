package com.auto.ht.listener;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    public final static int maxRetryCount = Integer.parseInt(System.getProperty("max.retry.count"));
    @Override
    public boolean retry(ITestResult iTestResult) {
        if (retryCount < maxRetryCount){
            retryCount ++;
            return true;
        }
        return false;
    }
}
