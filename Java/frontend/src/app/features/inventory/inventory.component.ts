import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ShopService } from '../../core/shop.service';
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

  private load(): void {
    this.executeAction(
      () => this.shopService.getItems(),
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
