package bsuir.labs.helper;

import java.util.Scanner;

//Паттерн singleton (для ввода через Scanner)
public class InputValidator {

    public static final InputValidator instance = new InputValidator();
    private static Scanner sc;

    private InputValidator() {
        sc = new Scanner(System.in);
    }

    public static int CheckInputInt(String query) {//Проверка ввода числа
        int result;
        do {
            System.out.print(query);
            if (sc.hasNextInt()) { //Число => возврат
                result = sc.nextInt();
                return result;
            }
            //Не число => очистка ввода
            System.out.println(sc.nextLine() + " is not Integer");
        } while (true);

    }

    public static double CheckInputDouble(String query) {//Проверка ввода числа
        double result;
        do {
            System.out.print(query);
            if (sc.hasNextDouble()) { //Число => возврат
                result = sc.nextDouble();
                return result;
            }
            //Не число => очистка ввода
            System.out.println(sc.nextLine() + " is not Double");
        } while (true);

    }
}
