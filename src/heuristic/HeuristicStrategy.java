package heuristic;

import java.util.Map;

/**
 * The {@code HeuristicStrategy} interface allows interchangeability between 
 * multiple heuristic algorithms depending on the context.
 * </br>
 * 
 * @param <K> key
 * @param <V> value extends {@link Number}
 * @see <a href="http://en.wikipedia.org/wiki/Strategy_pattern">Strategy Pattern</a>.
 * @author lvl2pillow
 *
 */
public interface HeuristicStrategy<K, V extends Number> {
	/**
	 * 
	 * @return mapping of the heuristic.
	 */
	public Map<K, V> getHeuristic();
}
