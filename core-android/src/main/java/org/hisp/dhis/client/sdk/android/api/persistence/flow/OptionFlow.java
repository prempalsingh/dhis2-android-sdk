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
import org.hisp.dhis.client.sdk.models.optionset.Option;

@Table(database = DbDhis.class)
@TableEndpoint(name = OptionFlow.NAME, contentProvider = DbDhis.class)
public final class OptionFlow extends BaseIdentifiableObjectFlow {

    public static final String NAME = "OptionFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    public static Mapper<Option, OptionFlow> MAPPER = new OptionMapper();

    static final String OPTION_SET_KEY = "optionSet";

    @Column
    int sortOrder;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = OPTION_SET_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    OptionSetFlow optionSet;

    @Column
    String code;

    public OptionFlow() {
        // empty constructor
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public OptionSetFlow getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(OptionSetFlow optionSet) {
        this.optionSet = optionSet;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private static class OptionMapper extends AbsMapper<Option, OptionFlow> {

        @Override
        public OptionFlow mapToDatabaseEntity(Option option) {
            if (option == null) {
                return null;
            }

            OptionFlow optionFlow = new OptionFlow();
            optionFlow.setId(option.getId());
            optionFlow.setUId(option.getUId());
            optionFlow.setCreated(option.getCreated());
            optionFlow.setLastUpdated(option.getLastUpdated());
            optionFlow.setName(option.getName());
            optionFlow.setDisplayName(option.getDisplayName());
            optionFlow.setAccess(option.getAccess());
            optionFlow.setSortOrder(option.getSortOrder());
            optionFlow.setOptionSet(OptionSetFlow.MAPPER
                    .mapToDatabaseEntity(option.getOptionSet()));
            optionFlow.setCode(option.getCode());
            return optionFlow;
        }

        @Override
        public Option mapToModel(OptionFlow optionFlow) {
            if (optionFlow == null) {
                return null;
            }

            Option option = new Option();
            option.setId(optionFlow.getId());
            option.setUId(optionFlow.getUId());
            option.setCreated(optionFlow.getCreated());
            option.setLastUpdated(optionFlow.getLastUpdated());
            option.setName(optionFlow.getName());
            option.setDisplayName(optionFlow.getDisplayName());
            option.setAccess(optionFlow.getAccess());
            option.setSortOrder(optionFlow.getSortOrder());
            option.setOptionSet(OptionSetFlow.MAPPER
                    .mapToModel(optionFlow.getOptionSet()));
            option.setCode(optionFlow.getCode());
            return option;
        }

        @Override
        public Class<Option> getModelTypeClass() {
            return Option.class;
        }

        @Override
        public Class<OptionFlow> getDatabaseEntityTypeClass() {
            return OptionFlow.class;
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
