import Model.Matrix;
import Model.RGBPixel;

public class Main {

    public static void printMatrix(Matrix<Matrix<Integer>> matrix){
        for(int i=0;i<matrix.getNumberOfRows();i++){
            for(int j=0;j<matrix.getNumberOfColumns();j++){
                Matrix<Integer> currentMatrix = matrix.get(i,j);
                for(int m=0;m<currentMatrix.getNumberOfRows();m++){
                    for(int n=0;n<currentMatrix.getNumberOfColumns();n++){
                        System.out.print(currentMatrix.get(m,n)+" ");
                    }
                    System.out.println();
                }
                System.out.println();
            }

        }
    }

    public static void main(String[] args){
        Matrix<RGBPixel> picture = PPMIO.readPPM("nt-P3.ppm");
        Encoder encoder = new Encoder();
        encoder.encode(picture);
//        System.out.println("Luminance matrix");
//        printMatrix(encoder.getLuminanceMatrix());
//        System.out.println("Blue chrominance matrix");
//        printMatrix(encoder.getBlueChrominanceMatrix());
//        System.out.println("Red chrominance matrix");
//        printMatrix(encoder.getRedChrominanceMatrix());
        Decoder decoder = new Decoder();
        Matrix<RGBPixel> compressedPicture = decoder.decode(encoder.getLuminanceMatrix(),encoder.getBlueChrominanceMatrix(),encoder.getRedChrominanceMatrix(),encoder.getQuantizationMatrix());
        PPMIO.writePPM(compressedPicture,"output.ppm");
    }
}
