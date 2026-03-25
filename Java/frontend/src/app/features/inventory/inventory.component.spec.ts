import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { InventoryComponent } from './inventory.component';
import { ShopService } from '../../core/shop.service';
import { ShopItem } from '../../core/models';
import { of, NEVER, throwError } from 'rxjs';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('InventoryComponent', () => {
  let component: InventoryComponent;
  let fixture: ComponentFixture<InventoryComponent>;
  let shopService: jasmine.SpyObj<ShopService>;

  beforeEach(async () => {
    const shopServiceSpy = jasmine.createSpyObj('ShopService', ['getItems']);

    await TestBed.configureTestingModule({
      imports: [InventoryComponent, BrowserAnimationsModule],
      providers: [{ provide: ShopService, useValue: shopServiceSpy }]
    }).compileComponents();

    shopService = TestBed.inject(ShopService) as jasmine.SpyObj<ShopService>;
    fixture = TestBed.createComponent(InventoryComponent);
    component = fixture.componentInstance;
  });

  it('should_displayItems_when_itemsAreLoaded', () => {
    // Arrange
    const mockItems: ShopItem[] = [
      { name: 'Aged Brie', sellIn: 5, quality: 20 },
      { name: 'Sulfuras', sellIn: -1, quality: 80 }
    ];
    shopService.getItems.and.returnValue(of(mockItems));

    // Act
    fixture.detectChanges();

    // Assert
    const html = fixture.nativeElement.innerHTML;
    expect(html).toContain('Aged Brie');
    expect(html).toContain('Sulfuras');
  });

  it('should_showLoadingSpinner_when_requestIsInFlight', () => {
    // Arrange
    shopService.getItems.and.returnValue(NEVER);

    // Act
    fixture.detectChanges();

    // Assert
    const spinner: DebugElement = fixture.debugElement.query(By.css('clr-spinner'));
    expect(spinner).toBeTruthy();
  });

  it('should_showErrorAlert_when_getItemsFails', () => {
    // Arrange
    shopService.getItems.and.returnValue(throwError(() => new Error('Network error')));

    // Act
    fixture.detectChanges();

    // Assert
    const alert: DebugElement = fixture.debugElement.query(By.css('clr-alert[clrAlertType="danger"]'));
    expect(alert).toBeTruthy();
  });
});
