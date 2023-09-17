import { Component, OnInit, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';
import {Area} from "../area";
import {UniqueZone} from "../unique-zone";

@Component({
  selector: 'app-upload-region-data',
  templateUrl: './upload-region-data.component.html',
  styleUrls: ['./upload-region-data.component.css']
})
export class UploadRegionDataComponent implements OnInit, AfterViewInit {

  minTakes: number = 0;

  maxTakes: number = 9999;

  areas: Area[] = [];

  selectedArea?: Area;

  zones: UniqueZone[] = [];

  map: any;


  constructor() { }

  ngOnInit(): void {
    this.zones = [
      {zoneName: "SophieHome", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 1885},
      {zoneName: "PineWoods", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 1037},
      {zoneName: "PineParty", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 1035},
      {zoneName: "Cykelbron", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 1013},
      {zoneName: "TheoryZone", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 997},
      {zoneName: "Profezone", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 961},
      {zoneName: "UmeVidÄlven", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 950},
      {zoneName: "CoalCreek", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 853},
      {zoneName: "Akademizonen", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 737},
      {zoneName: "Kökszon", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 734},
      {zoneName: "BroUndret", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 652},
      {zoneName: "Porfyrvägen", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 637},
      {zoneName: "BlueRoad", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 618},
      {zoneName: "NothingFancy", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 572},
      {zoneName: "Akademizonen", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 737},
      {zoneName: "Kökszon", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 734},
      {zoneName: "BroUndret", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 652},
      {zoneName: "Porfyrvägen", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 637},
      {zoneName: "BlueRoad", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 618},
      {zoneName: "NothingFancy", areaName: "Umeå kommun", longitude: 0.000000, latitude: 0.000000, takes: 572},
    ];
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

  }


  searchButtonIsDisabled(): boolean {
    return this.minTakes == null || this.maxTakes == null;
  }

  areasMissing(): boolean {
    return this.areas.length == 1;
  }

  getZones(): void {

  }

  chooseArea() {

  }

  zoneInListSelected(zone: UniqueZone) {

  }

}
