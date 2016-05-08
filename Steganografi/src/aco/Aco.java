/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import steganografi.ImageByte;
import steganografi.Steganografi;
/**
 *
 * @author Pujanadi
 * 
 */
public class Aco{
    
    private int currIbb;        // current image block bit want to finding. jumlah hop = jumlah ibb
    
    private int t;      // waktu atau jumlah looping
    private int NC;     // ant-cycle define by user
    private int m;      // jumlah semut
    private Ant[] ants;
    private int[] bestTour;
    private int bestTourLength;
    /*
     * pheromone global double[x][y][i] pheromoneGlobal
     * x = hop sebelumnya
     * y = hop selanjutnya
     * i = iterasi ke-n
     */
    
    private Graph graph;
    private Steganografi s;
    
    private boolean printBestTour = false;
    
    public Aco(Steganografi s)
    {
        //this.t = 1;//300;   // time counter looping 300x
        this.NC = 1;//3000;//12;   // ant-cycle
        this.m = 2;     // number of ant
        this.ants = new Ant[m];
        this.graph = s.getGraph();
        this.s = s;
    }
    
    public int[] findOptimumLSB()
    {
        int pheromoneGlobalLength = graph.pheromoneGlobal.length;
        int pheromoneGlobalSourceLength = graph.pheromoneGlobal[0].length;
        // Algoritma Ant system
        bestTour = new int[graph.getHopNumber()];
        
        // 6. If (NC < NC MAX ) and (not stagnation behavior)
        for (int z = 0; z < NC; z++) {
            // 2. set tabu list from cover bit block (cbb)
            // semut langsung random di step pertama
            
            // 3. Repeat until tabu list is full
            // run ant
            int nextHop;
            for (int i = 0; i < ants.length; i++) {
                Ant ant = new Ant(graph);
                
                final long start = System.nanoTime();
                ant.startTour();
                final long end = System.nanoTime();
                
                System.out.println(" ant tour time : "+((end/1000)-(start/1000))+" mikro second | "
                                +((end/1000000)-(start/1000000))+" millisecond | "
                                +((end/1000000000)-(start/1000000000))+" detik");
                
                //4a. move ants
                System.out.println("move ants");
                for (int j = 0; j < ant.getRoute().length; j++) {
                    //Move the k-th ant from tabu k (n) to tabu k (1)
                    //Compute the length L k of the tour described by the k-th ant -> class Ant getLK()                
                    //Update the shortest tour found
                    if(bestTourLength == 0) // First tour
                    {
                        bestTour = ant.getRoute();
                        bestTourLength = ant.getLK();
                    }else // n tour heve a better distance
                    {                        
                        if(ant.getLK() < bestTourLength)
                        {
                            bestTour = ant.getRoute();
                            bestTourLength = ant.getLK();
                        }
                    }
                    
                    if(printBestTour) System.out.println("##### Best Tour Found #####");
                    if(printBestTour) System.out.println("Ant tour length : "+ant.getLK());
                    if(printBestTour) System.out.println("Best tour length : "+bestTourLength);
                    
                }  
                ants[i] = ant;
            }
            
            //4.b hitung trail yang ditinggalkan oleh semut
            System.out.println("hitung trail");
            double DeltaTkij = 0.0;
            double Tkij = 0;
            for (int source = 0; source < pheromoneGlobalLength; source++) { //jumlah blok yang akan disisipkan
                for (int destination = 0; destination < pheromoneGlobalSourceLength; destination++) { //jumlah kemungkinan blok yang dapat disisipkan
                    
                        int Q = 1;
                        
                        for (int l = 0; l < ants.length; l++) { // jumlah semut
                            int[] antRoute = ants[l].getRoute(); // ambil rute semut
                            
                            if(antRoute[source] == destination){ // ada semut yang melalui jalur ini
                                try{
                                //    graph.DeltaTkij[source][destination] += Q/ants[l].getLK();
                                }catch(Exception e){
                                //    graph.DeltaTkij[source][destination] = 1;
                                    System.out.println("source : "+source+" destination : "+destination);
                                    System.out.println("LK : "+ants[l].getLK());
                                }
                                Tkij += Q/ants[l].getLK();
                                DeltaTkij += Tkij;
                            }
                            
                        }
                        
                }
            }
            
            //5. Update pheromone : evaporation deltaTij(t+n) 
            System.out.println("update pheromone");
            for (int source = 0; source < pheromoneGlobalLength; source++) { //jumlah blok yang akan disisipkan
                for (int destination = 0; destination < pheromoneGlobalSourceLength; destination++) { //jumlah kemungkinan blok yang dapat disisipkan                       
                    // update pheromone
                    graph.pheromoneGlobal[source][destination] = (graph.getEvaporation() * graph.pheromoneGlobal[source][destination]) + DeltaTkij;
                /*
                    graph.pheromoneGlobal[source][destination] = (graph.getEvaporation() * graph.pheromoneGlobal[source][destination]) + graph.DeltaTkij[source][destination];
                    graph.DeltaTkij[source][destination] = 0;
                */
                }
            }
            
            try {
                // Write log per iteration
                System.out.println("write log");
                writeLogEmbededImage(s,bestTour,z);
            } catch (IOException ex) {
                Logger.getLogger(Aco.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //graph.printPheromone();
        //printBestTour();
        return bestTour;
    }
    
    public void printAntsTour()
    {
        for (int i = 0; i < ants.length; i++) {
            int[] tours = ants[i].getRoute();

            for (int j = 0; j < tours.length; j++) {
                
                System.out.println("semut "+i+" rute ke-"+tours[j]);
            }
            System.out.println("length : "+ants[i].getLK());
            System.out.println("");
        }
        
    }
    
    public void printBestTour()
    {
        System.out.println("");
        System.out.println("##### Best Tour Found #####");
        System.out.println("Best tour length : "+this.bestTourLength);
        for (int i = 0; i < this.bestTour.length; i++) {
            System.out.println("rute ke-"+i+" : "+this.bestTour[i]);
        }
        System.out.println("");
    }
    
    public void writeLogEmbededImage(Steganografi s, int[]optimumLSB, int iteration) throws IOException{
        
        int[] arrToEmbed = getArrayToEmbed(s.arrIbb,s.arrCbb,optimumLSB,false);
        
        try {
            // embed LSB into cover image
            
            String stegoImageNameIteration = s.stegoImageName.substring(0, s.stegoImageName.length()-4) + "-" + iteration + ".png"; // dump file per iterasi
            s.coverImage.embedToHostImage(stegoImageNameIteration,arrToEmbed,false); // generate stego image
            
            try { 
           //     String stegoPath = "/Users/Rima/Documents/Workspace_Netbean/Java/Thesis/Steganografi/src/images/";
           
                File file = new File("src/images/"+stegoImageNameIteration);
                
                // retrieve image
                ImageByte myImageEmbeded = new ImageByte(stegoImageNameIteration);
                while(true){ // check if file exist
                    try{
                        BufferedImage img = myImageEmbeded.getBufferedImage();
                        break;
                    }catch(Exception e){
                        Thread.sleep(100);
                    }
                }
                
                BufferedImage img = myImageEmbeded.getBufferedImage();

                s.coverImage.retrieveEmbededImage(s.extractionImageName,img, arrToEmbed, optimumLSB, s.myImage.getWidth(),s.myImage.getHeight(),false);//, s.getImageWidth(), s.getImageHeight());

                s.writeHasilPercobaan(s.coverImage, myImageEmbeded, iteration);
            } catch (InterruptedException ex) {
                Logger.getLogger(Aco.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int[] getArrayToEmbed(int[] imageBitBlock, int[] coverBitBlock,int[] optimumLSB, boolean printTrace){
        /*
         * optimumLSB:
         *
         * optimumLSB[index ibb] = value cbb
         */
        
        if(printTrace) System.out.println("START getArrayToEmbed");
        
        for (int i = 0; i < optimumLSB.length; i++) {
            coverBitBlock[optimumLSB[i]] = imageBitBlock[i];
        }
        
        for (int i = 0; i < coverBitBlock.length; i++) {
            if(printTrace) System.out.println("New CBB ["+i+"] : "+parseToBit(coverBitBlock[i]) +" ("+coverBitBlock[i]+")");
        }
        
        if(printTrace) System.out.println("END getArrayToEmbed");
        if(printTrace) System.out.println("");
        
        return coverBitBlock;
    }
    
    public String parseToBit(int value) {
        StringBuilder str = new StringBuilder(String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0'));
        int idx = str.length() - 4;

        while (idx > 0)
        {
            str.insert(idx, " ");
            idx = idx - 4;
        }

        return str.toString();
        
    }

    
    
}
