package lox;

public class ErrorLogger {

    //TODO: write error logs to file

    public static void error(int line, String message)
    {
        report(line, "", message);
    }

    private static void report(int line, String where, String message)
    {
        System.err.println(String.format("[Line %d] Error %s: %s",line,where,message));
    }
}
