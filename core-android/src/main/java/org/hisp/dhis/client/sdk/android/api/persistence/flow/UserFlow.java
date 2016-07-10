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

import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.user.User;

@Table(database = DbDhis.class)
@TableEndpoint(name = UserFlow.NAME, contentProvider = DbDhis.class)
public final class UserFlow extends BaseIdentifiableObjectFlow {

    public static final String NAME = "UserFlow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(DbDhis.AUTHORITY, NAME);

    public static final Mapper<User, UserFlow> MAPPER = new UserMapper();

    public UserFlow() {
        // empty constructor
    }

    private static class UserMapper extends AbsMapper<User, UserFlow> {

        public UserMapper() {
            // empty constructor
        }

        @Override
        public UserFlow mapToDatabaseEntity(User user) {
            if (user == null) {
                return null;
            }

            UserFlow userFlow = new UserFlow();
            userFlow.setId(user.getId());
            userFlow.setUId(user.getUId());
            userFlow.setCreated(user.getCreated());
            userFlow.setLastUpdated(user.getLastUpdated());
            userFlow.setName(user.getName());
            userFlow.setDisplayName(user.getDisplayName());
            userFlow.setAccess(user.getAccess());
            return userFlow;
        }

        @Override
        public User mapToModel(UserFlow userFlow) {
            if (userFlow == null) {
                return null;
            }

            User user = new User();
            user.setId(userFlow.getId());
            user.setUId(userFlow.getUId());
            user.setCreated(userFlow.getCreated());
            user.setLastUpdated(userFlow.getLastUpdated());
            user.setName(userFlow.getName());
            user.setDisplayName(userFlow.getDisplayName());
            user.setAccess(userFlow.getAccess());
            return user;
        }

        @Override
        public Class<User> getModelTypeClass() {
            return User.class;
        }

        @Override
        public Class<UserFlow> getDatabaseEntityTypeClass() {
            return UserFlow.class;
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
