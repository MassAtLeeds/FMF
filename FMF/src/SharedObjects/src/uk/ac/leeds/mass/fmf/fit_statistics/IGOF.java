/*
 * IGOF.java
 *
 * Created on 12 December 2007, 11:37
 *
 */

package uk.ac.leeds.mass.fmf.fit_statistics;

/**
 * Interface implemented by the matrix GOF statistics
 *
 * @author Kirk Harland
 */

public interface IGOF {

	public double test(double[][] calib, double[][] test);
    public double test(double[][] calib, double[][] test, double N);
	public String testName();
	public String fieldName();
	public boolean calibrateToLessThan();
    public boolean isPerfect(double testStat);

}
