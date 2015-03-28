package core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Abstract class for Implicant Expansion.
 * 
 * @author lvl2pillow
 *
 */
public abstract class IE {
	protected final MintermList offset;
	protected final TermSet implicants;
	
	public IE(MintermList offset, TermSet implicants) {
		this.offset = offset;
		this.implicants = implicants;
	}
	
	/**
	 * Run the Implicant Expansion.
	 * 
	 * @return prime implicants.
	 */
	public TermSet run() {
		return getPrimeImplicants();
	}
	
	/**
	 * Returns all prime implicants found.
	 * 
	 * @return
	 */
	protected TermSet getPrimeImplicants() {
		TermSet primeImplicants = new TermSet();
		Queue<Term> implicantPermutations = new LinkedList<Term>();
		// initial permutations are the original implicants
		implicantPermutations.addAll(implicants);
		while (!implicantPermutations.isEmpty())
			getPrimeImplicantsHelper(primeImplicants, implicantPermutations,
					implicantPermutations.poll());
		return primeImplicants;
	}
	
	/**
	 * 
	 * @param primeImplicants all unique prime implicants found.
	 * @param implicantPermutations all permutations of the implicants.
	 * @param currentImplicant current permutation of the implicant.
	 */
	protected void getPrimeImplicantsHelper(TermSet primeImplicants,
			Queue<Term> implicantPermutations, Term currentImplicant) {
		// stop if there are no literal candidates to remove, i.e.
		// the current implicant can not be "expanded" further
		LiteralSet literalCandidates = getLiteralCandidates(currentImplicant);
		if (literalCandidates.isEmpty()) {
			primeImplicants.add(currentImplicant);
			return;
		}
		for (Literal literal : literalCandidates) {
			Term newImplicant = new Term(currentImplicant);
			newImplicant.remove(literal);
			implicantPermutations.add(newImplicant);
		}
	}
	
	/**
	 * Abstract method for getting set of literal candidates for removal.
	 * This method must be implemented in a concrete class.
	 * 
	 * @param currentImplicant
	 * @return set of literal candidates to remove from the
	 * 	current implicant.
	 */
	protected abstract LiteralSet getLiteralCandidates(Term currentImplicant);
	
	/**
	 * Checks if an implicant is a prime implicant. An implicant is a prime
	 * implicant if no literals can be removed without causing an intersection
	 * with the offset.
	 * 
	 * @param implicant
	 * @return
	 */
	protected boolean isPrimeImplicant(Term implicant) {
		boolean intersects = false;
		for (Minterm minterm : offset) {
			if (implicant.covers(minterm)) {
				intersects = true;
				break;
			}
		}
		return !intersects;
	}
	
	/**
	 * Returns the minimum Manhattan distance between a term and any minterm from
	 * the offset.
	 * 
	 * @param currentImplicant
	 * @return
	 */
	protected int minManhattanDistanceBetween(Term currentImplicant) {
		int minDist = Integer.MAX_VALUE;
		for (Minterm minterm : offset) {
			int dist = manhattanDistanceBetween(minterm, currentImplicant);
			minDist = (dist < minDist) ? dist : minDist;
		}
		return minDist;
	}
	
	/**
	 * Returns the Manhattan distance between a term and a minterm. The
	 * Manhattan distance is the number of differences between a term and a
	 * minterm.
	 * 
	 * @param minterm a minterm represented as a {@link Term}
	 * @param currentImplicant
	 * @return
	 */
	protected int manhattanDistanceBetween(Minterm minterm, Term currentImplicant) {
		int dist = 0;
		for (Literal literal : currentImplicant) {
			if (!minterm.contains(literal))
				++dist;
		}
		return dist;
	}
	
	/**
	 * 
	 * @return mapping of every literal with its manhattan distance heuristic
	 */
	protected Map<Literal, Integer> getManhattanDistanceHeuristic(Term currentImplicant) {
		Map<Literal, Integer> manhattanDistanceHeuristic = new HashMap<Literal, Integer>();
		// find the resulting min manhattan distance for each literal removal
		for (Literal literal : currentImplicant) {
			// remove the literal
			Term newTerm = new Term(currentImplicant);
			newTerm.remove(literal);
			// calculate min manhattan distance
			manhattanDistanceHeuristic.put(literal, minManhattanDistanceBetween(newTerm));
		}
		return manhattanDistanceHeuristic;
	}

}
