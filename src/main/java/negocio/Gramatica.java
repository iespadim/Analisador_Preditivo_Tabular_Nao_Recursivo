package negocio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gramatica {
    /**
     * G = ( N, T, P, S ), onde:
     * Simbolo da gramatica
     * N) conjunto finito de não-terminais (ou variáveis)
     * T) conjunto finito de terminais
     * P) conjunto finito de regras de produção
     * S) símbolo inicial da gramática
     */

    String simboloGramatica;
    ArrayList<String> naoTerminais;
    ArrayList<String> terminais;
    HashMap<String,ArrayList<String>> producoes;
    String simboloInicial;

    public Gramatica(String stringGramatica) {
        naoTerminais = new ArrayList<String>();
        terminais = new ArrayList<String>();
        producoes = new HashMap<String,ArrayList<String>>();
        validaGramatica(stringGramatica);

    }

    private void validaGramatica(String stringGramatica) {
        String[] lines = stringGramatica.split("\\r?\\n");

        String grammarStr = lines[0];

        String regex = "(^.*) = \\(\\{([^\\}]+)\\},\\s*\\{([^\\}]+)\\},\\s*([a-zA-Z]),\\s*([a-zA-Z])\\)\\s*$";

        //G = ({E, E’, T, T’, F }, { +, *, (, ), id }, P, E)
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(grammarStr);

        if (matcher.matches()) {
            String name = matcher.group(1);
            String nonTerminals = matcher.group(2);
            String terminals = matcher.group(3);
            String productions = matcher.group(4);
            String startSymbol = matcher.group(5);

            System.out.println("Grammar name: " + name);
            this.simboloGramatica = name;

            System.out.println("Non-terminals: " + nonTerminals);
            String[] nonTerminalsArray = nonTerminals.split(",\\s*");
            for (String nonTerminal : nonTerminalsArray) {
                naoTerminais.add(nonTerminal);
            }

            System.out.println("Terminals: " + terminals);
            String[] terminalsArray = terminals.split(",\\s*");
            for (String terminal : terminalsArray) {
                terminais.add(terminal);
            }

            System.out.println("Productions: " + productions);
            validaProducoes(lines);

            System.out.println("Start symbol: " + startSymbol);
        } else {
            System.out.println("Invalid grammar string");
        }
    }

    private void validaProducoes(String[] lines) {
        //start arraylist for each non-terminal
        for (String naoTerminal : naoTerminais) {
            producoes.put(naoTerminal, new ArrayList<String>());
        }

        //for lines >1 scan each production and add to its non-terminal in the hashmap
        for (int i = 2; i < lines.length-1; i++) {
            String[] production = lines[i].split("->");
            String nonTerminal = production[0].trim();
            String[] productions = production[1].split("\\|");
            ArrayList<String> producoes = new ArrayList<String>();
            for (String prod : productions) {
                producoes.add(prod.trim());
            }
            this.producoes.put(nonTerminal, producoes);
        }
        //print productions
        for (String key : this.producoes.keySet()) {
            //System.out.println(key + " -> " + this.producoes.get(key));
            for(String prod : this.producoes.get(key)){
                System.out.println(key + " -> "+prod);
            }
        }


    }


    public boolean verificaGramaticaLL() {
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
        for (String naoTerminal : naoTerminais) {
            ArrayList<String> producoesNaoTerminal = producoes.get(naoTerminal);
            for (String producao : producoesNaoTerminal) {
                if (producao.startsWith(naoTerminal)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verificaFatoravel() {
        for (String naoTerminal : naoTerminais) {
            ArrayList<String> producoesNaoTerminal = producoes.get(naoTerminal);

            for (int i = 0; i < producoesNaoTerminal.size(); i++) {
                String producao1 = producoesNaoTerminal.get(i);
                String[] prod1Tokens = producao1.split("\\s+");

                for (int j = i + 1; j < producoesNaoTerminal.size(); j++) {
                    String producao2 = producoesNaoTerminal.get(j);
                    String[] prod2Tokens = producao2.split("\\s+");

                    // Verifica se o primeiro símbolo de ambas as produções é o mesmo
                    if (prod1Tokens[0].equals(prod2Tokens[0])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
