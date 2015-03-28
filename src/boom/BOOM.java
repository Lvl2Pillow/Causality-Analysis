package boom;

import core.TermList;
import core.TermSet;

/**
 * Run the BOOM algorithm.
 * 
 * @author lvl2pillow
 *
 */
public class BOOM {
	private final TermList onset;
	private final TermList offset;
	private final int nIterations;
	
	public BOOM(TermList onset, TermList offset, int nIterations) {
		this.onset = onset;
		this.offset = offset;
		this.nIterations = nIterations;
	}
	
	/**
	 * Run the BOOM algorithm. Returns a set of essential prime implicants.
	 * 
	 * @return final solution.
	 */
	public TermSet run() {
		return new UCP(onset, new IE(offset, new CDS(onset, offset, nIterations).run()).run()).run();
	}

}
