package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class RuleEnrollmentTests {

    @Test
    public void createShouldThrowOnNullEnrollment() {
        try {
            RuleEnrollment.create(null, new Date(), new Date(),
                    RuleEnrollment.Status.ACTIVE, new ArrayList<RuleAttributeValue>());
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullIncidentDate() {
        try {
            RuleEnrollment.create("test_enrollment", null, new Date(),
                    RuleEnrollment.Status.ACTIVE, new ArrayList<RuleAttributeValue>());
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullEnrollmentDate() {
        try {
            RuleEnrollment.create("test_enrollment", new Date(), null,
                    RuleEnrollment.Status.ACTIVE, new ArrayList<RuleAttributeValue>());
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullStatus() {
        try {
            RuleEnrollment.create("test_enrollment", new Date(), new Date(),
                    null, new ArrayList<RuleAttributeValue>());
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }


    @Test
    public void createShouldThrowOnNullValueList() {
        try {
            RuleEnrollment.create("test_enrollment", new Date(), new Date(),
                    RuleEnrollment.Status.ACTIVE, null);
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void createShouldPropagatePropertiesCorrectly() {
        RuleAttributeValue ruleAttributeValueOne = mock(RuleAttributeValue.class);
        RuleAttributeValue ruleAttributeValueTwo = mock(RuleAttributeValue.class);
        RuleAttributeValue ruleAttributeValueThree = mock(RuleAttributeValue.class);

        Date incidentDate = new Date();
        Date enrollmentDate = new Date();

        RuleEnrollment ruleEnrollment = RuleEnrollment.create("test_enrollment",
                incidentDate, enrollmentDate, RuleEnrollment.Status.ACTIVE,
                Arrays.asList(ruleAttributeValueOne, ruleAttributeValueTwo, ruleAttributeValueThree));

        assertThat(ruleEnrollment.enrollment()).isEqualTo("test_enrollment");
        assertThat(ruleEnrollment.incidentDate()).isEqualTo(incidentDate);
        assertThat(ruleEnrollment.enrollmentDate()).isEqualTo(enrollmentDate);
        assertThat(ruleEnrollment.status()).isEqualTo(RuleEnrollment.Status.ACTIVE);
        assertThat(ruleEnrollment.attributeValues().size()).isEqualTo(3);
        assertThat(ruleEnrollment.attributeValues().get(0)).isEqualTo(ruleAttributeValueOne);
        assertThat(ruleEnrollment.attributeValues().get(1)).isEqualTo(ruleAttributeValueTwo);
        assertThat(ruleEnrollment.attributeValues().get(2)).isEqualTo(ruleAttributeValueThree);
    }

    @Test
    public void createShouldReturnImmutableList() {
        RuleAttributeValue ruleAttributeValueOne = mock(RuleAttributeValue.class);
        RuleAttributeValue ruleAttributeValueTwo = mock(RuleAttributeValue.class);
        RuleAttributeValue ruleAttributeValueThree = mock(RuleAttributeValue.class);

        List<RuleAttributeValue> attributeValues = new ArrayList<>();
        attributeValues.add(ruleAttributeValueOne);
        attributeValues.add(ruleAttributeValueTwo);

        RuleEnrollment ruleEnrollment = RuleEnrollment.create("test_enrollment",
                new Date(), new Date(), RuleEnrollment.Status.ACTIVE, attributeValues);

        // mutating source array
        attributeValues.add(ruleAttributeValueThree);

        assertThat(ruleEnrollment.attributeValues().size()).isEqualTo(2);
        assertThat(ruleEnrollment.attributeValues().get(0)).isEqualTo(ruleAttributeValueOne);
        assertThat(ruleEnrollment.attributeValues().get(1)).isEqualTo(ruleAttributeValueTwo);

        try {
            ruleEnrollment.attributeValues().clear();
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }
}
