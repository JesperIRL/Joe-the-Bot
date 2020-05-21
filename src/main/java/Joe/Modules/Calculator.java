package Joe.Modules;

import Joe.*;

import java.io.*;
import java.util.*;

/* Todo:
 * Move description to a !help text
 */

public class Calculator extends AbstractBotModule {
    private abstract class CalcException extends RuntimeException { }

    private class SyntaxErrorException extends CalcException { }

    private class UnknownIdentifierException extends CalcException {
        private String id;

        public UnknownIdentifierException(String id) {
            this.id = id;
        }

        public String getID() {
            return id;
        }
    }

    private HashMap<String, Double> variables = new HashMap<String, Double>();
    private ArrayList<Double> memory = new ArrayList<Double>();

    public Calculator() {
        setupBuiltIn();
    }

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!calc");
        return comm;
    }

    public String description() {
        return "Evaluates mathematical expressions. Answers can be stored for later use in variables " +
                "or can be accessed through `$n`, where `$0` is the most recent answer, `$1` the one before etc.\n" +
                "The following symbols and built-in functions are supported:\n" +
                "`+ - / * ^ % ( ) = ~ & | < > && || << >> PI E sin cos tan asin acos atan sinh cosh tanh " +
                "log ln lg abs rnd sqrt cbrt`";
    }

    public String handleCommand(Message message) {
        if (message.command().equals("!calc")) {
            if (message.params() == null) {
                return "Usage: `!calc` <_expression_|`reset`>";
            } else if (message.params().equals("reset")) {
                variables.clear();
                memory.clear();
                setupBuiltIn();
            } else {
                return calc(message.params());
            }
        }
        return null;
    }

    private void setupBuiltIn() {
        variables.put("pi", Double.valueOf(Math.PI));
        variables.put("e", Double.valueOf(Math.E));
    }

    private String calc(String something) {
        try {
            StringReader inputStream = new StringReader(filter(something));
            double a = readExpression(inputStream);

            if (inputStream.read() != -1) {
                return "Syntax error";
            }

            memory.add(0, Double.valueOf(a));
            if (memory.size() > 20) {
                memory.remove(20);
            }

            return "" + round(a);
        }
        catch (SyntaxErrorException se) {
            return "Syntax Error";
        }
        catch (UnknownIdentifierException ide) {
            return "Unknown identifier: " + ide.getID();
        }
        catch (IOException ioe) {
            return ioe.getMessage();
        }
    }

    private String filter(String something) {
        String result = "";

        for (int i = 0; i < something.length(); i++) {
            switch (something.charAt(i)) {
                case ' ':
                case '\t': break;
                default: result += Character.toLowerCase(something.charAt(i));
            }
        }
        return result;
    }

    private double readExpression(StringReader sr) throws IOException, CalcException {
        double a = readAdd(sr);
        int next;

        next = sr.read();
        while (next == '<' || next == '>' || next == '&' || next == '|') {
            sr.mark(1);
            if (next == '<') {
                next = sr.read();
                if (next == '<') {
                    sr.mark(1);
                    a = (double)((long)a << (long)readAdd(sr));
                } else {
                    sr.reset();
                    a = (a < readAdd(sr)) ? 1 : 0;
                }
            } else if (next == '>') {
                next = sr.read();
                if (next == '>') {
                    sr.mark(1);
                    a = (double)((long)a >> (long)readAdd(sr));
                } else {
                    sr.reset();
                    a = (a > readAdd(sr)) ? 1 : 0;
                }
            } else if (next == '&') {
                next = sr.read();
                if (next == '&') {
                    sr.mark(1);
                    a = ((readAdd(sr) != 0) && (a != 0)) ? 1 : 0;
                } else {
                    sr.reset();
                    a = (double)((long)a & (long)readAdd(sr));
                }
            } else if (next == '|') {
                next = sr.read();
                if (next == '|') {
                    sr.mark(1);
                    a = ((readAdd(sr) != 0) || (a != 0)) ? 1 : 0;
                } else {
                    sr.reset();
                    a = (double)((long)a | (long)readAdd(sr));
                }
            }
            next = sr.read();
        }
        sr.reset();
        return a;
    }

    private double readAdd(StringReader sr) throws IOException, CalcException {
        double a = readTerm(sr);
        int next;

        next = sr.read();
        while (next == '+' || next == '-') {
            sr.mark(1);
            if (next == '+')
                a += readTerm(sr);
            else
                a -= readTerm(sr);

            next = sr.read();
        }
        sr.reset();
        return a;
    }

    private double readTerm(StringReader sr) throws IOException, CalcException {
        double a = readPower(sr);
        int next;

        next = sr.read();
        while (next == '*' || next == '/') {
            sr.mark(1);
            if (next == '*') {
                a *= readPower(sr);
            } else {
                a /= readPower(sr);
            }

            next = sr.read();
        }
        sr.reset();
        return a;
    }

    private double readPower(StringReader sr) throws IOException, CalcException {
        double a = readFactor(sr);
        int next;

        next = sr.read();
        while (next == '^' || next == '%') {
            sr.mark(1);
            if (next == '^') {
                a = Math.pow(a,readFactor(sr));
            } else {
                a = a % readFactor(sr);
            }

            next = sr.read();
        }
        sr.reset();
        return a;
    }

    private double readFactor(StringReader sr) throws IOException, CalcException {
        int next = sr.read();

        /* ( expr ) */
        if (next == '(') {
            sr.mark(1);
            double a = readExpression(sr);
            next = sr.read();
            if (next != ')') {
                throw new SyntaxErrorException();
            }
            sr.mark(1);
            return a;
        }

        /* - factor */
        if (next == '-') {
            sr.mark(1);
            return -readFactor(sr);
        }

        /* ~ factor */
        if (next == '~') {
            sr.mark(1);
            return ~(long)readFactor(sr);
        }

        /* ! factor */
        if (next == '!') {
            sr.mark(1);
            return (((long)readFactor(sr)) == 0) ? 1 : 0;
        }

        /* $n */
        if (next == '$') {
            sr.mark(1);
            int a = (int)readNumber(sr);
            if (a >= memory.size())
                throw new UnknownIdentifierException("$" + a);
            return memory.get(a);
        }

        /* number */
        if (Character.isDigit(next)) {
            sr.reset();
            return readNumber(sr);
        }

        /* identifier */
        if (Character.isLetter(next)) {
            String ident;
            sr.reset();
            ident = readIdentifier(sr);

            if (ident.equals("sin")) return Math.sin(readFactor(sr));
            if (ident.equals("cos")) return Math.cos(readFactor(sr));
            if (ident.equals("tan")) return Math.tan(readFactor(sr));
            if (ident.equals("sinh")) return Math.sinh(readFactor(sr));
            if (ident.equals("cosh")) return Math.cosh(readFactor(sr));
            if (ident.equals("tanh")) return Math.tanh(readFactor(sr));
            if (ident.equals("asin")) return Math.asin(readFactor(sr));
            if (ident.equals("acos")) return Math.acos(readFactor(sr));
            if (ident.equals("atan")) return Math.atan(readFactor(sr));

            if (ident.equals("log")) return Math.log10(readFactor(sr));
            if (ident.equals("lg")) return Math.log(readFactor(sr)) / Math.log(2);
            if (ident.equals("ln")) return Math.log(readFactor(sr));

            if (ident.equals("abs")) return Math.abs(readFactor(sr));
            if (ident.equals("sqrt")) return Math.sqrt(readFactor(sr));
            if (ident.equals("cbrt")) return Math.cbrt(readFactor(sr));
            if (ident.equals("rnd")) return Math.random() * readFactor(sr);

            next = sr.read();
            if (next == '=') {
                sr.mark(1);
                variables.put(ident, Double.valueOf(readExpression(sr)));
            } else {
                sr.reset();
            }

            if (variables.get(ident) != null) {
                return variables.get(ident).doubleValue();
            }

            throw new UnknownIdentifierException(ident);
        }

        throw new SyntaxErrorException();
    }


    private double readNumber(StringReader sr) throws IOException {
        double a = 0;
        int next = sr.read();

        while (Character.isDigit(next)) {
            sr.mark(1);
            a = a * 10 + (next - '0');
            next = sr.read();
        }

        if (next == '.' || next == ',') {
            int frac = 10;
            sr.mark(1);
            next = sr.read();
            while(Character.isDigit(next)) {
                sr.mark(1);
                a += (next - '0') / (float)frac;
                frac *= 10;
                next = sr.read();
            }
        }

        sr.reset();
        return a;
    }

    private String readIdentifier(StringReader sr) throws IOException {
        String result = "";
        int next = sr.read();

        while (Character.isLetter(next) || Character.isDigit(next)) {
            sr.mark(1);
            result += (char)next;
            next = sr.read();
        }

        sr.reset();
        return result;
    }

    private double round(double n) {
        return ((long)(n * 1000000 + 0.5)) / 1000000.0;
    }
}
