import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TelaBreveComponent } from './tela-breve.component';

describe('TelaBreveComponent', () => {
  let component: TelaBreveComponent;
  let fixture: ComponentFixture<TelaBreveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TelaBreveComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TelaBreveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
