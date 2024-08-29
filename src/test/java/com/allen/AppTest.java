package com.allen;

import com.allen.agent.MyAgentMainAgent;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Vector;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    public void testGetClasses() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        while (classLoader != null) {
            System.out.println("ClassLoader -> " + classLoader.getClass());
            System.out.println("----------------------------------------");
            Vector<Class> classes = MyAgentMainAgent.getLoadedClasses(classLoader);

            for (Class cls : classes) {
                System.out.println("class => " + cls.getClass());
                System.out.println(cls.getName());
            }

            classLoader = classLoader.getParent();
        }
    }
}
