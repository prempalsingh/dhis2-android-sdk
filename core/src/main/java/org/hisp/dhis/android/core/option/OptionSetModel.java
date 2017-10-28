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

package org.hisp.dhis.android.core.option;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DbValueTypeColumnAdapter;


@AutoValue
public abstract class OptionSetModel extends BaseIdentifiableObjectModel {
    public static final String TABLE = "OptionSet";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String VERSION = "version";
        public static final String VALUE_TYPE = "valueType";
    }

    public static OptionSetModel create(Cursor cursor) {
        return AutoValue_OptionSetModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_OptionSetModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.VERSION)
    public abstract Integer version();

    @Nullable
    @ColumnName(Columns.VALUE_TYPE)
    @ColumnAdapter(DbValueTypeColumnAdapter.class)
    public abstract ValueType valueType();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder version(@Nullable Integer version);

        public abstract Builder valueType(@Nullable ValueType valueType);

        public abstract OptionSetModel build();
    }
}
