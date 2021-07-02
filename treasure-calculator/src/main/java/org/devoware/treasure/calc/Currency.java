package org.devoware.treasure.calc;

import com.google.common.base.Preconditions;

public enum Currency {
  COPPER {

    @Override
    protected int convertToCopper(int value) {
      return value;
    }

  },
  SILVER {

    @Override
    protected int convertToCopper(int value) {
      return 10 * value;
    }

  },
  ELECTRUM {

    @Override
    protected int convertToCopper(int value) {
      return 5 * 10 * value;
    }

  },
  GOLD {

    @Override
    protected int convertToCopper(int value) {
      return 10 * 10 * value;
    }

  },
  PLATINUM {

    @Override
    protected int convertToCopper(int value) {
      return 10 * 10 * 10 * value;
    }

  };

  public int toCopperPieces(int value) {
    Preconditions.checkArgument(value > 0, "expected a positive integer value");
    return convertToCopper(value);
  }

  protected abstract int convertToCopper(int value);

}
