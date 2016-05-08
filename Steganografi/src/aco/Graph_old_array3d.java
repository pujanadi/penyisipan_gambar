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
public class Graph_old_array3d {
    private final double c = 1; // small constant
    private int[] cbb,ibb;
    public double[][][] pheromoneGlobal;
    private final double p = 0.5;    // evaporation coeficient
    
    public Graph_old_array3d(int[] imageBitBlock, int[] coverBitBlock){
        cbb = coverBitBlock;
        ibb = imageBitBlock;
        /*
         * pheromone [jumlah blok embeded image][jumlah blok cover image][jumlah iterasi/embeded image]
         */
        
        pheromoneGlobal = new double[imageBitBlock.length][coverBitBlock.length][imageBitBlock.length];
        for (int i = 0; i < pheromoneGlobal.length; i++) {
            for (int j = 0; j < pheromoneGlobal[i].length; j++) {
                for (int k = 0; k < pheromoneGlobal[i][j].length; k++) {
                    pheromoneGlobal[i][j][k] = c;
                }
            }
        }
    }
    
    public void printPheromone()
    {
        for (int i = 0; i < pheromoneGlobal.length; i++) {
            for (int j = 0; j < pheromoneGlobal[i].length; j++) {
                for (int k = 0; k < pheromoneGlobal[i][j].length; k++) {
                    //System.out.print("Pheromone ["+i+"]["+j+"]["+k+"] = "+pheromoneGlobal[i][j][k]);
                    if(k % 3 == 0) System.out.println("");
                    System.out.print("  "+pheromoneGlobal[i][j][k]);
                    
                }
            }
        }
    }
    
    public double getEvaporation() {    return this.p;  }
    
    public void setPheromone(int source, int destination, int index, int value) { pheromoneGlobal[source][destination][index] = value; }
    
    public double getPheromone(int source, int destination, int index) { return this.pheromoneGlobal[source][destination][index]; }
    
    public int hitungSelisih(int indexImage, int indexCover) {   return Math.abs( (ibb[indexImage] - cbb[indexCover]) ); }
    
    public int getHopNumber()   {   return ibb.length;  }
    
    public int getHopValue(int index)   {   return ibb[index];  }
    
    public int getNodeNumber()   {   return cbb.length;  }
    
    public int getNodeValue(int index)   {   return cbb[index];  }
}
