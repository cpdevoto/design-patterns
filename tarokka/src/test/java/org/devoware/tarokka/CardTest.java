package org.devoware.tarokka;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class CardTest {

  @Test
  public void test_cards() {
    // Size of deck is 54
    assertThat(Card.values()).hasSize(54);

    // Size of high deck is 14
    List<Card> highDeck = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() == Suite.HIGH)
        .collect(toList());

    assertThat(highDeck).hasSize(14);

    // Size of common deck is 40
    List<Card> commonDeck = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() != Suite.HIGH)
        .collect(toList());

    assertThat(commonDeck).hasSize(40);

    // There are 10 Swords
    List<Card> swords = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() == Suite.SWORDS)
        .collect(toList());

    assertThat(swords).hasSize(10);

    // There are 10 Stars
    List<Card> stars = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() == Suite.STARS)
        .collect(toList());

    assertThat(stars).hasSize(10);

    // There are 10 Coins
    List<Card> coins = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() == Suite.COINS)
        .collect(toList());

    assertThat(coins).hasSize(10);

    // There are 10 Glyphs
    List<Card> glyphs = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() == Suite.GLYPHS)
        .collect(toList());

    assertThat(glyphs).hasSize(10);

    // All cards must have a unique name
    Set<String> names = Arrays.stream(Card.values())
        .map(Card::getName)
        .collect(toSet());

    assertThat(names).hasSize(54);

    // All cards must have a unique description
    Set<String> descriptions = Arrays.stream(Card.values())
        .map(Card::getDescription)
        .collect(toSet());

    assertThat(descriptions).hasSize(54);

    // All common deck cards must have a unique alternate name
    Set<String> commonDeckAltNames = commonDeck.stream()
        .map(Card::getAlternateName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toSet());

    assertThat(commonDeckAltNames).hasSize(40);

    // All high deck cards must have an empty alternate name
    Set<String> highDeckAltNames = highDeck.stream()
        .map(Card::getAlternateName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toSet());

    assertThat(highDeckAltNames).hasSize(0);
  }

}
