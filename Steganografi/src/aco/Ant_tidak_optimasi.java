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
import java.util.Random;

/**
 *
 * @author Pujanadi
 */
public class Ant_tidak_optimasi {
    private final int aCoef;    // alpha coeficient
    private final int bCoef;    // beta coeficient
    //private final int Q;
    private final double notCount;
    
    private ArrayList<Integer> tabuList;
    
    public int[] route;
    public int tourLength; // LK
    private Graph graph;
    private boolean printStartTour = true; //true
    private boolean printGetNextHopProbability = false; // true
    private boolean printIsAllowedHop = false;
    
    
    // get NextHopProbability
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

    
    
    public Ant_tidak_optimasi(Graph graph){
        this.graph = graph;
        this.tabuList = new ArrayList<Integer>();
        this.aCoef = 1; 
        this.bCoef = 1;
        //this.Q = 100;
        this.notCount = 1.1;
        
        gp0l = Graph.pheromoneGlobal[0].length;
    }
    
    public void startTour()
    {
        // 3. Repeat until tabu list is full
        // run ant
        
        
        int nextHop = -1;
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
        int tourLength = 0;
        int nodeIndex;
        
        for (int i = 0; i < tabuList.size(); i++) {
            nodeIndex = tabuList.get(i);            
            tourLength += Math.abs( graph.getHopValue(i) - graph.getNodeValue(nodeIndex) );
        }

        return tourLength;
    }
    
    private int getNextHopProbability()
    {
        System.out.println("int get NextHopProbability");
        
        
        // hitung sigmaPheDist
        nextHopProb = new double[gp0l];
        probability = new double[gp0l-tabuList.size()];
        probabilityLength = probability.length;
        probabilityIndex = new int[probabilityLength];
        iterasi = tabuList.size();
        int nextHopProbLength = nextHopProb.length;
        
        if(iterasi == 0){
            return getRandom( (graph.pheromoneGlobal.length-1) );
        }
        else
        {
            final long startGetNextNop = System.nanoTime();
            // count current hop pheromone * distance
            for (int i = 0; i < gp0l; i++) {
                // check if hop is allowed
                // check tabulist 1 (source)
                boolean countNode = isAllowedHop(i);

                if(!countNode)
                {
                    // node not allowed
                    nextHopProb[i] = notCount;
                }else{
                    
                    int hop = iterasi-1;
                    phe = this.graph.pheromoneGlobal[hop][i];
                    phe = Math.pow(phe, aCoef);

                    dist = Math.abs(graph.getHopValue(hop)-graph.getNodeValue(i));
                    dist = dist+1; // menghindari pembagian dengan 0, karena jarak terbaik adalah 0
                    double selisihDist = dist;
                    dist = (double)1.00/((double)dist);
                    dist = Math.pow(dist, bCoef);
                    
                    pheDist = phe*dist;
                    nextHopProb[i] = pheDist;

                }
                
                // count Sigma Phe * Dist
                sigmaPheDist = 0;
                for (int j = 0; j < nextHopProbLength; j++) {
                    if(!isAllowedHop(j)){ }
                    else                
                    {
                        sigmaPheDist += nextHopProb[j];
                    }
                }
                
                int count = 0;
                for (int j = 0; j < nextHopProbLength; j++) {
                    if(!isAllowedHop(j)){ }
                    else {
                        probabilityIndex[count] = j;
                        probability[count] = nextHopProb[j]/sigmaPheDist;
                        count++;
                    }
                }
                

            }
            
            final long endGetNextNop = System.nanoTime();
            System.out.println("get GetNextNop : "+ (endGetNextNop-startGetNextNop) );
            
            return probability(probability,probabilityIndex);
        }
    }
    
    private int probability(double[] item, int[] itemIndex)
    {
        final long start = System.nanoTime();
        System.out.println("int probability()");
        double rand;
        double[] probDimension;
        int itemLength = item.length;
        int probDimensionLength = itemLength+1;
        int result=0;
        double temp,temp2;
        
        
        //inisialisasi dimensi probabilitas
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
        System.out.println("get probability time : "+ (end-start) );
        return itemIndex[result];
    }
    
    public boolean isAllowedHop(int nextHop)   {
        boolean temp = true;
        int tmp;
        
        for (int i = 0; i < tabuList.size(); i++) {
            tmp = (int)tabuList.get(i);
            if(tmp == nextHop) 
            {
                temp = false;
                break;
            } 
        }
        return temp;
    }
    
    private int getRandom(int maxValue)
    {
        Random rand = new Random();
        int max = maxValue;
        int min = 0;
        return (int)rand.nextInt((max - min) + 1) + min;
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
