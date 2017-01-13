package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lexer.Lexer;
import parser.Parser;

public class Main {

  public static void main(String[] args) throws IOException {
    try (InputStream in = new FileInputStream("input.txt")) {
      Lexer lex = new Lexer(in);
      Parser parse = new Parser(lex);
      parse.program();
      System.out.println();
    }
  }
  
}
