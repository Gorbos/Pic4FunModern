package jpact.pic4funmodern.util.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

import jpact.pic4funmodern.util.*;

public class PhotoEffects {
	public static Bitmap result; 
	public static  Bitmap frameResource;
	public static Bitmap frame;
	public static Canvas canvas;
	
	//Tint
  	public static final double PI = 3.14159d;
  	public static final double FULL_CIRCLE_DEGREE = 360d;
  	public static final double HALF_CIRCLE_DEGREE = 180d;
  	public static final double RANGE = 256d;
  	//Sharpen from blog
  	final static int KERNAL_WIDTH = 3;
  	final static int KERNAL_HEIGHT = 3;
  	int[][] kernalBlur = {
  	   {0, -1, 0},
  	   {-1, 5, -1},
  	   {0, -1, 0}
  	};
	
	/***
	 * 
	 * Image Effects
	 * 
	 */
	
	//Sharpen
	public static Bitmap doSharpenToPhoto(Bitmap src, double weight) {
		double[][] SharpConfig = new double[][] {
			{ 0 , -2    , 0  },
			{ -2, weight, -2 },
			{ 0 , -2    , 0  }
		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(SharpConfig);
		convMatrix.Factor = weight - 8;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}
	
	//Sepia
	public static Bitmap doSepiaToPhoto(Bitmap src, int depth, double red, double green, double blue) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// constant grayscale
		final double GS_RED = 0.3;
		final double GS_GREEN = 0.59;
		final double GS_BLUE = 0.11;
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				// get color on each channel
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// apply grayscale sample
				B = G = R = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);

				// apply intensity level for sepid-toning on each channel
				R += (depth * red);
				if(R > 255) { R = 255; }

				G += (depth * green);
				if(G > 255) { G = 255; }

				B += (depth * blue);
				if(B > 255) { B = 255; }

				// set new pixel color to output image
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}
	
	//Sharpen 2
	private Bitmap doSharpenToPhoto(Bitmap src, int[][] knl) {
		Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
		    
		int bmWidth = src.getWidth();
		int bmHeight = src.getHeight();
		int bmWidth_MINUS_2 = bmWidth - 2;
		int bmHeight_MINUS_2 = bmHeight - 2;
		      
		for (int i = 1; i <= bmWidth_MINUS_2; i++) {
			for (int j = 1; j <= bmHeight_MINUS_2; j++) {
				//get the surround 3*3 pixel of current src[i][j] into a matrix subSrc[][]
				int[][] subSrc = new int[KERNAL_WIDTH][KERNAL_HEIGHT];
				for(int k = 0; k < KERNAL_WIDTH; k++) {
					for(int l = 0; l < KERNAL_HEIGHT; l++) {
						subSrc[k][l] = src.getPixel(i-1+k, j-1+l);
		    		}
		    	}
		    	//subSum = subSrc[][] * knl[][]
		    	int subSumA = 0;
		    	int subSumR = 0;
		    	int subSumG = 0;
		    	int subSumB = 0;
		 
		    	for (int k = 0; k < KERNAL_WIDTH; k++) {
		    		for (int l = 0; l < KERNAL_HEIGHT; l++) {
		    			subSumA += Color.alpha(subSrc[k][l]) * knl[k][l];
		    			subSumR += Color.red(subSrc[k][l]) * knl[k][l];
		    			subSumG += Color.green(subSrc[k][l]) * knl[k][l];
		    			subSumB += Color.blue(subSrc[k][l]) * knl[k][l];
		    		}
		    	}
		        
		    	if (subSumA<0) {
		    		subSumA = 0;
		    	} else if (subSumA>255) {
		    		subSumA = 255;
		    	}
		        
		    	if (subSumR<0) {
		    		subSumR = 0;
		    	} else if (subSumR>255) {
		    		subSumR = 255;
		    	}
		        
		    	if (subSumG<0) {
		    		subSumG = 0;
		    	} else if (subSumG>255) {
		    		subSumG = 255;
		    	}
		        
		    	if (subSumB<0) {
		    		subSumB = 0;
		    	} else if (subSumB>255) {
		    		subSumB = 255;
		    	}
		 
		    	dest.setPixel(i, j, Color.argb(subSumA, subSumR, subSumG, subSumB));
		    } 
		}
		      
		return dest;
	}
	
	//Invert
	public static Bitmap doInvertToPhoto(Bitmap src) {
		// create new bitmap with the same settings as source bitmap
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
		// color info
		int A, R, G, B;
		int pixelColor;
		// image size
		int height = src.getHeight();
		int width = src.getWidth();

		// scan through every pixel
	    for (int y = 0; y < height; y++)
	    {
	        for (int x = 0; x < width; x++)
	        {
	        	// get one pixel
	            pixelColor = src.getPixel(x, y);
	            // saving alpha channel
	            A = Color.alpha(pixelColor);
	            // inverting byte for each R/G/B channel
	            R = 255 - Color.red(pixelColor);
	            G = 255 - Color.green(pixelColor);
	            B = 255 - Color.blue(pixelColor);
	            // set newly-inverted pixel to output image
	            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
	        }
	    }

	    // return final bitmap
	    return bmOut;
	}
	
	//Tint
	public static Bitmap doTintToPhoto(Bitmap src, int degree) {

	    int width = src.getWidth();
		int height = src.getHeight();

	    int[] pix = new int[width * height];
	    src.getPixels(pix, 0, width, 0, 0, width, height);

	    int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
	    double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;
	   
	    int S = (int)(RANGE * Math.sin(angle));
	    int C = (int)(RANGE * Math.cos(angle));

	    for (int y = 0; y < height; y++)
	        for (int x = 0; x < width; x++) {
		    	int index = y * width + x;
		    	int r = ( pix[index] >> 16 ) & 0xff;
		    	int g = ( pix[index] >> 8 ) & 0xff;
		    	int b = pix[index] & 0xff;
		    	RY = ( 70 * r - 59 * g - 11 * b ) / 100;
		    	GY = (-30 * r + 41 * g - 11 * b ) / 100;
		    	BY = (-30 * r - 59 * g + 89 * b ) / 100;
		    	Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
		    	RYY = ( S * BY + C * RY ) / 256;
		    	BYY = ( C * BY - S * RY ) / 256;
		    	GYY = (-51 * RYY - 19 * BYY ) / 100;
		    	R = Y + RYY;
		    	R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
		    	G = Y + GYY;
		    	G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
		    	B = Y + BYY;
		    	B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
		    	pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
	    	}
	    
	    Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());	   
	    outBitmap.setPixels(pix, 0, width, 0, 0, width, height);
	   
	    pix = null;
	   
	    return outBitmap;
	}
	
	//Engrave
	public static Bitmap doEngraveToPhoto(Bitmap src) {
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.setAll(0);
		convMatrix.Matrix[0][0] = -2;
		convMatrix.Matrix[1][1] = 2;
		convMatrix.Factor = 1;
		convMatrix.Offset = 95;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}
	
	//Contrast
	public static Bitmap doContrastToPhoto(Bitmap src, double value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;
		// get contrast value
		double contrast = Math.pow((100 + value) / 100, 2);

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				// apply filter contrast for every channel R, G, B
				R = Color.red(pixel);
				R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(R < 0) { R = 0; }
				else if(R > 255) { R = 255; }

				G = Color.red(pixel);
				G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(G < 0) { G = 0; }
				else if(G > 255) { G = 255; }

				B = Color.red(pixel);
				B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(B < 0) { B = 0; }
				else if(B > 255) { B = 255; }

				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}
	
	//Round Corner
	public static Bitmap doRoundCornerToPhoto(Bitmap src, float round) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create bitmap output
	    Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	    // set canvas for painting
	    Canvas canvas = new Canvas(result);
	    canvas.drawARGB(0, 0, 0, 0);

	    // config paint
	    final Paint paint = new Paint();
	    paint.setAntiAlias(true);
	    paint.setColor(Color.BLACK);

	    // config rectangle for embedding
	    final Rect rect = new Rect(0, 0, width, height);
	    final RectF rectF = new RectF(rect);

	    // draw rect to canvas
	    canvas.drawRoundRect(rectF, round, round, paint);

	    // create Xfer mode
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    // draw source image to canvas
	    canvas.drawBitmap(src, rect, rect, paint);

	    // return final image
	    return result;
	}
	
	//Color Boost Up
	public static Bitmap doBoostToPhoto(Bitmap src, int type, float percent) {
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

		int A, R, G, B;
		int pixel;

		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				if(type == 1) {
					R = (int)(R * (1 + percent));
					if(R > 255) R = 255;
				}
				else if(type == 2) {
					G = (int)(G * (1 + percent));
					if(G > 255) G = 255;
				}
				else if(type == 3) {
					B = (int)(B * (1 + percent));
					if(B > 255) B = 255;
				}
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		return bmOut;
	}
	
	//Hue Filter
	public static Bitmap doHueToPhoto(Bitmap source, int level) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		float[] HSV = new float[3];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);
		
		int index = 0;
		// iteration through pixels
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;				
				// convert to HSV
				Color.colorToHSV(pixels[index], HSV);
				// increase Saturation level
				HSV[0] *= level;
				HSV[0] = (float) Math.max(0.0, Math.min(HSV[0], 360.0));
				// take color back
				pixels[index] |= Color.HSVToColor(HSV);
			}
		}
		// output bitmap				
		Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;			
	}
	
	//Saturation Filter
	public static Bitmap doSaturationToPhoto(Bitmap source, int level) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		float[] HSV = new float[3];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);

		int index = 0;
		// iteration through pixels
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// convert to HSV
				Color.colorToHSV(pixels[index], HSV);
				// increase Saturation level
				HSV[1] *= level;
				HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
				// take color back
				pixels[index] |= Color.HSVToColor(HSV);
			}
		}
		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}
	
	//Shading Filter
	public static Bitmap doShadingToPhoto(Bitmap source, int shadingColor) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);

		int index = 0;
		// iteration through pixels
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// AND
				pixels[index] &= shadingColor;
			}
		}
		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}
	
	public static Bitmap doFrame(Bitmap source, int frameID, Context context){
		// image size
		int width = source.getWidth();
		int height = source.getHeight();
		// create bitmap output new Matrix()
		result = Bitmap.createBitmap(width, height, source.getConfig());

	    frameResource = BitmapFactory.decodeResource(context.getResources(), frameID);
	    
		frame = Bitmap.createScaledBitmap(frameResource, width, height, true);
	    // set canvas for overlaying
	    canvas = new Canvas(result);
	    canvas.drawBitmap(source, new Matrix(), null);
	    canvas.drawBitmap(frame, 0, 0,null);
	    // return final image
	    return result;
	}

}
