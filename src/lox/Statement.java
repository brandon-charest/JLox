package lox;

abstract class Statement {
    interface Visitor<R>
    {
        R visitPrintStmt(Print stmt);
        R visitExpressionStmt(Expression stmt);
        R visitVariableStmt(Expression stmt);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Print extends Statement
    {
        final Expr expression;

        Print(Expr expression)
        {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitPrintStmt(this);
        }
    }

    static class Expression extends Statement
    {
        final Expr expression;

        Expression(Expr expression)
        {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class Var extends Statement
    {
        final Token name;
        final Expr initializer;

        Var(Token name, Expr initializer)
        {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return null;
        }
    }
}
