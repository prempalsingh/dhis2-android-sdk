/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;

@AutoValue
public abstract class UserOrganisationUnitLinkModel extends BaseModel {
    public static final String TABLE = "UserOrganisationUnit";

    public static class Columns extends BaseModel.Columns {
        public static final String USER = "user";
        public static final String ORGANISATION_UNIT = "organisationUnit";
        public static final String ORGANISATION_UNIT_SCOPE = "organisationUnitScope";
    }

    @Nullable
    @ColumnName(Columns.USER)
    public abstract String user();

    @Nullable
    @ColumnName(Columns.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @ColumnName(Columns.ORGANISATION_UNIT_SCOPE)
    public abstract String organisationUnitScope();

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static UserOrganisationUnitLinkModel create(Cursor cursor) {
        return AutoValue_UserOrganisationUnitLinkModel.createFromCursor(cursor);
    }

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_UserOrganisationUnitLinkModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder user(@Nullable String user);

        public abstract Builder organisationUnit(@Nullable String organisationUnit);

        public abstract Builder organisationUnitScope(@Nullable String organisationUnitScope);

        public abstract UserOrganisationUnitLinkModel build();
    }
}
