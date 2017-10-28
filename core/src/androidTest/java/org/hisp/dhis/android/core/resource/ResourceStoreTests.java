package org.hisp.dhis.android.core.resource;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.resource.ResourceModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ResourceStoreTests extends AbsStoreTestCase {
    private static final ResourceModel.Type RESOURCE_TYPE = ResourceModel.Type.OPTION_SET;

    private static final String[] PROJECTION = {Columns.RESOURCE_TYPE, Columns.LAST_SYNCED};

    private ResourceStore store;

    private final Date date;
    //    private final Date date2;
    private final String dateString;
    private static final String dateString2 = "2001-01-18T13:39:00.000";

    public ResourceStoreTests() throws ParseException {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
//        this.date2 = BaseIdentifiableObject.DATE_FORMAT.parse(dateString2);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.store = new ResourceStoreImpl(databaseAdapter());
    }

    @Test
    public void insert_shouldPersistResourceInDatabase() {
        long rowId = store.insert(RESOURCE_TYPE.name(), date);
        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, dateString).isExhausted();
    }

    @Test
    public void insert_shouldPersistResourceInDatabaseWithoutLastSynced() {
        long rowId = store.insert(RESOURCE_TYPE.name(), null);
        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, null).isExhausted();
    }


    @Test
    public void update_shouldUpdateExisting() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE.name(), dateString2));

        int returnValue = store.update(RESOURCE_TYPE.name(), date, RESOURCE_TYPE.name());

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(returnValue).isNotNull();
        assertThat(returnValue).isEqualTo(1);
        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, dateString).isExhausted();
    }

    @Test
    public void update_shouldNotInsert() {
        int returnValue = store.update(RESOURCE_TYPE.name(), date, RESOURCE_TYPE.name());

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(returnValue).isEqualTo(0);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void update_shouldNotUpdate_WithoutWhere() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE.name(), dateString));

        int returnValue = store.update(RESOURCE_TYPE.name(), date, "");

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(returnValue).isNotNull();
        assertThat(returnValue).isEqualTo(0);
        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, dateString).isExhausted();
    }

    @Test
    public void delete_shouldDeleteRow() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE.name(), dateString));
        int returnValue = store.delete(RESOURCE_TYPE.name());

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(returnValue).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void getLastUpdated_shouldReturnCorrectLastUpdated() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE.name(), dateString));

        String lastUpdated = store.getLastUpdated(RESOURCE_TYPE);

        assertThat(lastUpdated).isNotNull();
        assertThat(lastUpdated).isEqualTo(dateString);
    }

    @Test
    public void getLastUpdated_shouldReturnNull_IfNotExisting() {
        String lastUpdated = store.getLastUpdated(RESOURCE_TYPE);
        assertThat(lastUpdated).isNull();
    }

    private ContentValues createResource(String resourceName, String dateString) {
        ContentValues resource = new ContentValues();
        resource.put(Columns.RESOURCE_TYPE, resourceName);
        resource.put(Columns.LAST_SYNCED, dateString);
        return resource;
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_null_uid() {
        store.insert(null, date);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_uid() {
        store.update(null, date, RESOURCE_TYPE.name());
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_programRule() {
        store.update(RESOURCE_TYPE.name(), date, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void delete_null_uid() {
        store.delete(null);
    }
}
