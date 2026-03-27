import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ProjectionComponent } from './projection.component';
import { ShopService } from '../../core/shop.service';
import { ProjectedItem } from '../../core/models';
import { of, NEVER, throwError } from 'rxjs';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('ProjectionComponent', () => {
  let component: ProjectionComponent;
  let fixture: ComponentFixture<ProjectionComponent>;
  let shopService: jasmine.SpyObj<ShopService>;

  beforeEach(async () => {
    const shopServiceSpy = jasmine.createSpyObj('ShopService', ['projectAll', 'projectItem', 'getItems']);

    await TestBed.configureTestingModule({
      imports: [ProjectionComponent, BrowserAnimationsModule],
      providers: [{ provide: ShopService, useValue: shopServiceSpy }]
    }).compileComponents();

    shopService = TestBed.inject(ShopService) as jasmine.SpyObj<ShopService>;
    // Set default return for getItems to prevent errors in tests that don't expect it
    shopService.getItems.and.returnValue(of([]));
    fixture = TestBed.createComponent(ProjectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ===== BULK PROJECTION TESTS =====

  it('should_renderDaysForm_when_pageLoads', () => {
    // Arrange - component already loaded in beforeEach
    // Act/Assert
    const bulkDaysInput = fixture.debugElement.query(By.css('clr-input-container input[formControlName="days"]'));
    expect(bulkDaysInput).toBeTruthy();

    const submitButtons = fixture.debugElement.queryAll(By.css('button[type="submit"]'));
    expect(submitButtons.length).toBeGreaterThan(0);
  });

  it('should_showValidationError_when_bulkDaysIsNegative', (done) => {
    // Arrange
    const daysControl = component.bulkForm.get('days');

    // Act
    daysControl?.setValue(-1);
    daysControl?.markAsTouched();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      const html = fixture.nativeElement.textContent;
      expect(html).toContain('Days must');
      expect(shopService.projectAll).not.toHaveBeenCalled();
      done();
    });
  });

  it('should_showValidationError_when_bulkDaysIsEmpty', (done) => {
    // Arrange
    const daysControl = component.bulkForm.get('days');

    // Act
    daysControl?.setValue(null);
    daysControl?.markAsTouched();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(shopService.projectAll).not.toHaveBeenCalled();
      done();
    });
  });

  it('should_callProjectAll_when_validFormIsSubmitted', () => {
    // Arrange
    const mockItems: ProjectedItem[] = [
      { name: 'Item 1', sellIn: 5, quality: 20, price: 25 },
      { name: 'Item 2', sellIn: 3, quality: 30, price: 40 }
    ];
    shopService.projectAll.and.returnValue(of(mockItems));
    component.bulkForm.get('days')?.setValue(5);

    // Act
    component.submitBulkProjection();

    // Assert
    expect(shopService.projectAll).toHaveBeenCalledWith(5);
  });

  it('should_displayProjectedItems_when_bulkProjectionSucceeds', (done) => {
    // Arrange
    const mockItems: ProjectedItem[] = [
      { name: 'Aged Brie', sellIn: 2, quality: 23, price: 50 },
      { name: 'Sulfuras', sellIn: -1, quality: 80, price: 100 }
    ];
    shopService.projectAll.and.returnValue(of(mockItems));
    component.bulkForm.get('days')?.setValue(3);

    // Act
    component.submitBulkProjection();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(component.projectedItems).toEqual(mockItems);
      const datagrid = fixture.debugElement.query(By.css('clr-datagrid'));
      expect(datagrid).toBeTruthy();
      done();
    });
  });

  it('should_showErrorAlert_when_bulkProjectionFails', (done) => {
    // Arrange
    shopService.projectAll.and.returnValue(throwError(() => new Error('API error')));
    component.bulkForm.get('days')?.setValue(5);

    // Act
    component.submitBulkProjection();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(component.bulkError).toBe('Failed to project inventory');
      done();
    });
  });

  it('should_showLoadingSpinner_when_bulkRequestIsInFlight', () => {
    // Arrange
    shopService.projectAll.and.returnValue(NEVER);
    component.bulkForm.get('days')?.setValue(5);

    // Act
    component.submitBulkProjection();
    fixture.detectChanges();

    // Assert
    expect(component.bulkLoading).toBe(true);
  });

  // ===== PER-ITEM PROJECTION TESTS =====

  it('should_displayItemNameInput_when_pageLoads', () => {
    // Arrange/Act - component already loaded
    const itemNameSelect = fixture.debugElement.query(By.css('select[formControlName="itemName"]'));

    // Assert
    expect(itemNameSelect).toBeTruthy();
  });

  it('should_showValidationError_when_itemNameIsEmpty', (done) => {
    // Arrange
    const itemNameControl = component.itemForm.get('itemName');

    // Act
    itemNameControl?.setValue('');
    itemNameControl?.markAsTouched();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(shopService.projectItem).not.toHaveBeenCalled();
      done();
    });
  });

  it('should_showValidationError_when_itemDaysIsNegative', (done) => {
    // Arrange
    const daysControl = component.itemForm.get('days');

    // Act
    daysControl?.setValue(-1);
    daysControl?.markAsTouched();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(shopService.projectItem).not.toHaveBeenCalled();
      done();
    });
  });

  it('should_callProjectItem_when_itemFormIsValid', () => {
    // Arrange
    const mockItem: ProjectedItem = { name: 'Aged Brie', sellIn: 2, quality: 23, price: 50 };
    shopService.projectItem.and.returnValue(of(mockItem));
    component.itemForm.get('itemName')?.setValue('Aged Brie');
    component.itemForm.get('days')?.setValue(3);

    // Act
    component.submitItemProjection();

    // Assert
    expect(shopService.projectItem).toHaveBeenCalledWith('Aged Brie', 3);
  });

  it('should_displayProjectedItem_when_itemProjectionSucceeds', (done) => {
    // Arrange
    const mockItem: ProjectedItem = { name: 'Aged Brie', sellIn: 2, quality: 23, price: 50 };
    shopService.projectItem.and.returnValue(of(mockItem));
    component.itemForm.get('itemName')?.setValue('Aged Brie');
    component.itemForm.get('days')?.setValue(3);

    // Act
    component.submitItemProjection();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(component.projectedItem).toEqual(mockItem);
      done();
    });
  });

  it('should_showErrorAlert_when_itemProjectionFails', (done) => {
    // Arrange
    shopService.projectItem.and.returnValue(throwError(() => new Error('Item not found')));
    component.itemForm.get('itemName')?.setValue('Nonexistent');
    component.itemForm.get('days')?.setValue(3);

    // Act
    component.submitItemProjection();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(component.itemError).toBe('Failed to project item');
      done();
    });
  });

  it('should_showLoadingSpinner_when_itemRequestIsInFlight', () => {
    // Arrange
    shopService.projectItem.and.returnValue(NEVER);
    component.itemForm.get('itemName')?.setValue('Aged Brie');
    component.itemForm.get('days')?.setValue(5);

    // Act
    component.submitItemProjection();
    fixture.detectChanges();

    // Assert
    expect(component.itemLoading).toBe(true);
  });

  it('should_highlightDangerRows_when_projectedSellInIsZeroOrNegative', (done) => {
    // Arrange
    const mockItems: ProjectedItem[] = [
      { name: 'Aged Brie', sellIn: 5, quality: 20, price: 50 },
      { name: 'Expired', sellIn: 0, quality: 10, price: 15 },
      { name: 'Very Expired', sellIn: -1, quality: 5, price: 10 }
    ];
    shopService.projectAll.and.returnValue(of(mockItems));
    component.bulkForm.get('days')?.setValue(10);

    // Act
    component.submitBulkProjection();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      const dangerRows = fixture.debugElement.queryAll(By.css('clr-dg-row.text-danger'));
      expect(dangerRows.length).toBe(2);
      done();
    });
  });

  // ===== ITEM DROPDOWN TESTS =====

  it('should_loadAvailableItems_when_componentInitializes', (done) => {
    // Arrange
    const mockItems = [
      { name: 'Aged Brie', sellIn: 5, quality: 20, price: 50 },
      { name: 'Sulfuras', sellIn: -1, quality: 80, price: 100 }
    ];
    shopService.getItems.and.returnValue(of(mockItems));

    // Act
    component.ngOnInit();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      expect(shopService.getItems).toHaveBeenCalled();
      expect(component.availableItemNames).toEqual(['Aged Brie', 'Sulfuras']);
      done();
    });
  });

  it('should_displayItemsInDropdown_when_itemsAreLoaded', (done) => {
    // Arrange
    const mockItems = [
      { name: 'Aged Brie', sellIn: 5, quality: 20, price: 50 },
      { name: 'Sulfuras', sellIn: -1, quality: 80, price: 100 }
    ];
    shopService.getItems.and.returnValue(of(mockItems));
    component.ngOnInit();

    // Act
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      const options = fixture.debugElement.queryAll(By.css('select[formControlName="itemName"] option'));
      expect(options.length).toBe(3); // 1 placeholder + 2 items
      expect(options[0].nativeElement.textContent).toContain('Select an item');
      expect(options[1].nativeElement.textContent).toContain('Aged Brie');
      expect(options[2].nativeElement.textContent).toContain('Sulfuras');
      done();
    });
  });

  it('should_disableDropdown_when_itemsFailToLoad', (done) => {
    // Arrange
    shopService.getItems.and.returnValue(throwError(() => new Error('API error')));

    // Act
    component.ngOnInit();
    fixture.detectChanges();

    // Assert
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(component.itemsLoadError).toBe('Failed to load available items');
      expect(component.itemForm.get('itemName')?.disabled).toBe(true);
      done();
    });
  });

  it('should_submitWithSelectedItem_when_dropdownIsUsed', () => {
    // Arrange
    const mockItems = [
      { name: 'Aged Brie', sellIn: 5, quality: 20, price: 50 }
    ];
    const mockProjectedItem: ProjectedItem = { name: 'Aged Brie', sellIn: 2, quality: 23, price: 50 };
    shopService.getItems.and.returnValue(of(mockItems));
    shopService.projectItem.and.returnValue(of(mockProjectedItem));
    component.ngOnInit();

    // Act
    component.itemForm.get('itemName')?.setValue('Aged Brie');
    component.itemForm.get('days')?.setValue(3);
    component.submitItemProjection();

    // Assert
    expect(shopService.projectItem).toHaveBeenCalledWith('Aged Brie', 3);
  });
});
