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
    const shopServiceSpy = jasmine.createSpyObj('ShopService', ['getItems', 'advanceDay']);

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

  it('should_renderAdvanceDayButton_when_inventoryPageLoads', () => {
    // Arrange
    shopService.getItems.and.returnValue(of([]));

    // Act
    fixture.detectChanges();

    // Assert
    const button: DebugElement = fixture.debugElement.query(By.css('button'));
    expect(button).toBeTruthy();
    expect(button.nativeElement.textContent).toContain('Advance Day');
  });

  it('should_callAdvanceDay_when_buttonIsClicked', () => {
    // Arrange
    shopService.getItems.and.returnValue(of([]));
    shopService.advanceDay.and.returnValue(of([]));
    fixture.detectChanges();

    // Act
    const button: DebugElement = fixture.debugElement.query(By.css('button'));
    button.nativeElement.click();

    // Assert
    expect(shopService.advanceDay).toHaveBeenCalledTimes(1);
  });

  it('should_refreshInventory_when_advanceDaySucceeds', () => {
    // Arrange
    const initialItems: ShopItem[] = [{ name: 'Aged Brie', sellIn: 5, quality: 20 }];
    const updatedItems: ShopItem[] = [{ name: 'Aged Brie', sellIn: 4, quality: 21 }];
    shopService.getItems.and.returnValue(of(initialItems));
    shopService.advanceDay.and.returnValue(of(updatedItems));
    fixture.detectChanges();

    // Act
    const button: DebugElement = fixture.debugElement.query(By.css('button'));
    button.nativeElement.click();
    fixture.detectChanges();

    // Assert
    const html = fixture.nativeElement.innerHTML;
    expect(html).toContain('Aged Brie');
    expect(component.items).toEqual(updatedItems);
  });

  it('should_disableButton_when_requestIsInProgress', () => {
    // Arrange
    shopService.getItems.and.returnValue(of([]));
    shopService.advanceDay.and.returnValue(NEVER);
    fixture.detectChanges();

    // Act
    const button: DebugElement = fixture.debugElement.query(By.css('button'));
    button.nativeElement.click();
    fixture.detectChanges();

    // Assert
    expect(button.nativeElement.disabled).toBe(true);
  });

  it('should_showErrorAlert_when_advanceDayFails', () => {
    // Arrange
    shopService.getItems.and.returnValue(of([]));
    shopService.advanceDay.and.returnValue(throwError(() => new Error('Advance failed')));
    fixture.detectChanges();

    // Act
    const button: DebugElement = fixture.debugElement.query(By.css('button'));
    button.nativeElement.click();
    fixture.detectChanges();

    // Assert
    const alert: DebugElement = fixture.debugElement.query(By.css('clr-alert[clrAlertType="danger"]'));
    expect(alert).toBeTruthy();
  });

  it('should_callServiceWithSortParams_when_onDatagridRefreshCalledWithSort', () => {
    // Arrange
    const mockItems: ShopItem[] = [];
    shopService.getItems.and.returnValue(of(mockItems));
    const state = { sort: { by: 'name', reverse: false } };

    // Act
    component.onDatagridRefresh(state);

    // Assert
    expect(shopService.getItems).toHaveBeenCalledWith({ sortBy: 'name', sortDir: 'asc' });
  });

  it('should_callServiceWithDescSortDir_when_datagridSortReverse', () => {
    // Arrange
    const mockItems: ShopItem[] = [];
    shopService.getItems.and.returnValue(of(mockItems));
    const state = { sort: { by: 'quality', reverse: true } };

    // Act
    component.onDatagridRefresh(state);

    // Assert
    expect(shopService.getItems).toHaveBeenCalledWith({ sortBy: 'quality', sortDir: 'desc' });
  });

  it('should_callServiceWithoutSortParams_when_datagridSortChangesToNone', () => {
    // Arrange
    const mockItems: ShopItem[] = [];
    shopService.getItems.and.returnValue(of(mockItems));
    
    // First, set a sort state
    const sortedState = { sort: { by: 'name', reverse: false } };
    component.onDatagridRefresh(sortedState);
    shopService.getItems.calls.reset();
    
    // Then clear the sort
    const unsortedState = { sort: null };

    // Act
    component.onDatagridRefresh(unsortedState);

    // Assert - should reload when sort is cleared
    expect(shopService.getItems).toHaveBeenCalledWith({});
  });

  it('should_notReloadData_when_sortStateHasNotChanged', () => {
    // Arrange
    const mockItems: ShopItem[] = [];
    shopService.getItems.and.returnValue(of(mockItems));
    const state = { sort: { by: 'name', reverse: false } };

    // Act - call with same sort twice
    component.onDatagridRefresh(state);
    shopService.getItems.calls.reset();
    component.onDatagridRefresh(state);

    // Assert - should not reload on second call
    expect(shopService.getItems).not.toHaveBeenCalled();
  });
});
