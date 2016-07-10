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
import org.hisp.dhis.client.sdk.models.dataelement.ValueType;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;

@Table(database = DbDhis.class)
@TableEndpoint(name = DataElementFlow.NAME, contentProvider = DbDhis.class)
public final class DataElementFlow extends BaseIdentifiableObjectFlow {

    public static final String NAME = "DataElementFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    public static Mapper<DataElement, DataElementFlow> MAPPER = new DataElementMapper();

    private final static String OPTION_SET_KEY = "optionset";

    @Column
    ValueType valueType;

    @Column
    boolean zeroIsSignificant;

    @Column
    String aggregationOperator;

    @Column
    String formName;

    @Column
    String numberType;

    @Column
    String domainType;

    @Column
    String dimension;

    @Column
    String displayFormName;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = OPTION_SET_KEY, columnType = String.class,
                            foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    OptionSetFlow optionSet;


    public DataElementFlow() {
        // empty constructor
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public boolean isZeroIsSignificant() {
        return zeroIsSignificant;
    }

    public void setZeroIsSignificant(boolean zeroIsSignificant) {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    public String getAggregationOperator() {
        return aggregationOperator;
    }

    public void setAggregationOperator(String aggregationOperator) {
        this.aggregationOperator = aggregationOperator;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDisplayFormName() {
        return displayFormName;
    }

    public void setDisplayFormName(String displayFormName) {
        this.displayFormName = displayFormName;
    }

    public OptionSetFlow getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(OptionSetFlow optionSet) {
        this.optionSet = optionSet;
    }

    private static class DataElementMapper extends AbsMapper<DataElement, DataElementFlow> {

        @Override
        public DataElementFlow mapToDatabaseEntity(DataElement dataElement) {
            if (dataElement == null) {
                return null;
            }

            DataElementFlow dataElementFlow = new DataElementFlow();
            dataElementFlow.setId(dataElement.getId());
            dataElementFlow.setUId(dataElement.getUId());
            dataElementFlow.setCreated(dataElement.getCreated());
            dataElementFlow.setLastUpdated(dataElement.getLastUpdated());
            dataElementFlow.setName(dataElement.getName());
            dataElementFlow.setDisplayName(dataElement.getDisplayName());
            dataElementFlow.setAccess(dataElement.getAccess());
            dataElementFlow.setValueType(dataElement.getValueType());
            dataElementFlow.setZeroIsSignificant(dataElement.isZeroIsSignificant());
            dataElementFlow.setAggregationOperator(dataElement.getAggregationOperator());
            dataElementFlow.setFormName(dataElement.getFormName());
            dataElementFlow.setNumberType(dataElement.getNumberType());
            dataElementFlow.setDomainType(dataElement.getDomainType());
            dataElementFlow.setDimension(dataElement.getDimension());
            dataElementFlow.setDisplayFormName(dataElement.getDisplayFormName());
            dataElementFlow.setOptionSet(OptionSetFlow.MAPPER
                    .mapToDatabaseEntity(dataElement.getOptionSet()));
            return dataElementFlow;
        }

        @Override
        public DataElement mapToModel(DataElementFlow dataElementFlow) {
            if (dataElementFlow == null) {
                return null;
            }

            DataElement dataElement = new DataElement();
            dataElement.setId(dataElementFlow.getId());
            dataElement.setUId(dataElementFlow.getUId());
            dataElement.setCreated(dataElementFlow.getCreated());
            dataElement.setLastUpdated(dataElementFlow.getLastUpdated());
            dataElement.setName(dataElementFlow.getName());
            dataElement.setDisplayName(dataElementFlow.getDisplayName());
            dataElement.setAccess(dataElementFlow.getAccess());
            dataElement.setValueType(dataElementFlow.getValueType());
            dataElement.setZeroIsSignificant(dataElementFlow.isZeroIsSignificant());
            dataElement.setAggregationOperator(dataElementFlow.getAggregationOperator());
            dataElement.setFormName(dataElementFlow.getFormName());
            dataElement.setNumberType(dataElementFlow.getNumberType());
            dataElement.setDomainType(dataElementFlow.getDomainType());
            dataElement.setDimension(dataElementFlow.getDimension());
            dataElement.setDisplayFormName(dataElementFlow.getDisplayFormName());
            dataElement.setOptionSet(OptionSetFlow.MAPPER
                    .mapToModel(dataElementFlow.getOptionSet()));
            return dataElement;
        }

        @Override
        public Class<DataElement> getModelTypeClass() {
            return DataElement.class;
        }

        @Override
        public Class<DataElementFlow> getDatabaseEntityTypeClass() {
            return DataElementFlow.class;
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
