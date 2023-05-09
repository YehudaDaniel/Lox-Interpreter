import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int offset = 1;


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
                    //Acomment goes until the end of the line.
                    while(peek() != '\n' && !isAtEnd()) advance();
                }else{
                    addToken(TokenType.SLASH);
                }

            default:
                Lox.error(offset, "Unexpected character.");
                break;
        }
    }

    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false; //current already points to the next character, because of advance()

        current++;
        return true;
    }

    //returns a boolean whether or not current is greater than our string's length
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
