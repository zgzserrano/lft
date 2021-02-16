// Esercizio 3.1
import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	    throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    public void start() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            expr();
            match(Tag.EOF);
        } else {
            error ("Error in start");
        }
    }

    private void expr() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            term();
            exprp();
        } else {
            error("Error in expr");
        }
    }

    private void exprp() {
        switch (look.tag) {
            case '+':
                match('+');
                term();
                exprp();
                break;
            case '-':
                match('-');
                term();
                exprp();
                break;
            case ')':   
            case Tag.EOF:
            case '$':
                break;
            default:
                error("Error in exprp");
        }
    }

    private void term() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            fact();
            termp();
        } else {
            error("Error in term");
        }
    }

    private void termp() {
        switch(look.tag) {
            case '*':
                match(Token.mult.tag);
                fact();
                termp();
                break;
            case '/':
                match(Token.div.tag);
                fact();
                termp();
                break;
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
            case '$':
                break;
            default:
                error("Error in termp");
        }
    }

    private void fact() {
        switch(look.tag) {
            case '(':
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("Error in fact");
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test_parser.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
