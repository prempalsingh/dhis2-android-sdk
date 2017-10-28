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

package org.hisp.dhis.android.core.event;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.EventModel.Columns;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.NPathComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.AvoidInstantiatingObjectsInLoops"
})
public class EventStoreImpl implements EventStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + EventModel.TABLE + " (" +
            Columns.UID + ", " +
            Columns.ENROLLMENT_UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.CREATED_AT_CLIENT + ", " +
            Columns.LAST_UPDATED_AT_CLIENT + ", " +
            Columns.STATUS + ", " +
            Columns.LATITUDE + ", " +
            Columns.LONGITUDE + ", " +
            Columns.PROGRAM + ", " +
            Columns.PROGRAM_STAGE + ", " +
            Columns.ORGANISATION_UNIT + ", " +
            Columns.EVENT_DATE + ", " +
            Columns.COMPLETE_DATE + ", " +
            Columns.DUE_DATE + ", " +
            Columns.STATE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + EventModel.TABLE + " SET " +
            Columns.UID + " =? , " +
            Columns.ENROLLMENT_UID + " =? , " +
            Columns.CREATED + " =? , " +
            Columns.LAST_UPDATED + " =? ," +
            Columns.CREATED_AT_CLIENT + " =? , " +
            Columns.LAST_UPDATED_AT_CLIENT + " =? , " +
            Columns.STATUS + " =? ," +
            Columns.LATITUDE + " =? ," +
            Columns.LONGITUDE + " =? ," +
            Columns.PROGRAM + " =? ," +
            Columns.PROGRAM_STAGE + " =? , " +
            Columns.ORGANISATION_UNIT + " =?, " +
            Columns.EVENT_DATE + " =? , " +
            Columns.COMPLETE_DATE + " =? , " +
            Columns.DUE_DATE + " =? , " +
            Columns.STATE + " =? " +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String UPDATE_STATE_STATEMENT = "UPDATE " + EventModel.TABLE + " SET " +
            Columns.STATE + " =? " +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " +
            EventModel.TABLE + " WHERE " +
            Columns.UID + " =?;";

    private static final String QUERY_EVENTS_ATTACHED_TO_ENROLLMENTS = "SELECT " +
            "  Event.uid, " +
            "  Event.created, " +
            "  Event.lastUpdated, " +
            "  Event.createdAtClient, " +
            "  Event.lastUpdatedAtClient, " +
            "  Event.status, " +
            "  Event.latitude, " +
            "  Event.longitude, " +
            "  Event.program, " +
            "  Event.programStage, " +
            "  Event.organisationUnit, " +
            "  Event.enrollment, " +
            "  Event.eventDate, " +
            "  Event.completedDate, " +
            "  Event.dueDate " +
            "FROM (Event INNER JOIN Enrollment ON Event.enrollment = Enrollment.uid " +
            "  INNER JOIN TrackedEntityInstance ON Enrollment.trackedEntityInstance = TrackedEntityInstance.uid) " +
            "WHERE TrackedEntityInstance.state = 'TO_POST' OR TrackedEntityInstance.state = 'TO_UPDATE' " +
            "      OR Enrollment.state = 'TO_POST' OR Enrollment.state = 'TO_UPDATE' OR Event.state = 'TO_POST' " +
            "OR Event.state = 'TO_UPDATE';";

    private static final String QUERY_SINGLE_EVENTS = "SELECT " +
            "  Event.uid, " +
            "  Event.created, " +
            "  Event.lastUpdated, " +
            "  Event.createdAtClient, " +
            "  Event.lastUpdatedAtClient, " +
            "  Event.status, " +
            "  Event.latitude, " +
            "  Event.longitude, " +
            "  Event.program, " +
            "  Event.programStage, " +
            "  Event.organisationUnit, " +
            "  Event.eventDate, " +
            "  Event.completedDate, " +
            "  Event.dueDate " +
            "  FROM Event " +
            "WHERE Event.enrollment ISNULL AND (Event.state = 'TO_POST' OR Event.state = 'TO_UPDATE')";


    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final SQLiteStatement setStateStatement;
    private final DatabaseAdapter databaseAdapter;

    public EventStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
        this.setStateStatement = databaseAdapter.compileStatement(UPDATE_STATE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String enrollmentUid,
                       @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
                       @Nullable EventStatus status, @Nullable String latitude,
                       @Nullable String longitude, @NonNull String program,
                       @NonNull String programStage, @NonNull String organisationUnit,
                       @Nullable Date eventDate, @Nullable Date completedDate,
                       @Nullable Date dueDate, @Nullable State state) {
        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, enrollmentUid);
        sqLiteBind(insertStatement, 3, created);
        sqLiteBind(insertStatement, 4, lastUpdated);
        sqLiteBind(insertStatement, 5, createdAtClient);
        sqLiteBind(insertStatement, 6, lastUpdatedAtClient);
        sqLiteBind(insertStatement, 7, status);
        sqLiteBind(insertStatement, 8, latitude);
        sqLiteBind(insertStatement, 9, longitude);
        sqLiteBind(insertStatement, 10, program);
        sqLiteBind(insertStatement, 11, programStage);
        sqLiteBind(insertStatement, 12, organisationUnit);
        sqLiteBind(insertStatement, 13, eventDate);
        sqLiteBind(insertStatement, 14, completedDate);
        sqLiteBind(insertStatement, 15, dueDate);
        sqLiteBind(insertStatement, 16, state);

        long insert = databaseAdapter.executeInsert(EventModel.TABLE, insertStatement);

        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String enrollmentUid,
                      @NonNull Date created, @NonNull Date lastUpdated,
                      @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
                      @NonNull EventStatus eventStatus, @Nullable String latitude,
                      @Nullable String longitude, @NonNull String program,
                      @NonNull String programStage, @NonNull String organisationUnit,
                      @NonNull Date eventDate, @Nullable Date completedDate,
                      @Nullable Date dueDate, @NonNull State state,
                      @NonNull String whereEventUid) {

        sqLiteBind(updateStatement, 1, uid);
        sqLiteBind(updateStatement, 2, enrollmentUid);
        sqLiteBind(updateStatement, 3, created);
        sqLiteBind(updateStatement, 4, lastUpdated);
        sqLiteBind(updateStatement, 5, createdAtClient);
        sqLiteBind(updateStatement, 6, lastUpdatedAtClient);
        sqLiteBind(updateStatement, 7, eventStatus);
        sqLiteBind(updateStatement, 8, latitude);
        sqLiteBind(updateStatement, 9, longitude);
        sqLiteBind(updateStatement, 10, program);
        sqLiteBind(updateStatement, 11, programStage);
        sqLiteBind(updateStatement, 12, organisationUnit);
        sqLiteBind(updateStatement, 13, eventDate);
        sqLiteBind(updateStatement, 14, completedDate);
        sqLiteBind(updateStatement, 15, dueDate);
        sqLiteBind(updateStatement, 16, state);

        // bind the where clause
        sqLiteBind(updateStatement, 17, whereEventUid);

        int rowId = databaseAdapter.executeUpdateDelete(EventModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return rowId;
    }

    @Override
    public int delete(@NonNull String uid) {
        sqLiteBind(deleteStatement, 1, uid);

        int rowId = deleteStatement.executeUpdateDelete();
        deleteStatement.clearBindings();

        return rowId;
    }

    @Override
    public int setState(@NonNull String uid, @NonNull State state) {
        sqLiteBind(setStateStatement, 1, state);
        sqLiteBind(setStateStatement, 2, uid);

        int update = databaseAdapter.executeUpdateDelete(EventModel.TABLE, setStateStatement);
        setStateStatement.clearBindings();

        return update;
    }

    @Override
    public Map<String, List<Event>> queryEventsAttachedToEnrollmentToPost() {
        Cursor cursor = databaseAdapter.query(QUERY_EVENTS_ATTACHED_TO_ENROLLMENTS);
        Map<String, List<Event>> events = new HashMap<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String uid = cursor.getString(0);
                    Date created = cursor.getString(1) == null ? null : parse(cursor.getString(1));
                    Date lastUpdated = cursor.getString(2) == null ? null : parse(cursor.getString(2));
                    String createdAtClient = cursor.getString(3) == null ? null : cursor.getString(3);
                    String lastUpdatedAtClient = cursor.getString(4) == null ? null : cursor.getString(4);
                    EventStatus eventStatus =
                            cursor.getString(5) == null ? null : EventStatus.valueOf(cursor.getString(5));
                    String latitude = cursor.getString(6) == null ? null : cursor.getString(6);
                    String longitude = cursor.getString(7) == null ? null : cursor.getString(7);
                    String program = cursor.getString(8) == null ? null : cursor.getString(8);
                    String programStage = cursor.getString(9) == null ? null : cursor.getString(9);
                    String organisationUnit = cursor.getString(10) == null ? null : cursor.getString(10);
                    String enrollment = cursor.getString(11) == null ? null : cursor.getString(11);
                    Date eventDate = cursor.getString(12) == null ? null : parse(cursor.getString(12));
                    Date completedDate = cursor.getString(13) == null ? null : parse(cursor.getString(13));
                    Date dueDate = cursor.getString(14) == null ? null : parse(cursor.getString(14));

                    if (events.get(enrollment) == null) {
                        events.put(enrollment, new ArrayList<Event>());
                    }

                    events.get(enrollment).add(Event.create(
                            uid, enrollment, created, lastUpdated, createdAtClient, lastUpdatedAtClient,
                            program, programStage, organisationUnit, eventDate, eventStatus,
                            Coordinates.create(latitude, longitude), completedDate,
                            dueDate, false, null));

                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return events;
    }

    @Override
    public List<Event> querySingleEventsToPost() {
        Cursor cursor = databaseAdapter.query(QUERY_SINGLE_EVENTS);
        List<Event> events = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    String uid = cursor.getString(0);
                    Date created = cursor.getString(1) == null ? null : parse(cursor.getString(1));
                    Date lastUpdated = cursor.getString(2) == null ? null : parse(cursor.getString(2));
                    String createdAtClient = cursor.getString(3) == null ? null : cursor.getString(3);
                    String lastUpdatedAtClient = cursor.getString(4) == null ? null : cursor.getString(4);
                    EventStatus eventStatus =
                            cursor.getString(5) == null ? null : EventStatus.valueOf(cursor.getString(5));
                    String latitude = cursor.getString(6) == null ? null : cursor.getString(6);
                    String longitude = cursor.getString(7) == null ? null : cursor.getString(7);
                    String program = cursor.getString(8) == null ? null : cursor.getString(8);
                    String programStage = cursor.getString(9) == null ? null : cursor.getString(9);
                    String organisationUnit = cursor.getString(10) == null ? null : cursor.getString(10);
                    Date eventDate = cursor.getString(11) == null ? null : parse(cursor.getString(11));
                    Date completedDate = cursor.getString(12) == null ? null : parse(cursor.getString(12));
                    Date dueDate = cursor.getString(13) == null ? null : parse(cursor.getString(13));

                    events.add(Event.create(
                            uid, null, created, lastUpdated, createdAtClient, lastUpdatedAtClient,
                            program, programStage, organisationUnit, eventDate, eventStatus,
                            Coordinates.create(latitude, longitude), completedDate,
                            dueDate, false, null));

                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return events;
    }

}
