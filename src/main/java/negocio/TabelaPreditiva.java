package negocio;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;

public class TabelaPreditiva {
    @Getter
    public static HashMap<Symbol, HashMap<Symbol, List<Symbol>>> tabela;

    public TabelaPreditiva() {
        tabela = new HashMap<>();
    }

    public void addRegra(Symbol naoTerminal, Symbol terminal, List<Symbol> producao) {
        tabela.computeIfAbsent(naoTerminal, k -> new HashMap<>()).put(terminal, producao);
    }

    public List<Symbol> getRegra(Symbol naoTerminal, Symbol terminal) {
        return tabela.getOrDefault(naoTerminal, new HashMap<>()).get(terminal);
    }

    public List<Symbol> getProducao(Symbol naoTerminal, Symbol terminal) {
//        HashMap<Symbol, List<Symbol>> aux2 = tabela.get(naoTerminal);
//        System.out.println(aux2);
//        System.out.println(tabela.containsKey(naoTerminal.toString()));
////        System.out.println(tabela.containsKey());
//        HashMap<Symbol, List<Symbol>> aux = tabela.get(naoTerminal.toString());
//        List<Symbol> aux = aux.get(terminal);
//        System.out.println(aux);
        for (Symbol naoTerminalDaTabela : tabela.keySet()) {
            if (naoTerminalDaTabela.equals(naoTerminal)) {
                for (Symbol terminalDaTabela : tabela.get(naoTerminalDaTabela).keySet()) {
                    if (terminalDaTabela.equals(terminal)) {
                        return tabela.get(naoTerminalDaTabela).get(terminalDaTabela);
                    }
                }
            }
        }
        return null;
    }

    public boolean contemRegra(Symbol naoTerminal, Symbol simboloFollow) {
        for (Symbol terminal : tabela.get(naoTerminal).keySet()) {
            if (terminal.equals(simboloFollow)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol naoTerminal : tabela.keySet()) {
            sb.append(naoTerminal).append(":\n");
            for (Symbol terminal : tabela.get(naoTerminal).keySet()) {
                sb.append("\t").append(terminal).append(" -> ");
                for (Symbol simbolo : tabela.get(naoTerminal).get(terminal)) {
                    sb.append(simbolo).append(" ");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
