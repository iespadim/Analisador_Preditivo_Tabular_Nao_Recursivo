package negocio;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grammar {
    /**
     * G = ( N, T, P, S ), onde:
     * Symbol da gramatica
     * N) conjunto finito de não-terminais (ou variáveis)
     * T) conjunto finito de terminais
     * P) conjunto finito de regras de produção
     * S) símbolo inicial da gramática
     */

    String name;
    ArrayList<Symbol> symbols;
    ArrayList<Production> productions;
    String symbolInicial;

    public Grammar(String grammar) {
        this.symbols = new ArrayList<Symbol>();
        this.productions = new ArrayList<Production>();
        constructGrammar(grammar);
    }

    private void constructGrammar(String grammar) {
        String[] lines = grammar.split("\\r?\\n");

        String grammarStr = lines[0];

        String regex = "(^.*) = \\(\\{([^\\}]+)\\},\\s*\\{([^\\}]+)\\},\\s*([a-zA-Z]),\\s*([a-zA-Z])\\)\\s*$";

        // G = ({E, E’, T, T’, F }, { +, *, (, ), id }, P, E)
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(grammarStr);

        if (matcher.matches()) {
            String name = matcher.group(1);
            String nonTerminals = matcher.group(2);
            String terminals = matcher.group(3);
            String productions = matcher.group(4);
            String startSymbol = matcher.group(5);

            System.out.println("Grammar name: " + name);
            this.name = name;

            System.out.println("Non-terminals: " + nonTerminals);
            String[] nonTerminalsArray = nonTerminals.split(",\\s*");
            for (String nonTerminal : nonTerminalsArray) {
                symbols.add(new Symbol(nonTerminal, false));
            }

            System.out.println("Terminals: " + terminals);
            String[] terminalsArray = terminals.split(",\\s*");
            for (String terminal : terminalsArray) {
                symbols.add(new Symbol(terminal, true));
            }

            System.out.println("Productions: " + productions);
            validateProductions(lines);

            System.out.println("Start symbol: " + startSymbol);
        } else {
            System.out.println("Invalid grammar string");
        }
    }

    private void validateProductions(String[] lines) {
        // start arraylist for each non-terminal
        for (Symbol nonTerminal : getSymbols(false)) {
            this.productions.add(new Production(nonTerminal, new ArrayList<Symbol>()));
        }

        // for lines >1 scan each production and add to its non-terminal in the hashmap
        for (int i = 2; i < lines.length - 1; i++) {
            String[] productionLines = lines[i].split("->");
            String[] rightSymbolLines = productionLines[1].split("\\|");

            Symbol leftSymbol = new Symbol(productionLines[0].trim(), false);

            ArrayList<Symbol> rightSymbols = new ArrayList<Symbol>();
            for (String prod : rightSymbolLines) {
                rightSymbols.add(new Symbol(prod.trim(), true));
            }

            this.productions.add(new Production(leftSymbol, rightSymbols));
        }
    }

    public boolean validateGrammar() {
        boolean temRecursaoEsquerda = validateRecursion();
        boolean eFatoravel = verificaFatoravel();

        if (temRecursaoEsquerda || eFatoravel) {
            if (temRecursaoEsquerda) {
                System.out.println("A gramática possui recursão à esquerda");
            }
            if (eFatoravel) {
                System.out.println("A gramática é fatorável");
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean validateRecursion() {
        for (Production production : this.productions) {
            Symbol left = production.getLeft();
            ArrayList<Symbol> rights = production.getRight();
            System.out.println(rights);

            for (Symbol right : rights) {
                System.out.println(right);
                if (left.equals(right)) {
                    System.out.println("Recursão à esquerda");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verificaFatoravel() {
        // for (Production production : this.productions) {
        // Symbol left = production.getLeft();
        // ArrayList<Symbol> rights = production.getRight();

        // Set<Symbol> symbolsSeen = new HashSet<>();
        // for (Symbol symbol : this.rights) {
        // if (symbolsSeen.contains(symbol)) {
        // return true;
        // }
        // symbolsSeen.add(symbol);
        // }
        // }
        // return false;
        return false;
    }

    private ArrayList<Symbol> getSymbols(Boolean type) {
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();

        for (Symbol symbol : this.symbols) {
            if (symbol.isTerminal() == type) {
                symbols.add(symbol);
            }
        }

        return symbols;
    }
}
