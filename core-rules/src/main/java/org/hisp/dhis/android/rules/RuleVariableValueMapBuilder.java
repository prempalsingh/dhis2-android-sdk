package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleAttributeValue;
import org.hisp.dhis.android.rules.models.RuleDataValue;
import org.hisp.dhis.android.rules.models.RuleEnrollment;
import org.hisp.dhis.android.rules.models.RuleEvent;
import org.hisp.dhis.android.rules.models.RuleValueType;
import org.hisp.dhis.android.rules.models.RuleVariable;
import org.hisp.dhis.android.rules.models.RuleVariableAttribute;
import org.hisp.dhis.android.rules.models.RuleVariableCurrentEvent;
import org.hisp.dhis.android.rules.models.RuleVariableNewestEvent;
import org.hisp.dhis.android.rules.models.RuleVariableNewestStageEvent;
import org.hisp.dhis.android.rules.models.RuleVariablePreviousEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.hisp.dhis.android.rules.RuleVariableValue.create;

// ToDo: ensure that injected collections are not prone to concurrent bugs
final class RuleVariableValueMapBuilder {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String ENV_VAR_CURRENT_DATE = "current_date";
    private static final String ENV_VAR_EVENT_DATE = "event_date";
    private static final String ENV_VAR_EVENT_COUNT = "event_count";
    private static final String ENV_VAR_DUE_DATE = "due_date";
    private static final String ENV_VAR_EVENT_ID = "event_id";
    private static final String ENV_VAR_ENROLLMENT_DATE = "enrollment_date";
    private static final String ENV_VAR_ENROLLMENT_ID = "enrollment_id";
    private static final String ENV_VAR_ENROLLMENT_COUNT = "enrollment_count";
    private static final String ENV_VAR_INCIDENT_DATE = "incident_date";
    private static final String ENV_VAR_TEI_COUNT = "tei_count";

    @Nonnull
    private final SimpleDateFormat dateFormat;

    @Nonnull
    private final Map<String, RuleDataValue> currentEventValues;

    @Nonnull
    private final Map<String, RuleAttributeValue> currentEnrollmentValues;

    @Nonnull
    private final Map<String, List<RuleDataValue>> allEventsValues;

    @Nonnull
    private final List<RuleVariable> ruleVariables;

    @Nonnull
    private final List<RuleEvent> ruleEvents;

    @Nullable
    private RuleEnrollment ruleEnrollment;

    @Nullable
    private RuleEvent ruleEvent;

    private RuleVariableValueMapBuilder() {
        this.dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);

        // collections used for construction of resulting variable value map
        this.currentEnrollmentValues = new HashMap<>();
        this.currentEventValues = new HashMap<>();
        this.allEventsValues = new HashMap<>();
        this.ruleVariables = new ArrayList<>();
        this.ruleEvents = new ArrayList<>();
    }

    private RuleVariableValueMapBuilder(@Nonnull RuleEnrollment ruleEnrollment) {
        this();

        // enrollment is the target
        this.ruleEnrollment = ruleEnrollment;
    }

    private RuleVariableValueMapBuilder(@Nonnull RuleEvent ruleEvent) {
        this();

        // event is the target
        this.ruleEvent = ruleEvent;
    }

    @Nonnull
    static RuleVariableValueMapBuilder target(@Nonnull RuleEnrollment ruleEnrollment) {
        return new RuleVariableValueMapBuilder(ruleEnrollment);
    }

    @Nonnull
    static RuleVariableValueMapBuilder target(@Nonnull RuleEvent ruleEvent) {
        return new RuleVariableValueMapBuilder(ruleEvent);
    }

    @Nonnull
    RuleVariableValueMapBuilder ruleVariables(@Nonnull List<RuleVariable> ruleVariables) {
        this.ruleVariables.addAll(ruleVariables);
        return this;
    }

    @Nonnull
    RuleVariableValueMapBuilder ruleEnrollment(@Nonnull RuleEnrollment ruleEnrollment) {
        if (this.ruleEnrollment != null) {
            throw new IllegalStateException("It seems that enrollment has been set as target " +
                    "already. It can't be used as a part of execution context.");
        }

        this.ruleEnrollment = ruleEnrollment;
        return this;
    }

    @Nonnull
    RuleVariableValueMapBuilder ruleEvents(@Nonnull List<RuleEvent> ruleEvents) {
        if (isEventInList(ruleEvents, ruleEvent)) {
            throw new IllegalStateException(String.format(Locale.US, "ruleEvent %s is already set " +
                    "as a target, but also present in the context: ruleEvents list", ruleEvent.event()));
        }

        this.ruleEvents.addAll(ruleEvents);
        return this;
    }

    @Nonnull
    Map<String, RuleVariableValue> build() {
        Map<String, RuleVariableValue> valueMap = new HashMap<>();

        // map tracked entity attributes to values from enrollment
        buildCurrentEnrollmentValues();

        // build a map of current event values
        buildCurrentEventValues();

        // map data values within all events to data elements
        buildAllEventValues();

        // set environment variables
        buildEnvironmentVariables(valueMap);

        // set metadata variables
        buildRuleVariableValues(valueMap);

        // do not let outer world to alter variable value map
        return Collections.unmodifiableMap(valueMap);
    }

    private static boolean isEventInList(@Nonnull List<RuleEvent> ruleEvents,
            @Nullable RuleEvent ruleEvent) {
        if (ruleEvent != null) {
            for (int i = 0; i < ruleEvents.size(); i++) {
                RuleEvent event = ruleEvents.get(i);

                if (event.event().equals(ruleEvent.event())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void buildCurrentEventValues() {
        if (ruleEvent != null) {
            for (int index = 0; index < ruleEvent.dataValues().size(); index++) {
                RuleDataValue ruleDataValue = ruleEvent.dataValues().get(index);
                currentEventValues.put(ruleDataValue.dataElement(), ruleDataValue);
            }
        }
    }

    private void buildCurrentEnrollmentValues() {
        if (ruleEnrollment != null) {
            List<RuleAttributeValue> ruleAttributeValues = ruleEnrollment.attributeValues();
            for (int index = 0; index < ruleAttributeValues.size(); index++) {
                RuleAttributeValue attributeValue = ruleAttributeValues.get(index);
                currentEnrollmentValues.put(attributeValue.trackedEntityAttribute(), attributeValue);
            }
        }
    }

    private void buildAllEventValues() {
        List<RuleEvent> events = new ArrayList<>(ruleEvents);

        if (ruleEvent != null) {
            // target event should be among the list of all
            // events in order to achieve correct behavior
            events.add(ruleEvent);
        }

        // sort list of events by eventDate:
        Collections.sort(events, RuleEvent.EVENT_DATE_COMPARATOR);

        // aggregating values by data element uid
        for (int i = 0; i < events.size(); i++) {
            RuleEvent ruleEvent = events.get(i);

            for (int j = 0; j < ruleEvent.dataValues().size(); j++) {
                RuleDataValue ruleDataValue = ruleEvent.dataValues().get(j);

                // push new list if it is not there for the given data element
                if (!allEventsValues.containsKey(ruleDataValue.dataElement())) {
                    allEventsValues.put(ruleDataValue.dataElement(),
                            new ArrayList<RuleDataValue>(events.size()));
                }

                // append data value to the list
                allEventsValues.get(ruleDataValue.dataElement()).add(ruleDataValue);
            }
        }
    }

    private void buildEnvironmentVariables(@Nonnull Map<String, RuleVariableValue> valueMap) {
        String currentDate = dateFormat.format(new Date());
        valueMap.put(ENV_VAR_CURRENT_DATE, create(currentDate,
                RuleValueType.TEXT, Arrays.asList(currentDate)));

        if (!ruleEvents.isEmpty()) {
            valueMap.put(ENV_VAR_EVENT_COUNT, create(String.valueOf(ruleEvents.size()),
                    RuleValueType.NUMERIC, Arrays.asList(String.valueOf(ruleEvents.size()))));
        }

        if (ruleEnrollment != null) {
            valueMap.put(ENV_VAR_ENROLLMENT_ID, create(ruleEnrollment.enrollment(),
                    RuleValueType.TEXT, Arrays.asList(ruleEnrollment.enrollment())));
            valueMap.put(ENV_VAR_ENROLLMENT_COUNT, create("1",
                    RuleValueType.NUMERIC, Arrays.asList("1")));
            valueMap.put(ENV_VAR_TEI_COUNT, create("1",
                    RuleValueType.NUMERIC, Arrays.asList("1")));

            String enrollmentDate = dateFormat.format(ruleEnrollment.enrollmentDate());
            valueMap.put(ENV_VAR_ENROLLMENT_DATE, create(enrollmentDate,
                    RuleValueType.TEXT, Arrays.asList(enrollmentDate)));

            String incidentDate = dateFormat.format(ruleEnrollment.incidentDate());
            valueMap.put(ENV_VAR_INCIDENT_DATE, create(incidentDate,
                    RuleValueType.TEXT, Arrays.asList(incidentDate)));
        }

        if (ruleEvent != null) {
            String eventDate = dateFormat.format(ruleEvent.eventDate());
            valueMap.put(ENV_VAR_EVENT_DATE, create(eventDate, RuleValueType.TEXT,
                    Arrays.asList(eventDate)));

            String dueDate = dateFormat.format(ruleEvent.dueDate());
            valueMap.put(ENV_VAR_DUE_DATE, create(dueDate, RuleValueType.TEXT,
                    Arrays.asList(dueDate)));

            // override value of event count
            String eventCount = String.valueOf(ruleEvents.size() + 1);
            valueMap.put(ENV_VAR_EVENT_COUNT, create(eventCount,
                    RuleValueType.NUMERIC, Arrays.asList(eventCount)));
            valueMap.put(ENV_VAR_EVENT_ID, create(ruleEvent.event(),
                    RuleValueType.TEXT, Arrays.asList(ruleEvent.event())));
        }
    }

    private void buildRuleVariableValues(@Nonnull Map<String, RuleVariableValue> valueMap) {
        // split values into ruleVariables
        for (RuleVariable ruleVariable : ruleVariables) {
            if (ruleVariable instanceof RuleVariableAttribute) {
                if (ruleEnrollment != null) {
                    RuleVariableAttribute ruleVariableAttribute
                            = (RuleVariableAttribute) ruleVariable;
                    valueMap.put(ruleVariable.name(),
                            createAttributeVariableValue(ruleVariableAttribute));
                }
            } else if (ruleVariable instanceof RuleVariableCurrentEvent) {
                if (ruleEvent != null) {
                    RuleVariableCurrentEvent currentEventVariable
                            = (RuleVariableCurrentEvent) ruleVariable;
                    valueMap.put(currentEventVariable.name(),
                            createCurrentEventVariableValue(currentEventVariable));
                }
            } else if (ruleVariable instanceof RuleVariablePreviousEvent) {
                if (ruleEvent != null) {
                    RuleVariablePreviousEvent ruleVariablePreviousEvent
                            = (RuleVariablePreviousEvent) ruleVariable;
                    valueMap.put(ruleVariable.name(),
                            createPreviousEventVariableValue(ruleVariablePreviousEvent));
                }
            } else if (ruleVariable instanceof RuleVariableNewestEvent) {
                RuleVariableNewestEvent ruleVariableNewestEvent
                        = (RuleVariableNewestEvent) ruleVariable;
                valueMap.put(ruleVariableNewestEvent.name(),
                        createNewestEventVariableValue(ruleVariableNewestEvent));
            } else if (ruleVariable instanceof RuleVariableNewestStageEvent) {
                RuleVariableNewestStageEvent ruleVariableNewestEvent
                        = (RuleVariableNewestStageEvent) ruleVariable;
                valueMap.put(ruleVariableNewestEvent.name(),
                        createNewestStageEventVariableValue(ruleVariableNewestEvent));
            } else {
                throw new IllegalArgumentException("Unsupported RuleVariable type: " +
                        ruleVariable.getClass());
            }
        }
    }

    @Nonnull
    private RuleVariableValue createAttributeVariableValue(
            @Nonnull RuleVariableAttribute variable) {
        if (ruleEnrollment == null) {
            // there is no way to calculate variable value
            // for current enrollment if it is not present
            throw new IllegalStateException();
        }
        if (currentEnrollmentValues.containsKey(variable.trackedEntityAttribute())) {
            RuleAttributeValue value = currentEnrollmentValues
                    .get(variable.trackedEntityAttribute());
            return create(value.value(), variable.trackedEntityAttributeType(),
                    Arrays.asList(value.value()));
        }

        return RuleVariableValue.create(variable.trackedEntityAttributeType());
    }

    @Nonnull
    private RuleVariableValue createCurrentEventVariableValue(
            @Nonnull RuleVariableCurrentEvent variable) {
        if (ruleEvent == null) {
            // there is no way to calculate variable value
            // for current event if it is not present
            throw new IllegalStateException();
        }
        if (currentEventValues.containsKey(variable.dataElement())) {
            RuleDataValue value = currentEventValues.get(variable.dataElement());
            return create(value.value(), variable.dataElementType(),
                    Arrays.asList(value.value()));
        }

        return create(variable.dataElementType());
    }

    @Nonnull
    private RuleVariableValue createPreviousEventVariableValue(
            @Nonnull RuleVariablePreviousEvent variable) {
        if (ruleEvent == null) {
            // we can't calculate correct value if event is not present
            throw new IllegalStateException();
        }

        List<RuleDataValue> ruleDataValues = allEventsValues.get(variable.dataElement());
        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
            for (RuleDataValue ruleDataValue : ruleDataValues) {
                // We found preceding value to the current currentEventValues,
                // which is assumed to be best candidate.
                if (ruleEvent.eventDate().compareTo(ruleDataValue.eventDate()) > 0) {
                    return create(ruleDataValue.value(), variable.dataElementType(),
                            Utils.values(ruleDataValues));
                }
            }
        }

        return create(variable.dataElementType());
    }

    @Nonnull
    private RuleVariableValue createNewestEventVariableValue(
            @Nonnull RuleVariableNewestEvent variable) {
        List<RuleDataValue> ruleDataValues = allEventsValues.get(variable.dataElement());
        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
            return create(ruleDataValues.get(0).value(),
                    variable.dataElementType(), Utils.values(ruleDataValues));
        }

        return create(variable.dataElementType());
    }

    @Nonnull
    private RuleVariableValue createNewestStageEventVariableValue(
            @Nonnull RuleVariableNewestStageEvent variable) {
        List<RuleDataValue> stageRuleDataValues = new ArrayList<>();
        List<RuleDataValue> sourceRuleDataValues = allEventsValues.get(variable.dataElement());
        if (sourceRuleDataValues != null && !sourceRuleDataValues.isEmpty()) {
            // filter data values based on program stage
            for (int i = 0; i < sourceRuleDataValues.size(); i++) {
                RuleDataValue ruleDataValue = sourceRuleDataValues.get(i);
                if (variable.programStage().equals(ruleDataValue.programStage())) {
                    stageRuleDataValues.add(ruleDataValue);
                }
            }
        }

        if (!stageRuleDataValues.isEmpty()) {
            return create(stageRuleDataValues.get(0).value(),
                    variable.dataElementType(), Utils.values(stageRuleDataValues));
        }

        return create(variable.dataElementType());
    }
}
