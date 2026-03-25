import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ShopService } from './shop.service';
import { ShopItem, ProjectedItem } from './models';

describe('ShopService', () => {
  let service: ShopService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ShopService]
    });
    service = TestBed.inject(ShopService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should_returnItems_when_getItemsIsCalled', () => {
    const mockItems: ShopItem[] = [
      { name: 'Aged Brie', sellIn: 10, quality: 20 }
    ];

    service.getItems().subscribe(items => {
      expect(items).toEqual(mockItems);
    });

    const req = httpMock.expectOne('/api/items');
    expect(req.request.method).toBe('GET');
    req.flush(mockItems);
  });

  it('should_returnUpdatedItems_when_advanceDayIsCalled', () => {
    const mockItems: ShopItem[] = [
      { name: 'Aged Brie', sellIn: 9, quality: 21 }
    ];

    service.advanceDay().subscribe(items => {
      expect(items).toEqual(mockItems);
    });

    const req = httpMock.expectOne('/api/items/advance-day');
    expect(req.request.method).toBe('POST');
    req.flush(mockItems);
  });

  it('should_returnPrice_when_getPriceIsCalled', () => {
    const mockPrice = 25;

    service.getPrice('Aged Brie').subscribe(price => {
      expect(price).toBe(mockPrice);
    });

    const req = httpMock.expectOne('/api/items/Aged%20Brie/price');
    expect(req.request.method).toBe('GET');
    req.flush(mockPrice);
  });

  it('should_returnProjectedItem_when_projectItemIsCalled', () => {
    const mockProjected: ProjectedItem = {
      name: 'Aged Brie',
      sellIn: 5,
      quality: 25
    };

    service.projectItem('Aged Brie', 5).subscribe(item => {
      expect(item).toEqual(mockProjected);
    });

    const req = httpMock.expectOne(
      '/api/items/Aged%20Brie/projection?days=5'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockProjected);
  });

  it('should_returnProjectedItems_when_projectAllIsCalled', () => {
    const mockProjected: ProjectedItem[] = [
      { name: 'Aged Brie', sellIn: 5, quality: 25 },
      { name: '+5 Dexterity Vest', sellIn: 15, quality: 18 }
    ];

    service.projectAll(5).subscribe(items => {
      expect(items).toEqual(mockProjected);
    });

    const req = httpMock.expectOne('/api/items/projection?days=5');
    expect(req.request.method).toBe('GET');
    req.flush(mockProjected);
  });
});
