/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;

import aco.Aco;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Rima
 */
public class percobaan {
    public static void main(String[] args) throws IOException {
        // Percobaan 1
        String embedImagePath = "gray_8x8.png";//"4x4.png";
        String coverImagePath ="gray_16x16.png";//"10x10.png";//"lenna_128.png";
        String myEmbededImage = "lenna-Binus-embeded"; //tidak perlu diganti
        String stegoImageName = "stego-1pixel";//"lenna-BINUS-stego";
        String extractionImageName = "extraction-1pixel";//"lenna-Binus-extraction";//"/images/extraction.png";
        
        Steganografi s = new Steganografi(embedImagePath,coverImagePath,myEmbededImage,stegoImageName,extractionImageName);        
        writeHasilHeader(embedImagePath,coverImagePath,stegoImageName,extractionImageName);
        s.runSteganografi();
        Aco aco = new Aco(s);
        aco.findOptimumLSB();
        /*
        s = null;
        aco = null;
        
        // Percobaan 2
        embedImagePath = "1x1_purple.png";
        coverImagePath = "8x8.png";
        myEmbededImage = "lenna-Binus-embeded";
        stegoImageName = "lenna-BINUS-stego";
        extractionImageName = "lenna-Binus-extraction";//"/images/extraction.png";
        
        s = new Steganografi(embedImagePath,coverImagePath,myEmbededImage,stegoImageName,extractionImageName);        
        writeHasilHeader(embedImagePath,coverImagePath,stegoImageName,extractionImageName);
        s.runSteganografi();
        aco = new Aco(s);
        aco.findOptimumLSB();
        s = null;
        aco = null;
        */
        
    }
    
    public static void writeHasilHeader(String embedImagePath,String coverImagePath,String stegoImageName,String extractionImageName) throws IOException
    {
        File logFile = new File("PercobaanACO.txt");
        
        if(!logFile.exists()) {
            logFile.createNewFile();
        } 
        // Write log file
        Files.write(Paths.get(logFile.getCanonicalPath()), ("\nSecret image : "+embedImagePath+"\n"
                                                + "cover image : " + coverImagePath+"\n"
                                                + "stego image : " + stegoImageName+".png\n"
                                                + "extract image : " + extractionImageName+".png\n"
                                                + "\n").getBytes(), StandardOpenOption.APPEND);
    }
}
