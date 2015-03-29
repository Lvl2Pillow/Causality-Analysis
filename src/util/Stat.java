package util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Collection of utility methods for statistics.
 * 
 * @author lvl2pillow
 *
 */
public class Stat {
	/**
	 * Feature normalization (between 0 and 1).
	 * 
	 * @param numbers collection of numbers to normalize.
	 * @return collection of normalized numbers.
	 */
	public static Collection<Number> normalize(Collection<Number> numbers) {
		Collection<Number> normalizedNumbers = new ArrayList<Number>();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		// find min and max numbers
		for (Number n : numbers) {
			double d = n.doubleValue();
			min = (d < min) ? d : min;
			max = (d > max) ? d : max;
		}
		// normalize numbers
		for (Number n : numbers) {
			normalizedNumbers.add(normalize(n.doubleValue(), min, max));
		}
		return normalizedNumbers;
	}
	
	/**
	 * Normalize a value (between 0 and 1), given min and max values.
	 * 
	 * @param n value to normalize.
	 * @param min
	 * @param max
	 * @return normalized value.
	 */
	public static double normalize(double n, double min, double max) {
		return (n-min)/(max-min);
	}
}
