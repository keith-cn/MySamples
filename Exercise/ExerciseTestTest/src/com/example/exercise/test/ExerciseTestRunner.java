package com.example.exercise.test;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

public class ExerciseTestRunner extends InstrumentationTestRunner {

	@Override
	public ClassLoader getLoader() {
//		return super.getLoader();
		return ExerciseTestRunner.class.getClassLoader();
	}
	
	@Override
	public TestSuite getAllTests() {

        TestSuite suite = new InstrumentationTestSuite(this);
        suite.addTestSuite(MainActivityTest.class);
        return suite;
//		return super.getAllTests();
	}
}
