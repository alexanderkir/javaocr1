package com.ocr;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;


class Sample {
	public final String path;

	public double input[][][][];
	public final int MAX_SAMPLE = 10;
	public final int MAX_DIGIT = 27;
	InputStream in;
	InputStreamReader isr;
	BufferedReader file;
	String line;

	public Sample(String hh) {
		input = new double[MAX_DIGIT][MAX_SAMPLE][15][20];
		path=hh;
		openSampleFile();
	}

	void openSampleFile() {
		int i = 0, j = 0, k = 0, l = 0;
		byte b[];
		char c;

		b = new byte[1];

		try {
			
			HttpServletRequest request = null;
			
			 
            in = new BufferedInputStream(new FileInputStream(path+"//input.dat"));
 
            isr = new InputStreamReader(in);
            file = new BufferedReader(isr);

			for (l = 0; l < MAX_DIGIT; l++) {
				for (k = 0; k < MAX_SAMPLE; k++) {
					for (j = 0; j < 20; j++) {
						line = file.readLine();
						for (i = 0; i < 15; i++) {
							c = line.charAt(i);
							if (c == 'X') {
								input[l][k][i][j] = 1.0;
							} else if (c == '.') {
								input[l][k][i][j] = 0.0;
							}
						}
					}
					line = file.readLine();
				}
				line = file.readLine();
			}
		} catch (Exception e) {
			System.err.println("Error when loading file input.dat: " + e + "lkij=" + l + " " + k + " " + i + " " + j);
		}

	}
}
