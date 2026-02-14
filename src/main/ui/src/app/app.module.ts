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
import { UploadRegionDataDialogComponent } from './upload-region-data-dialog/upload-region-data-dialog.component';
import { HelpDialogComponent } from './help-dialog/help-dialog.component';
import { DonateDialogComponent } from './donate-dialog/donate-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    ZoneSuggestionsComponent,
    UploadRegionDataDialogComponent,
    HelpDialogComponent,
    DonateDialogComponent
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
