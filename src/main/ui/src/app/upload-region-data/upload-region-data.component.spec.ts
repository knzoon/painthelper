import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadRegionDataComponent } from './upload-region-data.component';

describe('UploadRegionDataComponent', () => {
  let component: UploadRegionDataComponent;
  let fixture: ComponentFixture<UploadRegionDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadRegionDataComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadRegionDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
