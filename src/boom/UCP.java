package boom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import core.Minterm;
import core.MintermList;
import core.Term;
import core.TermSet;

/**
 * Solution to the Unate Covering Problem for BOOM.
 * 
 * @author lvl2pillow
 *
 */
public class UCP extends core.UCP {
	public UCP(MintermList onset, TermSet primeImplicants) {
		super(onset, primeImplicants);
	}

	@Override
	protected void getEssentialPrimeImplicantsHelper(TermSet essentialPrimeImplicants, 
			Map<Minterm, Set<Term>> coverMatrix) {
		Set<Term> essentialPrimeImplicantCandidates = new HashSet<Term>();
		int score = 0;							// best heuristic score
		// least covered heuristic
		Map<Term, Integer> leastCoveredHeuristic = getLeastCoveredHeuristic();
		for (Entry<Term, Integer> entry : leastCoveredHeuristic.entrySet()) {
			Term primeImplicant = entry.getKey();
			int value = entry.getValue();
			if (value > score) {
				essentialPrimeImplicantCandidates.clear();
				essentialPrimeImplicantCandidates.add(primeImplicant);
				score = value;
			} else if (value == score) {
				essentialPrimeImplicantCandidates.add(primeImplicant);
			}
		}
		// if only one candidate, we don't need to use more heuristics
		if (essentialPrimeImplicantCandidates.size() == 1) {
			essentialPrimeImplicants.addAll(essentialPrimeImplicantCandidates);
			// TODO update the covering
			return;
		}
		// reset score
		score = 0;
		// most covered heuristic
		Map<Term, Integer> mostCoveredHeuristic = getMostCoveredHeuristic(essentialPrimeImplicants);
		// TODO
	}

}
