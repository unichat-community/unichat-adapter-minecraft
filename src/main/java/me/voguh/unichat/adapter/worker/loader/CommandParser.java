/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.worker.loader;

import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.worker.WorkerCommand;
import me.voguh.unichat.adapter.worker.function.WorkerFunction;
import me.voguh.unichat.adapter.worker.function.WorkerFunctions;

import java.util.ArrayList;
import java.util.List;

final class CommandParser {

    private final String throwPrefix;
    private final List<Property> properties;
    private final String command;

    private int cursor;

    private CommandParser(String throwPrefix, List<Property> properties, String command) {
        this.throwPrefix = throwPrefix;
        this.properties = properties;
        this.command = command;
    }

    public static WorkerCommand parse(String throwPrefix, List<Property> properties, String command) {
        return new CommandParser(throwPrefix, properties, command).template();
    }

    /* ====================================================================== */

    private WorkerCommand template() {
        List<WorkerCommand.Segment> segments = new ArrayList<>();
        StringBuilder literal = new StringBuilder();

        while (cursor < command.length()) {
            if (command.charAt(cursor) != '$' || peek(cursor + 1) != '{') {
                literal.append(command.charAt(cursor));
                cursor++;
                continue;
            }

            if (!literal.isEmpty()) {
                segments.add(new WorkerCommand.Segment.Literal(literal.toString()));
                literal.setLength(0);
            }

            segments.add(placeholder());
        }

        if (!literal.isEmpty()) {
            segments.add(new WorkerCommand.Segment.Literal(literal.toString()));
        }

        return new WorkerCommand(segments);
    }

    private WorkerCommand.Segment placeholder() {
        int start = cursor;
        cursor += 2;

        WorkerCommand.Node node = expression();
        skipSpaces();
        expect('}');

        return new WorkerCommand.Segment.Template(command.substring(start, cursor), node);
    }

    private WorkerCommand.Node expression() {
        skipSpaces();
        String name = identifier();
        skipSpaces();

        if (!consume('(')) {
            return property(name);
        }

        List<WorkerCommand.Node> args = arguments();
        expect(')');

        return call(name, args);
    }

    private List<WorkerCommand.Node> arguments() {
        List<WorkerCommand.Node> args = new ArrayList<>();

        do {
            args.add(argument());
            skipSpaces();
        } while (consume(','));

        return args;
    }

    private WorkerCommand.Node argument() {
        skipSpaces();
        char character = peek(cursor);
        if (character == '"') {
            return new WorkerCommand.Node.Constant(stringLiteral());
        }

        if (character == '-' || Character.isDigit(character)) {
            return new WorkerCommand.Node.Constant(numberLiteral());
        }

        if (isKeyword("true") || isKeyword("false")) {
            return new WorkerCommand.Node.Constant(booleanLiteral());
        }

        return expression();
    }

    /* ====================================================================== */

    private WorkerCommand.Node property(String name) {
        for (Property property : properties) {
            if (property.name().equals(name)) {
                return new WorkerCommand.Node.PropertyRef(property);
            }
        }

        throw new IllegalArgumentException(throwPrefix + " uses an unknown property '" + name + "'");
    }

    private WorkerCommand.Node call(String name, List<WorkerCommand.Node> args) {
        WorkerFunction function = WorkerFunctions.byId(name);
        if (function == null) {
            throw new IllegalArgumentException(throwPrefix + " uses an unknown function '" + name + "'");
        }

        return new WorkerCommand.Node.Call(function, args);
    }

    /* ====================================================================== */

    private String identifier() {
        int start = cursor;
        while (cursor < command.length() && isIdentifierPart(command.charAt(cursor))) {
            cursor++;
        }

        if (start == cursor) {
            throw new IllegalArgumentException(throwPrefix + " has an unexpected character at index " + cursor);
        }

        return command.substring(start, cursor);
    }

    private String stringLiteral() {
        cursor++;
        StringBuilder value = new StringBuilder();

        while (cursor < command.length()) {
            char character = command.charAt(cursor++);
            if (character == '"') {
                return value.toString();
            }

            if (character == '\\' && cursor < command.length()) {
                character = command.charAt(cursor++);
            }

            value.append(character);
        }

        throw new IllegalArgumentException(throwPrefix + " has an unterminated string literal");
    }

    private boolean booleanLiteral() {
        return Boolean.parseBoolean(identifier());
    }

    private double numberLiteral() {
        int start = cursor;
        if (peek(cursor) == '-') {
            cursor++;
        }

        while (cursor < command.length() && (Character.isDigit(command.charAt(cursor)) || command.charAt(cursor) == '.')) {
            cursor++;
        }

        try {
            return Double.parseDouble(command.substring(start, cursor));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(throwPrefix + " has an invalid number at index " + start, e);
        }
    }

    private void skipSpaces() {
        while (cursor < command.length() && command.charAt(cursor) == ' ') {
            cursor++;
        }
    }

    private boolean consume(char expected) {
        if (peek(cursor) != expected) {
            return false;
        }

        cursor++;

        return true;
    }

    private void expect(char expected) {
        if (!consume(expected)) {
            throw new IllegalArgumentException(throwPrefix + " expected '" + expected + "' at index " + cursor);
        }
    }

    private char peek(int index) {
        return index < command.length() ? command.charAt(index) : '\0';
    }

    private boolean isKeyword(String keyword) {
        return command.startsWith(keyword, cursor) && !isIdentifierPart(peek(cursor + keyword.length()));
    }

    private static boolean isIdentifierPart(char character) {
        return Character.isLetterOrDigit(character) || character == '_';
    }

}
