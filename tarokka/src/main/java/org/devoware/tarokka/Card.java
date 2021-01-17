package org.devoware.tarokka;

import static org.devoware.tarokka.Suite.COINS;
import static org.devoware.tarokka.Suite.GLYPHS;
import static org.devoware.tarokka.Suite.HIGH;
import static org.devoware.tarokka.Suite.STARS;
import static org.devoware.tarokka.Suite.SWORDS;

import java.util.Optional;

public enum Card {
  // @formatter:off
  ARTIFACT("Artifact",
      "The importance of some physical object that must be obtained, protected, or destroyed at all costs",
      HIGH),
  BEAST("Beast",
      "Great rage or passion; something bestial or malevolent hiding in plain sight or lurking just below the surface",
      HIGH),
  BROKEN_ONE("Broken One",
      "Defeat, failure, and despair; the loss of something or someone important, without which one feels incomplete",
      HIGH),
  DARKLORD("Darklord",
      "A single, powerful individual of an evil nature, one whose goals have enormous and far-reaching consequences",
      HIGH),
  DONJON("Donjon",
      "Isolation and imprisonment; being so conservative in thinking as to be a prisoner of one’s own beliefs",
      HIGH),
  GHOST("Ghost",
      "The looming past; the return of an old enemy or the discovery of a secret buried long ago",
      HIGH),
  EXECUTIONER("Executioner",
      "The imminent death of one rightly or wrongly convicted of a crime; false accusations and unjust prosecution",
      HIGH),
  HORSEMAN("Horseman",
      "Death; disaster in the form of the loss of wealth or property, a horrible defeat, or the end of a bloodline",
      HIGH),
  INNOCENT("Innocent",
      "A being of great importance whose life is in danger (who might be helpless or simply unaware of the peril)",
      HIGH),
  MARIONETTE("Marionette",
      "The presence of a spy or a minion of some greater power; an encounter with a puppet or an underling",
      HIGH),
  MISTS("Mists",
      "Something unexpected or mysterious that can’t be avoided; a great quest or journey that will try one’s spirit",
      HIGH),
  RAVEN("Raven",
      "A hidden source of information; a fortunate turn of events; a secret potential for good",
      HIGH),
  SEER("Seer",
      "Inspiration and keen intellect; a future event, the outcome of which will hinge on a clever mind",
      HIGH),
  TEMPTER("Tempter",
      "One who has been compromised or led astray by temptation or foolishness; one who tempts others for evil ends",
      HIGH),
  WARRIOR("Warrior",
      "Master of swords",
      "Strength and force personified; violence; those who use force to accomplish their goals",
      SWORDS),
  AVENGER("Avenger",
      "One of swords",
      "Justice and revenge for great wrongs; those on a quest to rid the world of great evil",
      SWORDS),
  PALADIN("Paladin",
      "Two of swords",
      "Just and noble warriors; those who live by a code of honor and integrity",
      SWORDS),
  SOLDIER("Soldier",
      "Three of swords",
      "War and sacrifice; the stamina to endure great hardship",
      SWORDS),
  MERCENARY("Mercenary",
      "Four of swords",
      "Inner strength and fortitude; those who fight for power or wealth",
      SWORDS),
  MYRMIDON("Myrmidon",
      "Five of swords",
      "Great heroes; a sudden reversal of fate; the triumph of the underdog over a mighty enemy",
      SWORDS),
  BERSERKER("Berserker",
      "Six of swords",
      "The brutal and barbaric side of warfare; bloodlust; those with a bestial nature",
      SWORDS),
  HOODED_ONE("Hooded One",
      "Seven of swords",
      "Bigotry, intolerance, and xenophobia; a mysterious presence or newcomer",
      SWORDS),
  DICTATOR("Dictator",
      "Eight of swords",
      "All that is wrong with government and leadership; those who rule through fear and violence",
      SWORDS),
  TORTURER("Torturer",
      "Nine of swords",
      "The coming of suffering or merciless cruelty; one who is irredeemably evil or sadistic",
      SWORDS),
  WIZARD("Wizard",
      "Master of stars",
      "Mystery and riddles; the unknown; those who crave magical power and great knowledge",
      STARS),
  TRANSMUTER("Transmuter",
      "One of stars",
      "A new discovery; the coming of unexpected things; unforeseen consequences and chaos",
      STARS),
  DIVINER("Diviner",
      "Two of stars",
      "The pursuit of knowledge tempered by wisdom; truth and honesty; sages and prophecy",
      STARS),
  ENCHANTER("Enchanter",
      "Three of stars",
      "Inner turmoil that comes from confusion, fear of failure, or false information",
      STARS),
  ABJURER("Abjurer",
      "Four of stars",
      "Those guided by logic and reasoning; warns of an overlooked clue or piece of information",
      STARS),
  ELEMENTALIST("Elementalist",
      "Five of stars",
      "The triumph of nature over civilization; natural disasters and bountiful harvests",
      STARS),
  EVOKER("Evoker",
      "Six of stars",
      "Magical or supernatural power that can’t be controlled; magic for destructive ends",
      STARS),
  ILLUSIONIST("Illusionist",
      "Seven of stars",
      "Lies and deceit; grand conspiracies; secret societies; the presence of a dupe or a saboteur",
      STARS),
  NECROMANCER("Necromancer",
      "Eight of stars",
      "Unnatural events and unhealthy obsessions; those who follow a destructive path",
      STARS),
  CONJURER("Conjurer",
      "Nine of stars",
      "The coming of an unexpected supernatural threat; those who think of themselves as gods",
      STARS),
  ROGUE("Rogue",
      "Master of coins",
      "Anyone for whom money is important; those who believe money is the key to their success",
      COINS),
  SWASHBUCKLER("Swashbuckler",
      "One of coins",
      "Those who like money yet give it up freely; likable rogues and rapscallions",
      COINS),
  PHILANTHROPIST("Philanthropist",
      "Two of coins",
      "Charity and giving on a grand scale; those who use wealth to fight evil and sickness",
      COINS),
  TRADER("Trader",
      "Three of coins",
      "Commerce; smuggling and black markets; fair and equitable trades",
      COINS),
  MERCHANT("Merchant",
      "Four of coins",
      "A rare commodity or business opportunity; deceitful or dangerous business transactions",
      COINS),
  GUILD_MEMBER("Guild Member",
      "Five of coins",
      "Like-minded individuals joined together in a common goal; pride in one’s work",
      COINS),
  BEGGAR("Beggar",
      "Six of coins",
      "Sudden change in economic status or fortune",
      COINS),
  THIEF("Thief",
      "Seven of coins",
      "Those who steal or burgle; a loss of property, beauty, innocence, friendship, or reputation",
      COINS),
  TAX_COLLECTOR("Tax Collector",
      "Eight of coins",
      "Corruption; honesty in an otherwise corrupt government or organization",
      COINS),
  MISER("Miser",
      "Nine of coins",
      "Hoarded wealth; those who are irreversibly unhappy or who think money is meaningless",
      COINS),
  PRIEST("Priest",
      "Master of glyphs",
      "Enlightenment; those who follow a deity, a system of values, or a higher purpose",
      GLYPHS),
  MONK("Monk",
      "One of glyphs",
      "Serenity; inner strength and self-reliance; supreme confidence bereft of arrogance",
      GLYPHS),
  MISSIONARY("Missionary",
      "Two of glyphs",
      "Those who spread wisdom and faith to others; warnings of the spread of fear and ignorance",
      GLYPHS),
  HEALER("Healer",
      "Three of glyphs",
      "Healing; a contagious illness, disease, or curse; those who practice the healing arts",
      GLYPHS),
  SHEPHERD("Shepherd",
      "Four of glyphs",
      "Those who protect others; one who bears a burden far too great to be shouldered alone",
      GLYPHS),
  DRUID("Druid",
      "Five of glyphs",
      "The ambivalence and cruelty of nature and those who feel drawn to it; inner turmoil",
      GLYPHS),
  ANARCHIST("Anarchist",
      "Six of glyphs",
      "A fundamental change brought on by one whose beliefs are being put to the test",
      GLYPHS),
  CHARLATAN("Charlatan",
      "Seven of glyphs",
      "Liars; those who profess to believe one thing but actually believe another",
      GLYPHS),
  BISHOP("Bishop",
      "Eight of glyphs",
      "Strict adherence to a code or a belief; those who plot, plan, and scheme",
      GLYPHS),
  TRAITOR("Traitor",
      "Nine of glyphs",
      "Betrayal by someone close and trusted; a weakening or loss of faith",
      GLYPHS)
  ;
  // @formatter:on

  private final String name;
  private final String alternateName;
  private final String description;
  private final Suite suite;

  private Card(String name, String description, Suite suite) {
    this(name, null, description, suite);
  }

  private Card(String name, String alternateName, String description, Suite suite) {
    this.name = name;
    this.alternateName = alternateName;
    this.description = description;
    this.suite = suite;
  }

  public String getName() {
    return name;
  }

  public Optional<String> getAlternateName() {
    return Optional.ofNullable(alternateName);
  }

  public String getDescription() {
    return description;
  }

  public Suite getSuite() {
    return suite;
  }

  @Override
  public String toString() {
    return name + (alternateName != null ? String.format(" (%s)", alternateName) : "");
  }


}
