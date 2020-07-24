package lox;

public class RunTimeError extends RuntimeException
{
    private final Token token;

    RunTimeError(Token token, String message)
    {
        super(message);
        this.token = token;
    }
}
