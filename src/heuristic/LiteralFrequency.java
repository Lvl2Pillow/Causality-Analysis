package heuristic;

import java.util.HashMap;
import java.util.Map;

import core.Literal;
import core.Term;
import core.TermList;

/**
 * The literal frequency of a literal is the number of occurrences of a literal
 * from all minterms of the remaining uncovered onset.
 * 
 * @author lvl2pillow
 *
 */
public class LiteralFrequency implements HeuristicStrategy<Literal, Integer> {
	private final Term term;
	private final TermList onset;
	
	/**
	 * 
	 * @param term current term under construction.
	 * @param onset
	 */
	public LiteralFrequency(Term term, TermList onset) {
		this.term = term;
		this.onset = onset;
	}
	
	@Override
	public Map<Literal, Integer> getHeuristic() {
		return getLiteralFrequencyHeuristic(term, onset);
	}
	
	/**
	 * 
	 * @param term current term under construction.
	 * @param currentOnset
	 * @return literal frequency
	 */
	protected Map<Literal, Integer> getLiteralFrequencyHeuristic(Term term, 
			TermList currentOnset) {
		Map<Literal, Integer> literalFrequencyHeuristic = new HashMap<Literal, Integer>();
		TermList uncoveredOnset = currentOnset.getUncoveredBy(term);
		// TODO log if currentOnset is empty or remaining uncovered onset is empty
		for (Term minterm : uncoveredOnset) {
			for (Literal literal : minterm) {
				// new literal
				if (!literalFrequencyHeuristic.containsKey(literal))
					literalFrequencyHeuristic.put(literal, 0);
				// increment literal frequency
				literalFrequencyHeuristic.replace(literal, 
						literalFrequencyHeuristic.get(literal)+1);
			}
		}
		return literalFrequencyHeuristic;
	}
	
}
