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
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;

@Table(database = DbDhis.class)
@TableEndpoint(name = RelationshipTypeFlow.NAME, contentProvider = DbDhis.class)
public final class RelationshipTypeFlow extends BaseIdentifiableObjectFlow {

    public static final String NAME = "RelationshipTypeFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    public static final Mapper<RelationshipType, RelationshipTypeFlow>
            MAPPER = new RelationshipTypeMapper();

    @Column
    String aIsToB;

    @Column
    String bIsToA;

    public RelationshipTypeFlow() {
        // empty constructor
    }

    public String getaIsToB() {
        return aIsToB;
    }

    public void setaIsToB(String aIsToB) {
        this.aIsToB = aIsToB;
    }

    public String getbIsToA() {
        return bIsToA;
    }

    public void setbIsToA(String bIsToA) {
        this.bIsToA = bIsToA;
    }

    private static class RelationshipTypeMapper
            extends AbsMapper<RelationshipType, RelationshipTypeFlow> {

        @Override
        public RelationshipTypeFlow mapToDatabaseEntity(RelationshipType relationshipType) {
            if (relationshipType == null) {
                return null;
            }

            RelationshipTypeFlow relationshipTypeFlow = new RelationshipTypeFlow();
            relationshipTypeFlow.setId(relationshipType.getId());
            relationshipTypeFlow.setUId(relationshipType.getUId());
            relationshipTypeFlow.setCreated(relationshipType.getCreated());
            relationshipTypeFlow.setLastUpdated(relationshipType.getLastUpdated());
            relationshipTypeFlow.setName(relationshipType.getName());
            relationshipTypeFlow.setDisplayName(relationshipType.getDisplayName());
            relationshipTypeFlow.setAccess(relationshipType.getAccess());
            relationshipTypeFlow.setaIsToB(relationshipType.getaIsToB());
            relationshipTypeFlow.setbIsToA(relationshipType.getbIsToA());
            return relationshipTypeFlow;
        }

        @Override
        public RelationshipType mapToModel(RelationshipTypeFlow relationshipTypeFlow) {
            if (relationshipTypeFlow == null) {
                return null;
            }

            RelationshipType relationshipType = new RelationshipType();
            relationshipType.setId(relationshipTypeFlow.getId());
            relationshipType.setUId(relationshipTypeFlow.getUId());
            relationshipType.setCreated(relationshipTypeFlow.getCreated());
            relationshipType.setLastUpdated(relationshipTypeFlow.getLastUpdated());
            relationshipType.setName(relationshipTypeFlow.getName());
            relationshipType.setDisplayName(relationshipTypeFlow.getDisplayName());
            relationshipType.setAccess(relationshipTypeFlow.getAccess());
            relationshipType.setaIsToB(relationshipTypeFlow.getaIsToB());
            relationshipType.setbIsToA(relationshipTypeFlow.getbIsToA());
            return relationshipType;
        }

        @Override
        public Class<RelationshipType> getModelTypeClass() {
            return RelationshipType.class;
        }

        @Override
        public Class<RelationshipTypeFlow> getDatabaseEntityTypeClass() {
            return RelationshipTypeFlow.class;
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
