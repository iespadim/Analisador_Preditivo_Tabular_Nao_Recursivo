package negocio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    String grammarName;
    ArrayList<Symbol> symbols;
    ArrayList<Production> productions;
    String startSymbol;

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
            String name_ = matcher.group(1);
            String nonTerminals_ = matcher.group(2);
            String terminals_ = matcher.group(3);
            String productions_ = matcher.group(4);
            String startSymbol_ = matcher.group(5);

            System.out.println("Grammar name: " + name_);
            grammarName = name_;

            System.out.println("Non-terminals: " + nonTerminals_);
            String[] nonTerminalsArray = nonTerminals_.split(",\\s*");
            for (String nonTerminal : nonTerminalsArray) {
                symbols.add(new Symbol(nonTerminal, false));
            }

            System.out.println("Terminals: " + terminals_);
            String[] terminalsArray = terminals_.split(",\\s*");
            for (String terminal : terminalsArray) {
                symbols.add(new Symbol(terminal, true));
            }

            System.out.println("Start symbol: " + startSymbol_);
            startSymbol = startSymbol_; // Store the start symbol
            productions.add(new Production(new Symbol(startSymbol_, false), new ArrayList<Symbol>())); // Add the start symbol to the productions

            System.out.println("Productions: " + productions_);
            validateProductions(lines);


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

            for (int j = 0; j < productions.size() ; j++) {
                if (productions.get(j).getLeft().equals(leftSymbol)) {
                    productions.get(j).getRight().addAll(rightSymbols);
                    break;
                }
            }
        }
    }

    public boolean validateGrammar() {
        boolean temRecursaoEsquerda = verificaRecursaoEsquerda();
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

    private boolean verificaRecursaoEsquerda() {
        for (Production production : this.productions) {
            Symbol leftSymbol = production.getLeft();
            for (Symbol rightSymbol : production.getRight()) {
                if (rightSymbol.isTerminal()) {
                    continue;
                }
                Symbol nonTerminalRightSymbol = getNonTerminalByName(rightSymbol.getName());
                if (leftSymbol.equals(nonTerminalRightSymbol)) {
                    // Recursão à esquerda direta
                    return true;
                } else {
                    // Verificar a recursão à esquerda indireta
                    if (hasIndirectLeftRecursion(leftSymbol, nonTerminalRightSymbol, new HashSet<>())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasIndirectLeftRecursion(Symbol leftSymbol, Symbol rightSymbol, Set<Symbol> visited) {
        if (visited.contains(rightSymbol)) {
            return false;
        }
        visited.add(rightSymbol);

        for (Production production : this.productions) {
            if (production.getLeft().equals(rightSymbol)) {
                for (Symbol nextSymbol : production.getRight()) {
                    if (nextSymbol.isTerminal()) {
                        continue;
                    }
                    Symbol nonTerminalNextSymbol = getNonTerminalByName(nextSymbol.getName());
                    if (leftSymbol.equals(nonTerminalNextSymbol)) {
                        return true;
                    }
                    if (hasIndirectLeftRecursion(leftSymbol, nonTerminalNextSymbol, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Symbol getNonTerminalByName(String name) {
        for (Symbol symbol : this.symbols) {
            if (!symbol.isTerminal() && symbol.getName().equals(name)) {
                return symbol;
            }
        }
        return null;
    }




    private boolean verificaFatoravel() {
        for (Production production : this.productions) {
            ArrayList<Symbol> rights = production.getRight();
            int n = rights.size();
            for (int i = 0; i < n - 1; i++) {
                Symbol symbol1 = rights.get(i);
                String prefix1 = symbol1.getName().substring(0, 1);
                for (int j = i + 1; j < n; j++) {
                    Symbol symbol2 = rights.get(j);
                    String prefix2 = symbol2.getName().substring(0, 1);
                    if (prefix1.equals(prefix2)) {
                        return true;
                    }
                }
            }
        }
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
