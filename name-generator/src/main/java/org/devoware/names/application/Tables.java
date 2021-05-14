package org.devoware.names.application;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.devoware.table.Table;
import org.devoware.table.TableFileParseException;
import org.devoware.table.TableFileParser;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum Tables {
  // @formatter:off
  DRAGONBORN_CLAN("dragonborn-clan.tbl"),
  DRAGONBORN_FEMALE("dragonborn-female.tbl"),
  DRAGONBORN_MALE("dragonborn-male.tbl"),
  DWARF_CLAN("dwarf-clan.tbl"),
  DWARF_FEMALE("dwarf-female.tbl"),
  DWARF_MALE("dwarf-male.tbl"),
  ELF_CHILD("elf-child.tbl"),
  ELF_FAMILY("elf-family.tbl"),
  ELF_FEMALE_ADULT("elf-female-adult.tbl"),
  ELF_MALE_ADULT("elf-male-adult.tbl"),
  GNOME_CLAN("gnome-clan.tbl"),
  GNOME_FEMALE("gnome-female.tbl"),
  GNOME_MALE("gnome-male.tbl"),
  HALF_ORC_FEMALE("half-orc-female.tbl"),
  HALF_ORC_MALE("half-orc-male.tbl"),
  HALFLING_FAMILY("halfling-family.tbl"),
  HALFLING_FEMALE("halfling-female.tbl"),
  HALFLING_MALE("halfling-male.tbl"),
  HUMAN_ARABIC_FEMALE("human-arabic-female.tbl"),
  HUMAN_ARABIC_MALE("human-arabic-male.tbl"),
  HUMAN_CELTIC_FEMALE("human-celtic-female.tbl"),
  HUMAN_CELTIC_MALE("human-celtic-male.tbl"),
  HUMAN_CHINESE_FEMALE("human-chinese-female.tbl"),
  HUMAN_CHINESE_MALE("human-chinese-male.tbl"),
  HUMAN_EGYPTIAN_FEMALE("human-egyptian-female.tbl"),
  HUMAN_EGYPTIAN_MALE("human-egyptian-male.tbl"),
  HUMAN_ENGLISH_FEMALE("human-english-female.tbl"),
  HUMAN_ENGLISH_MALE("human-english-male.tbl"),
  HUMAN_FRENCH_FEMALE("human-french-female.tbl"),
  HUMAN_FRENCH_MALE("human-french-male.tbl"),
  HUMAN_GERMAN_FEMALE("human-german-female.tbl"),
  HUMAN_GERMAN_MALE("human-german-male.tbl"),
  HUMAN_GREEK_FEMALE("human-greek-female.tbl"),
  HUMAN_GREEK_MALE("human-greek-male.tbl"),
  HUMAN_INDIAN_FEMALE("human-indian-female.tbl"),
  HUMAN_INDIAN_MALE("human-indian-male.tbl"),
  HUMAN_JAPANESE_FEMALE("human-japanese-female.tbl"),
  HUMAN_JAPANESE_MALE("human-japanese-male.tbl"),
  HUMAN_MESOAMERICAN_FEMALE("human-mesoamerican-female.tbl"),
  HUMAN_MESOAMERICAN_MALE("human-mesoamerican-male.tbl"),
  HUMAN_NIGER_CONGO_FEMALE("human-niger-congo-female.tbl"),
  HUMAN_NIGER_CONGO_MALE("human-niger-congo-male.tbl"),
  HUMAN_NORSE_FEMALE("human-norse-female.tbl"),
  HUMAN_NORSE_MALE("human-norse-male.tbl"),
  HUMAN_POLYNESIAN_FEMALE("human-polynesian-female.tbl"),
  HUMAN_POLYNESIAN_MALE("human-polynesian-male.tbl"),
  HUMAN_ROMAN_FEMALE("human-roman-female.tbl"),
  HUMAN_ROMAN_MALE("human-roman-male.tbl"),
  HUMAN_SLAVIC_FEMALE("human-slavic-female.tbl"),
  HUMAN_SLAVIC_MALE("human-slavic-male.tbl"),
  HUMAN_SPANISH_FEMALE("human-spanish-female.tbl"),
  HUMAN_SPANISH_MALE("human-spanish-male.tbl"),
  TIEFLING_FEMALE("tiefling-female.tbl"),
  TIEFLING_MALE("tiefling-male.tbl"),
  TIEFLING_VIRTUE("tiefling-virtue.tbl");
  // @formatter:on

  private static final Map<Tables, Table> TABLE_MAP;

  private final String resource;

  static {
    Map<Tables, Table> tableMap = Maps.newHashMap();
    for (Tables t : Tables.values()) {
      try (InputStream in = Tables.class.getResourceAsStream(t.resource)) {
        Table table = TableFileParser.parse(in);
        tableMap.put(t, table);
      } catch (IOException e) {
        throw new TableFileParseException(e);
      }
    }
    TABLE_MAP = ImmutableMap.copyOf(tableMap);
  }

  public static Table get(Tables table) {
    requireNonNull(table, "table cannot be null");
    return TABLE_MAP.get(table);
  }

  private Tables(String resource) {
    this.resource = resource;
  }

}
