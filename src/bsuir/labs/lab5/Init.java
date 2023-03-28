package bsuir.labs.lab5;

import bsuir.labs.helper.Matrix;
import bsuir.labs.helper.MyArray;
import bsuir.labs.helper.ArrayThread;
import bsuir.labs.helper.Options;

import java.util.concurrent.*;

import static bsuir.labs.helper.Options.WITHOUT_OPTIONS;

//Фьючерсы
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
        Matrix algAddsMatrix = matrix.GetAlgAddsMatrix(Options.FUTURE_OPTION);
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

        //Выполнеине фьючерса
        Future<String> future = es.submit(() ->
        {
            //Проверяет все ли элементы массива были использованы каждые 0.5 секунды
            do {
                Thread.sleep(500);
            } while(myArray.HasNext());
            return "";
        });


        int i = 0;//счётчик итераций
        while(!future.isDone())//Цикл выполняет операции пока фьючерс не закончит свою работу
        {
            /**Инициализация потоков и их выполнине (метод run())*/
            System.out.println("Запуск нового потока");
            ArrayThread myThread = new ArrayThread("Thread" + i, myArray, ++i, WITHOUT_OPTIONS);
            myThread.start();
            Thread.sleep(200);
        }


        System.out.println("\nРезультат перемножения элементов массива = " + myArray.GetMultiplyResult() +
                "\nБыло запущено " + i + " потоков");
        System.out.println();
        es.shutdown();
    }
}
