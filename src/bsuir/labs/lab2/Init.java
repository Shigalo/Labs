package bsuir.labs.lab2;

import bsuir.labs.helper.MyArray;
import bsuir.labs.helper.MyThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static bsuir.labs.helper.Options.MUTEX_OPTIONS;

//Мьютексы (гарантируют, что к объекту имеет доступ только 1 поток)
public class Init {

    public static void main(String args[]) throws InterruptedException {

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
        //!!Вывод промежуточного результата НЕ может повторятся
        //!!в связи с использованием мьютекса (только 1 поток может взаимодействовать, остальные ждут)
        for (int i = 0; i < 4; i++)
            es.execute(new MyThread("Thread" + i, myArray, i + 1, MUTEX_OPTIONS));

        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);//Ожидание окончания вполнения потоков

        //Вывод результата
        System.out.println("\nРезультат перемножения элементов массива = " + myArray.GetMultiplyResult());
    }

}
