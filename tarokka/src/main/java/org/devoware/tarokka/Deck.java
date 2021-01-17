package org.devoware.tarokka;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Deck {
  static final Function<Deck, Card> DRAW_FROM_COMMON_DECK =
      deck -> deck.drawFromCommonDeck();
  static final Function<Deck, Card> DRAW_FROM_HIGH_DECK = deck -> deck.drawFromHighDeck();


  private final List<Card> highDeck;
  private final List<Card> commonDeck;

  public Deck() {
    this.highDeck = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() == Suite.HIGH)
        .collect(toList());
    this.commonDeck = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() != Suite.HIGH)
        .collect(toList());
  }

  public Card drawFromHighDeck() {
    if (highDeck.isEmpty()) {
      return null;
    }
    int idx = (int) (Math.random() * highDeck.size());
    return highDeck.remove(idx);
  }

  public Card drawFromCommonDeck() {
    if (commonDeck.isEmpty()) {
      return null;
    }
    int idx = (int) (Math.random() * commonDeck.size());
    return commonDeck.remove(idx);
  }
}
