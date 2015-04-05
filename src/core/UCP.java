package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Abstract class for Unate Covering Problem.
 * 
 * @author lvl2pillow
 *
 */
public abstract class UCP {
	private final TermList onset;
	private final TermSet primeImplicants;
	
	public UCP(TermList onset, TermSet primeImplicants) {
		this.onset = onset;
		this.primeImplicants = primeImplicants;
	}
	
	/**
	 * Returns set of essential prime implicants.
	 * 
	 * @return
	 */
	public TermSet run() {
		return essentialPrimeImplicants();
	}
	
	/**
	 * 
	 * @return set of essential prime implicants that wholly covers the onset.
	 */
	protected TermSet essentialPrimeImplicants() {
		TermSet essentialPrimeImplicants = new TermSet();
		Map<Term, Set<Term>> coverMatrix = getCoverMatrix();
		while (!coverMatrix.isEmpty()) {
			essentialPrimeImplicantsHelper(essentialPrimeImplicants, coverMatrix);
		}
		return essentialPrimeImplicants;
	}
	
	/**
	 * Abstract method for finding essential prime implicants.
	 * This method must be implemented in a concrete class.
	 */
	protected abstract void essentialPrimeImplicantsHelper(TermSet essentialPrimeImplicants, 
			Map<Term, Set<Term>> coverMatrix);
	
	/**
	 * A cover matrix shows which minterms are covered by which prime implicants.
	 * </br>
	 * (key = minterm : value = set of prime implicants that covers the minterm)
	 * 
	 * @return cover matrix.
	 */
	protected Map<Term, Set<Term>> getCoverMatrix() {
		Map<Term, Set<Term>> coverMatrix = new HashMap<Term, Set<Term>>();
		for (Term minterm : onset) {
			// set of prime implicants that covers the minterm
			Set<Term> primeImplicants = new HashSet<Term>();
			for (Term primeImplicant : this.primeImplicants) {
				if (primeImplicant.covers(minterm))
					primeImplicants.add(primeImplicant);
			}
			coverMatrix.put(minterm, primeImplicants);
		}
		return coverMatrix;
	}
	
	/**
	 * 
	 * @return map of prime implicants with count of minterms that only they cover.
	 */
	protected Map<Term, Integer> getLeastCoveredHeuristic() {
		Map<Term, Integer> leastCoveredHeuristic = new HashMap<Term, Integer>();
		Map<Term, Set<Term>> coverMatrix = getCoverMatrix();
		for (Entry<Term, Set<Term>> entry : coverMatrix.entrySet()) {
			Set<Term> primeImplicants = entry.getValue();
			// only prime implicant that covers the minterm
			if (primeImplicants.size() == 1) {
				Term primeImplicant = null;
				// get first and only prime implicant
				for (Term term : primeImplicants) {
					primeImplicant = term;
					break;
				}
				// TODO log if primeImplicant == null
				if (!leastCoveredHeuristic.containsKey(primeImplicant))
					leastCoveredHeuristic.put(primeImplicant, 0);
				// increment count
				leastCoveredHeuristic.replace(primeImplicant, 
						leastCoveredHeuristic.get(primeImplicant)+1);
			}
		}
		return leastCoveredHeuristic;
	}
	
	/**
	 * NOTE: This method is very inefficient currently. If you save the uncovered
	 * minterms as you find more essential prime implicants, it will save some
	 * unnecessary processes.
	 * 
	 * @param essentialPrimeImplicants set of essential prime implicants found.
	 * @return map of prime implicants with count of minterms that they cover
	 * 	but are not covered yet.
	 */
	protected Map<Term, Integer> getMostCoveredHeuristic(TermSet essentialPrimeImplicants) {
		Map<Term, Integer> mostCoveredHeuristic = new HashMap<Term, Integer>();
		// get uncovered minterms
		TermList uncoveredOnset = onset;
		for (Term essentialPrimeImplicant : essentialPrimeImplicants)
			uncoveredOnset = uncoveredOnset.termsUncoveredBy(essentialPrimeImplicant);
		// find number of remaining uncovered minterms covered by each prime implicant
		for (Term primeImplicant : primeImplicants) {
			mostCoveredHeuristic.put(primeImplicant, 0);
			for (Term minterm : uncoveredOnset) {
				if (primeImplicant.covers(minterm))
					// increment count
					mostCoveredHeuristic.replace(primeImplicant, 
							mostCoveredHeuristic.get(primeImplicant)+1);
			}
		}
		return mostCoveredHeuristic;
	}
	
}
