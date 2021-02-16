// Esercizi 2.1, 2.2, 2.3
import java.io.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        while (peek == '/') {
            readch(br);
            if (peek == '/') { // semplice commento
                do {
                    readch(br);
                } while (peek != '\n' && peek != (char)-1);
                if (peek == '\n') {
                    line++; readch(br);
                }
            } else if (peek == '*') { // multi commento
                boolean out = false, preChiusso = false;
                do {
                    readch(br);

                    if (peek == '*') {
                        preChiusso = true;
                    } else if (preChiusso && peek == '/') {
                        out = true;
                        peek = ' ';
                    } else {
                        if (peek == '\n') {
                            line++;
                        }
                        preChiusso = false;
                    }

                } while (!out && peek != (char)-1);

                if (out) {
                    while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
                        if (peek == '\n') line++;
                        readch(br);
                    }
                } else { // non chiuso
                    System.err.println("Errore: Commento /*..*/ non chiuso");
                    return null;
                }
                
            } else { // division
                return Token.div;
            }
        }

        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

            case '(':
                peek = ' ';
                return Token.lpt;
            
            case ')':
                peek = ' ';
                return Token.rpt;

            case '{':
                peek = ' ';
                return Token.lpg;

            case '}':
                peek = ' ';
                return Token.rpg;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '*':
                peek = ' ';
                return Token.mult;

            case '/':
                peek = ' ';
                return Token.div;

            case '=':
                peek = ' ';
                return Token.assign;

            case ';':
                peek = ' ';
                return Token.semicolon;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }

            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            
            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else if (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
                    peek = ' ';
                    return Word.lt;
                } else if (Character.isLetter(peek) || Character.isDigit(peek)) {
                    return Word.lt;
                } else {
                    System.err.println("Erroneous character"
                            + " after < : "  + peek );
                    return null;
                }

            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else if (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
                    peek = ' ';
                    return Word.gt;
                } else if (Character.isLetter(peek) || Character.isDigit(peek)) {
                    return Word.gt;
                } else {
                    System.err.println("Erroneous character"
                            + " after > : "  + peek );
                    return null;
                }

            case (char)-1:
                return new Token(Tag.EOF);
            
            default:
                if (Character.isLetter(peek) || peek == '_') {
                    String aux = String.valueOf(peek);
                    boolean ok = Character.isLetter(peek);
                    readch(br);
                    while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
                        aux += String.valueOf(peek);
                        if (!ok && (Character.isLetter(peek) || Character.isDigit(peek))) {
                            ok = true;
                        }
                        readch(br);
                    }
                        
                    if (aux.equals("cond")) {
                        return Word.cond;
                    } else if (aux.equals("when")) {
                        return Word.when;
                    } else if (aux.equals("then")) {
                        return Word.then;
                    } else if (aux.equals("else")) {
                        return Word.elsetok;
                    } else if (aux.equals("while")) {
                        return Word.whiletok;
                    } else if (aux.equals("do")) {
                        return Word.dotok;
                    } else if (aux.equals("seq")) {
                        return Word.seq;
                    } else if (aux.equals("print")) {
                        return Word.print;
                    } else if (aux.equals("read")) {
                        return Word.read;
                    } else {
                        if (ok) {
                            return new Word(Tag.ID, aux);
                        } else {
                            System.err.println("Identifactore invalido");
                            return null;
                        }
                        
                    }
                    
                } else if (Character.isDigit(peek)) {
                    // If number followed by letter error  
                    int aux = Character.getNumericValue(peek);
                    readch(br);
                    while (Character.isDigit(peek)) {
                        aux = aux*10 + Character.getNumericValue(peek);
                        readch(br);
                    }

                    return new NumberTok(Tag.NUM, aux);

                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test_lexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
