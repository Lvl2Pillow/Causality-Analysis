package heuristic;

import java.util.Map;

/**
 * A HeuristicStrategy interface allows interchangeability between multiple
 * heuristic algorithms depending on the context.
 * </br>
 * See <a href="http://en.wikipedia.org/wiki/Strategy_pattern">Strategy Pattern</a>.
 * 
 * @param <K> key
 * @param <V> value
 * @author lvl2pillow
 *
 */
public interface HeuristicStrategy<K, V> {
	/**
	 * This method will trigger the computation of a heuristic.
	 * 
	 * @return mapping of the heuristic.
	 */
	public Map<K, V> getHeuristic();
}
