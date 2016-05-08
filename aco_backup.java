/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Pujanadi
 * 
 */
public class aco_backup {
    
    private int currIbb;        // current image block bit want to finding. jumlah hop = jumlah ibb
    private final int aCoef;    // alpha coeficient
    private final int bCoef;    // beta coeficient
    private final double evaporation = 0.5;    // evaporation coeficient
    private final double c = 0.01;  // small positive constant
    
    private int t;      // waktu atau jumlah looping
    private int NC;     // ant-cycle define by user
    private int m;      // jumlah semut
    private double[][][] pheromoneLocal;
    private double[][][] pheromoneGlobal;
    private int[] bestTour;
    private int bestTourLength;
    private int antOnTrail;
    /*
     * pheromone global double[x][y][i] pheromoneGlobal
     * x = hop sebelumnya
     * y = hop selanjutnya
     * i = iterasi ke-n
     */
    private int[][] tabuList; // (s) ant's tabu list
    
    private int[] ibb;
    private int[] cbb;
    
    private Random randHop;
    
    
    public aco_backup()
    {
        this.aCoef = 1; 
        this.bCoef = 1;      
    }
    
    public void findOptimumLSB(int[] ibb, int[]cbb)
    {
        // Algoritma Ant system
        this.ibb = ibb;
        this.cbb = cbb;
        this.t = 300;   // time counter looping 300x
        this.NC = 12;   // ant-cycle
        this.m = 12;    // number of ants
        bestTour = new int[ibb.length];
        
// 1. initialize
        pheromoneLocal = new double[cbb.length][cbb.length][this.t];
        pheromoneGlobal = new double[cbb.length][cbb.length][this.t];
          
        //for (double[][] row: pheromoneLocal)Arrays.fill(row, 1.0);
        
        for (double[][] row: pheromoneGlobal)Arrays.fill(row, 1.0);        
        
        // 6. If (NC < NC MAX ) and (not stagnation behavior)
        for (int z = 0; z < NC; z++) {
            // 2. set tabu list from cover bit block (cbb)
            this.tabuList = new int[NC][cbb.length];
            Arrays.fill(tabuList, -1);

            // 3. Repeat until tabu list is full
            int nextHop;
            Random tempRand = new Random();
            for (int i = 0; i < tabuList.length; i++) {
                nextHop = tempRand.nextInt(tabuList.length);//antProbability(i);
                System.out.println(" next hop : "+nextHop);
                if(isAllowedHop(nextHop))
                {
                    // 
                    // update tabu List
                    updateTabuList(nextHop);
                    
                    //
                    int fromNode = i;
                    int toNode = nextHop;
                    int iteration = z;// value of current NC
                    pheromoneLocal[fromNode][toNode][iteration] =+ 1;
                }

                
            //4. move ants
                for (int j = 0; j < m; j++) {
                    //Move the k-th ant from tabu k (n) to tabu k (1)
                    //Compute the length L k of the tour described by the k-th ant
                    int Q; // number of ants pass the same path
                    int Lk; //Lk is the tour length of the k-th ant.
                    
                    Q = 
                    Lk = Math.abs(ibb[nextHop]-cbb[i]);
                    
                    //Update the shortest tour found
                }

            }
        }
        

        

        
        
    }
    
    public int antProbability(int hop)
    {
        // jalur yang akan di lewati kemungkinannnya sama karena pheromonenya semua sama
        // rumus : pheromone local * bobot next hop
        double[] probContainer = new double[cbb.length];
        double sigmaProbability = sigmaAntProbability(hop);
        double hopByPheromone; // = ( Math.pow(pheromoneLocal[hop], aCoef) ) * ( Math.pow( (1/ibb[hop]), bCoef) ) / sigmaProbability;
        // array of cbb (192), 
        /*
         * cbb[0] = 0.012
         * cbb[1] = 0.001
         * cbb[2] = 0.003
         * 
         * - hitung probability
         * - dari hasil hitung probability dapat ditemukan next hop
         */
        
       // for (int i = 0; i < probContainer.length; i++) {
         for (int i = 0; i < 1; i++) {
            hopByPheromone = ( Math.pow(pheromoneLocal[hop], aCoef) ) * ( Math.pow( (1.00/ibb[hop]), bCoef) ) / sigmaProbability;
            probContainer[i] = hopByPheromone;
            System.out.println("hop -"+i+" : "+ pheromoneLocal[hop] );
            System.out.println("dikali pheromone baru -"+i+" : "+ Math.pow( (1.00/ibb[hop]), bCoef) );
            System.out.println("sigma probability -"+i+" : "+ sigmaProbability );
            System.out.println("hopByPheromone-"+i+" : "+ hopByPheromone );
            System.out.println("");
        }
        
        return probability(probContainer);
    }
    
    public double sigmaAntProbability(int hop)
    {
        double result=-1;
        double temp;
        double bobot;
        
        for (int i = 0; i < cbb.length; i++) {
            // count every possible next hop, whiches not include in tabu list
            boolean inTabuList = false;
            for (int j = 0; j < tabuList.length ; j++) {
                if(tabuList[j] == i)
                {
                    inTabuList = true;
                }
            }
            if(!inTabuList)
            {
                bobot = Math.abs(ibb[hop]-cbb[i]); // selisih ibb dengan cbb
                if(bobot == 0) bobot = 1.1; // menghindari 1 dibagi 0
                
                temp =  ( Math.pow(pheromoneLocal[hop], aCoef) ) * ( Math.pow( ( 1.00/bobot ), bCoef) );
                result =+ temp;
            }
            
        }
        
        return result;
    }
    
    public boolean isAllowedHop(int nextHop)
    {
        // Checking next hop isn't in tabu list
        boolean temp = true;
        
        for (int i = 0; i < this.tabuList.length; i++) {
            System.out.println("tabu list : "+tabuList[i]);
            if(nextHop == tabuList[i]){
                temp = false;
                break;
            }
        }
        
        return temp;
    }
    
    public void updateTabuList(int currHop)
    {
        System.out.println("updateTabuList currHop : "+currHop);
        for (int i = 0; i < tabuList.length; i++) {
            if(tabuList[i] == -1) tabuList[i] = currHop;
            break;
        }
    }
    
    public int probability(double[] item)
    {
        double rand;
        double[] probDimension;
        int result=0;
        
        probDimension = new double[item.length];
        rand = Math.random();
        
        for (int i = 0; i < item.length; i++) {
            if(i == 0) probDimension[i]=0;
            else{
                probDimension[i] = probDimension[i-1]+1*item[i];   
            }    
        }
        
        // find in item
        if(rand >= 0 && rand < item[1]) result = 0;
        else
        {
            for (int i = 1; i < item.length-1; i++) {
                double temp = probDimension[i];
                double temp2 = probDimension[i+1];
                if(rand >= temp && rand < temp2) result = i;
                else if(rand >= temp2 && temp <= 1) result = item.length-1;
            }
        }
        
        return result;
    }
    
    private int getRandom()
    {
        int max = this.cbb.length;
        int min = 0;
        return randHop.nextInt((max - min) + 1) + min;
    }
}
