package org.devoware.treasure.calc;

import static com.google.common.base.Preconditions.checkArgument;
import static org.devoware.treasure.calc.Currency.ELECTRUM;
import static org.devoware.treasure.calc.Currency.GOLD;
import static org.devoware.treasure.calc.Currency.PLATINUM;
import static org.devoware.treasure.calc.Currency.SILVER;

import java.util.Arrays;

public class TreasureCalculator {


  private final int partySize;
  private int cp;

  public static TreasureCalculator forPartyOfSize(int partySize) {
    return new TreasureCalculator(partySize);
  }

  private TreasureCalculator(int partySize) {
    checkArgument(partySize > 0, "expected a positive integer partySize");
    this.partySize = partySize;
  }

  public TreasureCalculator withCopperPieceValues(int... values) {
    Arrays.stream(values)
        .forEach(value -> cp += value);
    return this;
  }

  public TreasureCalculator withSilverPieceValues(int... values) {
    Arrays.stream(values)
        .forEach(value -> cp += SILVER.convertToCopper(value));
    return this;
  }

  public TreasureCalculator withElectrumPieceValues(int... values) {
    Arrays.stream(values)
        .forEach(value -> cp += ELECTRUM.convertToCopper(value));
    return this;
  }

  public TreasureCalculator withGoldPieceValues(int... values) {
    Arrays.stream(values)
        .forEach(value -> cp += GOLD.convertToCopper(value));
    return this;
  }

  public TreasureCalculator withPlatinumPieceValues(int... values) {
    Arrays.stream(values)
        .forEach(value -> cp += PLATINUM.convertToCopper(value));
    return this;
  }


  public Share computeShare() {
    int leftOverCopperPieces = this.cp % this.partySize;
    int copperPieces = this.cp / this.partySize;

    int silverPieces = copperPieces / 10;
    copperPieces %= 10;

    int goldPieces = silverPieces / 10;
    silverPieces %= 10;

    return Share.builder()
        .withGoldPieces(goldPieces)
        .withSilverPieces(silverPieces)
        .withCopperPieces(copperPieces)
        .withLeftOverCopperPieces(leftOverCopperPieces)
        .build();
  }

}
