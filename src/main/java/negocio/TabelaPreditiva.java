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

    public boolean contemRegra(Symbol naoTerminal, Symbol simboloFollow) {
        for (Symbol terminal : tabela.get(naoTerminal).keySet()) {
            if (terminal.equals(simboloFollow)) {
                return true;
            }
        }
        return false;
    }
}
