/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Pujanadi
 */
public class Ant {
    private final int aCoef;    // alpha coeficient
    private final int bCoef;    // beta coeficient
    //private final int Q;
    private final double notCount;
    
    private ArrayList<Integer> tabuList;
    
    public int[] route;
    private Graph graph;
    private boolean printStartTour = true; //true
    private boolean printGetNextHopProbability = false; // true
    private boolean printIsAllowedHop = false;
    
    // startTour()
    int nextHop = -1;
    
    // getLK()
    int tourLength = 0;
    int nodeIndex;
    
    // get NextHopProbability
    int nextHopProbLength;
    boolean countNode;
    
    double phe;             //pheromone
    double dist,oneperdist;            //distance
    double pheDist;         // pheromone * distance
    double sigmaPheDist=0;  //sigma pheromone
    double deltaij;
    double[] nextHopProb;
    double[] probability;
    int[] probabilityIndex;
    int result = -1;
    int iterasi;
    int probabilityLength;
    int gp0l;
    
    // probability();
    private double rand;
    private double[] probDimension;
    private int itemLength;
    private int probDimensionLength;
    private int resultProbability=0;
    private double temp,temp2;

    //custom getNextHopProbability
    private List<Integer> availableHop;
    
    
    public Ant(Graph graph){
        this.graph = graph;
        this.tabuList = new ArrayList<Integer>();
        this.aCoef = 1; 
        this.bCoef = 1;
        //this.Q = 100;
        this.notCount = 1.1;
        
        gp0l = Graph.pheromoneGlobal[0].length;
        availableHop = new ArrayList<Integer>();
        nextHopProb = new double[gp0l];
    }
    
    public void startTour()
    {
        // 3. Repeat until tabu list is full
        // run ant
        
        
        
        nextHop = -1;
        for (int i = 0; i < graph.getHopNumber(); i++) {
            
           if(printStartTour) System.out.println("### START startTour "+i);
           
           final long start = System.nanoTime();
           nextHop = getNextHopProbability();
           final long end = System.nanoTime();
           
            System.out.println(" Hop time : "+((end/1000)-(start/1000))+" mikro second | "
                                            +((end/1000000)-(start/1000000))+" millisecond | "
                                            +((end/1000000000)-(start/1000000000))+" detik");
           addToTabuList(nextHop);
           
           if(printStartTour) System.out.println("### END startTour "+i);
           if(printStartTour) System.out.println("");
        }
        

    }
    
    
    
    public int getLK()
    {
        tourLength = 0;
        
        for (int i = 0; i < tabuList.size(); i++) {
            nodeIndex = tabuList.get(i);            
            tourLength += Math.abs( (graph.getHopValue(i) + 1) - graph.getNodeValue(nodeIndex) );
        }

        return tourLength;
    }
    
    private int getNextHopProbability()
    {
        countPheDist(); // perhitungan Sigma Pheromone * Distance
        // update sigma pheDist
        if(tabuList.size() > 0){
            int lastHop = tabuList.get(tabuList.size()-1);
            sigmaPheDist -= nextHopProb[lastHop];
            availableHop.remove(lastHop);
        }
        
        
        
        
        
        //probability = new double[gp0l-tabuList.size()];
        probability = new double[availableHop.size()];
        probabilityLength = probability.length;
        probabilityIndex = new int[probabilityLength];
        iterasi = tabuList.size();
        nextHopProbLength = nextHopProb.length;
        
        if(iterasi == 0){return getRandom( (graph.pheromoneGlobal.length-1) );}
        else
        {
            final long startGetNextNop = System.nanoTime();


                //menghitung probrability distribution
                /*
                final long startcountProbabilityDist = System.nanoTime();
                int count = 0;
                System.out.println("count check 1 : "+count);
                for (int j = 0; j < gp0l; j++) {
                    if(isAllowedHop(j)){ 
                        probabilityIndex[count] = j;
                        probability[count] = nextHopProb[j]/sigmaPheDist;
                        count++;
                    }
                }
                final long endcountProbabilityDist = System.nanoTime();
                System.out.println("count ProbabilityDist : "+(endcountProbabilityDist-startcountProbabilityDist));
                System.out.println("count check 2 : "+count);
                */
                
                final long startcountProbabilityDist2 = System.nanoTime();
                int count2 = 0;
                for(int j =0; j < availableHop.size(); j++){
                    try{
                        probabilityIndex[count2] = j;
                        probability[count2] = nextHopProb[j]/sigmaPheDist;
                        count2++;
                    }catch(Exception e){
                        System.out.println("index out of bound : "+count2);
                        System.out.println("probabilty index : "+probabilityIndex.length);
                        System.out.println("probability "+probability.length);
                    }
                    
                    
                }
                
                System.out.println("availableHop.size : "+availableHop.size());
                System.out.println("nextHopProbe size : "+nextHopProb.length);
                final long endcountProbabilityDist2 = System.nanoTime();
                System.out.println("count ProbabilityDist (new) : "+(endcountProbabilityDist2-startcountProbabilityDist2));

            
            final long endGetNextNop = System.nanoTime();
            System.out.println("get GetNextNop : "+ (endGetNextNop-startGetNextNop) );
            
            return probability(probability,probabilityIndex);
        }
    }
    
    private void countPheDist(){
        // START menghitung pheDist yang available
        final long startcountPheDist = System.nanoTime();
        availableHop.clear();
        iterasi = tabuList.size();
        phe = 0;
        dist = 0;
        sigmaPheDist = 0;
        for (int i = 0; i < gp0l; i++) {
            // inisialisasi available hop
            availableHop.add(i);
            
            // Menghitung sigma pheromone * distance
            
            phe = Math.pow(Graph.pheromoneGlobal[iterasi][i], aCoef);
            dist = (Math.abs(graph.getHopValue(iterasi)-graph.getNodeValue(i)))+1; // menghindari pembagian dengan 0, karena jarak terbaik adalah 0                    
            dist = (double)1.00/((double)dist);
            dist = Math.pow(dist, bCoef);
            nextHopProb[i] = phe*dist; // digunakan untuk menghitung distribusi probabilitas
            sigmaPheDist += phe*dist;
            
        }
        final long endcountPheDist = System.nanoTime();
            System.out.println("count phedist nodes : "+(endcountPheDist-startcountPheDist));
        // END menghitung PheDist yang available
    }
    
    private int probability(double[] item, int[] itemIndex)
    {
        final long start = System.nanoTime();
        int itemLength= item.length;
        
        probDimension = new double[itemLength+1];
        probDimension[0] = (double)0.0;
        for (int i = 0; i < itemLength; i++) {
            probDimension[i+1] = item[i];
        }
        rand = Math.random();
        
        for (int i = 1; i < itemLength-1; i++) {
            temp = probDimension[i];
            temp2 = probDimension[i+1];
            if(rand >= temp && rand < temp2) result = i;
            else if(rand >= temp2 && temp <= 1) result = item.length-1;
        }
        final long end = System.nanoTime();
        System.out.println("probability() time : "+ (end-start) );
        return itemIndex[result];
    }
    
    public boolean isAllowedHop(int nextHop)   {
        for (int i = 0; i < tabuList.size(); i++) {
            if( ((int)tabuList.get(i)) == nextHop) return false;
        }
        return true;
    }
    
    private int getRandom(int maxValue)
    {
        Random rand = new Random();
        return (int)rand.nextInt((maxValue - 0) + 1);
    }
    
    public void addToTabuList(int currHop) {   this.tabuList.add(currHop);  }
    
    public int[] getRoute() { 
        this.route = new int[tabuList.size()];
        
        for (int i = 0; i < tabuList.size(); i++) {
            this.route[i] = this.tabuList.get(i);
        }
        return this.route;  
    }
    
    public void printTabuList()
    {
        for (int i = 0; i < tabuList.size(); i++) {
            System.out.println("tabu list "+i+" : "+tabuList.get(i));
        }
        System.out.println("");
    }
    
    //public static double round(double value, int places) {
    public static double round(double value) {
        int places = 7;    

        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
}
