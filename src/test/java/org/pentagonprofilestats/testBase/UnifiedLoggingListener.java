package org.pentagonprofilestats.testBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.*;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unified TestNG listener with rich, structured logging:
 * - Suite/Test/Class/Method lifecycle
 * - Parameters, duration, status (PASS/FAIL/SKIP)
 * - Throwable message + stack
 * - MDC tagging for testName and className (use in Logback/Log4j2 pattern)
 *
 * Wire via testng.xml <listeners> or @Listeners on a base class.
 */
public class UnifiedLoggingListener implements IExecutionListener, ISuiteListener,
        ITestListener, IInvokedMethodListener {

    private static final Logger log = LoggerFactory.getLogger(UnifiedLoggingListener.class);

    // Track start times for duration calculation
    private final Map<String, Long> startTimes = new ConcurrentHashMap<>();

    // -------------------- Execution (whole run) --------------------
    @Override
    public void onExecutionStart() {
        log.info("🚀 TEST RUN START");
    }

    @Override
    public void onExecutionFinish() {
        log.info("🏁 TEST RUN FINISH");
    }

    // -------------------- Suite --------------------
    @Override
    public void onStart(ISuite suite) {
        log.info("📦 SUITE START: {}", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("📦 SUITE FINISH: {}", suite.getName());
    }

    // -------------------- Per Test (from testng.xml <test>) --------------------
    @Override
    public void onStart(ITestContext context) {
        log.info("🧪 TEST BLOCK START: {} | XmlTestName='{}' | IncludedGroups={} | ExcludedGroups={}",
                context.getName(),
                context.getCurrentXmlTest() != null ? context.getCurrentXmlTest().getName() : "N/A",
                safeGroups(context.getIncludedGroups()),
                safeGroups(context.getExcludedGroups()));
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("🧪 TEST BLOCK FINISH: {} | Passed={} | Failed={} | Skipped={}",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    // -------------------- Test methods (per invocation) --------------------
    @Override
    public void onTestStart(ITestResult result) {
        String methodKey = methodKey(result);
        startTimes.put(methodKey, System.currentTimeMillis());

        MDC.put("testName", result.getMethod().getMethodName());
        MDC.put("className", result.getTestClass().getName());

        log.info("▶ START {}.{}({})",
                result.getTestClass().getName(),
                result.getMethod().getMethodName(),
                safeParams(result.getParameters()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        try {
            long durationMs = duration(result);
            log.info("✓ PASS {}.{} | duration={} ms",
                    result.getTestClass().getName(),
                    result.getMethod().getMethodName(),
                    durationMs);
        } finally {
            clearMdc();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            long durationMs = duration(result);
            Throwable t = result.getThrowable();
            // SLF4J: last argument as Throwable is treated as stacktrace
            log.error("✗ FAIL {}.{} | duration={} ms | message={}",
                    result.getTestClass().getName(),
                    result.getMethod().getMethodName(),
                    durationMs,
                    (t != null ? t.getMessage() : "no message"),
                    t);
        } finally {
            clearMdc();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        try {
            long durationMs = duration(result);
            Throwable t = result.getThrowable();
            log.warn("⧗ SKIP {}.{} | duration={} ms | reason={}",
                    result.getTestClass().getName(),
                    result.getMethod().getMethodName(),
                    durationMs,
                    (t != null ? t.getMessage() : "no reason"));
        } finally {
            clearMdc();
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        try {
            long durationMs = duration(result);
            log.warn("⚠ Within success percentage {}.{} | duration={} ms",
                    result.getTestClass().getName(),
                    result.getMethod().getMethodName(),
                    durationMs);
        } finally {
            clearMdc();
        }
    }

    // -------------------- Invocation (before/after each method, incl. config methods) --------------------
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            log.debug("➤ BEFORE INVOCATION {}.{}",
                    testResult.getTestClass().getName(),
                    testResult.getMethod().getMethodName());
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            log.debug("↩ AFTER INVOCATION {}.{} (status={})",
                    testResult.getTestClass().getName(),
                    testResult.getMethod().getMethodName(),
                    statusName(testResult.getStatus()));
        }
    }

    // -------------------- Helpers --------------------
    private static String safeParams(Object[] params) {
        return Arrays.toString(params != null ? params : new Object[]{});
    }

    private static String safeGroups(String[] groups) {
        return Arrays.toString(groups != null ? groups : new String[]{});
    }

    private String methodKey(ITestResult result) {
        return result.getTestClass().getName() + "#" + result.getMethod().getMethodName()
                + safeParams(result.getParameters());
    }

    private long duration(ITestResult result) {
        Long start = startTimes.remove(methodKey(result));
        long end = System.currentTimeMillis();
        if (start != null) {
            return end - start;
        }
        long startMillis = result.getStartMillis();
        return (startMillis > 0) ? (end - startMillis) : 0L;
    }

    private String statusName(int status) {
        return switch (status) {
            case ITestResult.SUCCESS -> "SUCCESS";
            case ITestResult.FAILURE -> "FAILURE";
            case ITestResult.SKIP -> "SKIP";
            default -> "UNKNOWN";
        };
    }

    private void clearMdc() {
        MDC.remove("testName");
        MDC.remove("className");
    }
}
