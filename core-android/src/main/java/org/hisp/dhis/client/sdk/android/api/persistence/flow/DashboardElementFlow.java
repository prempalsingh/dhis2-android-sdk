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
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;

@Table(database = DbDhis.class)
@TableEndpoint(name = DashboardElementFlow.NAME, contentProvider = DbDhis.class)
public final class DashboardElementFlow extends BaseIdentifiableObjectFlow {

    public static final String NAME = "DashboardElementFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    public static final Mapper<DashboardElement, DashboardElementFlow>
            MAPPER = new ElementMapper();

    static final String DASHBOARD_ITEM_KEY = "dashboardItem";

    @Column
    @NotNull
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = DASHBOARD_ITEM_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID)
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    DashboardItemFlow dashboardItem;

    public DashboardElementFlow() {
    }

    public DashboardItemFlow getDashboardItem() {
        return dashboardItem;
    }

    public void setDashboardItem(DashboardItemFlow dashboardItem) {
        this.dashboardItem = dashboardItem;
    }

    private static class ElementMapper extends AbsMapper<DashboardElement, DashboardElementFlow> {

        @Override
        public DashboardElementFlow mapToDatabaseEntity(DashboardElement dashboardElement) {
            if (dashboardElement == null) {
                return null;
            }

            DashboardElementFlow dashboardElementFlow = new DashboardElementFlow();
            dashboardElementFlow.setId(dashboardElement.getId());
            dashboardElementFlow.setUId(dashboardElement.getUId());
            dashboardElementFlow.setCreated(dashboardElement.getCreated());
            dashboardElementFlow.setLastUpdated(dashboardElement.getLastUpdated());
            dashboardElementFlow.setAccess(dashboardElement.getAccess());
            dashboardElementFlow.setName(dashboardElement.getName());
            dashboardElementFlow.setDisplayName(dashboardElement.getDisplayName());
            dashboardElementFlow.setDashboardItem(DashboardItemFlow.MAPPER
                    .mapToDatabaseEntity(dashboardElement.getDashboardItem()));
            return dashboardElementFlow;
        }

        @Override
        public DashboardElement mapToModel(DashboardElementFlow dashboardElementFlow) {
            if (dashboardElementFlow == null) {
                return null;
            }

            DashboardElement dashboardElement = new DashboardElement();
            dashboardElement.setId(dashboardElementFlow.getId());
            dashboardElement.setUId(dashboardElementFlow.getUId());
            dashboardElement.setCreated(dashboardElementFlow.getCreated());
            dashboardElement.setLastUpdated(dashboardElementFlow.getLastUpdated());
            dashboardElement.setAccess(dashboardElementFlow.getAccess());
            dashboardElement.setName(dashboardElementFlow.getName());
            dashboardElement.setDisplayName(dashboardElementFlow.getDisplayName());
            dashboardElement.setDashboardItem(DashboardItemFlow.MAPPER
                    .mapToModel(dashboardElementFlow.getDashboardItem()));
            return dashboardElement;
        }

        @Override
        public Class<DashboardElement> getModelTypeClass() {
            return DashboardElement.class;
        }

        @Override
        public Class<DashboardElementFlow> getDatabaseEntityTypeClass() {
            return DashboardElementFlow.class;
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