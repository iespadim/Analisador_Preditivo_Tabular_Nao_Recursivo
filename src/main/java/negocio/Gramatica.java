package negocio;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    HashMap<Symbol, List<Symbol>> conjuntoFirst;
    HashMap<Symbol, List<Symbol>> conjuntoFollow;

    HashMap<String, ArrayList<List<Symbol>>> producoes;
    // hashmap onde cada chave tem diversas produção;
    // cada produção é representada por um arrayList de simbolos
    String simboloInicial;

    public Gramatica(String stringGramatica) {
        naoTerminais = new ArrayList<Symbol>();
        terminais = new ArrayList<Symbol>();
        producoes = new HashMap<String, ArrayList<List<Symbol>>>();

        validaGramatica(stringGramatica);

        conjuntoFirst = new HashMap<Symbol, List<Symbol>>();
        conjuntoFollow = new HashMap<Symbol, List<Symbol>>();
        inicializaFirstFollow();

    }

    private void inicializaFirstFollow() {
        for (Symbol naoTerminal : naoTerminais) {
            conjuntoFirst.put(naoTerminal, new LinkedList<Symbol>());
            conjuntoFollow.put(naoTerminal, new LinkedList<Symbol>());
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
            System.out.println("FIRST(" + naoTerminal.toString() + "): " + conjuntoFirst.get(naoTerminal).toString());
        }

        System.out.println("\nConjuntos FOLLOW:");
        for (Symbol naoTerminal : naoTerminais) {
            System.out.println("FOLLOW(" + naoTerminal.toString() + "): " + conjuntoFollow.get(naoTerminal).toString());
        }
    }



    public void gerarFirst() {
        // percorre os não-terminais
        for (Symbol naoTerminal : naoTerminais) {
            conjuntoFirst.put(naoTerminal, gerarFirst(naoTerminal));
            //gerarFirst(naoTerminal);
        }
    }

    private List<Symbol> gerarFirst(Symbol naoTerminal) {
        List<Symbol> first = new LinkedList<Symbol>();

        // percorre as produções do não-terminal
        for (List<Symbol> producao : producoes.get(naoTerminal.toString())) {

            // verifica se a produção começa com um terminal
            Symbol prod1_ = producao.get(0);
            if (prod1_.isTerminal()) {
                first.add(prod1_);
            } else {
                //começa com um não terminal
                //percorre as produções do não terminal
                //e adiciona o first das produções ao first do não terminal atual
                for (List<Symbol> producao2_ : producoes.get(prod1_.toString())) {
                    if (producao2_.get(0).isTerminal()) {
                        first.add(producao2_.get(0));
                    } else {
                        first.addAll(gerarFirst(producao2_.get(0)));
                    }
                }
            }
        }

        return first;
    }



//    public void calcularConjuntoFollow() {
//        for (String naoTerminal : naoTerminais) {
//            conjuntoFollow.put(naoTerminal, new HashSet<String>());
//        }
//        conjuntoFollow.get(simboloInicial).add("$");
//
//        boolean mudouConjuntoFollow = true;
//        while (mudouConjuntoFollow) {
//            mudouConjuntoFollow = false;
//
//            for (String naoTerminal : naoTerminais) {
//                for (String outroNaoTerminal : naoTerminais) {
//                    for (String producao : producoes.get(outroNaoTerminal)) {
//                        String[] tokens = producao.split("\\s+");
//
//                        for (int i = 0; i < tokens.length; i++) {
//                            if (tokens[i].equals(naoTerminal)) {
//                                if (i == tokens.length - 1) {
//                                    if (mudouConjuntoFollow
//                                            || conjuntoFollow.get(naoTerminal).addAll(conjuntoFollow.get(outroNaoTerminal))) {
//                                        mudouConjuntoFollow = true;
//                                    }
//                                } else {
//                                    HashSet<String> conjuntoFirstProximoToken = new HashSet<String>();
//                                    boolean podeDerivarVazio = true;
//
//                                    for (int j = i + 1; j < tokens.length && podeDerivarVazio; j++) {
//                                        HashSet<String> conjuntoFirstToken = conjuntoFirst.get(tokens[j]);
//                                        conjuntoFirstProximoToken.addAll(conjuntoFirstToken);
//                                        podeDerivarVazio = conjuntoFirstToken.contains("ε");
//                                    }
//
//                                    if (podeDerivarVazio) {
//                                        if (mudouConjuntoFollow
//                                                || conjuntoFollow.get(naoTerminal).addAll(conjuntoFollow.get(outroNaoTerminal))) {
//                                            mudouConjuntoFollow = true;
//                                        }
//                                    }
//
//                                    if (mudouConjuntoFollow
//                                            || conjuntoFollow.get(naoTerminal).addAll(conjuntoFirstProximoToken)) {
//                                        mudouConjuntoFollow = true;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
}

