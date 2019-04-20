package org.devoware.character;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;
import static org.devoware.utils.StringUtils.hr;
import static org.devoware.utils.StringUtils.padLeft;
import static org.devoware.utils.StringUtils.padRight;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Maps;

public class BuildPrinter {
  private static enum ValueType {
    DPR("DPR"), DAMAGE_ON_HIT("Damage on Hit");

    private String label;

    private ValueType(String label) {
      this.label = label;
    }

    @Override
    public String toString() {
      return label;
    }
  }

  private final StringBuilder buf = new StringBuilder();
  private final ValueType valueType;
  private final String name;
  private final Map<Integer, Double> values;


  public static String printDpr(CharacterBuild... builds) {
    checkArgument(builds.length > 0, "builds must have at least one element");
    List<String> outputs = Arrays.stream(builds)
        .map(build -> new BuildPrinter(ValueType.DPR, build.getName(), build.dprByLevel())
            .print())
        .collect(Collectors.toList());
    return mergeOutputs(outputs);
  }

  public static String printDamageOnHit(CharacterBuild... builds) {
    checkArgument(builds.length > 0, "builds must have at least one element");
    List<String> outputs = Arrays.stream(builds)
        .map(build -> new BuildPrinter(ValueType.DAMAGE_ON_HIT, build.getName(),
            build.damageOnHitByLevel())
                .print())
        .collect(Collectors.toList());
    return mergeOutputs(outputs);
  }

  private static String mergeOutputs(List<String> outputs) {
    if (outputs.size() == 1) {
      return outputs.get(0);
    }

    List<List<String>> outputLines = outputs.stream()
        .map(output -> Arrays.stream(output.split("\n")).collect(toList()))
        .collect(toList());

    int numLines = outputLines.get(0).size();

    Map<Integer, Integer> columnWidths = Maps.newHashMap();
    IntStream.range(0, outputs.size())
        .forEach(i -> {
          columnWidths.put(i, outputLines.get(i).stream()
              .mapToInt(String::length)
              .max().getAsInt());
        });

    StringBuilder buf = new StringBuilder();
    IntStream.range(0, numLines)
        .forEach(i -> {
          String line = IntStream.range(0, outputLines.size())
              .mapToObj(j -> {
                int columnWidth = columnWidths.get(j);
                String s = padRight(outputLines.get(j).get(i), columnWidth);
                s = (j == 0 ? s : "     " + s);
                return s;
              })
              .collect(Collectors.joining());
          buf.append(line).append("\n");
        });

    return buf.toString();
  }


  private BuildPrinter(ValueType valueType, String name, Map<Integer, Double> values) {
    this.valueType = valueType;
    this.name = name;
    this.values = values;
  }

  private String print() {
    String header = name + " - " + valueType;
    println(hr('-', header.length()));
    println(header);
    println(hr('-', header.length()));
    println();

    int column1Width = values.keySet().stream()
        .map(Object::toString)
        .mapToInt(String::length)
        .max().getAsInt();

    int column2Width = values.values().stream()
        .map(v -> format("%,.2f", v))
        .mapToInt(String::length)
        .max().getAsInt();

    values.entrySet().stream()
        .forEach(entry -> {
          String level = padLeft(valueOf(entry.getKey()), column1Width);
          String value = padLeft(format("%,.2f", entry.getValue()), column2Width);
          print("Level ").print(level).print(":    ").println(value);
        });

    return buf.toString();
  }

  private BuildPrinter print(String s) {
    buf.append(s);
    return this;
  }

  private BuildPrinter println(String s) {
    buf.append(s).append("\n");
    return this;
  }

  private BuildPrinter println() {
    buf.append("\n");
    return this;
  }

}
