package lox;

import java.util.ArrayList;
import java.util.List;
import static lox.TokenType.*;

public class Scanner
{

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source)
    {
        this.source = source;
    }

    List<Token> scanTokens()
    {
        while (!isAtEnd())
        {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken()
    {
        char c = nextToken();
        switch (c)
        {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '?': addToken(QUESTION); break;
            case ':': addToken(COLON); break;
            //need to check if char following is a '='
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
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
                    addToken(SLASH);
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
        TokenType type = ReservedWords.keywords.get(word);

        if(type == null)
        {
            type = IDENTIFIER;
        }

        addToken(type);
    }

    private void blockComment()
    {
        while(!isAtEnd())
        {
            while(!match('*') && !isAtEnd())
            {
                if(peek() == '\n')
                {
                    line++;
                }
                nextToken();
            }

            if(!match('/'))
            {
                ErrorLogger.error(line, "Comment does not terminate.");
            }
            break;
        }
    }

    private void number()
    {
        while(isDigit(peek()))
        {
            nextToken();
        }

        if(peek() == '.' && isDigit(peekNext()))
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

        // The closing ".
        nextToken();

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

    private char peekNext()
    {
        if(current + 1 >= source.length())
        {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private char nextToken()
    {
        current++;
        return source.charAt(current -1);
    }
}
