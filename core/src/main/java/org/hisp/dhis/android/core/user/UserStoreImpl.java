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

package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class UserStoreImpl implements UserStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + UserModel.TABLE + " (" +
            UserModel.Columns.UID + ", " +
            UserModel.Columns.CODE + ", " +
            UserModel.Columns.NAME + ", " +
            UserModel.Columns.DISPLAY_NAME + ", " +
            UserModel.Columns.CREATED + ", " +
            UserModel.Columns.LAST_UPDATED + ", " +
            UserModel.Columns.BIRTHDAY + ", " +
            UserModel.Columns.EDUCATION + ", " +
            UserModel.Columns.GENDER + ", " +
            UserModel.Columns.JOB_TITLE + ", " +
            UserModel.Columns.SURNAME + ", " +
            UserModel.Columns.FIRST_NAME + ", " +
            UserModel.Columns.INTRODUCTION + ", " +
            UserModel.Columns.EMPLOYER + ", " +
            UserModel.Columns.INTERESTS + ", " +
            UserModel.Columns.LANGUAGES + ", " +
            UserModel.Columns.EMAIL + ", " +
            UserModel.Columns.PHONE_NUMBER + ", " +
            UserModel.Columns.NATIONALITY +
            ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + UserModel.TABLE + " SET " +
            UserModel.Columns.UID + " =?, " +
            UserModel.Columns.CODE + " =?, " +
            UserModel.Columns.NAME + " =?, " +
            UserModel.Columns.DISPLAY_NAME + " =?, " +
            UserModel.Columns.CREATED + " =?, " +
            UserModel.Columns.LAST_UPDATED + " =?, " +
            UserModel.Columns.BIRTHDAY + " =?, " +
            UserModel.Columns.EDUCATION + " =?, " +
            UserModel.Columns.GENDER + " =?, " +
            UserModel.Columns.JOB_TITLE + " =?, " +
            UserModel.Columns.SURNAME + " =?, " +
            UserModel.Columns.FIRST_NAME + " =?, " +
            UserModel.Columns.INTRODUCTION + " =?, " +
            UserModel.Columns.EMPLOYER + " =?, " +
            UserModel.Columns.INTERESTS + " =?, " +
            UserModel.Columns.LANGUAGES + " =?, " +
            UserModel.Columns.EMAIL + " =?, " +
            UserModel.Columns.PHONE_NUMBER + " =?, " +
            UserModel.Columns.NATIONALITY + " =? " + " WHERE " +
            UserModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + UserModel.TABLE +
            " WHERE " + UserModel.Columns.UID + " =?;";

    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    public UserStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid, @Nullable String code,
            @Nullable String name, @Nullable String displayName,
            @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String birthday, @Nullable String education, @Nullable String gender,
            @Nullable String jobTitle, @Nullable String surname, @Nullable String firstName,
            @Nullable String introduction, @Nullable String employer, @Nullable String interests,
            @Nullable String languages, @Nullable String email, @Nullable String phoneNumber,
            @Nullable String nationality) {

        isNull(uid);
        bindArguments(
                insertStatement, uid, code, name,
                displayName, created, lastUpdated, birthday, education, gender,
                jobTitle, surname, firstName, introduction, employer, interests,
                languages, email, phoneNumber, nationality
        );

        Long insert = databaseAdapter.executeInsert(UserModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(
            @NonNull String uid, @Nullable String code,
            @Nullable String name, @Nullable String displayName,
            @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String birthday, @Nullable String education, @Nullable String gender,
            @Nullable String jobTitle, @Nullable String surname, @Nullable String firstName,
            @Nullable String introduction, @Nullable String employer, @Nullable String interests,
            @Nullable String languages, @Nullable String email, @Nullable String phoneNumber,
            @Nullable String nationality, @NonNull String whereUid) {

        isNull(uid);
        isNull(whereUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, birthday, education, gender,
                jobTitle, surname, firstName, introduction, employer, interests, languages, email, phoneNumber,
                nationality
        );
        sqLiteBind(updateStatement, 20, whereUid);

        int update = databaseAdapter.executeUpdateDelete(UserModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        sqLiteBind(deleteStatement, 1, uid);

        int delete = databaseAdapter.executeUpdateDelete(UserModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(UserModel.TABLE);
    }

    private void bindArguments(SQLiteStatement sqLiteStatement, @NonNull String uid, @Nullable String code,
                               @Nullable String name, @Nullable String displayName,
                               @Nullable Date created, @Nullable Date lastUpdated,
                               @Nullable String birthday, @Nullable String education, @Nullable String gender,
                               @Nullable String jobTitle, @Nullable String surname, @Nullable String firstName,
                               @Nullable String introduction, @Nullable String employer, @Nullable String interests,
                               @Nullable String languages, @Nullable String email, @Nullable String phoneNumber,
                               @Nullable String nationality) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, birthday);
        sqLiteBind(sqLiteStatement, 8, education);
        sqLiteBind(sqLiteStatement, 9, gender);
        sqLiteBind(sqLiteStatement, 10, jobTitle);
        sqLiteBind(sqLiteStatement, 11, surname);
        sqLiteBind(sqLiteStatement, 12, firstName);
        sqLiteBind(sqLiteStatement, 13, introduction);
        sqLiteBind(sqLiteStatement, 14, employer);
        sqLiteBind(sqLiteStatement, 15, interests);
        sqLiteBind(sqLiteStatement, 16, languages);
        sqLiteBind(sqLiteStatement, 17, email);
        sqLiteBind(sqLiteStatement, 18, phoneNumber);
        sqLiteBind(sqLiteStatement, 19, nationality);
    }
}
