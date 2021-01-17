package org.devoware.tarokka;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

public class CardMapTest {

  @Test
  public void test_treasure_locations_map() {
    List<Card> commonDeck = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() != Suite.HIGH)
        .collect(toList());

    Set<String> values = Sets.newHashSet();
    commonDeck.forEach(card -> {
      String value = CardMap.TREASURE_LOCATION.get(card);
      assertThat(value)
          .as("Expected the TREASURE_LOCATION map to contain a value for card %s", card)
          .isNotNull();
      values.add(value);
    });

    // Each value must be different!
    assertThat(values)
        .as("Expected all of the values in the TREASURE_LOCATION map to be unique")
        .hasSameSizeAs(commonDeck);

  }

  @Test
  public void test_strahds_enemy_map() {
    List<Card> highDeck = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() == Suite.HIGH)
        .collect(toList());

    Set<String> values = Sets.newHashSet();
    highDeck.forEach(card -> {
      String value = CardMap.STRAHDS_ENEMY.get(card);
      assertThat(value)
          .as("Expected the STRAHDS_ENEMY map to contain a value for card %s", card)
          .isNotNull();
      values.add(value);
    });

    // Each value must be different!
    assertThat(values)
        .as("Expected all of the values in the STRAHDS_ENEMY map to be unique")
        .hasSameSizeAs(highDeck);

    // If the value starts with "A. ", it contains a single "\n" with a single "B. " right after it.
    for (Card card : highDeck) {
      String value = CardMap.STRAHDS_ENEMY.get(card);
      if (!value.startsWith("A. ")) {
        continue;
      }
      int lineBreak = value.indexOf('\n');
      assertThat(lineBreak)
          .isNotEqualTo(-1)
          .isLessThan(value.length() - 1);
      assertThat(value.indexOf('\n', lineBreak + 1))
          .isEqualTo(-1);
      int bStart = value.indexOf("B. ");
      assertThat(bStart)
          .isNotEqualTo(-1)
          .isEqualTo(lineBreak + 1);
      assertThat(value.indexOf("B. ", bStart + 1))
          .isEqualTo(-1);
    }
  }

  @Test
  public void test_strahds_location_in_castle_map() {
    List<Card> highDeck = Arrays.stream(Card.values())
        .filter(card -> card.getSuite() == Suite.HIGH)
        .collect(toList());

    Set<String> values = Sets.newHashSet();
    highDeck.forEach(card -> {
      String value = CardMap.STRAHDS_LOCATION_IN_CASTLE.get(card);
      assertThat(value)
          .as("Expected the STRAHDS_LOCATION_IN_CASTLE map to contain a value for card %s", card)
          .isNotNull();
      values.add(value);
    });

    // Each value must be different!
    assertThat(values)
        .as("Expected all of the values in the STRAHDS_LOCATION_IN_CASTLE map to be unique")
        .hasSameSizeAs(highDeck);

  }

}
