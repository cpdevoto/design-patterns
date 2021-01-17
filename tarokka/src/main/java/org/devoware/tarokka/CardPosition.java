package org.devoware.tarokka;

import static java.util.stream.Collectors.toList;
import static org.devoware.tarokka.CardMap.STRAHDS_ENEMY;
import static org.devoware.tarokka.CardMap.STRAHDS_LOCATION_IN_CASTLE;
import static org.devoware.tarokka.CardMap.TREASURE_LOCATION;
import static org.devoware.tarokka.Deck.DRAW_FROM_COMMON_DECK;
import static org.devoware.tarokka.Deck.DRAW_FROM_HIGH_DECK;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum CardPosition {
  // @formatter:off
  ONE(1, 
      "This card tells of history. Knowledge of the ancient will help you better understand your enemy.",
      DRAW_FROM_COMMON_DECK,
      TREASURE_LOCATION),
  TWO(2, 
      "This card tells of a powerful force for good and protection, a holy symbol of great hope.",
      DRAW_FROM_COMMON_DECK,
      TREASURE_LOCATION),
  THREE(3, 
      "This is a card of power and strength. It tells of a weapon of vengeance: a sword of sunlight.",
      DRAW_FROM_COMMON_DECK,
      TREASURE_LOCATION),
  FOUR(4, 
      "This card sheds light on one who will help you greatly in the battle against darkness.",
      DRAW_FROM_HIGH_DECK, 
      STRAHDS_ENEMY),
  FIVE(5, 
      "Your enemy is a creature of darkness, whose powers are beyond mortality. This card will lead you to him!",
      DRAW_FROM_HIGH_DECK,
      STRAHDS_LOCATION_IN_CASTLE);
  // @formatter:on


  private final int index;
  private final String description;
  private final Function<Deck, Card> drawFunction;
  private final CardMap cardMap;

  private CardPosition(int index, String description, Function<Deck, Card> drawFunction,
      CardMap cardMap) {
    this.index = index;
    this.description = description;
    this.drawFunction = drawFunction;
    this.cardMap = cardMap;
  }

  public int getIndex() {
    return index;
  }

  public String getDescription() {
    return description;
  }

  public Card drawCard(Deck deck) {
    return drawFunction.apply(deck);
  }

  public String getResult(Card card) {
    String result = cardMap.get(card);
    if (result.startsWith("A. ")) {
      int optionIdx = (int) (Math.random() * 2);
      result = extractResult(result, optionIdx);
    }
    return result;
  }

  static String extractResult(String result, int optionIdx) {
    List<String> options = Arrays.stream(result.split("[\r\n]"))
        .map(s -> s.substring(3))
        .collect(toList());
    result = options.get(optionIdx);
    return result;
  }


}
