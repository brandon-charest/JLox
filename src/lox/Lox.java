package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException
    {
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


    private static void runFile(String path)  throws  IOException
    {
        try
        {
            byte[] bytes  = Files.readAllBytes(Paths.get(path));
            run(new String(bytes, Charset.defaultCharset()));

            if(hadError)
            {
                System.exit(64);
            }

            if(hadRuntimeError)
            {
                System.exit(70);
            }

        }
        catch (IOException e)
        {
           throw new IOException(e);
        }
    }

    private static void runPrompt() throws  IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while(true)
        {
            System.out.println("> ");
            String line = reader.readLine();
            if(line == null) {break;}
            run(line);
            hadError = false;
        }
    }

    private static void run(String source)
    {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        if(hadError)
        {
            return;
        }

        interpreter.interpret(expression);

        System.out.println(new AstPrinter().print(expression));
    }
}
