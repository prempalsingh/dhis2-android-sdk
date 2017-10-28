package org.hisp.dhis.rules.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleDataValueTests {

    @Mock
    private RuleEvent ruleEvent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createShouldThrowOnNullDate() {
        try {
            RuleDataValue.create(null, "test_program_stage_uid", "test_field", "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullEvent() {
        try {
            RuleDataValue.create(new Date(), null, "test_field", "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullDataElement() {
        try {
            RuleDataValue.create(new Date(), "test_program_stage_uid", null, "test_value");
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullValue() {
        try {
            RuleDataValue.create(new Date(), "test_program_stage_uid", "test_dataelement", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldPropagateValuesCorrectly() {
        Date eventDate = new Date();
        RuleDataValue ruleDataValue = RuleDataValue.create(eventDate,
                "test_program_stage_uid", "test_dataelement", "test_value");

        assertThat(ruleDataValue.eventDate()).isEqualTo(eventDate);
        assertThat(ruleDataValue.programStage()).isEqualTo("test_program_stage_uid");
        assertThat(ruleDataValue.dataElement()).isEqualTo("test_dataelement");
        assertThat(ruleDataValue.value()).isEqualTo("test_value");
    }
}
