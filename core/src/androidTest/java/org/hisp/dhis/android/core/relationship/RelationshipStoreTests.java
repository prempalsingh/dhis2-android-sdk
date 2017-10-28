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

package org.hisp.dhis.android.core.relationship;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityInstanceUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class RelationshipStoreTests extends AbsStoreTestCase {
    // relationship attributes:
    private static final String TRACKED_ENTITY_INSTANCE_A = "test_tei_a_uid";
    private static final String TRACKED_ENTITY_INSTANCE_B = "test_tei_b_uid";

    // relationshipType (foreign key):
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE = "test_relationship_type_uid";

    // relationship projection:
    private static final String[] RELATIONSHIP_PROJECTION = {
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A,
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B,
            RelationshipModel.Columns.RELATIONSHIP_TYPE
    };

    private RelationshipStore store;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        store = new RelationshipStoreImpl(databaseAdapter());

        // Insert RelationshipType in RelationshipType table, such that it can be used as foreign key:
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(
                RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE
        );

        ContentValues trackedEntity = CreateTrackedEntityUtils.create(1L, "test_tracked_entity_uid");
        ContentValues orgUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, "test_organisation_unit_uid");

        ContentValues trackedEntityInstanceA = CreateTrackedEntityInstanceUtils.create(
                TRACKED_ENTITY_INSTANCE_A, "test_organisation_unit_uid", "test_tracked_entity_uid");
        ContentValues trackedEntityInstanceB = CreateTrackedEntityInstanceUtils.create(
                TRACKED_ENTITY_INSTANCE_B, "test_organisation_unit_uid", "test_tracked_entity_uid");

        database().insert(OrganisationUnitModel.TABLE, null, orgUnit);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstanceA);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstanceB);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
    }

    @Test
    public void insert_shouldPersistRelationshipInDatabase() {
        long rowId = store.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        );

        Cursor cursor = database().query(RelationshipModel.TABLE,
                RELATIONSHIP_PROJECTION, null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistDeferrableRelationshipInDatabase() {
        final String deferredRelationshipType = "deferredRelationshipType";

        database().beginTransaction();
        long rowId = store.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                deferredRelationshipType
        );

        ContentValues relationshipType = CreateRelationshipTypeUtils.create(2L, deferredRelationshipType);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(
                RelationshipModel.TABLE,
                RELATIONSHIP_PROJECTION,
                null, null, null, null, null, null
        );

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                deferredRelationshipType
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_shouldNotPersistRelationshipNullableInDatabase() {
        //Insert foreign keys in their respective tables:
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(
                RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE
        );
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);

        long rowId = store.insert(null, null, RELATIONSHIP_TYPE);
        Cursor cursor = database().query(RelationshipModel.TABLE, RELATIONSHIP_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(null, null, RELATIONSHIP_TYPE).isExhausted();
    }

    @Test
    public void delete_shouldDeleteRelationshipWhenDeletingRelationshipType() {
        store.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        );

        database().delete(RelationshipTypeModel.TABLE, RelationshipTypeModel.Columns.UID + "=?",
                new String[]{RELATIONSHIP_TYPE});

        Cursor cursor = database().query(RelationshipModel.TABLE, RELATIONSHIP_PROJECTION,
                null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteRelationshipWhenDeletingTeiA() {
        store.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        );

        database().delete(TrackedEntityInstanceModel.TABLE, TrackedEntityInstanceModel.Columns.UID + "=?",
                new String[]{TRACKED_ENTITY_INSTANCE_A});

        Cursor cursor = database().query(RelationshipModel.TABLE, RELATIONSHIP_PROJECTION,
                null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteRelationshipWhenDeletingTeiB() {
        store.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        );

        database().delete(TrackedEntityInstanceModel.TABLE, TrackedEntityInstanceModel.Columns.UID + "=?",
                new String[]{TRACKED_ENTITY_INSTANCE_B});

        Cursor cursor = database().query(RelationshipModel.TABLE, RELATIONSHIP_PROJECTION,
                null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistRelationshipWithInvalidRelationshipTypeForeignKey() {
        store.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                "wrong" //supply the wrong uid
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_null_relationshipType() {
        store.insert(TRACKED_ENTITY_INSTANCE_A, TRACKED_ENTITY_INSTANCE_B, null);
    }
}
