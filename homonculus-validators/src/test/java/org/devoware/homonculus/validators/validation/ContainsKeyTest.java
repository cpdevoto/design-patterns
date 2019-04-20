package org.devoware.homonculus.validators.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.devoware.homonculus.config.validation.Validators;
import org.devoware.homonculus.validators.validation.ContainsKeys;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ContainsKeyTest {

  private Validator validator;

  @Before
  public void setup () {
     validator = Validators.newValidator();
  }

  @Test
  public void test_invalid_map_with_one_required_key() {
    Map<String, String> processPaths = ImmutableMap.of("claimUpdate", "claim-update.sh");
    DdeService service = new DdeService(processPaths);
    Set<ConstraintViolation<DdeService>> violations = validator.validate(service);
    assertThat(violations.size(), equalTo(1));
  }
  
  @Test
  public void test_invalid_map_with_two_required_keys() {
    Map<String, String> processPaths = ImmutableMap.of("claimUpdate", "claim-update.sh");
    DdeService2 service = new DdeService2(processPaths);
    Set<ConstraintViolation<DdeService2>> violations = validator.validate(service);
    assertThat(violations.size(), equalTo(1));
  }

  @Test
  public void test_valid_map_with_one_required_key() {
    Map<String, String> processPaths = ImmutableMap.of("operator", "operator.sh", "claimUpdate", "claim-update.sh");
    DdeService service = new DdeService(processPaths);
    Set<ConstraintViolation<DdeService>> violations = validator.validate(service);
    assertThat(violations.size(), equalTo(0));
  }
  
  @Test
  public void test_valid_map_with_two_required_keys() {
    Map<String, String> processPaths = ImmutableMap.of("operator", "operator.sh", "claimUpdate", "claim-update.sh");
    DdeService2 service = new DdeService2(processPaths);
    Set<ConstraintViolation<DdeService2>> violations = validator.validate(service);
    assertThat(violations.size(), equalTo(1));
  }

  private static class DdeService {
    
    @ContainsKeys("operator")
    private final Map<String, String> processPaths;
    
    private DdeService (Map<String, String> processPaths) {
      this.processPaths = ImmutableMap.copyOf(processPaths);
    }
  }

  private static class DdeService2 {
    
    @ContainsKeys({"operator", "claimUpdates"})
    private final Map<String, String> processPaths;
    
    private DdeService2 (Map<String, String> processPaths) {
      this.processPaths = ImmutableMap.copyOf(processPaths);
    }
  }
}
