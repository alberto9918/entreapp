import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import { LanguageResponse } from '../interfaces/language-response';
import { LanguagesResponse } from '../interfaces/languages-response';


const languageUrl = `${environment.apiUrl}/languages`;
@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  constructor(private http: HttpClient) { }

  getUserLanguage(id: String, token: String): Observable<LanguageResponse> {
    return this.http.get<LanguageResponse>(`${languageUrl}/${id}?access_token=${token}`);
  }
  getSpanishLanguage(token: String):Observable<LanguageResponse> {
    return this.http.get<LanguageResponse>(`${languageUrl}/${"5c5429bf77bca32e879271d6"}?access_token=${token}`);
  }
  getAllLanguages(token: String): Observable<LanguagesResponse> {
    return this.http.get<LanguagesResponse>(`${languageUrl}?access_token=${token}`);

  }
}
