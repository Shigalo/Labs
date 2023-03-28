package bsuir.labs.helper;

import java.util.concurrent.Semaphore;

public class MatrixThread implements Runnable {

    private Thread thred;
    private String threadName;
    private Matrix mainMatix;
    private Matrix rMatrix;
    private int eLine;
    private int eCol;
    private Options option;
    private Semaphore semaphore;

    public MatrixThread(String name, Matrix matrix, Matrix rMatrix, int eLine, int eCol, Options option) {
        threadName = name;
        mainMatix = matrix;
        this.rMatrix = rMatrix;
        this.option = option;
        this.eLine = eLine;
        this.eCol = eCol;
    }

    public MatrixThread(String name, Matrix matrix, Matrix rMatrix, int eLine, int eCol, Semaphore semaphore) {
        threadName = name;
        mainMatix = matrix;
        this.rMatrix = rMatrix;
        this.eLine = eLine;
        this.eCol = eCol;
        this.option = Options.SEMAPHORE_OPTION;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        System.out.println(threadName + ": Начало выполнения потока");

        StringBuilder resultOutput = new StringBuilder(threadName).append(": Элемент a[")
                .append(eLine + 1)
                .append("][")
                .append(eCol + 1)
                .append("] = ")
                .append(OutputHelper.DoubleOutput(mainMatix.GetValue(eLine, eCol)));
        try {
            switch (option) {
                /**Lab 2*/
                case MUTEX_OPTIONS:
                    ExecuteWithMutex(resultOutput);
                    break;
                /**Lab 3*/
                case SEMAPHORE_OPTION:
                    ExecuteWithSemaphore(resultOutput);
                    break;
            }

        } catch (InterruptedException e) {
            System.out.println("Error:" + e.getMessage());
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

    //Нахождение элементов матрицы алгебраических дополнений в потоках
    private void Execute(StringBuilder resultOutput) {
        double aa = mainMatix.GetAlgAddition(eLine, eCol, resultOutput);//Вычисление алгебраического дополнениея
        rMatrix.SetValue(aa, eLine, eCol);//Запись в таблицу дополнений
        System.out.println(resultOutput);// Вывод результата выполнения потока
    }

    /**LAB 2*/
    private void ExecuteWithMutex(StringBuilder resultOutput) throws InterruptedException {
        mainMatix.mutex.lock(); //Блокировка доступа к объекту для других потоков
        Execute(resultOutput);//Выполнение
        mainMatix.mutex.unlock(); //Разблокировка доступа к объекту для других потоков
    }

    /**LAB 3*/
    private void ExecuteWithSemaphore(StringBuilder resultOutput) throws InterruptedException {
            System.out.println(threadName + ": Пытается получить доступ."/* +
                    "\n\tСвободно = " + semaphore.availablePermits() +
                    "\n\tПотоков в очереди = " + semaphore.getQueueLength()*/);
            if(!semaphore.tryAcquire()) {//Проверка доступа и его получение если возможно
                System.out.println(threadName + ": Доступ запрещён. Поток ожидает");
                semaphore.acquire();//Ожидание доступа
            }
            System.out.println(threadName + ": Доступ получен");

            Execute(resultOutput);//Выполнение

            System.out.println(threadName + ": Операция завершена. Освобожнеие дотупа");
            Thread.sleep(300);
            semaphore.release();
    }
}

