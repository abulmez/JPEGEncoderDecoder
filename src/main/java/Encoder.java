import DeepCopy.DeepCopy;
import Model.Matrix;
import Model.RGBPixel;
import Model.YUVPixel;

import java.util.ArrayList;

public class Encoder {

    private Matrix<Matrix<Integer>> luminanceMatrix,blueChrominanceMatrix,redChrominanceMatrix;

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


    private void splitYUVMatrixInBlocks(Matrix<YUVPixel> yuvMatrix){
        luminanceMatrix = new Matrix<>(yuvMatrix.getNumberOfRows()/8,yuvMatrix.getNumberOfColumns()/8,new Matrix<>(8,8,0));
        blueChrominanceMatrix = new Matrix<>(yuvMatrix.getNumberOfRows()/8,yuvMatrix.getNumberOfColumns()/8,new Matrix<>(8,8,0));
        redChrominanceMatrix = new Matrix<>(yuvMatrix.getNumberOfRows()/8,yuvMatrix.getNumberOfColumns()/8,new Matrix<>(8,8,0));
        for(int i=0;i<yuvMatrix.getNumberOfRows();i++){
            for(int j=0;j<yuvMatrix.getNumberOfColumns();j++){
                YUVPixel yuvPixel = yuvMatrix.get(i,j);
                luminanceMatrix.get(i/8,j/8).set(i%8,j%8,yuvPixel.getLuminance());
                blueChrominanceMatrix.get(i/8,j/8).set(i%8,j%8,yuvPixel.getBlueChrominance());
                redChrominanceMatrix.get(i/8,j/8).set(i%8,j%8,yuvPixel.getRedChrominance());
            }
        }
    }



    public Matrix<Matrix<Integer>> subsample420(Matrix<Matrix<Integer>> matrix){
        Matrix<Matrix<Integer>> result = new Matrix<>(matrix.getNumberOfRows(),matrix.getNumberOfColumns(),new Matrix<>(4,4,0));
        for(int i=0;i<matrix.getNumberOfRows();i++){
            for(int j=0;j<matrix.getNumberOfColumns();j++){
                Matrix<Integer> currentMatrix = matrix.get(i,j);
                for(int m=0;m<4;m++){
                    for(int n=0;n<4;n++){
                        Integer average = (currentMatrix.get(m*2,n*2) + currentMatrix.get(m*2+1,n*2)
                                            + currentMatrix.get(m*2,n*2+1) + currentMatrix.get(m*2+1,n*2+1))/4;
                        result.get(i,j).set(m,n,average);
                    }
                }
            }
        }
        return result;
    }

    public void encode(Matrix<RGBPixel> picture) {
        Matrix<YUVPixel> yuvMatrix = convertRGBPixelMatrixToYUVPixelMatrix(picture);
        splitYUVMatrixInBlocks(yuvMatrix);
        blueChrominanceMatrix = subsample420(blueChrominanceMatrix);
        redChrominanceMatrix = subsample420(redChrominanceMatrix);
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
