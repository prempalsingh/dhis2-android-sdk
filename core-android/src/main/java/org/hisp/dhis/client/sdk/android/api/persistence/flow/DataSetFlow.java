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
import org.hisp.dhis.client.sdk.models.dataset.DataSet;

@Table(database = DbDhis.class)
@TableEndpoint(name = DataSetFlow.NAME, contentProvider = DbDhis.class)
public final class DataSetFlow extends BaseIdentifiableObjectFlow {

    public static final String NAME = "DataSetFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    public static Mapper<DataSet, DataSetFlow> MAPPER = new DataSetMapper();

    private static final String CATEGORY_COMBO_KEY = "categoryComboKey";

    @Column
    int version;

    @Column
    int expiryDays;

    @Column
    boolean allowFuturePeriods;

    @Column
    String periodType;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = CATEGORY_COMBO_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    CategoryComboFlow categoryCombo;

    public DataSetFlow() {
    }

    public CategoryComboFlow getCategoryCombo() {
        return categoryCombo;
    }

    public void setCategoryCombo(CategoryComboFlow categoryCombo) {
        this.categoryCombo = categoryCombo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(int expiryDays) {
        this.expiryDays = expiryDays;
    }

    public boolean isAllowFuturePeriods() {
        return allowFuturePeriods;
    }

    public void setAllowFuturePeriods(boolean allowFuturePeriods) {
        this.allowFuturePeriods = allowFuturePeriods;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    private static class DataSetMapper extends AbsMapper<DataSet, DataSetFlow> {

        @Override
        public DataSetFlow mapToDatabaseEntity(DataSet dataSet) {
            if (dataSet == null) {
                return null;
            }

            DataSetFlow dataSetFlow = new DataSetFlow();
            dataSetFlow.setId(dataSet.getId());
            dataSetFlow.setUId(dataSet.getUId());
            dataSetFlow.setCreated(dataSet.getCreated());
            dataSetFlow.setLastUpdated(dataSet.getLastUpdated());
            dataSetFlow.setName(dataSet.getName());
            dataSetFlow.setDisplayName(dataSet.getDisplayName());
            dataSetFlow.setAccess(dataSet.getAccess());
            dataSetFlow.setVersion(dataSet.getVersion());
            dataSetFlow.setExpiryDays(dataSet.getExpiryDays());
            dataSetFlow.setAllowFuturePeriods(dataSet.isAllowFuturePeriods());
            dataSetFlow.setPeriodType(dataSet.getPeriodType());
            dataSetFlow.setCategoryCombo(null);
            return dataSetFlow;
        }

        @Override
        public DataSet mapToModel(DataSetFlow dataSetFlow) {
            if (dataSetFlow == null) {
                return null;
            }

            DataSet dataSet = new DataSet();
            dataSet.setId(dataSetFlow.getId());
            dataSet.setUId(dataSetFlow.getUId());
            dataSet.setCreated(dataSetFlow.getCreated());
            dataSet.setLastUpdated(dataSetFlow.getLastUpdated());
            dataSet.setName(dataSetFlow.getName());
            dataSet.setDisplayName(dataSetFlow.getDisplayName());
            dataSet.setAccess(dataSetFlow.getAccess());
            dataSet.setVersion(dataSetFlow.getVersion());
            dataSet.setExpiryDays(dataSetFlow.getExpiryDays());
            dataSet.setAllowFuturePeriods(dataSetFlow.isAllowFuturePeriods());
            dataSet.setPeriodType(dataSetFlow.getPeriodType());
            dataSet.setCategoryCombo(null);
            return dataSet;
        }

        @Override
        public Class<DataSet> getModelTypeClass() {
            return DataSet.class;
        }

        @Override
        public Class<DataSetFlow> getDatabaseEntityTypeClass() {
            return DataSetFlow.class;
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
