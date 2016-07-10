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

package org.hisp.dhis.client.sdk.android.api.persistence.flow;

import android.net.Uri;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationElement;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This model class is intended to represent content of Interpretation {map, chart,
 * reportTable, dataSet, period, organisationUnit}
 */
@Table(database = DbDhis.class)
@TableEndpoint(name = InterpretationElementFlow.NAME, contentProvider = DbDhis.class)
public final class InterpretationElementFlow extends BaseIdentifiableObjectFlow {

    public static final String NAME = "InterpretationElementFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    @Column
    @NotNull
    String type;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = "interpretation",
                            columnType = String.class, foreignKeyColumnName = "uId")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    InterpretationFlow interpretation;

    public InterpretationElementFlow() {
        // empty constructor
    }

    public static InterpretationElement toModel(InterpretationElementFlow elementFlow) {
        if (elementFlow == null) {
            return null;
        }

        InterpretationElement element = new InterpretationElement();
        element.setId(elementFlow.getId());
        element.setUId(elementFlow.getUId());
        element.setCreated(elementFlow.getCreated());
        element.setLastUpdated(elementFlow.getLastUpdated());
        element.setName(elementFlow.getName());
        element.setDisplayName(elementFlow.getDisplayName());
        element.setAccess(elementFlow.getAccess());
        element.setType(elementFlow.getType());
        //element.setInterpretation(Interpretation_Flow
        //        .toModel(elementFlow.getInterpretation()));
        return element;
    }

    public static InterpretationElementFlow fromModel(InterpretationElement element) {
        if (element == null) {
            return null;
        }

        InterpretationElementFlow elementFlow = new InterpretationElementFlow();
        elementFlow.setId(element.getId());
        elementFlow.setUId(element.getUId());
        elementFlow.setCreated(element.getCreated());
        elementFlow.setLastUpdated(element.getLastUpdated());
        elementFlow.setName(element.getName());
        elementFlow.setDisplayName(element.getDisplayName());
        elementFlow.setAccess(element.getAccess());
        elementFlow.setType(element.getType());
        //elementFlow.setInterpretation(Interpretation_Flow
        //        .fromModel(element.getInterpretation()));
        return elementFlow;
    }

    public static List<InterpretationElement> toModels(
            List<InterpretationElementFlow> elementFlows) {
        List<InterpretationElement> elements = new ArrayList<>();

        if (elementFlows != null && !elementFlows.isEmpty()) {
            for (InterpretationElementFlow elementFlow : elementFlows) {
                elements.add(InterpretationElementFlow.toModel(elementFlow));
            }
        }

        return elements;
    }

    public static List<InterpretationElementFlow> fromModels(
            List<InterpretationElement> elements) {
        List<InterpretationElementFlow> elementFlows = new ArrayList<>();

        if (elements != null && !elements.isEmpty()) {
            for (InterpretationElement element : elements) {
                elementFlows.add(InterpretationElementFlow.fromModel(element));
            }
        }

        return elementFlows;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InterpretationFlow getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(InterpretationFlow interpretation) {
        this.interpretation = interpretation;
    }

    @Override
    public Uri getDeleteUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return CONTENT_URI;
    }
}
