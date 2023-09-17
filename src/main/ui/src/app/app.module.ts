import { NgModule } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import {DropdownModule} from 'primeng/dropdown';
import {InputNumberModule} from 'primeng/inputnumber';
import {AutoCompleteModule} from 'primeng/autocomplete';
import {ToastModule} from 'primeng/toast';
import {ProgressSpinnerModule} from 'primeng/progressspinner';
import { AngularSplitModule } from 'angular-split';
import {CheckboxModule} from 'primeng/checkbox';


import { AppComponent } from './app.component';
import { ZoneSuggestionsComponent } from './zone-suggestions/zone-suggestions.component';
import { UploadRegionDataComponent } from './upload-region-data/upload-region-data.component';

@NgModule({
  declarations: [
    AppComponent,
    ZoneSuggestionsComponent,
    UploadRegionDataComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    InputTextModule,
    ButtonModule,
    DialogModule,
    DropdownModule,
    InputNumberModule,
    AutoCompleteModule,
    ToastModule,
    ProgressSpinnerModule,
    AngularSplitModule,
    CheckboxModule
  ],
  providers: [Title],
  bootstrap: [AppComponent]
})
export class AppModule { }
