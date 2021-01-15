package com.resolute.utils.simple.pojo_generator;

import static com.resolute.utils.simple.pojo_generator.PojoGenerator.dataMember;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.resolute.utils.simple.IOUtils;

public class PojoGeneratorTest {

  @Test
  public void test() throws IOException {
    String expected = IOUtils.resourceToString("generated-pojo.txt", StandardCharsets.UTF_8,
        PojoGeneratorTest.class);

    // @formatter:off
    String source = PojoGenerator.forClass("User")
        .inPackage("com.resolutebi.orders")
        .jacksonAnnotations()
        .dataMembers(
            dataMember(dm -> dm.name("id")
                .dataType("int")
                .required()
            ),
            dataMember(dm -> dm.name("age")
                .dataType("int")
            ),
            dataMember(dm -> dm.name("name")
                .dataType("String")
                .required()
            ),
            dataMember(dm -> dm.name("startDate")
                .dataType("LocalDate")
            ),
            dataMember(dm -> dm.name("roles")
                .dataType("Set<Role>")
            ),
            dataMember(dm -> dm.name("addresses")
                .dataType("List<String>")
                .required())
        )
        .generate();
    // @formatter:on

    System.out.println(source);
    assertThat(source.trim()).isEqualTo(expected.trim());
  }
}
