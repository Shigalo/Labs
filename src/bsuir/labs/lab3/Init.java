package bsuir.labs.lab3;

import bsuir.labs.helper.Matrix;
import bsuir.labs.helper.MyArray;
import bsuir.labs.helper.ArrayThread;
import bsuir.labs.helper.Options;

import java.util.concurrent.*;

//Семафоры (гарантируют, что к объекту имеет доступ ОГРАНИЧЕННОЕ количество потоков)
public class Init {

    public static void main(String args[]) throws InterruptedException {

        //Выполнение перемножения элементов массива
//        ArrayExecute();

        //Выполнение нахождения обратной матрицы
        MatrixExecute(args);
    }

    private static void MatrixExecute(String args[]) throws InterruptedException {
        //Иницализация матрицы
//        Matrix matrix = new Matrix(); //Ввод вручную
//        Matrix matrix = new Matrix(0); //Случайные числа (-20; 20)
        Matrix matrix = new Matrix(args); //Аргументы программы
        System.out.println("Введена матрица:\n" + matrix.toString());

        //Нахождение определителя (вырожденная/невырожденная)
        matrix.FindDeterminant();
        System.out.println("\n------------------------------------------------------------------------------------------");

        if(matrix.GetDeterminant() == 0) {
            System.out.println("Невозможно найти определитель: Заданная матрица вырожденная");
            return; //Выход из программы
        }

        /**Вычисление обратной матрицы с помощью алгебраических дополнений*/
        System.out.println("\n\n\n\nВычисление обратной матрицы с помощью алгебраических дополнений");

        //Матрица алгебраических дополнений
        Matrix algAddsMatrix = matrix.GetAlgAddsMatrix(Options.SEMAPHORE_OPTION);
        System.out.println("\nМатрица алгебраических дополнений:\n" + algAddsMatrix.toString());

        //Присоединённая матрица(транспонируем матрицу алгебраических дополнений)
        Matrix trAlgAddsMatrix = new Matrix(algAddsMatrix.GetTranspMatrix());
        System.out.println("Присоединённая матрица:\n" + trAlgAddsMatrix.toString());

        //Получение обратной матрицы
        trAlgAddsMatrix.DivideAll(matrix.GetDeterminant());
        System.out.println("Обратная матрица:\n" + trAlgAddsMatrix.toString());
    }

    private static void ArrayExecute() throws InterruptedException {
        // Для ожидания завершения потоков перед выводом
        ExecutorService es = Executors.newCachedThreadPool();

        //Инициализация массива случайными числами (от -20 до 20)
        ///////////////////////////////////////////////////////////////////////////////////////
        int[] temp = new int[10];
        for (int i = 0; i < temp.length; i++)
            temp[i] = ThreadLocalRandom.current().nextInt(-20, 20);
        ///////////////////////////////////////////////////////////////////////////////////////

        MyArray myArray = new MyArray(temp);
        System.out.println("Массив чисел: " + myArray.toString());

        /**Инициализация 4 потоков и их выполнине (метод run())*/
        //!!Вывод промежуточного результата может !повторятся! или быть !непоследовательным!
        //!!в связи с использованием семафора (несколько потоков могут взаимодействовать одновременно)
        Semaphore semaphore = new Semaphore(2);//Макс потоков одновременно = 2
        for (int i = 0; i < 4; i++)
            es.execute(new ArrayThread("Thread" + i, myArray, i + 1, semaphore));

        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);//Ожидание окончания вполнения потоков

        //Вывод результата
        System.out.println("\nРезультат перемножения элементов массива = " + myArray.GetMultiplyResult());
    }

}
