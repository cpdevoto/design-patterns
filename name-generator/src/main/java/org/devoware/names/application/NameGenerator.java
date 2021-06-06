package org.devoware.names.application;

public enum NameGenerator {
  BAROVIAN_MALE("Barovian, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.BAROVIAN_MALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.BAROVIAN_FAMILY).roll();
      int pos = secondName.indexOf('/');
      if (pos != -1) {
        secondName = secondName.substring(0, pos).trim();
      }
      return String.format("%s %s", firstName, secondName);
    }
  },
  BAROVIAN_FEMALE("Barovian, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.BAROVIAN_FEMALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.BAROVIAN_FAMILY).roll();
      int pos = secondName.indexOf('/');
      if (pos != -1) {
        secondName = secondName.substring(pos + 1).trim();
      }
      return String.format("%s %s", firstName, secondName);
    }
  },
  DRAGONBORN_MALE("Dragonborn, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.DRAGONBORN_MALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.DRAGONBORN_CLAN).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  DRAGONBORN_FEMALE("Dragonborn, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.DRAGONBORN_FEMALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.DRAGONBORN_CLAN).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  DWARF_FEMALE("Dwarf, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.DWARF_FEMALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.DWARF_CLAN).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  DWARF_MALE("Dwarf, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.DWARF_MALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.DWARF_CLAN).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  ELF_CHILD("Elf, Child") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.ELF_CHILD).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.ELF_FAMILY).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  ELF_FEMALE_ADULT("Elf, Female Adult") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.ELF_FEMALE_ADULT).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.ELF_FAMILY).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  ELF_MALE_ADULT("Elf, Male Adult") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.ELF_MALE_ADULT).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.ELF_FAMILY).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  GNOME_FEMALE("Gnome, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.GNOME_FEMALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.GNOME_CLAN).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  GNOME_MALE("Gnome, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.GNOME_MALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.GNOME_CLAN).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  HALFLING_FEMALE("Halfling, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HALFLING_FEMALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.HALFLING_FAMILY).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  HALFLING_MALE("Halfling, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HALFLING_MALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.HALFLING_FAMILY).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  HALF_ORC_FEMALE("Half-Orc, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HALF_ORC_FEMALE).roll();
      return firstName;
    }
  },
  HALF_ORC_MALE("Half-Orc, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HALF_ORC_MALE).roll();
      return firstName;
    }
  },
  TIEFLING_FEMALE("Tiefling, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.TIEFLING_FEMALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.TIEFLING_VIRTUE).roll();
      return String.format("%s \"%s\"", firstName, secondName);
    }
  },
  TIEFLING_MALE("Tiefling, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.TIEFLING_MALE).roll();
      String secondName = NameGeneratorTables.get(NameGeneratorTables.TIEFLING_VIRTUE).roll();
      return String.format("%s \"%s\"", firstName, secondName);
    }
  },
  HUMAN_ARABIC_FEMALE("Human - Arabic, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_ARABIC_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_ARABIC_MALE("Human - Arabic, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_ARABIC_MALE).roll();
      return firstName;
    }
  },
  HUMAN_CELTIC_FEMALE("Human - Celtic, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_CELTIC_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_CELTIC_MALE("Human - Celtic, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_CELTIC_MALE).roll();
      return firstName;
    }
  },
  HUMAN_CHINESE_FEMALE("Human - Chinese, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_CHINESE_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_CHINESE_MALE("Human - Chinese, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_CHINESE_MALE).roll();
      return firstName;
    }
  },
  HUMAN_CHULTAN_FEMALE("Human - Chultan, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_CHULTAN_FEMALE).roll();
      String secondName =
          NameGeneratorTables.get(NameGeneratorTables.HUMAN_CHULTAN_DYNASTIC).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  HUMAN_CHULTAN_MALE("Human - Chultan, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_CHULTAN_MALE).roll();
      String secondName =
          NameGeneratorTables.get(NameGeneratorTables.HUMAN_CHULTAN_DYNASTIC).roll();
      return String.format("%s %s", firstName, secondName);
    }
  },
  HUMAN_EGYPTIAN_FEMALE("Human - Egyptian, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_EGYPTIAN_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_EGYPTIAN_MALE("Human - Egyptian, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_EGYPTIAN_MALE).roll();
      return firstName;
    }
  },
  HUMAN_ENGLISH_FEMALE("Human - English, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_ENGLISH_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_ENGLISH_MALE("Human - English, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_ENGLISH_MALE).roll();
      return firstName;
    }
  },
  HUMAN_FRENCH_FEMALE("Human - French, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_FRENCH_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_FRENCH_MALE("Human - French, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_FRENCH_MALE).roll();
      return firstName;
    }
  },
  HUMAN_GERMAN_FEMALE("Human - German, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_GERMAN_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_GERMAN_MALE("Human - German, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_GERMAN_MALE).roll();
      return firstName;
    }
  },
  HUMAN_GREEK_FEMALE("Human - Greek, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_GREEK_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_GREEK_MALE("Human - Greek, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_GREEK_MALE).roll();
      return firstName;
    }
  },
  HUMAN_INDIAN_FEMALE("Human - Indian, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_INDIAN_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_INDIAN_MALE("Human - Indian, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_INDIAN_MALE).roll();
      return firstName;
    }
  },
  HUMAN_JAPANESE_FEMALE("Human - Japanese, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_JAPANESE_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_JAPANESE_MALE("Human - Japanese, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_JAPANESE_MALE).roll();
      return firstName;
    }
  },
  HUMAN_MESOAMERICAN_FEMALE("Human - Mesoamerican, Female") {
    @Override
    protected String doGenerate() {
      String firstName =
          NameGeneratorTables.get(NameGeneratorTables.HUMAN_MESOAMERICAN_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_MESOAMERICAN_MALE("Human - Mesoamerican, Male") {
    @Override
    protected String doGenerate() {
      String firstName =
          NameGeneratorTables.get(NameGeneratorTables.HUMAN_MESOAMERICAN_MALE).roll();
      return firstName;
    }
  },
  HUMAN_NIGER_CONGO_FEMALE("Human - Niger-Congo, Female") {
    @Override
    protected String doGenerate() {
      String firstName =
          NameGeneratorTables.get(NameGeneratorTables.HUMAN_NIGER_CONGO_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_NIGER_CONGO_MALE("Human - Niger-Congo, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_NIGER_CONGO_MALE).roll();
      return firstName;
    }
  },
  HUMAN_NORSE_FEMALE("Human - Norse, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_NORSE_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_NORSE_MALE("Human - Norse, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_NORSE_MALE).roll();
      return firstName;
    }
  },
  HUMAN_POLYNESIAN_FEMALE("Human - Polynesian, Female") {
    @Override
    protected String doGenerate() {
      String firstName =
          NameGeneratorTables.get(NameGeneratorTables.HUMAN_POLYNESIAN_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_POLYNESIAN_MALE("Human - Polynesian, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_POLYNESIAN_MALE).roll();
      return firstName;
    }
  },
  HUMAN_ROMAN_FEMALE("Human - Roman, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_ROMAN_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_ROMAN_MALE("Human - Roman, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_ROMAN_MALE).roll();
      return firstName;
    }
  },
  HUMAN_SLAVIC_FEMALE("Human - Slavic, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_SLAVIC_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_SLAVIC_MALE("Human - Slavic, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_SLAVIC_MALE).roll();
      return firstName;
    }
  },
  HUMAN_SPANISH_FEMALE("Human - Spanish, Female") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_SPANISH_FEMALE).roll();
      return firstName;
    }
  },
  HUMAN_SPANISH_MALE("Human - Spanish, Male") {
    @Override
    protected String doGenerate() {
      String firstName = NameGeneratorTables.get(NameGeneratorTables.HUMAN_SPANISH_MALE).roll();
      return firstName;
    }
  };


  private final String title;

  private NameGenerator(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public String generateName() {
    return doGenerate();
  }

  @Override
  public String toString() {
    return title;
  }

  protected abstract String doGenerate();

}
