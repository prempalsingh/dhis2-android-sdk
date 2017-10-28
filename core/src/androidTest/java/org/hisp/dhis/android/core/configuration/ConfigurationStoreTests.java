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

package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class ConfigurationStoreTests extends AbsStoreTestCase {
    private static final String[] PROJECTION = {ConfigurationModel.Columns.ID, ConfigurationModel.Columns.SERVER_URL};

    private ConfigurationStore store;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        store = new ConfigurationStoreImpl(databaseAdapter());
    }

    @Test
    public void save_shouldPersistRowInDatabase() {
        long rowId = store.save("http://testserver.org/");

        Cursor cursor = database().query(ConfigurationModel.CONFIGURATION,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "http://testserver.org/")
                .isExhausted();
    }

    @Test
    public void save_shouldNotThrowOnConflict() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "http://testserver.org/");

        database().insert(ConfigurationModel.CONFIGURATION, null, contentValues);

        // trying to configure configuration with server url (which is set to be unique in the table)
        long rowId = store.save("http://testserver.org/");

        Cursor cursor = database().query(ConfigurationModel.CONFIGURATION,
                PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "http://testserver.org/")
                .isExhausted();
    }

    @Test
    public void save_shouldNotPersistMoreThatOneUrl() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "http://testserver.org/");

        database().insert(ConfigurationModel.CONFIGURATION, null, contentValues);

        long rowId = store.save("test_another_url");

        Cursor cursor = database().query(ConfigurationModel.CONFIGURATION,
                PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "test_another_url")
                .isExhausted();
    }

    @Test
    public void delete_shouldDeletePersistedRows() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "http://testserver.org/");

        database().insert(ConfigurationModel.CONFIGURATION, null, contentValues);

        long deleted = store.delete();

        Cursor cursor = database().query(ConfigurationModel.CONFIGURATION,
                PROJECTION, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldNotFail_ifNoRowsArePersisted() {
        long deleted = store.delete();
        assertThat(deleted).isEqualTo(0);
    }

    @Test
    public void query_shouldReturnPersistedRow() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "http://testserver.org/");

        database().insert(ConfigurationModel.CONFIGURATION, null, contentValues);

        ConfigurationModel persistedConfiguration = store.query();
        assertThat(persistedConfiguration.id()).isEqualTo(1L);
        assertThat(persistedConfiguration.serverUrl().toString()).isEqualTo("http://testserver.org/");
    }

    @Test
    public void query_shouldReturnNull_ifNoRowsArePersisted() {
        ConfigurationModel persistedConfiguration = store.query();
        assertThat(persistedConfiguration).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_null_uid() {
        store.save(null);
    }
}
