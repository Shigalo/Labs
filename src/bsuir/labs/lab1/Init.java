package bsuir.labs.lab1;

import bsuir.labs.helper.InputValidator;
import bsuir.labs.helper.MyArray;
import bsuir.labs.helper.MyThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static bsuir.labs.helper.Options.WITHOUT_OPTIONS;


public class Init {

    public static void main(String args[]) throws InterruptedException {

        // Для ожидания завершения потоков перед выводом
        ExecutorService es = Executors.newCachedThreadPool();

        //Инициализация массива вручную
        ///////////////////////////////////////////////////////////////////////////////////////
        /*int n;
        do
            n = InputValidator.CheckInputInt("Введите количество элементов массива: ");
        while( n <= 0);

        int[] temp = new int[n];
        for(int i = 0; i < n; i++)
            temp[i] = InputValidator.CheckInputInt("Элемент " + (i+1) + " = ");*/
        ///////////////////////////////////////////////////////////////////////////////////////


        //Инициализация массива случайными числами (от -20 до 20)
        ///////////////////////////////////////////////////////////////////////////////////////
        int[] temp = new int[10];
        for (int i = 0; i < temp.length; i++)
            temp[i] = ThreadLocalRandom.current().nextInt(-20, 20);
        ///////////////////////////////////////////////////////////////////////////////////////

        MyArray myArray = new MyArray(temp);
        System.out.println("Массив чисел: " + myArray.toString());

        /**Инициализация 4 потоков и их выполнине (метод run())*/
        for (int i = 0; i < 4; i++) {
            //!!Вывод промежуточного результата может быть !непоследовательным!
            //!!в связи с одновременным обращением нескольких потоков
//            Thread.sleep(10);//задержка
            es.execute(new MyThread("Thread" + i, myArray, i + 1, WITHOUT_OPTIONS));
        }

        /** Инициализация 4 потоков и их выполнине (методы start() -> run())
         * с выводом БЕЗ ожидания их завершения*/
        /*for (int i = 0; i < 4; i++) {
            MyThread myThread = new MyThread("Thread" + i, myArray, i + 1, WITHOUT_OPTIONS);
            myThread.start();
        }*/

        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);//Ожидание окончания вполнения потоков

        //Вывод результата
        System.out.println("\nРезультат перемножения элементов массива = " + myArray.GetMultiplyResult());
    }


}
