/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Pujanadi
 * Algorimta:
 * 1. input embeded image dan cover image
 * 2. lakukan proses embed image
 * 3. kebalikan hasil return image
 * 4. buat fungsi untuk mengambil kembali pesan asli
 */
public class ImageByte {

    int[][] listHost;
    public String IMG;
    BufferedImage newImg = null;
    int[] ibb; // image block bit
    int[] cbb; // cover block bit
    private final int alpha = 0xff;
    public final String stegoPath = "/Users/Rima/Documents/Workspace_Netbean/Java/Thesis/Steganografi/src/images/";
    //public final String stegoPath = "/Users/Rima/Documents/Workspace_Netbean/images/";
    //public final String stegoPath = "/Users/Rima/Documents/Workspace_Netbean/Java/Thesis/Steganografi/src/images-stego/";
    public String stegoName = "stegoImg-";
    public final String formatFile = ".png";

    public ImageByte(String imagePath) {
        this.IMG = "/images/"+imagePath;
    }
    
    public URL getUrlImage(){
        return ImageByte.class.getClass().getResource(IMG);
    }
    
    public BufferedImage getBufferedImage() throws URISyntaxException, IOException{
        return ImageIO.read(new File(getUrlImage().toURI()));
    }

    //public int getBitDepth(File f) throws IOException {
    public int getBitDepth() throws IOException {
        //ImageInputStream in = ImageIO.createImageInputStream(f);
        File f = new File("src/"+IMG);
        ImageInputStream in = ImageIO.createImageInputStream(f);
        if (in == null) {
            throw new IOException("Can't create ImageInputStream!");
        }

        try {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(in);

            ImageReader reader;
            if (!readers.hasNext()) {
                throw new IOException("Can't read image format!");
            } else {
                reader = readers.next();
            }
            reader.setInput(in, true, true);
            int bitDepth = reader.getImageTypes(0).next().getColorModel().getPixelSize();
            reader.dispose();
            return bitDepth;
        } finally {
            in.close();
        }
    }


    public String parseToBit(int value) {
        //return String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
        
        StringBuilder str = new StringBuilder(String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0'));
        int idx = str.length() - 4;

        while (idx > 0)
        {
            str.insert(idx, " ");
            idx = idx - 4;
        }

        return str.toString();
        
    }

    public int[] getImageBlock(boolean printTrace)
    {
        URL imgURL;
        BufferedImage image;
        int imageWidth, imageHeight, imagePixels, blockbitindex;
        int pixel;
        int currPixel;
        byte[] intPixel;

        
        try {
            imgURL = getUrlImage();//Image.class.getClass().getResource(IMG);

            try {
                image = ImageIO.read(new File(imgURL.toURI()));
                imageWidth = image.getWidth();
                imageHeight = image.getHeight();

                imagePixels = (imageWidth * imageHeight);
                // Set array lengh for 4 bit of image container
                ibb = new int[imagePixels]; // one pixel divide into six 6 block of 4 bits LSB (R = 1 byte, G = 1 byte, B = 1 byte)
                blockbitindex = 0;
                
                if(printTrace) System.out.println("START Get Image Block Bit");
                
                for (int i = 0; i < imageWidth; i++) {
                    for (int j = 0; j < imageHeight; j++) {

                        pixel = image.getRGB(i, j); // RGB
                        //pixel = image.getData().getSample(i, j, 0); // Grayscale
                        
                        if(printTrace) System.out.println("pixel ("+i+","+j+") : "+parseToBit(pixel));

                        ibb[blockbitindex] = pixel;
                        blockbitindex++;
                    }
                }
                
                if(printTrace) System.out.println("END Get Image Block Bit");
                if(printTrace) System.out.println("");
                
                image = null;
            } catch (URISyntaxException ex) {
                Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
        }
        
        return ibb;
    }
    
    public int[] getCoverBlock(boolean printTrace)
    {
        URL imgURL;
        BufferedImage coverImage;
        int imageWidth, imageHeight, imagePixels, blockbitindex;
        int pixel;
        int currPixel;
        byte[] intPixel;

        
        try {
            imgURL = ImageByte.class.getClass().getResource(IMG);

            try {
                coverImage = ImageIO.read(new File(imgURL.toURI()));
                imageWidth = coverImage.getWidth();
                imageHeight = coverImage.getHeight();

                imagePixels = (imageWidth * imageHeight);
                // Set array lengh for 4 bit of image container
                cbb = new int[imagePixels]; // one pixel divide into six 6 block of 4 bits LSB (R = 1 byte, G = 1 byte, B = 1 byte)
                blockbitindex = 0;
                
                if(printTrace) System.out.println("START Get Cover Block Bit");
                
                for (int i = 0; i < imageWidth; i++) {
                    for (int j = 0; j < imageHeight; j++) {
                       
                       pixel = coverImage.getRGB(i, j); //RGB
                       //pixel = coverImage.getData().getSample(i, j, 0); //Grayscale
                       
                       if(printTrace) System.out.println("curr coordinate ("+i+","+j+")");
                       if(printTrace) System.out.println("pixel value : "+parseToBit(pixel));
                        
                       // Get pixel of cover image      //      A       R          G        B
                       cbb[blockbitindex] = pixel;      // 0000 0000 0000 1111 0000 0000 0000 0000
                       blockbitindex++;
                       if(printTrace) System.out.println("block bit-"+(blockbitindex)+" : "+parseToBit(cbb[blockbitindex]));

                    }
                }
                
                coverImage = null;
                
                if(printTrace) System.out.println("END Get Cover Block Bit");
                if(printTrace) System.out.println("");
                
            } catch (URISyntaxException ex) {
                Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {}
        
        return cbb;
        
    }
    
    public int getWidth(){ 
        URL imgURL;
        BufferedImage image;
        int imageWidth=0;
        
        try {
            imgURL = ImageByte.class.getClass().getResource(IMG);

            try {
                image = ImageIO.read(new File(imgURL.toURI()));
                imageWidth = image.getWidth();
            }catch(URISyntaxException e){}
        }catch(Exception e){}
        
        return imageWidth;
    }
    
    public int getHeight(){ 
        URL imgURL;
        BufferedImage image;
        int imageHeight=0;
        
        try {
            imgURL = ImageByte.class.getClass().getResource(IMG);

            try {
                image = ImageIO.read(new File(imgURL.toURI()));
                imageHeight = image.getHeight();
            }catch(URISyntaxException e){}
        }catch(Exception e){}
        
        return imageHeight;
    }
    
    public void embedToHostImage(String stegoImageName,int[] arrToEmbed, boolean printTrace) throws URISyntaxException, IOException{
        BufferedImage coverImage = getBufferedImage();
        BufferedImage embededImage;
        
        int imageWidth = coverImage.getWidth();
        int imageHeight = coverImage.getHeight();
        int pixels = imageWidth * imageHeight;
        int currX=0, currY=0;
        
        int arrToEmbdedIndex = 0;
        
        embededImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
        
        if(printTrace) System.out.println("START embedToHostImage");
        
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                // Get cover image pixel (3 imageBlockBit)
                int rgbHost = coverImage.getRGB(i, j);
                // get new value of pixel from array
                // pixels start from 0;
                int newPixel = arrToEmbed[arrToEmbdedIndex];
                arrToEmbdedIndex++;
                
                embededImage.setRGB(i, j, newPixel);
            }
        }
/*
        for (int i = 0; i < pixels; i++) {
            
            if(i > 0){
                if( (imageWidth - (currX)) == 0){
                   currX = 0;
                   currY++;
                }
            }
            if(printTrace) System.out.println("Width : "+imageWidth);
            if(printTrace) System.out.println("Height : "+imageHeight);
            if(printTrace) System.out.println(" currX : "+currX+" currY : "+currY);

            // Get cover image pixel (3 imageBlockBit)
            int rgbHost = coverImage.getRGB(currX, currY);

            if(printTrace) System.out.println("nilai pixel : " + rgbHost + " - " + parseToBit(rgbHost));

            // get new value of pixel from array
            // pixels start from 0;
            int newPixel = arrToEmbed[arrToEmbdedIndex];
            arrToEmbdedIndex++;
            
            if(printTrace) System.out.println("insert : " + newPixel + " - " + parseToBit(newPixel) + " to (" + currX + "," + currY + ")");
            embededImage.setRGB(currX, currY, newPixel);
                        
            currX++;

            if(printTrace) System.out.println("END embedToHostImage");
            if(printTrace) System.out.println("");
        }
  */      
        ImageIO.write(embededImage, "png", new File(stegoPath+stegoImageName));
       
    } 
        
    public void retrieveEmbededImage(String extractionImageName,BufferedImage coverImage,int[] arrCbb, int[] arrKey, int imageWidth, int imageHeight, boolean printTrace) throws IOException
    {
            String filePath = stegoPath+extractionImageName+formatFile;
            
            BufferedImage extractionImage;
    
            int hostWidth = coverImage.getWidth();
            int hostHeight = coverImage.getHeight();

            if(printTrace) System.out.println("############# START Retrieve Image #################");
            
            if(printTrace) System.out.println("Host Image Data");
            if(printTrace) System.out.println("host width : " + hostWidth);
            if(printTrace) System.out.println("host height : " + hostHeight);
            if(printTrace) System.out.println("");
            if(printTrace) System.out.println("Embeded Image Data");
            if(printTrace) System.out.println("imageWidth : " + imageWidth);
            if(printTrace) System.out.println("imageHeight : " + imageHeight);
            if(printTrace) System.out.println("");
            
            int RGB;
            
            // Get Pixel
            extractionImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
            
            int tempIndex = 0;
            
            for(int x = 0 ; x < extractionImage.getHeight() ; x++)
            {
                for(int y = 0 ; y < extractionImage.getWidth() ; y++)
                {
                    //looping 2x
                    RGB = arrCbb[arrKey[tempIndex]];
                    tempIndex++;

                    if(printTrace) System.out.println("extracted pixel : "+parseToBit(RGB));

                    System.out.println("coordinate : ("+x+","+y+")");
                    extractionImage.setRGB(x, y, RGB);
                }
            }
            
            ImageIO.write(extractionImage, "png", new File(filePath));

            if(printTrace) System.out.println("Extraction image : "+filePath);
            if(printTrace) System.out.println("############# END Retrieve Image #################");
    }
}
