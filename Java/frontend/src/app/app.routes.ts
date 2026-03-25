import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'inventory', pathMatch: 'full' },
  {
    path: 'inventory',
    loadComponent: () =>
      import('./features/inventory/inventory.component').then(m => m.InventoryComponent)
  },
  {
    path: 'projection',
    loadComponent: () =>
      import('./features/projection/projection.component').then(m => m.ProjectionComponent)
  },
];
