import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-donate-dialog',
  templateUrl: './donate-dialog.component.html',
  styleUrls: ['./donate-dialog.component.css']
})
export class DonateDialogComponent {
  @Input() displayDialog: boolean = false;
  @Output()  displayDialogChange= new EventEmitter<boolean>();

  title: string = "Visa din uppskattning genom att donera en slant";
  closeDialog() : void {
    this.displayDialogChange.emit(false);
  }

}
