/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
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
public class Image {

    int[][] listHost;
    public String IMG;
    BufferedImage newImg = null;
    byte[] ibb; // image block bit
    byte[] cbb; // cover block bit
    private final int alpha = 0xff;
    public final String stegoPath = "/Users/Rima/Documents/Workspace_Netbean/Java/Thesis/Steganografi/src/images/";
    //public final String stegoPath = "/Users/Rima/Documents/Workspace_Netbean/images/";
    //public final String stegoPath = "/Users/Rima/Documents/Workspace_Netbean/Java/Thesis/Steganografi/src/images-stego/";
    public String stegoName = "stegoImg-";
    public final String formatFile = ".png";

    public Image(String imagePath) {
        this.IMG = "/images/"+imagePath;
    }
    
    public URL getUrlImage(){
        URL imgURL = null;
        
        imgURL = Image.class.getClass().getResource(IMG);
        
        return imgURL;
    }
    
    public BufferedImage getBufferedImage() throws URISyntaxException, IOException{
        return ImageIO.read(new File(getUrlImage().toURI()));
    }

    public int getBitDepth(File f) throws IOException {
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

    public byte[] getImageBlockBit(boolean printTrace)
    {
        URL imgURL;
        BufferedImage image;
        int imageWidth, imageHeight, imagePixels, blockbitindex;
        int temp;
        byte red, green, blue, red1, red2, green1, green2, blue1, blue2;
        int currPixel, currRow, currColumn;
        byte[] intPixel;

        
        try {
            imgURL = getUrlImage();//Image.class.getClass().getResource(IMG);

            try {
                image = ImageIO.read(new File(imgURL.toURI()));
                imageWidth = image.getWidth();
                imageHeight = image.getHeight();

                imagePixels = (imageWidth * imageHeight);
                // Set array lengh for 4 bit of image container
                ibb = new byte[(imagePixels*6)]; // one pixel divide into six 6 block of 4 bits LSB (R = 1 byte, G = 1 byte, B = 1 byte)
                currPixel = 1;
                intPixel = new byte[(imagePixels*2)];
                blockbitindex = 0;
                
                if(printTrace) System.out.println("START Get Image Block Bit");
                
                for (int i = 0; i < imageWidth; i++) {
                    for (int j = 0; j < imageHeight; j++) {
                   //     currRow = (int) Math.ceil(currPixel / imageWidth);
                   //     currColumn = (currPixel - ((imageWidth * currRow) - 1)) - 1;

                        temp = image.getRGB(i, j);
                        
                        // extract first pixel
                        red1 = (byte) ((temp >> 20) & 0x0F);
                        red2 = (byte) ((temp >> 16) & 0x0F);
                        green1 = (byte) ((temp >> 12) & 0x0F);
                        green2 = (byte) ((temp >> 8) & 0x0F);
                        blue1 = (byte) ((temp >> 4) & 0x0F);
                        blue2 = (byte) ((temp) & 0x0F);
                        
                        if(printTrace) System.out.println("pixel ("+i+","+j+") : "+parseToBit(temp));
                        if(printTrace) System.out.println("red 1 : "+parseToBit(red1));
                        if(printTrace) System.out.println("red 2 : "+parseToBit(red2));
                        if(printTrace) System.out.println("green 1 : "+parseToBit(green1));
                        if(printTrace) System.out.println("green 2 : "+parseToBit(green2));
                        if(printTrace) System.out.println("blue 1 : "+parseToBit(blue1));
                        if(printTrace) System.out.println("blue 2 : "+parseToBit(blue2));

                        ibb[blockbitindex] = red1;
                        blockbitindex++;
                        ibb[blockbitindex] = red2;
                        blockbitindex++;
                        ibb[blockbitindex] = green1;
                        blockbitindex++;
                        ibb[blockbitindex] = green2;
                        blockbitindex++;
                        ibb[blockbitindex] = blue1;
                        blockbitindex++;
                        ibb[blockbitindex] = blue2;
                        blockbitindex++;

                        currPixel++;
                    }
                }
                
                if(printTrace) System.out.println("END Get Image Block Bit");
                if(printTrace) System.out.println("");
                
                image = null;
            } catch (URISyntaxException ex) {
                Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return ibb;
    }
    
    public byte[] getCoverBlockBit(boolean printTrace)
    {
        URL imgURL;
        BufferedImage coverImage;
        int imageWidth, imageHeight, imagePixels, blockbitindex;
        int temp, red, green, blue, red1, red2, green1, green2, blue1, blue2;
        int currPixel, currRow, currColumn;
        int[] intPixel;

        
        try {
            imgURL = Image.class.getClass().getResource(IMG);

            try {
                coverImage = ImageIO.read(new File(imgURL.toURI()));
                imageWidth = coverImage.getWidth();
                imageHeight = coverImage.getHeight();

                imagePixels = (imageWidth * imageHeight);
                // Set array lengh for 4 bit of image container
                cbb = new byte[(imagePixels*3)]; // one pixel divide into six 6 block of 4 bits LSB (R = 1 byte, G = 1 byte, B = 1 byte)
                currPixel = 1;
                intPixel = new int[(imagePixels*2)];
                blockbitindex = 0;
                
                if(printTrace) System.out.println("START Get Cover Block Bit");
                
                for (int i = 0; i < imageWidth; i++) {
                    for (int j = 0; j < imageHeight; j++) {
                       
                       temp = coverImage.getRGB(i, j);
                       
                       if(printTrace) System.out.println("curr coordinate ("+i+","+j+")");
                       if(printTrace) System.out.println("pixel value : "+parseToBit(temp));
                        
                       // Get pixel of cover image                     //      A       R          G        B
                       cbb[blockbitindex] = (byte) ((temp >> 16) & 0x0F);      // 0000 0000 0000 1111 0000 0000 0000 0000
                       if(printTrace) System.out.println("block bit-"+(blockbitindex)+" : "+parseToBit(cbb[blockbitindex]));
                       blockbitindex++;
                       cbb[blockbitindex] = (byte) ((temp >> 8) & 0x0F);       // 0000 0000 0000 0000 0000 1111 0000 0000
                       if(printTrace) System.out.println("block bit-"+(blockbitindex)+" : "+parseToBit(cbb[blockbitindex]));
                       blockbitindex++;
                       cbb[blockbitindex] = (byte) ((temp) & 0x0F);            // 0000 0000 0000 0000 0000 0000 0000 1111
                       if(printTrace) System.out.println("block bit-"+(blockbitindex)+" : "+parseToBit(cbb[blockbitindex]));
                       blockbitindex++;

                       currPixel++;
                       
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
            imgURL = Image.class.getClass().getResource(IMG);

            try {
                image = ImageIO.read(new File(imgURL.toURI()));
                imageWidth = image.getWidth();
            }catch(Exception e){}
        }catch(Exception e){}
        
        return imageWidth;
    }
    
    public int getHeight(){ 
        URL imgURL;
        BufferedImage image;
        int imageHeight=0;
        
        try {
            imgURL = Image.class.getClass().getResource(IMG);

            try {
                image = ImageIO.read(new File(imgURL.toURI()));
                imageHeight = image.getHeight();
            }catch(Exception e){}
        }catch(Exception e){}
        
        return imageHeight;
    }
    
    public void embedToHostImage(String stegoImageName,byte[] arrToEmbed, boolean printTrace) throws URISyntaxException, IOException{
        BufferedImage coverImage = getBufferedImage();
        BufferedImage embededImage;
        
        int imageWidth = coverImage.getWidth();
        int imageHeight = coverImage.getHeight();
        int pixels = imageWidth * imageHeight;
        int currX=0, currY=0;
        
        int arrToEmbdedIndex = 0;
        
        embededImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        
        if(printTrace) System.out.println("START embedToHostImage");

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

            int rHost = (rgbHost >> 16) & 0xff;     // get 8 bit of red pixel value
            int gHost = (rgbHost >> 8) & 0xff;      // get 8 bit of green pixel value
            int bHost = (rgbHost) & 0xff;           // get 8 bit of blue pixel value
            
            // get new value of pixel from array
            // pixels start from 0;
              
            int rEmbeded = arrToEmbed[arrToEmbdedIndex];
            arrToEmbdedIndex++;
            int gEmbeded = arrToEmbed[arrToEmbdedIndex];
            arrToEmbdedIndex++;
            int bEmbeded = arrToEmbed[arrToEmbdedIndex];
            arrToEmbdedIndex++;
            
            int rEmbeded1 = (rEmbeded) | (rHost & 0xF0);
            if(printTrace) System.out.println("bit block - "+(arrToEmbdedIndex-3)+" rHost : " + parseToBit(rHost & 0xF0));
            if(printTrace) System.out.println("bit block - "+(arrToEmbdedIndex-3)+"rToEmbed 1 :"+parseToBit(rEmbeded));
            if(printTrace) System.out.println("bit block - "+(arrToEmbdedIndex-3)+"rEmbeded 1 : " + parseToBit(rEmbeded1));

            int gEmbeded1 = gEmbeded | (gHost & 0xF0);
            if(printTrace) System.out.println("bit block - "+(arrToEmbdedIndex-2)+" gHost : " + parseToBit(gHost & 0xF0));
            if(printTrace) System.out.println("bit block - "+(arrToEmbdedIndex-2)+" gEmbeded 1 :"+parseToBit(gEmbeded));
            if(printTrace) System.out.println("bit block - "+(arrToEmbdedIndex-2)+" gEmbeded 1 : " + parseToBit(gEmbeded1));

            int bEmbeded1 = bEmbeded | (bHost & 0xF0);
            if(printTrace) System.out.println("bit block - "+arrToEmbdedIndex+" bHost : " + parseToBit(bHost & 0xF0));
            if(printTrace) System.out.println("bit block - "+arrToEmbdedIndex+" bEmbeded 1 :"+parseToBit(bEmbeded));
            if(printTrace) System.out.println("bit block - "+arrToEmbdedIndex+" bEmbeded 1 : " + parseToBit(bEmbeded1));

            int newPixel = (alpha << 24) | (rEmbeded1 << 16) | (gEmbeded1 << 8) | bEmbeded1;
            if(printTrace) System.out.println("insert : " + newPixel + " - " + parseToBit(newPixel) + " to (" + currX + "," + currY + ")");
            embededImage.setRGB(currX, currY, newPixel);
                        
            currX++;

            if(printTrace) System.out.println("END embedToHostImage");
            if(printTrace) System.out.println("");
        }
        
        // Windows
        //ImageIO.write(embededImage, "png", new File("E:/workspace_netbeans7.0.1/Java/Thesis/Steganografi/src/images/embededImage2.png"));
        // Mac 
        //ImageIO.write(embededImage, "png", new File(stegoPath+stegoImageName+formatFile));
        ImageIO.write(embededImage, "png", new File(stegoPath+stegoImageName));
       
    } 
        
    public void retrieveEmbededImage(String extractionImageName,BufferedImage coverImage,byte[] arrCbb, int[] arrKey, int imageWidth, int imageHeight, boolean printTrace) throws IOException
    {
        
            // bufferedimage image = image yang akan di retrieve
            // Windows
            //String filePath = "E:/workspace_netbeans7.0.1/Java/Thesis/Steganografi/src/images/extraction.png";
            // Mac 
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
            
            int temp, red, green, blue, red1, red2, green1, green2, blue1, blue2, RGB;
            
            // Get Pixel
            extractionImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
            
            int tempIndex = 0;
            
            for(int x = 0 ; x < extractionImage.getHeight() ; x++)
            {
                for(int y = 0 ; y < extractionImage.getWidth() ; y++)
                {
                    //looping 2x
                    red1 = arrCbb[arrKey[tempIndex]];
                    if(printTrace) System.out.println("block "+tempIndex+" : "+parseToBit(red1));
                    tempIndex++;
                    red2 = arrCbb[arrKey[tempIndex]];
                    if(printTrace) System.out.println("block "+tempIndex+" : "+parseToBit(red2));
                    tempIndex++;
                    green1 = arrCbb[arrKey[tempIndex]];
                    if(printTrace) System.out.println("block "+tempIndex+" : "+parseToBit(green1));
                    tempIndex++;
                    green2 = arrCbb[arrKey[tempIndex]];
                    if(printTrace) System.out.println("block "+tempIndex+" : "+parseToBit(green2));
                    tempIndex++;
                    blue1 = arrCbb[arrKey[tempIndex]];
                    if(printTrace) System.out.println("block "+tempIndex+" : "+parseToBit(blue1));
                    tempIndex++;
                    blue2 = arrCbb[arrKey[tempIndex]];
                    if(printTrace) System.out.println("block "+tempIndex+" : "+parseToBit(blue2));
                    tempIndex++;

                    red = ((red1 & 0x0F) << 4) | (red2 & 0x0F);
                    green = ((green1 & 0x0F) << 4) | (green2 & 0x0F);
                    blue = ((blue1 & 0x0F) << 4) | (blue2 & 0x0F);

                    RGB = (alpha << 24) | (red << 16) | (green << 8) | blue;

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
