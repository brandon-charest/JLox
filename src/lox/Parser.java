package src.lox;

import java.util.ArrayList;
import java.util.List;

public class Parser
{

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Parser(String source)
    {
        this.source = source;
    }

    List<Token> parseTokens()
    {
        while (!isAtEnd())
        {

            start = current;
            parseToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void parseToken()
    {
        char c = nextToken();
        switch (c)
        {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            //need to check if char following is a '='
            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case '/':
                // Check if the first '/' belongs to a comment.
                // if so take the rest of the tokens in the line.
                if(match('/'))
                {
                    while(peek() != '\n' && !isAtEnd())
                    {
                        nextToken();
                    }
                }
                else if(match('*'))
                {
                    blockComment();
                }
                else
                {
                    addToken(TokenType.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if(isDigit(c))
                {
                    number();
                }
                else if(isAlpha(c))
                {
                    identifier();
                }
                else
                {
                    ErrorLogger.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier()
    {
        while (isAlphaNumeric(peek()))
        {
            nextToken();
        }

        String word  = source.substring(start, current);
        TokenType type = ReseredWords.keywords.get(word);

        if(type == null)
        {
            type = TokenType.IDENTIFIER;
        }

        addToken(type);
    }

    private void blockComment()
    {
        //TODO finish block comment
    }

    private void number()
    {
        while(isDigit(peek()))
        {
            nextToken();
        }

        if(peek() == '.' && isDigit(peek(1)))
        {
            // consume '.'
            nextToken();

            // get rest of number
            while(isDigit(peek()))
            {
                nextToken();
            }
        }

        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void string()
    {
        while(peek() != '"' && !isAtEnd())
        {
            if(peek() == '\n')
            {
                line++;
            }
            nextToken();
        }

        if(isAtEnd())
        {
            ErrorLogger.error(line, "Unterminated string.");
            return;
        }

        //Trim surrounding quotes
        String value = source.substring(start + 1, current -1);
        addToken(TokenType.STRING, value);
    }

    private void addToken(TokenType type)
    {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal)
    {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAlpha(char c)
    {
        char tempChar = Character.toLowerCase(c);
        return (tempChar >= 'a' && tempChar <= 'z') || c == '_';
    }

    private boolean isAlphaNumeric(char c)
    {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    private boolean match(char expected)
    {
        if(isAtEnd())
        {
            return false;
        }
        if(source.charAt(current) != expected)
        {
            return false;
        }
        current++;
        return true;
    }
    //Check if at end of characters
    private boolean isAtEnd()
    {
        return current >= source.length();
    }
    // look ahead 1 character. Does not consume current character
    private char peek()
    {
        if(isAtEnd())
        {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peek(int lookahead)
    {
        if(current + lookahead >= source.length())
        {
            return '\0';
        }
        return source.charAt(current + lookahead);
    }

    private char nextToken()
    {
        current++;
        return source.charAt(current -1);
    }
}
