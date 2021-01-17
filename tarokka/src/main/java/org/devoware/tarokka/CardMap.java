package org.devoware.tarokka;

import static org.devoware.tarokka.Card.ABJURER;
import static org.devoware.tarokka.Card.ANARCHIST;
import static org.devoware.tarokka.Card.ARTIFACT;
import static org.devoware.tarokka.Card.AVENGER;
import static org.devoware.tarokka.Card.BEAST;
import static org.devoware.tarokka.Card.BEGGAR;
import static org.devoware.tarokka.Card.BERSERKER;
import static org.devoware.tarokka.Card.BISHOP;
import static org.devoware.tarokka.Card.BROKEN_ONE;
import static org.devoware.tarokka.Card.CHARLATAN;
import static org.devoware.tarokka.Card.CONJURER;
import static org.devoware.tarokka.Card.DARKLORD;
import static org.devoware.tarokka.Card.DICTATOR;
import static org.devoware.tarokka.Card.DIVINER;
import static org.devoware.tarokka.Card.DONJON;
import static org.devoware.tarokka.Card.DRUID;
import static org.devoware.tarokka.Card.ELEMENTALIST;
import static org.devoware.tarokka.Card.ENCHANTER;
import static org.devoware.tarokka.Card.EVOKER;
import static org.devoware.tarokka.Card.EXECUTIONER;
import static org.devoware.tarokka.Card.GHOST;
import static org.devoware.tarokka.Card.GUILD_MEMBER;
import static org.devoware.tarokka.Card.HEALER;
import static org.devoware.tarokka.Card.HOODED_ONE;
import static org.devoware.tarokka.Card.HORSEMAN;
import static org.devoware.tarokka.Card.ILLUSIONIST;
import static org.devoware.tarokka.Card.INNOCENT;
import static org.devoware.tarokka.Card.MARIONETTE;
import static org.devoware.tarokka.Card.MERCENARY;
import static org.devoware.tarokka.Card.MERCHANT;
import static org.devoware.tarokka.Card.MISER;
import static org.devoware.tarokka.Card.MISSIONARY;
import static org.devoware.tarokka.Card.MISTS;
import static org.devoware.tarokka.Card.MONK;
import static org.devoware.tarokka.Card.MYRMIDON;
import static org.devoware.tarokka.Card.NECROMANCER;
import static org.devoware.tarokka.Card.PALADIN;
import static org.devoware.tarokka.Card.PHILANTHROPIST;
import static org.devoware.tarokka.Card.PRIEST;
import static org.devoware.tarokka.Card.RAVEN;
import static org.devoware.tarokka.Card.ROGUE;
import static org.devoware.tarokka.Card.SEER;
import static org.devoware.tarokka.Card.SHEPHERD;
import static org.devoware.tarokka.Card.SOLDIER;
import static org.devoware.tarokka.Card.SWASHBUCKLER;
import static org.devoware.tarokka.Card.TAX_COLLECTOR;
import static org.devoware.tarokka.Card.TEMPTER;
import static org.devoware.tarokka.Card.THIEF;
import static org.devoware.tarokka.Card.TORTURER;
import static org.devoware.tarokka.Card.TRADER;
import static org.devoware.tarokka.Card.TRAITOR;
import static org.devoware.tarokka.Card.TRANSMUTER;
import static org.devoware.tarokka.Card.WARRIOR;
import static org.devoware.tarokka.Card.WIZARD;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class CardMap {
  // @formatter:off
  public static CardMap TREASURE_LOCATION = new CardMap.Builder()
      .name("Treasure Location")

      .entry(AVENGER, "The treasure lies in a dragon’s house, in hands once clean and now corrupted.")
      .entry(PALADIN, "I see a sleeping prince, a servant of light and the brother of darkness. The treasure lies with him.")
      .entry(SOLDIER, "Go to the mountains. Climb the white tower guarded by golden knights.")
      .entry(MERCENARY, "The thing you seek lies with the dead, under mountains of gold coins.")
      .entry(MYRMIDON, "Look for a den of wolves in the hills overlooking a mountain lake. The treasure belongs to Mother Night.")
      .entry(BERSERKER, "Find the Mad Dog’s crypt. The treasure lies within, beneath blackened bones.")
      .entry(HOODED_ONE, "I see a faceless god. He awaits you at the end of a long and winding road, deep in the mountains.")
      .entry(DICTATOR, "I see a throne fit for a king.")
      .entry(TORTURER, "There is a town where all is not well. There you will find a house of corruption, and within, a dark room full of still ghosts.")
      .entry(WARRIOR, "That which you seek lies in the womb of darkness, the devil’s lair: the one place to which he must return.")

      .entry(TRANSMUTER, "Go to a place of dizzying heights, where the stone itself is alive!")
      .entry(DIVINER, "Look to the one who sees all. The treasure is hidden in her camp.")
      .entry(ENCHANTER, "I see a kneeling woman—a rose of great beauty plucked too soon. The master of the marsh knows of whom I speak.")
      .entry(ABJURER, "I see a fallen house guarded by a great stone dragon. Look to the highest peak.")
      .entry(ELEMENTALIST, "The treasure is hidden in a small castle beneath a mountain, guarded by amber giants.")
      .entry(EVOKER, "Search for the crypt of a wizard ordinaire. His staff is the key.")
      .entry(ILLUSIONIST, "A man is not what he seems. He comes here in a carnival wagon. Therein lies what you seek.")
      .entry(NECROMANCER, "A woman hangs above a roaring fire. Find her, and you will find the treasure.")
      .entry(CONJURER, "I see a dead village, drowned by a river, ruled by one who has brought great evil into the world.")
      .entry(WIZARD, "Look for a wizard’s tower on a lake. Let the wizard’s name and servant guide you to that which you seek.")
      
      .entry(SWASHBUCKLER, "I see the skeleton of a deadly warrior, lying on a bed of stone flanked by gargoyles.")
      .entry(PHILANTHROPIST, "Look to a place where sickness and madness are bred. Where children once cried, the treasure lies still.")
      .entry(TRADER, "Look to the wizard of wines! In wood and sand the treasure hides.")
      .entry(MERCHANT, "Seek a cask that once contained the finest wine, of which not a drop remains.")
      .entry(GUILD_MEMBER, "I see a dark room full of bottles. It is the tomb of a guild member.")
      .entry(BEGGAR, "A wounded elf has what you seek. He will part with the treasure to see his dark dreams fulfilled.")
      .entry(THIEF, "What you seek lies at the crossroads of life and death, among the buried dead.")
      .entry(TAX_COLLECTOR, "The Vistani have what you seek. A missing child holds the key to the treasure’s release.")
      .entry(MISER, "Look for a fortress inside a fortress, in a place hidden behind fire.")
      .entry(ROGUE, "I see a nest of ravens. There you will find the prize.")

      .entry(MONK, "The treasure you seek is hidden behind the sun, in the house of a saint.")
      .entry(MISSIONARY, "I see a garden dusted with snow, watched over by a scarecrow with a sackcloth grin. Look not to the garden but to the guardian.")
      .entry(HEALER, "Look to the west. Find a pool blessed by the light of the white sun.")
      .entry(SHEPHERD, "Find the mother—she who gave birth to evil.")
      .entry(DRUID, "An evil tree grows atop a hill of graves where the ancient dead sleep. The ravens can help you find it. Look for the treasure there.")
      .entry(ANARCHIST, "I see walls of bones, a chandelier of bones, and a table of bones—all that remains of enemies long forgotten.")
      .entry(CHARLATAN, "I see a lonely mill on a precipice. The treasure lies within.")
      .entry(BISHOP, "What you seek lies in a pile of treasure, beyond a set of amber doors.")
      .entry(TRAITOR, "Look for a wealthy woman. A staunch ally of the devil, she keeps the treasure under lock and key, with the bones of an ancient enemy.")
      .entry(PRIEST, "You will find what you seek in the castle, amid the ruins of a place of supplication.")

      .build();
  // @formatter:on

  // @formatter:off
  public static CardMap STRAHDS_ENEMY = new CardMap.Builder()
      .name("Strahd's Enemy")

      .entry(ARTIFACT, "Look for an entertaining man with a monkey. This man is more than he seems.")
      .entry(BEAST, "A werewolf holds a secret hatred for your enemy. Use her hatred to your advantage.")
      .entry(BROKEN_ONE, "A. Your greatest ally will be a wizard. His mind is broken, but his spells are strong.\n"
          + "B. I see a man of faith whose sanity hangs by a thread. He has lost someone close to him.")
      .entry(DARKLORD, "Ah, the worst of all truths: You must face the evil of this land alone!")
      .entry(DONJON, "A. Search for a troubled young man surrounded by wealth and madness. His home is his prison.\n"
          + "B. Find a girl driven to insanity, locked in the heart of her dead father’s house. Curing her madness is key to your success.")
      .entry(SEER, "Look for a dusk elf living among the Vistani. He has suffered a great loss and is haunted by dark dreams. Help him, and he will help you in return.")
      .entry(GHOST, "A. I see a fallen paladin of a fallen order of knights. He lingers like a ghost in a dead dragon’s lair.\n"
          + "B. Stir the spirit of the clumsy knight whose crypt lies deep within the castle.")
      .entry(EXECUTIONER, "Seek out the brother of the devil’s bride. They call him “the lesser,” but he has a powerful soul.")
      .entry(HORSEMAN, "A. I see a dead man of noble birth, guarded by his widow. Return life to the dead man’s corpse, and he will be your staunch ally.\n"
          + "B. A man of death named Arrigal will forsake his dark lord to serve your cause. Beware! He has a rotten soul.")
      .entry(INNOCENT, "A. I see a young man with a kind heart. A mother’s boy! He is strong in body but weak of mind. Seek him out in the village of Barovia.\n"
          + "B. Evil’s bride is the one you seek!")
      .entry(MARIONETTE, "A. What horror is this? I see a man made by a man. Ageless and alone, it haunts the towers of the castle.\n"
          + "B. Look for a man of music, a man with two heads. He lives in a place of great hunger and sorrow.")
      .entry(MISTS, "A Vistana wanders this land alone, searching for her mentor. She does not stay in one place for long. Seek her out at Saint Markovia’s abbey, near the mists.")
      .entry(RAVEN, "Find the leader of the feathered ones who live among the vines. Though old, he has one more fight left in him.")
      .entry(TEMPTER, "A. I see a child—a Vistana. You must hurry, for her fate hangs in the balance. Find her at the lake!\n"
          + "B. I hear a wedding bell, or perhaps a death knell. It calls thee to a mountainside abbey, wherein you will find a woman who is more than the sum of her parts.")
  
       .build();
  // @formatter:on

  // @formatter:off
  public static CardMap STRAHDS_LOCATION_IN_CASTLE = new CardMap.Builder()
      .name("Strahd's Location in the Castle")

      .entry(ARTIFACT, "He lurks in the darkness where the morning light once shone—a sacred place.")
      .entry(BEAST, "The beast sits on his dark throne.")
      .entry(BROKEN_ONE, "He haunts the tomb of the man he envied above all.")
      .entry(DARKLORD, "He lurks in the depths of darkness, in the one place to which he must return.")
      .entry(DONJON, "He lurks in a hall of bones, in the dark pits of his castle.")
      .entry(SEER, "He waits for you in a place of wisdom, warmth, and despair. Great secrets are there.")
      .entry(GHOST, "Look to the father’s tomb.")
      .entry(EXECUTIONER, "I see a dark figure on a balcony, looking down upon this tortured land with a twisted smile.")
      .entry(HORSEMAN, "He lurks in the one place to which he must return—a place of death.")
      .entry(INNOCENT, "He dwells with the one whose blood sealed his doom, a brother of light snuffed out too soon.")
      .entry(MARIONETTE, "Look to great heights. Find the beating heart of the castle. He waits nearby.")
      .entry(MISTS, "The cards can’t see where the evil lurks. The mists obscure all!")
      .entry(RAVEN, "Look to the mother’s tomb.")
      .entry(TEMPTER, "I see a secret place—a vault of temptation hidden behind a woman of great beauty. The evil waits atop his tower of treasure.")
  
       .build();
  // @formatter:on

  private final String name;
  private final Map<Card, String> map;

  private CardMap(Builder builder) {
    this.name = builder.name;
    this.map = ImmutableMap.copyOf(builder.map);
  }

  public String getName() {
    return name;
  }

  public String get(Card card) {
    return map.get(card);
  }

  private static class Builder {
    private String name;
    private Map<Card, String> map = Maps.newLinkedHashMap();

    private Builder() {}

    private Builder name(String name) {
      this.name = name;
      return this;
    }

    private Builder entry(Card card, String value) {
      this.map.put(card, value);
      return this;
    }

    private CardMap build() {
      return new CardMap(this);
    }

  }

}
