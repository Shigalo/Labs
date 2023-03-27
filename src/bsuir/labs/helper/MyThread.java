package bsuir.labs.helper;

import java.util.concurrent.Semaphore;

public class MyThread implements Runnable {


    private Thread thred;
    private String threadName;
    private MyArray mainArray;
    private int delay;
    private Options option;
    private Semaphore semaphore;

    public MyThread(String name, MyArray array, int delay, Options option) {
        threadName = name;
        mainArray = array;
        this.delay = delay;
        this.option = option;
    }

    public MyThread(String name, MyArray array, int delay, Semaphore semaphore) {
        threadName = name;
        mainArray = array;
        this.delay = delay;
        this.option = Options.SEMAPHORE_OPTION;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        System.out.println(threadName + ": Начало выполнения потока");

        try {
            while (mainArray.HasNext()) {
//          while (true) {//будет исключение ArrayIndexOutOfBoundsException


                switch (option) {
                    case WITHOUT_OPTIONS:
                        ExecuteWithoutOptions();//Lab1
                        break;
                    case MUTEX_OPTIONS:
                        ExecuteWithMutex();//Lab2 - Мьютексы
                        break;
                    case SEMAPHORE_OPTION:
                        ExecuteWithSemaphore();
                        break;
                    case ATOMIC_OPTION:
                        ExecuteWithAtomic();
                        break;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Error:" + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(threadName + ": получено исключение: Выход за пределы массива. " +
                    "Завершение выполнения потока");

        }
        System.out.println(threadName + ": Выполнение потока завершено");
    }

    public void start() {
        System.out.println("Запущен новый поток: " + threadName);
        if (thred == null) {
            thred = new Thread(this, threadName);
            thred.start();
        }

    }

    /**LAB 1 and 5*/
    private void ExecuteWithoutOptions() throws InterruptedException, ArrayIndexOutOfBoundsException {
        //Может возникать исключение ArrayIndexOutOfBoundsException
        //т.к. проверка на наличие ~ mainArray.HasNext() ~ была заранее в методе run()
        int value = mainArray.GetNext();
        long result = mainArray.Multiply(value);
        System.out.println(threadName + ": получено значение " + value
                + ". Промежуточный результат перемножения = " + result);
        Thread.sleep(delay * 100);
    }

    /**LAB 2*/
    private void ExecuteWithMutex() throws InterruptedException {
        try {
            mainArray.mutex.lock(); //Блокировка доступа к объекту для других потоков

            /*if(!mainArray.HasNext()) { // Избавление от исключения
                mainArray.mutex.unlock();
                return; }*/

            //Причина возникновения исключения: Проверка mainArray.HasNext()
            //выполняется до блокировки mainArray.mutex.lock() в run()
            ExecuteWithoutOptions();

            mainArray.mutex.unlock(); //Разблокировка доступа к объекту для других потоков
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(threadName + ": получено исключение: Выход за пределы массива. Завершение выполнения потока");
            mainArray.mutex.unlock(); //Разблокировка при исключении
        }
    }

    /**LAB 3*/
    private void ExecuteWithSemaphore() throws InterruptedException {
        try {
            System.out.println(threadName + ": Пытается получить доступ. "/* +
                    "Свободно = " + semaphore.availablePermits() +
                    "Потоков в очереди = " + semaphore.getQueueLength()*/);
            if(!semaphore.tryAcquire()) {
                System.out.println(threadName + ": Доступ запрещён. Поток ожидает");
                semaphore.acquire();
            }
            System.out.println(threadName + ": Доступ получен");

            /*if(!mainArray.HasNext()) { // Избавление от исключения
                mainArray.mutex.unlock();
                return; }*/

            //Причина как и в мьютексах
            ExecuteWithoutOptions();


            System.out.println(threadName + ": Операция завершена. Освобожнеие дотупа");
            semaphore.release();

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(threadName + ": получено исключение: Выход за пределы массива. Завершение выполнения потока");
        }
        Thread.sleep(300);

    }

    /**LAB 4*/
    private synchronized void ExecuteWithAtomic() throws InterruptedException {
        int value = mainArray.GetNext();
        long result = mainArray.MultiplyAtomic(value);
        System.out.println(threadName + ": получено значение " + value
                + ". Промежуточный результат перемножения = " + result);
        Thread.sleep(delay * 100);
    }
}

