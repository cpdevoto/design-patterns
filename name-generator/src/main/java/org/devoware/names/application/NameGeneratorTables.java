package org.devoware.names.application;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import org.devoware.table.Table;
import org.devoware.table.TableFileParseException;
import org.devoware.table.Tables;

public enum NameGeneratorTables {
  // @formatter:off
  DRAGONBORN_CLAN("Dragonborn, Clan"),
  DRAGONBORN_FEMALE("Dragonborn, Female"),
  DRAGONBORN_MALE("Dragonborn, Male"),
  DWARF_CLAN("Dwarf, Clan"),
  DWARF_FEMALE("Dwarf, Female"),
  DWARF_MALE("Dwarf, Male"),
  ELF_CHILD("Elf, Child"),
  ELF_FAMILY("Elf, Family"),
  ELF_FEMALE_ADULT("Elf, Female Adult"),
  ELF_MALE_ADULT("Elf, Male Adult"),
  GNOME_CLAN("Gnome, Clan"),
  GNOME_FEMALE("Gnome, Female"),
  GNOME_MALE("Gnome, Male"),
  HALF_ORC_FEMALE("Half-Orc, Female"),
  HALF_ORC_MALE("Half-Orc, Male"),
  HALFLING_FAMILY("Halfling, Family"),
  HALFLING_FEMALE("Halfling, Female"),
  HALFLING_MALE("Halfling, Male"),
  HUMAN_ARABIC_FEMALE("Arabic, Female"),
  HUMAN_ARABIC_MALE("Arabic, Male"),
  HUMAN_CELTIC_FEMALE("Celtic, Female"),
  HUMAN_CELTIC_MALE("Celtic, Male"),
  HUMAN_CHINESE_FEMALE("Chinese, Female"),
  HUMAN_CHINESE_MALE("Chinese, Male"),
  HUMAN_CHULTAN_FEMALE("Chultan, Female"),
  HUMAN_CHULTAN_MALE("Chultan, Male"),
  HUMAN_CHULTAN_DYNASTIC("Chultan, Dynastic"),
  HUMAN_EGYPTIAN_FEMALE("Egyptian, Female"),
  HUMAN_EGYPTIAN_MALE("Egyptian, Male"),
  HUMAN_ENGLISH_FEMALE("English, Female"),
  HUMAN_ENGLISH_MALE("English, Male"),
  HUMAN_FRENCH_FEMALE("French, Female"),
  HUMAN_FRENCH_MALE("French, Male"),
  HUMAN_GERMAN_FEMALE("German, Female"),
  HUMAN_GERMAN_MALE("German, Male"),
  HUMAN_GREEK_FEMALE("Greek, Female"),
  HUMAN_GREEK_MALE("Greek, Male"),
  HUMAN_INDIAN_FEMALE("Indian, Female"),
  HUMAN_INDIAN_MALE("Indian, Male"),
  HUMAN_JAPANESE_FEMALE("Japanese, Female"),
  HUMAN_JAPANESE_MALE("Japanese, Male"),
  HUMAN_MESOAMERICAN_FEMALE("Mesoamerican, Female"),
  HUMAN_MESOAMERICAN_MALE("Mesoamerican, Male"),
  HUMAN_NIGER_CONGO_FEMALE("Niger–Congo, Female"),
  HUMAN_NIGER_CONGO_MALE("Niger–Congo, Male"),
  HUMAN_NORSE_FEMALE("Norse, Female"),
  HUMAN_NORSE_MALE("Norse, Male"),
  HUMAN_POLYNESIAN_FEMALE("Polynesian, Female"),
  HUMAN_POLYNESIAN_MALE("Polynesian, Male"),
  HUMAN_ROMAN_FEMALE("Roman, Female"),
  HUMAN_ROMAN_MALE("Roman, Male"),
  HUMAN_SLAVIC_FEMALE("Slavic, Female"),
  HUMAN_SLAVIC_MALE("Slavic, Male"),
  HUMAN_SPANISH_FEMALE("Spanish, Female"),
  HUMAN_SPANISH_MALE("Spanish, Male"),
  TIEFLING_FEMALE("Tiefling, Female"),
  TIEFLING_MALE("Tiefling, Male"),
  TIEFLING_VIRTUE("Tiefling, Virtue");
  // @formatter:on

  private static final Tables TABLES;

  private final String resource;

  static {
    try {
      TABLES = Tables.loadFromClasspath("org.devoware.names.application");
    } catch (IOException e) {
      throw new TableFileParseException(e);
    }
  }

  public static Table get(NameGeneratorTables table) {
    requireNonNull(table, "table cannot be null");
    return TABLES.get(table.resource);
  }

  private NameGeneratorTables(String resource) {
    this.resource = resource;
  }

}
