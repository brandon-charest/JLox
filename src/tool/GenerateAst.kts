package tool

import java.io.File
import java.io.PrintWriter
import java.util.*

fun main(args: Array<String>)
{
    if(args.size != 1)
    {
        print("Usage: generate_ast <output directory>")
        System.exit(1)
    }

    var outputDir = args[0]
    defineAst(outputDir, "Expr", Arrays.asList(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Unary    : Token operator, Expr right"
    ))
}

fun defineAst(outputDir: String, baseName: String, types: List<String>)
{
    var path = outputDir + "/" + baseName + ".java"
    var writer = File(path).printWriter()

    writer.use{ out ->
        out.println("package src.lox;")
        out.println()
        out.println("import java.util.List;")
        out.println()
        out.println("abstract class " + baseName + " {")
           types.forEach()
           {
               var className = it.split(":")[0].trim()
               var fields = it.split(":")[1].trim()
               defineType(writer, baseName, className, fields)
           }
        out.println("}")
        out.close()
    }
}

fun defineType(writer: PrintWriter, baseName: String, className:String, fieldList: String)
{
    writer.println(" static class $className extends $baseName")
    writer.println(" {")
    //Constructor:
    writer.println("    $className($fieldList)")
    writer.println("    {")


    var fields = fieldList.split(", ")

    fields.forEach()
    {
        var name = it.split(" ")[1]
        writer.println("        this.$name = $name;")
    }

    writer.println("    }")
    writer.println()

    fields.forEach()
    {
        writer.println("    final $it;")
    }

    writer.println("}")
}
