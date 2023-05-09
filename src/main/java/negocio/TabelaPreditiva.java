package negocio;

import java.util.HashMap;
import java.util.List;

public class TabelaPreditiva {
    private HashMap<Symbol, HashMap<Symbol, List<Symbol>>> tabela;

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
        return tabela.get(naoTerminal).get(terminal);
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
