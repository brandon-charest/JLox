package lox;

class ErrorLogger {

    //TODO: write error logs to file

    static void error(Token token, String message)
    {
        if(token.type == TokenType.EOF)
        {
            report(token.line, " at end", message);

        }
        else
        {
            report(token.line, " at '"+ token.lexeme +"'", message);
        }
    }

    static void error(int line, String message)
    {
        report(line, "", message);
    }

    private static void report(int line, String where, String message)
    {
        System.err.println(String.format("[Line %d] Error %s: %s",line,where,message));
        Lox.hadError = true;
    }

    static void runTimeError(RunTimeError error)
    {
        String errorMsg = error.getMessage();
        System.err.printf("%s\n[line %d]%n", errorMsg, error.token.line);
        Lox.hadRuntimeError = true;
    }
}
