package bsuir.labs.lab3;

import bsuir.labs.helper.MyArray;
import bsuir.labs.helper.MyThread;

import java.util.concurrent.*;

import static bsuir.labs.helper.Options.MUTEX_OPTIONS;

//Семафоры (гарантируют, что к объекту имеет доступ ОГРАНИЧЕННОЕ количество потоков)
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
        //!!Вывод промежуточного результата может !повторятся! или быть !непоследовательным!
        //!!в связи с использованием семафора (несколько потоков могут взаимодействовать одновременно)
        Semaphore semaphore = new Semaphore(2);//Макс потоков одновременно = 2
        for (int i = 0; i < 4; i++)
            es.execute(new MyThread("Thread" + i, myArray, i + 1, semaphore));

        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);//Ожидание окончания вполнения потоков

        //Вывод результата
        System.out.println("\nРезультат перемножения элементов массива = " + myArray.GetMultiplyResult());
    }

}
