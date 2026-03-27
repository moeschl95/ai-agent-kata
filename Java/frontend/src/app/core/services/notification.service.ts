import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Client } from '@stomp/stompjs';

export type Severity = 'INFO' | 'WARNING' | 'DANGER' | 'CRITICAL';

export interface Alert {
  id: string;
  severity: Severity;
  title: string;
  message: string;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private stompClient: Client;
  public alerts$ = new BehaviorSubject<Alert[]>([]);
  private alertTimeouts: Map<string, ReturnType<typeof setTimeout>> = new Map();

  constructor() {
    this.stompClient = new Client();
    this.initializeWebSocket();
  }

  private initializeWebSocket(): void {
    this.stompClient.brokerURL = `ws://${window.location.host}/ws/alerts`;

    this.stompClient.onConnect = () => {
      this.stompClient.subscribe('/topic/item.expired', (message) => {
        this.handleAlert(message.body);
      });

      this.stompClient.subscribe('/topic/quality.threshold', (message) => {
        this.handleAlert(message.body);
      });
    };

    this.stompClient.onStompError = (frame) => {
      console.error('STOMP connection error:', frame);
    };

    this.stompClient.activate();
  }

  handleAlert(body: string): void {
    try {
      const alertData = JSON.parse(body);
      const alert: Alert = {
        id: Date.now().toString(),
        severity: alertData.severity,
        title: alertData.title,
        message: alertData.message,
        timestamp: alertData.timestamp
      };

      const currentAlerts = this.alerts$.value;
      this.alerts$.next([...currentAlerts, alert]);

      // Auto-remove alert after 8 seconds
      const timeout = setTimeout(() => {
        this.removeAlert(alert.id);
      }, 8000);

      this.alertTimeouts.set(alert.id, timeout);
    } catch (error) {
      console.error('Error parsing alert:', error);
    }
  }

  removeAlert(id: string): void {
    const currentAlerts = this.alerts$.value;
    const filteredAlerts = currentAlerts.filter(alert => alert.id !== id);
    this.alerts$.next(filteredAlerts);

    // Clear timeout if it exists
    const timeout = this.alertTimeouts.get(id);
    if (timeout) {
      clearTimeout(timeout);
      this.alertTimeouts.delete(id);
    }
  }
}
