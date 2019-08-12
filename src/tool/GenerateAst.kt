package tool

import java.io.File
import java.io.PrintWriter

fun main()
{

    var outputDir = "src/lox/"
    defineAst(outputDir, "Expr",
            listOf("Binary   : Expr left, Token operator, Expr right",
                    "Grouping : Expr expression",
                    "Literal  : Object value",
                    "Unary    : Token operator, Expr right"))
}

fun defineAst(outputDir: String, baseName: String, types: List<String>)
{
    var path = "$outputDir/$baseName.java"
    var writer = File(path).printWriter()

    writer.use{ out ->
        out.println("package lox;")
        out.println()
        out.println("import java.util.List;")
        out.println()
        out.println("abstract class $baseName {")

        defineVisitor(writer, baseName, types)

           types.forEach()
           {
               var className = it.split(":")[0].trim()
               var fields = it.split(":")[1].trim()
               defineType(writer, baseName, className, fields)
           }
        out.println()
        out.println("  abstract <R> R accept(Visitor<R> visitor);")
        out.println("}")
        out.close()
    }
}

fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>)
{
    writer.println("  interface Visitor<R>")
    writer.println("  {")
    types.forEach()
    {
        var typeName = it.split(":")[0].trim()
        writer.println("    R visit$typeName$baseName($typeName ${baseName.toLowerCase()});")
    }
    writer.println("  }")
}

fun defineType(writer: PrintWriter, baseName: String, className:String, fieldList: String)
{
    writer.println(" static class $className extends $baseName")
    writer.println(" {")
    var fields = fieldList.split(", ")

    fields.forEach()
    {
        writer.println("    final $it;")
    }
    //Constructor:
    writer.println("    $className($fieldList)")
    writer.println("    {")
    fields.forEach()
    {
        var name = it.split(" ")[1]
        writer.println("        this.$name = $name;")
    }

    writer.println("    }")
    writer.println()
    writer.println("    <R> R accept(Visitor<R> visitor)")
    writer.println("    {")
    writer.println("        return visitor.visit$className$baseName(this);")
    writer.println("    }")
    writer.println(" }")
}
