import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.*;

import static sun.misc.Version.print;

//the next part is While statemnt   0
//the next part is ELSE statement   1


public class Interpreter {

    String[] code;
    String line;
    SymbolTable stable = new SymbolTable();
    String[] star;

    Stack<IfInfo> ifStack = new Stack<>();
    Stack<WhileInfo> whileStack = new Stack<>();
    Stack<ForInfo> forStack = new Stack<>();
    //labels map <labelName, code line>
    HashMap<String, Integer> labels = new HashMap<>();

    int nline = 0;

    public Interpreter(String[] code) {
        this.code = code;
    }

    /*

        var a = 5.2;

        print "a = ",a

        print "Hello"


     */


    public void run(){

        try {


            for (; nline < code.length; nline++) {

                line = code[nline].trim();

                if (line.equals("") || line.startsWith("//"))
                    continue;

                else if (line.startsWith("print "))
                    print();

                else if (line.startsWith("var "))
                    defineVar();

                else if (line.startsWith("read "))
                    readVar();

                else if (line.contains("=") && stable.has(line.split("=")[0].trim()))
                    assign();

                else if (line.startsWith("label "))
                    label();

                else if (line.startsWith("goto "))
                    goTo();

                else if (line.startsWith("if "))
                    If();

                else if (line.equals("else"))
                    Else();
                else if (line.equals("endif")) {

                    if (ifStack.empty() == false)
                        ifStack.pop();
                    else System.err.println("Error: endif without if");

                } else if (line.startsWith("while "))
                    While();

                else if (line.equals("endwhile")) {
                    if (new Expression(replaceByVal(whileStack.peek().exp)).eval().compareTo(BigDecimal.valueOf(1)) == 0)
                        nline = whileStack.peek().whileline;
                    else whileStack.pop();
                } else if (line.startsWith("for "))
                    For();

                else if (line.equals("endfor")) {

                    ForInfo forInfo = forStack.peek();

                    if ((Double.parseDouble(stable.getVal(forInfo.var)) < forInfo.finalVal && forInfo.step > 0) ||
                            (Double.parseDouble(stable.getVal(forInfo.var)) > forInfo.finalVal && forInfo.step < 0)) {
                        nline = forStack.peek().forline;

                        stable.setVal(forInfo.var, String.valueOf(Double.parseDouble(stable.getVal(forInfo.var)) + forInfo.step));
                    } else forStack.pop();
                    //if(new Expression(replaceByVal(whileStack.peek().exp)).eval().compareTo(BigDecimal.valueOf(1))==0)
                    //  nline = whileStack.peek().whileline;
                    //else whileStack.pop();
                }
            }
        }catch (Exception e){
            System.err.println("Error at line: "+nline);
            System.err.println(e);
        }
    }

    private void For() {
        line = line.substring(3).trim();
        if (line.indexOf(" step ")<5)
            line+= " step 1";

        star = line.split("=");
        if(stable.has(star[0].trim())) {
            stable.setVal(star[0].trim(), star[1].split("to")[0].trim());
        }else stable.add(star[0].trim(), star[1].split("to")[0].trim());


        String var = star[0].trim();
        double firstVal = Double.parseDouble( new Expression(replaceByVal(star[1].split("to")[0].trim())).eval().toPlainString());
        double to =  Double.parseDouble(new Expression(replaceByVal(star[1].split("to")[1].trim().split(" step ")[0].trim())).eval().toPlainString());

        double step = Double.parseDouble(star[1].split("to")[1].trim().split(" step ")[1].trim());

      //  System.out.println(star[0].trim()+"  "+ star[1].split("to")[0].trim()+"   "+to+"  "+step);
        //forStack.push(star)

        int thisScope = 1;
        if(!forStack.empty())
            thisScope = forStack.peek().scope + 1;


        forStack.push(new ForInfo(var ,to, step, thisScope, nline));


        boolean forExp = false;
        if((firstVal <= to && step > 0) || (firstVal >= to && step < 0))
            forExp=true;

        //forExp = new Expression(replaceByVal(line.trim())).eval().compareTo(BigDecimal.valueOf(1))==0;

        if(! forExp){


            while(true) {
                while (!line.equals("endfor")) {
                    line = code[nline++].trim();
                    if(line.startsWith("for "))
                        forStack.push(new ForInfo("", 0,0,forStack.peek().scope+1, 0));
                }
                if (forStack.peek().scope > thisScope ){
                    forStack.pop();
                    //else line="";
                }else{nline--;  break;} //nline--

            }
        }


    }

    private void While() {
        line = line.substring(6);

        int thisScope = 1;
        if(!whileStack.empty())
            thisScope = whileStack.peek().scope+1;

      //  System.out.println(line+"  "+new Expression(replaceByVal(line.trim())).eval().compareTo(BigDecimal.valueOf(1)));
        whileStack.push(new WhileInfo(line.trim(), thisScope, nline));

        boolean whileExp = new Expression(replaceByVal(line.trim())).eval().compareTo(BigDecimal.valueOf(1))==0;

        if(! whileExp){


            while(true) {
                while (!line.equals("endwhile")) {
                    line = code[nline++].trim();
                    if(line.startsWith("while "))
                        whileStack.push(new WhileInfo("", whileStack.peek().scope+1, 0));
                }
                if (whileStack.peek().scope > thisScope ){
                        whileStack.pop();
                    //else line="";
                }else{nline--;  break;} //nline--

            }
        }



    }

    private void Else() {

        if(ifStack.peek().result == true){


            int thisScope = ifStack.peek().scope;

            while(true) {
                while (!line.equals("endif")) {
                    line = code[++nline].trim();
                    if(line.startsWith("if "))
                        ifStack.push(new IfInfo(false, ifStack.peek().scope+1));
                }

                if (ifStack.peek().scope > thisScope ){

                        ifStack.pop();

                }else{nline-=2;  break;}


                /*
                if (ifStack.peek().scope > thisScope){

                    ifStack.pop();
                }else{nline--; break;}
*/
            }

            /*
            while(!line.equals("endif"))
                line = code[nline++];
                */
        }
    }

    private void If() {
        line = line.substring(2);

        int thisScope = 1;
        if(!ifStack.empty())
           thisScope = ifStack.peek().scope+1;

        boolean ifEpx = new Expression(replaceByVal(line.trim())).eval().compareTo(BigDecimal.valueOf(1))==0;

        ifStack.push(new IfInfo(ifEpx, thisScope) );

        if(! ifEpx){


            while(true) {

                while (!line.equals("else") && !line.equals("endif")) {
                //    System.out.println("Line "+line);
                    line = code[++nline].trim();
                    if(line.startsWith("if "))
                        ifStack.push(new IfInfo(false, ifStack.peek().scope+1));
                }
                if (ifStack.peek().scope > thisScope ){
                    if(  !line.equals("else"))
                        ifStack.pop();
                    else line="";
                }else{nline--;
                   // System.out.println("%%%% "+code[nline]);
                   break;}

            }
        }
    }

    private void goTo() {
       // Helper.printMap(labels);
        nline = labels.get(line.substring(4));
    }

    private void label() {

        labels.put(line.substring(5,line.length()-1), nline);

    }

    private void assign() {
        star = line.split("=");
        if(stable.has(star[0].trim())){
            stable.setVal(star[0].trim(), new Expression(replaceByVal(star[1])).eval().toPlainString());
        }

    }

    private void readVar() {
        line = line.substring(4).trim();
        if(stable.has(line)){
          stable.setVal(line, new Scanner(System.in).next());
        }

    }

    private void print() {

        line = line.substring(5).trim();

        star = line.split("<<");

        for (String str : star) {
            str =str.trim();

            if(str.startsWith("\"") && str.endsWith("\""))
                str = str.substring(1, str.length()-1);

            else if(stable.has(str))
                str = stable.getVal(str);
            else if(str.equals("nline")) {
                System.out.println();
                str = "";
            }
            // number * 2
            else {

                str = String.valueOf(new Expression(replaceByVal(str)).eval().toPlainString());
            }
            System.out.print(str);
        }

    }

    private void defineVar() {

        line = line.substring(3).trim();

        star = line.split("=");
        stable.add(star[0].trim(), star[1].trim());

    }




    private String replaceByVal(String exp){

        ArrayList<String> varNames = stable.sortByLength();

        for (int i = 0; i < varNames.size(); i++) {

            exp = exp.replaceAll(varNames.get(i), stable.getVal(varNames.get(i)));
        }

        return exp;

    }


    public static void main(String[] args) {

        String[] code = new String[]{
                "var number = 4",
                "var n = 9", "var num = 6",
                "label here:",
                "print \"Number = \"," +
                " number, \"   \", nline",
                "number = number+1",
                "goto here"
        };

        code = new String[]{
                "if 5>12",
                "print nline, 5",
                "else",
                "print \"Else\"",
                "if 14>5",
                "print \"  \"<<14",

                "endif"
        };

        Interpreter inter = new Interpreter(code);
        inter.run();

        System.out.println();
        inter.stable.printTable();


    }
}


class IfInfo{

    boolean result;
    int scope;

    public IfInfo(boolean result, int scope) {
        this.result = result;
        this.scope = scope;
    }
}


class WhileInfo{

    String exp ="";
    int scope;
    int whileline;

    public WhileInfo(String exp, int scope, int whileline) {
        this.exp = exp;
        this.scope = scope;
        this.whileline = whileline;
    }
}


class ForInfo{

    double finalVal;
    int scope;
    int forline;
    double step = 0;
    String var;

    public ForInfo(String var, double finalVal, double step, int scope, int forline) {
        this.var = var;
        this.finalVal = finalVal;
        this.step = step;
        this.scope = scope;
        this.forline = forline;
    }
}