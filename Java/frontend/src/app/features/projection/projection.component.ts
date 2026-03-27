import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClarityModule } from '@clr/angular';
import { Subscription } from 'rxjs';
import { ShopService } from '../../core/shop.service';
import { ProjectedItem } from '../../core/models';

@Component({
  selector: 'app-projection',
  standalone: true,
  imports: [CommonModule, ClarityModule, ReactiveFormsModule],
  templateUrl: './projection.component.html',
})
export class ProjectionComponent implements OnInit, OnDestroy {
  // Bulk projection state
  bulkForm!: FormGroup;
  bulkLoading = false;
  bulkError: string | null = null;
  projectedItems: ProjectedItem[] = [];

  // Per-item projection state
  itemForm!: FormGroup;
  itemLoading = false;
  itemError: string | null = null;
  projectedItem: ProjectedItem | null = null;
  availableItemNames: string[] = [];
  itemsLoadError: string | null = null;

  private subscriptions = new Subscription();

  constructor(private fb: FormBuilder, private shopService: ShopService) {}

  ngOnInit(): void {
    this.bulkForm = this.fb.group({
      days: [0, [Validators.required, Validators.min(0)]]
    });
    this.itemForm = this.fb.group({
      itemName: ['', Validators.required],
      days: [0, [Validators.required, Validators.min(0)]]
    });
    this.loadAvailableItems();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private loadAvailableItems(): void {
    const sub = this.shopService.getItems().subscribe({
      next: (items) => {
        this.availableItemNames = items.map(item => item.name);
        this.itemsLoadError = null;
        this.itemForm.get('itemName')?.enable();
      },
      error: (err: any) => {
        this.itemsLoadError = 'Failed to load available items';
        this.itemForm.get('itemName')?.disable();
        console.error('Failed to load available items:', err);
      }
    });
    this.subscriptions.add(sub);
  }

  submitBulkProjection(): void {
    if (this.bulkForm.invalid) {
      this.bulkForm.markAllAsTouched();
      return;
    }

    const days = this.bulkForm.get('days')?.value;
    this.bulkLoading = true;
    this.bulkError = null;
    this.projectedItems = [];

    const sub = this.shopService.projectAll(days)
      .subscribe({
        next: (items: ProjectedItem[]) => {
          this.projectedItems = items;
          this.bulkLoading = false;
        },
        error: (err: any) => {
          this.bulkLoading = false;
          this.bulkError = 'Failed to project inventory';
          console.error('Failed to project inventory:', err);
        }
      });
    
    this.subscriptions.add(sub);
  }

  submitItemProjection(): void {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }

    const itemName = this.itemForm.get('itemName')?.value;
    const days = this.itemForm.get('days')?.value;
    this.itemLoading = true;
    this.itemError = null;
    this.projectedItem = null;

    const sub = this.shopService.projectItem(itemName, days)
      .subscribe({
        next: (item: ProjectedItem) => {
          this.projectedItem = item;
          this.itemLoading = false;
        },
        error: (err: any) => {
          this.itemLoading = false;
          this.itemError = 'Failed to project item';
          console.error('Failed to project item:', err);
        }
      });
    
    this.subscriptions.add(sub);
  }
}

