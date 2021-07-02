package org.devoware.treasure.calc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TreasureCalculatorTest {

  @Test
  public void test() {
    Share share = TreasureCalculator.forPartyOfSize(5)
        .withPlatinumPieceValues(15)
        .withGoldPieceValues(150, 60, 120, 60, 150, 130, 90)
        .withElectrumPieceValues(160, 90, 50, 190)
        .withSilverPieceValues(220, 180, 160)
        .withCopperPieceValues(600, 1100)
        .computeShare();

    System.out.println("GP: " + share.getGoldPieces());
    System.out.println("SP: " + share.getSilverPieces());
    System.out.println("CP: " + share.getCopperPieces());
    System.out.println();
    System.out.println("LEFTOVER CP: " + share.getLeftOverCopperPieces());
  }


  @Test
  public void test2() {
    Share share = TreasureCalculator.forPartyOfSize(5)
        .withPlatinumPieceValues(9)
        .withGoldPieceValues(9)
        .withElectrumPieceValues(9)
        .withSilverPieceValues(9)
        .withCopperPieceValues(9)
        .computeShare();

    assertThat(share.getGoldPieces()).isEqualTo(20);
    assertThat(share.getSilverPieces()).isEqualTo(8);
    assertThat(share.getCopperPieces()).isEqualTo(9);
    assertThat(share.getLeftOverCopperPieces()).isEqualTo(4);
  }
}
