package com.pirates.game;
import java.lang.Math;
import java.util.Random;
/**
 * this class is just used to generate an array of perlin noise.
 * It also will contain a function to display this noise just for testing.
 * @author Elijah
 *
 */
class PerlinNoiseGen {
	private static PerlinNoiseGen instatiated = null;
	private int powerSize; //in the form of a power of 2, i.e. 2 means a 4x4 grid, 8 means a 256x256 grid
	private int realSize;
	private Random rand;
	/*Persistence, rate of change, and octave weight affect the weight of each octave
	 * center weighting is the power by which the expression
	 * y(y_range-y) and x(x_range-x) are raised to.
	 */
	private final double PERSISTANCE = .6;
	private final double RATEOFCHANGE = .8;
	private final double OCTAVEWEIGHTCONSTANT = 4.0;//if the distribution is good but the magnitude is off, change this one
	private final double CENTERWEIGHTING = .5;
	private PerlinNoiseGen() {
		rand = new Random();
	}

	static PerlinNoiseGen init() {
		if (instatiated == null) {
			instatiated = new PerlinNoiseGen();

		} else {
			
		}
		return instatiated;
	}
	/** interpolate between 4 squares given the x and y ratios of the location to there centers
	 * 	called by genOctave to get values for each point*/
	private double linInterpolate(double v11, double v21, double v12, double v22,  double xRatio, double yRatio) {
		return (v11*(1-xRatio)*(1-yRatio) + 
				v21*(xRatio)*(1-yRatio) + 
				v12*(1-xRatio)*(yRatio) + 
				v22*(xRatio)*(yRatio));
	}
	/**make a empty array of size powerOfTwo*/
	double[][] genEmptyArray(int pSize) {
		int size = (int) Math.pow(2,pSize);
		double [][] noiseArray = new double[size][size];
		return noiseArray;
	} 
	/** return an array with smoothed noise of size 2^octave
	 * 	as such should start with octave 0 up to octave powerSize*/
	double[][] genOctave(int octave) {
		double v11;
		double v21;
		double v12;
		double v22;
		double yRatio; 
		double xRatio;
		double[][] returnArray = genEmptyArray(powerSize);
		double[][] quantisedNoise = genEmptyArray(octave);
		int quantisedSize = quantisedNoise.length;
		int squareSize = (int) (realSize/quantisedSize);
		for (int i=0; i<quantisedSize; i++) {
			for (int j=0; j<quantisedSize; j++) {
				quantisedNoise[i][j] = rand.nextDouble();
			}
		}
		for (int i=0; i<realSize; i++) {//sorry this is ugly, its just to check that we are not trying to get a pixel thats out of the array
			for (int j=0; j<realSize; j++) {// there is probably a better way to do it, but its not important.
				if ((int) (i/squareSize + 1) < quantisedSize && (int) (j/squareSize + 1) < quantisedSize ) {
					v22 = quantisedNoise[(int) (i/squareSize + 1)][(int) (j/squareSize + 1)];
					v21 = quantisedNoise[(int) (i/squareSize + 1)][(int) ( j/squareSize)];
					v12 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize + 1)];
					v11 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize)];
				} else if ((int) (i/squareSize + 1) < quantisedSize) {
					v22 = quantisedNoise[(int) (i/squareSize + 1)][(int) (j/squareSize)];
					v21 = quantisedNoise[(int) (i/squareSize + 1)][(int) ( j/squareSize)];
					v12 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize)];
					v11 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize)];
				} else if ((int) (j/squareSize + 1) < quantisedSize) {
					v22 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize + 1)];
					v21 = quantisedNoise[(int) (i/squareSize)][(int) ( j/squareSize)];
					v12 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize + 1)];
					v11 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize)];
				} else {
					v22 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize)];
					v21 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize)];
					v12 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize)];
					v11 = quantisedNoise[(int) (i/squareSize)][(int) (j/squareSize)];
				}
				yRatio = ((double) (j%squareSize)) / squareSize;
				xRatio = ((double) (i%squareSize)) / squareSize;
				returnArray[i][j] = linInterpolate(v11,v21,v12,v22,xRatio,yRatio);
			}
		}
		return returnArray;
	}
	/** generates a full array by calling genOctave for each octave
	 * for more focus on large or small scale structures one could
	 * change the calculation of modifiedOctiveWeightConstant
	 * even specifying it manually once map size is known.
	 * This would allow for much more precise map generation.
	 * Additionally, some octaves could be provided by the programmer
	 * in order to create maps with some predefined non-procedural structure.
	 * */
	double[][] getFullPerlinArray(int powSize) {
		powerSize = powSize;
		realSize = (int) Math.pow(2,powerSize);
		double modifiedOctiveWeightConstant = OCTAVEWEIGHTCONSTANT/(Math.pow(2,powerSize));
		double octavePower;
		double centerWeighting;
		double[][] fullArray = new double [realSize][realSize];
		for (int o = 3; o<powerSize; o++) {
			double[][] newOctave = genOctave(o);
			octavePower = modifiedOctiveWeightConstant*Math.pow(PERSISTANCE, RATEOFCHANGE*o);
			for (int i=0; i<realSize;i++) {
				for (int j=0; j<realSize;j++) {
					centerWeighting = (Math.pow(i*(realSize-i),CENTERWEIGHTING)*
									   Math.pow(j*(realSize-j),CENTERWEIGHTING))/
									   Math.pow(realSize/2,2*CENTERWEIGHTING);
					fullArray[i][j] += newOctave[i][j]*octavePower*centerWeighting;
				}
			}
			//Following line was used to save debug renders of the map at each octave
			//MyUtils.visuliseArray(fullArray);
		}
		return fullArray;
	}
}
