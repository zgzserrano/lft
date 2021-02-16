// Esercizio 3.2
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser2 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser2(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	    throw new Error("near line " + Lexer.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    public void prog() {
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.READ || look.tag == Tag.COND || look.tag == Tag.WHILE || look.tag == '{') {
            statlist();
            match(Tag.EOF);
        } else {
            error("Error in prog");
        }
    }

    private void statlist() {
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.READ || look.tag == Tag.COND || look.tag == Tag.WHILE || look.tag == '{') {
            stat();
            statlistp();
        } else {
            error("Error in statlist");
        }
    }

    private void statlistp() {
        switch (look.tag) {
            case';':
                match(Token.semicolon.tag);
                stat();
                statlistp();
                break;
            case '}':
            case Tag.EOF:
                break;
            default:
                error("Error in statlistp");
        }
    }

    private void stat() {
        switch (look.tag){
            case '=':
                match(Token.assign.tag);    
                match(Tag.ID);
                expr();
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case Tag.READ:
                match(Tag.READ);
                match(Token.lpt.tag);
                match(Tag.ID);
                match(Token.rpt.tag);
                break;
            case Tag.COND:
                match(Tag.COND);
                whenlist();
                match(Tag.ELSE);
                stat();
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                stat();
                break;
            case '{':
                match(Token.lpg.tag);    
                statlist();
                match(Token.rpg.tag);
                break;
            default:
            error("Error in stat");

        }
        
    }

    private void whenlist(){ 
        if(look.tag == Tag.WHEN){
            whenitem();
            whenlistp();
        
        }else{
            error("Error in statlist");
        }
    }

    private void whenitem(){ 
        if(look.tag == Tag.WHEN){
            match(Tag.WHEN);
            match(Token.lpt.tag);
            bexpr();
            match(Token.rpt.tag);
            match(Tag.DO);
            stat();
        }else{
            error("Error in whenitem");
        }
    }

    private void whenlistp(){ //hacer
        if(look.tag == Tag.WHEN){
            whenitem();
            whenlistp();
        }else if(look.tag == Tag.ELSE){
        }else {
            error("Error in whenitem");
        }
    }
    


    private void bexpr() {
        if (look.tag == Tag.RELOP) {
            match(Tag.RELOP);
            expr();
            expr();
        } else {
            error("Error in bexpr");
        }
    }


    private void expr() {
        if (look.tag == Tag.NUM) {
            match(Tag.NUM);
        } else if (look.tag == Tag.ID) {
            match(Tag.ID);
        } else if (look.tag == '+') {
            match(Token.plus.tag);
            match(Token.lpt.tag);
            exprlist();
            match(Token.rpt.tag);
        } else if (look.tag == '*') {
            match(Token.mult.tag);
            match(Token.lpt.tag);
            exprlist();
            match(Token.rpt.tag);
        }else if (look.tag == '-') {
            match(Token.minus.tag);
            expr();
            expr();
        }else if (look.tag == '/') {
            match(Token.div.tag);
            expr();
            expr();
        }else {
            error("Error in expr");
        }
    }


    private void exprlist() {
        if (look.tag == Tag.NUM || look.tag == Tag.ID || look.tag == '+'|| look.tag == '-'|| look.tag == '*'|| look.tag == '/') {
            expr();
            exprlistp();
        } else {
            error("Error in exprlist");
        }
    }

    private void exprlistp() {
        if (look.tag == Tag.NUM || look.tag == Tag.ID || look.tag == '+'|| look.tag == '-'|| look.tag == '*'|| look.tag == '/') {
            expr();
            exprlistp();
        }  else if(look.tag == ')'){
        } else {
            error("Error in exprlistp");
        }
    }
    	
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = args[0]; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser2 parser = new Parser2(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}