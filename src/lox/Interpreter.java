package lox;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Statement.Visitor<Void>
{

    void interpret(List<Statement> stmts)
    {
        try
        {
            for (Statement stmt: stmts)
            {
                execute(stmt);
            }
        }
        catch (RunTimeError error)
        {
           ErrorLogger.runTimeError(error);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr)
    {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type)
        {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            // PLUS could be used for string concatenation or arithmetic operation
            case PLUS:
                if(left instanceof Double && right instanceof Double)
                {
                    return (double)left + (double)right;
                }

                if(left instanceof String && right instanceof String)
                {
                    return (String)left + (String)right;
                }

                throw new RunTimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            default:
                break;
        }

        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr)
    {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr)
    {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr)
    {
        Object right = evaluate(expr.right);

        switch (expr.operator.type)
        {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            default:
                break;
        }

        return null;
    }

    @Override
    public Object visitConditionalExpr(Expr.Conditional expr)
    {
        Object left = evaluate(expr.thenBranch);

        if(isTruthy(left))
        {
            return left;
        }
        else
        {
            if(!isTruthy(left))
            {
                return left;
            }
        }

        return evaluate(expr.elseBranch);
    }

    private String stringify(Object object)
    {
        if(object == null)
        {
            return "nil";
        }

        String text = object.toString();
        // Remove ".0" from integer-valued doubles
        if(object instanceof Double)
        {
            if (text.endsWith(".0"))
            {
                text = text.substring(0, text.length()-2);
            }
        }

        return text;
    }

    private void checkNumberOperand(Token operator, Object operand)
    {
        if(operand instanceof Double)
        {
            return;
        }
        throw new RunTimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right)
    {
        if(left instanceof Double && right instanceof Double)
        {
            return;
        }
        throw new RunTimeError(operator, "Operands must be a number.");
    }

    //False and 'nil' are considered false in lox
    //everything else is true
    private Boolean isTruthy(Object object)
    {
        if(object == null)
        {
            return false;
        }

        if(object instanceof Boolean)
        {
            return (boolean)object;
        }

        return true;
    }

    private Object evaluate(Expr expr)
    {
        return expr.accept(this);
    }

    private void execute(Statement stmt)
    {
        stmt.accept(this);
    }

    private Boolean isEqual(Object x, Object y)
    {
        if(x == null && y == null)
        {
            return true;
        }

        if(x == null)
        {
            return false;
        }

        return x.equals(y);
    }

    @Override
    public Void visitPrintStmt(Statement.Print stmt)
    {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Statement.Expression stmt)
    {
        evaluate(stmt.expression);
        return null;
    }
}
