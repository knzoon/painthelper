import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ImportResult} from "../import-result";
import {ZoneService} from "../zone.service";
import {FileUploadResult} from "../file-upload-result";

@Component({
  selector: 'app-upload-region-data-dialog',
  templateUrl: './upload-region-data-dialog.component.html',
  styleUrls: ['./upload-region-data-dialog.component.css']
})
export class UploadRegionDataDialogComponent  {

  @Input() displayDialog: boolean = false;
  @Output()  displayDialogChange= new EventEmitter<boolean>();
  @Output() displaySpinnerChange = new EventEmitter<boolean>();
  @Output() uploadResultChange = new EventEmitter<FileUploadResult>();

  title: string = "Importera användare från warded.se";
  constructor(private zoneService: ZoneService) { }

  closeDialog() : void {
    this.displayDialogChange.emit(false);
  }

  turnOnSpinner(): void {
    this.displaySpinnerChange.emit(true);
  }

  turnOffSpinner(): void {
    this.displaySpinnerChange.emit(false);
  }

  onFileSelected(event : any) {
    const file: File = event.target.files[0];

    if (file) {
      if (file.type === 'text/html') {
        const formData = new FormData();
        formData.append("file", file);
        this.turnOnSpinner();
        this.zoneService.uploadRegionData(formData).subscribe(
          (importResult: ImportResult) => {
            this.turnOffSpinner();
            const importText = importResult.importType + " Antal zoner: " + importResult.nrofImported;
            this.uploadResultChange.emit({resultType: 'success', resultInfo: importText});
          },
          err => {
            let errorMessage = err.error.message;
            this.turnOffSpinner();
            this.uploadResultChange.emit({resultType: 'error', resultInfo: errorMessage})
          });
        // TODO ta hand om exceptions på ett bra sätt
      } else {
        console.info("Du valde fel filtyp");
      }
    }
    this.closeDialog();
  }

}
