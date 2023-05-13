package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    //12/05/2023: TODO: add support for C-style comments /* .... */, handle new lines

    //------------------------ private identifiers -----------------------------------
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int offset = 0; //TODO: start from 1 or 0?

    //------------------------ static identifiers ------------------------------------

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    //Constructor
    Scanner(String source){
        this.source = source;
    }

    //iterating over each beginning of a token and calling ~scanToken~ that scans that token
    //when finished, returning back to the while loop to the beginning of a new token.
    List<Token> scanTokens(){
        while(!isAtEnd()){
            //we are currently at the beginning of the next lexeme.
            start = current;
            scanToken(); //scan a ~single~ token
        }

        tokens.add(new Token(TokenType.EOF, "", null, offset, 0));
        return tokens;
    }

    private void scanToken(){
        char c = advance();
        switch(c){
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
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '/':
                if(match('/')) {
                    //A comment goes until the end of the line.
                    //while we are not at the end of the line, keep running
                    while(peek() != '\n' && !isAtEnd()) advance();
                }else{
                    addToken(TokenType.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                //ignoring all whitespaces
                break;

            case '\n':
                offset++;
                break;

            case '"': string();
                break;
            default:
                if(isDigit(c)) {
                    number();
                }else if(isAlpha(c)){
                    identifier();
                }else{
                    Lox.error(offset, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier() {
        while(isAlphaNumeric(peek())) advance();

        //trimming the identifier lexeme itself
        //checking if the lexeme is in the hashmap, if it is then it is a reserved word
        //else, it is an identifier defined by the user
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if(type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private void number(){
        //as long as the current character is a digit, cosume it to the end of the number
        while(isDigit(peek())) advance();

        //Check if there is a fraction in the number consumed
        if(peek() == '.' && isDigit(peekNext())) {
            //consume the '.'
            advance();

            while(isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        //as long as we are still inside the string and inside the file, if we have new line, increment the offset by 1
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n') offset++;
            advance();
        }

        //if we are at the end of the file and not found a '"' then the string didnt close
        if(isAtEnd()){
            Lox.error(offset, "Unterminated string");
            return;
        }
        //we are here if everything worked and we have a '"'.
        advance();

        //trimming the quotes and adding a STRING token with the value inside the quotes
        String value = source.substring(start +1, current -1);
        addToken(TokenType.STRING, value);
    }

    //checks if the given parameter is matching the next lexeme on our file
    //achieving that since 'current' is already pointing to the next lexeme when calling this function.
    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false; //current already points to the next character, because of advance()

        current++;
        return true;
    }

    //returns the current character at 'current' position, if at the end of the line, returns '\0'
    private char peek(){
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    //returns the next character
    private char peekNext(){
        if(current +1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    //returns true if the character is either a letter a-zA-Z or an underscore
    //well use it to determine the first letter of an identifier assumming it is an identifier and not a reserved word
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    //returns true if the character of an identifier is an alphabet or a digit
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    //is the character in c is between 0-9
    private boolean isDigit(char c){
        return c >='0' && c <= '9';
    }

    //returns a boolean whether current is greater than our string's length
    private boolean isAtEnd(){
        return current >= source.length();
    }

    //returning the character at the current position and then advancing current
    private char advance(){
        return source.charAt(current++);
    }

    //addToken function overload: used for single character long lexemes
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    //addToken function overload: used for >1 length lexemes
    private void addToken(TokenType type, Object literal){
        String lexeme = source.substring(start, current); //cutting from the string only the lexeme we currenyly need
        tokens.add(new Token(type, lexeme, literal, offset, lexeme.length()));
    }
}
