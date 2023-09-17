import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable, of } from "rxjs";
import { catchError, map, tap } from 'rxjs/operators';

import {UniqueZoneSearchresult} from "./unique-zone-searchresult";
import {RegionTakes} from "./region-takes";
import {Area} from "./area";
import {User} from "./user";
import {ImportResult} from "./import-result";
import {Greeting} from "./greeting";
import {BroadcastMessage} from "./broadcast-message";


@Injectable({
  providedIn: 'root'
})
export class ZoneService {

  private baseURL = '/api/';

  constructor(private http: HttpClient) { }

  getZones(regionTakesId: number, minTakes: number, maxTakes: number, searchRoundOnly: boolean, area?: Area): Observable<UniqueZoneSearchresult> {
    let areaPart = '';

    if (area) {
      areaPart = "&areaId=" + area.id;
    }

    let roundPart = '';

    if (searchRoundOnly) {
      roundPart = "&searchForRound=1";
    }

    const url = `${this.baseURL}searchzones` + "?regionTakesId=" +  regionTakesId + "&minTakes=" + minTakes + "&maxTakes=" + maxTakes + areaPart + roundPart;
    return this.http.get<UniqueZoneSearchresult>(url);
  }

  uploadRegionData(formData: FormData) : Observable<ImportResult> {
    const url = `${this.baseURL}upload`;
    return this.http.post<ImportResult>(url, formData);
  }

  getRegions(username: string) : Observable<RegionTakes[]> {
    const url = `${this.baseURL}regiontakes` + "?username=" +  username;
    return this.http.get<RegionTakes[]>(url);
  }

  getAreas(regionTakesId: number) : Observable<Area[]> {
    const url = `${this.baseURL}areas` + "?regionTakesId=" +  regionTakesId;
    return this.http.get<Area[]>(url);
  }

  getUsers(searchStr: string): Observable<User[]> {
    const url = `${this.baseURL}users` + "?searchString=" +  searchStr;
    return this.http.get<User[]>(url);
  }

  getBroadcastMessages(userId: number): Observable<BroadcastMessage[]> {
    const url = `${this.baseURL}broadcast` + "?userId=" +  userId;
    return this.http.get<BroadcastMessage[]>(url);
  }



  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(operation);
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      // this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  testingErrorHandling(): Observable<Greeting[]> {
    const url = `${this.baseURL}greeting`;
    return this.http.get<Greeting[]>(url);
  }

}
