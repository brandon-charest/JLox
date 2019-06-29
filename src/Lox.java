import java.io.IOException;

public class Lox {
    public static void main(String[] args) throws IOException {
        if(args.length > 1)
        {
            System.out.println("Usage: jlox [args]");
            System.exit(64);
        }
        else if(args.length == 1)
        {
            runFile(args[0]);
        }
        else
        {
            runPrompt();
        }
    }


    private static void runFile(String path) throws IOException {

    }

    private static void runPrompt() throws  IOException {

    }

}
