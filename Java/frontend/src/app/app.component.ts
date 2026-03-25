import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ClarityModule } from '@clr/angular';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, ClarityModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Gilded Rose';
}
