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
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

@Table(database = DbDhis.class)
@TableEndpoint(name = ProgramStageSectionFlow.NAME, contentProvider = DbDhis.class)
public final class ProgramStageSectionFlow extends BaseIdentifiableObjectFlow {

    public static final String NAME = "ProgramStageSectionFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    public static final Mapper<ProgramStageSection, ProgramStageSectionFlow>
            MAPPER = new SectionMapper();

    private static final String PROGRAM_STAGE_KEY = "programStage";

    @Column
    int sortOrder;

    @Column
    boolean externalAccess;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramStageFlow programStage;

    public ProgramStageSectionFlow() {
        // empty constructor
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public ProgramStageFlow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStageFlow programStage) {
        this.programStage = programStage;
    }

    private static class SectionMapper extends AbsMapper<ProgramStageSection, ProgramStageSectionFlow> {

        @Override
        public ProgramStageSectionFlow mapToDatabaseEntity(
                ProgramStageSection programStageSection) {
            if (programStageSection == null) {
                return null;
            }

            ProgramStageSectionFlow programStageSectionFlow = new ProgramStageSectionFlow();
            programStageSectionFlow.setId(programStageSection.getId());
            programStageSectionFlow.setUId(programStageSection.getUId());
            programStageSectionFlow.setCreated(programStageSection.getCreated());
            programStageSectionFlow.setLastUpdated(programStageSection.getLastUpdated());
            programStageSectionFlow.setName(programStageSection.getName());
            programStageSectionFlow.setDisplayName(programStageSection.getDisplayName());
            programStageSectionFlow.setAccess(programStageSection.getAccess());
            programStageSectionFlow.setSortOrder(programStageSection.getSortOrder());
            programStageSectionFlow.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToDatabaseEntity(programStageSection.getProgramStage()));
            return programStageSectionFlow;
        }

        @Override
        public ProgramStageSection mapToModel(ProgramStageSectionFlow programStageSectionFlow) {
            if (programStageSectionFlow == null) {
                return null;
            }

            ProgramStageSection programStageSection = new ProgramStageSection();
            programStageSection.setId(programStageSectionFlow.getId());
            programStageSection.setUId(programStageSectionFlow.getUId());
            programStageSection.setCreated(programStageSectionFlow.getCreated());
            programStageSection.setLastUpdated(programStageSectionFlow.getLastUpdated());
            programStageSection.setName(programStageSectionFlow.getName());
            programStageSection.setDisplayName(programStageSectionFlow.getDisplayName());
            programStageSection.setAccess(programStageSectionFlow.getAccess());
            programStageSection.setSortOrder(programStageSectionFlow.getSortOrder());
            programStageSection.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(programStageSectionFlow.getProgramStage()));
            return programStageSection;
        }

        @Override
        public Class<ProgramStageSection> getModelTypeClass() {
            return ProgramStageSection.class;
        }

        @Override
        public Class<ProgramStageSectionFlow> getDatabaseEntityTypeClass() {
            return ProgramStageSectionFlow.class;
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
