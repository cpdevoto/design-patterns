package com.resolutebi.baseline.expr;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

class TokenList {
  private int index = 0;
  private final BiMap<Integer, Token> tokens = HashBiMap.create();
  
  static TokenList create () {
    return new TokenList();
  }

  private TokenList() {}
  
  void add(Token token) {
    tokens.put(index++, token);
  }
  
  Token get(int index) {
    return tokens.get(index);
  }
  
  Token next(Token token) {
    Integer index = tokens.inverse().get(token);
    if (index == tokens.size() - 1) {
      return null;
    }
    return tokens.get(index + 1);
  }

  Token previous(Token token) {
    Integer index = tokens.inverse().get(token);
    if (index == 0) {
      return null;
    }
    return tokens.get(index - 1);
  }
}
