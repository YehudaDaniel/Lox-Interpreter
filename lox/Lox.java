package lox;

import lox.Scanner;
import lox.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class Lox {
    static boolean hadError = false;

    //The main would get a path to a file to run from the command line
    public static void main(String[] args) throws IOException {
        if(args.length > 1){
            System.out.println("Usage: Jlox [Script]");
            System.exit(64); //exit Code: The command was used with wrong number of arguments / bad flag / bad syntax / etc
        }else if(args.length == 1){
            runFile(args[0]); //running the given file
        }else{
            runPrompt(); //running a command line prompt like in python, if no file path was given
        }
    }

    private static void runFile(String path) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(path)); //converting the files strings into bytes
        run(new String(bytes, Charset.defaultCharset())); //converting the bytes back to a string

        //Indicate an error in the exit code
        if(hadError) System.exit(65); // exit Code: user's input data was incorrect
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.print("> ");
            String line = reader.readLine(); //Cntrl + D on the cmd will return null
            if(line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for(Token token : tokens){
            System.out.println(token);
        }
    }
    //given a line integer and a string message, will return a report about an occcurred error
    //TODO: implement a better error message that givens the exact column as well (using the offset)
    static void error(int offset, String message){
        report(offset, "", message);
    }

    private static void report(int line, String where, String message){
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}





