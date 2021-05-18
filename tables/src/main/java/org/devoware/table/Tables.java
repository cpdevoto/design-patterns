package org.devoware.table;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Tables {
  private static final Pattern ROLL_X_PATTERN =
      Pattern.compile("^Roll ([1-9][0-9]*) times? on table '(.+)'$");

  private final Map<String, Table> tables;

  public static Tables loadFromClasspath(String packageName) {
    requireNonNull(packageName, "packageName cannot be null");
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forPackage(packageName))
        .setScanners(new ResourcesScanner())
        .filterInputsBy(new FilterBuilder().includePackage(packageName)));
    Set<String> tableFiles =
        reflections.getResources(Pattern.compile(".*\\.tbl"));
    Map<String, Table> temp = Maps.newHashMap();
    for (String tableFile : tableFiles) {
      try (InputStream in = Tables.class.getClassLoader().getResourceAsStream(tableFile)) {
        Table table = TableFileParser.parse(in);
        temp.put(table.getName(), table);
      } catch (IOException e) {
        throw new TableLoadException(e);
      }
    }
    return new Tables(temp);
  }

  public static Tables loadFromDirectory(String directoryName) {
    requireNonNull(directoryName, "directoryName cannot be null");
    return loadFromDirectory(new File(directoryName));
  }

  public static Tables loadFromDirectory(File directory) {
    requireNonNull(directory, "directory cannot be null");
    checkArgument(directory.isDirectory(), "expected a directory");
    Map<String, Table> temp = Maps.newHashMap();
    for (File f : directory.listFiles()) {
      try {
        Table table = TableFileParser.parse(f);
        temp.put(table.getName(), table);
      } catch (TableFileParseException e) {
        if (e.getCause() != null) {
          throw new TableLoadException(e.getCause());
        } else {
          throw new TableLoadException(e);
        }
      }
    }
    return new Tables(temp);
  }

  private Tables(Map<String, Table> tables) {
    checkArgument(!tables.isEmpty(), "expected at least one table");
    this.tables = ImmutableMap.copyOf(tables);
  }

  public Table get(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    return tables.get(tableName);
  }

  public int size() {
    return tables.size();
  }

  public boolean contains(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    return tables.containsKey(tableName);
  }

  public String roll(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    checkArgument(tables.containsKey(tableName), "invalid tableName '" + tableName + "'");
    Table table = tables.get(tableName);
    String result = table.roll();
    Matcher m = ROLL_X_PATTERN.matcher(result);
    if (!m.find()) {
      return result;
    }
    int numRolls = Integer.parseInt(m.group(1));
    String tableName2 = m.group(2);
    checkArgument(tables.containsKey(tableName2),
        "invalid tableName '" + tableName2 + "' referenced by '" + tableName + "'");
    List<String> results = Lists.newArrayList();
    for (int i = 0; i < numRolls; i++) {
      String s = roll(tableName2);
      results.add(s);
    }
    return results.stream()
        .collect(Collectors.joining("\n"));
  }

}
