import Model.Matrix;
import Model.RGBPixel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PPMIO {

    public static Integer maxPixelValue = 255;

    public static Matrix<RGBPixel> readPPM(String fileName) {


        Matrix<RGBPixel> matrix = new Matrix<>();
        try {
            ClassLoader classLoader = PPMIO.class.getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            Scanner sc = new Scanner(file);
            String line,red,green,blue;
            sc.nextLine();sc.nextLine();line = sc.nextLine();
            String[] dimension = line.split(" ");
            maxPixelValue = Integer.parseInt(sc.nextLine());
            Integer height = Integer.parseInt(dimension[1]), width = Integer.parseInt(dimension[0]),currentColumn = 0;
            List<RGBPixel> row = new ArrayList<>();
            try {
                while ((red = sc.nextLine()) != null && (green = sc.nextLine()) != null && (blue = sc.nextLine()) != null) {
                    row.add(new RGBPixel(Integer.parseInt(red),Integer.parseInt(green),Integer.parseInt(blue)));
                    currentColumn++;
                    if(currentColumn.equals(width)){
                        matrix.addRow(row);
                        currentColumn = 0;
                        row = new ArrayList<>();
                    }
                }
            } catch (Exception e) {
            }
        } catch (FileNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    public static void writePPM(Matrix<RGBPixel> matrix, String fileName) {
        try {
            File outputFile = new File(fileName);
            outputFile.createNewFile();
            PrintWriter writer = new PrintWriter(outputFile);
            try {
                writer.println("P3");
                writer.println("# CREATOR: GIMP PNM Filter Version 1.1");
                writer.println("800 600");
                writer.println(maxPixelValue);
                for (int i = 0; i < matrix.getNumberOfRows(); i++) {
                    for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                        RGBPixel currentPixel = matrix.get(i, j);
                        writer.println(currentPixel.getRed());
                        writer.println(currentPixel.getGreen());
                        writer.println(currentPixel.getBlue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            writer.close();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }
}
