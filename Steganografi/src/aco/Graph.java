/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.util.Arrays;

/**
 *
 * @author Pujanadi
 */
public class Graph {
    private final double c;
//    private final double c = 0.1; // small constant
    private static int[] cbb,ibb;
    public static double[][] pheromoneGlobal;
    //public double[][] DeltaTkij;
    private static final double p = 0.1;    // evaporation coeficient
    
    public Graph(int[] imageBitBlock, int[] coverBitBlock){
        cbb = coverBitBlock;
        ibb = imageBitBlock;
        /*
         * pheromone [jumlah blok embeded image][jumlah blok cover image][jumlah iterasi/embeded image]
         */
        
        c = (double)((double)1/cbb.length);
        
        pheromoneGlobal = new double[imageBitBlock.length][coverBitBlock.length];
        for (int i = 0; i < pheromoneGlobal.length; i++) {
            for (int j = 0; j < pheromoneGlobal[i].length; j++) {
              pheromoneGlobal[i][j] = c;  
            }
        }
        
        // initialization
        //DeltaTkij = pheromoneGlobal;
    }
    
    public void printPheromone(boolean tracePheromone)
    {
        if(tracePheromone) System.out.println("START print pheromone");
        if(tracePheromone) System.out.print("Node : ");
        
        for (int i = 0; i < pheromoneGlobal.length; i++) {
            if(tracePheromone) System.out.print(i+"  ");
        }
        
        if(tracePheromone) System.out.println("");
        
        for (int i = 0; i < pheromoneGlobal.length; i++) {
            for (int j = 0; j < pheromoneGlobal[i].length; j++) {
                if(tracePheromone) System.out.print("  "+pheromoneGlobal[i][j]);
            }
        }
        
        for (int i = 0; i < pheromoneGlobal.length; i++) {
            if(tracePheromone) System.out.print(i+"  ");
        }
        
        if(tracePheromone) System.out.println("END print pheromone");
        if(tracePheromone) System.out.println("");
    }
    
    public double getEvaporation() {    return this.p;  }
    
    public void setPheromone(int source, int destination, int value) { pheromoneGlobal[source][destination] = value; }
    
    public double getPheromone(int source, int destination) { return this.pheromoneGlobal[source][destination]; }
    
    public int hitungSelisih(int indexImage, int indexCover) {   return Math.abs( (ibb[indexImage] - cbb[indexCover]) ); }
    
    public int getHopNumber()   {   return ibb.length;  }
    
    public int getHopValue(int index)   {   return ibb[index];  }
    
    public int getNodeNumber()   {   return cbb.length;  }
    
    public int getNodeValue(int index)   {   return cbb[index];  }
}
