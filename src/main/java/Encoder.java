import DeepCopy.DeepCopy;
import Model.Matrix;
import Model.RGBPixel;
import Model.YUVPixel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Encoder {

    private Matrix<Matrix<Integer>> luminanceMatrix, blueChrominanceMatrix, redChrominanceMatrix;

    public Matrix<Integer> getQuantizationMatrix() {
        return quantizationMatrix;
    }

    private Matrix<Integer> quantizationMatrix;

    private void initQuantizationMatrix() {
        quantizationMatrix = new Matrix<>();
        Integer[] row = new Integer[]{6, 4, 4, 6, 10, 16, 20, 24};
        quantizationMatrix.addRow(Arrays.asList(row));
        row = new Integer[]{5, 5, 6, 8, 10, 23, 24, 22};
        quantizationMatrix.addRow(Arrays.asList(row));
        row = new Integer[]{6, 5, 6, 10, 16, 23, 28, 22};
        quantizationMatrix.addRow(Arrays.asList(row));
        row = new Integer[]{6, 7, 9, 12, 20, 35, 32, 25};
        quantizationMatrix.addRow(Arrays.asList(row));
        row = new Integer[]{7, 9, 15, 22, 27, 44, 41, 31};
        quantizationMatrix.addRow(Arrays.asList(row));
        row = new Integer[]{10, 14, 22, 26, 32, 42, 45, 37};
        quantizationMatrix.addRow(Arrays.asList(row));
        row = new Integer[]{20, 26, 31, 35, 41, 48, 48, 40};
        quantizationMatrix.addRow(Arrays.asList(row));
        row = new Integer[]{29, 37, 38, 39, 45, 40, 41, 40};
        quantizationMatrix.addRow(Arrays.asList(row));
    }

    private Matrix<YUVPixel> convertRGBPixelMatrixToYUVPixelMatrix(Matrix<RGBPixel> rgbMatrix) {
        Matrix<YUVPixel> yuvMatrix = new Matrix<>();
        for (int i = 0; i < rgbMatrix.getNumberOfRows(); i++) {
            ArrayList<YUVPixel> row = new ArrayList<>();
            for (int j = 0; j < rgbMatrix.getNumberOfColumns(); j++) {
                YUVPixel yuvPixel = new YUVPixel();
                RGBPixel currentPixel = rgbMatrix.get(i, j);
                yuvPixel.setLuminance((int) (0.257 * currentPixel.getRed() + 0.504 * currentPixel.getGreen() + 0.098 * currentPixel.getBlue()) + 16);
                yuvPixel.setBlueChrominance(128 + (int) (-0.148 * currentPixel.getRed() - 0.291 * currentPixel.getGreen() + 0.439 * currentPixel.getBlue()));
                yuvPixel.setRedChrominance(128 + (int) (0.439 * currentPixel.getRed() - 0.368 * currentPixel.getGreen() - 0.071 * currentPixel.getBlue()));
                row.add(yuvPixel);
            }
            yuvMatrix.addRow(row);
        }
        return yuvMatrix;
    }


    private void splitYUVMatrixInBlocks(Matrix<YUVPixel> yuvMatrix) {
        luminanceMatrix = new Matrix<>(yuvMatrix.getNumberOfRows() / 8, yuvMatrix.getNumberOfColumns() / 8, new Matrix<>(8, 8, 0));
        blueChrominanceMatrix = new Matrix<>(yuvMatrix.getNumberOfRows() / 8, yuvMatrix.getNumberOfColumns() / 8, new Matrix<>(8, 8, 0));
        redChrominanceMatrix = new Matrix<>(yuvMatrix.getNumberOfRows() / 8, yuvMatrix.getNumberOfColumns() / 8, new Matrix<>(8, 8, 0));
        for (int i = 0; i < yuvMatrix.getNumberOfRows(); i++) {
            for (int j = 0; j < yuvMatrix.getNumberOfColumns(); j++) {
                YUVPixel yuvPixel = yuvMatrix.get(i, j);
                luminanceMatrix.get(i / 8, j / 8).set(i % 8, j % 8, yuvPixel.getLuminance());
                blueChrominanceMatrix.get(i / 8, j / 8).set(i % 8, j % 8, yuvPixel.getBlueChrominance());
                redChrominanceMatrix.get(i / 8, j / 8).set(i % 8, j % 8, yuvPixel.getRedChrominance());
            }
        }
    }


    private Matrix<Matrix<Integer>> subsample420(Matrix<Matrix<Integer>> matrix) {
        Matrix<Matrix<Integer>> result = new Matrix<>(matrix.getNumberOfRows(), matrix.getNumberOfColumns(), new Matrix<>(4, 4, 0));
        for (int i = 0; i < matrix.getNumberOfRows(); i++) {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentMatrix = matrix.get(i, j);
                for (int m = 0; m < 4; m++) {
                    for (int n = 0; n < 4; n++) {
                        Integer average = (currentMatrix.get(m * 2, n * 2) + currentMatrix.get(m * 2 + 1, n * 2)
                                + currentMatrix.get(m * 2, n * 2 + 1) + currentMatrix.get(m * 2 + 1, n * 2 + 1)) / 4;
                        result.get(i, j).set(m, n, average);
                    }
                }
            }
        }
        return result;
    }

    private Matrix<Matrix<Integer>> reverseSubsample420(Matrix<Matrix<Integer>> matrix) {
        Matrix<Matrix<Integer>> result = new Matrix<>(matrix.getNumberOfRows(), matrix.getNumberOfColumns(), new Matrix<>(8, 8, 0));
        for (int i = 0; i < matrix.getNumberOfRows(); i++) {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentMatrix = matrix.get(i, j);
                for (int m = 0; m < 4; m++) {
                    for (int n = 0; n < 4; n++) {
                        result.get(i, j).set(m * 2, n * 2, currentMatrix.get(m, n));
                        result.get(i, j).set(m * 2 + 1, n * 2, currentMatrix.get(m, n));
                        result.get(i, j).set(m * 2, n * 2 + 1, currentMatrix.get(m, n));
                        result.get(i, j).set(m * 2 + 1, n * 2 + 1, currentMatrix.get(m, n));
                    }
                }
            }
        }
        return result;
    }

    private void valueReducer(Matrix<Matrix<Integer>> matrix, Integer value) {
        for (int i = 0; i < matrix.getNumberOfRows(); i++) {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentMatrix = matrix.get(i, j);
                for (int m = 0; m < currentMatrix.getNumberOfRows(); m++) {
                    for (int n = 0; n < currentMatrix.getNumberOfColumns(); n++) {
                        currentMatrix.set(m, n, currentMatrix.get(m, n) - value);
                    }
                }
            }
        }
    }

    private Matrix<Matrix<Integer>> forwardDCT(Matrix<Matrix<Integer>> matrix) {
        Matrix<Matrix<Integer>> result = new Matrix<>(matrix.getNumberOfRows(), matrix.getNumberOfColumns(), new Matrix<>(8, 8, 0));
        for (int i = 0; i < matrix.getNumberOfRows(); i++) {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentMatrix = matrix.get(i, j);
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
                        double sum = 0.0;
                        for (int x = 0; x < currentMatrix.getNumberOfRows(); x++) {
                            for (int y = 0; y < currentMatrix.getNumberOfColumns(); y++) {
                                sum = sum + (currentMatrix.get(x, y) *
                                        Math.cos(((2 * x + 1) * u * Math.PI) / 16) *
                                        Math.cos(((2 * y + 1) * v * Math.PI) / 16));
                            }
                        }
                        double resultValue = (firstArgument * secondArgument * sum) / 4;
                        result.get(i, j).set(u, v, (int) resultValue);
                    }
                }
            }
        }
        return result;
    }

    public void quantization(Matrix<Matrix<Integer>> matrix, Matrix<Integer> quantizationMatrix) {
        for (int i = 0; i < matrix.getNumberOfRows(); i++) {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                Matrix<Integer> currentMatrix = matrix.get(i, j);
                for (int m = 0; m < currentMatrix.getNumberOfRows(); m++) {
                    for (int n = 0; n < currentMatrix.getNumberOfColumns(); n++) {
                        currentMatrix.set(m, n, ( currentMatrix.get(m, n) /  quantizationMatrix.get(m, n)));
                    }
                }
            }
        }
    }

    public void encode(Matrix<RGBPixel> picture) {
        initQuantizationMatrix();
        Matrix<YUVPixel> yuvMatrix = convertRGBPixelMatrixToYUVPixelMatrix(picture);
        splitYUVMatrixInBlocks(yuvMatrix);
        blueChrominanceMatrix = subsample420(blueChrominanceMatrix);
        redChrominanceMatrix = subsample420(redChrominanceMatrix);
        blueChrominanceMatrix = reverseSubsample420(blueChrominanceMatrix);
        redChrominanceMatrix = reverseSubsample420(redChrominanceMatrix);
        valueReducer(luminanceMatrix, 128);
        valueReducer(blueChrominanceMatrix, 128);
        valueReducer(redChrominanceMatrix, 128);
        luminanceMatrix = forwardDCT(luminanceMatrix);
        blueChrominanceMatrix = forwardDCT(blueChrominanceMatrix);
        redChrominanceMatrix = forwardDCT(redChrominanceMatrix);
        quantization(luminanceMatrix,quantizationMatrix);
        quantization(blueChrominanceMatrix,quantizationMatrix);
        quantization(redChrominanceMatrix,quantizationMatrix);
    }

    public Matrix<Matrix<Integer>> getLuminanceMatrix() {
        return luminanceMatrix;
    }

    public Matrix<Matrix<Integer>> getBlueChrominanceMatrix() {
        return blueChrominanceMatrix;
    }

    public Matrix<Matrix<Integer>> getRedChrominanceMatrix() {
        return redChrominanceMatrix;
    }
}
