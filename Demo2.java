import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
 
public class Demo extends Component implements ActionListener {
    
    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************
  
    String descs[] = {
        "Original1",
		"Original2", 
        "Addition",
		"Subtraction",
		"Multiplication",
		"Division",
		"Bit AND",
		"Bit OR",
		"Bit XOR",
		"Bit NOT",
    };
 
    int opIndex;  //option index for 
    int lastOp;

    private BufferedImage bi1, bi2, biFiltered;   // the input image saved as bi;//
    int w1, h1, w2, h2;
     
    public Demo() {
        try {
            bi1 = ImageIO.read(new File("default.jpg"));
	    	bi2 = ImageIO.read(new File("default2.jpg"));

            w1 = bi1.getWidth(null);
            h1 = bi1.getHeight(null);
	    	w2 = bi2.getWidth(null);
            h2 = bi2.getHeight(null);

			if (w1 != w2) {
				System.out.println("Incompatible");
				System.exit(0);
			}

			if (w1 != w2) {
				System.out.println("Incompatible");
				System.exit(0);
			}

            System.out.println(bi1.getType());
	    	System.out.println(bi2.getType());

            if (bi2.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage biNew = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);
                Graphics big = biNew.getGraphics();
                big.drawImage(bi2, 0, 0, null);
                bi2 = biNew;
            }
			if (bi1.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage biNew = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_RGB);
                Graphics big = biNew.getGraphics();
                big.drawImage(bi1, 0, 0, null);
                bi1 = biNew;
            }

			biFiltered = bi1;

        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }                         
 
    public Dimension getPreferredSize() {
        return new Dimension(w1, h1);
    }
 

    String[] getDescriptions() {
        return descs;
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
 
    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();      
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
    //  Addition
    //************************************
    public BufferedImage addition(BufferedImage timg1, BufferedImage timg2){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] += ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] += ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] += ImageArray2[x][y][3];  //b
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray1));
	}

    //************************************
    //  Subtraction
    //************************************
    public BufferedImage subtraction(BufferedImage timg1, BufferedImage timg2){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] -= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] -= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] -= ImageArray2[x][y][3];  //b
            }
        }
        
        return rescaleAll(convertToBimage(ImageArray1));// Convert the array to BufferedImage
    }

    //************************************
    //  Multiplication
    //************************************
    public BufferedImage multiplication(BufferedImage timg1, BufferedImage timg2){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] *= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] *= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] *= ImageArray2[x][y][3];  //b
            }
        }
        
        return rescaleAll(convertToBimage(ImageArray1)); // Convert the array to BufferedImage
    }

    //************************************
    //  Division
    //************************************
    public BufferedImage division(BufferedImage timg1, BufferedImage timg2){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

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
        
        return rescaleAll(convertToBimage(ImageArray1));  // Convert the array to BufferedImage
    }

	//************************************
    //	BitAnd
    //************************************
    public BufferedImage bitAnd(BufferedImage timg1, BufferedImage timg2){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] &= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] &= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] &= ImageArray2[x][y][3];  //b
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray1));
	}

	//************************************
    //	Bit Or
    //************************************
    public BufferedImage bitOr(BufferedImage timg1, BufferedImage timg2){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] |= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] |= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] |= ImageArray2[x][y][3];  //b
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray1));
	}

	//************************************
    //	Bit XOR
    //************************************
    public BufferedImage bitXor(BufferedImage timg1, BufferedImage timg2){
        int width = timg1.getWidth();
        int height = timg1.getHeight();

        int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1[x][y][1] ^= ImageArray2[x][y][1];  //r
                ImageArray1[x][y][2] ^= ImageArray2[x][y][2];  //g
                ImageArray1[x][y][3] ^= ImageArray2[x][y][3];  //b
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray1));
	}

	//************************************
    //	Bit Not
    //************************************
    public BufferedImage bitNot(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
				for(int z=1; z<=3; z++){ 
                	ImageArray[x][y][z] = ~ImageArray[x][y][z];
				}
            }
         }
        
        return rescaleAll(convertToBimage(ImageArray));
	}

	public BufferedImage rescaleAll(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg); //  Convert the image to array	

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
    //  You need to register your functioin here
    //************************************
    public void filterImage() {
 
        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
        case 0: biFiltered = bi1; /* original */
                return; 
		case 1: biFiltered = bi2; /* original */
                return; 
        case 2: biFiltered = addition(bi1,bi2); /* Image Negative */
                return;
		case 3: biFiltered = subtraction(bi1,bi2); /* Image Negative */
                return;
		case 4: biFiltered = multiplication(bi1,bi2); /* Image Negative */
                return;
		case 5: biFiltered = division(bi1,bi2); /* Image Negative */
                return;
		case 6: biFiltered = bitAnd(bi1,bi2); /* Image Negative */
                return;
		case 7: biFiltered = bitOr(bi1,bi2); /* Image Negative */
                return;
		case 8: biFiltered = bitXor(bi1,bi2); /* Image Negative */
                return;
		case 9: biFiltered = bitNot(bi1); /* Image Negative */
                return;
        }
    }
 
     public void actionPerformed(ActionEvent e) {
         JComboBox cb = (JComboBox)e.getSource();
         if (cb.getActionCommand().equals("SetFilter")) {
             setOpIndex(cb.getSelectedIndex());
             repaint();
         } else if (cb.getActionCommand().equals("Formats")) {
             String format = (String)cb.getSelectedItem();
             File saveFile = new File("savedimage."+format);
             JFileChooser chooser = new JFileChooser();
             chooser.setSelectedFile(saveFile);
             int rval = chooser.showSaveDialog(cb);
             if (rval == JFileChooser.APPROVE_OPTION) {
                 saveFile = chooser.getSelectedFile();
                 try {
                     ImageIO.write(biFiltered, format, saveFile);
                 } catch (IOException ex) {
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
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}
