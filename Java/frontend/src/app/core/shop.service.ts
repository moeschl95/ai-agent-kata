import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ShopItem, ProjectedItem } from './models';

export interface SortOptions {
  sortBy?: string;
  sortDir?: string;
}

@Injectable({ providedIn: 'root' })
export class ShopService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/items';

  getItems(options?: SortOptions): Observable<ShopItem[]> {
    let params = new HttpParams();
    if (options?.sortBy) {
      params = params.set('sortBy', options.sortBy);
    }
    if (options?.sortDir) {
      params = params.set('sortDir', options.sortDir);
    }
    return this.http.get<ShopItem[]>(
      this.base,
      params.keys().length > 0 ? { params } : {}
    );
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
