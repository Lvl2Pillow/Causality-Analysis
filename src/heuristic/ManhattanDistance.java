package heuristic;

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
public class ManhattanDistance implements HeuristicStrategy {
	private final Term term;
	private final TermList offset;
	
	/**
	 * 
	 * @param term resulting term after literal removal.
	 * @param offset
	 */
	public ManhattanDistance(Term term, TermList offset) {
		this.term = term;
		this.offset = offset;
	}
	
	@Override
	public double getValue() {
		return (double)minManhattanDistanceBetween(term, offset);
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
