package heuristic;

import java.util.HashMap;
import java.util.Map;

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
 * This class represents the Least Covered component.
 * </p>
 * 
 * @author lvl2pillow
 *
 */
public class LeastCovered extends HashMap<Term, Integer> 
implements HeuristicStrategy<Term, Integer> {
	// auto-generated
	private static final long serialVersionUID = -985806684910244618L;
	
	/**
	 * 
	 * @param coverMatrix rows are minterms, columns are prime implicants.
	 * @param primeImplicants list of prime implicants.
	 */
	public LeastCovered(boolean[][] coverMatrix, TermList primeImplicants) {
		super(getLeastCovered(coverMatrix, primeImplicants));
	}
	
	/**
	 * 
	 * @param coverMatrix
	 * @return mapping of prime implicants to count of minterms only covered by
	 * 	the prime implicant.
	 */
	public static Map<Term, Integer> getLeastCovered(boolean[][] coverMatrix, TermList primeImplicants) {
		Map<Term, Integer> leastCovered = new HashMap<Term, Integer>();
		// initialize
		for (Term primeImplicant : primeImplicants)
			leastCovered.put(primeImplicant, 0);
		// count of minterms only covered by the prime implicant
		for (int i = 0; i < coverMatrix.length; ++i) {
			int count = 0;
			int index = 0;
			for (int j = 0; j < coverMatrix[0].length; ++j) {
				if (coverMatrix[i][j]) {
					++count;
					index = j;
				}
			}
			if (count == 1) {	// only covered by the prime implicant
				Term primeImplicant = primeImplicants.get(index);
				leastCovered.replace(primeImplicant, leastCovered.get(primeImplicant)+1);
			}
		}
		return leastCovered;
	}
	
	@Override
	public Map<Term, Integer> getHeuristic() { return this; }
	
}
