import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZoneSuggestionsComponent } from './zone-suggestions.component';

describe('ZoneSuggestionsComponent', () => {
  let component: ZoneSuggestionsComponent;
  let fixture: ComponentFixture<ZoneSuggestionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ZoneSuggestionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ZoneSuggestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
