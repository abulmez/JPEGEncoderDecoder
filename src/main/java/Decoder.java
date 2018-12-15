import Model.Matrix;
import Model.RGBPixel;

import java.util.Arrays;

public class Decoder {


    private void deQuantization(Matrix<Matrix<Integer>> matrix, Matrix<Integer> quantizationMatrix){
        for (int i = 0; i < matrix.getNumberOfRows(); i++) {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentMatrix = matrix.get(i, j);
                for (int m = 0; m < currentMatrix.getNumberOfRows(); m++) {
                    for (int n = 0; n < currentMatrix.getNumberOfColumns(); n++) {
                        currentMatrix.set(m, n, currentMatrix.get(m, n) * quantizationMatrix.get(m, n));
                    }
                }
            }
        }
    }

    private void valueAdder(Matrix<Matrix<Integer>> matrix,Integer value){
        for (int i = 0; i < matrix.getNumberOfRows(); i++) {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentMatrix = matrix.get(i, j);
                for (int m = 0; m < currentMatrix.getNumberOfRows(); m++) {
                    for (int n = 0; n < currentMatrix.getNumberOfColumns(); n++) {
                        currentMatrix.set(m, n, currentMatrix.get(m, n) + value);
                    }
                }
            }
        }
    }


    public Matrix<Matrix<Integer>> inverseDCT(Matrix<Matrix<Integer>> matrix){
        Matrix<Matrix<Integer>> result = new Matrix<>(matrix.getNumberOfRows(), matrix.getNumberOfColumns(), new Matrix<>(8, 8, 0));
        for (int i = 0; i < matrix.getNumberOfRows(); i++) {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentMatrix = matrix.get(i, j);
                for (int x = 0; x < currentMatrix.getNumberOfRows(); x++) {
                    for (int y = 0; y < currentMatrix.getNumberOfColumns(); y++) {
                        double sum = 0.0;
                        for (int u = 0; u < currentMatrix.getNumberOfRows(); u++) {
                            for (int v = 0; v < currentMatrix.getNumberOfColumns(); v++) {
                                double firstArgument = 1.0;
                                double secondArgument = 1.0;
                                if (u == 0) {
                                    firstArgument = 1.0 / Math.sqrt(2);
                                }
                                if (v == 0) {
                                    secondArgument = 1.0 / Math.sqrt(2);
                                }
                                sum = sum + (currentMatrix.get(u, v) *
                                        firstArgument*
                                        secondArgument*
                                        Math.cos(((2 * x + 1) * u * Math.PI) / 16) *
                                        Math.cos(((2 * y + 1) * v * Math.PI) / 16));
                            }
                        }
                        double resultValue = sum / 4;
                        result.get(i, j).set(x, y, (int) resultValue);
                    }
                }
            }
        }
        return result;
    }

    private Matrix<RGBPixel> rebuildRGBPixelMatrix(Matrix<Matrix<Integer>> luminanceMatrix, Matrix<Matrix<Integer>> blueChrominanceMatrix, Matrix<Matrix<Integer>> redChrominanceMatrix){
        Matrix<RGBPixel> rgbMatrix = new Matrix<>(luminanceMatrix.getNumberOfRows() * 8, luminanceMatrix.getNumberOfColumns() * 8, null);
        for (int i = 0; i < luminanceMatrix.getNumberOfRows(); i++) {
            for (int j = 0; j < luminanceMatrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentLuminanceMatrix = luminanceMatrix.get(i, j);
                Matrix<Integer> currentBlueChrominanceMatrix = blueChrominanceMatrix.get(i, j);
                Matrix<Integer> currentRedChrominanceMatrix = redChrominanceMatrix.get(i, j);
                for (int m = 0; m < currentLuminanceMatrix.getNumberOfRows(); m++) {
                    for (int n = 0; n < currentLuminanceMatrix.getNumberOfColumns(); n++) {
                        RGBPixel rgbPixel = new RGBPixel();
                        rgbPixel.setRed((int) (1.164 * (currentLuminanceMatrix.get(m, n) - 16) + 1.596 * (currentRedChrominanceMatrix.get(m, n ) - 128)));
                        if (rgbPixel.getRed() > 255) {
                            rgbPixel.setRed(255);
                        }
                        if (rgbPixel.getRed() < 0) {
                            rgbPixel.setRed(0);
                        }
                        rgbPixel.setGreen((int) (1.164 * (currentLuminanceMatrix.get(m, n) - 16) - 0.813 * (currentRedChrominanceMatrix.get(m, n ) - 128) - 0.391 * (currentBlueChrominanceMatrix.get(m, n) - 128)));
                        if (rgbPixel.getGreen() > 255) {
                            rgbPixel.setGreen(255);
                        }
                        if (rgbPixel.getGreen() < 0) {
                            rgbPixel.setGreen(0);
                        }
                        rgbPixel.setBlue((int) (1.164 * (currentLuminanceMatrix.get(m, n) - 16) + 2.018 * (currentBlueChrominanceMatrix.get(m, n) - 128)));
                        if (rgbPixel.getBlue() > 255) {
                            rgbPixel.setBlue(255);
                        }
                        if (rgbPixel.getBlue() < 0) {
                            rgbPixel.setBlue(0);
                        }
                        rgbMatrix.set(i * 8 + m, j * 8 + n, rgbPixel);
                    }
                }
            }
        }
        return rgbMatrix;
    }

    public Matrix<RGBPixel> decode(Matrix<Matrix<Integer>> luminanceMatrix, Matrix<Matrix<Integer>> blueChrominanceMatrix, Matrix<Matrix<Integer>> redChrominanceMatrix,Matrix<Integer> quantizationMatrix) {
        deQuantization(luminanceMatrix,quantizationMatrix);
        deQuantization(blueChrominanceMatrix,quantizationMatrix);
        deQuantization(redChrominanceMatrix,quantizationMatrix);
        luminanceMatrix = inverseDCT(luminanceMatrix);
        blueChrominanceMatrix = inverseDCT(blueChrominanceMatrix);
        redChrominanceMatrix = inverseDCT(redChrominanceMatrix);
        valueAdder(luminanceMatrix,128);
        valueAdder(blueChrominanceMatrix,128);
        valueAdder(redChrominanceMatrix,128);
        return rebuildRGBPixelMatrix(luminanceMatrix,blueChrominanceMatrix,redChrominanceMatrix);
    }
}
