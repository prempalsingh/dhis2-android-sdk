/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

class RuleEngineVariableValueMap {
    private final Map<String, ProgramRuleVariableValue> programRuleVariableValueMap;

    public RuleEngineVariableValueMap(List<ProgramRuleVariable> variables,
                                      Event currentEvent, List<Event> allEvents) {
        programRuleVariableValueMap = new HashMap<>();

        // if we don't have list of variables, we can't do anything
        if (variables == null) {
            return;
        }

        Map<String, TrackedEntityDataValue> currentEventToValuesMap =
                initEventToValuesMap(currentEvent);
        Map<String, List<TrackedEntityDataValue>> allEventsToValuesMap =
                initEventsToValuesMap(allEvents);
        initProgramRuleVariableMap(currentEvent, currentEventToValuesMap,
                allEventsToValuesMap, variables);
    }

    private Map<String, TrackedEntityDataValue> initEventToValuesMap(Event currentEvent) {
        Map<String, TrackedEntityDataValue> eventToValueMap = new HashMap<>();

        if (currentEvent != null && currentEvent.getDataValues() != null) {
            for (TrackedEntityDataValue value : currentEvent.getDataValues()) {
                eventToValueMap.put(value.getDataElement(), value);
            }
        }

        return eventToValueMap;
    }

    private Map<String, List<TrackedEntityDataValue>> initEventsToValuesMap(List<Event> allEvents) {
        Map<String, List<TrackedEntityDataValue>> eventsToValuesMap = new HashMap<>();

        if (allEvents == null || allEvents.isEmpty()) {
            return eventsToValuesMap;
        }

        Collections.sort(allEvents, Event.DATE_COMPARATOR);

        for (Event event : allEvents) {

            // if event does not contain values, skip it
            if (event.getDataValues() == null) {
                continue;
            }

            for (TrackedEntityDataValue value : event.getDataValues()) {
                if (!eventsToValuesMap.containsKey(value.getDataElement())) {
                    eventsToValuesMap.put(value.getDataElement(),
                            new ArrayList<TrackedEntityDataValue>());
                }

                // make sure the event is assigned, it is used later to check event date for
                // the data values
                if (value.getEvent() == null) {
                    value.setEvent(event);
                }

                eventsToValuesMap.get(value.getDataElement()).add(value);
            }
        }

        return eventsToValuesMap;
    }

    private void initProgramRuleVariableMap(
            Event currentEvent, Map<String, TrackedEntityDataValue> currentEventToValuesMap,
            Map<String, List<TrackedEntityDataValue>> allEventsToValuesMap,
            List<ProgramRuleVariable> programRuleVariables) {

        for (ProgramRuleVariable variable : programRuleVariables) {

            boolean valueFound = true;
            switch (variable.getSourceType()) {
                case DATAELEMENT_CURRENT_EVENT: {
                    if (currentEventToValuesMap.containsKey(variable.getDataElement().getUId())) {
                        TrackedEntityDataValue dataValue = currentEventToValuesMap
                                .get(variable.getDataElement().getUId());
                        addProgramRuleVariableValueToMap(variable, dataValue, null);
                        valueFound = true;
                    }
                    break;
                }
                case DATAELEMENT_NEWEST_EVENT_PROGRAM: {
                    if (allEventsToValuesMap.containsKey(variable.getDataElement().getUId())) {
                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.getDataElement().getUId());
                        TrackedEntityDataValue dataValue = valueList.get(valueList.size() - 1);
                        addProgramRuleVariableValueToMap(variable, dataValue, valueList);
                        valueFound = true;
                    }
                    break;
                }
                case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE: {
                    if (variable.getProgramStage() != null && allEventsToValuesMap.containsKey(
                            variable.getDataElement().getUId())) {

                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.getDataElement().getUId());

                        TrackedEntityDataValue bestCandidate = null;
                        for (TrackedEntityDataValue candidate : valueList) {

                            if (variable.getProgramStage().getUId().equals(
                                    candidate.getEvent().getProgramStage())) {

                                // The candidate matches the program stage, and will be newer than
                                // the potential previous candidate:
                                bestCandidate = candidate;
                            }
                        }

                        if (bestCandidate != null) {
                            addProgramRuleVariableValueToMap(variable, bestCandidate, valueList);
                            valueFound = true;
                        }
                    }
                    break;
                }
                case DATAELEMENT_PREVIOUS_EVENT: {
                    if (currentEvent != null && allEventsToValuesMap.containsKey(
                            variable.getDataElement().getUId())) {
                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.getDataElement().getUId());

                        TrackedEntityDataValue bestCandidate = null;
                        for (TrackedEntityDataValue candidate : valueList) {
                            if (candidate.getEvent().getEventDate().compareTo(
                                    currentEvent.getEventDate()) >= 0) {
                                // we have reached the current event time, stop iterating, keep the
                                // previous candidate, if any
                                break;
                            } else {
                                // we have not yet reached the current event, keep this candidate
                                // as it is the newest one examined:
                                bestCandidate = candidate;
                            }
                        }

                        if (bestCandidate != null) {
                            addProgramRuleVariableValueToMap(variable, bestCandidate, valueList);
                            valueFound = true;
                        }
                    }
                    break;
                }
                default: {
                    // TODO: Add general handling when value is not found.
                    // TODO: Use logger to output values
                    throw new NotImplementedException();
                }
            }
        }
    }

    public ProgramRuleVariableValue getProgramRuleVariableValue(String variableName) {
        return programRuleVariableValueMap.get(variableName);
    }

    public Map<String, ProgramRuleVariableValue> getProgramRuleVariableValueMap() {
        return programRuleVariableValueMap;
    }

    private void addProgramRuleVariableValueToMap(ProgramRuleVariable programRuleVariable,
                                                  TrackedEntityDataValue value,
                                                  List<TrackedEntityDataValue> allValues) {
        ProgramRuleVariableValue variableValue = new ProgramRuleVariableValue(value, allValues);
        programRuleVariableValueMap.put(programRuleVariable.getDisplayName(), variableValue);
    }

    @Override
    public String toString() {
        return "RuleEngineVariableValueMap{" +
                "programRuleVariableValueMap=" + programRuleVariableValueMap +
                '}';
    }
}
