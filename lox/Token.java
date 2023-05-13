package lox;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int offset;
    final int length;

    //using the offset we can get the line by counting the number of "\n"-s
    //and the column by offset - (offset to last "\n")

    Token(TokenType type, String lexeme, Object literal, int offset, int length){
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.offset = offset;
        this.length = length;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
