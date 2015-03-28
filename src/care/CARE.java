package care;

import core.TermList;
import core.TermSet;

public class CARE {
	final TermList onset;
	final TermList offset;
	final double interactivity;
	final Social social;
	
	public CARE(TermList onset, TermList offset, double interactivity) {
		this.onset = onset;
		this.offset = offset;
		this.interactivity = interactivity;
		this.social = new Social(onset, offset);
	}
	
	/**
	 * Run the CARE algorithm. Returns a set of essential prime implicants.
	 * 
	 * @return final solution.
	 */
	public TermSet run() {
		return new UCP(onset, new IE(offset, new CDS(onset, offset, 
				social.getSocialHeuristic(), interactivity).run()).run()).run();
	}
}
