import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import { PoiCreateDto } from '../dto/poi-create-dto';
import { OnePoiResponse } from '../interfaces/one-poi-response';
import { PoiResponse } from '../interfaces/poi-response';
import { AuthenticationService } from './authentication.service';
import { UrlResponse } from '../interfaces/url-response';

const uploadOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'multipart/form-data'
  })
};

const url = `${environment.apiUrl}/pois`;
environment.masterKey
@Injectable({
  providedIn: 'root'
})
export class PoiService {

  token = `?access_token=${this.authService.getToken()}`;
  public selectedPoi: OnePoiResponse;

  constructor(private http: HttpClient, private authService: AuthenticationService) { }

  uploadImage(formData: FormData): Observable<UrlResponse> {
    return this.http.post<UrlResponse>(
      `${environment.apiUrl}/files/upload/image?${environment.masterKeyTemporal}`,
      formData
    );
  }

  removeImage(key: String): Observable<UrlResponse> {
    return this.http.delete<UrlResponse>(
      `${environment.apiUrl}/files/`+key+`?${environment.masterKeyTemporal}`
    );
  }
  getAll(): Observable<PoiResponse> {
    return this.http.get<PoiResponse>(`${url}${this.token}`);
  }

  getOne(id: string): Observable<OnePoiResponse> {
    return this.http.get<OnePoiResponse>(`${url}/${id}${this.token}`);
  }

  create(resource: PoiCreateDto): Observable<PoiResponse> {
    return this.http.post<PoiResponse>(`${url}${this.token}`, resource);
  }

  remove(id: string): Observable<PoiResponse[]> {
    return this.http.delete<PoiResponse[]>(`${url}/${id}${this.token}`);
  }

  edit(id: string, resource: PoiCreateDto): Observable<OnePoiResponse> {
    return this.http.put<OnePoiResponse>(`${url}/${id}${this.token}`, resource);
  }

}
