import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from './../../environments/environment';
import { AuthenticationService } from './authentication.service';
import { RatingResponse } from './../interfaces/rating-response';
import { RatingDto } from './../dto/rating.dto';
import { OneRatingResponse } from '../interfaces/one-rating-response';

const ratingUrl = `${environment.apiUrl}/ratings`

const requestOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class RatingService {

  token = `?access_token=${this.authService.getToken()}`;

  constructor(private http: HttpClient, private authService: AuthenticationService) { }

  getAll() {
    return this.http.get<RatingResponse>(`${ratingUrl}${this.token}`);
  }

  getOne(id: number) {
    return this.http.get<OneRatingResponse>(`${ratingUrl}/${id}${this.token}`);
  }

  create(resource: RatingDto): Observable<OneRatingResponse> {
    return this.http.post<OneRatingResponse>(`${ratingUrl}${this.token}`, resource);
  }

  remove(id: string): Observable<RatingResponse[]> {
    return this.http.delete<RatingResponse[]>(`${ratingUrl}/${id}${this.token}`);
  }

  edit(id: string, resource: RatingDto): Observable<OneRatingResponse> {
    return this.http.put<OneRatingResponse>(`${ratingUrl}/${id}${this.token}`, resource);
  }

}
