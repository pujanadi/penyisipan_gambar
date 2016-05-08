/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Pujanadi
 */
public class Image_backup1 {

    public String IMG;
    BufferedImage img = null, newImg = null;

    public Image_backup1(String imagePath) {
        this.IMG = imagePath;
    }

    public void embedImage() {
        try {
            URL imgURL = Image.class.getClass().getResource(IMG);
            try {
                System.out.println("Pixel Depth : " + getBitDepth(new File(imgURL.toURI())));
            } catch (URISyntaxException ex) {
                Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                img = ImageIO.read(new File(imgURL.toURI()));
            } catch (URISyntaxException ex) {
                Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) { }


        //img path 
        //URL imgUjiCoba = Image.class.getClass().getResource("/images/8x8_grey.png");
        //URL gambarDIsisipkan = Image.class.getClass().getResource("/images/2x2.jpg");
        URL imgUjiCoba = Image.class.getClass().getResource("/images/Lenna.png");
        URL gambarDIsisipkan = Image.class.getClass().getResource("/images/ufopancoran2.jpg");
        try {
            try {
                byte[] imageInByte;

                BufferedImage originalImage = ImageIO.read(new File(imgUjiCoba.toURI()));
                BufferedImage theImage = ImageIO.read(new File(gambarDIsisipkan.toURI()));
                
                // convert BufferedImage to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(originalImage, "jpg", baos);
                baos.flush();
                imageInByte = baos.toByteArray();
                baos.close();

                for (int i = 0; i < imageInByte.length; i++) {
                    String s1 = String.format("%8s", Integer.toBinaryString(imageInByte[i] & 0xFF)).replace(' ', '0');
                //    System.out.println("image yang akan dimasukkan dalam byte ke-" + (i + 1) + " adalah " + imageInByte[i] + " bit valuenya adalah : " + s1);
                }

                // convert byte array back to BufferedImage
                InputStream ins = new ByteArrayInputStream(imageInByte);
                BufferedImage bImageFromConvert = ImageIO.read(ins);
                
                //test
        
                bImageFromConvert = embedToHostImage(originalImage,theImage);
                // end test

                ImageIO.write(bImageFromConvert, "png", new File("E:/workspace_netbeans7.0.1/Java/Thesis/Steganografi/src/images/embededImage.png"));
                
                /* START Get Host image pixel value */
                BufferedImage lihathasilimg;
                try {
                    lihathasilimg = ImageIO.read(new File("E:/workspace_netbeans7.0.1/Java/Thesis/Steganografi/src/images/embededImage.png"));

                    for (int i = 0; i < lihathasilimg.getWidth(); i++) {
                        for (int j = 0; j < lihathasilimg.getHeight(); j++) {
                            //System.err.println(getPixelData(lihathasilimg, i, j));
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
                }
                /* END Get Host image pixel value */
            } catch (URISyntaxException ex) {
                Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        } catch (IOException ex) {
            System.out.println("error : " + ex);
        }

        extractImage("/images/embededImage.png");
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

    private String getPixelData(BufferedImage img, int x, int y) {

        int argb = img.getRGB(x, y);
        /*
        int rgb[] = new int[] {
        (argb >> 16) & 0xff, //red
        (argb >>  8) & 0xff, //green
        (argb      ) & 0xff  //blue
        };
         */
        //  System.out.println("rgb: " + rgb[0] + " " + rgb[1] + " " + rgb[2]);
        //  return rgb; 

        /* get pixel byte value
        String s1;
        
        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < pixels.length; i++) {
        s1= String.format("%8s", Integer.toBinaryString(pixels[i] & 0xFF)).replace(' ', '0');
        System.out.println("pixel - "+i+" value : "+s1+" ("+pixels[i]+")");
        }
         * 
         */
        String s1;

        s1 = String.format("%8s", Integer.toBinaryString(argb)).replace(' ', '0');
        //System.out.println("pixel ("+x+","+y+") value : "+s1+" ("+argb+")");
        return "pixel (" + x + "," + y + ") value : " + s1 + " (" + argb + ")";
    }   

    public int[][] getPixelIntValue(BufferedImage b) {
        int argb;
        int imgWidth = b.getWidth();
        int imgHeight = b.getHeight();
        int[][] result = new int[imgWidth][imgHeight];

        // Set array value
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                argb = b.getRGB(x, y);

                result[x][y] = argb;
                System.out.println("x : " + x + " y : " + y + " value" + parseToBit(argb));
            }
        }
        // print array value
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                System.out.print(result[x][y] + " (" + parseToBit(result[x][y]) + ")   ");
            }
            System.out.println("");
        }
        
        return result;
    }
    
    public String parseToBit(int value){
        return String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
    }
    
    public BufferedImage embedToPixel(int currPixel, int embedPixel, BufferedImage hostImg){
        int alpha = 0xff;
        int hostImgWidth = hostImg.getWidth();
        int hostImgHeight = hostImg.getHeight();
        int hostImgDimension = hostImgWidth*hostImgHeight;
        
        int currRow = (int) Math.ceil(currPixel / hostImgWidth);
        int currColumn = (currPixel - ((hostImgWidth * currRow) - 1)) - 1;
        
        
        System.out.println("");        
        System.out.println("curr pixel : "+currPixel);
        System.out.println("curr row : "+currRow);
        System.out.println("curr column : "+currColumn);
        
        //lanjutkan coding yang dibawah (tinggal copas)
        int rgbHost = hostImg.getRGB(currColumn, currRow);
        
        System.out.println("nilai pixel : "+embedPixel+" - "+parseToBit(embedPixel));
        
        int rHost = (rgbHost >> 16) & 0xff;
        int gHost = (rgbHost >> 8) & 0xff;
        int bHost = (rgbHost) & 0xff;
        
        int rEmbeded1 = ((embedPixel >> 20) & 0xF) | (rHost & 0xF0);
        System.out.println("rHost : "+parseToBit(rHost & 0xF0));
        System.out.println("rEmbeded 1 : "+parseToBit(rEmbeded1));
        
        int gEmbeded1 = ((embedPixel >> 16) & 0xF) | (gHost & 0xF0);
        System.out.println("gHost : "+parseToBit(gHost & 0xF0));
        System.out.println("gEmbeded 1 : "+parseToBit(gEmbeded1));
        
        int bEmbeded1 = ((embedPixel >> 12) & 0xF) | (bHost & 0xF0);
        System.out.println("bHost : "+parseToBit(bHost & 0xF0));
        System.out.println("bEmbeded 1 : "+parseToBit(bEmbeded1));
        
        int newPixel1 = (alpha << 24)|(rEmbeded1 << 16) | (gEmbeded1 << 8) | bEmbeded1;
        System.out.println("insert : "+newPixel1+" - "+parseToBit(newPixel1)+" to ("+currRow+","+currColumn+")");
        hostImg.setRGB(currRow, currColumn, newPixel1);
        // Second pixel
        currPixel++; 
        
        if (currPixel <= hostImgDimension) {

            currRow = (int) Math.ceil(currPixel / hostImgWidth);
            currColumn = (currPixel - ((hostImgWidth * currRow) - 1)) - 1;

            System.out.println("");
            System.out.println("curr pixel : " + (currPixel));
            System.out.println("curr row : " + currRow);
            System.out.println("curr column : " + currColumn);

            rgbHost = hostImg.getRGB(currColumn, currRow);

            rHost = (rgbHost >> 16) & 0xff;
            gHost = (rgbHost >> 8) & 0xff;
            bHost = (rgbHost) & 0xff;

            int rEmbeded2 = ((embedPixel >> 8) & 0x0F) | (rHost & 0xF0);
            System.out.println("rHost : " + parseToBit(rHost & 0xF0));
            System.out.println("rEmbeded 2 : " + parseToBit(rEmbeded2));

            int gEmbeded2 = ((embedPixel >> 4) & 0x0F) | (gHost & 0xF0);
            System.out.println("gHost : " + parseToBit(gHost & 0xF0));
            System.out.println("gEmbeded 2 : " + parseToBit(gEmbeded2));


            int bEmbeded2 = ((embedPixel) & 0x0F) | (bHost & 0xF0);
            System.out.println("bHost : " + parseToBit(bHost & 0xF0));
            System.out.println("bEmbeded 2 : " + parseToBit(bEmbeded2));

            int newPixel2 = (alpha << 24) | (rEmbeded2 << 16) | (gEmbeded2 << 8) | bEmbeded2;

            System.out.println("insert : "+newPixel2+" - "+parseToBit(newPixel2)+" to ("+currRow+","+currColumn+")");
            hostImg.setRGB(currRow, currColumn, newPixel2);
        }
        
        return hostImg;
    }
    
    public BufferedImage embedToHostImage(BufferedImage hostImg, BufferedImage embedImg){
        // get dimensi host
        int hostImgWidth = hostImg.getWidth();
        int hostImgHeight = hostImg.getHeight();
        // get dimensi embed
        int embedImgWidth = embedImg.getWidth();
        int embedImgHeight = embedImg.getHeight();
        // 1 pixel embed image = 2pixel host image
        int tempEmbedPixel;
        int currPixel = 1;
        
        for (int i = 0; i < embedImgWidth; i++) {
            for (int j = 0; j < embedImgHeight; j++) {
                System.out.println("embed image ("+i+","+j+") : "+parseToBit(embedImg.getRGB(i, j)));
            }
        }
        
        System.out.println("embedimg height : "+embedImg.getHeight());
        System.out.println("embedimg width : "+embedImg.getWidth());
        
        // Save embed image dimension
        //set width
        System.out.println("save image width in ("+(hostImg.getHeight()-1)+","+(hostImg.getWidth()-2)+") value : "+embedImg.getWidth());
        hostImg.setRGB((hostImg.getHeight()-1), (hostImg.getWidth()-2), embedImg.getWidth());
        
        //set height
        System.out.println("save image width in ("+(hostImg.getHeight()-1)+","+(hostImg.getWidth()-1)+") value : "+embedImg.getHeight());
        hostImg.setRGB((hostImg.getHeight()-1), (hostImg.getWidth()-1), embedImg.getHeight());
        
        for (int y = 0; y < embedImg.getHeight(); y++) {
            for (int x = 0; x < embedImg.getWidth(); x++) {
                tempEmbedPixel = embedImg.getRGB(x, y);
                System.out.println("embed image value ("+x+","+y+")"+parseToBit(tempEmbedPixel));
                //embeding each pixel of embedImg
                hostImg = embedToPixel(currPixel,tempEmbedPixel, hostImg);
                currPixel+=2;
                
            }
        }
        
        // extract image
        //extractImage("E:/workspace_netbeans7.0.1/Java/Thesis/Steganografi/src/images/embededImage.png");
        //extractImage("/images/embededImage.png");
        
        
        /*
        
        // START Coding bener
        
        System.out.println("###### Embed Data ######");
        System.out.println("Embed image : "+parseToBit(rgb));
        System.out.println("Embed image R : "+parseToBit(((rgb >> 16) & 0xff)));
        System.out.println("Embed image G : "+parseToBit(((rgb >> 8 ) & 0xff)));
        System.out.println("Embed image B : "+parseToBit(((rgb      ) & 0xff)));
        System.out.println("");
        System.out.println("Host image R : "+parseToBit(rHost));
        System.out.println("Host image G : "+parseToBit(gHost));
        System.out.println("Host image B : "+parseToBit(bHost));
        System.out.println("");
        System.out.println("Host image R pixel 0,0 : "+parseToBit((rEmbeded1 << 16)  & 0xff));
        System.out.println("Host image G pixel 0,0 : "+parseToBit((gEmbeded1 <<  8)));
        System.out.println("Host image B pixel 0,0 : "+parseToBit(bEmbeded1));
        System.out.println("Host image B old pixel 0,0 : "+parseToBit(pixel0x0));
        System.out.println("Host image B new pixel 0,0 : "+parseToBit(newPixel1));
        System.out.println("");
        System.out.println("Host image R pixel 0,1 : "+parseToBit((rEmbeded2 << 16)));
        System.out.println("Host image G pixel 0,1 : "+parseToBit((gEmbeded2 <<  8)));
        System.out.println("Host image B pixel 0,1 : "+parseToBit(bEmbeded2));
        System.out.println("Host image B old pixel 0,1 : "+parseToBit(newPixel1));
        System.out.println("Host image B new pixel 0,1 : "+parseToBit(newPixel2));
         * 
         */
        return hostImg;
    }
    
    public void extractImage(String imageName){
        BufferedImage extractionImage;
        try {
            URL imgURL = Image.class.getClass().getResource(imageName);

            try {
                BufferedImage image = ImageIO.read(new File(imgURL.toURI()));
                int hostWidth = image.getWidth();
                int hostHeight = image.getHeight();
                
                System.out.println("host width : "+hostWidth);
                System.out.println("host height : "+hostHeight);

                int imageWidth = image.getRGB((hostWidth-1), (hostHeight-2));
                int imageHeight = image.getRGB((hostWidth-1), (hostHeight-1));
                
                imageWidth = imageWidth & 0xFFFF;
                imageHeight = imageHeight & 0xFFFF;
                
                System.out.println("imageWidth : "+imageWidth +" imageHeight : "+imageHeight);
                
                int imagePixels = (imageWidth*imageHeight)*2;
                int currPixel=1;
                int[] intPixel = new int[imagePixels];
                int temp, red,green,blue,red1,red2,green1,green2,blue1,blue2;
                int alpha = 0xFF;
                int currRow;
                int currColumn;


                for (int i = 0; i < imagePixels; i++) {
                    currRow = (int) Math.ceil(currPixel / hostWidth);
                    currColumn = (currPixel - ((hostWidth * currRow) - 1)) - 1;
                    
                    
                    temp = image.getRGB(currRow, currColumn);
                    // extract first pixel
                    red1 = ( (temp >> 16) & 0x0F );
                    red2 = ( (temp >>  8) & 0x0F );
                    green1 = ( (temp    ) & 0x0F );
                    System.out.println("##### Extraction #####");
                    System.out.println("curr pixel "+ currPixel);
                    System.out.println("cur row : "+currRow+" cur column : "+currColumn);
                    System.out.println("pixel value : "+temp+" - "+parseToBit(temp));
                    System.out.println("red 1 : "+parseToBit(red1));
                    System.out.println("red 2 : "+parseToBit(red2));
                    System.out.println("green 1 : "+parseToBit(green1));
                    
                    //extract second pixel
                    currPixel++;
                    currRow = (int) Math.ceil(currPixel / hostWidth);
                    currColumn = (currPixel - ((hostWidth * currRow) - 1)) - 1;
                    temp = image.getRGB(currRow, currColumn);
                    
                    green2 = ( (temp >> 16) & 0x0F );
                    blue1 = ( (temp >>  8) & 0x0F );
                    blue2 = ( (temp    ) & 0x0F );
                    System.out.println("cur row : "+currRow+" cur column : "+currColumn);
                    System.out.println("pixel value : "+temp+" - "+parseToBit(temp));
                    System.out.println("green 2 : "+parseToBit(green2));
                    System.out.println("blue 1 : "+parseToBit(blue1));
                    System.out.println("blue 2 : "+parseToBit(blue2));
                    
                    red = (red1 << 4) | red2;
                    green = (green1 << 4) | green2;
                    blue = (blue1 << 4) | blue2;
                    
                    intPixel[i]= (alpha << 24) | (red << 16) | (green << 8) | blue;
                    System.out.println("embed image pixel "+i+" - "+parseToBit(intPixel[i]));
                    currPixel++;
                }
                
                // mapping value to image
                extractionImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
                int rgb=0;
                System.out.println("@@@@@@@@@@@@@@@@@@");
                for (int y = 0; y < imageHeight; y++) {
                    for (int x = 0; x < imageWidth; x++) {
                        System.out.println("pixel ("+x+","+y+") : "+parseToBit(intPixel[rgb]));
                        extractionImage.setRGB(x, y, intPixel[rgb]);
                        rgb++;
                    }
                }
                System.out.println("@@@@@@@@@@@@@@@@@@");
                ImageIO.write(extractionImage, "png", new File("E:/workspace_netbeans7.0.1/Java/Thesis/Steganografi/src/images/extraction.png"));
                
            } catch (URISyntaxException ex) {
                Logger.getLogger(Steganografi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) { }
        
    }
}
