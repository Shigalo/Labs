package bsuir.labs.lab5;

import bsuir.labs.helper.MyArray;
import bsuir.labs.helper.MyThread;

import java.util.concurrent.*;

import static bsuir.labs.helper.Options.WITHOUT_OPTIONS;

//Фьючерсы
public class Init {

    public static void main(String args[]) throws InterruptedException, ExecutionException {

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
            MyThread myThread = new MyThread("Thread" + i, myArray, ++i, WITHOUT_OPTIONS);
            myThread.start();
            Thread.sleep(200);
        }


        System.out.println("\nРезультат перемножения элементов массива = " + myArray.GetMultiplyResult() +
                "\nБыло запущено " + i + " потоков");
        System.out.println();
        es.shutdown();
    }
}
