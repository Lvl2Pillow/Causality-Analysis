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
public class LiteralFrequency extends HashMap<Literal, Integer> 
implements HeuristicStrategy<Literal, Integer> {
	// auto-generated
	private static final long serialVersionUID = 2007516413283958859L;
	
	/**
	 * 
	 * @param term current term under construction.
	 * @param currentOnset
	 */
	public LiteralFrequency(Term term, TermList currentOnset) {
		super(literalFrequency(term, currentOnset));
	}
	
	/**
	 * 
	 * @param term current term under construction.
	 * @param currentOnset
	 * @return mapping of literals to their literal frequencies
	 */
	private static Map<Literal, Integer> literalFrequency(Term term, 
			TermList currentOnset) {
		// initialize
		Map<Literal, Integer> literalFrequency = new HashMap<Literal, Integer>();
		TermList uncoveredOnset = currentOnset.termsUncoveredBy(term);
		// TODO log if currentOnset is empty or remaining uncovered onset is empty
		for (Term minterm : uncoveredOnset) {
			for (Literal literal : minterm) {
				// new literal
				if (!literalFrequency.containsKey(literal))
					literalFrequency.put(literal, 0);
				// increment literal frequency
				literalFrequency.replace(literal, 
						literalFrequency.get(literal)+1);
			}
		}
		return literalFrequency;
	}
	
	@Override
	public Map<Literal, Integer> getHeuristic() {
		return this;
	}
	
}
