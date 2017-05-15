
package com.ocr;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.servlet.http.Part;

import org.apache.commons.net.ftp.FTPClient;
import sun.net.www.URLConnection;




public class Main {
    static Perceptron perceptron;  
    static 	Vector<Character> resul=new Vector<Character>();
    private static final int BUFFER_SIZE = 4096;
    static int n_in,n_out;
  
    static String hiddenTF1[] =new String [3];
    static String hh1;

    
    public static String main(String hh, InputStream  input, String savePath){
    
    	Sample sample=new Sample(hh);

    	hh1=hh;
        n_in=8*sample.MAX_DIGIT;
        n_out=sample.MAX_DIGIT;
    
        try {
			initGui();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        initPerceptron();
		setSample();
        learn();
      
      CharacterExtractor.slice( input);
      PrintArray();
	return resul.toString();
     
    }
    public static void initGui() throws IOException {
    	hiddenTF1[0]="10";
    	hiddenTF1[1]="";
    	hiddenTF1[2] ="";
    	}
    	
    

    public static void AddToArray(Object c)
    { String p=c.getClass().getName();
    	if(c.getClass().getName()=="java.lang.Integer")
    		resul.add(c.toString().charAt(0));
    	if(c.getClass().getName()=="java.lang.Character")
        	resul.add((Character) c);
    	
    }
    public static void PrintArray()
    {
        Collections.reverse(resul);
        System.out.println(resul);

    	
    }
    
    public static void initPerceptron()
    {
	int hid[] = new int[3];
	int nLayer,i,j,k;
	String text;
	String text1;

	perceptron = new Perceptron(n_in,n_out);
	nLayer=0;
	for(i=0;i<3;i++)
	    {
		text = hiddenTF1[i];

		if ("".equals(text)) hid[i]=0;
		else hid[i] = (Integer.valueOf(text)).intValue();
		if (hid[i]!=0)
		    {
			String s = "H" + String.valueOf(i) + "|";
			perceptron.addLayer(hid[i],s);
			nLayer++;
		    }
	    }
	for(j=0;j<nLayer;j++)
	    for(i=0;i<hid[j];i++) perceptron.biasConnect(j+1,i);
	perceptron.biasConnect(nLayer+1,0); // for the output

	if (nLayer==0)
	    for(i=0;i<n_in;i++) for(j=0;j<n_out;j++)
		perceptron.connect(0,i,1,j);
	else
	    {
		for(i=0;i<hid[0];i++) for(j=0;j<n_in;j++)
		    perceptron.connect(0,j,1,i);
		for(k=0;k<nLayer-1;k++) for(i=0;i<hid[k];i++) for(j=0;j<hid[k+1];j++)
		    perceptron.connect(k+1,i,k+2,j);
		for(i=0;i<hid[nLayer-1];i++) for(j=0;j<n_out;j++)
		    perceptron.connect(nLayer,i,nLayer+1,j);
	    }
    }
    public static void setSample()
    {
	Vector<Double> iS;
	Vector oS;
	int i,j,k,l;
	double s;
        Sample sample=new Sample(hh1);
	for(l=0;l<sample.MAX_DIGIT;l++) {
	    for(k=0;k<sample.MAX_SAMPLE;k++) {
		iS = new Vector();
		oS = new Vector();
		for(j=0;j<sample.MAX_DIGIT;j++) {
		    if (j==l) oS.addElement(new Double(1.0));
		    else oS.addElement(new Double(0.0));
		}
		for(j=0;j<20;j+=2) {
		    for(i=0;i<15;i+=2) {
			s = sample.input[l][k][i][j] + sample.input[l][k][i][j+1];
                        //System.out.println("("+sample.input[l][k][i][j] +" "+ sample.input[l][k][i][j+1]+")\n");
			if (i!=14) {
			    s += sample.input[l][k][i+1][j] + sample.input[l][k][i+1][j+1];
			    s /= 4.0;
			} else s /= 2.0;
			iS.addElement(new Double(s));
		    }
		}
		perceptron.addSample(iS,oS);
	    }
	}
    }
    @SuppressWarnings("deprecation")
	public static void learn()
    {
	setSample();
    Neuron.learningRate = 0.2;
    Neuron.momentum=0.9;
    int max = 10;

        for(int i = 0;i < max; i++)
	    {
		perceptron.learn(10,0.2);
		
	    }
        perceptron.print();
    }
  
    
    
    
    
    @SuppressWarnings("unchecked")
    public static void test(double g[][])
    {
	Vector<Double> iS,oS;
	int i,k;
	double s;
    double matrix[][] = new double[11][11];  
    iS = new Vector<Double>();
	
	for(k=0;k<19;k+=2)
		for(i=0;i<15;i+=2) {
			
			s = g[i][k] + g[i][k+1];
		    if (i!=14) {
			s += g[i+1][k] + g[i+1][k+1];
			s /= 4.0;
		    }
		    else s /= 2.0;
		    iS.addElement(new Double(s));
		    matrix[i/2][k/2]=s;
			
	}
	
double max=0;
int l=0;
	oS = perceptron.recognize(iS);
	for ( i=0 ; i<oS.size();i++){
		
	double h=oS.get(i);
	if (h<1){
		 if (h>max) {
				l=i;
				max = h;
			    }
			}
	}
	String alpha [] ={"à","á","â","ã","ä","å","æ","ç","è","é","ë","ì","î","ð","ñ","ò","ô","ö","÷"};

	 
		if (l<10){
    System.out.println("  choose :  "+l+" ");
	char b = (char) l;
	Main.AddToArray(l);
		}
    else{
		    System.out.println("  choose :  "+alpha[l-10]+" ");
	    	Main.AddToArray((alpha[l-10]).charAt(0));
		}
    }
   
}

    
