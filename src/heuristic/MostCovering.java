package heuristic;

import java.util.*;

import core.Term;
import core.TermList;

/**
 * <h4>Least Covered, Most Covering heuristic (Fiser & Hlavicka, 2002).</h4>
 * <p>
 * Choose prime implicants covering the most minterms that are not covered by 
 * other prime implicants. If there is a tie, select the prime implicant 
 * covering the most minterms not yet covered.
 * </p>
 * <p>
 * This class contains the Most Covering component.
 * </p>
 * 
 * @author lvl2pillow
 *
 */
public class MostCovering implements HeuristicStrategy<Term, Integer> {
	private Map<Term, Integer> termCovering;
	
	public MostCovering(boolean[][] coverMatrix, TermList primeImplicants) {
		this.termCovering = mostCovering(coverMatrix, primeImplicants);
	}
	
	private Map<Term, Integer> mostCovering(boolean[][] coverMatrix, 
			TermList primeImplicants) {
		Map<Term, Integer> termCovering = new HashMap<Term, Integer>();
		for (int j = 0; j < coverMatrix[0].length; ++j) {	// each prime implicant
			Term primeImplicant = primeImplicants.get(j);
			termCovering.put(primeImplicant, 0);
			for (int i = 0; i < coverMatrix.length; ++i) {	// each minterm
				if (coverMatrix[i][j])
					termCovering.put(primeImplicant, termCovering.get(primeImplicant)+1);
			}
		}
		return termCovering;
	}
	
	@Override
	public Map<Term, Integer> getHeuristic() {
		return this.termCovering;
	}
	
}
