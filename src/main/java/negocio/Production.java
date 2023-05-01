package negocio;

import lombok.Getter;

import java.util.ArrayList;

public class Production {
  @Getter
  private Symbol left;
  @Getter
  private ArrayList<Symbol> right;

  public Production(Symbol left, ArrayList<Symbol> right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public String toString() {
    return left + " -> " + right;
  }
}