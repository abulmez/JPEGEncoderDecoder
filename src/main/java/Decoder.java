import Model.Matrix;
import Model.RGBPixel;

public class Decoder {

    public Matrix<RGBPixel> decode(Matrix<Matrix<Integer>> luminanceMatrix, Matrix<Matrix<Integer>> blueChrominanceMatrix, Matrix<Matrix<Integer>> redChrominanceMatrix) {
        Matrix<RGBPixel> rgbMatrix = new Matrix<>(luminanceMatrix.getNumberOfRows() * 8, luminanceMatrix.getNumberOfColumns() * 8, null);
        for (int i = 0; i < luminanceMatrix.getNumberOfRows(); i++) {
            for (int j = 0; j < luminanceMatrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentLuminanceMatrix = luminanceMatrix.get(i, j);
                Matrix<Integer> currentBlueChrominanceMatrix = blueChrominanceMatrix.get(i, j);
                Matrix<Integer> currentRedChrominanceMatrix = redChrominanceMatrix.get(i, j);
                for (int m = 0; m < 8; m++) {
                    for (int n = 0; n < 8; n++) {
                        RGBPixel rgbPixel = new RGBPixel();
                        rgbPixel.setRed((int) (1.164 * (currentLuminanceMatrix.get(m, n) - 16) + 1.596 * (currentRedChrominanceMatrix.get(m / 2, n / 2) - 128)));
                        if(rgbPixel.getRed()>255) {
                            rgbPixel.setRed(255);
                        }
                        if(rgbPixel.getRed()<0) {
                            rgbPixel.setRed(0);
                        }
                        rgbPixel.setGreen((int) (1.164 * (currentLuminanceMatrix.get(m, n) - 16) - 0.813 * (currentRedChrominanceMatrix.get(m / 2, n / 2) - 128) - 0.391 * (currentBlueChrominanceMatrix.get(m / 2, n / 2) - 128)));
                        if(rgbPixel.getGreen()>255) {
                            rgbPixel.setGreen(255);
                        }
                        if(rgbPixel.getGreen()<0) {
                            rgbPixel.setGreen(0);
                        }
                        rgbPixel.setBlue((int) (1.164 * (currentLuminanceMatrix.get(m, n) - 16) + 2.018 * (currentBlueChrominanceMatrix.get(m / 2, n / 2) - 128)));
                        if(rgbPixel.getBlue()>255) {
                            rgbPixel.setBlue(255);
                        }
                        if(rgbPixel.getBlue()<0) {
                            rgbPixel.setBlue(0);
                        }
                        rgbMatrix.set(i*8+m,j*8+n,rgbPixel);
                    }
                }
            }
        }
        return rgbMatrix;
    }
}
