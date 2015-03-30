package heuristic;

/**
 * A HeuristicStrategy interface allows interchangeability between multiple
 * heuristic algorithms depending on the context.
 * </br>
 * See <a href="http://en.wikipedia.org/wiki/Strategy_pattern">Strategy Pattern</a>.
 * 
 * @author lvl2pillow
 *
 */
public interface HeuristicStrategy {
	/**
	 * This method will trigger the computation of a heuristic value for 
	 * heuristics that require dynamic inputs.
	 * 
	 * @return value of the heuristic.
	 */
	public double getValue();
}
