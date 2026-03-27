import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';
import { NotificationService, Alert, Severity } from '../../../core/services/notification.service';

@Component({
  selector: 'app-alert-container',
  standalone: true,
  imports: [CommonModule, ClarityModule],
  templateUrl: './alert-container.component.html',
  styleUrls: ['./alert-container.component.scss']
})
export class AlertContainerComponent implements OnInit {
  alerts: Alert[] = [];

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.notificationService.alerts$.subscribe((alerts) => {
      this.alerts = alerts;
    });
  }

  getAlertType(severity: Severity): string {
    switch (severity) {
      case 'CRITICAL':
      case 'DANGER':
        return 'error';
      case 'WARNING':
        return 'warning';
      case 'SUCCESS':
        return 'success';
      case 'INFO':
      default:
        return 'info';
    }
  }

  dismissAlert(id: string): void {
    this.notificationService.removeAlert(id);
  }
}

