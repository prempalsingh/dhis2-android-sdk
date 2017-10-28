package org.hisp.dhis.rules;

import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleEnrollment;
import org.hisp.dhis.rules.models.RuleEvent;
import org.hisp.dhis.rules.models.RuleVariable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class RuleEngineTests {

    @Mock
    private RuleExpressionEvaluator evaluator;

    @Mock
    private Rule rule;

    @Mock
    private RuleVariable ruleVariable;

    @Mock
    private RuleEvent ruleEvent;

    private RuleEngineContext ruleEngineContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ruleEngineContext = RuleEngineContext.builder(evaluator)
                .ruleVariables(Arrays.asList(mock(RuleVariable.class)))
                .rules(Arrays.asList(mock(Rule.class)))
                .build();
    }

    @Test
    public void builderShouldThrowOnNullEnrollment() {
        try {
            ruleEngineContext.toEngineBuilder()
                    .enrollment(null)
                    .events(new ArrayList<RuleEvent>())
                    .build();
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void builderShouldThrowOnNullEvents() {
        try {
            ruleEngineContext.toEngineBuilder()
                    .enrollment(null)
                    .events(null)
                    .build();
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void builderShouldPropagateImmutableEventsList() {
        RuleEvent ruleEventOne = mock(RuleEvent.class);
        RuleEvent ruleEventTwo = mock(RuleEvent.class);

        List<RuleEvent> ruleEvents = new ArrayList<>();
        ruleEvents.add(ruleEventOne);

        RuleEngine ruleEngine = ruleEngineContext.toEngineBuilder()
                .events(ruleEvents)
                .build();

        ruleEvents.add(ruleEventTwo);

        assertThat(ruleEngine.events().size()).isEqualTo(1);
        assertThat(ruleEngine.events().get(0)).isEqualTo(ruleEventOne);

        try {
            ruleEngine.events().clear();
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }

    @Test
    public void builderShouldPropagateImmutableEmptyListIfNoEventsProvided() {
        RuleEngine ruleEngine = ruleEngineContext.toEngineBuilder().build();

        assertThat(ruleEngine.events().size()).isEqualTo(0);

        try {
            ruleEngine.events().clear();
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }

    @Test
    public void builderShouldPropagateRuleEngineContext() {
        RuleEngine ruleEngine = ruleEngineContext.toEngineBuilder().build();
        assertThat(ruleEngine.executionContext()).isEqualTo(ruleEngineContext);
    }

    @Test
    public void evaluateShouldThrowOnNullEvent() {
        try {
            RuleEvent ruleEvent = null;
            ruleEngineContext.toEngineBuilder().build().evaluate(ruleEvent);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void evaluateShouldThrowIfEventIsAlreadyInContext() {
        RuleEvent ruleEvent = RuleEvent.create("test_event", "test_programstage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), new ArrayList<RuleDataValue>());

        List<RuleEvent> ruleEvents = new ArrayList<>();
        ruleEvents.add(ruleEvent);

        RuleEngine ruleEngine = ruleEngineContext.toEngineBuilder()
                .events(ruleEvents)
                .build();

        try {
            ruleEngine.evaluate(ruleEvent);
            fail("IllegalStateException was expected, but nothing was thrown.");
        } catch (IllegalStateException illegalStateException) {
            // noop
        }
    }

    @Test
    public void evaluateShouldThrowIfEnrollmentIsAlreadyInContext() {
        RuleEnrollment ruleEnrollment = mock(RuleEnrollment.class);

        RuleEngine ruleEngine = ruleEngineContext.toEngineBuilder()
                .enrollment(ruleEnrollment)
                .build();

        try {
            ruleEngine.evaluate(ruleEnrollment);
            fail("IllegalStateException was expected, but nothing was thrown.");
        } catch (IllegalStateException illegalStateException) {
            // noop
        }
    }

    @Test
    public void evaluateShouldThrowOnNullEnrollment() {
        try {
            RuleEnrollment ruleEnrollment = null;
            ruleEngineContext.toEngineBuilder().build().evaluate(ruleEnrollment);
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void concurrentIterationOverRulesListShouldNotFail() throws InterruptedException {
        final RuleEngine ruleEngine = RuleEngineContext.builder(mock(RuleExpressionEvaluator.class))
                .rules(Arrays.asList(mock(Rule.class), mock(Rule.class)))
                .build().toEngineBuilder().build();

        final CountDownLatch threadOneLatch = new CountDownLatch(1);
        final CountDownLatch threadTwoLatch = new CountDownLatch(1);

        new Thread() {
            @Override
            public void run() {
                for (Rule rule : ruleEngine.executionContext().rules()) {

                    try {
                        threadTwoLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    threadOneLatch.countDown();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                for (Rule rule : ruleEngine.executionContext().rules()) {
                    threadTwoLatch.countDown();

                    try {
                        threadOneLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();


        assertThat(threadOneLatch.await(4, TimeUnit.SECONDS)).isTrue();
        assertThat(threadTwoLatch.await(4, TimeUnit.SECONDS)).isTrue();
    }
}
