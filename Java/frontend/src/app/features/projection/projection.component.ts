import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';

@Component({
  selector: 'app-projection',
  standalone: true,
  imports: [CommonModule, ClarityModule],
  template: `
    <div class="content-container">
      <div class="content-area">
        <h2>Projection</h2>
        <p>Projection page coming soon...</p>
      </div>
    </div>
  `,
})
export class ProjectionComponent {}
