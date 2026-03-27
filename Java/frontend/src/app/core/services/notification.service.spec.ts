import { TestBed } from '@angular/core/testing';
import { NotificationService, Alert, Severity } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should_emitEmptyAlertArray_when_serviceIsInitialized', (done) => {
    service.alerts$.subscribe((alerts) => {
      expect(alerts).toEqual([]);
      done();
    });
  });

  it('should_addAlertToObservable_when_handleAlertIsCalled', (done) => {
    const alertJson = JSON.stringify({
      severity: 'INFO',
      title: 'Test Alert',
      message: 'This is a test',
      timestamp: Date.now()
    });

    service.handleAlert(alertJson);

    service.alerts$.subscribe((alerts) => {
      if (alerts.length > 0) {
        expect(alerts.length).toBe(1);
        expect(alerts[0].severity).toBe('INFO');
        expect(alerts[0].title).toBe('Test Alert');
        expect(alerts[0].message).toBe('This is a test');
        expect(alerts[0].id).toBeDefined();
        done();
      }
    });
  });

  it('should_removeAlertById_when_removeAlertIsCalled', () => {
    const alertJson = JSON.stringify({
      severity: 'INFO',
      title: 'Test Alert',
      message: 'Test message',
      timestamp: Date.now()
    });

    service.handleAlert(alertJson);
    let currentAlerts = service.alerts$.value;
    expect(currentAlerts.length).toBe(1);
    const alertId = currentAlerts[0].id;

    service.removeAlert(alertId);
    currentAlerts = service.alerts$.value;
    expect(currentAlerts.length).toBe(0);
  });

  it('should_parseAlertJsonCorrectly_when_handleAlertReceivesValidJson', (done) => {
    const alertJson = JSON.stringify({
      severity: 'DANGER',
      title: 'Critical Issue',
      message: 'An error occurred',
      timestamp: 1234567890
    });

    service.handleAlert(alertJson);

    service.alerts$.subscribe((alerts) => {
      if (alerts.length > 0) {
        expect(alerts[0].severity).toBe('DANGER');
        expect(alerts[0].title).toBe('Critical Issue');
        expect(alerts[0].message).toBe('An error occurred');
        done();
      }
    });
  });

  it('should_handleSuccessSeverity_when_handleAlertReceivesSuccessAlert', (done) => {
    const alertJson = JSON.stringify({
      severity: 'SUCCESS',
      title: 'Day Advanced',
      message: 'The shop advanced by one day',
      timestamp: Date.now()
    });

    service.handleAlert(alertJson);

    service.alerts$.subscribe((alerts) => {
      if (alerts.length > 0) {
        expect(alerts[0].severity).toBe('SUCCESS');
        expect(alerts[0].title).toBe('Day Advanced');
        done();
      }
    });
  });

  it('should_autoRemoveAlert_after_8seconds', (done) => {
    const alertJson = JSON.stringify({
      severity: 'INFO',
      title: 'Temporary Alert',
      message: 'Should disappear',
      timestamp: Date.now()
    });

    service.handleAlert(alertJson);

    service.alerts$.subscribe((alerts) => {
      if (alerts.length === 1) {
        // Alert was added, now wait for it to be removed
        setTimeout(() => {
          service.alerts$.subscribe((alertsAfterTimeout) => {
            expect(alertsAfterTimeout.length).toBe(0);
            done();
          });
        }, 8500);
      }
    });
  }, 10000);
});
