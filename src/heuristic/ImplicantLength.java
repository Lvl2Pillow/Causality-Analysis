package heuristic;

import java.util.*;

import core.Term;

public class ImplicantLength implements HeuristicStrategy<Term, Integer> {
	// auto-generated
	private static Map<Term, Integer> implicantLength = 
			new HashMap<Term, Integer>();
	
	public ImplicantLength(Collection<Term> primeImplicants) {
		if (implicantLength == null)
			implicantLength = implicantLength(primeImplicants);
	}
	
	private Map<Term, Integer> implicantLength(Collection<Term> primeImplicants) {
		Map<Term, Integer> implicantLength = new HashMap<Term, Integer>();
		for (Term primeImplicant : primeImplicants)
			implicantLength.put(primeImplicant, primeImplicant.size());
		return implicantLength;
	}
	
	@Override
	public Map<Term, Integer> getHeuristic() {
		return implicantLength;
	}
	
}
