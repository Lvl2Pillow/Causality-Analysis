package core;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Abstract class for Coverage-Directed Search.
 * 
 * @author lvl2pillow
 *
 */
public abstract class CDS {
	protected final TermList onset;
	protected final TermList offset;
	protected final LiteralSet literals;		// all unique literals
	
	public CDS(TermList onset, TermList offset) {
		this.onset = onset;
		this.offset = offset;
		this.literals = new LiteralSet();
		try {
			for (Literal literal : onset.get(0)) {
				literals.add(new Literal(literal.getIndex(), true));
				literals.add(new Literal(literal.getIndex(), false));
			}
		} catch (NullPointerException e) {
			System.err.println("onset is empty.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Run the Coverage-Directed Search.
	 * 
	 * @return implicants.
	 */
	public TermSet run() {
		return implicants();
	}
	
	/**
	 * Returns the cumulation of sets of implicants that wholly covers the onset.
	 * 
	 * @return
	 */
	protected TermSet implicants() {
		TermSet implicants = new TermSet();
		Queue<TermList> onsetPermutations = new LinkedList<TermList>();
		// initial permutation is the complete onset
		onsetPermutations.add(new TermList(onset));
		while (!onsetPermutations.isEmpty())
			implicantsHelper(implicants, onsetPermutations, onsetPermutations.poll());
		return implicants;
	}
	
	/**
	 * 
	 * @param implicants all unique implicants found.
	 * @param onsetPermutations all permutations of the onset.
	 * @param onsetPermutation current permutation of the onset.
	 */
	protected void implicantsHelper(TermSet implicants, 
			Queue<TermList> onsetPermutations, TermList currentOnset) {
		// stop if current onset is empty, i.e. completely covered
		if (currentOnset.isEmpty()) return;
		TermSet implicantCandidates = implicantCandidates(currentOnset);
		// update covering and add new onset permutations
		for (Term implicant : implicantCandidates) {
			TermList newOnset = new TermList(currentOnset);
			newOnset.removeTermsCoveredBy(implicant);
			onsetPermutations.add(newOnset);
		}
		// add implicants
		implicants.addAll(implicantCandidates);
	}
	
	/**
	 * Returns all "good" implicant candidates given the current state of the 
	 * onset covering.
	 * 
	 * @param currentOnset current permutation of the onset.
	 * @return all "good" implicant candidates.
	 */
	protected TermSet implicantCandidates(TermList currentOnset) {
		TermSet implicantCandidates = new TermSet();
		Queue<Term> termPermutations = new LinkedList<Term>();
		// initial permutation is an empty term
		termPermutations.add(new Term());
		while (!termPermutations.isEmpty()) {
			implicantCandidatesHelper(implicantCandidates, termPermutations, 
					termPermutations.poll(), currentOnset);
		}
		return implicantCandidates;
	}
	
	/**
	 * 
	 * @param implicantCandidates all implicant candidates found.
	 * @param termPermutations all permutations of unbuilt terms.
	 * @param currentTerm current term under construction.
	 * @param currentOnset current permutation of the onset.
	 */
	protected void implicantCandidatesHelper(TermSet implicantCandidates, 
			Queue<Term> termPermutations, Term currentTerm, 
			TermList currentOnset) {
		// stop if current term does not intersect with the offset
		if (!currentTerm.intersects(offset)) {
			implicantCandidates.add(currentTerm);
			return;
		}
		LiteralSet literalCandidates = literalCandiates(currentTerm, currentOnset);
		// add new term permutations
		// TODO log if multiple literals
		for (Literal literal : literalCandidates) {
			Term newTerm = new Term(currentTerm);
			newTerm.add(literal);
			termPermutations.add(newTerm);
		}
	}
	
	/**
	 * Abstract method for finding set of literal candidates. This method
	 * must be implemented in a concrete class.
	 * 
	 * @param currentOnset
	 * @param term current term under construction.
	 * @return set of literal candidates to add to term.
	 */
	protected abstract LiteralSet literalCandiates(Term term, TermList currentOnset);
	
}
