import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AlertContainerComponent } from './alert-container.component';
import { NotificationService, Alert } from '../../../core/services/notification.service';
import { BehaviorSubject } from 'rxjs';

describe('AlertContainerComponent', () => {
  let component: AlertContainerComponent;
  let fixture: ComponentFixture<AlertContainerComponent>;
  let notificationService: NotificationService;
  let mockAlerts$: BehaviorSubject<Alert[]>;

  beforeEach(async () => {
    mockAlerts$ = new BehaviorSubject<Alert[]>([]);

    const mockNotificationService = {
      alerts$: mockAlerts$,
      removeAlert: jasmine.createSpy('removeAlert')
    };

    await TestBed.configureTestingModule({
      imports: [AlertContainerComponent],
      providers: [
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AlertContainerComponent);
    component = fixture.componentInstance;
    notificationService = TestBed.inject(NotificationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should_subscribeToAlerts_when_ngOnInitIsCalled', () => {
    expect(component.alerts).toEqual([]);
  });

  it('should_displayAlerts_when_alertsAreEmitted', () => {
    const testAlert: Alert = {
      id: '1',
      severity: 'INFO',
      title: 'Test Alert',
      message: 'This is a test alert',
      timestamp: Date.now()
    };

    mockAlerts$.next([testAlert]);
    fixture.detectChanges();

    expect(component.alerts).toEqual([testAlert]);
  });

  it('should_renderClrAlertComponent_for_each_alert', () => {
    const testAlert: Alert = {
      id: '1',
      severity: 'INFO',
      title: 'Test Alert',
      message: 'This is a test alert',
      timestamp: Date.now()
    };

    mockAlerts$.next([testAlert]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const clrAlerts = compiled.querySelectorAll('clr-alert');
    expect(clrAlerts.length).toBe(1);
  });

  it('should_mapCriticalSeverityToErrorType_when_getAlertTypeIsCalled', () => {
    expect(component.getAlertType('CRITICAL')).toBe('error');
  });

  it('should_mapDangerSeverityToErrorType_when_getAlertTypeIsCalled', () => {
    expect(component.getAlertType('DANGER')).toBe('error');
  });

  it('should_mapWarningSeverityToWarningType_when_getAlertTypeIsCalled', () => {
    expect(component.getAlertType('WARNING')).toBe('warning');
  });

  it('should_mapSuccessSeverityToSuccessType_when_getAlertTypeIsCalled', () => {
    expect(component.getAlertType('SUCCESS')).toBe('success');
  });

  it('should_mapInfoSeverityToInfoType_when_getAlertTypeIsCalled', () => {
    expect(component.getAlertType('INFO')).toBe('info');
  });

  it('should_callRemoveAlert_when_dismissAlertIsCalled', () => {
    const alertId = '123';
    component.dismissAlert(alertId);
    expect(notificationService.removeAlert).toHaveBeenCalledWith(alertId);
  });

  it('should_displayAlertTitleAndMessage_in_template', () => {
    const testAlert: Alert = {
      id: '1',
      severity: 'INFO',
      title: 'Important',
      message: 'Something happened',
      timestamp: Date.now()
    };

    mockAlerts$.next([testAlert]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const alertContent = compiled.querySelector('clr-alert-item');
    const text = alertContent.textContent;
    
    expect(text).toContain('Important');
    expect(text).toContain('Something happened');
  });
});
