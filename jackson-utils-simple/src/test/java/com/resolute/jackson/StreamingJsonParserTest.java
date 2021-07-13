package com.resolute.jackson;


import static com.resolute.jackson.StreamingJsonParser.attribute;
import static com.resolute.jackson.StreamingJsonParser.onObjectEnd;
import static com.resolute.jackson.StreamingJsonParser.onObjectStart;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import com.resolute.jackson.fixtures.DesigoEnumeratedText;
import com.resolute.jackson.fixtures.DesigoNode;

public class StreamingJsonParserTest {

  @Test
  public void test() throws IOException {

    // 1. Execute
    List<DesigoNode> nodes = Lists.newArrayList();

    DesigoNode.Builder builder = DesigoNode.builder();

    try (InputStream in =
        StreamingJsonParserTest.class.getResourceAsStream("system-browser-endpoint.json")) {

      StreamingJsonParser.create(in)
          .parseObject()
          .find(attribute("Nodes"))
          .parseArray()
          .parseObject(
              onObjectStart(() -> builder.clear()),
              onObjectEnd(() -> {
                if (builder.isValid()) {
                  nodes.add(builder.build());
                } else {
                  System.err.println("Invalid node: " + builder);
                }
              }))
          .find(
              attribute("Designation", parser -> {
                builder.withDesignation(parser.getText());
              }),
              attribute("Attributes"))
          .parseObject()
          .find(
              attribute("ManagedType", parser -> {
                builder.withManagedType(parser.getLongValue());
              }),
              attribute("ManagedTypeName", parser -> {
                builder.withManagedTypeName(parser.getText());
              }),
              attribute("SubTypeDescriptor", parser -> {
                builder.withSubtypeDescriptor(parser.getText());
              }))
          .execute();
    }

    // 2. Assert
    Collections.reverse(nodes);

    DesigoNode node;

    List<DesigoNode> bacnetPoints =
        nodes.stream().filter(n -> "BACnetPoint".equals(n.getManagedTypeName()))
            .collect(toList());

    assertThat(bacnetPoints).hasSizeGreaterThan(0);
    node = bacnetPoints.get(0);
    assertThat(node.getDesignation()).isEqualTo(
        "System1.ManagementView:ManagementView.FieldNetworks.BACnet.Hardware.CHW.Local_IO.CHW_SPT_RST_DEL");
    assertThat(node.getManagedType()).isEqualTo(80);
    assertThat(node.getManagedTypeName()).isEqualTo("BACnetPoint");

    List<DesigoNode> points = nodes.stream().filter(n -> "Point".equals(n.getManagedTypeName()))
        .collect(toList());

    assertThat(points).hasSizeGreaterThan(4);
    node = points.get(4);
    assertThat(node.getDesignation()).isEqualTo(
        "System1.ManagementView:ManagementView.SystemSettings.OrganizationModes.OccupancyStatus");
    assertThat(node.getManagedType()).isEqualTo(0);
    assertThat(node.getManagedTypeName()).isEqualTo("Point");
  }

  @Test
  public void test2() throws IOException {

    // 1. Execute
    List<DesigoEnumeratedText> range = Lists.newArrayList();

    DesigoEnumeratedText.Builder builder = DesigoEnumeratedText.builder();

    try (InputStream in =
        StreamingJsonParserTest.class.getResourceAsStream("commands-endpoint-binary.json")) {

      StreamingJsonParser.create(in)
          .parseArray()
          .parseObject()
          .find(attribute("Commands"))
          .parseArray()
          .parseObject()
          .find(attribute("Parameters"))
          .parseArray()
          .parseObject()
          .find(attribute("EnumerationTexts"))
          .parseArray()
          .parseObject(
              onObjectStart(() -> builder.clear()),
              onObjectEnd(() -> {
                if (builder.isValid()) {
                  range.add(builder.build());
                } else {
                  System.err.println("Invalid enumerated text: " + builder);
                }
              }))
          .find(
              attribute("Descriptor", parser -> {
                builder.withDescriptor(parser.getText());
              }),
              attribute("Value", parser -> {
                builder.withValue(parser.getLongValue());
              }))
          .execute();

    }

    // 2. Assert
    assertThat(range).hasSize(2);

    Map<Long, DesigoEnumeratedText> rangeMap = range.stream()
        .collect(toMap(DesigoEnumeratedText::getValue, identity()));
    DesigoEnumeratedText entry;

    assertThat(rangeMap.containsKey(0L));
    entry = rangeMap.get(0L);
    assertThat(entry.getValue()).isEqualTo(0L);
    assertThat(entry.getDescriptor()).isEqualTo("OFF");

    assertThat(rangeMap.containsKey(1L));
    entry = rangeMap.get(1L);
    assertThat(entry.getValue()).isEqualTo(1L);
    assertThat(entry.getDescriptor()).isEqualTo("ON");

  }
}
