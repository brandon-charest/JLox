package lox;

import java.util.ArrayList;
import java.util.List;
import static lox.TokenType.*;

/*
* Parser Class
* Recursive Decent Parsing
* */
public class Parser
{
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;
    private final String missing_left_operand = "Missing left-hand operand.";

    Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    List<Statement> parse()
    {
       List<Statement> statements = new ArrayList<>();
       while(!isAtEnd())
       {
           statements.add(declaration());
       }
       return statements;
    }

    private Statement statement()
    {
        if(match(PRINT))
        {
            return printStatement();
        }
        return expressionStatement();
    }

    private Statement printStatement()
    {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");

        return new Statement.Print(value);
    }

    private Statement expressionStatement()
    {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");

        return new Statement.Expression(value);
    }

    private Statement declaration()
    {
        try
        {
            if(match(VAR))
            {
                return varDeclaration();
            }
            return statement();
        }
        catch (ParseError error)
        {
            synchronize();
            return null;
        }
    }

    private Statement varDeclaration()
    {
        Token name = consume(IDENTIFIER, "Expect variable name.");
        Expr initializer = null;

        if(match(EQUAL))
        {
            initializer = expression();
        }
        consume(SEMICOLON,"Expect ';' after variable declaration.");
        return new Statement.Var(name, initializer);
    }

    private Expr expression()
    {
        return conditional();
    }

    private Expr equality()
    {
        Expr expr = comparison();

        while(match(BANG_EQUAL, EQUAL_EQUAL))
        {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison()
    {
        Expr expr = addition();

        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL))
        {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr addition()
    {
        Expr expr = multiplication();

        while(match(MINUS, PLUS))
        {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr multiplication()
    {
        Expr expr = unary();

        while(match(SLASH, STAR))
        {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr conditional()
    {
        Expr expr = equality();

        if(match(QUESTION))
        {
            Expr thenBranch = expression();
            consume(COLON, "Expected ':' after then branch of conditional expression.");
            Expr elseBranch = conditional();
            expr = new Expr.Conditional(expr, thenBranch, elseBranch);
        }
        return expr;
    }

    private Expr unary()
    {
        if(match(BANG, MINUS))
        {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }


    private Expr primary()
    {
        if(match(FALSE))
        {
            return new Expr.Literal(false);
        }

        if(match(TRUE))
        {
            return new Expr.Literal(true);
        }

        if(match(NIL))
        {
            return new Expr.Literal(null);
        }

        if(match(NUMBER, STRING))
        {
            return new Expr.Literal(previous().literal);
        }

        if(match(IDENTIFIER))
        {
            return new Expr.Variable(previous());
        }

        if(match(LEFT_PAREN))
        {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if(match(BANG_EQUAL, EQUAL_EQUAL))
        {
            error(previous(), missing_left_operand);
            equality();
            return null;
        }

        if(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL))
        {
            error(previous(), missing_left_operand);
            comparison();
            return null;
        }

        if(match(PLUS))
        {
            error(previous(), missing_left_operand);
            addition();
            return null;
        }

        if(match(SLASH, STAR))
        {
            error(previous(), missing_left_operand);
            multiplication();
            return null;
        }

        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message)
    {
        if(check(type))
        {
            return advance();
        }

        throw error(peek(), message);
    }

    private boolean match(TokenType... types)
    {
        for (TokenType type : types)
        {
            if(check(type))
            {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type)
    {
        if(isAtEnd())
        {
            return false;
        }
        return peek().type == type;
    }

    //Consumes current token and returns it
    private Token advance()
    {
        if(!isAtEnd())
        {
            current++;
        }
        return previous();
    }

    //Checks if out of tokens to parse
    private boolean isAtEnd()
    {
        return peek().type == EOF;
    }

    //Returns current token that has yet to be consumed
    private Token peek()
    {
        return tokens.get(current);
    }

    //Returns most recently consumed token
    private Token previous()
    {
        return tokens.get(current -1);
    }

    private ParseError error(Token token, String message)
    {
        ErrorLogger.error(token, message);
        return new ParseError();
    }

    //Discard tokens until we reach a statement boundary
    private void synchronize()
    {
        advance();

        while(!isAtEnd())
        {
            if(previous().type == SEMICOLON)
            {
                return;
            }

            switch (peek().type)
            {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }
}
