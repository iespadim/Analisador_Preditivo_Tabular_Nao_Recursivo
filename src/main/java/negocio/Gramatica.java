package negocio;

import static negocio.Symbol.EPSILON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class Gramatica {
    /**
     * G = ( N, T, P, S ), onde:
     * Simbolo da gramatica
     * N) conjunto finito de não-terminais (ou variáveis)
     * T) conjunto finito de terminais
     * P) conjunto finito de regras de produção
     * S) símbolo inicial da gramática
     */

    @Getter
    String simboloGramatica;
    @Getter
    ArrayList<Symbol> naoTerminais;
    @Getter
    ArrayList<Symbol> terminais;
    @Getter
    @Setter
    TabelaPreditiva tabelaPreditiva;

    @Getter
    public HashMap<String, List<Symbol>> conjuntoFirst;
    @Getter
    public HashMap<String, List<Symbol>> conjuntoFollow;

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
            terminais.add(new Symbol("$", true));

            System.out.println("Productions: " + productions);
            validaProducoes(lines);

            System.out.println("Start symbol: " + startSymbol);
            simboloInicial = startSymbol;
            // naoTerminais.add(startSymbol);
        } else {
            System.out.println("Invalid grammar string");
        }
    }

    private void validaProducoes(String[] lines) {
        // Inicia o ArrayList para cada não-terminal
        for (Symbol naoTerminal : naoTerminais) {
            producoes.put(naoTerminal.toString(), new ArrayList<List<Symbol>>());
        }

        // Cria uma lista com todos os símbolos e ordena pelo tamanho em ordem
        // decrescente
        List<Symbol> allSymbols = new ArrayList<>();
        allSymbols.addAll(terminais);
        allSymbols.addAll(naoTerminais);
        allSymbols.add(EPSILON);
        allSymbols.sort((s1, s2) -> Integer.compare(s2.toString().length(), s1.toString().length()));

        // Para linhas >2, analisa cada produção e adiciona ao seu não-terminal no
        // hashmap
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
        // boolean eFatoravel = false;

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
            System.out.println(
                    "FIRST(" + naoTerminal.toString() + "): " + conjuntoFirst.get(naoTerminal.toString()).toString());
        }

        System.out.println("\nConjuntos FOLLOW:");
        for (Symbol naoTerminal : naoTerminais) {
            System.out.println(
                    "FOLLOW(" + naoTerminal.toString() + "): " + conjuntoFollow.get(naoTerminal.toString()).toString());
        }
    }

    public HashMap<String, List<Symbol>> gerarFirst() {
        // percorre os não-terminais
        for (Symbol naoTerminal : naoTerminais) {
            conjuntoFirst.put(naoTerminal.toString(), gerarFirst(naoTerminal));
            // gerarFirst(naoTerminal);
        }

        return conjuntoFirst;
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
                        first.addAll(firstOfCurrentSymbol.stream().filter(s -> !s.equals(EPSILON))
                                .collect(Collectors.toList()));
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
        conjuntoFollow.get(simboloInicial).add(new Symbol("$", true));

        boolean mudou;
        int maxIteracoes = 10;
        int iteracaoAtual = 0;
        do {
            mudou = false;
            iteracaoAtual++;
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
                                    followSimbolo.addAll(firstProxSimbolo.stream()
                                            .filter(s -> !s.toString().equals(EPSILON.toString()))
                                            .collect(Collectors.toList()));
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
            // interrompe o loop se o limite máximo de iteracões for atingido
            if (iteracaoAtual >= maxIteracoes) {
                System.out.println("Limite máximo de iterações atingido. Interrompendo o loop.");
                break;
            }
        } while (mudou);

        // remove duplicados do conjunto follow
        for (Symbol naoTerminal : naoTerminais) {
            List<Symbol> follow = conjuntoFollow.get(naoTerminal.toString());
            conjuntoFollow.put(naoTerminal.toString(), follow.stream().distinct().collect(Collectors.toList()));
        }
    }

    public TabelaPreditiva montarTabelaPreditivaTabular() {
        tabelaPreditiva = new TabelaPreditiva();

        for (Symbol naoTerminal : naoTerminais) {
            ArrayList<List<Symbol>> producoesNaoTerminal = producoes.get(naoTerminal.toString());
            for (List<Symbol> producao : producoesNaoTerminal) {
                List<Symbol> first = gerarFirstDaProducao(producao);

                for (Symbol simboloFirst : first) {
                    if (!simboloFirst.equals(EPSILON)) {
                        tabelaPreditiva.addRegra(naoTerminal, simboloFirst, producao);
                    } else {
                        List<Symbol> followNaoTerminal = conjuntoFollow.get(naoTerminal.toString());
                        for (Symbol simboloFollow : followNaoTerminal) {
                            tabelaPreditiva.addRegra(naoTerminal, simboloFollow, producao);
                        }
                    }
                }
            }
        }

        System.out.println("\n\nTabela Preditiva Tabular: ");
        System.out.println(tabelaPreditiva);
        return tabelaPreditiva;
    }

    public List<Symbol> gerarFirstDaProducao(List<Symbol> producao) {
        List<Symbol> first = new LinkedList<>();

        int pos = 0;
        boolean shouldContinue = true;

        while (pos < producao.size() && shouldContinue) {
            Symbol currentSymbol = producao.get(pos);

            if (currentSymbol.isTerminal() || currentSymbol.equals(EPSILON)) {
                first.add(currentSymbol);
                shouldContinue = false;
            } else {
                List<Symbol> firstOfCurrentSymbol = conjuntoFirst.get(currentSymbol.toString());

                if (firstOfCurrentSymbol.contains(EPSILON)) {
                    first.addAll(
                            firstOfCurrentSymbol.stream().filter(s -> !s.equals(EPSILON)).collect(Collectors.toList()));
                } else {
                    first.addAll(firstOfCurrentSymbol);
                    shouldContinue = false;
                }
            }
            pos++;
        }

        return first;
    }

    public boolean analisarEntrada(String entrada) {
        // Inicializa a pilha com o símbolo inicial e o fim de pilha ($)
        LinkedList<Symbol> pilha = new LinkedList<>();

        pilha.add(new Symbol("$", true));
        pilha.add(new Symbol(simboloInicial, false));

        int i = 0;
        while (!pilha.isEmpty()) {

            System.out.println("Pilha: " + pilha);
            // Remove o topo da pilha
            Symbol topo = pilha.removeLast();
            System.out.println("Topo: " + topo);

            // Se o topo for um terminal
            if (topo.isTerminal()) {
                System.out.println("Topo é terminal");
                // Se o topo for igual ao próximo símbolo da entrada
                if (topo.toString().equals(Character.toString(entrada.charAt(i)))) {
                    // Avança a entrada
                    i++;
                } else {
                    // A entrada não é reconhecida pela gramática
                    return false;
                }
            } else { // Senão, o topo é um não-terminal
                // Busca na tabela preditiva a produção a ser usada
                System.out.println("Topo é não-terminal");
                List<Symbol> producao = tabelaPreditiva.getProducao(topo,
                        new Symbol(String.valueOf(entrada.charAt(i)), false));

                System.out.println("Produção: " + producao);

                // Se não houver produção para o símbolo atual
                if (producao == null) {
                    // A entrada não é reconhecida pela gramática
                    return false;
                }

                // Se houver produção, adiciona os símbolos da produção na pilha (em ordem
                // inversa)
                for (int j = producao.size() - 1; j >= 0; j--) {
                    pilha.add(producao.get(j));
                }
            }
        }

        // Se a entrada foi completamente reconhecida pela gramática, retorna true
        return true;
    }
}
