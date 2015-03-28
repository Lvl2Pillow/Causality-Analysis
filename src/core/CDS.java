package core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Abstract class for Coverage-Directed Search.
 * 
 * @author lvl2pillow
 *
 */
public abstract class CDS {
	protected final MintermList onset;
	protected final MintermList offset;
	
	public CDS(MintermList onset, MintermList offset) {
		this.onset = onset;
		this.offset = offset;
	}
	
	/**
	 * Run the Coverage-Directed Search.
	 * 
	 * @return implicants.
	 */
	public TermSet run() {
		return getImplicants();
	}
	
	/**
	 * Returns the cumulation of sets of implicants that wholly covers the onset.
	 * 
	 * @return
	 */
	protected TermSet getImplicants() {
		TermSet implicants = new TermSet();
		Queue<MintermList> onsetPermutations = new LinkedList<MintermList>();
		// initial permutation is the complete onset
		onsetPermutations.add(new MintermList(onset));
		while (!onsetPermutations.isEmpty())
			getImplicantsHelper(implicants, onsetPermutations, onsetPermutations.poll());
		return implicants;
	}
	
	/**
	 * 
	 * @param implicants all unique implicants found.
	 * @param onsetPermutations all permutations of the onset.
	 * @param onsetPermutation current permutation of the onset.
	 */
	protected void getImplicantsHelper(TermSet implicants, 
			Queue<MintermList> onsetPermutations, MintermList currentOnset) {
		// stop if current onset is empty, i.e. completely covered
		if (currentOnset.isEmpty()) return;
		TermSet implicantCandidates = getImplicantCandidates(currentOnset);
		// add new onset permutations
		for (Term implicant : implicantCandidates) {
			MintermList newOnset = new MintermList(currentOnset);
			onsetPermutations.add(newOnset.removeTermsCoveredBy(implicant));
		}
		// add implicants
		implicants.addAll(implicantCandidates);
	}
	
	/**
	 * Returns all "good" implicants given the current state of the onset covering.
	 * 
	 * @param currentOnset current permutation of the onset.
	 * @return all "good" implicant candidates.
	 */
	protected TermSet getImplicantCandidates(MintermList currentOnset) {
		TermSet terms = new TermSet();
		Queue<Term> termPermutations = new LinkedList<Term>();
		// initial permutation is an empty term
		termPermutations.add(new Term());
		while (!termPermutations.isEmpty()) {
			getImplicantCandidatesHelper(terms, termPermutations, 
					termPermutations.poll(), currentOnset);
		}
		return terms;
	}
	
	/**
	 * 
	 * @param terms all terms built.
	 * @param termPermutations all permutations of unbuilt terms.
	 * @param currentTerm current term under construction.
	 * @param currentOnset current permutation of the onset.
	 */
	protected void getImplicantCandidatesHelper(TermSet terms, 
			Queue<Term> termPermutations, Term currentTerm, 
			MintermList currentOnset) {
		// stop if current term does not intersect with the offset
		boolean intersects = false;
		for (Minterm minterm : offset) {
			if (currentTerm.covers(minterm)) {
				intersects = true;
				break;
			}
		}
		if (!intersects) return;
		LiteralSet literalCandidates = getLiteralCandidates(currentTerm, currentOnset);
		// add new term permutations
		// TODO log if multiple literals
		for (Literal literal : literalCandidates) {
			Term newTerm = new Term(currentTerm);
			newTerm.add(literal);
			terms.add(newTerm);
		}
	}
	
	/**
	 * Abstract method for getting set of literal candidates. This method
	 * must be implemented in a concrete class.
	 * 
	 * @param currentOnset
	 * @param term current term under construction.
	 * @return set of literal candidates to add to term.
	 */
	protected abstract LiteralSet getLiteralCandidates(Term term, 
			MintermList currentOnset);
	
	/**
	 * Returns the literal frequencies of all valid literals. The literal
	 * frequency is the number of occurrences of a literal from the remaining
	 * uncovered onset.
	 * 
	 * @param term current term under construction.
	 * @param currentOnset
	 * @return
	 */
	protected Map<Literal, Double> getLiteralFrequencyHeuristic(Term term, 
			MintermList currentOnset) {
		Map<Literal, Double> literalFrequencyHeuristic = new HashMap<Literal, Double>();
		MintermList uncoveredOnset = currentOnset.getUncoveredBy(term);
		// TODO log if currentOnset is empty or remaining uncovered onset is empty
		for (Minterm minterm : uncoveredOnset) {
			for (Literal literal : minterm) {
				// new literal
				if (!literalFrequencyHeuristic.containsKey(literal))
					literalFrequencyHeuristic.put(literal, 0.0);
				// increment literal frequency
				literalFrequencyHeuristic.replace(literal, 
						literalFrequencyHeuristic.get(literal)+1.0);
			}
		}
		return literalFrequencyHeuristic;
	}
	
}
