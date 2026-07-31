package net.oktawia.insaneae2addons.logic.nbt;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;

import lombok.Getter;

import appeng.api.stacks.AEItemKey;

public final class NBTMatcher {

    private NBTMatcher() {
    }

    private static final class InvalidNbtMatcherSyntaxException extends Exception {
        private InvalidNbtMatcherSyntaxException(String s) {
            super(s);
        }
    }

    public static Compiled compile(@Nullable String expr) {
        if (expr == null || expr.isBlank())
            return Compiled.EMPTY;
        try {
            List<Token> tokens = tokenize(expr);
            Token[] rpn = toRpn(tokens);
            return new Compiled(rpn, true, null);
        } catch (InvalidNbtMatcherSyntaxException ex) {
            return new Compiled(new Token[0], false, ex.getMessage());
        }
    }

    public static boolean doesItemMatch(@Nullable AEItemKey item, String expr) {
        if (item == null)
            return false;
        return doesItemMatch(item, compile(expr));
    }

    public static boolean doesItemMatch(@Nullable AEItemKey item, @Nullable Compiled compiled) {
        if (item == null || compiled == null || !compiled.valid || compiled.rpn.length == 0)
            return false;
        CompoundTag tag = item.getTag();
        if (tag == null)
            tag = new CompoundTag();
        return doesTagMatch(tag, compiled);
    }

    public static boolean doesTagMatch(@Nullable CompoundTag tag, @Nullable Compiled compiled) {
        if (compiled == null || !compiled.valid || compiled.rpn.length == 0)
            return false;
        CompoundTag t = tag == null ? new CompoundTag() : tag;
        try {
            return evalRpn(compiled.rpn, t);
        } catch (InvalidNbtMatcherSyntaxException e) {
            return false;
        }
    }

    public static boolean doesTagMatch(@Nullable CompoundTag tag, String expr) {
        return doesTagMatch(tag, compile(expr));
    }

    public static final class Compiled {
        final Token[] rpn;
        @Getter
        final boolean valid;
        @Getter
        @Nullable
        final String error;

        private Compiled(Token[] rpn, boolean valid, @Nullable String error) {
            this.rpn = rpn;
            this.valid = valid;
            this.error = error;
        }

        static final Compiled EMPTY = new Compiled(new Token[0], true, null);
    }

    private enum TokenType {
        CRIT, OPERATOR, LPAREN, RPAREN
    }

    private enum Operator {
        NOT("!", 3, true),
        AND("&", 2, false),
        XOR("^", 1, false),
        OR("|", 0, false);

        final String symbol;
        final int precedence;
        final boolean rightAssociative;

        Operator(String symbol, int precedence, boolean rightAssociative) {
            this.symbol = symbol;
            this.precedence = precedence;
            this.rightAssociative = rightAssociative;
        }
    }

    private record Token(TokenType type, CompoundTag crit, Operator op) {
        static Token crit(CompoundTag crit) {
            return new Token(TokenType.CRIT, crit, null);
        }

        static Token op(Operator op) {
            return new Token(TokenType.OPERATOR, null, op);
        }

        static Token lparen() {
            return new Token(TokenType.LPAREN, null, null);
        }

        static Token rparen() {
            return new Token(TokenType.RPAREN, null, null);
        }
    }

    private static List<Token> tokenize(String expr) throws InvalidNbtMatcherSyntaxException {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;
        int lp = 0;
        boolean expectingOperand = true;

        while (pos < expr.length()) {
            char c = expr.charAt(pos);

            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }

            if (c == '(') {
                if (!expectingOperand)
                    throw new InvalidNbtMatcherSyntaxException("Unexpected '(' at position " + pos);
                tokens.add(Token.lparen());
                lp++;
                pos++;
            } else if (c == ')') {
                if (expectingOperand || lp <= 0)
                    throw new InvalidNbtMatcherSyntaxException("Unexpected ')' at position " + pos);
                tokens.add(Token.rparen());
                lp--;
                pos++;
            } else if (c == '!') {
                if (!expectingOperand)
                    throw new InvalidNbtMatcherSyntaxException("Unexpected '!' at position " + pos);
                tokens.add(Token.op(Operator.NOT));
                pos++;
            } else if (c == '&' || c == '|' || c == '^') {
                if (expectingOperand)
                    throw new InvalidNbtMatcherSyntaxException("Unexpected operator '" + c + "' at position " + pos);
                Operator op = c == '&' ? Operator.AND : c == '|' ? Operator.OR : Operator.XOR;
                pos++;
                if ((c == '&' || c == '|') && pos < expr.length() && expr.charAt(pos) == c) {
                    pos++;
                }
                tokens.add(Token.op(op));
                expectingOperand = true;
            } else if (c == '{') {
                if (!expectingOperand)
                    throw new InvalidNbtMatcherSyntaxException("Unexpected '{' at position " + pos);
                int end = readBalanced(expr, pos);
                String snippet = expr.substring(pos, end);
                tokens.add(Token.crit(parseSnippet(snippet)));
                pos = end;
                expectingOperand = false;
            } else {
                throw new InvalidNbtMatcherSyntaxException("Unexpected character '" + c + "' at position " + pos);
            }
        }

        if (tokens.isEmpty())
            throw new InvalidNbtMatcherSyntaxException("Expression cannot be empty.");
        if (lp > 0)
            throw new InvalidNbtMatcherSyntaxException("Missing ')' at the end of the expression.");
        if (expectingOperand)
            throw new InvalidNbtMatcherSyntaxException("Expression ended unexpectedly.");

        return tokens;
    }

    private static int readBalanced(String src, int start) throws InvalidNbtMatcherSyntaxException {
        int d = 0;
        int pos = start;
        do {
            if (pos >= src.length())
                throw new InvalidNbtMatcherSyntaxException("Unbalanced '{' at position " + start);
            char ch = src.charAt(pos++);
            if (ch == '{') {
                d++;
            } else if (ch == '}') {
                d--;
            } else if (ch == '"') {
                while (pos < src.length()) {
                    char q = src.charAt(pos++);
                    if (q == '\\')
                        pos++;
                    else if (q == '"')
                        break;
                }
            }
        } while (d > 0);
        return pos;
    }

    private static Token[] toRpn(List<Token> tokens) throws InvalidNbtMatcherSyntaxException {
        ArrayList<Token> out = new ArrayList<>(tokens.size());
        Deque<Token> stack = new ArrayDeque<>();

        for (Token t : tokens) {
            switch (t.type) {
                case CRIT -> out.add(t);
                case OPERATOR -> {
                    while (!stack.isEmpty() && stack.peek().type == TokenType.OPERATOR) {
                        Operator cur = t.op;
                        Operator top = stack.peek().op;
                        boolean shouldPop = (!cur.rightAssociative && cur.precedence <= top.precedence)
                                || (cur.rightAssociative && cur.precedence < top.precedence);
                        if (shouldPop)
                            out.add(stack.pop());
                        else
                            break;
                    }
                    stack.push(t);
                }
                case LPAREN -> stack.push(t);
                case RPAREN -> {
                    boolean found = false;
                    while (!stack.isEmpty()) {
                        Token top = stack.peek();
                        if (top.type == TokenType.LPAREN) {
                            stack.pop();
                            found = true;
                            break;
                        }
                        out.add(stack.pop());
                    }
                    if (!found)
                        throw new InvalidNbtMatcherSyntaxException("Mismatched parentheses.");
                }
            }
        }

        while (!stack.isEmpty()) {
            Token top = stack.pop();
            if (top.type == TokenType.LPAREN)
                throw new InvalidNbtMatcherSyntaxException("Mismatched parentheses.");
            out.add(top);
        }

        Token[] rpn = out.toArray(Token[]::new);
        validateRpnStackDepth(rpn);
        return rpn;
    }

    private static void validateRpnStackDepth(Token[] rpn) throws InvalidNbtMatcherSyntaxException {
        int sp = 0;
        for (Token t : rpn) {
            switch (t.type) {
                case CRIT -> sp++;
                case OPERATOR -> {
                    int required = t.op == Operator.NOT ? 1 : 2;
                    sp -= required;
                    if (sp < 0)
                        throw new InvalidNbtMatcherSyntaxException("Unexpected operator " + t.op);
                    sp++;
                }
                default -> throw new InvalidNbtMatcherSyntaxException("Unexpected token: " + t.type);
            }
        }
        if (sp != 1)
            throw new InvalidNbtMatcherSyntaxException("Depth at the end should equal 1");
    }

    private static boolean evalRpn(Token[] rpn, CompoundTag itemTag) throws InvalidNbtMatcherSyntaxException {
        if (rpn.length == 0)
            return false;

        boolean[] stack = new boolean[rpn.length];
        int sp = 0;

        for (Token t : rpn) {
            if (t.type == TokenType.CRIT) {
                stack[sp++] = matches(itemTag, t.crit);
            } else if (t.type == TokenType.OPERATOR) {
                Operator op = t.op;
                if (op == Operator.NOT) {
                    if (sp < 1)
                        throw new InvalidNbtMatcherSyntaxException("NOT needs 1 operand.");
                    stack[sp - 1] = !stack[sp - 1];
                } else {
                    if (sp < 2)
                        throw new InvalidNbtMatcherSyntaxException(op.symbol + " needs 2 operands.");
                    boolean right = stack[--sp];
                    boolean left = stack[--sp];
                    stack[sp++] = switch (op) {
                        case AND -> left && right;
                        case OR -> left || right;
                        case XOR -> left ^ right;
                        default -> throw new InvalidNbtMatcherSyntaxException("Unexpected op: " + op);
                    };
                }
            } else {
                throw new InvalidNbtMatcherSyntaxException("Paren token in RPN (should not happen).");
            }
        }

        if (sp == 1)
            return stack[0];
        throw new InvalidNbtMatcherSyntaxException("Invalid expression: stack size " + sp);
    }

    private static CompoundTag parseSnippet(String raw) throws InvalidNbtMatcherSyntaxException {
        String s = sanitise(raw);
        try {
            return TagParser.parseTag(s);
        } catch (CommandSyntaxException e) {
            throw new InvalidNbtMatcherSyntaxException("Bad SNBT: " + raw);
        }
    }

    private static String sanitise(String src) {
        StringBuilder out = new StringBuilder(src.length() + 8);
        boolean inQ = false;
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (ch == '"') {
                out.append(ch);
                inQ = !inQ;
                continue;
            }
            if (!inQ && ch == '*') {
                boolean key = lookAhead(src, i + 1, ':');
                boolean val = lookBehind(src, i - 1, ':');
                if (key || val) {
                    out.append("\"*\"");
                    continue;
                }
            }
            out.append(ch);
        }
        return out.toString();
    }

    private static boolean lookAhead(String s, int i, char t) {
        while (i < s.length() && Character.isWhitespace(s.charAt(i)))
            i++;
        return i < s.length() && s.charAt(i) == t;
    }

    private static boolean lookBehind(String s, int i, char t) {
        while (i >= 0 && Character.isWhitespace(s.charAt(i)))
            i--;
        return i >= 0 && s.charAt(i) == t;
    }

    private static boolean matches(CompoundTag item, CompoundTag crit) {
        for (String ck : crit.getAllKeys()) {
            Tag cv = crit.get(ck);

            if ("*".equals(ck)) {
                if (isAny(cv)) {
                    return !item.isEmpty();
                }
                boolean ok = false;
                for (String ik : item.getAllKeys()) {
                    if (valueMatches(item.get(ik), cv)) {
                        ok = true;
                        break;
                    }
                }
                if (!ok)
                    return false;
                continue;
            }

            if (!item.contains(ck))
                return false;
            if (!valueMatches(item.get(ck), cv))
                return false;
        }
        return true;
    }

    private static boolean valueMatches(Tag itemVal, Tag critVal) {
        if (isAny(critVal)) {
            if (itemVal instanceof ListTag itL) {
                return !itL.isEmpty();
            }
            return true;
        }

        if (itemVal instanceof CompoundTag itC && critVal instanceof CompoundTag crC) {
            return matches(itC, crC);
        }

        if (itemVal instanceof ListTag itL && critVal instanceof ListTag crL) {
            return listContains(itL, crL);
        }

        if (itemVal instanceof ListTag itL2 && critVal instanceof CompoundTag crC2) {
            return listAnyMatch(itL2, crC2);
        }

        if (critVal instanceof StringTag strTag) {
            String s = strTag.getAsString();
            if (s.startsWith("!")) {
                String negated = s.substring(1);
                return !itemVal.getAsString().equals(negated);
            }
        }

        return itemVal.equals(critVal);
    }

    private static boolean listContains(ListTag item, ListTag crit) {
        if (crit.isEmpty()) {
            return item.isEmpty();
        }
        outer: for (Tag c : crit) {
            for (Tag i : item) {
                if (valueMatches(i, c))
                    continue outer;
            }
            return false;
        }
        return true;
    }

    private static boolean listAnyMatch(ListTag item, CompoundTag crit) {
        for (Tag i : item)
            if (i instanceof CompoundTag ct && matches(ct, crit))
                return true;
        return false;
    }

    private static boolean isAny(Tag t) {
        return t instanceof StringTag st && "*".equals(st.getAsString());
    }
}
