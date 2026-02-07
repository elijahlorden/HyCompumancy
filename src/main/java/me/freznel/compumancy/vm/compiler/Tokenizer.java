package me.freznel.compumancy.vm.compiler;

import me.freznel.compumancy.vm.exceptions.ParseException;
import me.freznel.compumancy.vm.objects.Vector3Object;

import java.util.ArrayDeque;

public class Tokenizer {

    public record Token(TokenType type, Object value, int line) { }

    private static final char ESCAPE = '\\';

    private static final char LIST_START = '{';
    private static final char LIST_END = '}';
    private static final char VEC_START = '<';
    private static final char VEC_END = '>';

    private static final char CHAR_STR = '"';

    private final String parseString;
    private int index;
    private int line;

    private final ArrayDeque<Token> queue;

    public Tokenizer(String s) {
        parseString = s;
        index = 0;
        line = 1;
        queue = new ArrayDeque<>();
    }

    public boolean hasNext() {
        if (!queue.isEmpty()) return true;
        var tkn = readNextToken();
        if (tkn == null) return false;
        queue.add(tkn);
        return true;
    }

    public Token next() {
        if (!queue.isEmpty()) return queue.remove();
        return readNextToken();
    }

    public Token peek() {
        if (!queue.isEmpty()) return queue.peek();
        var tkn = readNextToken();
        if (tkn == null) return null;
        queue.add(tkn);
        return tkn;
    }

    public Token peek(int depth) {
        while (depth >= queue.size()) {
            var tkn = readNextToken();
            if (tkn == null) break;
            queue.add(tkn);
        }
        if (depth >= queue.size()) return null;
        var iterator = queue.iterator();
        while (depth-- > 0 && iterator.hasNext()) iterator.next();
        return iterator.hasNext() ? iterator.next() : null;
    }

    private void readWhitespace() {
        char c;
        while (index < parseString.length() && Character.isWhitespace(c = parseString.charAt(index))) {
            if (c == '\n') line++;
            index++;
        }
    }

    private double readNumber(char startChar) {
        StringBuilder sb = new StringBuilder();
        char c;
        if (startChar != Character.MIN_VALUE) sb.append(startChar);

        while (index < parseString.length()) {
            c = parseString.charAt(index);
            if (c == '\n') line++;
            if (Character.isWhitespace(c) || c == ',' || c == LIST_END || c == VEC_END) break;
            sb.append(c);
            index++;
        }

        String s = sb.toString();

        //TODO: This feels very lazy.  Find a better way to do this.
        try { return Double.parseDouble(s); } catch (NumberFormatException _) { }
        if (s.startsWith("0x")) try { return Integer.parseInt(s.substring(2)); } catch (NumberFormatException _) { }
        if (s.startsWith("-0x")) try { return -Integer.parseInt(s.substring(3)); } catch (NumberFormatException _) { }
        throw new ParseException(String.format("Line %d: Invalid numeric literal %s", line, s));
    }

    private String readString() {
        StringBuilder sb = new StringBuilder();
        char c;
        boolean ended = false;
        int startLine = line;

        while (index < parseString.length()) {
            c = parseString.charAt(index++);
            if (c == '\n') line++;
            if (c == CHAR_STR) { ended = true; break; }
            if (c == ESCAPE && index < parseString.length()) {
                char esc = parseString.charAt(index++);
                switch (esc) {
                    case CHAR_STR -> sb.append(CHAR_STR);
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '\\' -> sb.append('\\');
                    default -> throw new ParseException(String.format("Line %d: invalid string escape sequence", startLine));
                }
                continue;
            }
            sb.append(c);
        }

        if (!ended) throw new ParseException(String.format("Line %d: Unterminated string literal", startLine));
        return sb.toString();
    }

    private String readWord(char startChar) {
        StringBuilder sb = new StringBuilder();
        char c;
        if (startChar != Character.MIN_VALUE) sb.append(startChar);

        while (index < parseString.length()) {
            c = parseString.charAt(index++);
            if (c == '\n') line++;
            if (Character.isWhitespace(c)) break;
            sb.append(c);
        }

        return sb.toString();
    }

    private Vector3Object readVector() {
        char c;
        double x = 0, y = 0, z = 0;
        int startLine = line;
        int vecIndex = 0;
        boolean ended = false;

        while (index < parseString.length()) {
            readWhitespace();
            c = parseString.charAt(index++);
            if (c == VEC_END) { ended = true; break; }
            if (c == ',') continue; //Commas are optional.  <a b c> and <a, b, c> and <a,b,c> are all valid
            if (Character.isDigit(c) || (c == '-' && index < parseString.length() && Character.isDigit(parseString.charAt(index)))) {
                double n = readNumber(c);
                switch (vecIndex++) {
                    case 0 -> x = n;
                    case 1 -> y = n;
                    case 2 -> z = n;
                    default -> throw new ParseException(String.format("Line %d: Attempted to create a vector with %d components", startLine, vecIndex));
                }
            }
        }
        if (!ended) throw new ParseException(String.format("Line %d: Unterminated vector literal", startLine));
        if (vecIndex != 3) throw new ParseException(String.format("Line %d: Attempted to create a vector with %d components", startLine, vecIndex));
        return new Vector3Object(x, y, z);
    }

    private Token readNextToken() {
        readWhitespace();
        if (index >= parseString.length()) return null;
        int startLine = line;
        char nextChar = parseString.charAt(index++);
        if (nextChar == CHAR_STR) {
            return new Token(TokenType.String, readString(), startLine);
        } else if (nextChar == LIST_START) {
            return new Token(TokenType.OpenBrace, String.valueOf(LIST_START), startLine);
        } else if (nextChar == LIST_END) {
            return new Token(TokenType.CloseBrace, String.valueOf(LIST_END), startLine);
        } else if (Character.isDigit(nextChar) || (nextChar == '-' && index < parseString.length() && Character.isDigit(parseString.charAt(index)))) {
            return new Token(TokenType.Number, readNumber(nextChar), startLine);
        } else if (nextChar == VEC_START) {
            return new Token(TokenType.Vector3, readVector(), startLine);
        } else {
            return new Token(TokenType.Word, readWord(nextChar), startLine);
        }
    }

}
