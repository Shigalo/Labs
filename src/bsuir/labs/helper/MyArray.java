package bsuir.labs.helper;

import sun.awt.Mutex;

import java.util.concurrent.Semaphore;

public class MyArray {

    private int[] array;
    private int index = 0;
    private long multiplyResult = 1;
    private volatile long atomicMultiplyResult = 1;
    //volatile - обеспечивает корректную видимость между потоками (для атомарности - Lab4)

    //lab 2
    public Mutex mutex = new Mutex();

    //lab 3
    public MyArray(int[] array) {
        this.array = array;
    }

    // Проверка на выход за пределы массива
    public boolean HasNext() {
        return index < array.length;
    }

    //Получение следующего элемента
    public int GetNext() throws ArrayIndexOutOfBoundsException {
        return array[index++];
    }

    public long Multiply(int value) {
        multiplyResult *= value;
        return multiplyResult;
    }

    public synchronized long MultiplyAtomic(int value) {
        atomicMultiplyResult *= value;
        return atomicMultiplyResult;
    }

    public long GetMultiplyResult() {
        return multiplyResult;
    }

    public long GetAtomicMultiplyResult() {
        return multiplyResult;
    }

    public String toString() {
        String result = "";
        for(int element : array)
            result += " " + element;
        return result;
    }
}
