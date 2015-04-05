package heuristic;

import java.util.ArrayList;
import java.util.List;

import core.Literal;

/**
 * A {@code HeuristicValues} object stores multiple heuristic values in order 
 * of priority.
 * 
 * @author lvl2pillow
 * @deprecated adds more complexity
 */
public class HeuristicValues 
extends ArrayList<Double> implements Comparable<HeuristicValues> {
	// auto-generated
	private static final long serialVersionUID = 686652116771360263L;

	public HeuristicValues() {
		super();
	}
	
	/**
	 * Initialize with values from heuristic mappings.
	 * 
	 * @param literal literal of interest.
	 * @param strategies list of heuristic mappings.
	 */
	public HeuristicValues(Literal literal, 
			List<HeuristicStrategy<Literal, Number>> strategies) {
		super();
		for (HeuristicStrategy<Literal, Number> strategy : strategies)
			this.add(strategy.getHeuristic().get(literal).doubleValue());
	}
	
	@Override
	public int compareTo(HeuristicValues values) {
		for (int i = 0; i < this.size(); ++i) {
			if (values.size() < i+1) // specified HeuristicValue has less elements	
				return 1;
			int compare;
			if ((compare = this.get(i).compareTo(values.get(i))) != 0)
				return compare;
			// compare next heuristic value
		}
		// 0 if same number of elements, else -1
		return (this.size() == values.size()) ? 0 : -1;
	}

}
