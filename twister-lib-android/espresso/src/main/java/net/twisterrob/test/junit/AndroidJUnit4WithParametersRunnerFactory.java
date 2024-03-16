package net.twisterrob.test.junit;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

import android.annotation.SuppressLint;
import android.app.Instrumentation;

import androidx.test.internal.runner.RunnerArgs;
import androidx.test.internal.util.AndroidRunnerParams;
import androidx.test.platform.app.InstrumentationRegistry;

import net.twisterrob.java.utils.ReflectionTools;

/**
 * @see AndroidJUnit4WithParameters
 */
public class AndroidJUnit4WithParametersRunnerFactory implements ParametersRunnerFactory {
	public Runner createRunnerForTestWithParameters(TestWithParameters test)
			throws InitializationError {
		Instrumentation instr = InstrumentationRegistry.getInstrumentation();
		@SuppressLint("RestrictedApi") // Need to access internal API, because there's no public one.
		RunnerArgs runnerArgs = ReflectionTools.get(instr, "mRunnerArgs");

		@SuppressLint("RestrictedApi") // Need to access internal API, because there's no public one.
		@SuppressWarnings("deprecation") // will probably revisit soon
		AndroidRunnerParams runnerParams = new AndroidRunnerParams(
				instr,
				InstrumentationRegistry.getArguments(),
				runnerArgs != null && runnerArgs.logOnly,
				runnerArgs != null? runnerArgs.testTimeout : 0,
				true
		);

		return new AndroidJUnit4WithParameters(test, runnerParams);
	}
}
