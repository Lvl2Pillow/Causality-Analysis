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
	private final MintermList onset;
	private final TermSet primeImplicants;
	
	public UCP(MintermList onset, TermSet primeImplicants) {
		this.onset = onset;
		this.primeImplicants = primeImplicants;
	}
	
	/**
	 * Returns set of Essential Prime Implicants.
	 * 
	 * @return
	 */
	public TermSet run() {
		return getEssentialPrimeImplicants();
	}
	
	/**
	 * 
	 * @return set of essential prime implicants that wholly covers the onset.
	 */
	protected TermSet getEssentialPrimeImplicants() {
		TermSet essentialPrimeImplicants = new TermSet();
		Map<Minterm, Set<Term>> coverMatrix = getCoverMatrix();
		while (!coverMatrix.isEmpty()) {
			getEssentialPrimeImplicantsHelper(essentialPrimeImplicants, coverMatrix);
		}
		return essentialPrimeImplicants;
	}
	
	/**
	 * Abstract method for finding essential prime implicants.
	 * This method must be implemented in a concrete class.
	 */
	protected abstract void getEssentialPrimeImplicantsHelper(TermSet essentialPrimeImplicants, 
			Map<Minterm, Set<Term>> coverMatrix);
	
	/**
	 * A cover matrix shows which minterms are covered by which prime implicants.
	 * </br>
	 * (key = minterm : value = set of prime implicants that covers the minterm)
	 * 
	 * @return cover matrix.
	 */
	protected Map<Minterm, Set<Term>> getCoverMatrix() {
		Map<Minterm, Set<Term>> coverMatrix = new HashMap<Minterm, Set<Term>>();
		for (Minterm minterm : onset) {
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
		Map<Minterm, Set<Term>> coverMatrix = getCoverMatrix();
		for (Entry<Minterm, Set<Term>> entry : coverMatrix.entrySet()) {
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
		MintermList uncoveredOnset = onset;
		for (Term essentialPrimeImplicant : essentialPrimeImplicants)
			uncoveredOnset = uncoveredOnset.getUncoveredBy(essentialPrimeImplicant);
		// find number of remaining uncovered minterms covered by each prime implicant
		for (Term primeImplicant : primeImplicants) {
			mostCoveredHeuristic.put(primeImplicant, 0);
			for (Minterm minterm : uncoveredOnset) {
				if (primeImplicant.covers(minterm))
					// increment count
					mostCoveredHeuristic.replace(primeImplicant, 
							mostCoveredHeuristic.get(primeImplicant)+1);
			}
		}
		return mostCoveredHeuristic;
	}
	
}
