package bsuir.labs.lab6;

import bsuir.labs.helper.InputValidator;
import bsuir.labs.helper.OutputHelper;


public class Matrix {

    private static final int SIZE = 4; //Частный случай (для матрицы 4х4)

    private double determinant;
    private double[][] elements = new double[SIZE][SIZE];

    public Matrix(double[][] matrix) {
        elements = matrix.clone();
    }

    /**Строковый вид - для ввода в консоль как аргументов
     * Развёрнутый - для наглядности
     *
     * Ввод матрицы из аргументов программы
     * Пример 1
     * Строковый вид (−1 3 2 −3 4 −2 5 1 −5 0 −4 0 9 7 8 −7)
     * Развёрнутый вид
     * [−1  3  2 −3
     *  4 −2  5  1
     *  −5  0 −4  0
     *  9  7  8 −7]
     * <p>
     * Пример 2
     * Строковый вид (6 −5 8 4 9 7 5 2 7 5 3 7 −4 8 −8 −3)
     * Развёрнутый вид
     * [6 −5  8  4
     *  9  7  5  2
     *  7  5  3  7
     * −4  8 −8 −3]
     *
     *  Пример 3
     *  Строковый вид (−2 3 0 1 −6 9 −2 7 0 −2 −18 27 −4 5 −8 14)
     *  [−2  3   0  1
     *   −6  9  −2  7
     *    0 −2 −18 27
     *   −4  5  −8 14]
     *
     *   Пример 4 (вырожденная)
     *   Строковый вид (5 −4 1 0 12 −11 4 0 −5 58 4 0 3 −1 −9 0)
     *   [5  −4  1 0
     *   12 −11  4 0
     *   −5  58  4 0
     *    3  −1 −9 0]
     */
    public Matrix(String array[]) {
        for (int i = 0; i < array.length; i++) {
            int line = (int) i / SIZE;
            int col = (int) i % SIZE;
            elements[line][col] = Double.valueOf(array[i].replace("?", "-"));
        }
    }

    // Ввод матрицы ручную
    public Matrix() {
        for (int i = 0; i < elements.length; i++) { //Проход по строке
            for (int j = 0; j < elements[i].length; j++) { //Проход по столбцу
                String query = "Элемент матрицы a[" + (i + 1) + "][" + (j + 1) + "] = ";
                elements[i][j] = InputValidator.CheckInputDouble(query); //Ввод
            }
        }
    }

    public Matrix(int a) {
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < elements[i].length; j++) {
                elements[i][j] = i * (j - (2 * i)) * 2 + 1;
                System.out.println("Элемент матрицы a[" + i + "][" + j + "] = " + elements[i][j]);
            }
        }

    }

    public double GetDeterminant() {
        return determinant;
    }

    public double[][] GetAlgAddsMatrix() {
        System.out.println("Построение матрицы алгебраических дополнений");

        double[][] resultMatrix = new double[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.println("Элемент a[" + i + "][" + j + "] = " + OutputHelper.DoubleOutput(elements[i][j]));
                resultMatrix[i][j] = GetAlgAddition(i, j) * Math.pow((-1), (i + j));
            }
        }
        return resultMatrix;
    }

    public double[][] GetTranspMatrix() {
        System.out.println("Построение присоединённой матрицы");
        double[][] resultMatrix = new double[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                resultMatrix[i][j] = elements[j][i];
        return resultMatrix;
    }

    //Деление всех элементов матрицы на разделитель
    public void DivideAll(double divider) {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                elements[i][j] /= divider;
    }

    /*определитель равен сумме произведений элементов некоей строки или
    столбца на алгебраические дополнения этих элементов*/
    public void FindDeterminant() {
        System.out.println("\nВычисление определителя: ");
        int k = 1; // выбор строки
        k -= 1; // учёт начала индексации(0~1)
        System.out.println("Выбрана строка " + (k));

        double adds[] = new double[SIZE];

        for (int j = 0; j < elements[k].length; j++) { //Проход по столбцу
            System.out.println("\nВычисление алгебраического дополнение элемента a[" + (k + 1) + "][" + (j + 1) + "] = " + OutputHelper.DoubleOutput(elements[k][j]));
            double algAdd = GetAlgAddition(k, j);// Алгебраическое дополнение элемента
            System.out.println("Алгебраического дополнение = " + OutputHelper.DoubleOutput(algAdd));
            adds[j] = algAdd;
        }

        System.out.print("\nОпределитель матрицы = ");
        determinant = elements[k][0] * adds[0] * (int) Math.pow((-1), (k));
        System.out.print(OutputHelper.DoubleOutput(elements[k][0]) +
                "*" + OutputHelper.DoubleOutput(adds[0]) +
                "*(-1)^(" + (k + 2) + ")");
        for (int i = 1; i < SIZE; i++) {
            System.out.print(" + " + OutputHelper.DoubleOutput(elements[k][i]) +
                    "*" + OutputHelper.DoubleOutput(adds[i]) +
                    "*(-1)^(" + (k + i + 2) + ")");
            determinant += elements[k][i] * adds[i] * Math.pow((-1), (k + i));
        }
        System.out.print(" = " + OutputHelper.DoubleOutput(determinant));
    }

    private double GetAlgAddition(int line, int col) {
        double subMatrix[][] = GetSubMatrix(line, col, elements); //Получение уменьшенной матрицы
        double result = 0;


        System.out.print("Минор = ");
        for (int i = 0; i < subMatrix.length; i++) {
            double bufResult = 1;
            /**Вывод в консоль*/if (i != 0) System.out.print(" + ");
            for (int j = 0; j < subMatrix.length; j++) {
                int k = (i + j) % subMatrix.length;
                /**Вывод в консоль*/
                if (subMatrix[j][k] < 0) System.out.print(OutputHelper.DoubleOutput(subMatrix[j][k]));
                /**Вывод в консоль*/
                else System.out.print(OutputHelper.DoubleOutput(subMatrix[j][k]));
                /**Вывод в консоль*/if (j != (subMatrix.length - 1)) System.out.print("*");
                bufResult *= subMatrix[j][k];
            }
            result += bufResult;
        }

        for (int i = 0; i < subMatrix.length; i++) {
            int bufResult = 1;
            /**Вывод в консоль*/System.out.print(" - ");
            for (int j = 0; j < subMatrix.length; j++) {
                int k = ((subMatrix.length + 1) - (i + j)) % subMatrix.length;
                /**Вывод в консоль*/
                if (subMatrix[j][k] < 0) System.out.print(OutputHelper.DoubleOutput(subMatrix[j][k]));
                /**Вывод в консоль*/
                else System.out.print(OutputHelper.DoubleOutput(subMatrix[j][k]));
                /**Вывод в консоль*/if (j != (subMatrix.length - 1)) System.out.print("*");
                bufResult *= subMatrix[j][k];
            }
            result -= bufResult;
        }
        System.out.println(" = " + OutputHelper.DoubleOutput(result));
        return result;
    }

    private double[][] GetSubMatrix(int line, int col, double[][] matrix) {
        double subMatrix[][] = new double[matrix.length - 1][matrix.length - 1];
        int k, n = 0; //Итераторы для строки (n) и столбца (k) новой матрицы

        for (int y = 0; y < matrix.length; y++) {
            if (y == line) continue;
            k = 0;
            for (int x = 0; x < matrix[y].length; x++) {
                if (x == col) continue;
                subMatrix[n][k] = matrix[y][x];
                k++;
            }
            n++;
        }

        Matrix m = new Matrix(subMatrix);
        /**Вывод в консоль*/System.out.print("Уменьшенная матрица:\n" + m.toString());


        return subMatrix;
    }

    public String toString() {
        String result = "";
        for (double[] line : elements) {
            result += "|";
            for (double element : line) {
                result += String.format("%6s|", OutputHelper.DoubleOutput(element));
            }
            result += "\n";
        }

        return result;
    }
}
