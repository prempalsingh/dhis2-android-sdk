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
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

@Table(database = DbDhis.class, uniqueColumnGroups = {
        @UniqueGroup(
                groupNumber = TrackedEntityDataValueFlow.UNIQUE_EVENT_DATAVALUE,
                uniqueConflict = ConflictAction.FAIL)
})
@TableEndpoint(name = TrackedEntityDataValueFlow.NAME, contentProvider = DbDhis.class)
public final class TrackedEntityDataValueFlow extends BaseModelFlow {

    public static final String NAME = "TrackedEntityDataValueFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    public static final Mapper<TrackedEntityDataValue,
                TrackedEntityDataValueFlow> MAPPER = new TrackedEntityDataValueMapper();

    static final int UNIQUE_EVENT_DATAVALUE = 1;

    @Column
    @Unique(unique = false, uniqueGroups = {
            UNIQUE_EVENT_DATAVALUE
    })
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = "event", columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    EventFlow event;

    @Column
    @Unique(unique = false, uniqueGroups = {
            UNIQUE_EVENT_DATAVALUE
    })
    String dataElement;

    @Column
    String storedBy;

    @Column
    String value;

    public TrackedEntityDataValueFlow() {
        // explicit empty constructor
    }

    public EventFlow getEvent() {
        return event;
    }

    public void setEvent(EventFlow event) {
        this.event = event;
    }

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public String getStoredBy() {
        return storedBy;
    }

    public void setStoredBy(String storedBy) {
        this.storedBy = storedBy;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private static class TrackedEntityDataValueMapper extends AbsMapper<TrackedEntityDataValue,
            TrackedEntityDataValueFlow> {

        @Override
        public TrackedEntityDataValueFlow mapToDatabaseEntity(TrackedEntityDataValue model) {
            if (model == null) {
                return null;
            }

            TrackedEntityDataValueFlow flow = new TrackedEntityDataValueFlow();
            flow.setId(model.getId());
            flow.setEvent(EventFlow.MAPPER.mapToDatabaseEntity(model.getEvent()));
            flow.setDataElement(model.getDataElement());
            flow.setStoredBy(model.getStoredBy());
            flow.setValue(model.getValue());
            return flow;
        }

        @Override
        public TrackedEntityDataValue mapToModel(TrackedEntityDataValueFlow flow) {
            if (flow == null) {
                return null;
            }

            TrackedEntityDataValue model = new TrackedEntityDataValue();
            model.setId(flow.getId());
            model.setEvent(EventFlow.MAPPER.mapToModel(flow.getEvent()));
            model.setDataElement(flow.getDataElement());
            model.setStoredBy(flow.getStoredBy());
            model.setValue(flow.getValue());
            return model;
        }

        @Override
        public Class<TrackedEntityDataValue> getModelTypeClass() {
            return TrackedEntityDataValue.class;
        }

        @Override
        public Class<TrackedEntityDataValueFlow> getDatabaseEntityTypeClass() {
            return TrackedEntityDataValueFlow.class;
        }
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
