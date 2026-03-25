import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, ClarityModule],
  template: `
    <div class="content-container">
      <div class="content-area">
        <h2>Inventory</h2>
        <p>Inventory page coming soon...</p>
      </div>
    </div>
  `,
})
export class InventoryComponent {}
