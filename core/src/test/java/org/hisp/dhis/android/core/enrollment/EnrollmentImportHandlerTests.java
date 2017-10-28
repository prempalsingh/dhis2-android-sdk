package org.hisp.dhis.android.core.enrollment;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.imports.ImportEvent;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EnrollmentImportHandlerTests {

    @Mock
    private EnrollmentStore enrollmentStore;

    @Mock
    private EventImportHandler eventImportHandler;

    @Mock
    private ImportEvent importEvent;

    @Mock
    private ImportSummary eventSummary;

    @Mock
    private ImportSummary importSummary;

    // object to test
    private EnrollmentImportHandler enrollmentImportHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);


        enrollmentImportHandler = new EnrollmentImportHandler(enrollmentStore, eventImportHandler);
    }

    @Test
    public void doNothing_shouldDoNothingWhenPassingNullArguments() throws Exception {
        enrollmentImportHandler.handleEnrollmentImportSummary(null);

        verify(enrollmentStore, never()).setState(anyString(), any(State.class));
    }

    @Test
    public void setState_shouldUpdateEnrollmentStateSuccess() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_enrollment_uid");

        enrollmentImportHandler.handleEnrollmentImportSummary(Collections.singletonList(importSummary));

        verify(enrollmentStore, times(1)).setState("test_enrollment_uid", State.SYNCED);
    }

    @Test
    public void setState_shouldUpdateEnrollmentStateError() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.ERROR);
        when(importSummary.reference()).thenReturn("test_enrollment_uid");

        enrollmentImportHandler.handleEnrollmentImportSummary(Collections.singletonList(importSummary));

        verify(enrollmentStore, times(1)).setState("test_enrollment_uid", State.ERROR);
    }

    @Test
    public void setState_shouldUpdateEnrollmentStateSuccessAndUpdateNestedEvent() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_enrollment_uid");
        when(importSummary.importEvent()).thenReturn(importEvent);

        List<ImportSummary> eventSummaries = Collections.singletonList(eventSummary);
        when(importEvent.importSummaries()).thenReturn(eventSummaries);


        enrollmentImportHandler.handleEnrollmentImportSummary(Collections.singletonList(importSummary));

        verify(enrollmentStore, times(1)).setState("test_enrollment_uid", State.SYNCED);
        verify(eventImportHandler, times(1)).handleEventImportSummaries(eventSummaries);

    }
}
