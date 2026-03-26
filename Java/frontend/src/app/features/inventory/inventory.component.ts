import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ShopService, SortOptions } from '../../core/shop.service';
import { ShopItem } from '../../core/models';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, ClarityModule],
  templateUrl: './inventory.component.html',
})
export class InventoryComponent implements OnInit, OnDestroy {
  private readonly shopService = inject(ShopService);
  private readonly destroy$ = new Subject<void>();

  items: ShopItem[] = [];
  loading = false;
  advancing = false;
  error: string | null = null;
  sortBy: string | null = null;
  sortDir: string | null = null;
  private lastRequestedSortBy: string | null = null;
  private lastRequestedSortDir: string | null = null;

  ngOnInit(): void {
    this.load();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  advanceDay(): void {
    this.executeAction(
      () => this.shopService.advanceDay(),
      () => this.advancing = true,
      () => this.advancing = false,
      'Failed to advance day'
    );
  }

  onDatagridRefresh(state: any): void {
    // Extract sort state from the refresh event
    let newSortBy: string | null = null;
    let newSortDir: string | null = null;

    if (state && state.sort && state.sort.by) {
      newSortBy = state.sort.by;
      newSortDir = state.sort.reverse ? 'desc' : 'asc';
    }

    // Only reload if the sort state differs from what we last requested
    // This prevents duplicate requests when the datagrid refresh fires after data updates
    if (newSortBy !== this.lastRequestedSortBy || newSortDir !== this.lastRequestedSortDir) {
      this.sortBy = newSortBy;
      this.sortDir = newSortDir;
      this.load();
    }
  }

  private load(): void {
    const options: SortOptions = {};
    if (this.sortBy) {
      options.sortBy = this.sortBy;
      options.sortDir = this.sortDir || undefined;
    }
    this.executeAction(
      () => this.shopService.getItems(options),
      () => this.loading = true,
      () => this.loading = false,
      'Failed to load inventory'
    );
  }

  private executeAction(
    serviceCall: () => any,
    onStart: () => void,
    onComplete: () => void,
    errorMessage: string
  ): void {
    onStart();
    this.error = null;
    serviceCall()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (items: ShopItem[]) => {
          this.items = items;
          this.lastRequestedSortBy = this.sortBy;
          this.lastRequestedSortDir = this.sortDir;
          onComplete();
        },
        error: (err: any) => {
          onComplete();
          this.error = errorMessage;
          console.error(errorMessage, ':', err);
        }
      });
  }
}

