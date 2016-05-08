/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;

import aco.Graph;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Pujanadi
 */
public class Steganografi {
    
    public final String keyFile = "src/steganografi/key";
    private int imageWidth;
    private int imageHeight;
    private int[] globalOptimumLSB;
    
    public String embedImagePath;// = "/images/1x1_purple.png";
    public String coverImagePath;// = "/images/8x8.png";
    public String myEmbededImage;// = "/images/embededImage2.png"
    
    public String stegoImageName; // lenna-BINUS-stego.png
    public String extractionImageName; // = "/images/extraction.png"
    
    public BufferedImage embededImage;
    public int[] optimumLSB, arrToEmbed;
    public int[] arrIbb, arrCbb;
    
    public ImageByte myImage;
    public ImageByte coverImage;
    public Graph graph;
    
    public Steganografi(String embedImage, String coverImage, String myEmbededImage, 
                        String stegoImageName, String extractionImageName){
        this.embedImagePath = embedImage;
        this.coverImagePath = coverImage;
        this.myEmbededImage = myEmbededImage+".png";
        this.stegoImageName = stegoImageName+".png";
        this.extractionImageName = extractionImageName;
    }
    
    public void runSteganografi() throws IOException {
        myImage = new ImageByte(embedImagePath);
        coverImage = new ImageByte(coverImagePath);
        
        arrIbb = myImage.getImageBlock(true);
        arrCbb = coverImage.getImageBlock(true);
        
        graph = new Graph(arrIbb, arrCbb);
    }
    
    public Graph getGraph(){
        return this.graph;
    }
    
    public void writeHasilPercobaan(ImageByte coverImage, ImageByte myImageEmbeded, int iteration) throws IOException
    {
        double PSNR = getPNSR(coverImage, myImageEmbeded);
            
        File logFile = new File("PercobaanACO.txt");
        //System.out.println("log file path : "+logFile.getCanonicalPath());
        if(!logFile.exists()) {
            logFile.createNewFile();
        } 
        // Write log file
        Files.write(Paths.get(logFile.getCanonicalPath()), ("iterasi#"+iteration+" : "+PSNR+" \n").getBytes(), StandardOpenOption.APPEND);
    }
    
    public void writeKeyFile(Image myImage,int[] optimumLSB)
    {
        // Format key File
        // w:(width embeded image)h:(height embeded image)
        // (bitblock embeded image-1):(bitblock cover image-n)
        // ...
        // (bitblock embeded image-n):(bitblock cover image-m)
        String data = "w:"+myImage.getWidth()+"#h:"+myImage.getHeight()+"\n";
        
        for (int i = 0; i < optimumLSB.length; i++) {
            data += i+":"+optimumLSB[i]+"\n";
        }
        
        writeUsingOutputStream(data);
        globalOptimumLSB = optimumLSB;
        //s.printOptimumLSB();
    }
    
    public void writeUsingOutputStream(String data) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(keyFile));
            os.write(data.getBytes(), 0, data.length());
        } catch (IOException e) {
        }finally{
            try {
                os.close();
            } catch (IOException e) {
            }
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
    
    public int[] readKeyFile(int bitBlockLength, boolean printTrace){

        int[] optimumLSB = new int[bitBlockLength];
        boolean getWidthHeight = false;

        // This will reference one line at a time
        String line;
        String[] theLine;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(keyFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // get width and height
            line = bufferedReader.readLine();
            theLine = line.split("#");
            
            String[] temp = theLine[0].split(":");
            imageWidth = Integer.parseInt(temp[1]);
            
            temp = theLine[1].split(":");
            imageHeight = Integer.parseInt(temp[1]);

            if(printTrace) System.out.println("START readKeyFile");
            
            while((line = bufferedReader.readLine()) != null) {
                if(printTrace) System.out.println(line);
                
                // parsing line
                theLine = line.split(":");
                int index = Integer.parseInt(theLine[0]);
                int value = Integer.parseInt(theLine[1]);
                
                // input to array
                optimumLSB[index] = value; 
            }   
            
            if(printTrace) System.out.println("END readKeyFile");
            if(printTrace) System.out.println();

            // Always close files.
            bufferedReader.close();   
            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + keyFile + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + keyFile + "'");
        }
        
        return optimumLSB;
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
    
    public void printOptimumLSB()
    {
        System.out.println("START Optimum LSB");
        for (int i = 0; i < globalOptimumLSB.length; i++) {
            System.out.println("optimumLSB["+i+"] : "+globalOptimumLSB[i]);
        }
        System.out.println("END Optimum LSB");
        System.out.println("");
    }
    
    public double getPNSR(ImageByte coverImage, ImageByte stegoImage)
    {
        /*
            Rumush PNSR :
            10 x log ( ( (2 pangkat n ) -1 ) kuadtrat / MSE )
        */
        try {
            //int n = 2;
            int n = (coverImage.getBitDepth() >= 24) ? 24 : 8;
            n=8;
            System.out.println(" n : "+n);
            double p = Math.pow( (Math.pow(2,n)-1) , 2);
            //return 10 * Math.log( p / getMSE(coverImage, stegoImage));
            
            
            int pixels = coverImage.getWidth()*coverImage.getHeight();
            
            return  ( (double)pixels / (getMSE(coverImage, stegoImage) +1.00) );
            
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        } catch (IOException ex) {
            Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    public double getMSE(ImageByte coverImage, ImageByte stegoImage) throws URISyntaxException, IOException{
        BufferedImage cImg = coverImage.getBufferedImage();
        BufferedImage sImg = stegoImage.getBufferedImage();
        
        int width = coverImage.getWidth();
        int height = coverImage.getHeight();
        int zh;

        double mse = 0;
        double tempMse = 0;
        int cPixelTemp;
        int sPixelTemp;
        
        
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                
                cPixelTemp = cImg.getRGB(i, j); 
                sPixelTemp = sImg.getRGB(i, j);
                
                zh = Math.abs(cPixelTemp - sPixelTemp);
                
                tempMse += Math.pow(zh, 2);
            }
        }
        
        
        mse = (double) ((1/tempMse)/Math.pow((width*height),2));
        
        System.out.println("bagian atas : "+(1/Math.pow((width*height),2)));
        System.out.println("temp MSE : "+tempMse);
        System.out.println("MSE : "+mse);
        
        return mse;
    }
    
    public int getImageWidth(){ return imageWidth;}
    public int getImageHeight(){ return imageHeight;}
}
