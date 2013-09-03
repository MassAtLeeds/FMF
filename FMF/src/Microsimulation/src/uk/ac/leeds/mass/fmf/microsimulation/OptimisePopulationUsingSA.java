/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.microsimulation;

import uk.ac.leeds.mass.fmf.generic_algorithms.SimulatedAnnealing;
import uk.ac.leeds.mass.fmf.generic_algorithms.SimulatedAnnealingProcess;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.ICallBack;
import uk.ac.leeds.mass.fmf.shared_objects.ProcessTimer;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author geo8kh
 */
public class OptimisePopulationUsingSA extends FrameworkProcess implements ICallBack{

    private LinkConfiguration lc=null;
    private IApplicationInformation ai = null;
    private SimulatedAnnealing sa = null;
    
    private FMFTable popTable = null;

    private FMFTable output = null;
    private FMFTable stats = null;

    private PopulationSynthesisSA[] pssa = new PopulationSynthesisSA[Runtime.getRuntime().availableProcessors()];

    private int[][] population = null;
    private String[] populationID = null;
    private String[][] lkp = null;
    private double[][][] consts = null;
    private String[]   zones = null;
    private int totalPopulationIndex = 0;

    private int zoneCommitCounter = 0;
    private final static int COMMIT_ZONE_COUNT = 50;

    public OptimisePopulationUsingSA(IApplicationInformation ai, LinkConfiguration lc,SimulatedAnnealing sa){
        this.lc = lc;
        this.ai = ai;
        this.sa = sa;
    }

    @Override
    public void runProcess() {

        ProcessTimer pt = new ProcessTimer();
        pt.start();

        sa.enableSliders(false);

        this.initilise(10);

        //load in all the required data
        this.setName( "Loading Data..." );
        loadData();

        this.setName( "Optimising..." );
        //initialise the stages
        this.initilise( zones.length + 1 );

        for (int i = 0; i < zones.length; ) {


            synchronized(this){
                boolean shouldSleep = true;

                //set individual processes going
                for (int j = 0; j < pssa.length; j++) {

                    if (cancelled){break;}

                    if ( i == zones.length ){break;}

                    if ( pssa[j]==null || pssa[j].isDone() ){

                        SimulatedAnnealingProcess sap = sa.getSAProcess();

                        if ( lc.useSeed() ){
                            pssa[j] = new PopulationSynthesisSA(lc.getSeed());
                            sap.setRandomSeed(lc.getSeed());
                        }else{
                            pssa[j] = new PopulationSynthesisSA();
                        }

                        pssa[j].createPopulation(zones[i], population, consts[i], totalPopulationIndex);

                        sap.setSa(pssa[j]);

                        sap.setCallBack(new ICallBack(){
                            @Override
                            public void callBack(){
                                createOutput();
                                wakeup();
                            }
                        });

                        ai.getProcessManager().addProcess( sap );


                        boolean sleep = shouldSleep;
                        if ( pssa[j]==null ){
                            if (i == zones.length) {shouldSleep = sleep;} else {shouldSleep = false;}
                        } else if ( pssa[j]!=null && pssa[j].isDone() ){
                            shouldSleep = false;
                        }


                        progress++;
                        i++;
                    }

                }

                if ( shouldSleep ) { sleep(); }

            }

        }
        
        for (int maxProcesses = 0; maxProcesses < pssa.length; maxProcesses++) {
            checkAllProcessesFinished();
        }


        //set the process finished
        if (!this.cancelled){this.finished();}

        //make sure any outstanding commits are taken care of...
        output.commitInserts();
        stats.commitInserts();

        output.unlock();
        output.clear();

        stats.unlock();
        stats.clear();
        
        sa.enableSliders(true);

        pt.printProcessTime();
        pt.printProcessTimeInMinutes();

    }


    private synchronized void checkAllProcessesFinished(){
        boolean shouldSleep = false;
        for (int j = 0; j < pssa.length; j++) {
            if ( pssa[j]!=null && !pssa[j].isDone() ){
                shouldSleep = true;
            }
        }
        if ( shouldSleep ) { sleep(); }
    }

    
    private void loadData(){

        LinkDataManager dataLoader = new LinkDataManager(ai,lc);

        dataLoader.setCallBack(this);
        ai.getProcessManager().addProcess(dataLoader);
        sleep();

        popTable = dataLoader.getPopTable();
        population = dataLoader.getPopulation();
        populationID = dataLoader.getPopulationID();
        lkp = dataLoader.getLkp();
        consts = dataLoader.getConsts();
        zones = dataLoader.getZones();
        totalPopulationIndex = dataLoader.getLinkTableForPopulationIndex();

        dataLoader = null;

    }




    private synchronized void createOutput(){

        RegisteredDataSource rds = ai.getRDSfromFileName(lc.getOutputRDS());

        String personIDFieldName = lc.getPopulationIDField();

        

        if ( output == null ){

            popTable.loadData(ai, null);
            output = new FMFTable(lc.getOutputTableGroup()+"_population", null, null);
            output.addFieldToNewTable("ZoneID", FMFTable.FIELD_TYPE_STRING);
            output.addFieldToNewTable( personIDFieldName, popTable.getColumnType( lc.getPopulationIDField() ) );
            ai.getDataAccessFactory().createTable(rds,output, true);
            popTable.clear();

            stats = new FMFTable(lc.getOutputTableGroup()+"_stats",null,null);
            stats.addFieldToNewTable("ZoneID", FMFTable.FIELD_TYPE_STRING);
            stats.addFieldToNewTable("Order", FMFTable.FIELD_TYPE_INT);
            stats.addFieldToNewTable("Fit", FMFTable.FIELD_TYPE_DOUBLE);
            ai.getDataAccessFactory().createTable(rds, stats, true);

            output.loadData(ai, null);
            output.lock();
            stats.loadData(ai, null);
            stats.lock();
        }

        for (int i = 0; i < pssa.length; i++) {

            if ( pssa[i]!=null && pssa[i].isOptimised() && !pssa[i].isDone() ){

                int[] id = pssa[i].getPopulation();
                
                String zoneID = pssa[i].getCurrentZone();

                for (int j = 0; j < id.length; j++) {
                    output.insertValue("zoneID", zoneID);
                    output.insertValue(personIDFieldName, populationID[id[j]]);
                    output.insertRow();
                }

                double[] gof = pssa[i].getGofStat();
                for (int j = 0; j < gof.length; j++) {
                    if ( gof[j] > -1.0 ){
                        stats.insertValue("ZoneID", zoneID);
                        stats.insertValue("Order", new Integer(j+1));
                        stats.insertValue("Fit", gof[j]);
                        stats.insertRow();
                    }else{
                        break;
                    }
                }
                
                pssa[i].setDone(true);

                zoneCommitCounter ++;

            }
        }
        
        //commit the zones every so often to keep memory usage down
        if (zoneCommitCounter >= COMMIT_ZONE_COUNT){
            output.unlock();
            output.commitInserts();
            output.lock();
            stats.unlock();
            stats.commitInserts();
            stats.lock();
            zoneCommitCounter = 0;
        }

    }


    private synchronized void sleep(){
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void wakeup(){
        notifyAll();
    }

    @Override
    public void callBack() {
        wakeup();
    }

}
