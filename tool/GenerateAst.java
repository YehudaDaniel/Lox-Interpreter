package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

//Automating the creation of several classes inside the "lox" package, instead of writing them one by one
public class GenerateAst {
    public static void main(String[] args) throws IOException{
//        if(args.length != 1) { //wrong number of arguments given with the execution of the main
//            System.err.println("Usage: generate-ast <output directory>");
//            System.exit(64); //the command was used incorrectly
//        }
//        String outputDir = args[0];

        //each of these string is a name of a class, followed by the list of fields it has, seperated by commas (Type name)
        defineAst("src/lox", "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    private static void defineAst(
            String outputDir, String baseName, List<String> types)
            throws IOException {

        String path = outputDir + "/" + baseName + ".java"; //the basic chosen name of the class file
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        //writing to the file
        writer.println("package lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName+ " {");

        //Abstract Syntax Tree classes
        for(String type : types){
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }

    private static void defineType(
            PrintWriter writer, String baseName, String className, String fieldList) {

        writer.println("    static class " + className + " extends " + baseName + " {");

        //Constructor
        writer.println("        " + className + "(" + fieldList + ") {");

        //Store all the given parameter of the constructor in relevant fields
        String[] fields = fieldList.split(", ");
        for(String field: fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        //Fields
        writer.println();
        for(String field : fields) {
            writer.println("        final " + field + ";");
        }

        writer.println("    }");
    }
}






















