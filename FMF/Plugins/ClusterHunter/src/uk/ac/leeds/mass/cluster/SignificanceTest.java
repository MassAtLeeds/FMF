package uk.ac.leeds.mass.cluster;

// base class for significance tests for GAM/K
//
// J R Davy
//      Jan 1989

public abstract class SignificanceTest implements Constants
{
    // Members
    protected int meas;
    protected double thresh;
    private double
        size[],
        table[],
        table2[],
        table3[];
    private double 
        popMin, // minimum population size
        canMin; // minimum incidence count
    private int
        maxRep; // number of multiple testing re-runs

    // Constructors
    public SignificanceTest(int measure, double pMin, double cMin, int mRep, double thr)
    {
        meas = measure;
        canMin = cMin;
        popMin = pMin;
        maxRep = mRep;
        thresh = thr;
        size = new double[MAXBOO];
        table = new double[MAXTAB];
        table2 = new double[MAXTAB];
        table3 = new double[MAXTAB];
    }

    // Methods
    public final void initialiseMultipleTesting()
    {
        for (int i=0; i< MAXTAB; i++){
            table2[i] = table3[i] = 0;
        }
    }
    public final void  multipleTests(int rTimes)
    {
        System.out.println("\n\n *multiple testing check");
        for (int j=0; j< rTimes; j++)
        {
            double prob = (table2[j] + (double) 1.0) / (maxRep + (double) 1.0);
            // double prob2 = (table3[j] + (double) 1.0) / (maxRep + (double) 1.0);
            System.out.print (" Circle = " + size[j] +  " km  Obs Circles = " + table[j]);
            System.out.println (" Random probability = " + prob);
        }
    }
    public abstract boolean isWorthTesting(double sumP, double sumC);
    public abstract boolean isSignificant(double sumP, double sumC);
    public final void setSize(int  i, double r) { size[i] = r; }
    public final void saveResults(int loop, int sample, int numHyps, int numSigCircles)
    {
        if (loop < MAXTAB)
            if (sample == 0)
                table[loop] = numSigCircles;
            else
            {
                if (numSigCircles >= table[loop])
                    table2[loop]++;
                if (numHyps * thresh >= table[loop])
                    table3[loop]++;
            }
    }
    protected boolean worthTesting(double sumP, double sumC)
    {
        return ((sumP <= sumC) && (sumP >= popMin) && (sumC >= canMin));
    }

    // Warning: The following 5 methods have been renamed, when it seems reasonable the ones with old names should be removed 
    public final int statype()            {return meas;}
    public final double threshold()        {return thresh;}
    public final double minPopSize()         {return popMin;}
    public final double minIncidenceCount()  {return canMin;}
    public final double multipleTestReruns() {return maxRep;}
    public final int getStatType() {return meas;}
    public final double getThreshold() {return thresh;}
    public final double getMinPopSize() {return popMin;}
    public final double getMinIncidenceCount() {return canMin;}
    public final double getMultipleTestReruns() {return maxRep;}

}