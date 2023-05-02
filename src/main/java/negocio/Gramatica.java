package negocio;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static negocio.Symbol.EPSILON;

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
    ArrayList<Symbol> naoTerminais;
    ArrayList<Symbol> terminais;

    HashMap<String, List<Symbol>> conjuntoFirst;
    HashMap<String, List<Symbol>> conjuntoFollow;

    HashMap<String, ArrayList<List<Symbol>>> producoes;
    // hashmap onde cada chave tem diversas produção;
    // cada produção é representada por um arrayList de simbolos
    String simboloInicial;

    public Gramatica(String stringGramatica) {
        naoTerminais = new ArrayList<Symbol>();
        terminais = new ArrayList<Symbol>();
        producoes = new HashMap<String, ArrayList<List<Symbol>>>();

        validaGramatica(stringGramatica);

        conjuntoFirst = new HashMap<String, List<Symbol>>();
        conjuntoFollow = new HashMap<String, List<Symbol>>();
        inicializaFirstFollow();

    }

    private void inicializaFirstFollow() {
        for (Symbol naoTerminal : naoTerminais) {
            conjuntoFirst.put(naoTerminal.toString(), new LinkedList<Symbol>());
            conjuntoFollow.put(naoTerminal.toString(), new LinkedList<Symbol>());
        }
    }

    private void validaGramatica(String stringGramatica) {
        String[] lines = stringGramatica.split("\\r?\\n");

        String grammarStr = lines[0];

        String regex = "(^.*) = \\(\\{([^\\}]+)\\},\\s*\\{([^\\}]+)\\},\\s*([a-zA-Z]),\\s*([a-zA-Z])\\)\\s*$";

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
                naoTerminais.add(new Symbol(nonTerminal, false));
            }

            System.out.println("Terminals: " + terminals);
            String[] terminalsArray = terminals.split(",\\s*");
            for (String terminal : terminalsArray) {
                terminais.add(new Symbol(terminal, true));
            }

            System.out.println("Productions: " + productions);
            validaProducoes(lines);

            System.out.println("Start symbol: " + startSymbol);
            simboloInicial = startSymbol;
//            naoTerminais.add(startSymbol);
        } else {
            System.out.println("Invalid grammar string");
        }
    }

    private void validaProducoes(String[] lines) {
        // Inicia o ArrayList para cada não-terminal
        for (Symbol naoTerminal : naoTerminais) {
            producoes.put(naoTerminal.toString(), new ArrayList<List<Symbol>>());
        }

        // Cria uma lista com todos os símbolos e ordena pelo tamanho em ordem decrescente
        List<Symbol> allSymbols = new ArrayList<>();
        allSymbols.addAll(terminais);
        allSymbols.addAll(naoTerminais);
        allSymbols.add(EPSILON);
        allSymbols.sort((s1, s2) -> Integer.compare(s2.toString().length(), s1.toString().length()));

        // Para linhas >2, analisa cada produção e adiciona ao seu não-terminal no hashmap
        for (int i = 2; i < lines.length - 1; i++) {
            // Produção de cada linha
            String[] production = lines[i].split("->");
            // Não terminal da produção (esquerda)
            String nonTerminal = production[0].trim();
            // Produções do não terminal (direitas)
            String[] productions = production[1].split("\\|");
            // ArrayList a ser retornado
            ArrayList<List<Symbol>> producoesResultado = new ArrayList<>();

            // Para cada string de cada produção
            for (String prod : productions) {
                System.out.println("Produção: " + prod);

                // Cria uma lista para guardar a produção atual
                ArrayList<Symbol> producao = new ArrayList<>();

                // Inicializa o índice da posição atual na string 'prod'
                int index = 0;

                // Percorre a string 'prod' enquanto houver caracteres
                while (index < prod.length()) {
                    boolean symbolFound = false;

                    // Percorre todos os símbolos (terminais e não terminais) ordenados pelo tamanho
                    for (Symbol symbol : allSymbols) {
                        String symbolStr = symbol.toString();

                        // Se a string 'prod' contém o símbolo na posição atual
                        if (prod.startsWith(symbolStr, index)) {
                            System.out.println((symbol.isTerminal() ? "Terminal: " : "Não terminal: ") + symbol);
                            // Adiciona o símbolo na lista de produções
                            producao.add(symbol);
                            // Atualiza o índice da posição atual na string 'prod'
                            index += symbolStr.length();
                            symbolFound = true;
                            break;
                        }
                    }

                    // Caso nenhum símbolo seja encontrado na posição atual, incrementa o índice
                    if (!symbolFound) {
                        index++;
                    }
                }

                // Adiciona a lista de produção atual ao resultado
                producoesResultado.add(producao);
            }

            // Adiciona as produções do não terminal no hashmap
            producoes.put(nonTerminal, producoesResultado);
        }

        // Print produções
        for (Symbol naoTerminal : naoTerminais) {
            System.out.println(naoTerminal + " -> " + producoes.get(naoTerminal.toString()));
        }
    }

    public boolean verificaGramaticaLL() {
        boolean temRecursaoEsquerda = verificaRecursaoEsquerda();
        boolean eFatoravel = verificaFatoravel();
        //boolean eFatoravel = false;

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
        for (Symbol naoTerminal : naoTerminais) {
            ArrayList<List<Symbol>> producoesNaoTerminal = producoes.get(naoTerminal.toString());
            for (List<Symbol> producao : producoesNaoTerminal) {
                if (producao.get(0).equals(naoTerminal)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verificaFatoravel() {
        for (Symbol naoTerminal : naoTerminais) {
            ArrayList<List<Symbol>> producao = producoes.get(naoTerminal.toString());

            for (int i = 0; i < producao.size(); i++) {
                for (int j = i + 1; j < producao.size(); j++) {
                    if (producao.get(i).get(0).equals(producao.get(j).get(0))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }




    public void exibirFirstFollow() {
        System.out.println("Conjuntos FIRST:");
        for (Symbol naoTerminal : naoTerminais) {
            System.out.println("FIRST(" + naoTerminal.toString() + "): " + conjuntoFirst.get(naoTerminal.toString()).toString());
        }

        System.out.println("\nConjuntos FOLLOW:");
        for (Symbol naoTerminal : naoTerminais) {
            System.out.println("FOLLOW(" + naoTerminal.toString() + "): " + conjuntoFollow.get(naoTerminal.toString()).toString());
        }
    }



    public void gerarFirst() {
        // percorre os não-terminais
        for (Symbol naoTerminal : naoTerminais) {
            conjuntoFirst.put(naoTerminal.toString(), gerarFirst(naoTerminal));
            //gerarFirst(naoTerminal);
        }
    }

    private List<Symbol> gerarFirst(Symbol naoTerminal) {
        System.out.println("Gerando FIRST(" + naoTerminal.toString() + ")");
        List<Symbol> first = new LinkedList<Symbol>();

        // percorre as produções do não-terminal
        for (List<Symbol> producao : producoes.get(naoTerminal.toString())) {
            int pos = 0;
            boolean shouldContinue = true;

            while (pos < producao.size() && shouldContinue) {
                Symbol currentSymbol = producao.get(pos);

                if (currentSymbol.isTerminal() || currentSymbol.equals(EPSILON)) {
                    first.add(currentSymbol);
                    shouldContinue = false;
                } else {
                    List<Symbol> firstOfCurrentSymbol = gerarFirst(currentSymbol);

                    if (firstOfCurrentSymbol.contains(EPSILON)) {
                        first.addAll(firstOfCurrentSymbol.stream().filter(s -> !s.equals(EPSILON)).collect(Collectors.toList()));
                    } else {
                        first.addAll(firstOfCurrentSymbol);
                        shouldContinue = false;
                    }
                }
                pos++;
            }
        }
        return first;
    }




    public void gerarFollow() {
        // Inicializa o conjunto FOLLOW do símbolo inicial com o símbolo delimitador $
        conjuntoFollow.get(simboloInicial).add(new Symbol("$", true));

        boolean mudou;
        int maxIteracoes = 10; // Define o limite máximo de iterações
        int iteracaoAtual = 0;
        do {
            mudou = false;
            iteracaoAtual++; // Incrementa a contagem de iterações
            for (Symbol naoTerminal : naoTerminais) {
                ArrayList<List<Symbol>> producoesNaoTerminal = producoes.get(naoTerminal.toString());
                for (List<Symbol> producao : producoesNaoTerminal) {
                    for (int i = 0; i < producao.size(); i++) {
                        Symbol simbolo = producao.get(i);
                        if (!simbolo.isTerminal()) {
                            List<Symbol> followSimbolo = conjuntoFollow.get(simbolo.toString());
                            int prevSize = followSimbolo.size();
                            if (i < producao.size() - 1) {
                                Symbol proxSimbolo = producao.get(i + 1);
                                if (proxSimbolo.isTerminal()) {
                                    followSimbolo.add(proxSimbolo);
                                } else {
                                    List<Symbol> firstProxSimbolo = conjuntoFirst.get(proxSimbolo.toString());
                                    followSimbolo.addAll(firstProxSimbolo.stream().filter(s -> !s.toString().equals(EPSILON.toString())).collect(Collectors.toList()));
                                    if (firstProxSimbolo.toString().contains(EPSILON.toString())) {
                                        followSimbolo.addAll(conjuntoFollow.get(proxSimbolo.toString()));
                                    }
                                }
                            } else {
                                followSimbolo.addAll(conjuntoFollow.get(naoTerminal.toString()));
                            }
                            if (prevSize != followSimbolo.size()) {
                                mudou = true;
                            }
                        }
                    }
                }
            }
            // Interrompe o loop se o limite máximo de iterações for atingido
            if (iteracaoAtual >= maxIteracoes) {
                System.out.println("Limite máximo de iterações atingido. Interrompendo o loop.");
                break;
            }
        } while (mudou);

        //remove duplicados do conjunto follow
        for (Symbol naoTerminal : naoTerminais) {
            List<Symbol> follow = conjuntoFollow.get(naoTerminal.toString());
            conjuntoFollow.put(naoTerminal.toString(), follow.stream().distinct().collect(Collectors.toList()));
        }
    }



}

