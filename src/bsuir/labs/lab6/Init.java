package bsuir.labs.lab6;


import bsuir.labs.helper.Matrix;
import bsuir.labs.helper.Options;

public class Init {
    public static void main(String args[]) throws InterruptedException {

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
        Matrix algAddsMatrix = matrix.GetAlgAddsMatrix(Options.WITHOUT_OPTIONS);
        System.out.println("\nМатрица алгебраических дополнений:\n" + algAddsMatrix.toString());

        //Присоединённая матрица(транспонируем матрицу алгебраических дополнений)
        Matrix trAlgAddsMatrix = new Matrix(algAddsMatrix.GetTranspMatrix());
        System.out.println("Присоединённая матрица:\n" + trAlgAddsMatrix.toString());

        //Получение обратной матрицы
        trAlgAddsMatrix.DivideAll(matrix.GetDeterminant());
        System.out.println("Обратная матрица:\n" + trAlgAddsMatrix.toString());
    }


}
