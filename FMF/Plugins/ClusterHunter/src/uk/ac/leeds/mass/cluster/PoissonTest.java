/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.mass.cluster;

// Poisson tests class for cluster methods
// A modification of GamPoissonTest written by J R Davy Jan 1998
public class PoissonTest extends SignificanceTest implements Constants
{
    //value to be returned to the algorithm
    private double stat = 0.0;
    
    public PoissonTest(int nLarge, int nPoints, int measure, double pMin, double cMin, int mRep, double thr)
    {
        super(measure, pMin, cMin, mRep, thr);
        try
        {
            cons = new double[MAXCAN];
            canMax = MAXCAN;
            Logger.log("Poisson Test used with MAXIMUM case count of " + MAXCAN, Logger.messageSeverity.Information,"PoissonTest");
            if (nLarge > nPoints * (double) 0.5)
                throw new Exception ("Fatal Error! too many big incidence counts for Poisson test");
            for (int i=1; i< MAXCAN; i++)
            {
                cons[i] = ((double) 1.0)/i;
            }
        }
        catch (Exception e)
        {
            Logger.log(e.getMessage(), Logger.messageSeverity.Error, "PoissonTest");
            e.printStackTrace(Logger.getPrintStream());
            System.exit(1);
        }
    }

    @Override
    public final boolean isWorthTesting(double sumP, double sumC)
    {
        { return (worthTesting(sumP, sumC) && (sumC <= canMax)); }
    }    

    @Override
    public final boolean isSignificant(double sumP, double sumC)
    {
        stat = 0.0;
        
        double  cumPrb[];
        cumPrb = new double[MAXCAN];
        int jA = (int) sumC;
        double  aMean = (double) sumP;
        double prob;

        if (jA > 1)
        {
            cumPrb[0] = Math.exp(-aMean);
            prob = cumPrb[0];
            for(int j=1; j<jA; j++)
            {
                cumPrb[j] = aMean * cons[j] * cumPrb[j-1];
                prob += cumPrb[j];
            }
            prob = 1.0 - prob;
        }
        else
            prob = 1.0 - Math.exp(-aMean);
 
        if (prob <= thresh)
        {
            stat = computeStat(meas, sumP, sumC, (double)  prob);
            return true;
        }
        else
            return false;
    }

    public double getStat(){return stat;}
    
    private final double computeStat(int meas, double sumP, double sumC, double prob)
    {

        switch (meas)
        {
            case 1: stat = sumC - sumP; break;
            case 2: stat = sumC/sumP;   break;
            case 3: stat = (double) 1.0 - prob;  break;
        }
        return stat;
    }

    private int canMax;
    private double cons[];
}