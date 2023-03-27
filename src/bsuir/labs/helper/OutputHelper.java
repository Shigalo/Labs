package bsuir.labs.helper;

public class OutputHelper {

    public static String IntOutput(int i) {
        if(i<0)
            return "(" + i + ")";
        return "" + i;
    }

    public static String DoubleOutput(double i) {
        String result = "" + i;
        if(i % 1 == 0)
            result = "" + (int)i;
        if(i<0)
            return "(" + result + ")";
        return result;
    }
}
