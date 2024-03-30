//ECS605U Coursework Mohammed Rifath Ahmed 190183969

import java.io.*;
import java.util.TreeSet;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.lang.Math.*;

public class Demo extends Component implements ActionListener {
    
    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************
  
    String descs[] = {
        "N/A","Original","Negative","Undo","Shift 20","Shift -20","Rescale All","Log","Power Law",
		"Bit Slice","Salt&Pepper","Median","Min Filter","Max Filter","Midpoint","Histogram","Image2"
    };
    
    String scales[] = {
        "N/A","Scale 0.5","Scale 1.5","Scale 2"
    };
    
    String blur[] = {
    	"N/A","Averaging","Weighted Averaging","4 N Laplacian Enhance","Roberts"
    };
    
    String binary[] = {
    	"N/A","Addition","Subtraction","Multiplication","Division","Bit AND","Bit OR","Bit XOR",
    	"Bit NOT","Test",
    };
    
    int opIndex;  //option index for filters
    int lastOp;
    int scIndex;  //option index for scales
    int lastSc;
    int blIndex;  //option index for blurs
    int lastBl;
    int binIndex;  //option index for binary
    int lastBin;
    
	public int[][][] LastArray;
    private BufferedImage bi, biFiltered, bi2;   // the input image saved as bi;//
    int w, h, w2, h2;
     
    public Demo() {
        try {
            bi = ImageIO.read(new File("default.jpg"));
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
        
        try {
            bi2 = ImageIO.read(new File("default2.jpg"));
            w2 = bi2.getWidth(null);
            h2 = bi2.getHeight(null);
            
            if (w != w2) {
				System.out.println("Incompatible");
				System.exit(0);
			}

			if (h != h2) {
				System.out.println("Incompatible");
				System.exit(0);
			}
            
            System.out.println(bi2.getType());
            
            if (bi2.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage biNew = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);
                Graphics big = biNew.getGraphics();
                big.drawImage(bi2, 0, 0, null);
                bi2 = biNew;
            }
            
            System.out.println("Success");
            biFiltered = bi;
            
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }                         
 
    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }
 
    String[] getDescriptions() {
        return descs;
    }
    
    String[] getScales() {
        return scales;
    }
    
    String[] getBlur() {
        return blur;
    }
    
    String[] getBinary() {
        return binary;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    void setOpIndex(int i) {
        opIndex = i;
    }
    
    void setScIndex(int i) {
        scIndex = i;
    }
    
    void setBlIndex(int i) {
        blIndex = i;
    }
    
    void setBinIndex(int i) {
        binIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
    	super.paint(g);
        filters(g);
        rescaleImg(g);
        blurImg(g);
        binaryImg(g);
        //undof(g);
    }
    
    public void filters(Graphics g) {
    	scaleImage();
        g.drawImage(biFiltered, 0, 0, null);
    }
    
/*    public void undof(Graphics g) {
    	//biFiltered = Undo();
        g.drawImage(bi, 0, 0, null);
    }*/
    
    public void rescaleImg(Graphics g) {
    	filterImage();      
        g.drawImage(biFiltered, 0, 0, null);
    }
    
    public void blurImg(Graphics g) {
    	blurImage();      
        g.drawImage(biFiltered, 0, 0, null);
    }
    
    public void binaryImg(Graphics g) {
    	binaryImage();      
        g.drawImage(biFiltered, 0, 0, null);
    }
 
    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
      int width = image.getWidth();
      int height = image.getHeight();

      int[][][] result = new int[width][height][4];

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int p = image.getRGB(x,y);
            int a = (p>>24)&0xff;
            int r = (p>>16)&0xff;
            int g = (p>>8)&0xff;
            int b = p&0xff;

            result[x][y][0]=a;
            result[x][y][1]=r;
            result[x][y][2]=g;
            result[x][y][3]=b;
         }
      }
      return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];
                
                //set RGB value
                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);
            }
        }
        return tmpimg;
    }

    //************************************
    //  Original
    //************************************
    
    public BufferedImage Original(BufferedImage timg, BufferedImage cimg){
        LastArray = convertToArray(cimg);
        int[][][] ImageArray = convertToArray(timg);
        return convertToBimage(ImageArray);
    }
    
    //************************************
    //  Addition
    //************************************
    public BufferedImage addition(BufferedImage timg1, BufferedImage timg2, BufferedImage cimg){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);
		LastArray = convertToArray(cimg);	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] += ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] += ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] += ImageArray2[x][y][3];  //b
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray1),false);
	}
	
	//************************************
    //  Subtraction
    //************************************
    public BufferedImage subtraction(BufferedImage timg1, BufferedImage timg2, BufferedImage cimg){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);
		LastArray = convertToArray(cimg);	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] -= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] -= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] -= ImageArray2[x][y][3];  //b
            }
        }
        
        return rescaleAll(convertToBimage(ImageArray1),false);// Convert the array to BufferedImage
    }
    
    //************************************
    //  Multiplication
    //************************************
    public BufferedImage multiplication(BufferedImage timg1, BufferedImage timg2,BufferedImage cimg){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);
		LastArray = convertToArray(cimg);	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] *= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] *= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] *= ImageArray2[x][y][3];  //b
            }
        }
        
        return rescaleAll(convertToBimage(ImageArray1),false); // Convert the array to BufferedImage
    }
    
    //************************************
    //  Division
    //************************************
    public BufferedImage division(BufferedImage timg1, BufferedImage timg2,BufferedImage cimg){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);
		LastArray = convertToArray(cimg);	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
				if (ImageArray2[x][y][1] != 0) {
					ImageArray1[x][y][1] /= ImageArray2[x][y][1];  //r
				}
				if (ImageArray2[x][y][2] != 0) {
					ImageArray1[x][y][2] /= ImageArray2[x][y][2];  //r
				}
				if (ImageArray2[x][y][3] != 0) {
					ImageArray1[x][y][3] /= ImageArray2[x][y][3];  //r
				}
            }
        }
        
        return rescaleAll(convertToBimage(ImageArray1),false);  // Convert the array to BufferedImage
    }
    
    //************************************
    //	BitAnd
    //************************************
    public BufferedImage bitAnd(BufferedImage timg1, BufferedImage timg2,BufferedImage cimg){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);
		LastArray = convertToArray(cimg);	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] &= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] &= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] &= ImageArray2[x][y][3];  //b
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray1),false);
	}
	
	//************************************
    //	Bit Or
    //************************************
    public BufferedImage bitOr(BufferedImage timg1, BufferedImage timg2,BufferedImage cimg){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);
		LastArray = convertToArray(cimg);	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] |= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] |= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] |= ImageArray2[x][y][3];  //b
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray1),false);
	}
	
	//************************************
    //	Bit XOR
    //************************************
    public BufferedImage bitXor(BufferedImage timg1, BufferedImage timg2,BufferedImage cimg){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);
		LastArray = convertToArray(cimg);	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] ^= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] ^= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] ^= ImageArray2[x][y][3];  //b
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray1),false);
	}

	//************************************
    //	Bit Not
    //************************************
    public BufferedImage bitNot(BufferedImage timg,BufferedImage cimg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);
        LastArray = convertToArray(cimg);	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
				for(int z=1; z<=3; z++){ 
                	ImageArray[x][y][z] = ~ImageArray[x][y][z];
				}
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray),false);
	}

    
    /**************** NEGATIVE IMAGE *************/
    public BufferedImage ImageNegative(BufferedImage timg, BufferedImage cimg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(cimg); //  Convert the image to array
		LastArray = convertToArray(cimg);		

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

	/***************** BIT SLICING *******************/

	public BufferedImage bitSlicing(BufferedImage cimg, int k){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg);
		int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		

        for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int r = ImageArray1[x][y][1]; //r
				int g = ImageArray1[x][y][2]; //g
				int b = ImageArray1[x][y][3]; //b
				ImageArray2[x][y][1] = ((r>>k)&1)*255; //r
				ImageArray2[x][y][2] = ((g>>k)&1)*255; //g
				ImageArray2[x][y][3] = ((b>>k)&1)*255; //b
				int xy1 = ImageArray2[x][y][1];
				int xy2 = ImageArray2[x][y][2];
				int xy3 = ImageArray2[x][y][3];
			}
		}

    	return convertToBimage(ImageArray2);
    }

	/**************** Logarithmic IMAGE *************/
    public BufferedImage logimg(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg); //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		

		int[] LUT = new int[256];
		for(int k=0; k<=255; k++){
			LUT[k] = (int)(Math.log(1+k)*255/Math.log(256));
		}

		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int r = ImageArray1[x][y][1]; //r
				int g = ImageArray1[x][y][2]; //g
				int b = ImageArray1[x][y][3]; //b
				ImageArray2[x][y][1] = LUT[r]; //r
				ImageArray2[x][y][2] = LUT[g]; //g
				ImageArray2[x][y][3] = LUT[b]; //b
			}
		}

        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

	/**************** Power Law IMAGE ***************/
    public BufferedImage powerLaw(BufferedImage cimg, float p){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg); //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		

		int[] LUT = new int[256];
		for(int k=0; k<=255; k++){
			LUT[k] = (int)(Math.pow(255,1-p)*Math.pow(k,p));
		}

		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int r = ImageArray1[x][y][1]; //r
				int g = ImageArray1[x][y][2]; //g
				int b = ImageArray1[x][y][3]; //b
				ImageArray2[x][y][1] = LUT[r]; //r
				ImageArray2[x][y][2] = LUT[g]; //g
				ImageArray2[x][y][3] = LUT[b]; //b
			}
		}

        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

	/**************** Smoothing ***************/
    public BufferedImage smooth(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg); //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		

		float[][] Mask = {
        {0.111f, 0.111f, 0.111f},
        {0.111f, 0.111f, 0.111f},
        {0.111f, 0.111f, 0.111f} 
    	};

    	// for Mask of size 3x3, no border extension
		for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){
				int r = 0; int g = 0; int b = 0;
				for(int s=-1; s<=1; s++){
					for(int t=-1; t<=1; t++){
						r = r + Math.round(Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]); //r
						g = g + Math.round(Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]); //g
						b = b + Math.round(Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]); //b
					}
				}
				ImageArray2[x][y][1] = r; //r
				ImageArray2[x][y][2] = g; //g
				ImageArray2[x][y][3] = b; //b
			}
		}

		return convertToBimage(ImageArray2);
    }
    
    /**************** Weighted Average ***************/
    public BufferedImage weighted(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg); //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		

		float[][] Mask = {
        {0.0625f, 0.125f, 0.0625f},
        {0.125f, 0.25f, 0.125f},
        {0.0625f, 0.125f, 0.0625f} 
    	};

    	// for Mask of size 3x3, no border extension
		for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){
				int r = 0; int g = 0; int b = 0;
				for(int s=-1; s<=1; s++){
					for(int t=-1; t<=1; t++){
						r = r + Math.round(Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]); //r
						g = g + Math.round(Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]); //g
						b = b + Math.round(Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]); //b
					}
				}
				ImageArray2[x][y][1] = r; //r
				ImageArray2[x][y][2] = g; //g
				ImageArray2[x][y][3] = b; //b
			}
		}

		return convertToBimage(ImageArray2);
    }
    
    /**************** 4 N Laplacian Enhance ***************/
    public BufferedImage nlaplacian(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg); //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		

		int[][] Mask = {
        {0, -1, 0},
        {-1, 5, -1},
        {0, -1, 0} 
    	};

		for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){
				int r = 0; int g = 0; int b = 0;
				for(int s=-1; s<=1; s++){
					for(int t=-1; t<=1; t++){
						r = r + (Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]); //r
						g = g + (Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]); //g
						b = b + (Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]); //b
					}
				}
				ImageArray2[x][y][1] = r; //r
				ImageArray2[x][y][2] = g; //g
				ImageArray2[x][y][3] = b; //b
			}
		}

		return convertToBimage(ImageArray2);
    }
    
    /**************** Roberts ***************/
    public BufferedImage roberts(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg); //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		

		int[][] Mask = {
        {0, 0, 0},
        {0, 0, -1},
        {0, 1, 0} 
    	};

    	// for Mask of size 3x3, no border extension
		for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){
				int r = 0; int g = 0; int b = 0;
				for(int s=-1; s<=1; s++){
					for(int t=-1; t<=1; t++){
						r = r + (Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]); //r
						g = g + (Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]); //g
						b = b + (Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]); //b
					}
				}
				ImageArray2[x][y][1] = r; //r
				ImageArray2[x][y][2] = g; //g
				ImageArray2[x][y][3] = b; //b
			}
		}

		return convertToBimage(ImageArray2);
    }
    
    /**************** Salt & Pepper *************/
    public BufferedImage saltpepper(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray = convertToArray(cimg); //  Convert the image to array
		LastArray = convertToArray(cimg);		
		Random random = new Random();  
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
            	boolean saltpepper = false;
            	int sp;
            	int z = random.nextInt(2); 
            	
            	if (z == 0){
            		saltpepper = true;
            		
            		int z1 = random.nextInt(2);
            		if (z1 == 0){
            			sp = 0;
            		}
            		else {sp = 1;};
            		
            		if (sp == 0) {
            			ImageArray[x][y][1] = 255;  //r
                		ImageArray[x][y][2] = 255;  //g
                		ImageArray[x][y][3] = 255;  //b
            		}
            		
            		else if (sp == 1) {
            			ImageArray[x][y][1] = 0;  //r
                		ImageArray[x][y][2] = 0;  //g
                		ImageArray[x][y][3] = 0;  //b
            		}
            	} 
            }
        }
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }
    
    /**************** MEDIAN *************/
    public BufferedImage median(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg);
        int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		
		
		int[] rWindow = new int[9];
		int[] gWindow = new int[9];
		int[] bWindow = new int[9];
  
        for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){
				int k = 0;
				for(int s=-1; s<=1; s++){
					for(int t=-1; t<=1; t++){
						rWindow[k] = ImageArray1[x+s][y+t][1]; //r
						gWindow[k] = ImageArray1[x+s][y+t][2]; //g
						bWindow[k] = ImageArray1[x+s][y+t][3]; //b
						k++;
					}
				}
				Arrays.sort(rWindow);
				Arrays.sort(gWindow);
				Arrays.sort(bWindow);
				ImageArray2[x][y][1] = rWindow[4]; //r
				ImageArray2[x][y][2] = gWindow[4]; //g
				ImageArray2[x][y][3] = bWindow[4]; //b
			}
		}
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

	/**************** Min Filter *************/
    public BufferedImage minfilter(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg);
        int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		
		
		int[] rWindow = new int[9];
		int[] gWindow = new int[9];
		int[] bWindow = new int[9];
  
        for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){
				int k = 0;
				for(int s=-1; s<=1; s++){
					for(int t=-1; t<=1; t++){
						rWindow[k] = ImageArray1[x+s][y+t][1]; //r
						gWindow[k] = ImageArray1[x+s][y+t][2]; //g
						bWindow[k] = ImageArray1[x+s][y+t][3]; //b
						k++;
					}
				}
				Arrays.sort(rWindow);
				Arrays.sort(gWindow);
				Arrays.sort(bWindow);
				ImageArray2[x][y][1] = rWindow[0]; //r
				ImageArray2[x][y][2] = gWindow[0]; //g
				ImageArray2[x][y][3] = bWindow[0]; //b
			}
		}
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

	/**************** Max Filter *************/
    public BufferedImage maxfilter(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg);
        int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		
		
		int[] rWindow = new int[9];
		int[] gWindow = new int[9];
		int[] bWindow = new int[9];
  
        for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){
				int k = 0;
				for(int s=-1; s<=1; s++){
					for(int t=-1; t<=1; t++){
						rWindow[k] = ImageArray1[x+s][y+t][1]; //r
						gWindow[k] = ImageArray1[x+s][y+t][2]; //g
						bWindow[k] = ImageArray1[x+s][y+t][3]; //b
						k++;
					}
				}
				Arrays.sort(rWindow);
				Arrays.sort(gWindow);
				Arrays.sort(bWindow);
				ImageArray2[x][y][1] = rWindow[8]; //r
				ImageArray2[x][y][2] = gWindow[8]; //g
				ImageArray2[x][y][3] = bWindow[8]; //b
			}
		}
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }
    
    /**************** Midpoint *************/
    public BufferedImage midpoint(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();

        int[][][] ImageArray1 = convertToArray(cimg);
        int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		
		
		int[] rWindow = new int[9];
		int[] gWindow = new int[9];
		int[] bWindow = new int[9];
  
        for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){
				int k = 0;
				for(int s=-1; s<=1; s++){
					for(int t=-1; t<=1; t++){
						rWindow[k] = ImageArray1[x+s][y+t][1]; //r
						gWindow[k] = ImageArray1[x+s][y+t][2]; //g
						bWindow[k] = ImageArray1[x+s][y+t][3]; //b
						k++;
					}
				}
				Arrays.sort(rWindow);
				Arrays.sort(gWindow);
				Arrays.sort(bWindow);
				ImageArray2[x][y][1] = Math.round((rWindow[8]+rWindow[0])/2); //r
				ImageArray2[x][y][2] = Math.round((gWindow[8]+gWindow[0])/2); //g
				ImageArray2[x][y][3] = Math.round((bWindow[8]+bWindow[0])/2); //b
			}
		}
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }
    
    /**************** HISTOGRAM *************/
    public BufferedImage histogram(BufferedImage cimg){
        int width = cimg.getWidth();
        int height = cimg.getHeight();
		
        int[][][] ImageArray1 = convertToArray(cimg);
        int[][][] ImageArray2 = convertToArray(cimg);
		LastArray = convertToArray(cimg);		
		
		//int[][] rgbSeries = new int[width*height][2];
		int[] rgbSeries = new int[width*height];
		int count = 0;
		
        for(int y=1; y<height-1; y++){
			for(int x=1; x<width-1; x++){ //Search through pixels
				
				int p = cimg.getRGB(x,y); //pixel value
				rgbSeries[count] = p;
				count += 1;
				
			}
		}
		Arrays.sort(rgbSeries);
		
		HashMap<Integer,Integer> hashmap = new HashMap<Integer,Integer>();
		for (int j = 0; j < rgbSeries.length; j++) {   
            hashmap.put(rgbSeries[j], j);   
        } 
        
        for(Map.Entry<Integer,Integer> entry : hashmap.entrySet() ){
    		System.out.println( entry.getKey() + " => " + (entry.getValue()/hashmap.size()));
		}
		
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
        
        /*BufferedImage nimg = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
                         
    	WritableRaster writeCurrent = cimg.getRaster();
    	WritableRaster er = nimg.getRaster();
    	
    	int[] histogram = new int[256];
    
    	for (int x = 0; x < writeCurrent.getWidth(); x++) {
        	for (int y = 0; y < writeCurrent.getHeight(); y++) {
            	histogram[writeCurrent.getSample(x, y, 0)]++;
        	}
    	}
    
    	int[] updated = new int[256];
    	updated[0] = histogram[0];
    	
    	for(int i=1;i<256;i++){
        	updated[i] = updated[i-1] + histogram[i];
    	}

    	float[] arr = new float[256];
    	for(int i=0;i<256;i++){
        	arr[i] =  (float)((updated[i]*255.0)/(float)(width*height));
    	}

	 	for (int x = 0; x < writeCurrent.getWidth(); x++) {
        	for (int y = 0; y < writeCurrent.getHeight(); y++) {
        	    int nVal = (int) arr[writeCurrent.getSample(x, y, 0)];
        	    er.setSample(x, y, 0, nVal);
        	}
    	}
    	nimg.setData(er);
    	return nimg;
    	*/
    
    }

	/*********************** UNDO *********************/
	public BufferedImage Undo(){
        return convertToBimage(LastArray);  // Convert the array to BufferedImage
    }
    
    /********* RESCALE COLOUR *********/

    public BufferedImage rescaleColour(BufferedImage timg, float scale){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg); //  Convert the image to array
		LastArray = convertToArray(timg);	// Store value of array for the undo function	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                float xy1 = ImageArray[x][y][1]*scale;  //r

                float xy2 = ImageArray[x][y][2]*scale;  //g

                float xy3 = ImageArray[x][y][3]*scale;  //b
                if(xy1 > 255){
                	xy1 = 255;
                }
                if(xy2 > 255) {
                	xy2 = 255;
                }
                if(xy3 > 255) {
                	xy3 = 255;
                }
                ImageArray[x][y][1] = Math.round(xy1);
                ImageArray[x][y][2] = Math.round(xy2);
                ImageArray[x][y][3] = Math.round(xy1);
            }
        }
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }
    
   	/********* SHIFTING COLOUR *********/

    public BufferedImage shiftColour(BufferedImage timg, int shift){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg); //  Convert the image to array
		LastArray = convertToArray(timg);	// Store value of array for the undo function	

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1]+= shift;  //r
                ImageArray[x][y][2]+= shift;  //g
                ImageArray[x][y][3]+=shift;  //b
                if(ImageArray[x][y][1] > 255){
                	ImageArray[x][y][1] = 255;
                }
                else if(ImageArray[x][y][1] < 0) {
                	ImageArray[x][y][1] = 0;
                }
                if(ImageArray[x][y][2] > 255) {
                	ImageArray[x][y][2] = 255;
                }
                else if(ImageArray[x][y][2] < 0) {
                	ImageArray[x][y][2] = 0;
                }
                if(ImageArray[x][y][3] > 255) {
                	ImageArray[x][y][3] = 255;
                }
                else if(ImageArray[x][y][3] < 0) {
                	ImageArray[x][y][3] = 0;
                }
            }
        }
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    /************************* SHIFTING & RESCALING ************************/

    public BufferedImage rescaleAll(BufferedImage timg, boolean save){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg); //  Convert the image to array
        
        if (save) {
			LastArray = convertToArray(timg);	// Store value of array for the undo function	
		}

        int rint = (int)(Math.random() * 256);
        int gint = (int)(Math.random() * 256);
        int bint = (int)(Math.random() * 256);

        int rmin = ImageArray[0][0][1]+rint; int rmax = rmin;
        int gmin = ImageArray[0][0][2]+gint; int gmax = gmin;
        int bmin = ImageArray[0][0][3]+bint; int bmax = bmin;

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray[x][y][1]+=rint; //r
                ImageArray[x][y][2]+=gint; //g
                ImageArray[x][y][3]+=bint; //b
                if (rmin>ImageArray[x][y][1]) { 
                    rmin = ImageArray[x][y][1]; 
                }
                if (gmin>ImageArray[x][y][2]) { 
                    gmin = ImageArray[x][y][2]; 
                }
                if (bmin>ImageArray[x][y][3]) { 
                    bmin = ImageArray[x][y][3]; 
                }
                if (rmax<ImageArray[x][y][1]) { 
                    rmax = ImageArray[x][y][1]; 
                }
                if (gmax<ImageArray[x][y][2]) { 
                    gmax = ImageArray[x][y][2]; 
                }
                if (bmax<ImageArray[x][y][3]) { 
                    bmax = ImageArray[x][y][3]; 
                }
            }
        }

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
				if ((rmax-rmin) == 0) {
					rmax+=1;
				}
				if ((gmax-gmin) == 0) {
					gmax+=1;
				}
				if ((bmax-bmin) == 0) {
					bmax+=1;
				}
                ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
                ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
                ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
            
    }
    
    //************************************
    //  FILTER MENU
    //************************************
    public void filterImage() {
 
        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
        case 1: biFiltered = Original(bi, biFiltered); /* original */
                return; 
        case 2: biFiltered = ImageNegative(bi, biFiltered); /* Image Negative */
                return;
        case 3: biFiltered = Undo(); //Undo
                return;
        case 4: biFiltered = shiftColour(biFiltered, 20); //Shift 20
                return; 
        case 5: biFiltered = shiftColour(biFiltered, -20); //Shift -20
                return;
        case 6: biFiltered = rescaleAll(biFiltered, true);
                return;
		case 7: biFiltered = logimg(biFiltered); //log
                return;
		case 8: Scanner userpower = new Scanner(System.in); 
    			System.out.println("Enter power level");
				float upower = userpower.nextFloat();
				biFiltered = powerLaw(biFiltered, upower); //power
                return;
		case 9: Scanner bitplane = new Scanner(System.in); 
    			System.out.println("Enter plane to slice (8 bit image = 7 planes)");
				int plane = bitplane.nextInt();
				biFiltered = bitSlicing(biFiltered, plane); //bit slicing
                return; 
        case 10: biFiltered = saltpepper(biFiltered); //salt & pepper
                return;
        case 11: biFiltered = median(biFiltered); //median
                return;
		case 12: biFiltered = minfilter(biFiltered); //min
                return;
        case 13: biFiltered = maxfilter(biFiltered); //max
                return;
        case 14: biFiltered = midpoint(biFiltered); //midpoint
                return;
        case 15: biFiltered = histogram(biFiltered); //midpoint
                return;
        case 16: biFiltered = Original(bi2, biFiltered); /* original */
                return; 
        } 
    }
    /********** SCALE MENU **********/
    public void scaleImage() {
 
        if (scIndex == lastSc) {
            return;
        }

        lastSc = scIndex;
        switch (scIndex) {
        case 1: biFiltered = rescaleColour(biFiltered, 0.5f); //Scale 0.5
                return; 
        case 2: biFiltered = rescaleColour(biFiltered, 1.5f); //Scale 1.5
                return;
        case 3: biFiltered = rescaleColour(biFiltered, 2f);  //Scale 2
                return;
        } 
    }
    
    /********** BLUR MENU **********/
    public void blurImage() {
        if (blIndex == lastBl) {
            return;
        }

        lastBl = blIndex;
        switch (blIndex) {
        case 1: biFiltered = smooth(biFiltered); //log
        		return;
        case 2: biFiltered = weighted(biFiltered); //log
        		return; 
        case 3: biFiltered = nlaplacian(biFiltered); //log
        		return;
      	case 4: biFiltered = roberts(biFiltered); //log
        		return;
        } 
    }
    
    public void binaryImage() {
        if (binIndex == lastBin) {
            return;
        }

        lastBin = binIndex;
        switch (binIndex) {
        case 1: biFiltered = addition(bi,bi2,biFiltered); 
                return;
		case 2: biFiltered = subtraction(bi,bi2,biFiltered);
                return;
		case 3: biFiltered = multiplication(bi,bi2,biFiltered);
                return;
		case 4: biFiltered = division(bi,bi2,biFiltered);
                return;
		case 5: biFiltered = bitAnd(bi,bi2,biFiltered);
                return;
		case 6: biFiltered = bitOr(bi,bi2,biFiltered); 
                return;
		case 7: biFiltered = bitXor(bi,bi2,biFiltered);
                return;
		case 8: biFiltered = bitNot(bi,biFiltered); 
                return;
        case 9: biFiltered = Original(bi2, biFiltered); /* original */
                return; 
        } 
    }
    
    /****** LISTENING ACTIONS FOR MENU ********/
 
     public void actionPerformed(ActionEvent e) {
         JComboBox cb = (JComboBox)e.getSource();
         /*JButton b = (JButton)e.getSource();
         
         if (b.getActionCommand().equals("Undo")) {
             
             repaint();
         } */
         
         if (cb.getActionCommand().equals("SetFilter")) {
             setOpIndex(cb.getSelectedIndex());
             repaint();
         } 
         
         else if (cb.getActionCommand().equals("SetScale")) {
             setScIndex(cb.getSelectedIndex());
             repaint();
         } 
         
         else if (cb.getActionCommand().equals("SetBlur")) {
             setBlIndex(cb.getSelectedIndex());
             repaint();
         } 
         
         else if (cb.getActionCommand().equals("SetBinary")) {
             setBinIndex(cb.getSelectedIndex());
             repaint();
         } 
         
		 else if (cb.getActionCommand().equals("Formats")) {
             String format = (String)cb.getSelectedItem();
             File saveFile = new File("savedimage."+format);
             JFileChooser chooser = new JFileChooser();
             chooser.setSelectedFile(saveFile);
             int rval = chooser.showSaveDialog(cb);
             if (rval == JFileChooser.APPROVE_OPTION) {
                 saveFile = chooser.getSelectedFile();
                 try {
                     ImageIO.write(biFiltered, format, saveFile);
                 } 
		 		catch (IOException ex) {
                 }
             }
         }
    };
 
    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        Demo de = new Demo();
        f.add("Center", de);

        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        
        JComboBox scales = new JComboBox(de.getScales());
        scales.setActionCommand("SetScale");
        scales.addActionListener(de);
        
        JComboBox blurs = new JComboBox(de.getBlur());
        blurs.setActionCommand("SetBlur");
        blurs.addActionListener(de);
        
        JComboBox binarys = new JComboBox(de.getBinary());
        binarys.setActionCommand("SetBinary");
        binarys.addActionListener(de);
        
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);

        JButton b1 = new JButton("Original");
		b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame fx = new JFrame("Image Processing Demo");
                Demo old = new Demo();
                fx.add("Center", old);
                fx.pack();
        		fx.setVisible(true);
            }
		});
		
		/*JButton b2 = new JButton("Undo");
		b2.setActionCommand("Undo");
		b2.addActionListener(de);*/

        JPanel panel = new JPanel();
		panel.add(new JLabel("Filter"));
        panel.add(choices);
        panel.add(new JLabel("Scales"));
        panel.add(scales);
        panel.add(new JLabel("Blur"));
        panel.add(blurs);
        panel.add(new JLabel("Binary"));
        panel.add(binarys);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
		panel.add(b1);
		//panel.add(b2);

        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}
