package com.hospital;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Test Runner - Execute all unit tests and display results
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("           HOSPITAL MANAGEMENT SYSTEM - UNIT TEST EXECUTION");
        System.out.println("================================================================================\n");

        Result result = JUnitCore.runClasses(AllTests.class);

        System.out.println("\n================================================================================");
        System.out.println("                           TEST RESULTS SUMMARY");
        System.out.println("================================================================================");
        System.out.println("Total tests run:     " + result.getRunCount());
        System.out.println("Tests passed:        " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Tests failed:        " + result.getFailureCount());
        System.out.println("Tests ignored:       " + result.getIgnoreCount());
        System.out.println("Time taken:          " + result.getRunTime() + "ms");
        System.out.println("================================================================================");

        if (result.getFailureCount() > 0) {
            System.out.println("\n                              FAILURES");
            System.out.println("================================================================================");
            for (Failure failure : result.getFailures()) {
                System.out.println("\n" + failure.getTestHeader());
                System.out.println(failure.getTrace());
            }
        }

        if (result.wasSuccessful()) {
            System.out.println("\n✅ ALL TESTS PASSED!");
            System.out.println("================================================================================\n");
            System.exit(0);
        } else {
            System.out.println("\n❌ SOME TESTS FAILED!");
            System.out.println("================================================================================\n");
            System.exit(1);
        }
    }
}
