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
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;

@Table(database = DbDhis.class)
@TableEndpoint(name = ProgramIndicatorToProgramStageSectionRelationFlow.NAME, contentProvider = DbDhis.class)
public final class ProgramIndicatorToProgramStageSectionRelationFlow
        extends BaseProviderModel<ProgramIndicatorToProgramStageSectionRelationFlow> {

    public static final String NAME = "ProgramIndicatorToProgramStageSectionRelationFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    private final static String PROGRAM_INDICATOR_KEY = "programIndicator";
    private final static String PROGRAM_STAGE_SECTION_KEY = "programStageSection";

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_INDICATOR_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramIndicatorFlow programIndicator;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_SECTION_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramStageSectionFlow programStageSection;

    public ProgramIndicatorToProgramStageSectionRelationFlow() {
        //empty constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProgramIndicatorFlow getProgramIndicator() {
        return programIndicator;
    }

    public void setProgramIndicator(ProgramIndicatorFlow programIndicator) {
        this.programIndicator = programIndicator;
    }

    public ProgramStageSectionFlow getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(ProgramStageSectionFlow programStageSection) {
        this.programStageSection = programStageSection;
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
