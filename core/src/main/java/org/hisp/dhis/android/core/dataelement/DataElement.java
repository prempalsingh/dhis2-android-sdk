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

package org.hisp.dhis.android.core.dataelement;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.option.OptionSet;

import java.util.Date;

@AutoValue
public abstract class DataElement extends BaseNameableObject {
    private final static String VALUE_TYPE = "valueType";
    private final static String ZERO_IS_SIGNIFICANT = "zeroIsSignificant";
    private final static String AGGREGATION_TYPE = "aggregationType";
    private final static String FORM_NAME = "formName";
    private final static String NUMBER_TYPE = "numberType";
    private final static String DOMAIN_TYPE = "domainType";
    private final static String DIMENSION = "dimension";
    private final static String DISPLAY_FORM_NAME = "displayFormName";
    private final static String OPTION_SET = "optionSet";

    public static final Field<DataElement, String> uid = Field.create(UID);
    public static final Field<DataElement, String> code = Field.create(CODE);
    public static final Field<DataElement, String> name = Field.create(NAME);
    public static final Field<DataElement, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<DataElement, String> created = Field.create(CREATED);
    public static final Field<DataElement, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<DataElement, String> shortName = Field.create(SHORT_NAME);
    public static final Field<DataElement, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<DataElement, String> description = Field.create(DESCRIPTION);
    public static final Field<DataElement, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    public static final Field<DataElement, Boolean> deleted = Field.create(DELETED);

    public static final Field<DataElement, ValueType> valueType = Field.create(VALUE_TYPE);
    public static final Field<DataElement, Boolean> zeroIsSignificant = Field.create(ZERO_IS_SIGNIFICANT);
    public static final Field<DataElement, String> aggregationType = Field.create(AGGREGATION_TYPE);
    public static final Field<DataElement, String> formName = Field.create(FORM_NAME);
    public static final Field<DataElement, String> numberType = Field.create(NUMBER_TYPE);
    public static final Field<DataElement, String> domainType = Field.create(DOMAIN_TYPE);
    public static final Field<DataElement, String> dimension = Field.create(DIMENSION);
    public static final Field<DataElement, String> displayFormName = Field.create(DISPLAY_FORM_NAME);
    public static final NestedField<DataElement, OptionSet> optionSet = NestedField.create(OPTION_SET);

    @Nullable
    @JsonProperty(VALUE_TYPE)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty(ZERO_IS_SIGNIFICANT)
    public abstract Boolean zeroIsSignificant();

    @Nullable
    @JsonProperty(AGGREGATION_TYPE)
    public abstract String aggregationType();

    @Nullable
    @JsonProperty(FORM_NAME)
    public abstract String formName();

    @Nullable
    @JsonProperty(NUMBER_TYPE)
    public abstract String numberType();

    @Nullable
    @JsonProperty(DOMAIN_TYPE)
    public abstract String domainType();

    @Nullable
    @JsonProperty(DIMENSION)
    public abstract String dimension();

    @Nullable
    @JsonProperty(DISPLAY_FORM_NAME)
    public abstract String displayFormName();

    @Nullable
    @JsonProperty(OPTION_SET)
    public abstract OptionSet optionSet();

    @JsonCreator
    public static DataElement create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(SHORT_NAME) String shortName,
            @JsonProperty(DISPLAY_SHORT_NAME) String displayShortName,
            @JsonProperty(DESCRIPTION) String description,
            @JsonProperty(DISPLAY_DESCRIPTION) String displayDescription,
            @JsonProperty(VALUE_TYPE) ValueType valueType,
            @JsonProperty(ZERO_IS_SIGNIFICANT) Boolean zeroIsSignificant,
            @JsonProperty(AGGREGATION_TYPE) String aggregationType,
            @JsonProperty(FORM_NAME) String formName,
            @JsonProperty(NUMBER_TYPE) String numberType,
            @JsonProperty(DOMAIN_TYPE) String domainType,
            @JsonProperty(DIMENSION) String dimension,
            @JsonProperty(DISPLAY_FORM_NAME) String displayFormName,
            @JsonProperty(OPTION_SET) OptionSet optionSet,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_DataElement(uid, code, name,
                displayName, created, lastUpdated, deleted,
                shortName, displayShortName, description, displayDescription, valueType,
                zeroIsSignificant, aggregationType, formName, numberType,
                domainType, dimension, displayFormName, optionSet);

    }
}
