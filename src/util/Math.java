package util;

/**
 * Collection of utility methods for maths.
 * 
 * @author lvl2pillow
 *
 */
public class Math {
	public static int factorial(int n) {
		int factorial = 1;
		for (int i = 1; i <= n; ++i) {
			factorial *= i;
		}
		return factorial;
	}

}
