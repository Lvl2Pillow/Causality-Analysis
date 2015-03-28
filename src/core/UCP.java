package core;

/**
 * Abstract class for Unate Covering Problem.
 * 
 * @author lvl2pillow
 *
 */
public abstract class UCP {
	@SuppressWarnings("unused")
	private final MintermList onset;
	@SuppressWarnings("unused")
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
	 * Abstract method for getting set of essential prime implicants that wholly
	 * covers the onset. This method must be implemented in a concrete class.
	 * 
	 * @return set of essential prime implicants that wholly covers the onset.
	 */
	protected abstract TermSet getEssentialPrimeImplicants();
	
}
