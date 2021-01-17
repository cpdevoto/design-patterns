package org.devoware.tarokka;

public class Main {

  public static void main(String[] args) {
    Deck deck = new Deck();

    for (CardPosition cardPosition : CardPosition.values()) {
      drawCard(deck, cardPosition);
    }
  }


  private static void drawCard(Deck deck, CardPosition cardPosition) {
    Card card = cardPosition.drawCard(deck);
    System.out.println("--------------------------------------------");
    System.out.println(String.format("CARD %d: %s", cardPosition.getIndex(), card));
    System.out.println("--------------------------------------------");
    System.out.println();
    System.out.println(cardPosition.getDescription());
    System.out.println();
    System.out.println(String.format("RESULT: %s", cardPosition.getResult(card)));
    System.out.println();
    System.out.println();
  }

  private Main() {}
}
