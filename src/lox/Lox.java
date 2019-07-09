package src.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    public static boolean hasError;

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

            if(hasError)
            {
                System.exit(64);
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
            run(reader.readLine());
        }
    }

    private static void run(String source)
    {
        Parser parse = new Parser(source);
        List<Token> tokens = parse.parseTokens();

        for (Token token: tokens)
        {
            System.out.println(token);
        }
    }

}
