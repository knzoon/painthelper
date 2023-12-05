import { Component, OnInit, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';
import {MessageService} from 'primeng/api';

import {ZoneService} from "../zone.service";
import {UniqueZone} from "../unique-zone";
import {UniqueZoneSearchresult} from "../unique-zone-searchresult";
import {RegionTakes} from "../region-takes";
import {Area} from "../area";
import {User} from "../user";
import {ImportResult} from "../import-result";
import {Greeting} from "../greeting";
import {BroadcastMessage} from "../broadcast-message";

const iconRetinaUrl = 'assets/marker-icon-2x.png';
const iconUrl = 'assets/marker-icon.png';
const shadowUrl = 'assets/marker-shadow.png';
const iconDefault = L.icon({
  iconRetinaUrl,
  iconUrl,
  shadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  tooltipAnchor: [16, -28],
  shadowSize: [41, 41]
});
L.Marker.prototype.options.icon = iconDefault;

@Component({
  selector: 'app-zone-suggestions',
  templateUrl: './zone-suggestions.component.html',
  styleUrls: ['./zone-suggestions.component.css'],
  providers: [MessageService]
})
export class ZoneSuggestionsComponent implements OnInit , AfterViewInit{

  displayModal: boolean = false;

  displayHelpModal: boolean = false;

  displaySpinner: boolean = false;

  readonly lsUserKey: string = "lastUser";
  readonly lsRegionKey: string = "lastRegion";
  readonly lsAreaKey: string = "lastArea";
  readonly lsMinTakesKey: string = "minTakes";
  readonly lsMaxTakesKey: string = "maxTakes";
  readonly lsSearchRoundOnly: string = "searchRoundOnly";

  errorFindingUser?: string = "noUserChosen";

  selectedUser?: User;

  suggestedUsers: User[] = [];

  regionTakes: RegionTakes[] = [];

  areas: Area[] = [];

  zones: UniqueZone[] = [];

  map: any;

  regionTakesId: number = 0;

  selectedRegion?: RegionTakes;

  selectedArea?: Area;

  minTakes: number = 0;

  maxTakes: number = 9999;

  searchRoundOnly: boolean = false;

  layerGroup: any;

  untakenBox: string = "";
  untakenBoxPercent: string = ""
  greenbox: string = "";
  greenboxPercent: string = "";
  yellowbox: string = "";
  yellowboxPercent: string = "";
  orangebox: string = "";
  orangeboxPercent: string = "";
  redbox: string = "";
  redboxPercent: string = "";
  purplebox: string = "";
  purpleboxPercent: string = "";


  constructor(private zoneService: ZoneService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.testingPopulatingUser();
    this.testingPopulatingRegion();
    this.testingPopulatingArea();
    this.populatingMinTakesFromLocalstorage();
    this.populatingMaxTakesFromLocalstorage();
    this. populatingSearchRoundOnlyFromLocalstorage()
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  initMap(): void {
    this.map = L.map('map', {
      center: [ 63.807757, 20.300605 ],
      zoom: 12
    });

    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      minZoom: 3,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    tiles.addTo(this.map);

    this.layerGroup = L.layerGroup();
    this.layerGroup.addTo(this.map);
  }

  addMarkers(): void {
    console.info(this.zones.length);

    for (let z of this.zones) {
      let color = this.getColorForIcon(z);
      let derivedIconsize = this.getIconSize(z);

      let numberIcon = L.divIcon({
        className: color,
        iconSize: [derivedIconsize[0], derivedIconsize[1]],
        iconAnchor: [10, 44],
        popupAnchor: [3, -40],
        html: z.takes.toString()
      });

      let marker = L.marker([z.latitude, z.longitude], {
        icon: numberIcon
      });

      marker.bindPopup(z.zoneName).openPopup();

      this.layerGroup.addLayer(marker);
    }
  }

  getIconSize(zone: UniqueZone) {
    if (zone.takes < 1000) {
      return [25, 41];
    }

    return [32, 41];
  }

  getColorForIcon(zone: UniqueZone): string {
    const iconBase = 'number-icon-';

    if (zone.takes < 1) {
      return iconBase + 'warning';
    }

    if (zone.takes < 2) {
      return iconBase + 'green';
    }

    if (zone.takes < 11) {
      return iconBase + 'yellow';
    }

    if (zone.takes < 21) {
      return iconBase + 'orange';
    }

    if (zone.takes < 51) {
      return iconBase + 'red';
    }

    if (zone.takes < 1000) {
      return iconBase + 'purple';
    }

    return iconBase + 'fetlila';
  }

  clearMarkers(): void {
    if (this.layerGroup) {
      this.layerGroup.clearLayers();
    }
  }

  clearSearchresult(): void {
    this.zones = [];
  }

  searchButtonIsDisabled(): boolean {
    return this.minTakes == null || this.maxTakes == null;
  }

  areasMissing(): boolean {
    return this.areas.length == 1;
  }

  getZones(): void {
    this.clearMarkers();
    this.zoneService.getZones(this.regionTakesId, this.minTakes, this.maxTakes, this.searchRoundOnly, this.selectedArea).subscribe((searchResult: UniqueZoneSearchresult) => {
      this.zones = searchResult.zones;
      this.addMarkers();
      if (this.zones.length > 0) {
        this.map.panTo(new L.LatLng(searchResult.latitude, searchResult.longitude));
      }
    });
  }

  userChanged(event : any) {
    let userName = event.username;
    console.info("event.username: " + userName);
    const jsonData = JSON.stringify(this.selectedUser);
    localStorage.setItem(this.lsUserKey, jsonData);
    if (this.selectedUser) {
      this.showEventualBroadcastMessages();
      this.updateRegions(this.selectedUser.username);
    } else {
      console.error('There should be a selected user at this point');
    }
  }


  handleMinTakesOnInput(event: any) {
    if (event.value) {
      const jsonData = JSON.stringify(event.value);
      localStorage.setItem(this.lsMinTakesKey, jsonData);
    }
  }

  handleMaxTakesOnInput(event: any) {
    if (event.value) {
      const jsonData = JSON.stringify(event.value);
      localStorage.setItem(this.lsMaxTakesKey, jsonData);
    }
  }

  handleSearchOnlyRoundChange() {
    const jsonData = JSON.stringify(this.searchRoundOnly);
    localStorage.setItem(this.lsSearchRoundOnly, jsonData);
  }

  updateRegions(userName: string) {
    this.selectedRegion = undefined;
    this.clearSearchresult();
    this.clearAreaSelect();
    this.clearMarkers();
    console.info("apa3: ");

    this.zoneService.getRegions(userName).subscribe((regions: RegionTakes[]) => {
      console.info("region size before: " + this.regionTakes.length);
      console.info("regions from server size: " + regions.length);
      this.regionTakes = regions;
      console.info("region size after: " + this.regionTakes.length);

      if (this.regionTakes.length == 0) {
        this.errorFindingUser = "noUserFound";
        console.info('Dålig användare: ' + userName);
        this.regionTakesId = 0;
      } else {
        this.errorFindingUser = undefined;
        console.info('Bra användare: ' + userName);
      }
    });
  }

  private showEventualBroadcastMessages() {
    if (this.selectedUser) {
      this.zoneService.getBroadcastMessages(this.selectedUser.id).subscribe((messages: BroadcastMessage[]) => {
        if (messages.length > 0) {
          this.messageService.add({severity:'error', summary: 'Viktig information', detail: messages[0].message, life: 15000});
        }
      });
    }
  }

  private populateAreaSelect() {
    this.zoneService.getAreas(this.regionTakesId).subscribe((areas : Area[]) => {
      this.areas = areas;
    });
  }

  private populateColorboxesForRegion() {
    if (this.selectedRegion) {
      let totalAmount = this.totalAmountFromRegion(this.selectedRegion);
      this.untakenBox = this.selectedRegion.untaken;
      this.untakenBoxPercent = this.buildPercentString(this.selectedRegion.untaken, totalAmount);
      this.greenbox = this.selectedRegion.green;
      this.greenboxPercent =  this.buildPercentString(this.selectedRegion.green, totalAmount);
      this.yellowbox = this.selectedRegion.yellow;
      this.yellowboxPercent = this.buildPercentString(this.selectedRegion.yellow, totalAmount);
      this.orangebox = this.selectedRegion.orange;
      this.orangeboxPercent = this.buildPercentString(this.selectedRegion.orange, totalAmount);
      this.redbox = this.selectedRegion.red;
      this.redboxPercent = this.buildPercentString(this.selectedRegion.red, totalAmount);
      this.purplebox = this.selectedRegion.purple;
      this.purpleboxPercent = this.buildPercentString(this.selectedRegion.purple, totalAmount);
    } else {
      this.clearBoxes();
    }
  }

  private populateColorboxesForArea() {
    if (this.selectedArea) {
      let totalAmount = this.totalAmountFromArea(this.selectedArea);
      this.untakenBox = this.selectedArea.untaken;
      this.untakenBoxPercent = this.buildPercentString(this.selectedArea.untaken, totalAmount);
      this.greenbox = this.selectedArea.green;
      this.greenboxPercent =  this.buildPercentString(this.selectedArea.green, totalAmount);
      this.yellowbox = this.selectedArea.yellow;
      this.yellowboxPercent = this.buildPercentString(this.selectedArea.yellow, totalAmount);
      this.orangebox = this.selectedArea.orange;
      this.orangeboxPercent = this.buildPercentString(this.selectedArea.orange, totalAmount);
      this.redbox = this.selectedArea.red;
      this.redboxPercent = this.buildPercentString(this.selectedArea.red, totalAmount);
      this.purplebox = this.selectedArea.purple;
      this.purpleboxPercent = this.buildPercentString(this.selectedArea.purple, totalAmount);
    } else {
      this.clearBoxes();
    }
  }

  private totalAmountFromRegion(region: RegionTakes): number {
    return this.totalAmountOfTakes(+region.untaken, +region.green, +region.yellow, +region.orange, +region.red, +region.purple);
  }

  private totalAmountFromArea(area: Area): number {
    return this.totalAmountOfTakes(+area.untaken, +area.green, +area.yellow, +area.orange, +area.red, +area.purple);
  }

  private totalAmountOfTakes(untaken: number, green: number, yellow: number, orange: number, red: number, purple: number): number {
    return untaken + green + yellow + orange + red + purple;
  }

  private buildPercentStringOld(part: string, total:number): string {
    let percent: number = +part  * 100/ total;
    return part + " (" + percent.toFixed() + "%)";
  }

  private buildPercentString(part: string, total:number): string {
    let percent: number = +part  * 100/ total;
    return "(" + percent.toFixed() + "%)";
  }


  chooseRegion() {
    this.clearSearchresult();
    this.clearMarkers();

    if (this.selectedRegion) {
      const jsonData = JSON.stringify(this.selectedRegion);
      localStorage.setItem(this.lsRegionKey, jsonData);

      this.regionTakesId = this.selectedRegion.id;
      this.selectedArea = undefined;
      console.info("RegionTakesId: " + this.selectedRegion.id);
      this.populateColorboxesForRegion();
      this.populateAreaSelect();
    } else {
      this.regionTakesId = 0;
      this.clearAreaSelect();
    }
  }

  private clearBoxes() {
    this.untakenBox = "0";
    this.greenbox = "0";
    this.yellowbox = "0";
    this.orangebox = "0";
    this.redbox = "0";
    this.purplebox = "0";
  }

  private clearAreaSelect() {
    this.areas = [];
    this.selectedArea = undefined;
  }

  chooseArea() {
    if (this.selectedArea) {
      const jsonData = JSON.stringify(this.selectedArea);
      localStorage.setItem(this.lsAreaKey, jsonData);

      this.populateColorboxesForArea();
    } else {
      this.populateColorboxesForRegion();
    }
  }


  showUploadModalDialog() {
    this.displayModal = true;
  }

  showHelpModalDialog() {
    this.displayHelpModal = true;
  }

  onFileSelected(event : any) {
    const file: File = event.target.files[0];

    if (file) {
      if (file.type === 'text/html') {
        const formData = new FormData();
        formData.append("file", file);
        this.displaySpinner = true;
        this.zoneService.uploadRegionData(formData).subscribe(
          (importResult: ImportResult) => {
            this.displaySpinner = false;
            const importText = importResult.importType + " Antal zoner: " + importResult.nrofImported;
            this.messageService.add({severity:'success', summary: 'Lyckad inläsning', detail: importText, life: 15000})
            if (this.selectedUser) {
              this.updateRegions(this.selectedUser.username);
            }
          },
          err => {
            let errorMessage = err.error.message;
            this.displaySpinner = false;
            this.messageService.add({severity:'error', summary: 'Dörnöö', detail: errorMessage, life: 15000});
          });
        // TODO ta hand om exceptions på ett bra sätt
      } else {
        console.info("Du valde fel filtyp");
      }
    }
    this.displayModal = false;
  }

  searchUsers(event: any) {
    this.zoneService.getUsers(event.query).subscribe((users : User[]) => {
      this.suggestedUsers = users;
    });
  }

  zoneInListSelected(zone: UniqueZone) {
    console.info("lat: " + zone.latitude + " long: " + zone.longitude);
    this.map.panTo(new L.LatLng(zone.latitude, zone.longitude));
  }

  testingPopulatingUser() {
    const jsonStringFromStorage = localStorage.getItem(this.lsUserKey);
    console.info('user jsonString from storage:' + jsonStringFromStorage);

    if (jsonStringFromStorage) {
      let user : User= JSON.parse(jsonStringFromStorage);
      console.info(user.id + '-' + user.username);
      this.selectedUser = user;
      this.userChanged(user);
    }

  }

  testingPopulatingRegion() {
    const jsonStringFromStorage = localStorage.getItem(this.lsRegionKey);
    console.info('region jsonString from storage:' + jsonStringFromStorage);

    if (jsonStringFromStorage) {
      let region : RegionTakes = JSON.parse(jsonStringFromStorage);
      console.info(region.id + '-' + region.regionName);
      this.selectedRegion = region;
      this.chooseRegion();
    }

  }

  testingPopulatingArea() {
    const jsonStringFromStorage = localStorage.getItem(this.lsAreaKey);
    console.info('area jsonString from storage:' + jsonStringFromStorage);

    if (jsonStringFromStorage) {
      let area : Area = JSON.parse(jsonStringFromStorage);
      console.info(area.id + '-' + area.areaName);
      this.selectedArea = area;
      this.chooseArea();
    }

  }

  populatingMinTakesFromLocalstorage() {
    const jsonStringFromStorage = localStorage.getItem(this.lsMinTakesKey);
    console.info('minTakes jsonString from storage:' + jsonStringFromStorage);

    if (jsonStringFromStorage) {
      let minTakes: number = JSON.parse(jsonStringFromStorage);
      this.minTakes = minTakes;
    }
  }

  populatingMaxTakesFromLocalstorage() {
    const jsonStringFromStorage = localStorage.getItem(this.lsMaxTakesKey);
    console.info('minTakes jsonString from storage:' + jsonStringFromStorage);

    if (jsonStringFromStorage) {
      let maxTakes: number = JSON.parse(jsonStringFromStorage);
      this.maxTakes = maxTakes;
    }
  }

  populatingSearchRoundOnlyFromLocalstorage() {
    const jsonStringFromStorage = localStorage.getItem(this.lsSearchRoundOnly);
    console.info('searchRoundOnly jsonString from storage:' + jsonStringFromStorage);

    if (jsonStringFromStorage) {
      let searchRoundOnly: boolean = JSON.parse(jsonStringFromStorage);
      this.searchRoundOnly = searchRoundOnly;
    }
  }


  testingErrorHandling() {
    this.zoneService.testingErrorHandling().subscribe(
      (greetings: Greeting[]) => {
        let nrofGreetings = "Number of greetings: " + greetings.length;
        this.messageService.add({severity:'success', summary: 'Hurra', detail: nrofGreetings, life: 5000});
      },
      err => {
        let errorMessage = err.error.message;
        this.messageService.add({severity:'error', summary: 'Dörnöö', detail: errorMessage, life: 15000});
      });
  }

}
