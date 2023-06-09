package negocio;

import lombok.Getter;
import lombok.Setter;

public class Symbol {
    public static final Symbol EPSILON = new Symbol("E", true);
    @Getter
    private String name;
    @Getter
    private boolean isTerminal;

//   public static final Symbol EPSILON = new Symbol("E");
//   public static final Symbol END_OF_INPUT = new Symbol("$");

    public Symbol(String name, Boolean isTerminal) {
        this.name = name;
        this.isTerminal = isTerminal;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Symbol)) {
            return false;
        }
        Symbol other = (Symbol) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}