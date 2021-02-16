import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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
            int lnext_prog = code.newLabel();
            statlist(lnext_prog);
            code.emitLabel(lnext_prog);
            match(Tag.EOF);
            try {
                code.toJasmin();
            }
            catch(java.io.IOException e) {
                System.out.println("IO error\n");
            };
        } else {
            error("Error in prog");
        }
    }

    private void statlist(int lnext) {
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.READ || look.tag == Tag.COND || look.tag == Tag.WHILE || look.tag == '{') {
            int lnext_prog = code.newLabel();
            stat(lnext_prog);
            code.emitLabel(lnext_prog);
            statlistp(lnext);
        } else {
            error("Error in statlist");
        }
    }

    private void statlistp(int lnext) {
        switch (look.tag) {
            case';':
                match(Token.semicolon.tag);
                int lnext_prog = code.newLabel();
                stat(lnext_prog);
                code.emitLabel(lnext_prog);
                statlistp(lnext);
                break;
            case '}':
            case Tag.EOF:
                break;
            default:
                error("Error in statlistp");
        }
    }

    private void stat(int lnext) {
        switch(look.tag) {
            case '=':
                match(Token.assign.tag);
                if (look.tag == Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr == -1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }
                    match(Tag.ID);
                    expr();
                    code.emit(OpCode.istore,read_id_addr);
                } else {
                    error("Error in grammar (statp) after = with " + look);
                }
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match(Token.lpt.tag); 
                exprlist();
                match(Token.rpt.tag);
                code.emit(OpCode.invokestatic,1);
                break;

            case Tag.READ:
                match(Tag.READ);
                match(Token.lpt.tag);
                if (look.tag == Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr == -1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    } 
                    match(Tag.ID);
                    match(Token.rpt.tag);
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,read_id_addr);   
                }
                else
                    error("Error in grammar (stat) after read with " + look);
                break;

            case Tag.COND:     
                match(Tag.COND);
                int lTrue = code.newLabel();
                int lFalse = code.newLabel();
                //code.emitLabel(lTrue);
                whenlist(lTrue, lFalse);
                code.emit(OpCode.GOto, lnext);
                code.emitLabel(lFalse);
                match(Tag.ELSE);
                stat(lnext);
                break; 
            
            case Tag.WHILE:
                match(Tag.WHILE);
                int lCond = code.newLabel();
                int lBucle = code.newLabel();
                code.emitLabel(lCond);
                match(Token.lpt.tag);
                bexpr(lBucle, lnext);
                match(Token.rpt.tag);
                code.emitLabel(lBucle);
                stat(lCond);
                code.emit(OpCode.GOto, lCond);
                break;
            case '{':
                match(Token.lpg.tag);
                statlist(lnext);
                match(Token.rpg.tag);
                break;
            }


    }

   
    private void whenlist(int lTrue, int lFalse) {
        switch (look.tag) {
            case Tag.WHEN:
                whenitem(lTrue, lFalse);
                whenlistp(lTrue, lFalse);
                break;
           default:
                error("Error in whenlist");
        }
    }

    private void whenlistp(int lTrue, int lFalse) {
        switch (look.tag) {
            case Tag.WHEN:
                whenitem(lTrue, lFalse);
                whenlistp(lTrue, lFalse);
                break;
            case Tag.ELSE:
                break;
           default:
                error("Error in whenlistp");
        }
    }

    private void whenitem(int lTrue, int lFalse) {
        switch (look.tag) {
            case Tag.WHEN:
                match(Tag.WHEN);
                match(Token.lpt.tag);
                bexpr(lTrue, lFalse);
                code.emitLabel(lTrue);
                match(Token.rpt.tag);
                match(Tag.DO);
                stat(lTrue);
                break;
           default:
                error("Error in whenitem");
        }
    }


    private void bexpr(int lTrue, int lFalse) {
        if (look.tag == Tag.RELOP) {
            String val = ((Word)look).lexeme;
            match(Tag.RELOP);
            expr();
            expr();
            OpCode op;
            switch (val) {
                case "==":
                    op = OpCode.if_icmpeq;
                    break;
                case "<=":
                    op = OpCode.if_icmple;
                    break;
                case "<":
                    op = OpCode.if_icmplt;
                    break;
                case ">=":
                    op = OpCode.if_icmpge;
                    break;
                case ">":
                    op = OpCode.if_icmpgt;
                    break;
                default: // <>
                    op = OpCode.ifne;
                    break;
            }
            code.emit(op, lTrue);
            code.emit(OpCode.GOto, lFalse);
        } else {
            error ("Error in bexprp");
        }

    }

    private void expr() {

        switch(look.tag){
            case Tag.NUM: 
            int val = ((NumberTok)look).number;
            match(Tag.NUM);
            code.emit(OpCode.ldc, val);
            break;
        case Tag.ID:
            int read_id_addr = st.lookupAddress(((Word)look).lexeme);
            if (read_id_addr == -1) {
                error("Error the ID " + ((Word)look).lexeme + " does not exist");
            }                    
            match(Tag.ID);
            code.emit(OpCode.iload, read_id_addr);
            break;
        case '+':
            match(Token.plus.tag);
            match(Token.lpt.tag);
            exprlist();
            match(Token.rpt.tag);
            code.emit(OpCode.iadd);
            break;
        case '-':
            match(Token.minus.tag);
            expr();
            expr();
            code.emit(OpCode.isub);
            break;                
        case '*':
            match(Token.mult.tag);
            match(Token.lpt.tag);
            exprlist();
            match(Token.rpt.tag);
            code.emit(OpCode.imul);
            break;
        case '/':
            match(Token.div.tag);
            expr();
            expr();
            code.emit(OpCode.idiv);
            break;
        default:
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
        } else if (look.tag == ')') {
            return;
        } else {
            error("Error in exprlistp");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = args[0]; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}