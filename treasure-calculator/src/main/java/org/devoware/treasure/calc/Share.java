package org.devoware.treasure.calc;

public class Share {
  private final int copperPieces;
  private final int silverPieces;
  private final int goldPieces;
  private final int leftOverCopperPieces;

  static Builder builder() {
    return new Builder();
  }

  private Share(Builder builder) {
    this.copperPieces = builder.copperPieces;
    this.silverPieces = builder.silverPieces;
    this.goldPieces = builder.goldPieces;
    this.leftOverCopperPieces = builder.leftOverCopperPieces;
  }

  public int getCopperPieces() {
    return copperPieces;
  }

  public int getSilverPieces() {
    return silverPieces;
  }

  public int getGoldPieces() {
    return goldPieces;
  }

  public int getLeftOverCopperPieces() {
    return leftOverCopperPieces;
  }

  @Override
  public String toString() {
    return "Share [copperPieces=" + copperPieces + ", silverPieces=" + silverPieces
        + ", goldPieces=" + goldPieces + ", leftOverCopperPieces=" + leftOverCopperPieces + "]";
  }

  static class Builder {
    private int copperPieces = 0;
    private int silverPieces = 0;
    private int goldPieces = 0;
    private int leftOverCopperPieces;

    private Builder() {}

    Builder withCopperPieces(int copperPieces) {
      this.copperPieces = copperPieces;
      return this;
    }

    Builder withSilverPieces(int silverPieces) {
      this.silverPieces = silverPieces;
      return this;
    }

    Builder withGoldPieces(int goldPieces) {
      this.goldPieces = goldPieces;
      return this;
    }

    Builder withLeftOverCopperPieces(int leftOverCopperPieces) {
      this.leftOverCopperPieces = leftOverCopperPieces;
      return this;
    }

    Share build() {
      return new Share(this);
    }

  }

}
