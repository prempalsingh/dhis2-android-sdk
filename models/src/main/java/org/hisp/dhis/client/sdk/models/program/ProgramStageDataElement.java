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

package org.hisp.dhis.client.sdk.models.program;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.base.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.base.IModel;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ProgramStageDataElement extends BaseIdentifiableObject {

    @JsonProperty("programStage")
    private ProgramStage programStage;

    @JsonProperty("dataElement")
    private DataElement dataElement;

    @JsonProperty("allowFutureDate")
    private boolean allowFutureDate;

    @JsonProperty("sortOrder")
    private int sortOrder;

    @JsonProperty("displayInReports")
    private boolean displayInReports;

    @JsonProperty("allowProvidedElsewhere")
    private boolean allowProvidedElsewhere;

    @JsonProperty("compulsory")
    private boolean compulsory;

    private ProgramStageSection programStageSection;

    @JsonProperty("dataElement")
    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }

    public DataElement getDataElement() {
        return dataElement;
    }

    public ProgramStage getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStage programStage) {
        this.programStage = programStage;
    }

    public boolean isAllowFutureDate() {
        return allowFutureDate;
    }

    public void setAllowFutureDate(boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isDisplayInReports() {
        return displayInReports;
    }

    public void setDisplayInReports(boolean displayInReports) {
        this.displayInReports = displayInReports;
    }

    public boolean isAllowProvidedElsewhere() {
        return allowProvidedElsewhere;
    }

    public void setAllowProvidedElsewhere(boolean allowProvidedElsewhere) {
        this.allowProvidedElsewhere = allowProvidedElsewhere;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    public ProgramStageSection getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(ProgramStageSection programStageSection) {
        this.programStageSection = programStageSection;
    }
}
