package net.twisterrob.test.junit;

import org.junit.Test;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.TestWithParameters;

import android.annotation.SuppressLint;

import androidx.test.internal.util.AndroidRunnerParams;

/**
 * This class is needs to merge two classes' logic together:<ul>
 * <li>{@link androidx.test.ext.junit.runners.AndroidJUnit4}<br>
 * which is an alias to {@link androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner}</li>
 * <li>{@link org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters}</li>
 * </ul>
 *
 * For this reason it extends one of them and contains an exact (formatted) copy of the other.
 *
 * To run Android tests with {@link org.junit.runners.Parameterized} runner use
 * <pre><code>
 * {@code @}RunWith(Parameterized.class)
 * {@code @}Parameterized.UseParametersRunnerFactory(AndroidJUnit4WithParametersRunnerFactory.class)
 * public class Test {}
 * </code></pre>
 * instead of
 * <pre><code>
 * {@code @}RunWith(AndroidJUnit4.class)
 * public class Test {}
 * </code></pre>
 * @see org.junit.runners.Parameterized
 * @see AndroidJUnit4WithParametersRunnerFactory
 */
public class AndroidJUnit4WithParameters extends BlockJUnit4ClassRunnerWithParameters {
	@SuppressLint("RestrictedApi") // Need to access internal API, because there's no public one.
	private final AndroidRunnerParams mAndroidRunnerParams;

	public AndroidJUnit4WithParameters(
			TestWithParameters test,
			@SuppressLint("RestrictedApi") // Need to access internal API, because there's no public one.
			AndroidRunnerParams runnerParams
	) throws InitializationError {
		super(test);
		mAndroidRunnerParams = runnerParams;
	}

	/**
	 * Default to <a href="http://junit.org/javadoc/latest/org/junit/Test.html">{@code Test}</a> level timeout if set.
	 * Otherwise, set the timeout that was passed to the instrumentation via argument.
	 */
	@SuppressLint("RestrictedApi")
	@SuppressWarnings("deprecation")
	@Override protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
		long timeout = getTimeout(method.getAnnotation(Test.class));
		if (timeout > 0) {
			return new FailOnTimeout(next, timeout);
		} else if (mAndroidRunnerParams.getPerTestTimeout() > 0) {
			return new FailOnTimeout(next, mAndroidRunnerParams.getPerTestTimeout());
		}
		return next;
	}

	private long getTimeout(Test annotation) {
		if (annotation == null) {
			return 0;
		}
		return annotation.timeout();
	}
}
