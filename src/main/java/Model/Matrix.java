package Model;

import DeepCopy.DeepCopy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Matrix<E> implements Serializable {

    public List<List<E>> matrix;
    Integer rows, columns;

    /**
     * @param rows     - number of rows of the initialized matrix
     * @param columns  - number of columns of the initialized matrix
     * @param initElem - default matrix element
     */
    public Matrix(Integer rows, Integer columns, E initElem) {
        this.columns = columns;
        this.rows = rows;
        matrix = new ArrayList<>();
        initMatrix(initElem);
    }

    public Matrix() {
        this.columns = 0;
        this.rows = 0;
        matrix = new ArrayList<>();
    }

    /**
     * Initializes all the elements of the matrix with a given value
     *
     * @param elem - initialization element
     */
    private void initMatrix(E elem) {
        for (int i = 0; i < rows; i++) {
            ArrayList<E> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                row.add((E) DeepCopy.copy(elem));
            }
            matrix.add(row);
        }
    }


    /**
     * @param row    - row index
     * @param column - column index
     * @return the value from the matrix from the given row and column
     */
    public E get(Integer row, Integer column) {
        return matrix.get(row).get(column);
    }


    /**
     * Saves a value in the matrix at a given row and column
     *
     * @param row    - row index
     * @param column - column index
     * @param value  - the values to be saved in the matrix
     */
    public void set(Integer row, Integer column, E value) {
        matrix.get(row).set(column, value);
    }

    /**
     * @return Number the number of rows contained by the matrix
     */
    public Integer getNumberOfRows() {
        return rows;
    }

    /**
     * @return Number the number of columns contained by the matrix
     */
    public Integer getNumberOfColumns() {
        return columns;
    }

    /**
     * @param index - index of the row
     * @return Returns the row of the matrix of the given index
     */
    public List<E> getRow(Integer index) {
        return matrix.get(index);
    }

    /**
     * @param row   - row to be set
     * @param index - index of the row to be set
     */
    public void setRow(ArrayList<E> row, Integer index) {
        matrix.set(index, row);
    }

    /**
     * Adds a row to the end of the matrix
     *
     * @param row - row to be added
     */
    public void addRow(List<E> row) {
        matrix.add(row);
        rows++;
        if (columns.equals(0)) {
            columns = row.size();
        }
    }
}
