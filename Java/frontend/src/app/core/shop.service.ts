import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ShopItem, ProjectedItem } from './models';

@Injectable({ providedIn: 'root' })
export class ShopService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/items';

  getItems(): Observable<ShopItem[]> {
    return this.http.get<ShopItem[]>(this.base);
  }

  advanceDay(): Observable<ShopItem[]> {
    return this.http.post<ShopItem[]>(`${this.base}/advance-day`, {});
  }

  getPrice(name: string): Observable<number> {
    return this.http.get<number>(`${this.base}/${encodeURIComponent(name)}/price`);
  }

  projectItem(name: string, days: number): Observable<ProjectedItem> {
    return this.http.get<ProjectedItem>(
      `${this.base}/${encodeURIComponent(name)}/projection`,
      { params: { days } }
    );
  }

  projectAll(days: number): Observable<ProjectedItem[]> {
    return this.http.get<ProjectedItem[]>(
      `${this.base}/projection`,
      { params: { days } }
    );
  }
}
