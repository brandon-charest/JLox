package lox;

import java.util.List;

abstract class Expr {
  interface Visitor<R>
  {
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
    R visitConditionalExpr(Conditional expr);
    R visitVariableExpr(Variable expr);
  }



 static class Binary extends Expr
 {
    final Expr left;
    final Token operator;
    final Expr right;
    Binary(Expr left, Token operator, Expr right)
    {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    <R> R accept(Visitor<R> visitor)
    {
        return visitor.visitBinaryExpr(this);
    }
 }
 static class Grouping extends Expr
 {
    final Expr expression;
    Grouping(Expr expression)
    {
        this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor)
    {
        return visitor.visitGroupingExpr(this);
    }
 }
 static class Literal extends Expr
 {
    final Object value;
    Literal(Object value)
    {
        this.value = value;
    }

    <R> R accept(Visitor<R> visitor)
    {
        return visitor.visitLiteralExpr(this);
    }
 }
 static class Unary extends Expr
 {
    final Token operator;
    final Expr right;
    Unary(Token operator, Expr right)
    {
        this.operator = operator;
        this.right = right;
    }

    <R> R accept(Visitor<R> visitor)
    {
        return visitor.visitUnaryExpr(this);
    }
 }

    static class Conditional extends Expr
    {
        final Expr expr;
        final Expr elseBranch;
        final Expr thenBranch;
        Conditional(Expr expr, Expr thenBranch,Expr elseBranch)
        {
            this.expr = expr;
            this.elseBranch = elseBranch;
            this.thenBranch = thenBranch;
        }

        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitConditionalExpr(this);
        }
    }

    static class Variable extends Expr
    {
        final Token name;

        Variable(Token name)
        {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitVariableExpr(this);
        }
    }

  abstract <R> R accept(Visitor<R> visitor);
}
