package bsuir.labs.helper;

import sun.awt.Mutex;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;


public class Matrix {

    private static final int SIZE = 4; //Частный случай (для матрицы 4х4)
    int progress = 0;

    //Определитель матрицы - атомарный тип (Lab 4)
    private AtomicReference<Double> determinant = new AtomicReference<>((double) 0);
    private double[][] elements = new double[SIZE][SIZE];

    //lab 2
    public Mutex mutex = new Mutex();

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
    //Получение матрицы из аргументов
    public Matrix(String array[]) {
        for (int i = 0; i < array.length; i++) {
            int line = (int) i / SIZE;
            int col = (int) i % SIZE;
            elements[line][col] = Double.valueOf(array[i].replace("?", "-"));
        }
    }

    //Ввод матрицы ручную
    public Matrix() {
        for (int i = 0; i < elements.length; i++) { //Проход по строке
            for (int j = 0; j < elements[i].length; j++) { //Проход по столбцу
                String query = "Элемент матрицы a[" + (i + 1) + "][" + (j + 1) + "] = ";
                elements[i][j] = InputValidator.CheckInputDouble(query); //Ввод
            }
        }
    }

    //Генерация матрицы из случайных чисел (-20, 20)
    public Matrix(int a) {
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < elements[i].length; j++) {
                elements[i][j] = ThreadLocalRandom.current().nextInt(-20, 20);
                System.out.println("Элемент матрицы a[" + i + "][" + j + "] = " + elements[i][j]);
            }
        }

    }


    /**Lab 2, 3, 5, 6*/
    //Выбор метода расчёта матрицы алгебраических дополнений
    //исходя из лабараторной работы
    public Matrix GetAlgAddsMatrix(Options option) throws InterruptedException {
        System.out.println("Построение матрицы алгебраических дополнений");

        // Для ожидания завершения потоков
        ExecutorService es = Executors.newCachedThreadPool();
        Semaphore semaphore = new Semaphore(2);//Макс потоков одновременно = 2

        Matrix resultMatrix = new Matrix(new double[SIZE][SIZE]);//Матрица алгебраических дополнений

                switch(option) {
                    /**Lab 4, 6*/
                    case WITHOUT_OPTIONS:
                    case ATOMIC_OPTION://Lab 4 - Атомарная операция в FindDeterminant()
                        GetAlgAddsMatrixNonThread(resultMatrix);
                        break;

                    /**Lab 2*/
                    case MUTEX_OPTIONS: {
                        for (int i = 0; i < SIZE; i++) {
                            for (int j = 0; j < SIZE; j++) {
                                MatrixThread mutexThread = new MatrixThread("Thread" + (i * SIZE + j), this, resultMatrix, i, j, option);
                                es.execute(mutexThread);
                            }
                        }
                        break;
                    }

                    /**Lab 3*/
                    case SEMAPHORE_OPTION: {
                        for (int i = 0; i < SIZE; i++) {
                            for (int j = 0; j < SIZE; j++) {
                                MatrixThread semaphoreThread = new MatrixThread("Thread" + (i * SIZE + j), this, resultMatrix, i, j, semaphore);
                                es.execute(semaphoreThread);
                            }
                        }
                        break;
                    }

                    /**Lab 5*/
                    case FUTURE_OPTION: {
                        int CHECK_TIME = 50;
                        Future<String> future = es.submit(() -> {
                            GetAlgAddsMatrixNonThread(resultMatrix);//Обычный расчёт без использования потоков (Lab 6)
                            return "";
                        });

                        while (!future.isDone())//Цикл выполняет операции пока фьючерс не закончит свою работу
                        {
                            System.out.println("\t\tTask in progress. Status: " + progress + "/" + (SIZE * SIZE));
                            Thread.sleep(CHECK_TIME);//Интервал проверки
                        }
//                        Thread.sleep(CHECK_TIME);//Задржка для вывода после проверки
                        System.out.print("\t\tTask is complete\n");
                        break;
                    }
        }
        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);//Ожидание окончания вполнения потоков
        return resultMatrix;
    }

    /**Lab 5, 6*/
    //Нахождение матрица алгебраических дополнений без потоков
    private void GetAlgAddsMatrixNonThread(Matrix matrix) throws InterruptedException {
        progress = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                //Постороение результата выполнения
                StringBuilder resultOutput = new StringBuilder("\nЭлемент a[")//
                        .append(i + 1).append("][").append(j + 1).append("] = ")
                        .append(OutputHelper.DoubleOutput(elements[i][j]));

                double aa = GetAlgAddition(i, j, resultOutput);//Вычисление алгебраического дополнения
                matrix.SetValue(aa, i, j);//Запись в таблицу дополнений
                System.out.println(resultOutput);//Вывод результата выполнения
                progress++;
                Thread.sleep(30);//задержка после выполнения
            }
        }
    }

    //Транспонирование
    public double[][] GetTranspMatrix() {
        System.out.println("Построение присоединённой матрицы");
        double[][] resultMatrix = new double[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                resultMatrix[i][j] = elements[j][i];
        return resultMatrix;
    }

    //Деление всех элементов на определитель
    public void DivideAll(double divider) {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                elements[i][j] /= divider;
    }

    /**LAB 4*/
    /**определитель равен сумме произведений элементов некоей строки или
    столбца на алгебраические дополнения этих элементов*/
    public void FindDeterminant() throws InterruptedException {
        System.out.println("\nВычисление определителя: ");
        int k = 1; // выбор строки
        System.out.println("Выбрана строка " + k);
        k -= 1; // учёт начала индексации(1~0)

        double adds[] = new double[SIZE];

        for (int j = 0; j < elements[k].length; j++) { //Проход по столбцу
            StringBuilder resultOutput = new StringBuilder("\nВычисление алгебраического дополнение элемента a[")
                    .append(k + 1).append("][").append(j + 1).append("] = ")
                    .append(OutputHelper.DoubleOutput(elements[k][j]));

            double algAdd = GetAlgAddition(k, j, resultOutput);// Алгебраическое дополнение элемента
            System.out.println(resultOutput);
            System.out.println("Алгебраического дополнение = " + OutputHelper.DoubleOutput(algAdd));
            adds[j] = algAdd;
        }

        System.out.print("\nОпределитель матрицы = ");
        System.out.print(OutputHelper.DoubleOutput(elements[k][0]) +
                "*" + OutputHelper.DoubleOutput(adds[0]) +
                "*(-1)^(" + (k + 2) + ")");
        for (int i = 1; i< SIZE; i++) {
            System.out.print(" + " + OutputHelper.DoubleOutput(elements[k][i]) +
                    "*" + OutputHelper.DoubleOutput(adds[i]) +
                    "*(-1)^(" + (k + i + 2) + ")");
        }

        /////////////////////////////////////////////////////////////////////////////////////////
        /**Lab 4 - вычисление определителя с помощью атомарных операций*/
        determinant.set(0.0);
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < SIZE; i++) {
            AccumulateThread thread = new AccumulateThread(elements[k][i] * adds[i]);
            es.execute(thread);
        }
        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);//Ожидание окончания вполнения потоков

        System.out.print(" = " + OutputHelper.DoubleOutput(determinant.get()));
        /////////////////////////////////////////////////////////////////////////////////////////
    }

    //Алгебраическое дополнение для элемента a(line)(col)
    public double GetAlgAddition(int line, int col, StringBuilder resultOutput) {
        double subMatrix[][] = GetSubMatrix(line, col, elements, resultOutput); //Получение уменьшенной матрицы
        double result = 0;

        resultOutput.append("Минор = ");
        for (int i = 0; i < subMatrix.length; i++) {
            double bufResult = 1;
            /**Вывод в консоль*/if (i != 0) resultOutput.append(" + ");
            for (int j = 0; j < subMatrix.length; j++) {
                int k = (i + j) % subMatrix.length;
                /**Вывод в консоль*/
                resultOutput.append(OutputHelper.DoubleOutput(subMatrix[j][k]));
                /**Вывод в консоль*/if (j != (subMatrix.length - 1)) resultOutput.append("*");
                bufResult *= subMatrix[j][k];
            }
            result += bufResult;
        }

        for (int i = 0; i < subMatrix.length; i++) {
            int bufResult = 1;
            /**Вывод в консоль*/resultOutput.append(" - ");
            for (int j = 0; j < subMatrix.length; j++) {
                int k = ((subMatrix.length + 1) - (i + j)) % subMatrix.length;
                /**Вывод в консоль*/
                resultOutput.append(OutputHelper.DoubleOutput(subMatrix[j][k]));
                /**Вывод в консоль*/if (j != (subMatrix.length - 1)) resultOutput.append("*");
                bufResult *= subMatrix[j][k];
            }
            result -= bufResult;
        }
        resultOutput.append(" = ").append(OutputHelper.DoubleOutput(result));
        return result * Math.pow((-1), (line + col));
    }

    //Уменьшенная матрица для элемента a(line)(col)
    private double[][] GetSubMatrix(int line, int col, double[][] matrix, StringBuilder resultOutput) {
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
        /**Вывод в консоль*/resultOutput.append("\nУменьшенная матрица:\n").append(m.toString());


        return subMatrix;
    }



    //Поток для увеличения определителя с помощью атомарных операцй
    class AccumulateThread extends Thread {
        double value;
        AccumulateThread(double value) {
            this.value = value;
        }

        BinaryOperator<Double> operation = (u, v) -> u + v;//операция сложения

        @Override
        public void run() {
            //determinant = operation()
            determinant.accumulateAndGet(value, operation);
        }
    }

    public double GetDeterminant() {
        return determinant.get();
    }

    public void SetValue(double value, int line, int col) {
        elements[line][col] = value;
    }

    public double GetValue(int line, int col) {
        return elements[line][col];
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (double[] line : elements) {
            result.append("|");
            for (double element : line) {
                result.append(String.format("%8s|", OutputHelper.DoubleOutput(element)));
            }
            result.append("\n");
        }

        return result.toString();
    }
}
