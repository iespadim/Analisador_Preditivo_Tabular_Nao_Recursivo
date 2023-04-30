package negocio;

import java.util.ArrayList;

public class Production {
  private Symbol left;
  private ArrayList<Symbol> right;

  public Production(Symbol left, ArrayList<Symbol> right) {
    this.left = left;
    this.right = right;
  }

  public Symbol getLeft() {
    return left;
  }

  public ArrayList<Symbol> getRight() {
    return right;
  }

  @Override
  public String toString() {
    return left + " -> " + right;
  }
}