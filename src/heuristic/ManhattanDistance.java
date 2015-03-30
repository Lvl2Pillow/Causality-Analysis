package heuristic;

import java.util.HashMap;
import java.util.Map;

import core.Literal;
import core.Term;
import core.TermList;

/**
 * The Manhattan distance of a literal is defined as the minimum Manhattan 
 * distance between the resulting term and any minterm from the offset.
 * 
 * @author lvl2pillow
 *
 */
public class ManhattanDistance implements HeuristicStrategy<Literal, Integer> {
	private final Term term;
	private final TermList offset;
	
	/**
	 * 
	 * @param term
	 * @param offset
	 */
	public ManhattanDistance(Term term, TermList offset) {
		this.term = term;
		this.offset = offset;
	}	
	
	@Override
	public Map<Literal, Integer> getHeuristic() {
		return getManhattanDistanceHeuristic(term, offset);
	}
	
	/**
	 * 
	 * @return mapping of every literal with its manhattan distance
	 */
	private Map<Literal, Integer> getManhattanDistanceHeuristic(Term term, TermList offset) {
		Map<Literal, Integer> manhattanDistanceHeuristic = new HashMap<Literal, Integer>();
		// find the resulting min manhattan distance for each literal removal
		for (Literal literal : term) {
			// remove the literal
			Term newTerm = new Term(term);
			newTerm.remove(literal);
			// calculate min manhattan distance
			manhattanDistanceHeuristic.put(literal, minManhattanDistanceBetween(newTerm, offset));
		}
		return manhattanDistanceHeuristic;
	}
	
	/**
	 * Returns the minimum Manhattan distance between a term and any minterm 
	 * from the offset.
	 * 
	 * @param term
	 * @return
	 */
	private int minManhattanDistanceBetween(Term term, TermList offset) {
		int minDist = Integer.MAX_VALUE;
		for (Term minterm : offset) {
			int dist = manhattanDistanceBetween(minterm, term);
			minDist = (dist < minDist) ? dist : minDist;
		}
		return minDist;
	}
	
	/**
	 * Returns the Manhattan distance between a term and a minterm. The
	 * Manhattan distance is essentially the number of differences between a 
	 * term and a minterm.
	 * 
	 * @param term
	 * @param minterm a minterm from the offset.
	 * @return
	 */
	protected int manhattanDistanceBetween(Term term, Term minterm) {
		int dist = 0;
		for (Literal literal : term) {
			if (!minterm.contains(literal))
				++dist;
		}
		return dist;
	}
	
}
